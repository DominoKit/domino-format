package org.dominokit.format.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.dominokit.format.FormatException;

/**
 * Parses dollar-token templates into reusable token lists using a single-pass character scan.
 *
 * <p>The parser intentionally avoids regular expressions so the same implementation remains small,
 * allocation-conscious, and easy to reuse on both JVM and GWT runtimes.
 */
public final class TokenTemplateParser {

  /**
   * Parses a template into ordered text and placeholder tokens.
   *
   * @param template the token-style template to parse
   * @return the parsed token list
   */
  public List<TemplateToken> parse(String template) {
    Objects.requireNonNull(template, "template");
    List<TemplateToken> tokens = new ArrayList<>();
    StringBuilder literal = new StringBuilder();

    for (int index = 0; index < template.length(); index++) {
      char current = template.charAt(index);
      if (current != '$') {
        literal.append(current);
        continue;
      }

      if (index == template.length() - 1) {
        throw new FormatException("Unexpected end of template after '$'");
      }

      flushLiteral(tokens, literal);
      char marker = template.charAt(++index);
      if (marker == '$') {
        tokens.add(new TextToken("$"));
        continue;
      }

      TokenType type;
      try {
        type = TokenType.fromMarker(marker);
      } catch (IllegalArgumentException e) {
        throw new FormatException(e.getMessage(), e);
      }

      String pattern = null;
      if (index + 1 < template.length() && template.charAt(index + 1) == '(') {
        int patternStart = index + 2;
        int patternEnd = template.indexOf(')', patternStart);
        if (patternEnd < 0) {
          throw new FormatException("Unexpected end of template while parsing pattern for $" + marker);
        }
        if (patternEnd == patternStart) {
          throw new FormatException("Empty pattern is not allowed for $" + marker);
        }
        pattern = template.substring(patternStart, patternEnd);
        index = patternEnd;
      }

      tokens.add(new ArgToken(type, pattern));
    }

    flushLiteral(tokens, literal);
    return tokens;
  }

  private void flushLiteral(List<TemplateToken> tokens, StringBuilder literal) {
    if (literal.length() == 0) {
      return;
    }
    tokens.add(new TextToken(literal.toString()));
    literal.setLength(0);
  }
}
