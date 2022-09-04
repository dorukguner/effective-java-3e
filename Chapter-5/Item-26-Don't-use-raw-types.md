A class or interface whose declaration has one or more *type parameters* is a *generic*
class or interface. Each generic type defines a *raw type*, which is the name of the
generic type used without any accompanying type parameters. E.g. the raw type corresponding to
*List\<E\>* is *List*. Raw types behave as if all of the generic type information were erased
from the type declaration and exist primarily for compatibility with pre-generics code.

Before generics, we would have seen the following:
```
// Raw collection type - don't do this!
// My stamp collection. Contains only Stamp instances.
private final Collection stamps = ... ;
```
If we do this today and accidentally put a coin into our stamp collection, we would see
a compilation warning, however the erroneous insertion compiles and runs without error:
```
// Erroneous insertion of coin into stamp collection
stamps.add(new Coin( ... )); // Emits "unchecked call" warning
```
We don't get an error until we try to retrieve the coin from the stamp collection:
```
// Raw iterator type - don't do this!
for (Iterator i = stamps.iterator(); i.hasNext(); )
    Stamp stamp = (Stamp) i.next(); // Throws ClassCastException
      stamp.cancel();
```
We want to be discovering errors as soon as possible, ideally at compile time.

With generics, the type declaration allows us to find errors like the one above during
compilation:
```
// Parameterized collection type - typesafe
private final Collection<Stamp> stamps = ... ;
```
The compiler knows that *stamps* should only contain *Stamp* instances and *guarantees*
it to be true:
```
Test.java:9: error: incompatible types: Coin cannot be converted
to Stamp
    c.add(new Coin());
```

While we shouldn't use raw types such as *List*, it is fine to use types that are
parameterised to allow insertion of arbitrary objects, such as *List\<Object\>*.
The difference being that the former means we have opted out of the generic type system,
while the latter has explicitly told the compiler that it is capable of holding
objects of any type. 

We also have the wildcard type *List\<?\>* which represents a list that can contain only
objects of some unknown type, and is useful when we don't know or care what the actual
type parameter is, but we still want the type safety that generics bring.

There are a couple exceptions to the rule:
* You must use raw types in class literals, i.e. *List.class, String[].class, int.class*
are all legal but *List\<String\>.class and List\<?\>.class* are not.
* When using the *instanceof* operator:
```
// Legitimate use of raw type - instanceof operator
if (o instanceof Set) { // Raw type
    Set<?> s = (Set<?>) o; // Wildcard type
    ...
}
```