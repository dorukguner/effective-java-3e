The general contract for *toString* says that the returned string should be "a concise but informative representation
that is easy for a person to read." While *Object* provides an implementation of the *toString* method that returns the class
name followed by the unsigned hexadecimal representation of the hash code, (E.g. PhoneNumber@163b91) that is easy to read,
it is not very informative.

**Providing a good *toString* implementation makes your class much more pleasant to use and makes systems using the class
easier to debug.** Providing a good *toString* method, generating a useful diagnostic method is as easy as:
```
System.out.println("Failed to connect to " + phoneNumber);
```

**When practical, the *toString* method should return *all* of the interesting information contained in the object.** The
string should be self-explanatory, failing to include all of an object's interesting information in its string representation
could lead to test failure reports the look like this:
```
Assertion failure: expected {abc, 123}, but was {abc, 123}.
```

An important decision to make when implementing a *toString* method is whether to specify the format of the return value
in the documentation. It is recommended that you do this for *value classes* such as phone number or matrix. This also
allows for a matching static factory or constructor such as in *BigInteger and BigDecimal*. However, this also means that
you're stuck with this format for life, assuming your class is widely used as programmers will write code to parse the
representation, generate it, and embed it into persistent data.

**Whether or not you decide to specify the format, you should clearly document your intentions.* For example here's a
*toString* method for the *PhoneNumber* class in [Item 11](./Item-11-Always-override-hashCode-when-you-override-equals.md)
with a specified format:
```
/**
 * Returns the string representation of this phone number.
 * The string consists of twelve characters whose format is
 * "XXX-YYY-ZZZZ", where XXX is the area code, YYY is the
 * prefix, and ZZZZ is the line number. Each of the capital
 * letters represents a single decimal digit.
 *
 * If any of the three parts of this phone number is too small
 * to fill up its field, the field is padded with leading zeros.
 * For example, if the value of the line number is 123, the last
 * four characters of the string representation will be "0123".
 */
@Override public String toString() {
    return String.format("%03d-%03d-%04d",
            areaCode, prefix, lineNum);
}
```

If you decide not to specify a format, the documentation should read something like this:
```
/**
* Returns a brief description of this potion. The exact details
* of the representation are unspecified and subject to change,
* but the following may be regarded as typical:
*
* "[Potion #9: type=love, smell=turpentine, look=india ink]"
*/
@Override public String toString() { ... }
```

**Always provide programmatic access to the information contained in the value returned by *toString*.** Failing to do so
will force programmers who need this information to parse the string.