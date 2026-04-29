package org.dominokit.format;

import java.util.Objects;

/**
 * Represents one named value that can satisfy a placeholder such as {@code $(userName)}.
 *
 * <p>Instances are typically created through {@link DominoFormatter#byName(String, Object)} or the
 * runtime {@code DominoFormat.byName(...)} helpers. Leading and trailing whitespace in the
 * supplied name is ignored, while internal whitespace is preserved.
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
