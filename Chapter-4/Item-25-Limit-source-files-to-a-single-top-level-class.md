**Never** consider defining multiple top-level classes in a single source file. Doing so makes
it possible to provide multiple definitions for a class, with which definition gets used being
decided by the order in which the source files are passed to the compiler

Consider this source file that refers to members of two other top-level classes:
```
public class Main {
    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }
}
```

Now suppose you define both *Utensil* and *Dessert* in a single source file named *Utensil.java*:
```
// Two classes defined in one file. Don't ever do this!
class Utensil {
    static final String NAME = "pan";
}

class Dessert {
    static final String NAME = "cake";
}
```

The main program prints *pancake*.

Now suppose you add another source file named *Dessert.java* that defines the same two classes:
```
// Two classes defined in one file. Don't ever do this!
class Utensil {
    static final String NAME = "pot";
}

class Dessert {
    static final String NAME = "pie";
}
```

Compiling the program with the command *javac Main.java Dessert.java*, the compilation
will fail, with the compiler telling you that you've multiply defined the classes
*Utensil* and *Dessert*.

However, compiling with the command *javac Main.java* or *javac Main.java Utiensil.java*
will print out *pancake* while compiling with *javac Dessert.java Main.java* will print
*potpie*. The behaviour of the program is affected by the order in which the source files
are passed to the compiler.

We can fix this by splitting the top-level classes into separate source files or static
member classes ([Item 24](./Item-24-Favor-static-member-classes-over-nonstatic.md)):
```
// Static member classes instead of multiple top-level classes
public class Test {
    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }
    
    private static class Utensil {
        static final String NAME = "pan";
    }
    
    private static class Dessert {
        static final String NAME = "cake";
    }
}
```