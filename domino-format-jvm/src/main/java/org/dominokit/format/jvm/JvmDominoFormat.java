package org.dominokit.format.jvm;

import org.dominokit.format.DominoFormat;
import org.dominokit.format.DominoFormatter;

/**
 * JVM convenience facade for Domino Format.
 *
 * <p>This class exposes the concise static API expected by JVM consumers without requiring them to
 * install {@link JvmFormattingSupport} manually before formatting patterned number or date tokens.
 */
public final class JvmDominoFormat {

  private static final DominoFormatter FORMATTER =
      DominoFormat.withFormattingSupport(JvmFormattingSupport.create());

  private JvmDominoFormat() {}

  /**
   * Installs JVM formatting support into the shared static API.
   */
  public static void installAsDefault() {
    DominoFormat.setDefaultFormattingSupport(JvmFormattingSupport.create());
  }

  /**
   * Returns the reusable JVM-backed formatter instance.
   *
   * @return the JVM-backed formatter
   */
  public static DominoFormatter formatter() {
    return FORMATTER;
  }

  /**
   * Formats an indexed template using JVM-backed support.
   *
   * @param template the template to render
   * @param arguments the indexed arguments consumed by the template
   * @return the formatted string
   */
  public static String indexed(String template, Object... arguments) {
    return FORMATTER.indexed(template, arguments);
  }

  /**
   * Formats a percent template using JVM-backed support.
   *
   * @param template the template to render
   * @param arguments the sequential arguments consumed by the template
   * @return the formatted string
   */
  public static String percent(String template, Object... arguments) {
    return FORMATTER.percent(template, arguments);
  }

  /**
   * Formats a token template using JVM numeric and date delegates.
   *
   * @param template the template to render
   * @param arguments the sequential arguments consumed by the template
   * @return the formatted string
   */
  public static String tokens(String template, Object... arguments) {
    return FORMATTER.tokens(template, arguments);
  }
}
