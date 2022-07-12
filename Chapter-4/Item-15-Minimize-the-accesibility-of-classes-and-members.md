A well-designed component hides all its implementation details, cleanly separating its API from its implementation. Components then communicate only through their APIs and are oblivious to each other's inner workings. This concept, known as *information hiding* or *encapsulation*, is a fundamental tenet of software design.

Information hiding *decouples* components that comprise a system, allowing them to be developed, tested, optimised, used, understood and modified in isolation.

The *access control* mechanism specifies the *accessibility* of classes, interfaces, and members. The accessibility of an entity is determined by the location of its declaration and by which, if any, of the access modifiers (*private, protected, public*) is present. 

The rule of thumb is simple: **make each class or member as inaccessible as possible**. 

For top-level (non-nested) classes and interfaces, the two possible access levels are *package-private* and *public*. If a top-level class is package-private, it is made part of the implementation rather than the exported API, and you can modify it, replace it, or eliminate it in a subsequent release without fear of harming existing clients. If you make it public you are obligated to support it forever to maintain compatibility.

If a package-private top-level class or interface is used by only one class, consider making the top-level class a private static nested class of the sole class that uses it (Item 24)

For members (fields, methods, nested classes, and nested interfaces), there are four possible access levels, listed in order of increasing accessibility:
* **private** - Accessible only from the top-level class where it is declared
* **package-private** - Accessible from any class in the package where it is declared. This is the *default* access level if no modifier is provided (except for interface members, which are public by default)
* **protected** - Accessible from subclasses of the class where it is declared and from any class in the package where it is declared
* **public** - Accessible from anywhere

As of Java 9, two additional implicit access levels are introduced as part of the *module system*. A module is a grouping of packages, like a package is a grouping of classes. A module may explicitly export some of its packages via *export declarations* in its *module declaration*. Public and protected members of unexported packages in a module are inaccessible outside the module.

TODO link to item 24