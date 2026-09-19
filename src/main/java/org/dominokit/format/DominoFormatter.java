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
package org.dominokit.format;

import java.util.Map;
import java.util.Objects;
import org.dominokit.format.internal.MixedStyleFormatter;

/**
 * Instance-based entry point for Domino Format.
 *
 * <p>A formatter instance owns the {@link FormattingSupport} used to resolve patterned number and
 * date tokens while parsing mixed indexed, named, percent, and dollar-token placeholders through a
 * single formatting pipeline.
 */
public final class DominoFormatter {

  private static final MixedStyleFormatter MIXED_STYLE_FORMATTER = new MixedStyleFormatter();

  private final FormattingSupport formattingSupport;
  private final MissingNamedArgumentHandler missingNamedArgumentHandler;

  /**
   * Creates a formatter that can process templates without platform-specific pattern delegation.
   *
   * <p>Patterned numeric and date tokens such as {@code $N(000)} will fail until a matching {@link
   * FormattingSupport} is supplied. Missing named placeholders are replaced with an empty string by
   * default.
   */
  public DominoFormatter() {
    this(FormattingSupport.none(), MissingNamedArgumentHandler.replaceWithEmpty());
  }

  /**
   * Creates a formatter backed by the supplied platform support while using the default missing
   * named argument handler.
   *
   * @param formattingSupport the delegates used for patterned numeric and date tokens
   */
  public DominoFormatter(FormattingSupport formattingSupport) {
    this(formattingSupport, MissingNamedArgumentHandler.replaceWithEmpty());
  }

  /**
   * Creates a formatter that uses the supplied missing named argument handler and no patterned
   * number or date support.
   *
   * @param missingNamedArgumentHandler the handler used when a named placeholder cannot be resolved
   */
  public DominoFormatter(MissingNamedArgumentHandler missingNamedArgumentHandler) {
    this(FormattingSupport.none(), missingNamedArgumentHandler);
  }

  /**
   * Creates a formatter backed by the supplied platform support and missing named argument handler.
   *
   * @param formattingSupport the delegates used for patterned numeric and date tokens
   * @param missingNamedArgumentHandler the handler used when a named placeholder cannot be resolved
   */
  public DominoFormatter(
      FormattingSupport formattingSupport,
      MissingNamedArgumentHandler missingNamedArgumentHandler) {
    this.formattingSupport = Objects.requireNonNull(formattingSupport, "formattingSupport");
    this.missingNamedArgumentHandler =
        Objects.requireNonNull(missingNamedArgumentHandler, "missingNamedArgumentHandler");
  }

  /**
   * Creates a named argument for placeholders such as {@code $(userName)}.
   *
   * @param name the placeholder expression to satisfy
   * @param value the value associated with the expression
   * @return a named argument carrying the supplied name and value
   */
  public static NamedArgument byName(String name, Object value) {
    return NamedArgument.of(name, value);
  }

  /**
   * Returns the formatting support currently used by this formatter instance.
   *
   * @return the configured patterned number and date support
   */
  public FormattingSupport getFormattingSupport() {
    return formattingSupport;
  }

  /**
   * Returns the handler currently used for missing named placeholders.
   *
   * @return the configured missing named argument handler
   */
  public MissingNamedArgumentHandler getMissingNamedArgumentHandler() {
    return missingNamedArgumentHandler;
  }

  /**
   * Creates a new formatter that preserves the current missing named argument handler while
   * replacing the formatting support.
   *
   * @param formattingSupport the delegates used for patterned numeric and date tokens
   * @return a new formatter instance using the supplied support
   */
  public DominoFormatter withFormattingSupport(FormattingSupport formattingSupport) {
    return new DominoFormatter(formattingSupport, missingNamedArgumentHandler);
  }

  /**
   * Creates a new formatter that preserves the current formatting support while replacing the
   * missing named argument handler.
   *
   * @param missingNamedArgumentHandler the handler used when a named placeholder cannot be resolved
   * @return a new formatter instance using the supplied missing argument handler
   */
  public DominoFormatter withMissingNamedArgumentHandler(
      MissingNamedArgumentHandler missingNamedArgumentHandler) {
    return new DominoFormatter(formattingSupport, missingNamedArgumentHandler);
  }

  /**
   * Formats a template using the supplied named argument map together with any positional
   * arguments.
   *
   * <p>The named arguments satisfy placeholders such as {@code $(userName)}. The remaining
   * positional arguments continue to serve indexed, percent, and dollar-token placeholders.
   *
   * @param template the template to render
   * @param namedArguments the named arguments available to {@code $(...)} placeholders
   * @param arguments the positional arguments consumed by indexed, percent, and dollar tokens
   * @return the formatted string
   */
  public String format(String template, Map<String, ?> namedArguments, Object... arguments) {
    Objects.requireNonNull(namedArguments, "namedArguments");
    Object[] mergedArguments = new Object[arguments.length + 1];
    mergedArguments[0] = namedArguments;
    System.arraycopy(arguments, 0, mergedArguments, 1, arguments.length);
    return format(template, mergedArguments);
  }

  /**
   * Formats a template that may mix indexed placeholders such as {@code {0}}, named placeholders
   * such as {@code $(userName)}, percent placeholders such as {@code %d}, and dollar-token
   * placeholders such as {@code $N(000)}.
   *
   * <p>Named placeholders can be satisfied either by passing {@link NamedArgument} instances
   * created through {@link #byName(String, Object)} or by passing {@link Map} objects whose keys
   * represent placeholder expressions.
   *
   * @param template the template to render
   * @param arguments the arguments consumed by the template
   * @return the formatted string
   */
  public String format(String template, Object... arguments) {
    return MIXED_STYLE_FORMATTER.format(
        template, formattingSupport, missingNamedArgumentHandler, arguments);
  }
}
