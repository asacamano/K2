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
package com.google.k2.internal.keys;

import com.google.k2.api.exceptions.InsufficientSecurityException;

/**
 * This interface describes a key that can be used for performing keyed hash calculation and
 * verification.
 */
public interface HashKey extends SymmetricCryptKey {

  /**
   * Returns the HMAC key material as an array of bytes
   *
   * @return the key material
   *
   * @throws InsufficientSecurityException if the key is not available - such as a HARDWARE_BOUND
   * key.
   */
  byte[] getHmacKey()  throws InsufficientSecurityException;

  /**
   * Returns the HMAC signature size in bits
   *
   * @return the size of the HMAC in bits
   */
  int getHmacSize();
}
