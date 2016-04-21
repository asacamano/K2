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

import com.google.k2.internal.common.Util;
import com.google.k2.internal.primitives.Operation;

/**
 * MessageFields are how an {@link Operation} communicates it's message structure to a
 * {@link WrappedDataFormat} so that the {@link WrappedDataFormat} knows how to write and parse
 * the results of that {@link Operation}.
 *
 * @See {@link WrappedDataFormat}
 */
public class MessageField {

  private final MessageFieldType type;
  private final int sizeInBytes;
  private final String name;

  /**
   * Build a description of a field.
   *
   * @param name the name of the field
   * @param type the type of the field
   * @param sizeInBytes the size in bytes - ignored if type is
   * {@link MessageFieldType#BYTES_VARIABLE}
   */
  public MessageField(String name, MessageFieldType type, int sizeInBytes) {
    this.name = Util.checkNotNullOrEmpty(name, "name");
    this.type = Util.checkNotNull(type, "type");
    if (type == MessageFieldType.BYTES_VARIABLE) {
      this.sizeInBytes = -1;
    } else {
      if (sizeInBytes <= 0) {
        throw new IllegalArgumentException("Can't specify empty fields.");
      }
      this.sizeInBytes = sizeInBytes;
    }
  }

  /**
   * Returns the name of the field.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the type of the field.
   */
  public MessageFieldType getType() {
    return type;
  }

  /**
   * Returns the size of the field in bytes.
   *
   * <p>Returns -1 for fields of type {@link MessageFieldType#BYTES_VARIABLE}
   *
   * <p>Note that this is merely a minimum for the {@link WrappedDataFormat} - the
   * {@link WrappedDataFormat} may chose to make more space available if it decides to for some
   * reason (like boundary alignment, or readability).
   */
  public int getSizeInBytes() {
    return sizeInBytes;
  }
}
