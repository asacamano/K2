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

import com.google.k2.api.exceptions.CantReadException;
import com.google.k2.internal.primitives.Operation;

import java.nio.ByteBuffer;

/**
 * A readable {@link InternalMessagePart} - this is how all {@link Operation}s read data.
 */
public interface Readable {
  /**
   * Returns true is this part has more data to read.
   * @throws CantReadException
   */
  public boolean hasMoreToRead() throws CantReadException;

  /**
   * Writes as much data as possible to the ByteBuffer, if this is of type
   * {@link InternalMessagePartType#BYTES}
   */
  public void writeTo(ByteBuffer dest) throws CantReadException;
}
