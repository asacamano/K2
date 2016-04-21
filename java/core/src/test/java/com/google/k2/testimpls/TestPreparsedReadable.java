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

import java.util.Iterator;
import java.util.List;

/**
 * An {@link SourceMessage} that uses preparsed data to populate the Readables.
 *
 * <p>Note that - following the TestWrappedDataFormat - all fixed bytes fields are hex encoded,
 * and all variable byte fields are just kept as strings (UTF_8 encoded).
 */
public class TestPreparsedReadable implements SourceMessage {

  private final Iterator<Object> objects;
  private final Iterator<MessageField> fields;

  public TestPreparsedReadable(List<Object> objects, List<MessageField> fields) {
    this.objects = objects.iterator();
    this.fields = fields.iterator();
  }

  @Override
  public int getNextInt() throws CantReadException {
    MessageField field = fields.next();
    if (field.getType() != MessageFieldType.INTEGER) {
      throw new CantReadException("Expected integer, but got " + objects.next());
    }
    return (Integer) objects.next();
  }

  @Override
  public Readable getNextReadable() throws CantReadException {
    MessageField field = fields.next();
    if (field.getType() == MessageFieldType.BYTES_FIXED) {
      return new ByteArrayReadable(TestUtil.hexToByteArray((String) objects.next()));
    } else if (field.getType() == MessageFieldType.BYTES_VARIABLE) {
      return new ByteArrayReadable(((String) objects.next()).getBytes(Util.UTF_8));
    } else {
      throw new CantReadException(
          "Unexpectedly asked for bytes when expecting to be asked for " + field.getType());
    }
  }
}
