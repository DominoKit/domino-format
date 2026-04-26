package org.dominokit.format;

import java.util.Map;
import java.util.Objects;

/**
 * Test-only static facade used by the core module tests.
 *
 * <p>The production static {@code DominoFormat} facade now lives in the runtime modules, but the
 * core parser tests still need a small static entry point so they can verify shared formatting
 * semantics without choosing a concrete runtime adapter.
 */
public final class DominoFormat {

  private static volatile FormattingSupport defaultFormattingSupport = FormattingSupport.none();
  private static volatile MissingNamedArgumentHandler defaultMissingNamedArgumentHandler =
      MissingNamedArgumentHandler.replaceWithEmpty();

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
   * Installs the default missing named argument handler used by the static formatting methods.
   *
   * @param missingNamedArgumentHandler the handler used for unresolved named placeholders
   */
  public static void setDefaultMissingNamedArgumentHandler(
      MissingNamedArgumentHandler missingNamedArgumentHandler) {
    defaultMissingNamedArgumentHandler =
        Objects.requireNonNull(missingNamedArgumentHandler, "missingNamedArgumentHandler");
  }

  /**
   * Restores the default missing named argument handler.
   *
   * <p>The default behavior replaces unresolved named placeholder expressions with an empty
   * string.
   */
  public static void resetDefaultMissingNamedArgumentHandler() {
    defaultMissingNamedArgumentHandler = MissingNamedArgumentHandler.replaceWithEmpty();
  }

  /**
   * Returns the default missing named argument handler currently used by the static API.
   *
   * @return the installed handler for unresolved named placeholders
   */
  public static MissingNamedArgumentHandler getDefaultMissingNamedArgumentHandler() {
    return defaultMissingNamedArgumentHandler;
  }

  /**
   * Creates an instance-based formatter backed by the supplied support object.
   *
   * @param formattingSupport the delegates used to resolve numeric and date patterns
   * @return a formatter bound to the provided support
   */
  public static DominoFormatter withFormattingSupport(FormattingSupport formattingSupport) {
    return new DominoFormatter(formattingSupport, defaultMissingNamedArgumentHandler);
  }

  /**
   * Creates an instance-based formatter backed by the supplied missing named argument handler.
   *
   * @param missingNamedArgumentHandler the handler used for unresolved named placeholders
   * @return a formatter bound to the provided handler
   */
  public static DominoFormatter withMissingNamedArgumentHandler(
      MissingNamedArgumentHandler missingNamedArgumentHandler) {
    return new DominoFormatter(defaultFormattingSupport, missingNamedArgumentHandler);
  }

  /**
   * Creates a named argument for placeholders such as {@code $(userName)}.
   *
   * @param name the placeholder expression to satisfy
   * @param value the value associated with the expression
   * @return a named argument carrying the supplied name and value
   */
  public static NamedArgument byName(String name, Object value) {
    return DominoFormatter.byName(name, value);
  }

  /**
   * Formats a template using a named argument map together with any positional arguments.
   *
   * @param template the template to render
   * @param namedArguments the named arguments available to {@code $(...)} placeholders
   * @param arguments the positional arguments consumed by indexed, percent, and dollar tokens
   * @return the formatted string
   */
  public static String format(String template, Map<String, ?> namedArguments, Object... arguments) {
    return new DominoFormatter(defaultFormattingSupport, defaultMissingNamedArgumentHandler)
        .format(template, namedArguments, arguments);
  }

  /**
   * Formats a template that may freely mix indexed, named, percent, and dollar-token
   * placeholders.
   *
   * @param template the template to render
   * @param arguments the arguments consumed by the template
   * @return the formatted string
   */
  public static String format(String template, Object... arguments) {
    return new DominoFormatter(defaultFormattingSupport, defaultMissingNamedArgumentHandler)
        .format(template, arguments);
  }
}
