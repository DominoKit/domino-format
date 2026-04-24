package org.dominokit.format.internal;

import java.util.Objects;

/**
 * Represents a parsed dollar-token placeholder that consumes one runtime argument.
 *
 * <p>Argument tokens capture both the placeholder kind and the optional pattern text so formatting
 * can remain a simple pass over a pre-parsed token list.
 */
public final class ArgToken implements TemplateToken {

  private final TokenType type;
  private final String pattern;

  /**
   * Creates a new argument token.
   *
   * @param type the placeholder kind
   * @param pattern the optional pattern text, or {@code null} when absent
   */
  public ArgToken(TokenType type, String pattern) {
    this.type = Objects.requireNonNull(type, "type");
    this.pattern = pattern;
  }

  /**
   * Returns the placeholder kind.
   *
   * @return the token type
   */
  public TokenType getType() {
    return type;
  }

  /**
   * Returns the optional pattern text.
   *
   * @return the pattern text, or {@code null} when no pattern was declared
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Indicates whether the token carries explicit pattern text.
   *
   * @return {@code true} when a pattern is present
   */
  public boolean hasPattern() {
    return pattern != null;
  }

  /**
   * Returns a user-facing token label such as {@code $S}.
   *
   * @return the placeholder label used in error messages
   */
  public String getLabel() {
    return "$" + type.getMarker();
  }
}
