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

import com.google.k2.api.Key;
import com.google.k2.api.KeyPurpose;
import com.google.k2.api.KeyStorage;
import com.google.k2.api.exceptions.NoSuchKeyException;
import com.google.k2.internal.common.K2Objects;
import com.google.k2.internal.common.Util;
import com.google.k2.internal.keys.PlainSymmetricKeyImpl;
import com.google.k2.internal.keys.SymmetricHmacKeyImpl;

/**
 * A {@link KeyStorage} for use in tests.
 */
public class TestKeyStorage implements KeyStorage {

  /**
   * The ID of an AES key.
   */
  public static final String SYM_KEY_ID = "SYM_KEY_1";
  public static final byte[] SYM_KEY = TestUtil.hexToByteArray("00112233445566778899AABBCCDDEEFF");

  public static final String SYM_HMAC_KEY_ID = "SYM_HMAC_KEY_1";
  public static final byte[] SYM_HMAC_KEY =
      TestUtil.hexToByteArray("00112233445566778899AABBCCDDEEFF");


  @Override
  public void setK2Objects(K2Objects k2Objects) {
    // Not currently needed
  }

  @Override
  public Key getKey(String keyId) throws NoSuchKeyException {
    if (SYM_KEY_ID.equals(keyId)) {
      return new PlainSymmetricKeyImpl(
          SYM_KEY_ID, SYM_KEY, Util.arrayToSet(KeyPurpose.DECRYPT, KeyPurpose.ENCRYPT));
    } else if (SYM_HMAC_KEY_ID.equals(keyId)) {
        return new SymmetricHmacKeyImpl(SYM_KEY_ID, SYM_KEY, SYM_HMAC_KEY,
            Util.arrayToSet(KeyPurpose.DECRYPT, KeyPurpose.ENCRYPT));
    }
    throw new NoSuchKeyException("No key found with ID \"" + keyId + "\"");
  }
}
