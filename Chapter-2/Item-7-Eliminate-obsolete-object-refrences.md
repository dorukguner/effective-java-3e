Working with Java, as it is a garbage-collected language, may lead to the impression that you don't have to think about
memory management, however this isn't quite true. In extreme cases memory leaks can cause disk paging and even program
failure with an *OutOfMemoryError*.


```
// Can you spot the "memory leak"?
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }

        return elements[--size];
    }

    /**
    * Ensure space for at least one more element, roughly
    * doubling the capacity each time the array needs to grow.
    */
    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

Looking at the above snippet, there is a memory leak due to *obsolete references* (A reference that will never be
dereferenced again). If a stack grows and then shrinks, the objects that were popped off the stack will not be garbage
collected. Not only are these object references excluded from garbage collection, but so too are any objects referenced
by that object, and so on. 

The above problem can be simply fixed by nulling out references once they become obsolete, which in this case is when
the object is popped off the stack. The corrected version of the pop method looks like this:

<pre>
public Object pop() {
    if (size == 0) {
        throw new EmptyStackException();
    }

   Object result = elements[--size];
   <b>elements[size] = null; // Eliminate obsolete reference</b>
   return result;
}
</pre>

An additional benefit of nulling out obsolete references is that a *NullPointerException* is thrown if they are
dereferenced by mistake, rather than quietly doing the wrong thing.

However, we should not overcompensate by nulling out every object reference as soon as the program is done using it.
**Nulling out object references should be the exception rather than the norm.** The best way to eliminate an obsolete
reference is to let the variable that contained the reference fall out of scope. This occurs naturally if you define
each variable in the narrowest possible scope (Item 57).

Nulling out references is only necessary when a class *manages its own memory*, like in the Stack class above. This is
because the garbage collector has no way of knowing which elements are in the active portion of the array (those
elements whose index is less than the size). Generally speaking, **whenever a class manages its own memory, the
programmer should be alert for memory leaks**. Whenever an element is freed, any object references contained in the
element should be nulled out.

Caches are another common source of memory leaks. Objects can often be left in the cache long after they become
irrelevant. Memory leaks in caches can be solved by:
* Representing the cache using a *WeakHashMap*; entries will be removed automatically after there are no more references
to its key outside of the cache
* A background thread can be used to clean up old entries. The *LinkedHashMap* class facilitates this approach with its
*removeEldestEntry* method

Listeners and other callbacks can also be a common source of memory leaks. Callbacks registered by clients should be
deregistered explicitly. One way to ensure they are garbage collected promptly is to store on *weak references* to them,
for instance, by storing them only as keys in a *WeakHashMap*.

Memory leaks aren't always obvious and are typically discovered only as a result of careful code inspection or with the
aid of a ***heap profiler***. Therefore, it is beneficial to learn to anticipate problems like this before they occur.

[A runnable class showcasing the Stack class' memory leak can be found here](../src/effectivejava/chapter2/item7/Stack.java)

TODO link to item 57