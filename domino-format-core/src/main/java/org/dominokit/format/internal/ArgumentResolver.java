package org.dominokit.format.internal;

import org.dominokit.format.FormatException;

/**
 * Resolves mixed indexed and sequential formatter arguments against one shared argument array.
 *
 * <p>Indexed placeholders such as {@code {0}} explicitly reserve an argument slot. Sequential
 * placeholders such as {@code %s} and {@code $S} then walk the argument array from left to right
 * and skip any slot that has already been reserved or consumed.
 */
public final class ArgumentResolver {

  private final Object[] arguments;
  private final boolean[] reservedArguments;
  private int nextSequentialIndex;

  /**
   * Creates a resolver for the supplied argument list.
   *
   * @param arguments the argument array used by the template
   */
  public ArgumentResolver(Object[] arguments) {
    this.arguments = arguments;
    this.reservedArguments = new boolean[arguments.length];
  }

  /**
   * Resolves an explicitly indexed placeholder.
   *
   * @param argumentIndex the requested argument index
   * @param tokenLabel the placeholder label used in error messages
   * @return the referenced argument
   */
  public Object resolveIndexed(int argumentIndex, String tokenLabel) {
    if (argumentIndex < 0) {
      throw new FormatException("Invalid indexed placeholder " + tokenLabel);
    }
    if (argumentIndex >= arguments.length) {
      throw new FormatException("Missing argument for indexed placeholder " + tokenLabel);
    }
    reservedArguments[argumentIndex] = true;
    return arguments[argumentIndex];
  }

  /**
   * Resolves the next sequential placeholder.
   *
   * @param tokenLabel the placeholder label used in error messages
   * @return the next unreserved argument in left-to-right order
   */
  public Object resolveSequential(String tokenLabel) {
    while (nextSequentialIndex < arguments.length && reservedArguments[nextSequentialIndex]) {
      nextSequentialIndex++;
    }
    if (nextSequentialIndex >= arguments.length) {
      throw new FormatException("Missing argument for token " + tokenLabel);
    }
    reservedArguments[nextSequentialIndex] = true;
    return arguments[nextSequentialIndex++];
  }
}
