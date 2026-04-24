package org.dominokit.format;

import java.util.Objects;
import org.dominokit.format.internal.IndexedStyleFormatter;
import org.dominokit.format.internal.PercentStyleFormatter;
import org.dominokit.format.internal.TokenStyleFormatter;

/**
 * Instance-based entry point for Domino Format.
 *
 * <p>A formatter instance owns the {@link FormattingSupport} used to resolve patterned number and
 * date tokens while reusing the same shared parser implementations for indexed, percent, and
 * dollar-token templates.
 */
public final class DominoFormatter {

  private static final IndexedStyleFormatter INDEXED_STYLE_FORMATTER = new IndexedStyleFormatter();
  private static final PercentStyleFormatter PERCENT_STYLE_FORMATTER = new PercentStyleFormatter();
  private static final TokenStyleFormatter TOKEN_STYLE_FORMATTER = new TokenStyleFormatter();

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
   * Formats an indexed template such as {@code "Hello {0}"}.
   *
   * @param template the template to render
   * @param arguments the indexed arguments consumed by the template
   * @return the formatted string
   */
  public String indexed(String template, Object... arguments) {
    return INDEXED_STYLE_FORMATTER.format(template, arguments);
  }

  /**
   * Formats a percent-style template using the supported token subset.
   *
   * @param template the template to render
   * @param arguments the sequential arguments consumed by percent placeholders
   * @return the formatted string
   */
  public String percent(String template, Object... arguments) {
    return PERCENT_STYLE_FORMATTER.format(template, arguments);
  }

  /**
   * Formats a dollar-token template such as {@code "Qty: $N(000)"}.
   *
   * @param template the template to render
   * @param arguments the sequential arguments consumed by token placeholders
   * @return the formatted string
   */
  public String tokens(String template, Object... arguments) {
    return TOKEN_STYLE_FORMATTER.format(template, formattingSupport, arguments);
  }
}
