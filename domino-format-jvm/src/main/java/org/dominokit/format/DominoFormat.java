package org.dominokit.format;

import java.util.Objects;

/**
 * JVM convenience facade for Domino Format.
 *
 * <p>This facade provides the shared static entry point used by JVM applications. It is initialized
 * with {@link JvmFormattingSupport} by default, so patterned number and date tokens work without
 * any manual setup.
 */
public final class DominoFormat {

  private static DominoFormatter FORMATTER = new DominoFormatter(JvmFormattingSupport.create());

  private DominoFormat() {}


  /**
   * Replaces the shared static formatter support used by {@link #format(String, Object...)}.
   *
   * <p>This method updates both the support object and the reusable formatter returned by
   * {@link #formatter()}.
   *
   * @param formattingSupport the delegates used by {@link #format(String, Object...)}
   */
  public static void setFormattingSupport(FormattingSupport formattingSupport) {
    FORMATTER = new DominoFormatter(Objects.requireNonNull(formattingSupport, "formattingSupport"));
  }

  /**
   * Restores the shared static formatter to the default JVM formatting support.
   *
   * <p>After reset, patterned number and date tokens are again delegated to
   * {@link JvmFormattingSupport}.
   */
  public static void resetDefaultFormattingSupport() {
    withFormattingSupport(JvmFormattingSupport.create());
  }

  /**
   * Returns the formatting support currently used by the static API.
   *
   * @return the installed default support
   */
  public static FormattingSupport getFormattingSupport() {
    return FORMATTER.getFormattingSupport();
  }

  /**
   * Replaces the shared static formatter with one backed by the supplied support object and returns
   * it.
   *
   * <p>If you need an isolated formatter instance that does not mutate global static state, create
   * a new {@link DominoFormatter} directly.
   *
   * @param formattingSupport the delegates used to resolve numeric and date patterns
   * @return the shared static formatter after installing the supplied support
   */
  public static DominoFormatter withFormattingSupport(FormattingSupport formattingSupport) {
    setFormattingSupport(formattingSupport);
    return FORMATTER;
  }

  /**
   * Returns the current shared static formatter instance.
   *
   * @return the current shared formatter
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
