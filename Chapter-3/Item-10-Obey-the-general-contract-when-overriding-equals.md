It is best not to override the equals method on an object when:
* Each instance of the class is inherently unique. E.g. *Threads*
* There is no need for the class to provide a *logical equality* test. E.g. *java.util.regex.Pattern,
the designers didn't think clients would need or want to check whether two *Pattern* instances were equal
* A superclass has already overridden equals, and the superclass behaviour is appropriate for this class.
* The class is private or package-private, and its *equals* method will never be invoked. It is also possible
to override the *equals* method as follows in this scenario
```
@Override public boolean equals(Object o) {
    throw new AssertionError(); // Method is never called
}
```

**It is appropriate** to override equals when a class has a notion of *logical equality* that differs from mere object
identity and a superclass has not already overridden *equals*. E.g. *Integer* or *String*.

A class that uses instance control ([Item 1](../Chapter-2/Item-1-Consider-static-factory-methods-instead-of-constructors.md))
to ensure that at most a single instance of a class exists does *not* require the *equals* method to be overridden. *Enum
types* (Item 34) fall into this category too.

When overriding the *equals* method, you must adhere to its general contract. From the specification for *Object*:
* *Reflexive*: For any non-null reference value x, x.equals(x) must return true
* *Symmetric*: For any non-null reference values x and y, x.equals(y) must return true if and only if y.equals(x)
returns true
* *Transitive*: For any non-null reference values x,y,z, if x.equals(y) returns true and y.equals(z) returns true,
then x.equals(z) must return true
* *Consistent*: For any non-null reference values x and y, multiple invocations of x.equals(y) must consistently
return true or consistently return false, provided no information used in equals comparisons is modified
* *Non-nullity**: For any non-null reference value x, x.equals(null) must return false

The recipe for a high-quality *equals* method:
1. Use the == operator to check if the argument is a reference to this object
2. Use the *instanceof* operator to check if the argument has the correct type
3. Cast the argument to the correct type
4. For each "significant" field in the class, check if that field of the argument matches the corresponding field of this
object

Final caveats:
* Always override *hashCode* when you override *equals* (Item 11)
* Don't try to be too clever, simply testing fields for equality is usually enough
* Don't substitute another type for *Object* in the *equals* declaration

[Runnable classes showcasing classes that both violate and follow the above rules can be found here](../src/effectivejava/chapter3/item10)

[PhoneNumber.java showcases a typical equals method that follows the recipe above](../src/effectivejava/chapter3/item10/PhoneNumber.java)

TODO link to item 34, 18, 11