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
import com.google.k2.api.KeyPurpose;
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
import com.google.k2.internal.messages.SourceMessage;
import com.google.k2.internal.messages.Writable;
import com.google.k2.internal.primitives.CorePrimitives;
import com.google.k2.internal.primitives.Operation;
import com.google.k2.internal.primitives.SymmetricCryptPrimitive;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;

/**
 * This operation performs Symmetric encryption using an IV but no MAC of any sort.
 *
 * <p>It only provides {@link SecurityProperty#CONFIDENTIALITY}
 */
public class SymmetricCryptWrap implements Operation {

  private static final Set<SecurityProperty> PROVIDED_PROPERTIES =
      Util.arrayToSet(SecurityProperty.CONFIDENTIALITY);

  private final K2Objects k2Objects;

  public SymmetricCryptWrap(K2Objects k2Objects) {
    this.k2Objects = Util.checkNotNull(k2Objects, "k2Objects");
  }

  @Override
  public K2Id getOpId() {
    return CoreOperations.SYMMETRIC_CRYPT_WRAP;
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
    return key instanceof SymmetricCryptKey
        && key.getKeyPurposes().contains(KeyPurpose.ENCRYPT);
  }


  @Override
  public void perform(Key key, SourceMessage input, DestinationMessage output)
      throws CantReadException, CantWriteException, UnimplementedPrimitiveException {
    if (!(key instanceof SymmetricCryptKey)) {
      throw new IllegalArgumentException(
          "Asked to perform SymmetricDecryption with the wrong kind of key");
    }
    SymmetricCryptKey symmetricCryptingKey = (SymmetricCryptKey) key;

    SymmetricCryptPrimitive cryptPrimitive = (SymmetricCryptPrimitive) k2Objects
        .getPrimitiveFactory().makeNewPrimitive(CorePrimitives.SYMMETRIC_CRYPT);

    byte[] iv = cryptPrimitive.generateNewIv(symmetricCryptingKey);

    // Write the version of a SymmetricCrypted message
    output.addInt("msgVersion", 1, 0);

    // Now write the IV
    Writable ivPart = output.addFixedBytes("iv", iv.length);
    ivPart.fillFrom(ByteBuffer.wrap(iv));

    // Now write the encrypted data
    cryptPrimitive.crypt(symmetricCryptingKey, iv, input.getNextReadable(),
        output.addVariableBytes("ciphertext"));
  }
}
