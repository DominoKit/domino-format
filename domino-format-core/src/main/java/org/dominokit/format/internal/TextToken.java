package org.dominokit.format.internal;

import java.util.Objects;

/**
 * Represents literal text captured while parsing a dollar-token template.
 *
 * <p>Text tokens preserve the source template verbatim so the formatter can append them without any
 * further interpretation.
 */
public final class TextToken implements TemplateToken {

  private final String text;

  /**
   * Creates a new text token.
   *
   * @param text the literal text segment represented by this token
   */
  public TextToken(String text) {
    this.text = Objects.requireNonNull(text, "text");
  }

  /**
   * Returns the literal text segment.
   *
   * @return the text captured by the parser
   */
  public String getText() {
    return text;
  }
}
