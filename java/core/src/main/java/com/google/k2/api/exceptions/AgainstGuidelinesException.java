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

/**
 * This exception is thrown when the CryptoGuidelines reject a particular key and operation.
 *
 * <p>This could be becuse the key length is too small, or the operation is not
 */
public class AgainstGuidelinesException extends K2Exception {
  private static final long serialVersionUID = 1L;

  public AgainstGuidelinesException() {
    super();
  }

  public AgainstGuidelinesException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  public AgainstGuidelinesException(String arg0) {
    super(arg0);
  }

  public AgainstGuidelinesException(Throwable arg0) {
    super(arg0);
  }
}
