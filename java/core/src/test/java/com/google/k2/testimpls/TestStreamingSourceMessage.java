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

import com.google.k2.api.exceptions.CantReadException;
import com.google.k2.internal.common.Util;
import com.google.k2.internal.messages.ByteArrayReadable;
import com.google.k2.internal.messages.MessageField;
import com.google.k2.internal.messages.MessageFieldType;
import com.google.k2.internal.messages.Readable;
import com.google.k2.internal.messages.SourceMessage;
import com.google.k2.internal.messages.StreamReadable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

/**
 * An {@link SourceMessage} that parses a stream data to populate the Readables.
 *
 * <p>Note that - following the TestWrappedDataFormat - all fixed bytes fields are hex encoded,
 * and all variable byte fields are just kept as strings (UTF_8 encoded).
 */
public class TestStreamingSourceMessage implements SourceMessage {

  private final InputStream source;
  private final Iterator<MessageField> fields;
  private final int messageFormatVersion;

  public TestStreamingSourceMessage(InputStream source, List<MessageField> fields,
      int messageFormatVersion) {
    this.fields = fields.iterator();
    this.source = Util.checkNotNull(source, "source");
    // Skip over the version
    this.fields.next();
    this.messageFormatVersion = messageFormatVersion;
  }

  @Override
  public int getMessageFormatVersion() {
    return messageFormatVersion;
  }

  @Override
  public int getNextInt() throws CantReadException {
    MessageField field = fields.next();
    if (field.getType() != MessageFieldType.INTEGER) {
      throw new CantReadException("Asked for integer but have " + field.getType());
    }
    StringBuffer intValue = readToNextDelim();
    return Integer.parseInt(intValue.toString());
  }

  @Override
  public Readable getNextReadable() throws CantReadException {
    MessageField field = fields.next();
    if (field.getType() == MessageFieldType.BYTES_FIXED) {
      StringBuffer hex = readToNextDelim();
      return new ByteArrayReadable(TestUtil.hexToByteArray(hex.toString()));
    } else if (field.getType() == MessageFieldType.BYTES_VARIABLE) {
      return new StreamReadable(new DelimiterStoppingInputStream(source));
    } else {
      throw new CantReadException(
          "Unexpectedly asked for bytes when expecting to be asked for " + field.getType());
    }
  }

  private StringBuffer readToNextDelim() throws CantReadException {
    StringBuffer intValue = new StringBuffer();
    try {
      while (true) {
        int c = source.read();
        if (c == -1 || c == '|') {
          break;
        }
        intValue.append((char)(c & 0x00FF));
      }
    } catch (IOException e) {
      throw new CantReadException("Error reading underlying stream", e);
    }
    return intValue;
  }

  private static class DelimiterStoppingInputStream extends InputStream {
    InputStream source;
    boolean done;

    public DelimiterStoppingInputStream(InputStream source) {
      this.source = source;
      done = false;
    }

    @Override
    public int read() throws IOException {
      if (done) {
        return -1;
      }
      int read = source.read();
      if (read == '|') {
        done = true;
        return -1;
      }
      return read;
    }

    @Override
    public int available() throws IOException {
      return source.available() > 0 && !done ? 1 : 0;
    }
  }
}
