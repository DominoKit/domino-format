package org.dominokit.format.internal;

import java.util.Objects;
import org.dominokit.format.FormatException;

/**
 * Formats percent-style templates using the intentionally small supported token subset.
 *
 * <p>The formatter supports {@code %s}, {@code %d}, {@code %f}, {@code %b}, {@code %%}, and
 * {@code %n}. Unsupported flags, width specifiers, and advanced {@link String#format(String,
 * Object...)} behavior are deliberately excluded.
 */
public final class PercentStyleFormatter {

  /**
   * Renders a percent-style template.
   *
   * @param template the template to render
   * @param arguments the sequential arguments referenced by the template
   * @return the formatted string
   */
  public String format(String template, Object... arguments) {
    Objects.requireNonNull(template, "template");
    StringBuilder output = new StringBuilder(template.length());
    int argumentIndex = 0;

    for (int index = 0; index < template.length(); index++) {
      char current = template.charAt(index);
      if (current != '%') {
        output.append(current);
        continue;
      }

      if (index == template.length() - 1) {
        throw new FormatException("Unexpected end of percent template after '%'");
      }

      char token = template.charAt(++index);
      switch (token) {
        case '%':
          output.append('%');
          break;
        case 'n':
          output.append('\n');
          break;
        case 's':
          output.append(String.valueOf(requireArgument(arguments, argumentIndex++, "%s")));
          break;
        case 'd':
          output.append(formatInteger(requireArgument(arguments, argumentIndex++, "%d")));
          break;
        case 'f':
          output.append(formatDecimal(requireArgument(arguments, argumentIndex++, "%f")));
          break;
        case 'b':
          output.append(formatBoolean(requireArgument(arguments, argumentIndex++, "%b")));
          break;
        default:
          throw new FormatException("Unsupported percent token %" + token);
      }
    }

    return output.toString();
  }

  private Object requireArgument(Object[] arguments, int index, String token) {
    if (index >= arguments.length) {
      throw new FormatException("Missing argument for percent token " + token);
    }
    return arguments[index];
  }

  private String formatInteger(Object value) {
    if (!(value instanceof Byte
        || value instanceof Short
        || value instanceof Integer
        || value instanceof Long)) {
      throw new FormatException(typeMismatch("%d", "an integral number", value));
    }
    return String.valueOf(value);
  }

  private String formatDecimal(Object value) {
    if (!(value instanceof Number)) {
      throw new FormatException(typeMismatch("%f", Number.class.getName(), value));
    }
    return Double.toString(((Number) value).doubleValue());
  }

  private String formatBoolean(Object value) {
    if (!(value instanceof Boolean)) {
      throw new FormatException(typeMismatch("%b", Boolean.class.getName(), value));
    }
    return String.valueOf(value);
  }

  private String typeMismatch(String token, String expectedType, Object value) {
    String actualType = value == null ? "null" : value.getClass().getName();
    return "Type mismatch for token " + token + ": expected " + expectedType + " but got " + actualType;
  }
}
