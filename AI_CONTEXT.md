# Domino Format — AI Context

## Overview

**Domino Format** is a lightweight, cross-platform (JVM + GWT) string formatting library designed to support multiple formatting styles in a unified, extensible way.

The library avoids reliance on `java.util.Formatter` and other non-GWT-compatible APIs, while still providing powerful formatting capabilities for:

* User-facing messages
* Logging
* Structured text generation
* Lightweight templating

---

## Design Goals

* ✅ GWT compatible
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
DominoFormat.format("Hello {0}, you have {1} items", name, count);
```

**Syntax:**

```text
{0}, {1}, {2}...
```

---

### 2. Named Style

```java
DominoFormat.format(
    "Hello $(userName), you have %d items",
    DominoFormat.byName("userName", "Ahmad"),
    3);
```

**Syntax:**

```text
$(name)
$(first name)
```

**Named argument sources:**

* `DominoFormat.byName(name, value)`
* `Map<String, ?>`

**Missing-name behavior:**

* Default: replace with `""`
* Customizable through `MissingNamedArgumentHandler`

---

### 3. Percent Style (Subset)

```java
DominoFormat.format("Hello %s, count=%d", name, count);
```

**Supported tokens:**

| Token | Meaning     |
| ----- | ----------- |
| `%s`  | String      |
| `%d`  | Integer     |
| `%f`  | Decimal     |
| `%b`  | Boolean     |
| `%%`  | Literal `%` |
| `%n`  | System line separator |

---

### 4. Dollar Token Style (Primary Innovation)

```java
DominoFormat.format(
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
DominoFormat.format("Qty: $N(000)", 5);
// Qty: 005

DominoFormat.format("Price: $D(0.00)", 12.5);
// Price: 12.50

DominoFormat.format("Date: $T(yyyy-MM-dd)", date);
// Date: 2026-04-24
```

---

## Mixed Templates

All four styles can appear in the same template:

```java
DominoFormat.format(
    "Hello {0}, $(userName), count=%d, price=$D(0.00)",
    DominoFormat.byName("userName", name),
    name,
    count,
    price);
```

Argument handling rules:

* Named argument sources are removed from the positional argument stream.
* Indexed placeholders reserve explicit argument slots.
* Named placeholders resolve by expression and do not consume positional arguments.
* Percent and dollar-token placeholders consume arguments sequentially from left to right.
* Sequential placeholders skip any argument slot already reserved by an indexed placeholder.

---

## Grammar

```text
template       := part*
part           := text | placeholder

placeholder    := indexed | named | percent | token
indexed        := '{' digits '}'
named          := '$(' namedText ')'
percent        := '%' ('s' | 'd' | 'f' | 'b' | '%' | 'n')
token          := '$' tokenType pattern?
tokenType      := 'S' | 'L' | 'N' | 'D' | 'B' | 'T' | '$'
pattern        := '(' patternText ')'
```

---

## Internal Model

### Placeholder Resolution

```java
class ArgumentResolver {
    Object resolveIndexed(int index);
    Object resolveSequential(String tokenLabel);
    String resolveNamed(String expression);
}
```

---

## Architecture

```text
domino-format
└── domino-format-core
    ├── mixed formatter engine
    ├── argument resolver
    ├── shared DominoFormat facade
    ├── formatting support interfaces
    ├── java.text adapters
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
* Resolves placeholders during the scan
* Can support template caching later if needed

---

## Formatting Flow

```text
template → scan → resolve args → format values → build String
```

---

## Argument Handling

* Indexed placeholders reserve explicit argument slots
* Named placeholders resolve independently from positional consumption
* Sequential percent and dollar-token placeholders consume arguments from left to right
* Named argument sources are removed before indexed or sequential consumption begins
* Sequential placeholders skip slots already reserved by indexed placeholders
* Strict validation:

    * Missing args → exception
    * Invalid pattern → exception
    * Type mismatch → exception
    * Missing named args → handler fallback

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

* Conditional formatting
* Pluralization support
* Locale-aware formatting abstraction
* Compile-time template validation (annotation processor)

---

## Naming

Recommended API:

```java
DominoFormat.format(...)
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
DominoFormat.format(
    "User {0} / $(userName) bought %d items for $D(#,##0.00)",
    DominoFormat.byName("userName", "Ahmad"),
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
