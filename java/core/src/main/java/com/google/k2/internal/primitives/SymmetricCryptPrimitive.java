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
package com.google.k2.internal.primitives;

import com.google.k2.api.exceptions.CantReadException;
import com.google.k2.api.exceptions.CantWriteException;
import com.google.k2.internal.keys.SymmetricCryptKey;
import com.google.k2.internal.messages.Readable;
import com.google.k2.internal.messages.Writable;

/**
 * This interface defines a Primitive that performs symmetric encryption with the given IV.
 */
public interface SymmetricCryptPrimitive extends Primitive {

  /**
   * Crypts (either encrypts or decrypts) the data in input (using the given IV) and writes the
   * result to output.
   *
   * @param iv The initialization vector
   * @param input the source of data to be crypted
   * @param output the destination of crypted data
   * @throws CantWriteException if there are any problems writing data
   * @throws CantReadException if there are any problems reading data
   */
  void crypt(SymmetricCryptKey key, byte[] iv, Readable input, Writable output)
      throws CantReadException, CantWriteException;

  /**
   * Generates a new initialization vector for the specific key.
   *
   * @return a new IV as bytes
   */
  byte[] generateNewIv(SymmetricCryptKey key);

}
