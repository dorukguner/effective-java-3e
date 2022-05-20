Many classes depend on one or more underlying resources. For example, a spell checker depends on a dictionary. **Stray
away from having these dependencies as static utility classes or singletons.**

```
// Inappropriate use of static utility - inflexible & untestable!
public class SpellChecker {
    private static final Lexicon dictionary = ...;
    private SpellChecker() {} // Noninstantiable
    public static boolean isValid(String word) { ... }
    public static List<String> suggestions(String typo) { ... }
}
```
```
// Inappropriate use of singleton - inflexible & untestable!
public class SpellChecker {
    private final Lexicon dictionary = ...;
    private SpellChecker(...) {}
    public static INSTANCE = new SpellChecker(...);
    public boolean isValid(String word) { ... }
    public List<String> suggestions(String typo) { ... }
}
```

Both of the above approaches assume there is only one dictionary worth using, which is impractical as each language
can have it's own dictionary. A non final dictionary field that is continually updated can work but is awkward,
error-prone and unworkable in a concurrent setting.

**Instead we should look at supporting multiple instances of the class, each of which uses the resource desired by the
client using dependency injection.**

```
// Dependency injection provides flexibility and testability
public class SpellChecker {
    private final Lexicon dictionary;
    public SpellChecker(Lexicon dictionary) {
    this.dictionary = Objects.requireNonNull(dictionary);
    }
    public boolean isValid(String word) { ... }
    public List<String> suggestions(String typo) { ... }
}
```

* Allows for working with an arbitrary number of resources and dependency graphs
* Preserves immutability
* Equally applicable to constructors, static factories ([Item 1](./Item-1-Consider-static-factory-methods-instead-of-constructors.md))
and builders ([Item 2](./Item-2-Consider-a-builder-when-faced-with-many-constructor-parameters.md))

Although dependency injection can clutter up large projects which contain thousands of dependencies, this can be
eliminated using a *dependency injection framework* such as [Dagger](https://dagger.dev/),
[Guice](https://github.com/google/guice) or [Spring](https://spring.io/)