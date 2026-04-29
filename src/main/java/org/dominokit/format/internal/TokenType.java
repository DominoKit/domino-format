package org.dominokit.format.internal;

/**
 * Enumerates the supported dollar-token placeholder kinds.
 *
 * <p>Each enum constant carries the single-character suffix used in templates such as {@code $S}
 * or {@code $T(yyyy-MM-dd)}.
 */
public enum TokenType {
  STRING('S'),
  LITERAL('L'),
  NUMBER('N'),
  DECIMAL('D'),
  BOOLEAN('B'),
  TEMPORAL('T');

  private final char marker;

  TokenType(char marker) {
    this.marker = marker;
  }

  /**
   * Resolves a template marker to its token type.
   *
   * @param marker the single-character placeholder marker found after {@code $}
   * @return the matching token type
   * @throws IllegalArgumentException when the marker is unsupported
   */
  public static TokenType fromMarker(char marker) {
    for (TokenType value : values()) {
      if (value.marker == marker) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unsupported token marker $" + marker);
  }

  /**
   * Returns the single-character suffix used in templates.
   *
   * @return the placeholder marker
   */
  public char getMarker() {
    return marker;
  }
}
