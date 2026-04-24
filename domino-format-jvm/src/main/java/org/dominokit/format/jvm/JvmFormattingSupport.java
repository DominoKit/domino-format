package org.dominokit.format.jvm;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import org.dominokit.format.FormattingSupport;

/**
 * Provides JVM-native patterned number and date formatting support for Domino Format.
 *
 * <p>The implementation creates fresh formatter instances for each call so usage remains
 * thread-safe and independent of caller state.
 */
public final class JvmFormattingSupport {

  private static final FormattingSupport SUPPORT =
      FormattingSupport.create(
          (pattern, value) -> new DecimalFormat(pattern).format(value),
          (pattern, value) -> new SimpleDateFormat(pattern).format(value));

  private JvmFormattingSupport() {}

  /**
   * Returns a reusable JVM formatting support instance.
   *
   * @return the JVM support object
   */
  public static FormattingSupport create() {
    return SUPPORT;
  }
}
