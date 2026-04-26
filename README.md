# Domino Format

Domino Format is a standalone multi-style string formatting library for DominoKit projects.

It is built around one parser that can mix four placeholder styles in the same template:

- indexed placeholders such as `{0}`
- named placeholders such as `$(userName)`
- percent placeholders such as `%d`
- dollar tokens such as `$D(#,##0.00)`

The library is split into three modules:

- `domino-format-core`
  Shared parser, `DominoFormatter`, `FormattingSupport`, and validation rules
- `domino-format-jvm`
  JVM runtime adapter that exposes `org.dominokit.format.DominoFormat` with `DecimalFormat` and
  `SimpleDateFormat` support preinstalled
- `domino-format-gwt`
  GWT runtime adapter that exposes the same `org.dominokit.format.DominoFormat` API with
  `NumberFormat` and `DateTimeFormat` support preinstalled

## Choose The Right Module

Use `domino-format-core` when:

- you only need the shared parser and instance-based formatting API
- you are writing runtime-agnostic code
- you want to provide your own `FormattingSupport`

Use `domino-format-jvm` when:

- your code runs on the JVM
- you want `DominoFormat.format(...)` to work out of the box for numeric and date patterns
- you want JVM-native `DecimalFormat` and `SimpleDateFormat` behavior

Use `domino-format-gwt` when:

- your code runs on GWT
- you want the same `DominoFormat.format(...)` API as the JVM module
- you want GWT-native `NumberFormat` and `DateTimeFormat` behavior

## Maven Dependencies

### Core Only

```xml
<dependency>
  <groupId>org.dominokit.format</groupId>
  <artifactId>domino-format-core</artifactId>
  <version>HEAD-SNAPSHOT</version>
</dependency>
```

### JVM

```xml
<dependency>
  <groupId>org.dominokit.format</groupId>
  <artifactId>domino-format-jvm</artifactId>
  <version>HEAD-SNAPSHOT</version>
</dependency>
```

### GWT

```xml
<dependency>
  <groupId>org.dominokit.format</groupId>
  <artifactId>domino-format-gwt</artifactId>
  <version>HEAD-SNAPSHOT</version>
</dependency>
```

## Quick Start

### JVM Or GWT Runtime Modules

When you depend on `domino-format-jvm` or `domino-format-gwt`, the selected runtime module
provides the shared `org.dominokit.format.DominoFormat` facade and installs the correct default
`FormattingSupport` automatically. No manual bootstrap step is required for patterned numbers or
dates.

```java
import org.dominokit.format.DominoFormat;

String message =
    DominoFormat.format(
        "User $(userName) bought $N(000) items for $D(#,##0.00) on $T(yyyy-MM-dd)",
        DominoFormat.byName("userName", "Ahmad"),
        3,
        1250.75,
        new Date());
```

### Named Arguments Quick Start

Named placeholders use the syntax `$(expression)`. You can satisfy them either with explicit named
arguments created through `DominoFormat.byName(...)` or with a `Map<String, ?>`.

```java
String message =
    DominoFormat.format(
        "Hello $(userName), you bought %d items",
        DominoFormat.byName("userName", "Ahmad"),
        3);
```

```java
String message =
    DominoFormat.format(
        "Hello $(userName), you bought %d items",
        Map.of("userName", "Ahmad"),
        3);
```

### Core-Only Usage

`domino-format-core` does not publish the static `DominoFormat` facade. It exposes the reusable
instance API through `DominoFormatter`.

```java
import org.dominokit.format.DominoFormatter;

DominoFormatter formatter = new DominoFormatter();

String message = formatter.format("Hello {0}, count=%d, done=%b", "Ahmad", 3, true);
```

### Core-Only Usage With Custom Formatting Support

If you depend on `domino-format-core` directly and still want patterned number or date formatting,
provide a `FormattingSupport` instance explicitly.

