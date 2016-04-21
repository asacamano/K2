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
package com.google.k2.internal.operations;

import com.google.k2.api.Key;
import com.google.k2.api.SecurityProperty;
import com.google.k2.api.exceptions.CantReadException;
import com.google.k2.api.exceptions.CantWriteException;
import com.google.k2.api.exceptions.NoSuchMessageVersionException;
import com.google.k2.api.exceptions.UnimplementedPrimitiveException;
import com.google.k2.internal.common.K2Id;
import com.google.k2.internal.common.K2Objects;
import com.google.k2.internal.common.Util;
import com.google.k2.internal.keys.SymmetricCryptKey;
import com.google.k2.internal.messages.DestinationMessage;
import com.google.k2.internal.messages.MessageField;
import com.google.k2.internal.messages.Readable;
import com.google.k2.internal.messages.SourceMessage;
import com.google.k2.internal.primitives.CorePrimitives;
import com.google.k2.internal.primitives.Operation;
import com.google.k2.internal.primitives.SymmetricCryptPrimitive;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;

/**
 * This operation performs Symmetric decryption using an IV but no MAC of any sort.
 *
 * <p>It only provides {@link SecurityProperty#CONFIDENTIALITY}
 */
public class SymmetricCryptUnwrap implements Operation {

  private static final Set<SecurityProperty> PROVIDED_PROPERTIES =
      Util.arrayToSet(SecurityProperty.CONFIDENTIALITY);

  private final K2Objects k2Objects;

  public SymmetricCryptUnwrap(K2Objects k2Objects) {
    this.k2Objects = Util.checkNotNull(k2Objects, "k2Objects");
  }

  @Override
  public K2Id getOpId() {
    return CoreOperations.SYMMETRIC_CRYPT_UNWRAP;
  }

  @Override
  public Set<SecurityProperty> getProvidedProperties() {
    return PROVIDED_PROPERTIES;
  }

  @Override
  public List<MessageField> getMessageStructure(int version, Key key)
      throws NoSuchMessageVersionException {
    return SymmetricCryptMessage.getVersion(version, key);
  }

  @Override
  public boolean canUseKey(Key key) {
    return key instanceof SymmetricCryptKey;
  }

  @Override
  public void perform(Key key, SourceMessage inputMessage, DestinationMessage outputMessage)
      throws CantReadException, CantWriteException, UnimplementedPrimitiveException {
    if (!(key instanceof SymmetricCryptKey)) {
      throw new IllegalArgumentException(
          "Asked to perform SymmetricDecryption with the wrong kind of key");
    }
    SymmetricCryptKey symmetricCryptingKey = (SymmetricCryptKey) key;

    SymmetricCryptPrimitive cryptPrimitive = (SymmetricCryptPrimitive) k2Objects
        .getPrimitiveFactory().makeNewPrimitive(CorePrimitives.SYMMETRIC_CRYPT);

    // Now parse a SymmetricCryptMessage
    // TODO(asacamano@gmail.com) think about how to keep this in sync with the message format
    // defined in SymmetricCryptMessage

    // Start with the version
    int version = inputMessage.getNextInt();
    if (version > 0) {
      throw new CantReadException("The encrypted message is the wrong version. It must be < 0");
    }

    // Now read the initialization vector
    Readable ivPart = inputMessage.getNextReadable();
    ByteBuffer iv = ByteBuffer.allocate(symmetricCryptingKey.getIvSize() / 8);
    ivPart.writeTo(iv);

    // Now perform the decryption
    Readable ciphertextPart = inputMessage.getNextReadable();
    cryptPrimitive.crypt(symmetricCryptingKey, iv.array(), ciphertextPart,
        outputMessage.addVariableBytes("plaintext"));
  }
}
