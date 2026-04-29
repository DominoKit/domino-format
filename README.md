# Domino Format

Domino Format is a standalone multi-style string formatting library for DominoKit projects.

It is built around one parser that can mix four placeholder styles in the same template:

- indexed placeholders such as `{0}`
- named placeholders such as `$(userName)`
- percent placeholders such as `%d`
- dollar tokens such as `$D(#,##0.00)`

The library publishes one artifact:

- `domino-format`
  Shared parser, `DominoFormat`, `DominoFormatter`, `FormattingSupport`, GWT formatting support,
  and JVM formatting support

## Choose The Right Artifact

Use `domino-format` when:

- you are writing runtime-agnostic code
- you want `DominoFormat.format(...)` to work out of the box for numeric and date patterns
- you want JVM-native `DecimalFormat` and `SimpleDateFormat` behavior
- you need GWT-compatible formatting support from the same artifact
- you want to provide your own `FormattingSupport`

## Maven Dependencies

```xml
<dependency>
  <groupId>org.dominokit.format</groupId>
  <artifactId>domino-format</artifactId>
  <version>HEAD-SNAPSHOT</version>
</dependency>
```

## Quick Start

### Shared Static API

The library provides the shared `org.dominokit.format.DominoFormat` facade and installs
`JvmFormattingSupport` by default. No manual bootstrap step is required for patterned numbers or
dates on the JVM.

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

### Instance Usage

Use `DominoFormatter` when you need an isolated formatter instance that does not mutate the shared
static facade.

```java
import org.dominokit.format.DominoFormatter;

DominoFormatter formatter = new DominoFormatter();

String message = formatter.format("Hello {0}, count=%d, done=%b", "Ahmad", 3, true);
```

### Custom Formatting Support

If you want patterned number or date formatting to use custom delegates, provide a
`FormattingSupport` instance explicitly.

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

### Custom Missing Named Argument Handler

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
- `%n` portable line feed (`\n`)

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
format that pattern. The shared static API installs JVM support by default; isolated formatter
instances use the support object supplied to their constructor.

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
  Shared static facade supplied by `domino-format`
- `new DominoFormatter(...)`
  Explicit formatter instance supplied by `domino-format`

Use the static API when application-wide defaults are appropriate.

Use `DominoFormatter` instances when:

- you want isolated configuration
- you do not want to mutate shared static state
- you are building reusable library code

### Overriding Runtime Defaults

The static facade installs a default support object automatically, but you can replace it:

```java
FormattingSupport support =
    FormattingSupport.create(
        (pattern, value) -> "number[" + pattern + "]=" + value,
        (pattern, value) -> "date[" + pattern + "]=" + value.getTime());

DominoFormat.setFormattingSupport(support);

String message = DominoFormat.format("Price: $D(0.00)", 12.5);
```

To restore the JVM-backed support:

```java
DominoFormat.resetDefaultFormattingSupport();
```

`DominoFormat.withFormattingSupport(...)` is a convenience that replaces the shared static
formatter and returns it. If you need an isolated formatter that does not change global state, use
`new DominoFormatter(customSupport)` instead.

### Overriding The Missing Named Argument Handler

The library also installs a default missing named argument handler that replaces unresolved
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

- `domino-format` is a single `gwt-lib` module and the only published artifact.
- `DominoFormat` is the shared static facade and starts with `JvmFormattingSupport`.
- `GwtFormattingSupport` and `JvmFormattingSupport` both extend `FormattingSupport`, so callers can
  replace the shared default with a runtime-specific or custom support object.
