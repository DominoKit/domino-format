package org.dominokit.format;

/**
 * GWT super-source replacement for the JVM formatting support type.
 *
 * <p>The shared {@link DominoFormat} facade always instantiates {@code JvmFormattingSupport}. During
 * GWT compilation this super-source shadows the JVM implementation that depends on {@code java.text}
 * and routes construction through {@link GwtFormattingSupport} instead.
 */
public class JvmFormattingSupport extends GwtFormattingSupport {

  /**
   * Creates support backed by GWT number and date formatting delegates.
   */
  public JvmFormattingSupport() {
    super();
  }

  /**
   * Creates support backed by caller-provided formatting delegates.
   *
   * @param numberFormatter the delegate used for numeric patterns, or {@code null}
   * @param dateFormatter the delegate used for date patterns, or {@code null}
   */
  public JvmFormattingSupport(NumberFormatter numberFormatter, DateFormatter dateFormatter) {
    super(numberFormatter, dateFormatter);
  }
}
