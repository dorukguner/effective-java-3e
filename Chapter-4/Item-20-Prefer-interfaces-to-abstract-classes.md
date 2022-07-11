Since the introduction of *default methods* for interfaces in Java 8, both abstract
classes and interfaces allow you to provide implementations for some instance methods. 
However, because Java permits only single inheritance, abstract classes are severely
constrained when used as type definitions. 

**Existing classes can easily be retrofitted to implement a new interface**, whereas an
existing class cannot, in general, be retrofitted to extend a new abstract class.

**Interfaces are ideal for defining mixins.** Abstract classes can't be used to define
mixins for the same reason they can't be retrofitted onto existing classes: a class
cannot have more than one parent, and there is no reasonable place in the class
hierarchy to insert a mixin.

**Interfaces allow for the construction of non-hierarchical type frameworks.** Suppose
we have an interface representing a singer and another representing a songwriter:
```
public interface Singer {
    AudioClip sing(Song s);
}

public interface Songwriter {
    Song compose(int chartPosition);
}
```
In reality, some singers are also songwriters. Because we used interfaces rather than
abstract classes here, it is perfectly permissible for a single class to implement
both *Singer* and *Songwriter*. In fact, we can define a third interface that extends
both *Singer* and *Songwriter* and adds new methods that are appropriate to the
combination:
```
public interface SingerSongwriter extends Singer, Songwriter {
    AudioClip strum();
    void actSensitive();
}
```

**Interfaces enable safe, powerful functionality enhancements** via the *wrapper class*
idiom [Item 18](./Item-18-Favor-composition-over-inheritance.md).

When there is an obvious implementation of an interface method, consider providing a
default method:
```
default void defaultMethod() {
    // default method implementation
}
```

It is possible to combine the advantages of interfaces and abstract classes by providing
an abstract *skeletal implementation class* to go with an interface. The interface
defines the type, perhaps providing some default methods, while the skeletal implementation
class implements the remaining non-primitive interface methods atop the primitive interface
methods. This is the *Template Method* pattern.

By convention, skeletal implementation classes are called *Abstract**Interface***, where
***Interface*** is the name of the interface they implement. For example, we have
*AbstractCollection, AbstractSet, AbstractList* and *AbstractMap* in the Collections
Framework. Skeletal implementations can make it easy for programmers to provide their own
implementations of an interface. Here's a static factory method containing a *List*
implementation atop *AbstractList*:
```
// Concrete implementation built atop skeletal implementation
static List<Integer> intArrayAsList(int[] a) {
    Objects.requireNonNull(a);

    return new AbstractList<>() {
        @Override public Integer get(int i) {
            return a[i]; // Autoboxing (Item 6)
        }
        
        @Override public Integer set(int i, Integer val) {
            int oldVal = a[i];
            a[i] = val; // Auto-unboxing
            return oldVal; // Autoboxing
        }
        
        @Override public int size() {
            return a.length;
        }
    };
}
```

Extending skeletal implementation classes is strictly optional and in the case that
a class cannot be made to extend the skeletal implementation, the class can implement
the interface directly. Furthermore, the skeletal implementation can still aid the 
implementor's task. The class implementing the interface can forward invocations of
interface methods to a contained instance of a private inner class that extends the
skeletal implementation. This is known as *simulated multiple inheritance* and is
closely related to the wrapper class idiom ([Item 18](./Item-18-Favor-composition-over-inheritance.md)).
It provides many of the benefits of multiple inheritance, while avoiding the pitfalls.
```
// Skeletal implementation class
public abstract class AbstractMapEntry<K,V> implements Map.Entry<K,V> {
    // Entries in a modifiable map must override this method
    @Override public V setValue(V value) {
        throw new UnsupportedOperationException();
    }
    
    // Implements the general contract of Map.Entry.equals
    @Override public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Map.Entry))
            return false;
        Map.Entry<?,?> e = (Map.Entry) o;
        return Objects.equals(e.getKey(), getKey())
            && Objects.equals(e.getValue(), getValue());
    }
    
    // Implements the general contract of Map.Entry.hashCode
    @Override public int hashCode() {
        return Objects.hashCode(getKey())
            ^ Objects.hashCode(getValue());
    }
    
    @Override public String toString() {
        return getKey() + "=" + getValue();
    }
}
```

Note that this skeletal implementation could not be implemented in the *Map.Entry*
interface or as a subinterface because default methods are not permitted to override
*Object* methods such as *equals, hashCode* and *toString*. **In the case that all
primitives and default methods cover the interface there is no need for a skeletal
implementation**.

A minor variant on the skeletal implementation is the *simple implementation*,
exemplified by *AbstractMap.SimpleEntry*. Similar to a skeletal implementation, it
implements an interface and is designed for inheritance, but is not abstract: it is the
simplest possible working implementation.

[A skeletal implementation in use can be found here](../src/effectivejava/chapter4/item20)