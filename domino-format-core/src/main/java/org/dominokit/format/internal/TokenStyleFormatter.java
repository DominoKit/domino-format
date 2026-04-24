package org.dominokit.format.internal;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.dominokit.format.FormatException;
import org.dominokit.format.FormattingSupport;

/**
 * Formats templates that use the Domino Format dollar-token syntax.
 *
 * <p>This formatter combines a reusable token parser with sequential argument consumption so token
 * templates stay compact while remaining strict about malformed input and type mismatches.
 */
public final class TokenStyleFormatter {

  private final TokenTemplateParser parser = new TokenTemplateParser();

  /**
   * Renders a token-style template.
   *
   * @param template the template to render
   * @param formattingSupport the platform delegates used for patterned values
   * @param arguments the sequential arguments referenced by the template
   * @return the formatted string
   */
  public String format(String template, FormattingSupport formattingSupport, Object... arguments) {
    Objects.requireNonNull(template, "template");
    Objects.requireNonNull(formattingSupport, "formattingSupport");
    List<TemplateToken> parsedTemplate = parser.parse(template);
    StringBuilder output = new StringBuilder(template.length());
    int argumentIndex = 0;

    for (TemplateToken templateToken : parsedTemplate) {
      if (templateToken instanceof TextToken) {
        output.append(((TextToken) templateToken).getText());
        continue;
      }

      ArgToken argToken = (ArgToken) templateToken;
      if (argumentIndex >= arguments.length) {
        throw new FormatException("Missing argument for token " + argToken.getLabel());
      }

      output.append(formatArgument(argToken, arguments[argumentIndex++], formattingSupport));
    }

    return output.toString();
  }

  private String formatArgument(
      ArgToken argToken, Object argument, FormattingSupport formattingSupport) {
    switch (argToken.getType()) {
      case STRING:
        if (!(argument instanceof CharSequence)) {
          throw new FormatException(typeMismatch(argToken, CharSequence.class.getName(), argument));
        }
        return argument.toString();
      case LITERAL:
        return String.valueOf(argument);
      case NUMBER:
        return formatNumberToken(argToken, argument, formattingSupport);
      case DECIMAL:
        return formatDecimalToken(argToken, argument, formattingSupport);
      case BOOLEAN:
        if (!(argument instanceof Boolean)) {
          throw new FormatException(typeMismatch(argToken, Boolean.class.getName(), argument));
        }
        return argument.toString();
      case TEMPORAL:
        return formatDateToken(argToken, argument, formattingSupport);
      default:
        throw new FormatException("Unsupported token " + argToken.getLabel());
    }
  }

  private String formatNumberToken(
      ArgToken argToken, Object argument, FormattingSupport formattingSupport) {
    if (!(argument instanceof Number)) {
      throw new FormatException(typeMismatch(argToken, Number.class.getName(), argument));
    }
    Number number = (Number) argument;
    if (argToken.hasPattern()) {
      return formattingSupport.formatNumber(argToken.getPattern(), number);
    }
    return String.valueOf(number);
  }

  private String formatDecimalToken(
      ArgToken argToken, Object argument, FormattingSupport formattingSupport) {
    if (!(argument instanceof Number)) {
      throw new FormatException(typeMismatch(argToken, Number.class.getName(), argument));
    }
    Number number = (Number) argument;
    if (argToken.hasPattern()) {
      return formattingSupport.formatNumber(argToken.getPattern(), number);
    }
    return String.valueOf(number);
  }

  private String formatDateToken(
      ArgToken argToken, Object argument, FormattingSupport formattingSupport) {
    if (!(argument instanceof Date)) {
      throw new FormatException(typeMismatch(argToken, Date.class.getName(), argument));
    }
    Date date = (Date) argument;
    if (argToken.hasPattern()) {
      return formattingSupport.formatDate(argToken.getPattern(), date);
    }
    return String.valueOf(date);
  }

  private String typeMismatch(ArgToken token, String expectedType, Object value) {
    String actualType = value == null ? "null" : value.getClass().getName();
    return "Type mismatch for token "
        + token.getLabel()
        + ": expected "
        + expectedType
        + " but got "
        + actualType;
  }
}
