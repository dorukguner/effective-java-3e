The Java libraries include many resources that must be closed manually, such as *InputStream* and
*OutputStream*. Failure to close resources can lead to performance consequences, even in the case
that the resource uses finalizers ([Item 8](./Item-8-Avoid-finalizers-and-cleaners.md)).

Historically, a *try-finally* statement was the best way to guarantee a resource would be closed properly.
```
// try-finally - No longer the best way to close resources!
static String firstLineOfFile(String path) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(path));
    try {
        return br.readLine();
    } finally {
        br.close();
    }
}
```

This may not look bad but it gets worse when you add a second resource:
```
// try-finally is ugly when used with more than one resource!
static void copy(String src, String dst) throws IOException {
    InputStream in = new FileInputStream(src);
    try {
        OutputStream out = new FileOutputStream(dst);
        try {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            }
        } finally {
            out.close();
        }
    } finally {
        in.close();
    }
}
```

The two above examples have a subtle deficiency, the code in the *try* block and the *finally* block is
capable of throwing exceptions. In the *firstLineOfFile* method, the call to *readLine* could throw an
exception, and the call to *close* could also fail for the same reason. Under these circumstances, the
second exception completely obliterates the first one. There is no record of the first exception in the
stack trace, greatly complicating debugging the system. 

This can be solved using *try-with-resources* introduced in Java 7. To be usable with this construct, a
resource must implement the *AutoCloseable* interface.

The first example using *try-with-resources*:
```
// try-with-resources - the the best way to close resources!
static String firstLineOfFile(String path) throws IOException {
    try (BufferedReader br = new BufferedReader(
            new FileReader(path))) {
        return br.readLine();
    }
}
```

The second example using *try-with-resources*:
```
// try-with-resources on multiple resources - short and sweet
static void copy(String src, String dst) throws IOException {
    try (InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst)) {
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = in.read(buf)) >= 0) {
            out.write(buf, 0, n);
        }
    }
}
```

Not only are *try-with-resources* shorter and more readable, they also provide far better diagnostics.
The first exception thrown suppresses any subsequent exception, however, the suppressed exceptions are
not merely discarded; they are printed in the stack trace with a notation saying that they were suppressed.
You can also access them programmatically with the *getSuppressed* method in the *Throwable* class.

It is also possible to put catch clauses on *try-with-resources* statements:
```
// try-with-resources with a catch clause
static String firstLineOfFile(String path, String defaultVal) {
    try (BufferedReader br = new BufferedReader(
            new FileReader(path))) {
        return br.readLine();
    } catch (IOException e) {
        return defaultVal;
    }
}
```

[Runnable classes showcasing both try-finally and try-with-resources examples can be found here](../src/effectivejava/chapter2/item9)