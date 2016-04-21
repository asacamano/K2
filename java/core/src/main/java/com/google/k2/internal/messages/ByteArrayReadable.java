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
import com.google.k2.internal.common.Util;

import java.nio.ByteBuffer;

/**
 * A {@link Readable} that gets its data from a byte array.
 */
public class ByteArrayReadable implements Readable {

  private final ByteBuffer buffer;

  /**
   * Builds a {@link ByteArrayReadable} that wraps the given bytes.
   * @param bytes the bytes that will be read.
   */
  public ByteArrayReadable(byte[] bytes) {
    buffer = ByteBuffer.wrap(Util.checkNotNull(bytes, "bytes"));
  }

  @Override
  public boolean hasMoreToRead() {
    return buffer.position() < buffer.limit();
  }

  @Override
  public void writeTo(ByteBuffer dest) throws CantReadException {
    dest.put(buffer);
  }
}