```java
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import org.dominokit.format.DominoFormatter;
import org.dominokit.format.FormattingSupport;

FormattingSupport support =
    FormattingSupport.create(
        (pattern, value) -> new DecimalFormat(pattern).format(value),
        (pattern, value) -> new SimpleDateFormat(pattern).format(value));

DominoFormatter formatter = new DominoFormatter(support);

String message =
    formatter.format(
        "Price=$D(#,##0.00), created=$T(yyyy-MM-dd)",
        1250.75,
        new Date());
```

### Core-Only Usage With A Custom Missing Named Argument Handler

If unresolved named placeholders should produce custom fallback text, supply a
`MissingNamedArgumentHandler`.

```java
import org.dominokit.format.DominoFormatter;

DominoFormatter formatter =
    new DominoFormatter()
        .withMissingNamedArgumentHandler(expression -> "<missing:" + expression + ">");

String message = formatter.format("Hello $(userName)");
```

## Formatting Styles

### Indexed Placeholders

Indexed placeholders read arguments by explicit zero-based position.

```java
DominoFormat.format("Hello {0}, you have {1} items", "Ahmad", 3);
```

### Named Placeholders

Named placeholders use the syntax `$(expression)`.

Supported named argument sources:

- `DominoFormat.byName("expression", value)` or `DominoFormatter.byName("expression", value)`
- `Map<String, ?>` values passed directly to `format(...)`

Examples:

```java
DominoFormat.format(
    "Hello $(userName), you have %d items",
    DominoFormat.byName("userName", "Ahmad"),
    3);
```

```java
DominoFormat.format(
    "Hello $(userName), you have %d items",
    Map.of("userName", "Ahmad"),
    3);
```

Resolution behavior:

- leading and trailing whitespace inside `$(...)` is ignored
- internal whitespace is preserved, so `$(first name)` is valid
- if the same name is provided more than once, the last supplied value wins
- if a name is missing, the default behavior replaces it with an empty string

You can override the default missing-name behavior:

```java
DominoFormat.setMissingNamedArgumentHandler(expression -> "<missing:" + expression + ">");

String message = DominoFormat.format("Hello $(userName)");
// Hello <missing:userName>
```

### Percent Placeholders

Supported percent tokens:

- `%s` string via `String.valueOf`
- `%d` integral values (`byte`, `short`, `int`, `long`)
- `%f` decimal values
- `%b` boolean values
- `%%` literal percent sign
- `%n` runtime line separator via `System.lineSeparator()`

Example:

```java
DominoFormat.format("Hello %s, count=%d, done=%b%n%%", "Ahmad", 3, true);
```

### Dollar Tokens

Supported dollar tokens:

- `$S` string values
- `$L` literal `String.valueOf(...)`
- `$N` number values, optionally with a pattern such as `$N(000)`
- `$D` decimal values, optionally with a pattern such as `$D(#,##0.00)`
- `$B` boolean values
- `$T` `java.util.Date` values, optionally with a pattern such as `$T(yyyy-MM-dd)`
- `$$` literal dollar sign

Examples:

```java
DominoFormat.format("Qty: $N(000)", 7);
DominoFormat.format("Price: $D(#,##0.00)", 1250.75);
DominoFormat.format("Created: $T(yyyy-MM-dd)", new Date());
```

If you use `$N(...)`, `$D(...)`, or `$T(...)`, the active `FormattingSupport` must know how to
format that pattern. This is automatic in the JVM and GWT runtime modules, and explicit when you
use `domino-format-core` directly.

### Mixed Templates

All placeholder styles can be mixed in one template:

```java
DominoFormat.format(
    "User {0} / $(userName) bought %d items for $D(#,##0.00)",
    DominoFormat.byName("userName", "Ahmad"),
    "Ahmad",
    3,
    1250.75);
```

## Argument Resolution Rules

Argument consumption is intentionally strict and predictable:

- named arguments supplied through `byName(...)` or `Map<String, ?>` sources are removed from the
  positional argument stream
