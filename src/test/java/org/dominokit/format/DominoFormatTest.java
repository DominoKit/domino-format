/*
 * Copyright © 2026 Dominokit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dominokit.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Verifies the shared, platform-neutral Domino Format behavior.
 *
 * <p>The tests focus on mixed placeholder parsing, strict validation, and the delegation points
 * used by patterned number and date formatting.
 */
class DominoFormatTest {

  @AfterEach
  void resetDefaultSupport() {
    DominoFormat.resetDefaultFormattingSupport();
    DominoFormat.resetDefaultMissingNamedArgumentHandler();
  }

  @Test
  void shouldFormatIndexedTemplates() {
    assertEquals(
        "Hello Ahmad, you have 3 items",
        DominoFormat.format("Hello {0}, you have {1} items", "Ahmad", 3));
  }

  @Test
  void shouldFormatPercentTemplates() {
    assertEquals(
        "Hello Ahmad, count=3, done=true\n%",
        DominoFormat.format("Hello %s, count=%d, done=%b%n%%", "Ahmad", 3, true));
  }

  @Test
  void shouldFormatNamedTemplatesUsingExplicitNamedArguments() {
    assertEquals(
        "Hello Ahmad, you have 3 items",
        DominoFormat.format(
            "Hello $(userName), you have %d items", DominoFormat.byName("userName", "Ahmad"), 3));
  }

  @Test
  void shouldFormatNamedTemplatesUsingNamedArgumentMaps() {
    assertEquals(
        "Hello Ahmad, you have 3 items",
        DominoFormat.format(
            "Hello $(userName), you have %d items", Map.of("userName", "Ahmad"), 3));
  }

  @Test
  void shouldTrimNamedPlaceholderExpressionsAndNamedArgumentNames() {
    assertEquals(
        "Hello Ahmad",
        DominoFormat.format("Hello $( userName )", DominoFormat.byName(" userName ", "Ahmad")));
  }

  @Test
  void shouldFormatMixedTemplatesWithIndexedPercentAndTokenPlaceholders() {
    DominoFormat.setFormattingSupport(
        FormattingSupport.create(
            (pattern, value) -> "number[" + pattern + "]=" + value,
            (pattern, value) -> "date[" + pattern + "]=" + value.getTime()));

    assertEquals(
        "User Ahmad bought 3 items for number[0.00]=12.5",
        DominoFormat.format("User {0} bought %d items for $D(0.00)", "Ahmad", 3, 12.5));
  }

  @Test
  void shouldSkipArgumentsReservedByIndexedPlaceholdersDuringSequentialConsumption() {
    assertEquals(
        "alpha beta gamma delta",
        DominoFormat.format("{0} $S {2} %s", "alpha", "beta", "gamma", "delta"));
  }

  @Test
  void shouldExcludeNamedArgumentSourcesFromPositionalResolution() {
    assertEquals(
        "one zero Ahmad",
        DominoFormat.format(
            "{1} %s $(userName)", DominoFormat.byName("userName", "Ahmad"), "zero", "one"));
  }

  @Test
  void shouldExcludeNamedArgumentMapsFromPositionalResolution() {
    assertEquals(
        "one zero Ahmad",
        DominoFormat.format("{1} %s $(userName)", Map.of("userName", "Ahmad"), "zero", "one"));
  }

  @Test
  void shouldFormatTokenTemplatesWithInstalledSupport() {
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

  @Test
  void shouldReplaceMissingNamedArgumentsWithEmptyStringByDefault() {
    assertEquals("Hello ", DominoFormat.format("Hello $(missingUser)"));
  }

  @Test
  void shouldUseCustomMissingNamedArgumentHandler() {
    DominoFormat.setMissingNamedArgumentHandler(expression -> "<missing:" + expression + ">");

    assertEquals("Hello <missing:userName>", DominoFormat.format("Hello $(userName)"));
  }

  @Test
  void shouldResetMissingNamedArgumentHandlerToDefaultEmptyBehavior() {
    DominoFormat.setMissingNamedArgumentHandler(expression -> "<missing:" + expression + ">");
    DominoFormat.resetDefaultMissingNamedArgumentHandler();

    assertEquals("Hello ", DominoFormat.format("Hello $(userName)"));
  }

  @Test
  void shouldPreferLaterNamedArgumentsWhenDuplicateNamesAreSupplied() {
    assertEquals(
        "Hello Dana",
        DominoFormat.format(
            "Hello $(userName)",
            DominoFormat.byName("userName", "Ahmad"),
            DominoFormat.byName("userName", "Dana")));
  }

  @Test
  void shouldAllowMapAndExplicitNamedArgumentsToBeCombined() {
    assertEquals(
        "Hello Dana",
        DominoFormat.format(
            "Hello $(userName)",
            Map.of("userName", "Ahmad"),
            DominoFormat.byName("userName", "Dana")));
  }

  @Test
  void shouldFailWhenPatternedNumberSupportIsMissing() {
    DominoFormat.setFormattingSupport(FormattingSupport.none());

    FormatException exception =
        assertThrows(FormatException.class, () -> DominoFormat.format("Qty: $N(000)", 7));

    assertEquals("No number formatter configured for pattern '000'", exception.getMessage());
  }

  @Test
  void shouldFailWhenTokenTypeDoesNotMatchArgument() {
    FormatException exception =
        assertThrows(FormatException.class, () -> DominoFormat.format("Value: $B", "true"));

    assertEquals(
        "Type mismatch for token $B: expected java.lang.Boolean but got java.lang.String",
        exception.getMessage());
  }

  @Test
  void shouldFailWhenTokenArgumentIsMissing() {
    FormatException exception =
        assertThrows(FormatException.class, () -> DominoFormat.format("Value: $S"));

    assertEquals("Missing argument for token $S", exception.getMessage());
  }

  @Test
  void shouldFailOnMalformedIndexedTemplate() {
    FormatException exception =
        assertThrows(FormatException.class, () -> DominoFormat.format("Hello {name}", "Ahmad"));

    assertEquals("Invalid indexed placeholder {name}", exception.getMessage());
  }

  @Test
  void shouldFailOnUnsupportedPercentToken() {
    FormatException exception =
        assertThrows(FormatException.class, () -> DominoFormat.format("Value=%x", 10));

    assertEquals("Unsupported percent token %x", exception.getMessage());
  }

  @Test
  void shouldFailOnEmptyNamedPlaceholder() {
    FormatException exception =
        assertThrows(FormatException.class, () -> DominoFormat.format("Hello $()"));

    assertEquals("Empty named placeholder is not allowed", exception.getMessage());
  }

  @Test
  void shouldFailWhenNamedArgumentMapContainsBlankKeys() {
    FormatException exception =
        assertThrows(
            FormatException.class,
            () -> DominoFormat.format("Hello $(userName)", Map.of(" ", "Ahmad")));

    assertEquals("Named argument map keys must not be blank", exception.getMessage());
  }

  @Test
  void shouldFailWhenNamedArgumentNameIsBlank() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> DominoFormat.byName(" ", "Ahmad"));

    assertEquals("Named argument name must not be blank", exception.getMessage());
  }
}
