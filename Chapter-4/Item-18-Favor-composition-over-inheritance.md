Implementation inheritance from ordinary concrete classes across package boundaries can be
dangerous. **Unlike method invocation, inheritance violates encapsulation**, i.e. a subclass
depends on the implementation of its superclass for proper function. If the superclass'
implementation changes, the subclass may break.

As an example, we have a program that uses a HashSet. We want to query how many elements have
been added since it was created:
```
// Broken - Inappropriate use of inheritance!
public class InstrumentedHashSet<E> extends HashSet<E> {
    // The number of attempted element insertions
    private int addCount = 0;
    
    public InstrumentedHashSet() {
    }
    
    public InstrumentedHashSet(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }
    
    @Override public boolean add(E e) {
        addCount++;
        return super.add(e);
    }
    
    @Override public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }
    
    public int getAddCount() {
        return addCount;
    }
}
```

Suppose we add three elements to an instance of our InstrumentedHashSet using the *addAll*
method, when calling *getAddCount* we would expect it to return 3, however it returns 6.
This is due to internally, *HashSet's* *addAll* method is implemented on top if its *add*
method, although, this is (reasonably) not documented in *HashSet*.

We could "fix" the subclass by:
* Eliminating its override of the *addAll* method. However, this would be fragile as it would 
now depend on *HashSet's addAll* method being implemented on top of its *add* method. This
is not guaranteed to hold in all implementations of Java
* Overriding the *addAll* method to iterate over the specified collection, calling *add* once
for each element. However, this amounts to reimplementing superclass methods that may or may
not result in self-use, which is error-prone and may reduce performance

Additionally, any subsequent updates made to the superclass may break this implementation in 
the case that a new method capable of inserting an element is added, which is not overridden
by the subclass.

To avoid the problems described above, instead of extending an existing class, give your new
class a private field that references an instance of the existing class. The resulting class
will be rock solid, with no dependencies on the implementation details of the existing class.
An implementation of *InstrumentedHashSet* that uses composition-and-forwarding:
```
// Wrapper class - uses composition in place of inheritance
public class InstrumentedSet<E> extends ForwardingSet<E> {
    private int addCount = 0;
    
    public InstrumentedSet(Set<E> s) {
        super(s);
    }
    
    @Override public boolean add(E e) {
        addCount++;
        return super.add(e);
    }
    
    @Override public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }
    
    public int getAddCount() {
        return addCount;
    }
}

// Reusable forwarding class
public class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;
    public ForwardingSet(Set<E> s) { this.s = s; }
    
    public void clear() { s.clear(); }
    public boolean contains(Object o) { return s.contains(o); }
    public boolean isEmpty() { return s.isEmpty(); }
    public int size() { return s.size(); }
    public Iterator<E> iterator() { return s.iterator(); }
    public boolean add(E e) { return s.add(e); }
    public boolean remove(Object o) { return s.remove(o); }
    public boolean containsAll(Collection<?> c) { return s.containsAll(c); }
    public boolean addAll(Collection<? extends E> c) { return s.addAll(c); }
    public boolean removeAll(Collection<?> c) { return s.removeAll(c); }
    public boolean retainAll(Collection<?> c) { return s.retainAll(c); }
    public Object[] toArray() { return s.toArray(); }
    public <T> T[] toArray(T[] a) { return s.toArray(a); }
    @Override public boolean equals(Object o) { return s.equals(o); }
    @Override public int hashCode() { return s.hashCode(); }
    @Override public String toString() { return s.toString(); }
}
```

Above, the *InstrumentedSet* class is enabled by the existence of the *Set* interface, which
captures the functionality of the *HashSet* class. Unlike the inheritance-based approach,
this allows for robustness and flexibility as the class essentially transforms any one *Set*
into another, adding the instrumentation functionality:
```
Set<Instant> times = new InstrumentedSet<>(new TreeSet<>(cmp));
Set<E> s = new InstrumentedSet<>(new HashSet<>(INIT_CAPACITY));
```
The *InstrumentedSet* class is known as a *wrapper* class, and this pattern is known as the
*Decorator* pattern. 

The disadvantages of wrapper classes are few:
* They are not suited for use in *callback frameworks* as the wrapped object doesn't know of
its wrapper and passes a reference to itself and callbacks that elude the wrapper
* There is also a worry about the performance impact forwarding method invocations or the
memory footprint of wrapper objects. Neither of which turned out to have much impact in practice

Inheritance is appropriate only in circumstances where the subclass really is a *subtype* of
the superclass. Ask 'Is every *B* really an *A*?'. We should also ask 'Does the class we are
extending have any flaws in its API?' as inheritance propagates any flaws in the superclass'
API, while composition lets your design a new API that hides these flaws.

[Runnable classes of the InstrumentedHashSet, InstrumentedSet and ForwardingSet can be found here](../src/effectivejava/chapter4/item18)