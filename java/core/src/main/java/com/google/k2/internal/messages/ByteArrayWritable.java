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
import com.google.k2.internal.common.Util;

import java.nio.ByteBuffer;

/**
 * A {@link Writable} that writes its data to a byte array or a {@link ByteBuffer}.
 */
public class ByteArrayWritable implements Writable {

  private final ByteBuffer buffer;

  /**
   * Builds a {@link ByteArrayWritable} that wraps the given bytes.
   * @param bytes the bytes that will be read.
   */
  public ByteArrayWritable(byte[] bytes) {
    buffer = ByteBuffer.wrap(Util.checkNotNull(bytes, "bytes"));
  }

  /**
   * Builds a {@link ByteArrayWritable} that wraps the given bytes.
   * @param bytes the bytes that will be read.
   */
  public ByteArrayWritable(ByteBuffer buffer) {
    this.buffer = Util.checkNotNull(buffer, "bytes");
  }

  @Override
  public void fillFrom(ByteBuffer source) throws CantWriteException {
    if (source.limit() - source.position() > buffer.limit() - buffer.position()) {
      throw new CantWriteException("Not enough room in the destination byte array");
    }
    buffer.put(buffer);
  }
}
