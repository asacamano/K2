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

import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A PlaintextWritable is a common {@link DestinationMessage} that just handles one field, a plaintext
 * field.
 */
public class PlaintextDestinationMessage implements DestinationMessage {

  private final OutputStream destinationStream;
  private final byte[] destinationBytes;
  private final AtomicBoolean moreToDo = new AtomicBoolean(true);

  public PlaintextDestinationMessage(byte[] destination) {
    destinationStream = null;
    destinationBytes = Util.checkNotNull(destination, "destination");
  }

  public PlaintextDestinationMessage(OutputStream destination) {
    destinationStream = Util.checkNotNull(destination, "destination");
    destinationBytes = null;
  }

  @Override
  public void addInt(String name, int size, int value) throws CantWriteException {
    throw new CantWriteException("Can't write structured data to the plaintext");
  }

  @Override
  public Writable addFixedBytes(String name, int size) throws CantWriteException {
    throw new CantWriteException("Can't write structured data to the plaintext");
  }

  @Override
  public Writable addVariableBytes(String name) throws CantWriteException {
    if (moreToDo.compareAndSet(true, false)) {
      if (destinationStream != null) {
        return new StreamWritable(destinationStream);
      } else {
        return new ByteArrayWritable(destinationBytes);
      }
    } else {
      throw new CantWriteException("Plaintext only has one part, can't call addWritable more "
          + "than once on PlaintextWritable");
    }
  }
}
