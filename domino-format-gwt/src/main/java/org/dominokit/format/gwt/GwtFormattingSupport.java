package org.dominokit.format.gwt;

import org.dominokit.format.FormattingSupport;
import org.gwtproject.i18n.client.NumberFormat;
import org.gwtproject.i18n.shared.DateTimeFormat;

/**
 * Provides GWT/J2CL-native patterned number and date formatting support for Domino Format.
 *
 * <p>The implementation delegates directly to the {@code org.gwtproject.i18n} APIs already used by
 * Domino-UI, keeping the shared parser free of runtime-specific dependencies.
 */
public final class GwtFormattingSupport {

  private static final FormattingSupport SUPPORT =
      FormattingSupport.create(
          (pattern, value) -> NumberFormat.getFormat(pattern).format(value.doubleValue()),
          (pattern, value) -> DateTimeFormat.getFormat(pattern).format(value));

  private GwtFormattingSupport() {}

  /**
   * Returns a reusable GWT/J2CL formatting support instance.
   *
   * @return the GWT/J2CL support object
   */
  public static FormattingSupport create() {
    return SUPPORT;
  }
}
