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
package com.google.k2.internal.messages;

import com.google.k2.api.exceptions.CantWriteException;
import com.google.k2.internal.primitives.Operation;

/**
 * An InternalWritable is where {@link Operation}s output their results.
 *
 * <p>An InternalWritable is logically an ordered list of fields. The field names are a convenience,
 * so that a {@link WrappedDataFormat} may include the names in is representation. All fields are
 * required. The API is very restricted in order to preserve the ability to stream data.
 *
 * @see {@link WrappedDataFormat} for how InternalWritables are converted to formatted data.
 * @see {@link SourceMessage} for how data is read
 */
public interface DestinationMessage {

  /**
   * Writes an integer to the output.
   *
   * @param name The name of this field
   * @param size The size of this int, 1, 2, 4, or 8 bytes. All ints are signed.
   * @param value the value be written
   *
   * @throws CantWriteException if anything goes wrong
   */
  public void addInt(String name, int size, int value) throws CantWriteException;

  /**
   * Gets a new {@link Writable} that will write a fixed size byte field to the underlying
   * destination
   *
   * @param name The name of this field
   * @param size The number of bytes - must be strictly greater than 0
   *
   * @throws CantWriteException if anything goes wrong
   */
  public Writable addFixedBytes(String name, int size) throws CantWriteException;

  /**
   * Gets a new {@link Writable} will write an unlimited size byte field to the underlying
   * destination
   *
   * @param name The name of this field
   *
   * @throws CantWriteException if anything goes wrong
   */
  public Writable addVariableBytes(String name) throws CantWriteException;

}
