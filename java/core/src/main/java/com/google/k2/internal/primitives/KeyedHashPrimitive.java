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
import com.google.k2.api.exceptions.MessageAuthenticationException;
import com.google.k2.internal.keys.HashKey;
import com.google.k2.internal.messages.Readable;
import com.google.k2.internal.messages.Writable;

/**
 * This interface defines a Primitive that performs keyed hashing.
 */
public interface KeyedHashPrimitive extends Primitive {

  /**
   * Sets the hash key for this primitive.
   *
   * <p>Note that calling this more than once could result in an exception.
   *
   * @param key the key to use
   */
  public void setKey(HashKey key);

  /**
   * Returns a {@link Writable} that will pass all data  through to the core destination, all while
   * accumulating a hash of the data based on the given hash key.
   *
   * @param core The place to which the data should be passed.
   *
   * @return a {@link Writable} that passes data to the core while computing the hash with the given
   * key
   */
  Writable wrap(Writable core);


  /**
   * Returns a {@link Writable} that will read all data  from to the core source, all while
   * accumulating a hash of the data based on the given hash key.
   *
   * @param core The place from which the data should be read.
   *
   * @return a {@link Readable} that gets data from the core while computing the hash with the given
   * key
   */
  Readable wrap(Readable core);


  /**
   * Returns the length of the hash, in bits.
   */
  int getHashLength();

  /**
   * Writes the accumulated hash to the given writable
   *
   * <p>The underlying primitive is free to implement this so that this method may only be called
   * once.
   *
   * @throws CantWriteException if there are problems writing the hash
   *
   */
  void writeHash(Writable hash) throws CantWriteException;


  /**
   * Checks the provided hash code against the computed hash code.
   *
   * @throws MessageAuthenticationException if providedHash does not match the computed one
   * @throws CantReadException if there a problems reading the underlying data
   */
  void verify(Readable providedHash)
      throws MessageAuthenticationException, CantReadException;
}
