An immutable class is a class whose instances cannot be modified. To make a class immutable,
follow these five rules:
1. Don't provide methods that modify the object's state
2. Ensure that the class can't be extended
3. Make all fields final
4. Make all fields private
5. Ensure exclusive access to any mutable components

An immutable complex number class that follows the above rules:
```
// Immutable complex number class
public final class Complex {
    private final double re;
    private final double im;
    
    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }
    
    public double realPart() { return re; }
    public double imaginaryPart() { return im; }
    
    public Complex plus(Complex c) {
        return new Complex(re + c.re, im + c.im);
    }
    
    public Complex minus(Complex c) {
        return new Complex(re - c.re, im - c.im);
    }
    
    public Complex times(Complex c) {
        return new Complex(re * c.re - im * c.im,
                            re * c.im + im * c.re);
    }
    
    public Complex dividedBy(Complex c) {
        double tmp = c.re * c.re + c.im * c.im;
        return new Complex((re * c.re + im * c.im) / tmp,
                            (im * c.re - re * c.im) / tmp);
    }
    
    @Override public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        
        if (!(o instanceof Complex)) {
            return false;
        }
        
        Complex c = (Complex) o;
        // See page 47 to find out why we use compare instead of ==
        return Double.compare(c.re, re) == 0
            && Double.compare(c.im, im) == 0;
    }
    
    @Override public int hashCode() {
        return 31 * Double.hashCode(re) + Double.hashCode(im);
    }
    
    @Override public String toString() {
        return "(" + re + " + " + im + "i)";
    }
}
```

Immutable classes should encourage clients to reuse existing instances wherever possible.
An easy way to do this is to provide public static final constants for commonly used values:
```
public static final Complex ZERO = new Complex(0, 0);
public static final Complex ONE = new Complex(1, 0);
public static final Complex I = new Complex(0, 1);
```

An immutable class can also provide static factories ([Item 1](../Chapter-2/Item-1-Consider-static-factory-methods-instead-of-constructors.md))
to achieve this, such as in BigInteger.

Any copies of immutable objects would be forever equivalent to the originals, therefore, a
*clone* methods or *copy constructor* ([Item 13](../Chapter-3/Item-13-Override-clone-judiciously.md))
should not be provided. 

Further benefits of immutable classes:
* Can share their internals
* Make great building blocks for other objects
* Provide failure atomicity for free

**The major disadvantage of immutable classes is that they require a separate object for each distinct
value**, which can be costly to create. This performance problem is magnified if you perform
a chain of operations that generate a new object at every step. *BigInteger* has a 
package-private mutable "companion class" that it uses to speed up multistep operations 
such as modular exponentiation. An example of a public mutable companion class is 
*StringBuilder*.

Another approach to making a class that does not permit itself to be subclassed is to make
all of its constructors private or package-private and add public static factories in place of
the public constructors ([Item 1](../Chapter-2/Item-1-Consider-static-factory-methods-instead-of-constructors.md)).
Here's how *Complex* would look if you took this approach:
```
// Immutable class with static factories instead of constructors
public class Complex {
    private final double re;
    private final double im;
    
    private Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }
    
    public static Complex valueOf(double re, double im) {
        return new Complex(re, im);
    }
    
    ... // Remainder unchanged
}
```

Classes should be immutable unless there's a very good reason to make them mutable. If a
class can't be made immutable, limit its mutability as much as possible. Constructors should
create fully initialised objects with all of their invariants established. 

[A runnable Complex class can be found here](../src/effectivejava/chapter4/item17/Complex.java)