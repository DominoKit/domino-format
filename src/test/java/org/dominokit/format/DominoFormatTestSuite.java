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
