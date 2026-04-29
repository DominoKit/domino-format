package org.dominokit.format;

import org.gwtproject.i18n.client.NumberFormat;
import org.gwtproject.i18n.shared.DateTimeFormat;

/**
 * Provides GWT-native patterned number and date formatting support for Domino Format.
 *
 * <p>The support extends {@link FormattingSupport} directly so shared code can depend on the same
 * abstraction regardless of whether it runs in GWT or on the JVM. The default constructor installs
 * the GWT i18n delegates, while the argument constructor allows callers and tests to provide
 * alternate delegates without replacing the surrounding formatter pipeline.
 */
public class GwtFormattingSupport extends FormattingSupport {

  /**
   * Creates support backed by GWT number and date formatting delegates.
   *
   * <p>Numeric values are formatted through {@link NumberFormat}; date values are formatted through
   * {@link DateTimeFormat}.
   */
  public GwtFormattingSupport() {
    this(
        (pattern, value) -> NumberFormat.getFormat(pattern).format(value.doubleValue()),
        (pattern, value) -> DateTimeFormat.getFormat(pattern).format(value));
  }

  /**
   * Creates support backed by caller-provided formatting delegates.
   *
   * @param numberFormatter the delegate used for numeric patterns, or {@code null}
   * @param dateFormatter the delegate used for date patterns, or {@code null}
   */
  public GwtFormattingSupport(NumberFormatter numberFormatter, DateFormatter dateFormatter) {
    super(numberFormatter, dateFormatter);
  }
}
