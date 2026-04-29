package org.dominokit.format;

import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * GWT test suite for the Domino Format module.
 *
 * <p>The suite exposes {@link DominoFormatGwtTest} to the GWT test runner so the shared formatting
 * contract is compiled and executed in the browser-compatible runtime.
 */
public class DominoFormatTestSuite extends GWTTestSuite {

  /**
   * Builds the GWT-compatible Domino Format test suite.
   *
   * @return the suite containing all Domino Format GWT tests
   */
  public static Test suite() {
    TestSuite suite = new TestSuite("Tests for Domino-format");

    suite.addTestSuite(DominoFormatGwtTest.class);

    return suite;
  }
}
