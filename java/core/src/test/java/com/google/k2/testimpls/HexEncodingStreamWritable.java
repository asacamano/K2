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
import com.google.k2.internal.messages.Writable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * A test Writable that encodes bytes as hex, and writes them as a string in UTF-8.
 */
public class HexEncodingStreamWritable implements Writable {

  private final OutputStream destination;

  public HexEncodingStreamWritable(OutputStream destination) {
    this.destination = Util.checkNotNull(destination, "destination");
  }

  @Override
  public void fillFrom(ByteBuffer buffer) throws CantWriteException {
    byte[] temporary = new byte[buffer.limit() - buffer.position()];
    buffer.get(temporary);
    String hex = TestUtil.byteArrayToHex(temporary);
    try {
      destination.write(hex.getBytes(Util.UTF_8));
    } catch (IOException e) {
      throw new CantWriteException(e);
    }
  }
}
