# Domino Format

Domino Format is a standalone multi-style string formatting library for DominoKit projects.

The initial implementation is split into three modules:

- `domino-format-core`: shared parsing and formatting engine
- `domino-format-jvm`: JVM number/date pattern adapters
- `domino-format-gwt`: GWT/J2CL number/date pattern adapters

## Supported Styles

```java
DominoFormat.indexed("Hello {0}, you have {1} items", "Ahmad", 3);
DominoFormat.percent("Hello %s, count=%d", "Ahmad", 3);
JvmDominoFormat.tokens("User $S bought $N(000) items", "Ahmad", 3);
```

## Usage

For plain templates that do not require platform pattern formatting, depend on `domino-format-core`.

For JVM applications, depend on `domino-format-jvm` and either:

```java
JvmDominoFormat.tokens("Price: $D(#,##0.00)", 1234.5);
```

or install JVM support once and keep using the shared API:

```java
DominoFormat.setDefaultFormattingSupport(JvmFormattingSupport.create());
DominoFormat.tokens("Price: $D(#,##0.00)", 1234.5);
```
