A class documented for inheritance **must document its *self-use* of overridable methods.**
For each public or protected method, the documentation must indicate which overridable methods
the method invokes, in what sequence, and how the results of each invocation affect subsequent
processing. 
An example from *java.util.AbstractCollection*:
```
public boolean remove(Object o)
Removes a single instance of the specified element from this collection, if it
is present (optional operation). More formally, removes an element e such
that Objects.equals(o, e), if this collection contains one or more such
elements. Returns true if this collection contained the specified element (or
equivalently, if this collection changed as a result of the call).

Implementation Requirements: This implementation iterates over the collection looking for the specified element. If it finds the element, it removes
the element from the collection using the iterator’s remove method. Note that
this implementation throws an UnsupportedOperationException if the
iterator returned by this collection’s iterator method does not implement
the remove method and this collection contains the specified object
```
Designing for inheritance also involves potentially providing hooks into its internal workings
in the form of judiciously chosen protected methods or, in rare instances, protected fields.
Consider the *removeRange* method from *java.util.AbstractList*:
```
protected void removeRange(int fromIndex, int toIndex)

Removes from this list all of the elements whose index is between
fromIndex, inclusive, and toIndex, exclusive. Shifts any succeeding
elements to the left (reduces their index). This call shortens the list by
(toIndex - fromIndex) elements. (If toIndex == fromIndex, this operation
has no effect.)

This method is called by the clear operation on this list and its sublists.
Overriding this method to take advantage of the internals of the list implementation can substantially improve the performance of the clear operation
on this list and its sublists.

Implementation Requirements: This implementation gets a list iterator
positioned before fromIndex and repeatedly calls ListIterator.next
followed by ListIterator.remove, until the entire range has been
removed. Note: If ListIterator.remove requires linear time, this
implementation requires quadratic time.

Parameters:
fromIndex index of first element to be removed.
toIndex index after last element to be removed
```

This method is provided solely to make it easy for subclasses to provide a fast *clear*
method on sublists, which is of no interest to end users of a *List* implementation.

Which protected members to expose isn't always clear. The best you can do is usually take a
best guess, then test it by writing subclasses, while exposing as few protected members as
possible.

By writing a subclass any crucial omissions will be painfully obvious. Conversely, if 
several subclasses don't use a protected field, it should probably be private. Any self-use
patterns and protected methods and fields are committed to *forever* and make it extremely
difficult or impossible to improve performance or functionality of the class in any subsequent
release. 

**Constructors must not invoke overridable methods**, directly or indirectly as the superclass
constructor runs before the subclass constructor. Here's a class that violates this rule:
```
public class Super {
    // Broken - constructor invokes an overridable method
    public Super() {
        overrideMe();
    }
    
    public void overrideMe() {
    }
}
```
And here's a subclass that overrides the *overrideMe* method:
```
public final class Sub extends Super {
    // Blank final, set by constructor
    private final Instant instant;
    
    Sub() {
        instant = Instant.now();
    }
    
    // Overriding method invoked by superclass constructor
    @Override public void overrideMe() {
        System.out.println(instant);
    }
    
    public static void main(String[] args) {
        Sub sub = new Sub();
        sub.overrideMe();
    }
}
```

This program prints out *null* during the superclass' constructor as the Sub constructor
hasn't had a chance to initialise the *instant* field. If Sub invoked any method on *instant*
it would have thrown a *NullPointerException*.

Note that implementing either *Cloneable* or *Serializable* should be done with caution as
the *clone* and *readObject* methods behave a lot like constructors, and so these methods
also may not invoke overridable methods.

In the case of ordinary concrete classes there is a chance that each time a change is made,
a subclass extending the class will break. **The best solution to this problem is to
prohibit subclassing in classes that are not designed and documented to be safely subclassed**.
Which can be done by either:
* Declaring the class final
* Making all constructors private and add public static factories in place of constructors.
This approach provides allows for internal subclasses

[Runnable classes of the above Super and Sub snippets can be found here](../src/effectivejava/chapter4/item19)