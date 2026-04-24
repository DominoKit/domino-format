package org.dominokit.format;

/**
 * Formats numeric values using a platform-specific pattern engine.
 *
 * <p>The shared Domino Format parser never interprets numeric patterns itself. Instead it forwards
 * the raw pattern string to an implementation of this contract so JVM, GWT, and future runtimes
 * can each delegate to their native formatting facilities.
 */
@FunctionalInterface
public interface NumberFormatter {

  /**
   * Formats a numeric value using the provided pattern.
   *
   * @param pattern the runtime-specific numeric pattern
   * @param value the numeric value to format
   * @return the formatted representation
   */
  String format(String pattern, Number value);
}
