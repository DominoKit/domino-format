package org.dominokit.format;

import com.google.gwt.junit.client.GWTTestCase;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Runs the shared Domino Format behavior through the GWT test runner.
 *
 * <p>The test methods intentionally use the GWT/JUnit3 naming convention, where each executable
 * test is a public method whose name starts with {@code test}. The assertions mirror the core JVM
 * tests so the shared formatter contract is verified after GWT compilation as well as on the JVM.
 */
public class DominoFormatGwtTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.dominokit.format.DominoFormat";
  }

  @Override
  protected void gwtTearDown() throws Exception {
    try {
      DominoFormat.resetDefaultFormattingSupport();
      DominoFormat.resetDefaultMissingNamedArgumentHandler();
    } finally {
      super.gwtTearDown();
    }
  }

  public void testShouldFormatIndexedTemplates() {
    assertEquals(
        "Hello Ahmad, you have 3 items",
        DominoFormat.format("Hello {0}, you have {1} items", "Ahmad", 3));
  }

  public void testShouldFormatPercentTemplates() {
    assertEquals(
        "Hello Ahmad, count=3, done=true\n%",
        DominoFormat.format("Hello %s, count=%d, done=%b%n%%", "Ahmad", 3, true));
  }

  public void testShouldFormatNamedTemplatesUsingExplicitNamedArguments() {
    assertEquals(
        "Hello Ahmad, you have 3 items",
        DominoFormat.format(
            "Hello $(userName), you have %d items",
            DominoFormat.byName("userName", "Ahmad"),
            3));
  }

  public void testShouldFormatNamedTemplatesUsingNamedArgumentMaps() {
    assertEquals(
        "Hello Ahmad, you have 3 items",
        DominoFormat.format(
            "Hello $(userName), you have %d items",
            namedArguments("userName", "Ahmad"),
            3));
  }

  public void testShouldTrimNamedPlaceholderExpressionsAndNamedArgumentNames() {
    assertEquals(
        "Hello Ahmad",
        DominoFormat.format("Hello $( userName )", DominoFormat.byName(" userName ", "Ahmad")));
  }

  public void testShouldFormatMixedTemplatesWithIndexedPercentAndTokenPlaceholders() {
    DominoFormat.setFormattingSupport(
        FormattingSupport.create(
            (pattern, value) -> "number[" + pattern + "]=" + value,
            (pattern, value) -> "date[" + pattern + "]=" + value.getTime()));

    assertEquals(
        "User Ahmad bought 3 items for number[0.00]=12.5",
        DominoFormat.format("User {0} bought %d items for $D(0.00)", "Ahmad", 3, 12.5));
  }

  public void testShouldSkipArgumentsReservedByIndexedPlaceholdersDuringSequentialConsumption() {
    assertEquals(
        "alpha beta gamma delta",
        DominoFormat.format("{0} $S {2} %s", "alpha", "beta", "gamma", "delta"));
  }

  public void testShouldExcludeNamedArgumentSourcesFromPositionalResolution() {
    assertEquals(
        "one zero Ahmad",
        DominoFormat.format(
            "{1} %s $(userName)",
            DominoFormat.byName("userName", "Ahmad"),
            "zero",
            "one"));
  }

  public void testShouldExcludeNamedArgumentMapsFromPositionalResolution() {
    assertEquals(
        "one zero Ahmad",
        DominoFormat.format(
            "{1} %s $(userName)", namedArguments("userName", "Ahmad"), "zero", "one"));
  }

  public void testShouldFormatTokenTemplatesWithInstalledSupport() {
    Date date = new Date(0L);
    DominoFormat.setFormattingSupport(
        FormattingSupport.create(
            (pattern, value) -> "number[" + pattern + "]=" + value,
            (pattern, value) -> "date[" + pattern + "]=" + value.getTime()));

    assertEquals(
        "Item: Chocolate, qty: number[000]=7, created: date[yyyy-MM-dd]=0",
        DominoFormat.format(
            "Item: $S, qty: $N(000), created: $T(yyyy-MM-dd)", "Chocolate", 7, date));
  }

  public void testShouldReplaceMissingNamedArgumentsWithEmptyStringByDefault() {
    assertEquals("Hello ", DominoFormat.format("Hello $(missingUser)"));
  }

  public void testShouldUseCustomMissingNamedArgumentHandler() {
    DominoFormat.setMissingNamedArgumentHandler(expression -> "<missing:" + expression + ">");

    assertEquals("Hello <missing:userName>", DominoFormat.format("Hello $(userName)"));
  }

  public void testShouldResetMissingNamedArgumentHandlerToDefaultEmptyBehavior() {
    DominoFormat.setMissingNamedArgumentHandler(expression -> "<missing:" + expression + ">");
    DominoFormat.resetDefaultMissingNamedArgumentHandler();

    assertEquals("Hello ", DominoFormat.format("Hello $(userName)"));
  }

  public void testShouldPreferLaterNamedArgumentsWhenDuplicateNamesAreSupplied() {
    assertEquals(
        "Hello Dana",
        DominoFormat.format(
            "Hello $(userName)",
            DominoFormat.byName("userName", "Ahmad"),
            DominoFormat.byName("userName", "Dana")));
  }

  public void testShouldAllowMapAndExplicitNamedArgumentsToBeCombined() {
    assertEquals(
        "Hello Dana",
        DominoFormat.format(
            "Hello $(userName)",
            namedArguments("userName", "Ahmad"),
            DominoFormat.byName("userName", "Dana")));
  }

  public void testShouldFailWhenPatternedNumberSupportIsMissing() {
    DominoFormat.setFormattingSupport(FormattingSupport.none());

    FormatException exception =
        assertFormatException(() -> DominoFormat.format("Qty: $N(000)", 7));

    assertEquals("No number formatter configured for pattern '000'", exception.getMessage());
  }

  public void testShouldFailWhenTokenTypeDoesNotMatchArgument() {
    FormatException exception =
        assertFormatException(() -> DominoFormat.format("Value: $B", "true"));

    assertEquals(
        "Type mismatch for token $B: expected java.lang.Boolean but got java.lang.String",
        exception.getMessage());
  }

  public void testShouldFailWhenTokenArgumentIsMissing() {
    FormatException exception = assertFormatException(() -> DominoFormat.format("Value: $S"));

    assertEquals("Missing argument for token $S", exception.getMessage());
  }

  public void testShouldFailOnMalformedIndexedTemplate() {
    FormatException exception =
        assertFormatException(() -> DominoFormat.format("Hello {name}", "Ahmad"));

    assertEquals("Invalid indexed placeholder {name}", exception.getMessage());
  }

  public void testShouldFailOnUnsupportedPercentToken() {
    FormatException exception =
        assertFormatException(() -> DominoFormat.format("Value=%x", 10));

    assertEquals("Unsupported percent token %x", exception.getMessage());
  }

  public void testShouldFailOnEmptyNamedPlaceholder() {
    FormatException exception = assertFormatException(() -> DominoFormat.format("Hello $()"));

    assertEquals("Empty named placeholder is not allowed", exception.getMessage());
  }

  public void testShouldFailWhenNamedArgumentMapContainsBlankKeys() {
    Map<String, Object> arguments = namedArguments(" ", "Ahmad");

    FormatException exception =
        assertFormatException(() -> DominoFormat.format("Hello $(userName)", arguments));

    assertEquals("Named argument map keys must not be blank", exception.getMessage());
  }

  public void testShouldFailWhenNamedArgumentNameIsBlank() {
    IllegalArgumentException exception =
        assertIllegalArgumentException(() -> DominoFormat.byName(" ", "Ahmad"));

    assertEquals("Named argument name must not be blank", exception.getMessage());
  }

  public void testShouldFormatPatternedNumbersAndDates() {
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

  public void testShouldExposePatternSupportThroughTheSharedStaticApiByDefault() {
    assertEquals("Price: 1,234.50", DominoFormat.format("Price: $D(#,##0.00)", 1234.5));
  }

  public void testShouldReplaceSharedStaticFormatterWhenCustomSupportIsInstalled() {
    DominoFormat.setFormattingSupport(
        FormattingSupport.create(
            (pattern, value) -> "number[" + pattern + "]=" + value,
            (pattern, value) -> "date[" + pattern + "]=" + value.getTime()));

    assertEquals("Price: number[0.00]=12.5", DominoFormat.format("Price: $D(0.00)", 12.5));
  }

  public void testShouldFormatNamedArgumentsThroughTheSharedStaticApi() {
    assertEquals(
        "Hello Ahmad",
        DominoFormat.format("Hello $(userName)", namedArguments("userName", "Ahmad")));
  }

  public void testShouldFormatNamedArgumentsThroughTheSharedStaticByNameHelper() {
    assertEquals(
        "Hello Ahmad",
        DominoFormat.format("Hello $(userName)", DominoFormat.byName("userName", "Ahmad")));
  }

  public void testShouldUseTheConfiguredMissingNamedArgumentHandlerThroughTheSharedStaticApi() {
    DominoFormat.setMissingNamedArgumentHandler(expression -> "<missing:" + expression + ">");

    assertEquals("Hello <missing:userName>", DominoFormat.format("Hello $(userName)"));
  }

  public void testShouldResetTheSharedStaticMissingNamedArgumentHandlerToDefaultBehavior() {
    DominoFormat.setMissingNamedArgumentHandler(expression -> "<missing:" + expression + ">");
    DominoFormat.resetDefaultMissingNamedArgumentHandler();

    assertEquals("Hello ", DominoFormat.format("Hello $(userName)"));
  }

  /**
   * Creates a mutable named argument map that is compatible with the GWT runtime.
   *
   * @param name the named placeholder expression
   * @param value the value used for the placeholder expression
   * @return a map containing the supplied named argument
   */
  private static Map<String, Object> namedArguments(String name, Object value) {
    Map<String, Object> arguments = new HashMap<>();
    arguments.put(name, value);
    return arguments;
  }

  /**
   * Verifies that a command fails with a {@link FormatException}.
   *
   * @param command the command to execute
   * @return the thrown format exception
   */
  private static FormatException assertFormatException(ThrowingCommand command) {
    try {
      command.execute();
    } catch (FormatException exception) {
      return exception;
    } catch (Throwable throwable) {
      fail("Expected FormatException but got " + throwable.getClass().getName());
    }
    fail("Expected FormatException to be thrown");
    return null;
  }

  /**
   * Verifies that a command fails with an {@link IllegalArgumentException}.
   *
   * @param command the command to execute
   * @return the thrown illegal argument exception
   */
  private static IllegalArgumentException assertIllegalArgumentException(ThrowingCommand command) {
    try {
      command.execute();
    } catch (IllegalArgumentException exception) {
      return exception;
    } catch (Throwable throwable) {
      fail("Expected IllegalArgumentException but got " + throwable.getClass().getName());
    }
    fail("Expected IllegalArgumentException to be thrown");
    return null;
  }

  /**
   * Functional command used by exception assertion helpers.
   */
  private interface ThrowingCommand {

    /**
     * Executes code that is expected to throw.
     */
    void execute();
  }
}
