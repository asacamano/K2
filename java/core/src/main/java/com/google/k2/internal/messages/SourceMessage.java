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
import com.google.k2.internal.primitives.Operation;

/**
 * An InternalReadable is how {@link Operation}s get data to work on.
 *
 * <p>An InternalWritable is logically an ordered list of fields. Names used by
 * {@link DestinationMessage} are ignored in {@link SourceMessage} - the order of fields is what
 * is important, and all fields are required to be included. The API is very restricted in order to
 * preserve the ability to stream data.
 *
 * @see {@link WrappedDataFormat} for how formatted data is converted to a InternalReadable.
 * @see {@link DestinationMessage} for how data is written
 */
public interface SourceMessage {

  /**
   * Returns the message format version.
   *
   * This is often the first field, and can be used by {@link WrappedDataFormat} implementations
   * to define how to parse the message.  However, it is also needed by the Operation to validate
   * that the operation is getting the right kind of message, so SourceMessage implementations need
   * to keep it around and provide it on request.
   */
  public int getMessageFormatVersion();

  /**
   * Returns the next part as an int, if there is one, otherwise throws.
   *
   * @return the int value of the next part
   *
   * @throws CantReadException if anything goes wrong
   */
  public int getNextInt() throws CantReadException;

  /**
   * Returns the next part if there is one available, otherwise throws
   *
   * @return the next part
   *
   * @throws CantReadException if anything goes wrong
   */
  public Readable getNextReadable() throws CantReadException;

}
