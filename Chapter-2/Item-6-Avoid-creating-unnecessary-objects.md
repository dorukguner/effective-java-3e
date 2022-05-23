It is often appropriate to reuse a single object instead of creating a new equivalent object each time it is needed.
Reuse can be both faster and easier to understand. An object can always be reused if it is immutable (Item 17)

```
String s = new String("bikini"); // DON'T DO THIS!
```

The above statement creates a new String instance each time it is executed, which is unnecessary as the argument to the
constructor is itself a *String* instance

The improved version is simply:
```
String s = "bikini";
```

You can often avoid creating unnecessary objects by using *static factory methods* ([Item 1](./Item-1-Consider-static-factory-methods-instead-of-constructors.md))
in preference to constructors. E.g. *Boolean.valueOf(String)* vs constructor *Boolean(String)*

Some object creations are much more expensive than others, so it is advised to cache such objects for reuse.

```
// Performance can be greatly improved!
static boolean isRomanNumeral(String s) {
    return s.matches("^(?=.)M*(C[MD]|D?C{0,3})"
            + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
}
```

String.matches internally creates a *Pattern* instance for the regular expression, which after use gets picked up by
the garbage collector.

Instead we can explicitly compile the Pattern instance into an immutable field during class initialisation, which will
be available for reuse and greatly improve performance and clarity.

```
// Reusing expensive object for improved performance
public class RomanNumerals {
    private static final Pattern ROMAN = Pattern.compile(
            "^(?=.)M*(C[MD]|D?C{0,3})"
            + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

    static boolean isRomanNumeral(String s) {
        return ROMAN.matcher(s).matches();
    }
}
```

In the case where the *RomanNumerals* class above is initialised but the method *isRomanNumeral* is never invoked, the
field *ROMAN* will have been initialised needlessly. It is possible to *lazily initialise* (Item 83) the field however
this can complicate the implementation with no measurable performance improvement (Item 67).

When an object is immutable, it is obvious it can be reused safely. However, an adapter is an object that delegates its
state to a backing object, meaning there's no need to create more than one instance of a given adapter to a given
object. For example the *keySet* method of the *Map* interface, all calls to this method are backed by the same *Map*
instance and return objects that are functionally identical.

*Autoboxing* is another way to create unnecessary objects. **Autoboxing blurs but does not erase the distinction between
primitive and boxed primitive types**. There are not-so-subtle performance differences (Item 61) when utilising
autoboxing.

```
// Hideously slow! Can you spot the object creation?
private static long sum() {
    Long sum = 0L;
    for (long i = 0; i <= Integer.MAX_VALUE; i++)
        sum += i;
    return sum;
}
```

The above snippet sums all positive *int* values. The variable sum is declared as a *Long* instead of a *long*, meaning
about 2<sup>31</sup> unnecessary *Long* instances are created. Changing the sum declaration to use *long* instead of *Long*
greatly reduces runtime. **Prefer primitives to boxed primitives and watch out for unintentional autoboxing**

Conversely, the user of an *object pool* is generally a bad idea unless the objects in the pool are extremely
heavyweight. The classic example of an object that *does* justify an object pool is a database connection as the cost
of establishing a connection is sufficiently high.

[Runnable classes that display the runtime of the above examples can be found here](../src/effectivejava/chapter2/item6)

TODO link to item 17, 83, 63, 61