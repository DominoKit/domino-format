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

import java.util.Date;

/**
 * Formats {@link Date} instances using a platform-specific date/time pattern engine.
 *
 * <p>The core library keeps date token parsing completely shared across platforms, while concrete
 * implementations bridge to runtime-specific date formatting APIs.
 */
@FunctionalInterface
public interface DateFormatter {

  /**
   * Formats a date value using the provided pattern.
   *
   * @param pattern the runtime-specific date pattern
   * @param value the date to format
   * @return the formatted representation
   */
  String format(String pattern, Date value);
}
