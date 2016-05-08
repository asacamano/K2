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
package com.google.k2.internal.primitives;

import com.google.k2.api.Key;
import com.google.k2.api.SecurityProperty;
import com.google.k2.api.exceptions.CantReadException;
import com.google.k2.api.exceptions.CantWriteException;
import com.google.k2.api.exceptions.MessageAuthenticationException;
import com.google.k2.api.exceptions.NoSuchMessageVersionException;
import com.google.k2.api.exceptions.UnimplementedPrimitiveException;
import com.google.k2.internal.common.K2Id;
import com.google.k2.internal.messages.DestinationMessage;
import com.google.k2.internal.messages.MessageField;
import com.google.k2.internal.messages.MessageFieldType;
import com.google.k2.internal.messages.SourceMessage;
import com.google.k2.internal.messages.WrappedDataFormat;

import java.util.List;
import java.util.Set;

/**
 * An {@link Operation} performs simple or complex cryptographic operations given keys and
 * input data.
 *
 * <p>Specifically, operations describe two things:
 * <ol>
 * <li>composition of cryptographic primitives - for example, both encrypting and applying an HMAC
 * <li>structure of messages - for example a key ID prefix, an IV, ciphertext, and an HAMC
 * </ol>
 *
 * <p>For example, an operation might be as simple as calculating a hash, or it might be as complex
 * as doing session key generation, encrypting the key using an asymmetric key, signing a structured
 * message containing the encryption key id and the raw data, encrypting the structured message and
 * its signature with the session key, appending an HMAC to the entire message, and then prefixing
 * it with some signals specifying the format and type of the message.
 *
 * <p>In general, any method on an Operation should be safe to call from multiple threads.
 *
 * <p>An Operation must define (and follow) a message structure as a list of {@link MessageField}
 * objects. At most one {@link MessageField} may be of type {@link MessageFieldType#BYTES_VARIABLE}
 */
public interface Operation {

  /**
   * Returns a string that identifies this operation - for example "AsycSignAndSessionEncrypt".
   *
   * <p>It should be unique among other operation classes, and not include any data about the key
   * being used, or the data being operated on.
   *
   * <p>This is used in error messages, logging, monitoring, and so on.
   *
   * @return a {@link K2Id} that identifies this operation.
   */
  public K2Id getOpId();

  /**
   * Returns the set of {@link SecurityProperty}s provided by this operation.
   * @return the set of properties.
   */
  public Set<SecurityProperty> getProvidedProperties();

  /**
   * Returns a List{@link} of {@link MessageField} describing how the results of the operation will
   * be passed to the {@link WrappedDataFormat}, or should be parsed by a {@link WrappedDataFormat}.
   *
   * <p>Since it is possible for new research to require a new message structure to guarantee the
   * same properties, all Operations must support versioned {@link MessageField}s - meaning the
   * first element of every message structure should be a version identifier.
   *
   * @param version the version number being requested
   *
   * @return the {@link List} of {@link MessageField}s used by this operation for the given version
   *
   * @throws NoSuchMessageVersionException if the requested version is not defined
   */
  public List<MessageField> getMessageStructure(int version, Key key)
      throws NoSuchMessageVersionException;

  /**
   * Returns true if the key is suitable to be used in this operation. IN general, this has to do
   * with the key purpose, and the interfaces the key object implements.
   *
   * @param key The key to test.
   *
   * @return true if the key can be used, false otherwise.
   */
  public boolean canUseKey(Key key);

  /**
   * Using the given key, transform the input to the output according to the specifics of this
   * operation.
   *
   * @param key The key to use for this operation
   * @param input The input to the operation - where the data comes from
   * @param output The output to the operation - where to write the resulting data
   * @throws CantReadException if there are errors reading from the input
   * @throws CantWriteException if there are errors writing to the output
   * @throws UnimplementedPrimitiveException if a {@link Primitive} required by this Operation is
   * not available.
   * @throws MessageAuthenticationException if there were problems verifying the message
   */
  public void perform(Key key, SourceMessage input, DestinationMessage output)
      throws CantReadException, CantWriteException, UnimplementedPrimitiveException,
      MessageAuthenticationException;
}
