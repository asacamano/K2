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

import com.google.k2.api.Key;
import com.google.k2.api.exceptions.InsufficientSecurityException;

/**
 * This interface describes a key that can be used for symmetric encryption and decryption.
 */
public interface SymmetricCryptKey extends Key {

  /**
   * Returns the key size in bits
   *
   * @return the size of the key in bits
   */
  int getKeySize();

  /**
   * Returns the key size in bits
   *
   * @return the size of the key in bits
   *
   * @throws InsufficientSecurityException if the key is not available - such as a HARDWARE_BOUND
   * key.
   */
  byte[] getKeyMaterial() throws InsufficientSecurityException;

  /**
   * Returns the size of the initialization vector for this key, in bits.
   *
   * @return the IV size in bits
   */
  int getIvSize();
}
