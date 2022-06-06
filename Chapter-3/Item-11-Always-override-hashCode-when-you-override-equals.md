**You must override *hashCode* in every class that overrides *equals*.** Failing to do so will prevent your class from
functioning properly in collections such as *HashMap* and *HashSet*. These classes have an optimisation that caches the
hash code associated with each entry and doesn't bother checking for object  equality if the hash codes don't match,
and so **Equal objects must have equals hash codes**.

```
// The worst possible legal hashCode implementation - never use!
@Override public int hashCode() { return 42; }
```

Above is technically a legal implementation of the *hashCode* method, however it is atrocious because it ensures that
*every* object has the same hash code. Therefore, every object hashes to the same bucket, and has tables degenerate to
linked lists, rendering *HashMaps* and *HashSets* pointless.

A good hash function tends to produce unequal hash codes for unequal instances and should distribute any reasonable
collection of unequal instances uniformly acreoss all *int* values. You should use every significant field in your object,
however, you *must* exclude any fields that are not used in the *equals* comparisons.

An example for the *PhoneNumber* class used previously:
```
// Typical hashCode method
// Note: 31 is used below as it is a prime number that can be optimised to a bit shift and a subtraction (31 * i == (i << 5) - i)
@Override public int hashCode() {
    int result = Short.hashCode(areaCode);
    result = 31 * result + Short.hashCode(prefix);
    result = 31 * result + Short.hashCode(lineNum);
    return result;
}
```

It is also possible to use the *Objects* class' static *hash* method, however this method may run more slowly due to
array creation to pass a variable number of arguments and boxing/unboxing of any arguments.
```
// One-line hashCode method - mediocre performance
@Override public int hashCode() {
    return Objects.hash(lineNum, prefix, areaCode);
}
```

If a class is immutable and the cost of computing the hash code is significant, you might consider caching the hash code
object, either by calculating when the instance is created or lazily initializing when the hash code is needed.

```
// hashCode method with lazily initialized cached hash code
private int hashCode; // Automatically initialized to 0
@Override public int hashCode() {
    int result = hashCode;
    if (result == 0) {
        result = Short.hashCode(areaCode);
        result = 31 * result + Short.hashCode(prefix);
        result = 31 * result + Short.hashCode(lineNum);
        hashCode = result;
    }
    return result;
}
```

**Do not be tempted to exclude significant fields from the hash code computation to improve performance.**

[A working PhoneNumber class showcasing the need for overriding hashcode when you override equals can be found here](../src/effectivejava/chapter3/item11/PhoneNumber.java)