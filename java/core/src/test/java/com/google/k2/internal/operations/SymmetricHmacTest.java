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

import static com.google.k2.api.SecurityProperty.CONFIDENTIALITY;
import static com.google.k2.api.SecurityProperty.SOURCE_AUTHENTICITY;

import static org.junit.Assert.assertEquals;

import com.google.k2.api.K2;
import com.google.k2.api.Unwrapper;
import com.google.k2.api.Wrapper;
import com.google.k2.api.exceptions.K2Exception;
import com.google.k2.internal.common.Util;
import com.google.k2.testimpls.TestCryptoGuidelines;
import com.google.k2.testimpls.TestKeyStorage;
import com.google.k2.testimpls.TestPrimitiveFactory;
import com.google.k2.testimpls.TestWrappedDataFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link SymmetricCryptAndHmacWrap} and {@link SymmetricCryptAndHmacUnwrap}
 */
public class SymmetricHmacTest {

  private static final String TEST_MESSAGE = "Mr. Watson - Come here - I want to see you.";
  private static final String EXPECTED_RAW_CIPHERTEXT = "encrypt:" + TEST_MESSAGE;

  private static final String EXPECTED_HASH =
      String.format("%016X", EXPECTED_RAW_CIPHERTEXT.length());
  // The expected ciphertext is version|iv|ciphertext|hmac
  private static final String EXPECTED_CIPHERTEXT =
      "0|00000000000000000000000000000000|"+ EXPECTED_RAW_CIPHERTEXT +
      "|" + EXPECTED_HASH + "0000000000000000";

  K2 k2;
  String keyId;

  @Before
  public void setup() throws K2Exception {
    k2 = K2.fromGuidelines(new TestCryptoGuidelines()).withKeyStorage(new TestKeyStorage())
        .withWrappedDataFormat(new TestWrappedDataFormat())
        .withPrimitiveFactory(new TestPrimitiveFactory()).build();

    keyId = TestKeyStorage.SYM_HMAC_KEY_ID;
  }

  @Test
  public void testNaiveEncryptAndDecrypt_byteArrayData() throws K2Exception {
    // Get a wrapper and an unwrapper
    Wrapper encrypter = k2.getWrapper(keyId, CONFIDENTIALITY, SOURCE_AUTHENTICITY);
    Unwrapper decrypter = k2.getUnwrapper(keyId, CONFIDENTIALITY, SOURCE_AUTHENTICITY);

    // Example encryption call
    byte[] ciphertext = encrypter.wrap(asBytes(TEST_MESSAGE));

    // Test the results of the (quite fake) encryption
    assertEquals(EXPECTED_CIPHERTEXT, asString(ciphertext));

    // Test the results of the (quite fake) decryption
    String roundTripPlaintext = asString(decrypter.unwrap(ciphertext));
    assertEquals(TEST_MESSAGE, roundTripPlaintext);
  }

  @Test
  public void testNaiveEncryptAndDecrypt_streamData() throws K2Exception {
    // Get a wrapper and an unwrapper
    Wrapper encrypter = k2.getWrapper(keyId, CONFIDENTIALITY, SOURCE_AUTHENTICITY);
    Unwrapper decrypter = k2.getUnwrapper(keyId, CONFIDENTIALITY, SOURCE_AUTHENTICITY);

    ByteArrayInputStream sourcePlaintext = asByteStream(TEST_MESSAGE);
    ByteArrayOutputStream desinationCiphertext = new ByteArrayOutputStream();

    // Example encryption call
    encrypter.wrap(sourcePlaintext, desinationCiphertext);

    // Test the results of the (quite fake) encryption
    assertEquals(EXPECTED_CIPHERTEXT, asString(desinationCiphertext));

    ByteArrayInputStream sourceCiphertext =
        new ByteArrayInputStream(desinationCiphertext.toByteArray());
    ByteArrayOutputStream desinationPlaintext = new ByteArrayOutputStream();

    // Example decryption call
    decrypter.unwrap(sourceCiphertext, desinationPlaintext);

    // Test the results of the (quite fake) decryption
    assertEquals(TEST_MESSAGE, asString(desinationPlaintext));
  }

  // Utility string to UFT8 bytes method
  private byte[] asBytes(String msg) {
    return msg.getBytes(Util.UTF_8);
  }

  // Utility UTF8 bytes to String method
  private String asString(byte[] msg) {
    return new String(msg, Util.UTF_8);
  }

  // Utility String to UTF8 ByteArrayInputStream
  private ByteArrayInputStream asByteStream(String msg) {
    return new ByteArrayInputStream(asBytes(msg));
  }

  // Utility UTF8 bytes to String
  private String asString(ByteArrayOutputStream msg) {
    return new String(msg.toByteArray(), Util.UTF_8);
  }
}
