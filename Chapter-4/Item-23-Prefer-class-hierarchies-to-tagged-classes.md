Consider this class, which is capable of representing a circle or a rectangle:
```
// Tagged class - vastly inferior to a class hierarchy!
class Figure {
    enum Shape { RECTANGLE, CIRCLE };
    
    // Tag field - the shape of this figure
    final Shape shape;
    
    // These fields are used only if shape is RECTANGLE
    double length;
    double width;
    
    // This field is used only if shape is CIRCLE
    double radius;
    
    // Constructor for circle
    Figure(double radius) {
        shape = Shape.CIRCLE;
        this.radius = radius;
    }
    
    // Constructor for rectangle
    Figure(double length, double width) {
        shape = Shape.RECTANGLE;
        this.length = length;
        this.width = width;
    }
    
    double area() {
        switch(shape) {
            case RECTANGLE:
                return length * width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
        }
    }
}
```
Such *tagged classes* have numerous shortcomings:
* They are cluttered with boilerplate
* Readability is bad as multiple implementations are jumbled together
* Increased memory footprint due to unused fields
* Fields can't be made final unless constructors initialise irrelevant fields
* Constructors must set the tag field and initialise the right data fields with no help
from the compiler
* Adding a new shape cumbersome due to the need to modify every switch statement
* The data type of an instance gives no clue as to its flavour. 

**Tagged classes are verbose, error-prone and inefficient.**

**A tagged class is just a pallid imitation of a class hierarchy.** Here is the class
hierarchy corresponding to the original *Figure* class:
```
// Class hierarchy replacement for a tagged class
abstract class Figure {
    abstract double area();
}

class Circle extends Figure {
    final double radius;
    Circle(double radius) { this.radius = radius; }
    @Override double area() { return Math.PI * (radius * radius); }
}

class Rectangle extends Figure {
    final double length;
    final double width;
    
    Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }
    
    @Override double area() { return length * width; }
}
```

This class hierarchy corrects every shortcoming of tagged classes noted previously. 
Additionally, class hierarchies can be made to reflect natural hierarchical relationships
among types, allowing for increased flexibility and better compile-time type checking.
Adding a *Square* to the hierarchy can be made to reflect the fact that a square is a special
kind of *Rectangle* and is as simple as:
```
class Square extends Rectangle {
    Square(double side) {
        super(side, side);
    }
}
```

Note that the fields in the above hierarchy are accessed directly rather than by accessor
methods. This was done for brevity and would be a poor design if the hierarchy were public
[(Item 16)](./Item-16-In-public-classes-use-accessor-methods-not-public-fields.md)