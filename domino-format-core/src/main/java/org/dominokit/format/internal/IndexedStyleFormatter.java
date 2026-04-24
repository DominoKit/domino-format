package org.dominokit.format.internal;

import java.util.Objects;
import org.dominokit.format.FormatException;

/**
 * Formats indexed templates such as {@code "Hello {0}"}.
 *
 * <p>The implementation performs a small character scan and replaces numbered placeholders with
 * the matching argument index. Escaping is intentionally omitted in this first iteration to keep
 * the behavior explicit and easy to extend later.
 */
public final class IndexedStyleFormatter {

  /**
   * Renders an indexed template.
   *
   * @param template the template to render
   * @param arguments the indexed arguments referenced by the template
   * @return the formatted string
   */
  public String format(String template, Object... arguments) {
    Objects.requireNonNull(template, "template");
    StringBuilder output = new StringBuilder(template.length());

    for (int index = 0; index < template.length(); index++) {
      char current = template.charAt(index);
      if (current == '{') {
        int end = template.indexOf('}', index + 1);
        if (end < 0) {
          throw new FormatException("Unexpected end of indexed template after '{'");
        }
        if (end == index + 1) {
          throw new FormatException("Empty indexed placeholder is not allowed");
        }
        String placeholder = template.substring(index + 1, end);
        int argumentIndex;
        try {
          argumentIndex = Integer.parseInt(placeholder);
        } catch (NumberFormatException e) {
          throw new FormatException("Invalid indexed placeholder {" + placeholder + "}", e);
        }
        if (argumentIndex < 0 || argumentIndex >= arguments.length) {
          throw new FormatException("Missing argument for indexed placeholder {" + argumentIndex + "}");
        }
        output.append(String.valueOf(arguments[argumentIndex]));
        index = end;
      } else if (current == '}') {
        throw new FormatException("Unexpected '}' in indexed template");
      } else {
        output.append(current);
      }
    }

    return output.toString();
  }
}
