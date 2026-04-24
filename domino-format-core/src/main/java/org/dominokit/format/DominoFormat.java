package org.dominokit.format;

import java.util.Objects;

/**
 * Static convenience API for Domino Format.
 *
 * <p>This class exposes the concise entry points described in the library design while still
 * allowing applications to install platform-specific formatting support when they need numeric or
 * date patterns.
 */
public final class DominoFormat {

  private static volatile FormattingSupport defaultFormattingSupport = FormattingSupport.none();

  private DominoFormat() {}

  /**
   * Installs the default formatting support used by the static formatting methods.
   *
   * @param formattingSupport the delegates used by {@link #tokens(String, Object...)}
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
   * Formats an indexed template using the shared static API.
   *
   * @param template the template to render
   * @param arguments the indexed arguments consumed by the template
   * @return the formatted string
   */
  public static String indexed(String template, Object... arguments) {
    return new DominoFormatter().indexed(template, arguments);
  }

  /**
   * Formats a percent-style template using the shared static API.
   *
   * @param template the template to render
   * @param arguments the sequential arguments consumed by percent placeholders
   * @return the formatted string
   */
  public static String percent(String template, Object... arguments) {
    return new DominoFormatter().percent(template, arguments);
  }

  /**
   * Formats a dollar-token template using the currently installed default support.
   *
   * @param template the template to render
   * @param arguments the sequential arguments consumed by token placeholders
   * @return the formatted string
   */
  public static String tokens(String template, Object... arguments) {
    return new DominoFormatter(defaultFormattingSupport).tokens(template, arguments);
  }
}
