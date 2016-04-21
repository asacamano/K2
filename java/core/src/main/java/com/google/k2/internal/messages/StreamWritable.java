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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * A {@link Writable} that writes its data to an {@link OutputStream}.
 */
public class StreamWritable implements Writable {

  private final OutputStream stream;

  /**
   * Builds a {@link StreamWritable} that wraps the given bytes.
   * @param bytes the bytes that will be read.
   */
  public StreamWritable(OutputStream stream) {
    this.stream = Util.checkNotNull(stream, "stream");
  }

  @Override
  public void fillFrom(ByteBuffer source) throws CantWriteException {
    // See http://stackoverflow.com/questions/579600 for why we just do it this way
    byte[] bytes = new byte[source.limit() - source.position()];
    source.get(bytes);
    try {
      stream.write(bytes);
    } catch (IOException e) {
      throw new CantWriteException("Error writing to stream", e);
    }
  }
}
