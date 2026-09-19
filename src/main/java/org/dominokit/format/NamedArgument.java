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
package org.dominokit.format;

import java.util.Objects;

/**
 * Represents one named value that can satisfy a placeholder such as {@code $(userName)}.
 *
 * <p>Instances are typically created through {@link DominoFormatter#byName(String, Object)} or the
 * runtime {@code DominoFormat.byName(...)} helpers. Leading and trailing whitespace in the supplied
 * name is ignored, while internal whitespace is preserved.
 */
public final class NamedArgument {

  private final String name;
  private final Object value;

  private NamedArgument(String name, Object value) {
    this.name = normalizeName(name);
    this.value = value;
  }

  /**
   * Creates a named argument with the supplied name and value.
   *
   * @param name the placeholder expression to satisfy, for example {@code userName}
   * @param value the value associated with the expression
   * @return a named argument carrying the supplied name and value
   */
  public static NamedArgument of(String name, Object value) {
    return new NamedArgument(name, value);
  }

  /**
   * Returns the normalized argument name.
   *
   * @return the placeholder expression handled by this argument
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the associated value.
   *
   * @return the value that should be used when the named expression is resolved
   */
  public Object getValue() {
    return value;
  }

  private static String normalizeName(String name) {
    String normalizedName = Objects.requireNonNull(name, "name").trim();
    if (normalizedName.isEmpty()) {
      throw new IllegalArgumentException("Named argument name must not be blank");
    }
    return normalizedName;
  }
}
