package org.dominokit.format;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Verifies JVM numeric and date pattern behavior exposed by the shared static facade.
 *
 * <p>The tests ensure the single core {@link DominoFormat} entry point is initialized with JVM
 * formatting support by default while still allowing callers to replace the shared support and
 * missing named argument handler.
 */
class JvmDominoFormatTest {

  @AfterEach
  void resetDefaultSupport() {
    DominoFormat.resetDefaultFormattingSupport();
    DominoFormat.resetDefaultMissingNamedArgumentHandler();
  }

  @Test
  void shouldFormatPatternedNumbersAndDates() {
    Date date = new Date(1776988800000L);

    assertEquals(
        "User Ahmad bought 003 items for 1,250.75 on 2026-04-24",
        DominoFormat.format(
            "User {0} bought $N(000) items for $D(#,##0.00) on $T(yyyy-MM-dd)",
            "Ahmad",
            3,
            1250.75,
            date));
  }

  @Test
  void shouldExposeJvmPatternSupportThroughTheSharedStaticApiByDefault() {
    assertEquals("Price: 1,234.50", DominoFormat.format("Price: $D(#,##0.00)", 1234.5));
  }

  @Test
  void shouldReplaceSharedStaticFormatterWhenCustomSupportIsInstalled() {
    DominoFormat.setFormattingSupport(
        FormattingSupport.create(
            (pattern, value) -> "number[" + pattern + "]=" + value,
            (pattern, value) -> "date[" + pattern + "]=" + value.getTime()));

    assertEquals("Price: number[0.00]=12.5", DominoFormat.format("Price: $D(0.00)", 12.5));
  }

  @Test
  void shouldFormatNamedArgumentsThroughTheSharedStaticApi() {
    assertEquals(
        "Hello Ahmad",
        DominoFormat.format("Hello $(userName)", Map.of("userName", "Ahmad")));
  }

  @Test
  void shouldFormatNamedArgumentsThroughTheSharedStaticByNameHelper() {
    assertEquals(
        "Hello Ahmad",
        DominoFormat.format("Hello $(userName)", DominoFormat.byName("userName", "Ahmad")));
  }

  @Test
  void shouldUseTheConfiguredMissingNamedArgumentHandlerThroughTheSharedStaticApi() {
    DominoFormat.setMissingNamedArgumentHandler(expression -> "<missing:" + expression + ">");

    assertEquals("Hello <missing:userName>", DominoFormat.format("Hello $(userName)"));
  }

  @Test
  void shouldResetTheSharedStaticMissingNamedArgumentHandlerToDefaultBehavior() {
    DominoFormat.setMissingNamedArgumentHandler(expression -> "<missing:" + expression + ">");
    DominoFormat.resetDefaultMissingNamedArgumentHandler();

    assertEquals("Hello ", DominoFormat.format("Hello $(userName)"));
  }
}
