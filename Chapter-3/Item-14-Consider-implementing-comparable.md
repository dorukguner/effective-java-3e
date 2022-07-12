Similar to *Object's equals* method, *Comparable's compareTo* compares objects by order in addition to equality comparisons. By implementing *Comparable*, a class indicates that its instances have a *natural ordering*.

When a class implements *Comparable* it is easy to sort, search, compute extreme values, and maintain automatically sorted collections.

Sorting is as simple as:
```
Arrays.sort(a);
```

While the following prints an alphabetized list of its command line arguments with duplicates eliminated:
```
public class WordList {
    public static void main(String[] args) {
        Set<String> s = new TreeSet<>();
        Collections.addAll(s, args);
        System.out.println(s);
    }
}
```

The general contract of the *compareTo* method is:
Compares this object with the specified object for order. Returns a negative integer, zero, or a positive integer as this object is less than, equal to, or grater than the specified object. Throws *ClassCastException* if the specified object's type prevents it from being compared to this object. 

sgn(expression) designates the mathematical *signum* function, which returns -1, 0, or 1, according to whether the value of *expression* is negative, zero, or positive. The implementor must ensure that:
* sgn(x.compareTo(y)) == -sgn(y.compareTo(x))
* (x.compareTo(y) > 0 && y.compareTo(z) > 0) implies x.compareTo(z) > 0, i.e. the relation is transitive
* x.compareTo(y) == 0 implies that sgn(x.compareTo(z)) == sgn(y.compareTo(z))
* It is strongly recommended, but not required, that (x.compareTo(y) == 0) == (x.equals(y)). If this is violated, it is recommended that this is indicated on the class with a note "Note: This class has a natural ordering that is inconsistent with equals."

The caveat to the above is that we must obey the same restrictions imposed by the *equals* contract: there is no way to extend an instantiable class with a new value component while preserving the *compareTo* contract, unless you are willing to forgo the benefits of object-oriented abstraction ([Item 10](./Item-10-Obey-the-general-contract-when-overriding-equals.md)). The same workaround applies, too. To add a value component to a class that implements *Comparable*, don't extend it; write an unrelated class containing an instance of the first class. 

To compare object reference fields, invoke the *compareTo* method recursively. If a field does not implement *Comparable* or you need a nonstandard ordering, use a *Comparator* instead.

If a class has multiple significant fields, the order in which you compare them is critical. Start with the most significant field and work your way down. If a comparison results in anything other than zero, just return the result, otherwise continue onto the next most-significant field, as so on. Here is a *compareTo* method for the *PhoneNumber* class in Item 11:
```
// Multiple-field Comparable with primitive fields
public int compareTo(PhoneNumber pn) {
    int result = Short.compare(areaCode, pn.areaCode);
    if (result == 0) {
        result = Short.compare(prefix, pn.prefix);
        if (result == 0) {
            result = Short.compare(lineNum, pn.lineNum);
        }
    }

    return result;
}
```

In Java 8, the *Comparator* interface was outfitted with a set of *Comparator construction methods* which can be used to implement a *compareTo* method. Many programmers prefer the conciseness of this approach, however, sorting arrays of *PhoneNumber*& instances is about 10% slower on the author's machine. Here's how the *compareTo* method for *PhoneNumber* looks using this approach:
```
// Comparable with comparator construction methods
private static final Comparator<PhoneNumber> COMPARATOR =
        comparingInt((PhoneNumber pn) -> pn.areaCode)
            .thenComparingInt(pn -> pn.prefix)
            .thenComparingInt(pn -> pn.lineNum);

public int compareTo(PhoneNumber pn) {
    return COMPARATOR.compare(this, pn);
}
```

The *Comparator* class has a full complement of construction methods which can be used for any primitive or object reference types.

Occasionally you may see *compareTo* or *compare* methods that rely on the difference between two values:
```
// BROKEN difference-based comparator - violates transitivity!
static Comparator<Object> hashCodeOrder = new Comparator<>() {
    public int compare(Object o1, Object o2) {
        return o1.hashCode() - o2.hashCode();
    }
};
```
***Do not*** use this technique. It is susceptible to integer overflow and IEEE 654 floating point arithmetic artifacts. It is best to use either a static compare method:
```
// Comparator based on static compare method
static Comparator<Object> hashCodeOrder = new Comparator<>() {
    public int compare(Object o1, Object o2) {
       return Integer.compare(o1.hashCode(), o2.hashCode());
    }
};
```
or a comparator construction method:
```
// Comparator based on Comparator construction method
static Comparator<Object> hashCodeOrder =
        Comparator.comparingInt(o -> o.hashCode());
```

[Example classes implementing comparable can be found here](../src/effectivejava/chapter3/item14)