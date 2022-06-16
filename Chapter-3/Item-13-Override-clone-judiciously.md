Though the specification doesn't say it, **in practice, a class implementing *Cloneable* is expected to provide a properly
function public *clone* method.**

The precise meaning of a "copy" of an object may depend on the class of the object. The general intent is that, for any
object x, the expression
```
x.clone() != x
```
will be true, and the expression
```
x.clone().getClass() == x.getClass()
```
will be true, but these are not absolute requirements. While it is typically the case that
```
x.clone().equals(x)
```
will be true, but this is not an absolute requirement.

Note that **immutable classes should never provide a *clone* method** because it would merely encourage wasteful copying.
With that caveat, here's how a *clone* method for the *PhoneNumber* class from [item 11](./Item-11-Always-override-hashCode-when-you-override-equals.md)
would look:
```
// Clone method for class with no references to mutable state
@Override public PhoneNumber clone() {
    try {
        return (PhoneNumber) super.clone();
    } catch (CloneNotSupportedException e) {
        throw new AssertionError(); // Can't happen
    }
}
```

We would also have to modify the class declaration for *PhoneNumber* to implement *Cloneable*. Since Java supports *covariant
return types* this clone method can return *PhoneNumber* instead of *Object*.

In the case that an object contains fields that refer to mutable objects, the simple *clone* implementation shown above
can be disastrous. Consider the *Stack* class in [Item 7](../src/effectivejava/chapter2/item7/Stack.java). Returning
*super.clone()* will result in the *elements* field referring to the same array as the original *Stack* instance. Modifying
the original will destroy the invariants in the clone and vice versa. Instead, we must copy the internals of the stack by
calling *clone* recursively on the elements array:
```
// Clone method for class with references to mutable state
@Override public Stack clone() {
    try {
        Stack result = (Stack) super.clone();
        result.elements = elements.clone();
        return result;
    } catch (CloneNotSupportedException e) {
        throw new AssertionError();
    }
}
```
Note that it may be necessary to remove *final* modifiers from some fields the make a class cloneable since **the *Cloneable*
architecture is incompatible with normal use of final fields referring to mutable objects**.

It is not always sufficient to call *clone* recursively. For example, when cloning a hash table whose internals consist
of an array of buckets, each which references the first entry in a linked list of key-value pairs:
```
public class HashTable implements Cloneable {
    private Entry[] buckets = ...;
    private static class Entry {
        final Object key;
        Object value;
        Entry next;
        
        Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
... // Remainder omitted
}
```
If we try to clone the bucket array recursively, as we did for the *Stack* above, the bucket arrays reference the same 
linked lists as the original:
```
// Broken clone method - results in shared mutable state!
@Override public HashTable clone() {
    try {
        HashTable result = (HashTable) super.clone();
        result.buckets = buckets.clone();
        return result;
    } catch (CloneNotSupportedException e) {
        throw new AssertionError();
    }
}
```

Instead, we should copy the linked list that comprises each bucket:
```
// Recursive clone method for class with complex mutable state
public class HashTable implements Cloneable {
    private Entry[] buckets = ...;
    
    private static class Entry {
        final Object key;
        Object value;
        Entry next;
        
        Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
        
        // Recursively copy the linked list headed by this Entry
        Entry deepCopy() {
            return new Entry(key, value,
                next == null ? null : next.deepCopy());
        }
    }
    
    @Override public HashTable clone() {
        try {
            HashTable result = (HashTable) super.clone();
            result.buckets = new Entry[buckets.length];
            for (int i = 0; i < buckets.length; i++) {
                if (buckets[i] != null) {
                    result.buckets[i] = buckets[i].deepCopy();
                }
            }
            
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    ... // Remainder omitted
}
```
The above implementation consumes one stack frame for each element in a given list, potentially causing a stack overflow.
Instead, we can replace the recursion in *deepCopy* with iteration:
```
// Iteratively copy the linked list headed by this Entry
Entry deepCopy() {
    Entry result = new Entry(key, value, next);
    for (Entry p = result; p.next != null; p = p.next) {
        p.next = new Entry(p.next.key, p.next.value, p.next.next);
    }
    
    return result;
}
```

A final approach to cloning complex mutable objects is to call _super.clone_, set all of the fields in the resulting object
to their initial state, then call higher-level methods to regenerate the state of the original object. For example,
initialising new bucket arrays and using the *put(key, value)* method to set each key-value mapping in the hash table.
This approach does not run as quickly as one that directly manipulates the innards of the clone.

Final notes:
* Like a constructor, a *clone* method must never invoke an overridable method on the clone under construction. An
overridden method will execute before the subclass has had a chance to fix its state in the clone
* Public *clone* methods should omit the *throw CloneNotSupportedException* clause
* When designing a class for inheritance, the class should *not* implement *Cloneable*
* A thread-safe class that implements *Cloneable* must have a properly synchronised *clone* method

In the case that you extend a class that already implements *Cloneable*, **A better approach to object copying is to 
provide a *copy constructor* or *copy factory*.**
```
// Copy constructor
public Yum(Yum yum) { ... };

// Copy factory
public static Yum newInstance(Yum yum) { ... };
```

This has the following advantages:
* No reliance on a risk-prone extralinguistic object creation mechanism
* No demand for unenforceable adherence to thinly documented conventions
* No conflict with the proper use of final fields
* No unnecessary checked exceptions are thrown
* No type casting is required
* Can take an argument whose type is an interface implemented by the class. E.g. when copying a *HashSet* to a *TreeSet*
we can use the conversion constructor *new TreeSet<>(HashSet s)*