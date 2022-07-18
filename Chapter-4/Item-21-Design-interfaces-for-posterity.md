Since Java 8, interfaces allow for a *default method* construct, allowing the addition
of methods to existing interfaces. But adding new methods to existing interfaces is fraught
with risk.

The declaration for a default method includes a *default implementation* that is used by
all classes that implement the interface but do not implement the default method. 
However, **it is not always possible to write a default method that maintains all invariants
of every conceivable implementation.**

Consider the *removeIf* method, added to the *Collection* interface in Java 8. This method
removes all elements for which a given *predicate* returns *true*. The declaration would
look something like this:
```
// Default method added to the Collection interface in Java 8
default boolean removeIf(Predicate<? super E> filter) {
    Objects.requireNonNull(filter);
    boolean result = false;
    for (Iterator<E> it = iterator(); it.hasNext(); ) {
        if (filter.test(it.next())) {
            it.remove();
            result = true;
        }
    }
    return result;
}
```
This is the best general-purpose implementation one could possibly write for the *removeIf*
method, however it fails on some real-world *Collection* implementations. Consider the
*org.apache.commons.collections4.collection.SynchronizedCollection*, which is a wrapper class
[(Item 18)](./Item-18-Favor-composition-over-inheritance.md), all of whose methods
synchronize on a locking object before delegating to the wrapped collection. This class
does not override the *removeIf* method, and the default method *cannot* maintain the
class's fundamental promise of automatically synchronizing around each method
invocation. 

**In the presence of default methods, existing implementations of an interface may
compile without error or warning but fail at runtime.** Using default methods to add new
methods to existing interfaces should be avoided unless the need is critical, in which case
considerable thought should be put into whether an existing interface implementation might be broken
by your default method implementation.