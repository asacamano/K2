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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * A {@link Readable} that gets its data from an {@link InputStream}.
 */
public class StreamReadable implements Readable {

  private final InputStream stream;

  public StreamReadable(InputStream stream) {
    this.stream = Util.checkNotNull(stream, "stream");
  }

  @Override
  public boolean hasMoreToRead() throws CantReadException {
    // TODO(asacamano@gmail.com) This is a short term hack - ultimately, we need a pushback
    // stream, and to try to read one byte, and to include somes kind of timeout.  For now, this
    // works for local streams, but may behave oddly when using remote streams.
    try {
      return stream.available() > 0;
    } catch (IOException e) {
      throw new CantReadException("Can't read from input stream", e);
    }
  }

  @Override
  public void writeTo(ByteBuffer dest) throws CantReadException {
    try {
      int bytesRead = stream.read(dest.array(), dest.position(), dest.limit() - dest.position());
      dest.position(dest.position() + bytesRead);
    } catch (IOException e) {
      throw new CantReadException("Can't read from input stream", e);
    }
  }
}
