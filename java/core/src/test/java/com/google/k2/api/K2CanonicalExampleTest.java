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

import static org.junit.Assert.assertEquals;

import com.google.k2.api.K2;
import com.google.k2.api.SecurityProperty;
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
import org.junit.Ignore;
import org.junit.Test;

/**
 * This class tests the canonical K2 examples.
 *
 */
public class K2CanonicalExampleTest {

  private static final String TEST_MESSAGE = "Mr. Watson — Come here — I want to see you.";
  private static final String EXPECTED_CIPHERTEXT =
      "0|00000000000000000000000000000000|encrypt:Mr. Watson — Come here — I want to see you.";

  K2 k2;

  @Before
  public void setup() throws K2Exception {
    // Build a K2 instance
    // Note this is more complex than most users will need. that non-test instances could be as
    // simple as calling
    //
    // k2 = JceK2LBuilder.latestGuidelines().withFileStorage("/keydir").build();
    //
    // To use keys in keydir, with the default K2 serialization structures and the JCE crypto
    // operations.
    //
    k2 = K2.fromGuidelines(new TestCryptoGuidelines()).withKeyStorage(new TestKeyStorage())
        .withWrappedDataFormat(new TestWrappedDataFormat())
        .withPrimitiveFactory(new TestPrimitiveFactory()).build();
  }

  @Test
  public void testNaiveEncryptAndDecrypt_byteArrayData() throws K2Exception {
    // Get a wrapper and an unwrapper
    Wrapper encrypter = k2.getWrapper(TestKeyStorage.SYM_KEY_ID, SecurityProperty.CONFIDENTIALITY);
    Unwrapper decrypter =
        k2.getUnwrapper(TestKeyStorage.SYM_KEY_ID, SecurityProperty.CONFIDENTIALITY);

    // Example encryption call
    byte[] ciphertext = encrypter.wrap(TEST_MESSAGE.getBytes(Util.UTF_8));

    // Test the results of the (quite fake) encryption
    assertEquals(EXPECTED_CIPHERTEXT, new String(ciphertext, Util.UTF_8));

    // Example decryption call
    byte[] plaintext = decrypter.unwrap(ciphertext);

    // Test the results of the (quite fake) decryption
    assertEquals(TEST_MESSAGE, new String(plaintext, Util.UTF_8));
  }

  @Test
  @Ignore // this functionality is not yet implemented for the TestWrappedDataFormat code
  public void testNaiveEncryptAndDecrypt_streamData() throws K2Exception {
    // Get a wrapper and an unwrapper
    Wrapper encrypter = k2.getWrapper(TestKeyStorage.SYM_KEY_ID, SecurityProperty.CONFIDENTIALITY);
    Unwrapper decrypter =
        k2.getUnwrapper(TestKeyStorage.SYM_KEY_ID, SecurityProperty.CONFIDENTIALITY);

    ByteArrayInputStream sourcePlaintext =
        new ByteArrayInputStream(TEST_MESSAGE.getBytes(Util.UTF_8));
    ByteArrayOutputStream desinationCiphertext = new ByteArrayOutputStream();

    // Example encryption call
    encrypter.wrap(sourcePlaintext, desinationCiphertext);

    // Test the results of the (quite fake) encryption
    assertEquals(EXPECTED_CIPHERTEXT, new String(desinationCiphertext.toByteArray(), Util.UTF_8));


    ByteArrayInputStream sourceCiphertext =
        new ByteArrayInputStream(desinationCiphertext.toByteArray());
    ByteArrayOutputStream desinationPlaintext = new ByteArrayOutputStream();

    // Example decryption call
    decrypter.unwrap(sourceCiphertext, desinationPlaintext);

    // Test the results of the (quite fake) decryption
    assertEquals(TEST_MESSAGE, new String(desinationPlaintext.toByteArray(), Util.UTF_8));
  }
}
