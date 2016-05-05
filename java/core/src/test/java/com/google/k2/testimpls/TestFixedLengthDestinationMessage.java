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
package com.google.k2.testimpls;

import com.google.k2.api.exceptions.CantWriteException;
import com.google.k2.internal.common.Util;
import com.google.k2.internal.messages.ByteArrayWritable;
import com.google.k2.internal.messages.DestinationMessage;
import com.google.k2.internal.messages.SourceMessage;
import com.google.k2.internal.messages.Writable;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * This is a test writable {@link SourceMessage} that formats data according to the test
 * approach, pipe delimited, with everything else in decimal and ASCII as much as possible, and only
 * really supporting the encryption of String data.
 */
public class TestFixedLengthDestinationMessage implements DestinationMessage {

  private final ByteBuffer destination;
  private boolean first = true;

  public TestFixedLengthDestinationMessage(byte[] destination) {
    this.destination = ByteBuffer.wrap(Util.checkNotNull(destination, "destination"));
  }

  @Override
  public void addInt(String name, int size, int value) throws CantWriteException {
    try {
      handleFirst();
      destination.put(Integer.toString(value).getBytes(Util.UTF_8));
    } catch (IOException e) {
      throw new CantWriteException("Error writing int value", e);
    }
  }

  @Override
  public Writable addFixedBytes(String name, int size) throws CantWriteException {
    return new HexEncodingByteBufferWritable(destination);
  }

  @Override
  public Writable addVariableBytes(String name) throws CantWriteException {
    try {
      handleFirst();
    } catch (IOException e) {
      throw new CantWriteException("Error writing separator", e);
    }
    return new ByteArrayWritable(destination);
  }

  private void handleFirst() throws IOException {
    if (first) {
      first = false;
    } else {
      destination.put((byte) '|');
    }
  }
}
