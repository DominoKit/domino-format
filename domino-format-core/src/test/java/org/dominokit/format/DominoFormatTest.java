package org.dominokit.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Verifies the shared, platform-neutral Domino Format behavior.
 *
 * <p>The tests focus on parsing rules, strict validation, and the delegation points used by the
 * token formatter.
 */
class DominoFormatTest {

  @AfterEach
  void resetDefaultSupport() {
    DominoFormat.resetDefaultFormattingSupport();
  }

  @Test
  void shouldFormatIndexedTemplates() {
    assertEquals(
        "Hello Ahmad, you have 3 items",
        DominoFormat.indexed("Hello {0}, you have {1} items", "Ahmad", 3));
  }

  @Test
  void shouldFormatPercentTemplates() {
    assertEquals(
        "Hello Ahmad, count=3, done=true%n%".replace("%n", "\n"),
        DominoFormat.percent("Hello %s, count=%d, done=%b%n%%", "Ahmad", 3, true));
  }

  @Test
  void shouldFormatTokenTemplatesWithInstalledSupport() {
    Date date = new Date(0L);
    DominoFormat.setDefaultFormattingSupport(
        FormattingSupport.create(
            (pattern, value) -> "number[" + pattern + "]=" + value,
            (pattern, value) -> "date[" + pattern + "]=" + value.getTime()));

    assertEquals(
        "Item: Chocolate, qty: number[000]=7, created: date[yyyy-MM-dd]=0",
        DominoFormat.tokens("Item: $S, qty: $N(000), created: $T(yyyy-MM-dd)", "Chocolate", 7, date));
  }

  @Test
  void shouldFailWhenPatternedNumberSupportIsMissing() {
    FormatException exception =
        assertThrows(FormatException.class, () -> DominoFormat.tokens("Qty: $N(000)", 7));

    assertEquals("No number formatter configured for pattern '000'", exception.getMessage());
  }

  @Test
  void shouldFailWhenTokenTypeDoesNotMatchArgument() {
    FormatException exception =
        assertThrows(FormatException.class, () -> DominoFormat.tokens("Value: $B", "true"));

    assertEquals(
        "Type mismatch for token $B: expected java.lang.Boolean but got java.lang.String",
        exception.getMessage());
  }

  @Test
  void shouldFailWhenTokenArgumentIsMissing() {
    FormatException exception =
        assertThrows(FormatException.class, () -> DominoFormat.tokens("Value: $S"));

    assertEquals("Missing argument for token $S", exception.getMessage());
  }

  @Test
  void shouldFailOnMalformedIndexedTemplate() {
    FormatException exception =
        assertThrows(FormatException.class, () -> DominoFormat.indexed("Hello {name}", "Ahmad"));

    assertEquals("Invalid indexed placeholder {name}", exception.getMessage());
  }

  @Test
  void shouldFailOnUnsupportedPercentToken() {
    FormatException exception =
        assertThrows(FormatException.class, () -> DominoFormat.percent("Value=%x", 10));

    assertEquals("Unsupported percent token %x", exception.getMessage());
  }
}
