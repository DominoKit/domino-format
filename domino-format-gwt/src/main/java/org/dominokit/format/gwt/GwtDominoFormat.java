package org.dominokit.format.gwt;

import org.dominokit.format.DominoFormat;
import org.dominokit.format.DominoFormatter;

/**
 * GWT/J2CL convenience facade for Domino Format.
 *
 * <p>This wrapper mirrors the JVM facade so browser-facing code can use the same high-level API
 * while relying on GWT-native date and number formatting delegates.
 */
public final class GwtDominoFormat {

  private static final DominoFormatter FORMATTER =
      DominoFormat.withFormattingSupport(GwtFormattingSupport.create());

  private GwtDominoFormat() {}

  /**
   * Installs GWT/J2CL formatting support into the shared static API.
   */
  public static void installAsDefault() {
    DominoFormat.setDefaultFormattingSupport(GwtFormattingSupport.create());
  }

  /**
   * Returns the reusable GWT-backed formatter instance.
   *
   * @return the GWT-backed formatter
   */
  public static DominoFormatter formatter() {
    return FORMATTER;
  }

  /**
   * Formats an indexed template using GWT-backed support.
   *
   * @param template the template to render
   * @param arguments the indexed arguments consumed by the template
   * @return the formatted string
   */
  public static String indexed(String template, Object... arguments) {
    return FORMATTER.indexed(template, arguments);
  }

  /**
   * Formats a percent template using GWT-backed support.
   *
   * @param template the template to render
   * @param arguments the sequential arguments consumed by the template
   * @return the formatted string
   */
  public static String percent(String template, Object... arguments) {
    return FORMATTER.percent(template, arguments);
  }

  /**
   * Formats a token template using GWT-native numeric and date delegates.
   *
   * @param template the template to render
   * @param arguments the sequential arguments consumed by the template
   * @return the formatted string
   */
  public static String tokens(String template, Object... arguments) {
    return FORMATTER.tokens(template, arguments);
  }
}
