package org.dominokit.format.jvm;

import org.dominokit.format.DominoFormat;
import org.dominokit.format.DominoFormatter;

/**
 * JVM convenience facade for Domino Format.
 *
 * <p>This class exposes a single mixed-style formatting API for JVM consumers without requiring
 * them to install {@link JvmFormattingSupport} manually before formatting patterned number or date
 * tokens.
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
   * Formats a template that may mix indexed, percent, and dollar-token placeholders.
   *
   * @param template the template to render
   * @param arguments the arguments consumed by the template
   * @return the formatted string
   */
  public static String format(String template, Object... arguments) {
    return FORMATTER.format(template, arguments);
  }
}
