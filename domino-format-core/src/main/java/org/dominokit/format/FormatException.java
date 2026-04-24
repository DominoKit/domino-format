package org.dominokit.format;

/**
 * Signals malformed templates, missing arguments, unsupported patterns, or invalid argument types
 * detected while formatting text with Domino Format.
 *
 * <p>The exception is intentionally unchecked because template formatting is usually performed deep
 * inside rendering, logging, or UI projection code where propagating a checked exception would add
 * ceremony without improving recovery.
 */
public class FormatException extends RuntimeException {

  /**
   * Creates a new exception with the supplied message.
   *
   * @param message a human-readable explanation of the formatting failure
   */
  public FormatException(String message) {
    super(message);
  }

  /**
   * Creates a new exception with the supplied message and root cause.
   *
   * @param message a human-readable explanation of the formatting failure
   * @param cause the underlying parsing or platform formatting failure
   */
  public FormatException(String message, Throwable cause) {
    super(message, cause);
  }
}
