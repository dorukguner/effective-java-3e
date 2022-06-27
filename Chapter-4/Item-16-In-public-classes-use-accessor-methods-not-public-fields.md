```
// Degenerate classes like this should not be public!
class Point {
    public double x;
    public double y;
}
```

The above class does not offer the benefits of *encapsulation* (Item 15) since the fields are
accessed directly.
* Changing the representation will change the API
* You can't enforce invariants
* You can't take auxiliary action when a field is accessed.

Instead, we should use private fields and public *accessor methods* (getters) and, for mutable
classes, *mutators* (setters):
```
// Encapsulation of data by accessor methods and mutators
class Point {
    private double x;
    private double y;
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}
```

**If a class is accessible outside its package, provide accessor methods** to preserve
flexibility to change the class's internal representation. 

However, **if a class is package-private or is a private nested class, there is nothing
inherently wrong with exposing its data fields**.

It is also acceptable for a public class to expose fields directly is the fields are
immutable. 
* You can't change the representation of such a class without changing its API
* You can't take auxiliary actions when a field is read
* You *can* enforce invariants.
```
// Public class with exposed immutable fields
public final class Time {
    private static final int HOURS_PER_DAY = 24;
    private static final int MINUTES_PER_HOUR = 60;
    
    public final int hour;
    public final int minute;
    
    public Time(int hour, int minute) {
        if (hour < 0 || hour >= HOURS_PER_DAY) {
            throw new IllegalArgumentException("Hour: " + hour);
        }
        
        if (minute < 0 || minute >= MINUTES_PER_HOUR) {
            throw new IllegalArgumentException("Min: " + minute);
        }
        
        this.hour = hour;
        this.minute = minute;
    }
    ... // Remainder omitted
}
```

[Example classes with exposed fields and encapsulation of data can be found here](../src/effectivejava/chapter4/item16)