package org.dominokit.format.internal;

import java.util.Date;
import java.util.Objects;
import org.dominokit.format.FormatException;
import org.dominokit.format.FormattingSupport;

/**
 * Formats templates that may mix indexed, percent, and dollar-token placeholder styles.
 *
 * <p>The formatter scans the template once, resolves indexed placeholders through explicit
 * argument positions, and resolves percent and dollar-token placeholders through a shared
 * left-to-right sequential cursor that skips any argument slot already claimed by an indexed
 * placeholder.
 */
public final class MixedStyleFormatter {

  /**
   * Renders a mixed-style template.
   *
   * @param template the template to render
   * @param formattingSupport the platform delegates used for patterned number and date values
   * @param arguments the arguments consumed by the template
   * @return the formatted string
   */
  public String format(String template, FormattingSupport formattingSupport, Object... arguments) {
    Objects.requireNonNull(template, "template");
    Objects.requireNonNull(formattingSupport, "formattingSupport");
    ArgumentResolver argumentResolver = new ArgumentResolver(arguments);
    StringBuilder output = new StringBuilder(template.length());

    for (int index = 0; index < template.length(); index++) {
      char current = template.charAt(index);
      switch (current) {
        case '{':
          index = appendIndexedPlaceholder(template, index, output, argumentResolver);
          break;
        case '}':
          throw new FormatException("Unexpected '}' in format template");
        case '%':
          index = appendPercentPlaceholder(template, index, output, argumentResolver);
          break;
        case '$':
          index =
              appendDollarPlaceholder(template, index, output, argumentResolver, formattingSupport);
          break;
        default:
          output.append(current);
      }
    }

    return output.toString();
  }

  private int appendIndexedPlaceholder(
      String template, int startIndex, StringBuilder output, ArgumentResolver argumentResolver) {
    int endIndex = template.indexOf('}', startIndex + 1);
    if (endIndex < 0) {
      throw new FormatException("Unexpected end of format template after '{'");
    }
    if (endIndex == startIndex + 1) {
      throw new FormatException("Empty indexed placeholder is not allowed");
    }

    String placeholder = template.substring(startIndex + 1, endIndex);
    int argumentIndex;
    try {
      argumentIndex = Integer.parseInt(placeholder);
    } catch (NumberFormatException e) {
      throw new FormatException("Invalid indexed placeholder {" + placeholder + "}", e);
    }

    output.append(String.valueOf(argumentResolver.resolveIndexed(argumentIndex, "{" + placeholder + "}")));
    return endIndex;
  }

  private int appendPercentPlaceholder(
      String template, int startIndex, StringBuilder output, ArgumentResolver argumentResolver) {
    if (startIndex == template.length() - 1) {
      throw new FormatException("Unexpected end of format template after '%'");
    }

    char token = template.charAt(startIndex + 1);
    switch (token) {
      case '%':
        output.append('%');
        return startIndex + 1;
      case 'n':
        output.append('\n');
        return startIndex + 1;
      case 's':
        output.append(String.valueOf(argumentResolver.resolveSequential("%s")));
        return startIndex + 1;
      case 'd':
        output.append(formatIntegral(argumentResolver.resolveSequential("%d"), "%d"));
        return startIndex + 1;
      case 'f':
        output.append(formatDecimal(argumentResolver.resolveSequential("%f"), "%f"));
        return startIndex + 1;
      case 'b':
        output.append(formatBoolean(argumentResolver.resolveSequential("%b"), "%b"));
        return startIndex + 1;
      default:
        throw new FormatException("Unsupported percent token %" + token);
    }
  }

