package org.dominokit.format.jvm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import org.dominokit.format.DominoFormat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Verifies JVM-specific numeric and date pattern behavior.
 *
 * <p>The tests ensure the standalone JVM adapter produces the output expected by the initial
 * library specification.
 */
class JvmDominoFormatTest {

  @AfterEach
  void resetDefaultSupport() {
    DominoFormat.resetDefaultFormattingSupport();
  }

  @Test
  void shouldFormatPatternedNumbersAndDates() {
    Date date = new Date(1776988800000L);

    assertEquals(
        "User Ahmad bought 003 items for 1,250.75 on 2026-04-24",
        JvmDominoFormat.format(
            "User {0} bought $N(000) items for $D(#,##0.00) on $T(yyyy-MM-dd)",
            "Ahmad",
            3,
            1250.75,
            date));
  }

  @Test
  void shouldInstallJvmSupportForSharedStaticApi() {
    JvmDominoFormat.installAsDefault();

    assertEquals("Price: 1,234.50", DominoFormat.format("Price: $D(#,##0.00)", 1234.5));
  }
}
