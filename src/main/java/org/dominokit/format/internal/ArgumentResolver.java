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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.dominokit.format.FormatException;
import org.dominokit.format.MissingNamedArgumentHandler;
import org.dominokit.format.NamedArgument;

/**
 * Resolves mixed indexed, sequential, and named formatter arguments from one shared input list.
 *
 * <p>Named arguments supplied as {@link NamedArgument} instances or {@link Map} entries are
 * collected into a dedicated lookup table and removed from the positional argument stream. Indexed
 * placeholders such as {@code {0}} explicitly reserve a positional slot. Sequential placeholders
 * such as {@code %s} and {@code $S} then walk the remaining positional argument array from left to
 * right and skip any slot that has already been reserved or consumed.
 */
public final class ArgumentResolver {

  private final Object[] positionalArguments;
  private final boolean[] reservedArguments;
  private final Map<String, Object> namedArguments = new LinkedHashMap<>();
  private final MissingNamedArgumentHandler missingNamedArgumentHandler;
  private int nextSequentialIndex;

  /**
   * Creates a resolver for the supplied argument list.
   *
   * @param arguments the argument array used by the template
   * @param missingNamedArgumentHandler the handler used when a named placeholder is unresolved
   */
  public ArgumentResolver(
      Object[] arguments, MissingNamedArgumentHandler missingNamedArgumentHandler) {
    this.missingNamedArgumentHandler =
        Objects.requireNonNull(missingNamedArgumentHandler, "missingNamedArgumentHandler");
    List<Object> positionalArguments = new ArrayList<>();
    collectArguments(arguments, positionalArguments);
    this.positionalArguments = positionalArguments.toArray(new Object[0]);
    this.reservedArguments = new boolean[this.positionalArguments.length];
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
    if (argumentIndex >= positionalArguments.length) {
      throw new FormatException("Missing argument for indexed placeholder " + tokenLabel);
    }
    reservedArguments[argumentIndex] = true;
    return positionalArguments[argumentIndex];
  }

  /**
   * Resolves the next sequential placeholder.
   *
   * @param tokenLabel the placeholder label used in error messages
   * @return the next unreserved argument in left-to-right order
   */
  public Object resolveSequential(String tokenLabel) {
    while (nextSequentialIndex < positionalArguments.length
        && reservedArguments[nextSequentialIndex]) {
      nextSequentialIndex++;
    }
    if (nextSequentialIndex >= positionalArguments.length) {
      throw new FormatException("Missing argument for token " + tokenLabel);
    }
    reservedArguments[nextSequentialIndex] = true;
    return positionalArguments[nextSequentialIndex++];
  }

  /**
   * Resolves a named placeholder expression.
   *
   * @param expression the normalized named placeholder expression
   * @return the resolved value rendered as text, or the configured fallback text when unresolved
   */
  public String resolveNamed(String expression) {
    if (namedArguments.containsKey(expression)) {
      return String.valueOf(namedArguments.get(expression));
    }
    return String.valueOf(missingNamedArgumentHandler.handle(expression));
  }

  private void collectArguments(Object[] arguments, List<Object> positionalArguments) {
    for (Object argument : arguments) {
      if (argument instanceof NamedArgument) {
        NamedArgument namedArgument = (NamedArgument) argument;
        namedArguments.put(namedArgument.getName(), namedArgument.getValue());
        continue;
      }
      if (argument instanceof Map<?, ?>) {
        addNamedArgumentsFromMap((Map<?, ?>) argument);
        continue;
      }
      positionalArguments.add(argument);
    }
  }

  private void addNamedArgumentsFromMap(Map<?, ?> source) {
    for (Map.Entry<?, ?> entry : source.entrySet()) {
      namedArguments.put(normalizeMapKey(entry.getKey()), entry.getValue());
    }
  }

  private String normalizeMapKey(Object key) {
    if (!(key instanceof String)) {
      String actualType = key == null ? "null" : key.getClass().getName();
      throw new FormatException(
          "Named argument map keys must be java.lang.String but got " + actualType);
    }

    String normalizedKey = ((String) key).trim();
    if (normalizedKey.isEmpty()) {
      throw new FormatException("Named argument map keys must not be blank");
    }
    return normalizedKey;
  }
}
