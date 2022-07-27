A nested class should exist only to serve its enclosing class. If a nested class would
be useful in some other context, then it should be a top-level class. There are 4 kinds
of nested classes: 
* Static member class:
  * Simplest kind of nested class
  * Has access to all the enclosing class's members
  * Commonly used as a public helper class
  * An example would be the operations supported by a calculator, the *Operation* enum.
  Clients of *Calculator* can refer to operations like *Calculator.Operation.PLUS*
* Non-static member class:
  * Each instance is implicitly associated with an *enclosing instance* of its containing
  class
  * Able to invoke methods on or obtain reference to the enclosing instance using *this*
  * A common use is to define an *Adapter* that allows an instance of the outer class to
  be viewed as an instance of some unrelated class:
```
// Typical use of a nonstatic member class
public class MySet<E> extends AbstractSet<E> {
    ... // Bulk of the class omitted
    
    @Override public Iterator<E> iterator() {
        return new MyIterator();
    }
    
    private class MyIterator implements Iterator<E> {
        ...
    }
}
```

**If a member class does not require access to an enclosing instance, *always* put the *static*
modifier in its declaration.**
* Anonymous class:
  * Have no name, not a member of its enclosing class
  * Simultaneously declared and instantiated at the point of use
  * Cannot have any static members other than *constant variables*, which are final primitive
  or string fields
  * Were previously used to create small *function objects* however lambdas are now preferred (Item 42)
  * Commonly used in the implementation of static factory methods (see *intArrayAsList* in [Item 20](./Item-20-Prefer-interfaces-to-abstract-classes.md))
* Local class:
  * Least frequently used
  * Can be declared practically anywhere a local variable can be declared and obeys the same
  scoping rules
  * Similar to member classes they have names and can be used repeatedly
  * Similar to anonymous classes they have enclosing instances only if they are defined in
  a nonstatic context and cannot contain static members

TODO link to Item 42