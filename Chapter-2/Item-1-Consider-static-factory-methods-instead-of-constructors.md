A static method that returns an instance of the class. Can be provided in place of or in addition to public
constructors.

```
public static Boolean valueOf(boolean b) {
    return b ? Boolean.TRUE : Boolean.FALSE;
}
```

### Advantages
* Static factory methods have names, making them easier to use and read.E.g. BigInteger(int, int, Random) constructor
vs BigInteger.probablePrime
* Not required to create a new object each time they're invoked, as shown in the snippet above
* Can return an object of any subtype of their return type. The Collections Framework API is small and easier to 
understand as it doesn't need to export many public classes
* The class of the returned object can very depending on the input parameters
* The class of the returned object doesn't need to exist when the factory method is written

### Disadvantages
* If no constructors are provided in a class with static factory methods, the class cannot be subclassed
* They are hard for programmers to find as they do not stand out like constructors do. Common names for static
factory methods include from, of, valueOf, instance, getInstance, create, newInstance, get*Type*, new*Type* and *type*