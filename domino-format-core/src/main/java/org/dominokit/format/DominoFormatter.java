package org.dominokit.format;

import java.util.Objects;
import org.dominokit.format.internal.MixedStyleFormatter;

/**
 * Instance-based entry point for Domino Format.
 *
 * <p>A formatter instance owns the {@link FormattingSupport} used to resolve patterned number and
 * date tokens while parsing mixed indexed, percent, and dollar-token placeholders through a single
 * formatting pipeline.
 */
public final class DominoFormatter {

  private static final MixedStyleFormatter MIXED_STYLE_FORMATTER = new MixedStyleFormatter();

  private final FormattingSupport formattingSupport;

  /**
   * Creates a formatter that can process templates without platform-specific pattern delegation.
   *
   * <p>Patterned numeric and date tokens such as {@code $N(000)} will fail until a matching
   * {@link FormattingSupport} is supplied.
   */
  public DominoFormatter() {
    this(FormattingSupport.none());
  }

  /**
   * Creates a formatter backed by the supplied platform support.
   *
   * @param formattingSupport the delegates used for patterned numeric and date tokens
   */
  public DominoFormatter(FormattingSupport formattingSupport) {
    this.formattingSupport = Objects.requireNonNull(formattingSupport, "formattingSupport");
  }

  /**
   * Formats a template that may mix indexed placeholders such as {@code {0}}, percent
   * placeholders such as {@code %d}, and dollar-token placeholders such as {@code $N(000)}.
   *
   * @param template the template to render
   * @param arguments the arguments consumed by the template
   * @return the formatted string
   */
  public String format(String template, Object... arguments) {
    return MIXED_STYLE_FORMATTER.format(template, formattingSupport, arguments);
  }
}
