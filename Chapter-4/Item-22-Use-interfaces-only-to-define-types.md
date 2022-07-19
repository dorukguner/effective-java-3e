An interface that is implemented by a class can be used as a *type* to refer to instances
of the class, and tell us what a client can do with instances of said class. It is
inappropriate to define an interface for any other purpose.

*Constant interfaces* fail this test as they contain no methods, consisting of only static final fields.
```
// Constant interface antipattern - do not use!
public interface PhysicalConstants {
    // Avogadro's number (1/mol)
    static final double AVOGADROS_NUMBER = 6.022_140_857e23;
    
    // Boltzmann constant (J/K)
    static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
    
    // Mass of the electron (kg)
    static final double ELECTRON_MASS = 9.109_383_56e-31;
}
```
**The constant interface pattern is a poor use of interfaces** as they cause implementation
details (the constants a class uses internally) to leak into the class's exported API.

If you want to export constants, there are several reasonable choices:
* Add them to the class or interface if they are strongly tied to one that already exists, 
i.e. *Integer* exports *MIN_VALUE* and *MAX_VALUE* constants
* An *enum type* (Item 34)
* A non-instantiable *utility* class [(Item 4)](../Chapter-2/Item-4-Enforce-noninstantiability-with-a-private-constructor.md)