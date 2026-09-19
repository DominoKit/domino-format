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
 * Signals malformed templates, missing arguments, unsupported patterns, or invalid argument types
 * detected while formatting text with Domino Format.
 *
 * <p>The exception is intentionally unchecked because template formatting is usually performed deep
 * inside rendering, logging, or UI projection code where propagating a checked exception would add
 * ceremony without improving recovery.
 */
public class FormatException extends RuntimeException {

  /**
   * Creates a new exception with the supplied message.
   *
   * @param message a human-readable explanation of the formatting failure
   */
  public FormatException(String message) {
    super(message);
  }

  /**
   * Creates a new exception with the supplied message and root cause.
   *
   * @param message a human-readable explanation of the formatting failure
   * @param cause the underlying parsing or platform formatting failure
   */
  public FormatException(String message, Throwable cause) {
    super(message, cause);
  }
}
