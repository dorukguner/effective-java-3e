**Finalizers are unpredictable, often dangerous, and generally unnecessary.** As of Java 9, finalizers have been
deprecated and replaced by *cleaners*. **Cleaners are less dangerous than finalizers, but still unpredictable, slow,
and generally unnecessary.**

Java's garbage collector reclaims storage associated with an object when it becomes unreachable. For other non-memory
resources, a *try-with-resources* or a *try-finally* block is used (Item 9).

A shortcoming of finalizers and cleaners is that there is no guarantee they'll be executed promptly. It can take
arbitrarily long between the time that an object becomes unreachable and the time its finalizer or cleaner runs. Meaning
you should **never do anything time-critical in a finalizer or cleaner.** The time in which finalizers and cleaners
are executed is dependent on the garbage collection algorithm, which vary widely across implementations. The 
specification provides no guarantee that finalizers and cleaners will run at all, and so you should **never depend on a
finalizer or cleaner to update persistent state.**

Another shortcoming is that uncaught exceptions thrown during finalization are ignored and terminate the finalization
of that object. This can leave objects in a corrupt state. 

**There is a *severe* performance penalty for using finalizers and cleaners**. On Joshua Bloch's machine the lifecycle
of a simple *AutoCloseable* object took:
* About 12ns using a *try-with-resources* 
* About 550ns using a *finalizer*
* About 500ns using a *cleaner* on all instances of the class, or about 66ns when only used as a safety net

**Finalizers open up your class to *finalizer attacks*.** If an exception is thrown from a constructor or its
serialization equivalents, the finalizer of a malicious subclass can run on the partially constructed object. **To
protect non-final classes from finalizer attacks, write a final *finalize* method that does nothing.**

Instead of writing a finalizer or a cleaner for a class, **have your class implement *AutoCloseable***, and require its
clients to invoke the *close* method on each instance when it is no longer needed.

Use cases for finalizers and cleaners:
* Act as a safety net in case the owner of a resource neglects to call its *close* method. There is no guarantee the
cleaner or finalizer will run promptly (or at all), however it is better to free the resource late rather than never.
* Clearing *native peers*. A native peer is a native (non-Java) object to which a normal object delegates via native
methods. The garbage collector does not know about these objects and can't reclaim them when their Java peers are
reclaimed. However, this can also be achieved using a *close* method as described earlier.

```
// An autocloseable class using a cleaner as a safety net
public class Room implements AutoCloseable {
    private static final Cleaner cleaner = Cleaner.create();

    // Resource that requires cleaning. Must not refer to Room!
    private static class State implements Runnable {
        int numJunkPiles; // Number of junk piles in this room

        State(int numJunkPiles) {
            this.numJunkPiles = numJunkPiles;
        }

        // Invoked by close method or cleaner
        @Override public void run() {
            System.out.println("Cleaning room");
            numJunkPiles = 0;
        }
    }

    // The state of this room, shared with our cleanable
    private final State state;

    // Our cleanable. Cleans the room when it’s eligible for gc
    private final Cleaner.Cleanable cleanable;

    public Room(int numJunkPiles) {
        state = new State(numJunkPiles);
        cleanable = cleaner.register(this, state);
    }

    @Override public void close() {
        cleanable.clean();
    }
}
```

It is critical that a *State* instance does not refer to its *Room* instance. If it did, it would create a circularity
that would prevent the *Room* instance from becoming eligible for garbage collection. 

The *Room's* cleaner is only a safety net, if clients surround all *Room* instantiations in *try-with-resource* blocks,
automatic cleaning will never be required.
```
public class Adult {
    public static void main(String[] args) {
        try (Room myRoom = new Room(7)) {
            System.out.println("Goodbye");
        }
    }
}
```

[Runnable classes showcasing both well and ill-behaved clients of an AutoCloseable class can be found here](../src/effectivejava/chapter2/item8)

TODO link to item 9