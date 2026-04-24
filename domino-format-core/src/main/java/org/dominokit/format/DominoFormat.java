package org.dominokit.format;

import java.util.Objects;

/**
 * Static convenience API for Domino Format.
 *
 * <p>This class exposes a single {@link #format(String, Object...)} entry point that can resolve
 * indexed placeholders such as {@code {0}}, percent placeholders such as {@code %d}, and
 * dollar-token placeholders such as {@code $N(000)} in the same template. Applications can still
 * install platform-specific formatting support when they need numeric or date patterns.
 */
public final class DominoFormat {

  private static volatile FormattingSupport defaultFormattingSupport = FormattingSupport.none();

  private DominoFormat() {}

  /**
   * Installs the default formatting support used by the static formatting methods.
   *
   * @param formattingSupport the delegates used by {@link #format(String, Object...)}
   */
  public static void setDefaultFormattingSupport(FormattingSupport formattingSupport) {
    defaultFormattingSupport = Objects.requireNonNull(formattingSupport, "formattingSupport");
  }

  /**
   * Resets the static API back to support-free operation.
   *
   * <p>After reset, templates that require numeric or date patterns will fail until support is
   * installed again.
   */
  public static void resetDefaultFormattingSupport() {
    defaultFormattingSupport = FormattingSupport.none();
  }

  /**
   * Returns the formatting support currently used by the static API.
   *
   * @return the installed default support
   */
  public static FormattingSupport getDefaultFormattingSupport() {
    return defaultFormattingSupport;
  }

  /**
   * Creates an instance-based formatter backed by the supplied support object.
   *
   * @param formattingSupport the delegates used to resolve numeric and date patterns
   * @return a formatter bound to the provided support
   */
  public static DominoFormatter withFormattingSupport(FormattingSupport formattingSupport) {
    return new DominoFormatter(formattingSupport);
  }

  /**
   * Formats a template that may freely mix indexed, percent, and dollar-token placeholders.
   *
   * @param template the template to render
   * @param arguments the arguments consumed by the template
   * @return the formatted string
   */
  public static String format(String template, Object... arguments) {
    return new DominoFormatter(defaultFormattingSupport).format(template, arguments);
  }
}
