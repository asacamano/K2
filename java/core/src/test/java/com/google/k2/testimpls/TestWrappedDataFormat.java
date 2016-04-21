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

import com.google.k2.api.Key;
import com.google.k2.api.exceptions.CantReadException;
import com.google.k2.api.exceptions.NoSuchMessageVersionException;
import com.google.k2.internal.common.K2Objects;
import com.google.k2.internal.common.Util;
import com.google.k2.internal.messages.DestinationMessage;
import com.google.k2.internal.messages.MessageField;
import com.google.k2.internal.messages.SourceMessage;
import com.google.k2.internal.messages.WrappedDataFormat;
import com.google.k2.internal.primitives.Operation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link WrappedDataFormat} for use in tests that makes the underlying data quite easy to
 * read.
 */
public class TestWrappedDataFormat implements WrappedDataFormat {

  @Override
  public void setK2Objects(K2Objects k2Objects) {
    // Not currently used
  }

  @Override
  public DestinationMessage getWritableMessage(OutputStream destination, Operation operation,
      Key key) {
    // Note that writing for this format is simple - we just write the data, we don't need the
    // operation to define the structure
    return new TestStreamingInternalWritable(destination);
  }

  @Override
  public DestinationMessage getWritableMessage(byte[] destination, Operation operation, Key key) {
    // Note that writing for this format is simple - we just write the data, we don't need the
    // operation to define the structure
    return new TestByteArrayInternalWritable(destination);
  }

  @Override
  public SourceMessage getReadableMessage(InputStream source, Operation operation, Key key)
      throws CantReadException {
    // Step 1, read the version - so we know which version to get the rest of the structure from
    // don't worry about more than 255 version in the test context (ROFL)
    try {
      int version = source.read();
      List<MessageField> structure = operation.getMessageStructure(version, key);
    } catch (IOException e) {
      throw new CantReadException("Can't read from the intput stream", e);
    } catch (NoSuchMessageVersionException e) {
      throw new CantReadException("Message parse error", e);
    }

    return null;
  }

  @Override
  public SourceMessage getReadableMessage(byte[] source, Operation operation, Key key)
      throws CantReadException {
    // Split the message into the pipe delimited bits
    String[] parts = new String(source, Util.UTF_8).split("\\|");

    // Get the version number, the message structure, and make sure there are the right number
    // of parts.
    int version = Integer.parseInt(parts[0].toString());
    List<MessageField> structure;
    try {
      structure = operation.getMessageStructure(version, key);
    } catch (NoSuchMessageVersionException e) {
      throw new CantReadException("Invalid message format", e);
    }
    if (structure.size() != parts.length) {
      throw new CantReadException(
          "Expected " + structure.size() + " fields, but got " + parts.length);
    }

    // walk the message structure and build a list of objects for the TestPreparsedInputReader
    List<Object> objects = new ArrayList<Object>();
    for (int i = 0; i < structure.size(); i++) {
      MessageField field = structure.get(i);
      switch (field.getType()) {
        case INTEGER:
          try {
            objects.add(Integer.parseInt(parts[i]));
          } catch (NumberFormatException e) {
            throw new CantReadException("Message parse error", e);
          }
          break;
        case BYTES_FIXED:
          objects.add(parts[i]);
          break;
        case BYTES_VARIABLE:
          // Because of this format, it's not actually variable here
          objects.add(parts[i]);
          break;
      }
    }

    return new TestPreparsedReadable(objects, structure);
  }
}
