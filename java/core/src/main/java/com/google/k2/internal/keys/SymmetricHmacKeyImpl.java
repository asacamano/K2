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
package com.google.k2.internal.keys;

import com.google.k2.api.KeyPurpose;
import com.google.k2.internal.common.Util;

import java.util.Collections;
import java.util.Set;

/**
 * This class represents a symmetric crypting key that also has an HMAC key.
 */
public class SymmetricHmacKeyImpl implements SymmetricCryptAndHmacKey {

  private final String id;
  private final byte[] keyMaterial;
  private final byte[] hmacKeyMaterial;
  private final Set<KeyPurpose> keyPurposes;

  public SymmetricHmacKeyImpl(
      String id, byte[] keyMaterial, byte[] hmacKeyMaterial, Set<KeyPurpose> keyPurposes) {
    this.id = Util.checkNotNullOrEmpty(id, "id");
    Util.checkNotNull(keyMaterial, "keyMaterial");
    this.keyPurposes = Collections.unmodifiableSet(Util.checkNotNull(keyPurposes, "keyPurposes"));
    // Prevent shenanigans - cheap peace(ish) of mind
    this.keyMaterial = new byte[keyMaterial.length];
    System.arraycopy(keyMaterial, 0, this.keyMaterial, 0, keyMaterial.length);
    this.hmacKeyMaterial = new byte[hmacKeyMaterial.length];
    System.arraycopy(hmacKeyMaterial, 0, this.hmacKeyMaterial, 0, hmacKeyMaterial.length);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Set<KeyPurpose> getKeyPurposes() {
    return keyPurposes;
  }

  @Override
  public int getKeySize() {
    return keyMaterial.length * 8;
  }

  @Override
  public byte[] getKeyMaterial() {
    // Prevent shenanigans - cheap peace(ish) of mind
    byte[] result = new byte[keyMaterial.length];
    System.arraycopy(keyMaterial, 0, result, 0, keyMaterial.length);
    return result;
  }

  @Override
  public int getIvSize() {
    return keyMaterial.length * 8;
  }

  @Override
  public byte[] getHmacKey() {
    return hmacKeyMaterial;
  }

  @Override
  public int getHmacSize() {
    return 128;
  }
}
