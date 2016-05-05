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

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A PlaintextReadable is a common {@link SourceMessage} that just handles one field, a plaintext
 * field.
 */
public class PlaintextReadable implements SourceMessage {

  private final InputStream sourceStream;
  private final byte[] sourceBytes;
  private final AtomicBoolean moreToDo = new AtomicBoolean(true);

  public PlaintextReadable(byte[] source) {
    sourceStream = null;
    sourceBytes = Util.checkNotNull(source, "source");
  }

  public PlaintextReadable(InputStream source) {
    sourceStream = Util.checkNotNull(source, "source");
    sourceBytes = null;
  }

  @Override
  public int getNextInt() throws CantReadException {
    throw new CantReadException("Can't read structured data from the plaintext");
  }

  @Override

  public Readable getNextReadable() throws CantReadException {
    if (moreToDo.compareAndSet(true, false)) {
      if (sourceStream != null) {
        return new StreamReadable(sourceStream);
      } else {
        return new ByteArrayReadable(sourceBytes);
      }
    } else {
      throw new CantReadException("Plaintext only has one part, can't call getNextReadable more "
          + "than once on PlaintextWritable");
    }
  }
}
