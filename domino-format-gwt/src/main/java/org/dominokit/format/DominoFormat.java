package org.dominokit.format;

import java.util.Map;
import java.util.Objects;

/**
 * GWT convenience facade for Domino Format.
 *
 * <p>This facade provides the shared static entry point used by GWT applications. It is
 * initialized with {@link GwtFormattingSupport} by default, so patterned number and date tokens
 * work without any manual setup.
 */
public final class DominoFormat {

  private static DominoFormatter FORMATTER = new DominoFormatter(GwtFormattingSupport.create());

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
    FORMATTER =
        FORMATTER.withFormattingSupport(
            Objects.requireNonNull(formattingSupport, "formattingSupport"));
  }

  /**
   * Restores the shared static formatter to the default GWT formatting support.
   *
   * <p>After reset, patterned number and date tokens are again delegated to
   * {@link GwtFormattingSupport}.
   */
  public static void resetDefaultFormattingSupport() {
    withFormattingSupport(GwtFormattingSupport.create());
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
   * Replaces the shared missing named argument handler used by {@code $(...)} placeholders.
   *
   * <p>The installed handler is only consulted when a named placeholder expression cannot be
   * resolved from the supplied named arguments.
   *
   * @param missingNamedArgumentHandler the handler used for unresolved named expressions
   */
  public static void setMissingNamedArgumentHandler(
      MissingNamedArgumentHandler missingNamedArgumentHandler) {
    FORMATTER =
        FORMATTER.withMissingNamedArgumentHandler(
            Objects.requireNonNull(
                missingNamedArgumentHandler, "missingNamedArgumentHandler"));
  }

  /**
   * Restores the default missing named argument handler.
   *
   * <p>The default behavior replaces unresolved named placeholder expressions with an empty
   * string.
   */
  public static void resetDefaultMissingNamedArgumentHandler() {
    withMissingNamedArgumentHandler(MissingNamedArgumentHandler.replaceWithEmpty());
  }

  /**
   * Returns the missing named argument handler currently used by the shared static formatter.
   *
   * @return the installed handler for unresolved named placeholders
   */
  public static MissingNamedArgumentHandler getMissingNamedArgumentHandler() {
    return FORMATTER.getMissingNamedArgumentHandler();
  }

  /**
   * Replaces the shared missing named argument handler and returns the updated formatter.
   *
   * @param missingNamedArgumentHandler the handler used for unresolved named expressions
   * @return the shared static formatter after installing the supplied handler
   */
  public static DominoFormatter withMissingNamedArgumentHandler(
      MissingNamedArgumentHandler missingNamedArgumentHandler) {
    setMissingNamedArgumentHandler(missingNamedArgumentHandler);
    return FORMATTER;
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
   * Returns the current shared static formatter instance.
   *
   * @return the current shared formatter
   */
  public static DominoFormatter formatter() {
    return FORMATTER;
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
    return FORMATTER.format(template, namedArguments, arguments);
  }

  /**
   * Formats a template that may mix indexed, named, percent, and dollar-token placeholders.
   *
   * @param template the template to render
   * @param arguments the arguments consumed by the template
   * @return the formatted string
   */
  public static String format(String template, Object... arguments) {
    return FORMATTER.format(template, arguments);
  }
}
