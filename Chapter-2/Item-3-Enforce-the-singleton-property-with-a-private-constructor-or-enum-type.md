A *singleton* is a class that is instantiated exactly once, typically representing stateless objects or system
components. **Singletons can be difficult to test with**.

### Singleton With Public Field
```
// Singleton with public final field
public class Elvis {
    public static final Elvis INSTANCE = new Elvis();
    private Elvis() { ... }
    public void leaveTheBuilding() { ... }
}
```
* Clear the class is a singleton due to the public static final instance

### Singleton with Static Factory
```
// Singleton with static factory
public class Elvis {
    private static final Elvis INSTANCE = new Elvis();
    private Elvis() { ... }
    public static Elvis getInstance() { return INSTANCE; }
    public void leaveTheBuilding() { ... }
}
```

* Gives you flexibility. E.g. could be made to return a new instance for each thread that invokes it
* Allows for a *generic singleton factory* (item 30) 
* A *method reference* can be used as a supplier

#### To make the above singletons serializable
* Add implements Serializable to its declaration
* Declare all instance fields transient
* Provide a readResolve method (item 89) as seen below
```
// readResolve method to preserve singleton property
private Object readResolve() {
    // Return the one true Elvis and let the garbage collector
    // take care of the Elvis impersonator.
    return INSTANCE;
}
```

### Single-element enum - The Preferred Approach
```
// Enum singleton - the preferred approach
public enum Elvis {
    INSTANCE;
    public void leaveTheBuilding() { ... }
}
```
* Concise
* Provides serialization for free
* Guaranteed against multiple instantiation and reflection attacks
* **Note this can't extend a superclass**

[View complete classes of each approach here](../src/effectivejava/chapter2/item3)

TODO link to item 30 and 89 below