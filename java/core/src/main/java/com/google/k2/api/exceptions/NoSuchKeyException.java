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

import com.google.k2.api.KeyStorage;

/**
 * This exception indicates that a {@link KeyStorage} instance could not find a key with the given
 * ID.
 */
public class NoSuchKeyException extends K2Exception {
  private static final long serialVersionUID = 1L;

  public NoSuchKeyException() {
    super();
  }

  public NoSuchKeyException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  public NoSuchKeyException(String arg0) {
    super(arg0);
  }

  public NoSuchKeyException(Throwable arg0) {
    super(arg0);
  }
}
