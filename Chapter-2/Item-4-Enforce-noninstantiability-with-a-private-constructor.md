In some cases we want a *utility* class that is not to be instantiated. We cannot enforce non-instantiability by making
the class abstract as the class can be subclassed and the subclass instantiated. If we don't provide a constructor, the
compiler provides a public, parameterless *default constructor*.

A class can be guaranteed non-instantiable by including a private constructor
```
// Noninstantiable utility class
public class UtilityClass {
    // Suppress default constructor for noninstantiability
    private UtilityClass() {
        throw new AssertionError();
    }
    ... // Remainder omitted
}
```

* The AssertionError isn't strictly required, stops constructor from accidentally being invoked from within class
* Prevents the class from being subclassed as all constructors must invoke a superclass constructor