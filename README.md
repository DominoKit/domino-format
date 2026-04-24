# Domino Format

Domino Format is a standalone multi-style string formatting library for DominoKit projects.

The initial implementation is split into three modules:

- `domino-format-core`: shared parsing and formatting engine
- `domino-format-jvm`: JVM number/date pattern adapters
- `domino-format-gwt`: GWT/J2CL number/date pattern adapters

## Supported Styles

```java
DominoFormat.format("Hello {0}, you have %d items", "Ahmad", 3);
DominoFormat.format("Price: $D(0.00), ok=%b", 12.5, true);
JvmDominoFormat.format(
    "User {0} bought $N(000) items for $D(#,##0.00)",
    "Ahmad",
    3,
    1250.75);
```

## Usage

For plain templates that do not require platform pattern formatting, depend on `domino-format-core`.

For JVM applications, depend on `domino-format-jvm` and either:

```java
JvmDominoFormat.format("Price: $D(#,##0.00)", 1234.5);
```

or install JVM support once and keep using the shared API:

```java
DominoFormat.setDefaultFormattingSupport(JvmFormattingSupport.create());
DominoFormat.format("Price: $D(#,##0.00)", 1234.5);
```

## Argument Semantics

Indexed placeholders such as `{0}` reserve explicit argument slots. Sequential percent and
dollar-token placeholders then walk the argument array from left to right and skip any slot already
reserved by an indexed placeholder.

```java
DominoFormat.format("Hello {0}, you have %d items", "Ahmad", 3);
// Hello Ahmad, you have 3 items
```
