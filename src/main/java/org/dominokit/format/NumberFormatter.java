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

/**
 * Formats numeric values using a platform-specific pattern engine.
 *
 * <p>The shared Domino Format parser never interprets numeric patterns itself. Instead it forwards
 * the raw pattern string to an implementation of this contract so JVM, GWT, and future runtimes can
 * each delegate to their native formatting facilities.
 */
@FunctionalInterface
public interface NumberFormatter {

  /**
   * Formats a numeric value using the provided pattern.
   *
   * @param pattern the runtime-specific numeric pattern
   * @param value the numeric value to format
   * @return the formatted representation
   */
  String format(String pattern, Number value);
}
