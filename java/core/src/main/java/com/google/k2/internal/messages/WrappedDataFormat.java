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

import com.google.k2.api.Key;
import com.google.k2.api.exceptions.CantReadException;
import com.google.k2.api.exceptions.CantWriteException;
import com.google.k2.internal.common.K2CollaborationParticipant;
import com.google.k2.internal.primitives.Operation;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides the API by which internal messages are converted to a particular output format.
 *
 * <p>Notes on implementing a WrappedDataFormat:
 * <ul>
 * <li>It's good practice to always make the first item in your format a version indicator, and
 * all operations
 * </ul>
 *
 * @see MessageField
 */
public interface WrappedDataFormat extends K2CollaborationParticipant {

  /**
   * Returns a writable {@link SourceMessage} that writes its data to the given
   * {@link OutputStream}, formatted by this WrappedDataFormat.
   * @param destination the destination to which data is written
   * @param operation the operation which defines the message structure
   * @param key the key being used - which often influences the structure of the message
   * @throws {@link CantWriteException} if any problem occurs building the {@link DestinationMessage}
   */
  DestinationMessage getWritableMessage(OutputStream destination, Operation operation, Key key)
      throws CantWriteException;

  /**
   * Returns a writable {@link SourceMessage} that writes its data to the given byte array,
   * formatted by this WrappedDataFormat.
   * @param destination the destination to which data is written
   * @param operation the operation which defines the message structure
   * @throws {@link CantWriteException} if any problem occurs building the {@link DestinationMessage}
   */
  DestinationMessage getWritableMessage(byte[] destination, Operation operation, Key key)
      throws CantWriteException;

  /**
   * Returns a Readable {@link SourceMessage} that reads its data from the given
   * {@link InputStream}, assuming the byte array was correctly formatted for this
   * wrappedDataFormat.
   * @param source the source data to parse
   * @param operation the operation which defines the message structure
   * @throws {@link CantReadException} if any problem occurs building the {@link SourceMessage}
   */
  SourceMessage getReadableMessage(InputStream source, Operation operation, Key key)
      throws CantReadException;

  /**
   * Returns a Readable {@link SourceMessage} that reads its data from the given byte array,
   * assuming the byte array was correctly formatted for this WrappedDataFormat and the given
   * operation.
   * @param source the source data to parse
   * @param operation the operation which defines the message structure
   * @throws {@link CantReadException} if any problem occurs building the {@link SourceMessage}
   */
  SourceMessage getReadableMessage(byte[] source, Operation operation, Key key)
      throws CantReadException;
}
