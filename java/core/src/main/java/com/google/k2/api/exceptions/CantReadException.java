/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.k2.api.exceptions;

import com.google.k2.internal.primitives.Operation;

/**
 * This exception indicates that there was a problem reading data during execution of an
 * {@link Operation}.
 *
 * <p>The message and cause will indicate the reasons.
 */
public class CantReadException extends K2Exception {
  private static final long serialVersionUID = 1L;

  public CantReadException() {
    super();
  }

  public CantReadException(String message, Throwable cause) {
    super(message, cause);
  }

  public CantReadException(String message) {
    super(message);
  }

  public CantReadException(Throwable cause) {
    super(cause);
  }
}
