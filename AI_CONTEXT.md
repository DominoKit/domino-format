# Domino Format — AI Context

## Overview

**Domino Format** is a lightweight, cross-platform (JVM + GWT/J2CL) string formatting library designed to support multiple formatting styles in a unified, extensible way.

The library avoids reliance on `java.util.Formatter` and other non-GWT-compatible APIs, while still providing powerful formatting capabilities for:

* User-facing messages
* Logging
* Structured text generation
* Lightweight templating

---

## Design Goals

* ✅ GWT / J2CL compatible
* ✅ No dependency on `java.util.Formatter`
* ✅ No OpenJDK code reuse (clean-room implementation)
* ✅ Multiple formatting styles supported
* ✅ Small, fast, allocation-conscious
* ✅ Extensible token system
* ✅ Shared parsing engine across platforms
* ✅ Platform-specific formatting delegation (GWT vs JVM)

---

## Supported Formatting Styles

### 1. Indexed Style

```java
DominoFormat.indexed("Hello {0}, you have {1} items", name, count);
```

**Syntax:**

```text
{0}, {1}, {2}...
```

---

### 2. Percent Style (Subset)

```java
DominoFormat.percent("Hello %s, count=%d", name, count);
```

**Supported tokens:**

| Token | Meaning     |
| ----- | ----------- |
| `%s`  | String      |
| `%d`  | Integer     |
| `%f`  | Decimal     |
| `%b`  | Boolean     |
| `%%`  | Literal `%` |
| `%n`  | Newline     |

---

### 3. Dollar Token Style (Primary Innovation)

```java
DominoFormat.tokens(
    "Item: $S, qty: $N(000), price: $D(#,##0.00)",
    "Chocolate",
    7,
    1234.5
);
```

**Output:**

```text
Item: Chocolate, qty: 007, price: 1,234.50
```

---

## Dollar Token Syntax

### Basic Tokens

| Token | Description                |
| ----- | -------------------------- |
| `$S`  | String                     |
| `$L`  | Literal (`String.valueOf`) |
| `$N`  | Number                     |
| `$D`  | Decimal / Double           |
| `$B`  | Boolean                    |
| `$T`  | Date/Time                  |
| `$$`  | Literal `$`                |

---

### Tokens with Patterns

```text
$N(000)
$D(0.00)
$D(#,##0.##)
$T(yyyy-MM-dd)
```

---

### Examples

```java
DominoFormat.tokens("Qty: $N(000)", 5);
// Qty: 005

DominoFormat.tokens("Price: $D(0.00)", 12.5);
// Price: 12.50

DominoFormat.tokens("Date: $T(yyyy-MM-dd)", date);
// Date: 2026-04-24
```

---

## Grammar

```text
template       := part*
part           := text | placeholder

placeholder    := '$' token pattern?
token          := 'S' | 'L' | 'N' | 'D' | 'B' | 'T' | '$'
pattern        := '(' patternText ')'
```

---

## Internal Model

### Token Representation

```java
interface Token {}

class TextToken implements Token {
    String text;
}

class ArgToken implements Token {
    char type;
    String pattern; // nullable
}
```

---

## Architecture

```text
domino-format
├── core
│   ├── parser
│   ├── tokens
│   ├── formatter engine
│   └── interfaces
│
├── style-indexed
├── style-percent
├── style-tokens
│
├── platform-jvm
│   └── java.text adapters
│
└── platform-gwt
    └── GWT i18n adapters
```

---

## Platform Abstraction

### Interfaces

```java
public interface NumberFormatter {
    String format(String pattern, Number value);
}

public interface DateFormatter {
    String format(String pattern, Date value);
}
```

---

### GWT Implementation

```java
NumberFormat.getFormat(pattern).format(value);
DateTimeFormat.getFormat(pattern).format(date);
```

---

### JVM Implementation

```java
new DecimalFormat(pattern).format(value);
new SimpleDateFormat(pattern).format(date);
```

---

## Parsing Strategy

* Single-pass parser
* No heavy regex dependency
* Produces token list
* Tokens reused for formatting (optional caching)

---

## Formatting Flow

```text
template → parse → tokens → iterate → resolve args → build String
```

---

## Argument Handling

* Sequential argument consumption
* Future extension: indexed arguments for tokens
* Strict validation:

    * Missing args → exception
    * Invalid pattern → exception
    * Type mismatch → exception

---

## Error Handling

Custom exception:

```java
FormatException extends RuntimeException
```

Examples:

```text
Missing argument for token $N
Invalid pattern for $D
Unexpected end of template
```

---

## Performance Considerations

* Avoid regex where possible
* Use `StringBuilder`
* Optional template caching
* Avoid boxing where possible (future optimization)

---

## Future Extensions

* Named arguments:

```java
"Hello $S{name}"
```

* Conditional formatting
* Pluralization support
* Locale-aware formatting abstraction
* Compile-time template validation (annotation processor)

---

## Naming

Recommended API:

```java
DominoFormat.indexed(...)
DominoFormat.percent(...)
DominoFormat.tokens(...)
```

Alternative:

```java
TextFormat.*
Strings.*
Format.*
```

---

## Non-Goals

* ❌ Full `String.format` compatibility
* ❌ Full i18n message system (like ICU)
* ❌ Heavy templating engine
* ❌ Reflection-based formatting

---

## Positioning

This library is:

> A lightweight, GWT-safe, multi-style formatting engine with a modern token-based syntax.

---

## Key Differentiator

The **$-token formatting system**:

* Simple
* Extensible
* Cross-platform
* Pattern-enabled
* Not tied to JavaPoet or Java Formatter

---

## Example Summary

```java
DominoFormat.tokens(
    "User $S bought $N(000) items for $D(#,##0.00)",
    "Ahmad",
    3,
    1250.75
);
```

```text
User Ahmad bought 003 items for 1,250.75
```

---

## End of Context
