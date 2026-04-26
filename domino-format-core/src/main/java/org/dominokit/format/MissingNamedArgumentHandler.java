package org.dominokit.format;

/**
 * Produces replacement text when a named placeholder expression cannot be resolved.
 *
 * <p>Named placeholders such as {@code $(userName)} first attempt to resolve their value from the
 * supplied named arguments. When no value exists for the requested expression, the formatter
 * delegates to this handler. The default handler replaces missing expressions with an empty string.
 */
@FunctionalInterface
public interface MissingNamedArgumentHandler {

  /**
   * Shared default handler that replaces unresolved expressions with an empty string.
   */
  MissingNamedArgumentHandler REPLACE_WITH_EMPTY = expression -> "";

  /**
   * Returns the default missing named argument handler.
   *
   * @return the handler that replaces missing expressions with an empty string
   */
  static MissingNamedArgumentHandler replaceWithEmpty() {
    return REPLACE_WITH_EMPTY;
  }

  /**
   * Produces replacement text for a missing named placeholder expression.
   *
   * @param expression the normalized named placeholder expression, for example {@code userName}
   * @return the replacement text that should be appended to the formatted result
   */
  String handle(String expression);
}
