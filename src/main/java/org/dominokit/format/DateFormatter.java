package org.dominokit.format;

import java.util.Date;

/**
 * Formats {@link Date} instances using a platform-specific date/time pattern engine.
 *
 * <p>The core library keeps date token parsing completely shared across platforms, while concrete
 * implementations bridge to runtime-specific date formatting APIs.
 */
@FunctionalInterface
public interface DateFormatter {

  /**
   * Formats a date value using the provided pattern.
   *
   * @param pattern the runtime-specific date pattern
   * @param value the date to format
   * @return the formatted representation
   */
  String format(String pattern, Date value);
}
