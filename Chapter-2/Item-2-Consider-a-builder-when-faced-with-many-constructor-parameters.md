Static factory methods and constructors share a limitation in which they do not scale well to large numbers of
optional parameters. 

### Telescoping Constructor Pattern
A pattern where multiple constructors are provided. One takes in only required parameters and others take in
additional optional parameters.

[NutritionFacts.java - Telescoping](../src/effectivejava/chapter2/item2/telescopingconstructor/NutritionFacts.java)

```
NutritionFacts cocaCola =
new NutritionFacts(240, 8, 100, 0, 35, 27);
```

* Hard for client to read and write, easy to confuse ordering of constructor parameters as in the snippet above
* Multiple optional fields lead to many constructors with different amounts of parameters

### JavaBeans Pattern
A parameterless constructor is called to create the object, then setter methods are called to set each parameter.

[NutritionFacts.java - JavaBeans](../src/effectivejava/chapter2/item2/javabeans/NutritionFacts.java)
```
NutritionFacts cocaCola = new NutritionFacts();
cocaCola.setServingSize(240);
cocaCola.setServings(8);
cocaCola.setCalories(100);
cocaCola.setSodium(35);
cocaCola.setCarbohydrate(27);
```

* The object will be in an inconsistent state partway through its construction
* Prevents the class from being immutable
* Requires additional effort to ensure thread safety

### Builder Pattern
A constructor or static factory with the mandatory parameters is called which returns a builder object. The client can
then call setter-like methods on the builder object to set optional parameters. A build method is then called on the 
builder object to generate the object.

[NutritionFacts.java - Builder](../src/effectivejava/chapter2/item2/builder/NutritionFacts.java)

```
NutritionFacts cocaCola = new NutritionFacts.Builder(240, 8)
   .calories(100).sodium(35).carbohydrate(27).build();
```

* The class is immutable and thread safe as object is built after setting fields
* Required and optional parameters are clear, builders are easily re-usable
* Able to easily detect invalid parameters
* Well suited to class hierarchies as demonstrated in the [hierarchical builder package](../src/effectivejava/chapter2/item2/hierarchicalbuilder/)
* Extra cost of creating the builder first may be noticeable in performance-critical situations

**Builder pattern is a good choice when class constructors or static factories have more than a handful of parameters**