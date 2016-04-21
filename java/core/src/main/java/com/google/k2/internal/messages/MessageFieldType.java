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

/**
 * Identifies the kind of data in a field
 */
public enum MessageFieldType {
  /**
   * Indicates that a field contains an integer, either 1, 2, 4, or 8 bytes.
   *
   * <p>All integer values are signed in K2, since some languages (I'm looking at you, Java) don't
   * allow specification of unsigned values.
   *
   * <p>Note that the endian-ness of the storage depends entirely upon the
   * {@link WrappedDataFormat}s implementation.
   */
  INTEGER,

  /**
   * Indicates that a field contains a fixed length array of bytes.
   */
  BYTES_FIXED,

  /**
   * Indicates that a field contains bytes in an unknown length.
   *
   * <p>This is troublesome, because it's really inconvenient to not know the size of a field
   * when parsing the structure.  However, it is required to have this in order to be able to
   * do one-pass processing of data that is either unknown in length or too big to fit in memory.
   */
  BYTES_VARIABLE
}
