package org.dominokit.format;

import java.util.Date;
import java.util.Objects;

/**
 * Bundles the platform-specific collaborators required to resolve patterned number and date tokens.
 *
 * <p>The core formatter can always process string, literal, boolean, and unpatterned values. When
 * a template includes patterns such as {@code $N(000)} or {@code $T(yyyy-MM-dd)}, the formatter
 * delegates to this object instead of embedding platform-specific logic in the parser.
 */
public class FormattingSupport {

  private static final FormattingSupport NONE = new FormattingSupport(null, null);

  private final NumberFormatter numberFormatter;
  private final DateFormatter dateFormatter;

  /**
   * Creates a support object backed by optional number and date delegates.
   *
   * <p>Subclasses use this constructor to expose runtime-specific defaults while preserving the
   * shared null-handling and exception wrapping behavior implemented by this class.
   *
   * @param numberFormatter the delegate used for numeric patterns, or {@code null}
   * @param dateFormatter the delegate used for date patterns, or {@code null}
   */
  protected FormattingSupport(NumberFormatter numberFormatter, DateFormatter dateFormatter) {
    this.numberFormatter = numberFormatter;
    this.dateFormatter = dateFormatter;
  }

  /**
   * Returns a support object that disables patterned number and date formatting.
   *
   * <p>Templates that request numeric or date patterns while this support object is active will
   * fail with a {@link FormatException}.
   *
   * @return a singleton support object with no platform delegates
   */
  public static FormattingSupport none() {
    return NONE;
  }

  /**
   * Creates a support object backed by the provided platform delegates.
   *
   * <p>Either formatter may be {@code null}. A {@code null} formatter simply means that patterned
   * formatting for that value family is unavailable.
   *
   * @param numberFormatter the delegate used for numeric patterns, or {@code null}
   * @param dateFormatter the delegate used for date patterns, or {@code null}
   * @return a support object with the supplied delegates
   */
  public static FormattingSupport create(
      NumberFormatter numberFormatter, DateFormatter dateFormatter) {
    if (numberFormatter == null && dateFormatter == null) {
      return NONE;
    }
    return new FormattingSupport(numberFormatter, dateFormatter);
  }

  /**
   * Indicates whether numeric patterns can be resolved by this support object.
   *
   * @return {@code true} when a numeric delegate is available
   */
  public boolean supportsNumberPatterns() {
    return numberFormatter != null;
  }

  /**
   * Indicates whether date patterns can be resolved by this support object.
   *
   * @return {@code true} when a date delegate is available
   */
  public boolean supportsDatePatterns() {
    return dateFormatter != null;
  }

  /**
   * Formats a number with the configured numeric delegate.
   *
   * @param pattern the runtime-specific numeric pattern
   * @param value the numeric value to format
   * @return the formatted number
   * @throws FormatException if numeric pattern support is unavailable or the delegate rejects the
   *     pattern
   */
  public String formatNumber(String pattern, Number value) {
    Objects.requireNonNull(pattern, "pattern");
    Objects.requireNonNull(value, "value");
    if (numberFormatter == null) {
      throw new FormatException("No number formatter configured for pattern '" + pattern + "'");
    }
    try {
      return numberFormatter.format(pattern, value);
    } catch (RuntimeException e) {
      throw new FormatException("Invalid numeric pattern '" + pattern + "'", e);
    }
  }

  /**
   * Formats a date with the configured date delegate.
   *
   * @param pattern the runtime-specific date pattern
   * @param value the date to format
   * @return the formatted date
   * @throws FormatException if date pattern support is unavailable or the delegate rejects the
   *     pattern
   */
  public String formatDate(String pattern, Date value) {
    Objects.requireNonNull(pattern, "pattern");
    Objects.requireNonNull(value, "value");
    if (dateFormatter == null) {
      throw new FormatException("No date formatter configured for pattern '" + pattern + "'");
    }
    try {
      return dateFormatter.format(pattern, value);
    } catch (RuntimeException e) {
      throw new FormatException("Invalid date pattern '" + pattern + "'", e);
    }
  }
}
