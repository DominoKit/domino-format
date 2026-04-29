package org.dominokit.format;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import org.gwtproject.core.shared.GwtIncompatible;

/**
 * Provides JVM-native patterned number and date formatting support for Domino Format.
 *
 * <p>The class extends {@link GwtFormattingSupport} so the single shared {@link DominoFormat}
 * facade can instantiate one support type. JVM executions receive delegates backed by
 * {@link DecimalFormat} and {@link SimpleDateFormat}; GWT keeps a compatible inheritance path
 * through the base GWT support class.
 */
public class JvmFormattingSupport extends GwtFormattingSupport {

  @GwtIncompatible
  private static final NumberFormatter DEFAULT_NUMBER_FORMATTER =
      (pattern, value) -> new DecimalFormat(pattern).format(value);

  @GwtIncompatible
  private static final DateFormatter DEFAULT_DATE_FORMATTER =
      (pattern, value) -> new SimpleDateFormat(pattern).format(value);

  /**
   * Creates support backed by JVM number and date formatting delegates.
   *
   * <p>Fresh formatter instances are created for each call so usage remains thread-safe and
   * independent of caller state.
   */
  @GwtIncompatible
  public JvmFormattingSupport() {
    this(DEFAULT_NUMBER_FORMATTER, DEFAULT_DATE_FORMATTER);
  }

  /**
   * Creates support backed by caller-provided formatting delegates.
   *
   * @param numberFormatter the delegate used for numeric patterns, or {@code null}
   * @param dateFormatter the delegate used for date patterns, or {@code null}
   */
  @GwtIncompatible
  public JvmFormattingSupport(NumberFormatter numberFormatter, DateFormatter dateFormatter) {
    super(numberFormatter, dateFormatter);
  }
}
