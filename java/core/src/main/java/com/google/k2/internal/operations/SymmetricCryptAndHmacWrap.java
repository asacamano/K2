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
import com.google.k2.internal.keys.SymmetricCryptAndHmacKey;
import com.google.k2.internal.messages.DestinationMessage;
import com.google.k2.internal.messages.MessageField;
import com.google.k2.internal.messages.SourceMessage;
import com.google.k2.internal.messages.Writable;
import com.google.k2.internal.primitives.CorePrimitives;
import com.google.k2.internal.primitives.KeyedHashPrimitive;
import com.google.k2.internal.primitives.Operation;
import com.google.k2.internal.primitives.SymmetricCryptPrimitive;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;

/**
 * This operation performs Symmetric encryption using an IV and a keyed HMAC.
 *
 * <p>It only provides {@link SecurityProperty#CONFIDENTIALITY} and
 * {@link SecurityProperty#SOURCE_AUTHENTICITY}
 */
public class SymmetricCryptAndHmacWrap implements Operation {

  private static final Set<SecurityProperty> PROVIDED_PROPERTIES =
      Util.arrayToSet(SecurityProperty.CONFIDENTIALITY, SecurityProperty.SOURCE_AUTHENTICITY);

  private final K2Objects k2Objects;

  public SymmetricCryptAndHmacWrap(K2Objects k2Objects) {
    this.k2Objects = Util.checkNotNull(k2Objects, "k2Objects");
  }

  @Override
  public K2Id getOpId() {
    return CoreOperations.SYMMETRIC_CRYPT_AND_HMAC_WRAP;
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
    return key instanceof SymmetricCryptAndHmacKey
        && key.getKeyPurposes().contains(KeyPurpose.ENCRYPT);
  }


  @Override
  public void perform(Key key, SourceMessage input, DestinationMessage output)
      throws CantReadException, CantWriteException, UnimplementedPrimitiveException {
    if (!(key instanceof SymmetricCryptAndHmacKey)) {
      throw new IllegalArgumentException(
          "Asked to perform SymmetricDecryption with the wrong kind of key");
    }
    SymmetricCryptAndHmacKey symmetricCryptingKey = (SymmetricCryptAndHmacKey) key;

    // Build the primitives
    SymmetricCryptPrimitive cryptPrimitive = (SymmetricCryptPrimitive)
        k2Objects.getPrimitiveFactory().makeNewPrimitive(CorePrimitives.SYMMETRIC_CRYPT);
    KeyedHashPrimitive keyedHashPrimitive = (KeyedHashPrimitive)
        k2Objects.getPrimitiveFactory().makeNewPrimitive(CorePrimitives.KEYED_HASH);
    keyedHashPrimitive.setKey(symmetricCryptingKey);

    // Write the version of a SymmetricCryptAndHmac message
    output.addInt("msgVersion", 1, 0);

    // Now generate and write write the IV
    byte[] iv = cryptPrimitive.generateNewIv(symmetricCryptingKey);
    Writable ivPart = output.addFixedBytes("iv", iv.length);
    ivPart.fillFrom(ByteBuffer.wrap(iv));

    // Now write the encrypted data
    Writable cipherText = output.addVariableBytes("ciphertext");
    Writable hashCollector = keyedHashPrimitive.wrap(cipherText);
    cryptPrimitive.crypt(symmetricCryptingKey, iv, input.getNextReadable(), hashCollector);

    // Now write the hash signature
    Writable hashPart = output.addFixedBytes("hmac", keyedHashPrimitive.getHashLength());
    keyedHashPrimitive.writeHash(hashPart);
  }
}
