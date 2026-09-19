/*
 * Copyright © 2026 Dominokit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dominokit.format.internal;

/**
 * Enumerates the supported dollar-token placeholder kinds.
 *
 * <p>Each enum constant carries the single-character suffix used in templates such as {@code $S} or
 * {@code $T(yyyy-MM-dd)}.
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
