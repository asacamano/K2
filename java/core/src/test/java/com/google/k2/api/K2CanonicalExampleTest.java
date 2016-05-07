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
package com.google.k2.api;

import static com.google.k2.api.SecurityProperty.CONFIDENTIALITY;

import static org.junit.Assert.assertEquals;

import com.google.k2.api.exceptions.K2Exception;
import com.google.k2.internal.common.Util;
import com.google.k2.internal.messages.WrappedDataFormat;
import com.google.k2.testimpls.TestCryptoGuidelines;
import com.google.k2.testimpls.TestKeyStorage;
import com.google.k2.testimpls.TestPrimitiveFactory;
import com.google.k2.testimpls.TestWrappedDataFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the canonical K2 examples.
 *
 * It does so using test encryption, which just prepends "encrypt:" to the plaintext, and a test
 * {@link WrappedDataFormat} which makes it possible to treat the ciphertext as a string and see
 * exactly what is happening inside the message.
 */
public class K2CanonicalExampleTest {

  private static final String TEST_MESSAGE = "Mr. Watson — Come here — I want to see you.";

  // The expected ciphertext is version|iv|ciphertext
  private static final String EXPECTED_CIPHERTEXT =
      "0|00000000000000000000000000000000|encrypt:Mr. Watson — Come here — I want to see you.";

  K2 k2;
  String symmetricCryptKeyId;

  @Before
  public void setup() throws K2Exception {
    // Build a K2 instance
    // Note this is more complex than most users will need. that non-test instances could be as
    // simple as calling
    //
    // k2 = JceK2LBuilder.latestGuidelines().withFileStorage("/keydir").build();
    //
    // to use K2 with keys in keydir, with the default K2 serialization structures and the JCE
    // crypto operations.
    //
    k2 = K2.fromGuidelines(new TestCryptoGuidelines()).withKeyStorage(new TestKeyStorage())
        .withWrappedDataFormat(new TestWrappedDataFormat())
        .withPrimitiveFactory(new TestPrimitiveFactory()).build();

    symmetricCryptKeyId = TestKeyStorage.SYM_KEY_ID;
  }

  @Test
  public void testNaiveEncryptAndDecrypt_byteArrayData() throws K2Exception {
    // Get a wrapper and an unwrapper
    Wrapper encrypter = k2.getWrapper(symmetricCryptKeyId, CONFIDENTIALITY);
    Unwrapper decrypter = k2.getUnwrapper(symmetricCryptKeyId, CONFIDENTIALITY);

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
    Wrapper encrypter = k2.getWrapper(symmetricCryptKeyId, CONFIDENTIALITY);
    Unwrapper decrypter = k2.getUnwrapper(symmetricCryptKeyId, CONFIDENTIALITY);

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