- indexed placeholders such as `{0}` reserve a specific argument slot
- named placeholders such as `$(userName)` resolve by expression and do not consume positional
  arguments
- percent placeholders and dollar tokens consume arguments sequentially from left to right
- sequential placeholders skip any slot already reserved by an indexed placeholder

Example:

```java
DominoFormat.format(
    "{1} %s $(userName)",
    DominoFormat.byName("userName", "Ahmad"),
    "zero",
    "one");
// one zero Ahmad
```

## Static API vs Instance API

There are two ways to use Domino Format:

- `DominoFormat.format(...)`
  Shared static facade supplied by `domino-format-jvm` and `domino-format-gwt`
- `new DominoFormatter(...)`
  Explicit formatter instance supplied by `domino-format-core`

Use the static API when application-wide defaults are appropriate.

Use `DominoFormatter` instances when:

- you want isolated configuration
- you do not want to mutate shared static state
- you are building reusable library code

### Overriding Runtime Defaults

Runtime modules install a default support object automatically, but you can replace it:

```java
FormattingSupport support =
    FormattingSupport.create(
        (pattern, value) -> "number[" + pattern + "]=" + value,
        (pattern, value) -> "date[" + pattern + "]=" + value.getTime());

DominoFormat.setFormattingSupport(support);

String message = DominoFormat.format("Price: $D(0.00)", 12.5);
```

To restore the runtime-provided support:

```java
DominoFormat.resetDefaultFormattingSupport();
```

`DominoFormat.withFormattingSupport(...)` is a convenience that replaces the shared static
formatter and returns it. If you need an isolated formatter that does not change global state, use
`new DominoFormatter(customSupport)` instead.

### Overriding The Missing Named Argument Handler

Runtime modules also install a default missing named argument handler that replaces unresolved
`$(...)` placeholders with an empty string. You can replace it:

```java
DominoFormat.setMissingNamedArgumentHandler(expression -> "[" + expression + "]");

String message = DominoFormat.format("Hello $(userName)");
// Hello [userName]
```

To restore the default empty-string behavior:

```java
DominoFormat.resetDefaultMissingNamedArgumentHandler();
```

`DominoFormat.withMissingNamedArgumentHandler(...)` updates the shared static formatter and returns
it. If you want per-instance behavior instead, create a dedicated `DominoFormatter` with the
desired handler.

## Migrating From The Old Runtime Facades

If you were using the older runtime-specific facade classes, migrate as follows:

- replace `org.dominokit.format.jvm.JvmDominoFormat` with `org.dominokit.format.DominoFormat`
  from `domino-format-jvm`
- replace `org.dominokit.format.gwt.GwtDominoFormat` with `org.dominokit.format.DominoFormat`
  from `domino-format-gwt`
- remove manual runtime-support installation when you only need the default JVM or GWT
  behavior, because the runtime modules now install their own default `FormattingSupport`

Before:

```java
JvmDominoFormat.format("Price: $D(#,##0.00)", 1234.5);
```

After:

```java
DominoFormat.format("Price: $D(#,##0.00)", 1234.5);
```

## Error Handling

Domino Format fails fast with `FormatException` when it encounters invalid input. Typical cases
include:

- malformed placeholders such as `{name}` when only numeric indexes are allowed
- empty named placeholders such as `$()`
- missing arguments for indexed, percent, or dollar-token placeholders
- unsupported percent or dollar-token markers
- type mismatches such as `%b` with a string or `$T` with a non-date value
- patterned number or date tokens used without a matching formatter
- invalid named argument map keys
- invalid numeric or date patterns rejected by the active runtime formatter
- unresolved named placeholders do not fail by default; they are delegated to the installed
  `MissingNamedArgumentHandler`

## Notes

- `domino-format-jvm` and `domino-format-gwt` export the same `org.dominokit.format.DominoFormat`
  class name. Choose the module that matches your runtime.
- `domino-format-core` is the shared engine module and is the right choice when you need direct
  control over formatter instances.