  private int appendDollarPlaceholder(
      String template,
      int startIndex,
      StringBuilder output,
      ArgumentResolver argumentResolver,
      FormattingSupport formattingSupport) {
    if (startIndex == template.length() - 1) {
      throw new FormatException("Unexpected end of format template after '$'");
    }

    char marker = template.charAt(startIndex + 1);
    if (marker == '$') {
      output.append('$');
      return startIndex + 1;
    }

    TokenType tokenType;
    try {
      tokenType = TokenType.fromMarker(marker);
    } catch (IllegalArgumentException e) {
      throw new FormatException(e.getMessage(), e);
    }

    String pattern = null;
    int endIndex = startIndex + 1;
    if (endIndex + 1 < template.length() && template.charAt(endIndex + 1) == '(') {
      int patternStartIndex = endIndex + 2;
      int patternEndIndex = template.indexOf(')', patternStartIndex);
      if (patternEndIndex < 0) {
        throw new FormatException(
            "Unexpected end of format template while parsing pattern for $" + marker);
      }
      if (patternEndIndex == patternStartIndex) {
        throw new FormatException("Empty pattern is not allowed for $" + marker);
      }
      pattern = template.substring(patternStartIndex, patternEndIndex);
      endIndex = patternEndIndex;
    }

    output.append(
        formatDollarToken(
            tokenType,
            pattern,
            argumentResolver.resolveSequential("$" + marker),
            formattingSupport));
    return endIndex;
  }

  private String formatDollarToken(
      TokenType tokenType, String pattern, Object argument, FormattingSupport formattingSupport) {
    switch (tokenType) {
      case STRING:
        if (!(argument instanceof CharSequence)) {
          throw new FormatException(typeMismatch("$S", CharSequence.class.getName(), argument));
        }
        return argument.toString();
      case LITERAL:
        return String.valueOf(argument);
      case NUMBER:
        return formatNumber(pattern, argument, "$N", formattingSupport);
      case DECIMAL:
        return formatNumber(pattern, argument, "$D", formattingSupport);
      case BOOLEAN:
        if (!(argument instanceof Boolean)) {
          throw new FormatException(typeMismatch("$B", Boolean.class.getName(), argument));
        }
        return String.valueOf(argument);
      case TEMPORAL:
        if (!(argument instanceof Date)) {
          throw new FormatException(typeMismatch("$T", Date.class.getName(), argument));
        }
        if (pattern != null) {
          return formattingSupport.formatDate(pattern, (Date) argument);
        }
        return String.valueOf(argument);
      default:
        throw new FormatException("Unsupported token $" + tokenType.getMarker());
    }
  }

  private String formatNumber(
      String pattern, Object argument, String tokenLabel, FormattingSupport formattingSupport) {
    if (!(argument instanceof Number)) {
      throw new FormatException(typeMismatch(tokenLabel, Number.class.getName(), argument));
    }
    Number number = (Number) argument;
    if (pattern != null) {
      return formattingSupport.formatNumber(pattern, number);
    }
    return String.valueOf(number);
  }

  private String formatIntegral(Object argument, String tokenLabel) {
    if (!(argument instanceof Byte
        || argument instanceof Short
        || argument instanceof Integer
        || argument instanceof Long)) {
      throw new FormatException(typeMismatch(tokenLabel, "an integral number", argument));
    }
    return String.valueOf(argument);
  }

  private String formatDecimal(Object argument, String tokenLabel) {
    if (!(argument instanceof Number)) {
      throw new FormatException(typeMismatch(tokenLabel, Number.class.getName(), argument));
    }
    return Double.toString(((Number) argument).doubleValue());
  }

  private String formatBoolean(Object argument, String tokenLabel) {
    if (!(argument instanceof Boolean)) {
      throw new FormatException(typeMismatch(tokenLabel, Boolean.class.getName(), argument));
    }
    return String.valueOf(argument);
  }

  private String typeMismatch(String tokenLabel, String expectedType, Object value) {
    String actualType = value == null ? "null" : value.getClass().getName();
    return "Type mismatch for token "
        + tokenLabel
        + ": expected "
        + expectedType
        + " but got "
        + actualType;
  }
}
