/*
 * Copyright 2014 Google Inc. All rights reserved.
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

package com.google.k2crypto.keyversions;

import com.google.k2crypto.KeyVersionBuilder;
import com.google.k2crypto.exceptions.BuilderException;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class represents an AES key version in K2. It allows you to encrypt and decrypt messaged
 * using AES symmetric key encryption
 *
 * @author John Maheswaran (maheswaran@google.com)
 */
public class AESKeyVersion extends SymmetricKeyVersion {
  /**
   * TODO: Add keyVersionID String - include security properties in calculation?
   *
   */

  /**
   * The key length in bytes (128 bits / 8 = 16 bytes) Can be 16, 24 or 32 (NO OTHER VALUES)
   */
  private int keyVersionLengthInBytes = 16;

  /**
   * SecretKey object representing the key matter in the AES key
   */
  private SecretKey secretKey;

  /**
   * The actual key matter of the AES key used by encKey.
   */
  protected byte[] keyVersionMatter = new byte[keyVersionLengthInBytes];

  /**
   * initialization vector used for encryption and decryption
   */
  protected byte[] initvector = new byte[16];

  /**
   * Enum representing all supported modes Supported modes: CBC, ECB, OFB, CFB, CTR Unsupported
   * modes: XTS, OCB
   */
  public enum Mode {
    CBC, ECB, OFB, CFB, CTR
  }

  /**
   * The encryption mode
   */
  private Mode mode = Mode.CBC;

  /**
   * Supported padding: PKCS5PADDING Unsupported padding: PKCS7Padding, ISO10126d2Padding,
   * X932Padding, ISO7816d4Padding, ZeroBytePadding
   */
  private String padding = "PKCS5PADDING";

  /**
   * represents the algorithm, mode, and padding to use and paddings (NOT algorithm - AES ONLY)
   *
   */
  private String algModePadding = "AES/" + this.mode + "/" + padding;

  /**
   * Method to give length of key in BITS. Used to prevent mixing up bytes and bits
   *
   * @return Key length in BITS
   */
  private int keyLengthInBits() {
    return this.keyVersionLengthInBytes * 8;
  }

  /**
   * Cipher for encrypting data using this AES key version
   */
  private Cipher encryptingCipher;

  /**
   * Cipher for decrypting data using this AES key version
   */
  private Cipher decryptingCipher;

  /**
   * Initializes the key using key matter and initialization vector parameters.
   *
   * @param keyVersionMatter Byte array representation of a key we want to use
   * @param initvector Byte array representation of initialization vector.
   */
  public void setkeyVersionMatter(byte[] keyVersionMatter, byte[] initvector) {

    // save key matter byte array in this object
    this.keyVersionMatter = keyVersionMatter;
    // load the initialization vector
    this.initvector = initvector;

    // initialize secret key using key matter byte array
    secretKey = new SecretKeySpec(this.keyVersionMatter, 0, this.keyLengthInBytes(), "AES");
  }

  /**
   *
   * Method to give length of key in BYTES. Used to prevent mixing up bytes and bits
   *
   * @return Key length in BYTES
   */
  private int keyLengthInBytes() {
    return this.keyVersionLengthInBytes;
  }

  /**
   * Method to get the encrypting cipher of this key version
   *
   * @return The Cipher object representing the encrypting cipher of this key version
   */
  @Override
  public Cipher getEncryptingCipher() {
    return this.encryptingCipher;
  }

  /**
   * Method to get the decrypting cipher of this key version
   *
   * @return The Cipher object representing the decrypting cipher of this key version
   */
  @Override
  public Cipher getDecryptingCipher() {
    return this.decryptingCipher;
  }

  /**
   * Constructor to make an AESKeyVersion using the AESKeyVersionBuilder. Private to prevent use
   * unless through the AESKeyVersionBuilder
   *
   * @param builder An AESKeyVersionBuilder with all the variables set according to how you want the
   *        AESKeyVersion to be setup.
   * @throws BuilderException
   */
  private AESKeyVersion(AESKeyVersionBuilder builder) throws BuilderException {
    // set key version length, mode and padding based on the key version builder
    this.keyVersionLengthInBytes = builder.keyVersionLengthInBytes;
    this.mode = builder.mode;
    this.padding = builder.padding;

    // IMPORTANT! this line of code updates the algorithm/mode/padding string to reflect the new
    // mode and padding. The class will not work if you move or remove this line of code
    this.algModePadding = "AES/" + this.mode + "/" + padding;

    // use try catch block to abstract from individual exceptions using BuilderException
    try {
      // set the key matter and initialization vector from input if is was provided
      if (builder.keyVersionMatterInitVectorProvided) {
        // set key matter and init vector according to provided key matter and init vector
        this.setkeyVersionMatter(builder.keyVersionMatter, builder.initVector);
      } else {
        // Generate the key using JCE crypto libraries
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(this.keyLengthInBits());
        secretKey = keyGen.generateKey();
        // save the keyVersionMatter to the local variable keyVersionMatter
        this.keyVersionMatter = secretKey.getEncoded();

        // use this secure random number generator to initialize the vector with random bytes
        SecureRandom prng = new SecureRandom();
        prng.nextBytes(initvector);
        // create the SecretKey object from the byte array
        secretKey = new SecretKeySpec(this.keyVersionMatter, 0, this.keyLengthInBytes(), "AES");
      }

      // make an AES cipher that we can use for encryption
      this.encryptingCipher = Cipher.getInstance(this.algModePadding);

      // initialize the encrypting cipher
      if (this.mode.equals(Mode.CBC) || this.mode.equals(Mode.OFB) || this.mode.equals(Mode.CFB)
          || this.mode.equals(Mode.CTR)) {
        // Initialize the cipher using the secret key of this class and the initialization vector
        encryptingCipher.init(Cipher.ENCRYPT_MODE, this.secretKey,
            new IvParameterSpec(this.initvector));
      } else if (this.mode.equals(Mode.ECB)) {
        // Initialize the cipher using the secret key - ECB does NOT use an initialization vector
        encryptingCipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
      }

      // make an AES cipher that we can use for decryption
      this.decryptingCipher = Cipher.getInstance(this.algModePadding);

      // initialize the decrypting cipher
      if (this.mode.equals(Mode.CBC) || this.mode.equals(Mode.OFB) || this.mode.equals(Mode.CFB)
          || this.mode.equals(Mode.CTR)) {
        // Initialize the cipher using the secret key of this class and the initialization vector
        decryptingCipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(initvector));
      } else if (this.mode.equals(Mode.ECB)) {
        // Initialize the cipher using the secret key - ECB does NOT use an initialization vector
        decryptingCipher.init(Cipher.DECRYPT_MODE, secretKey);
      }

      // Catch all exceptions
    } catch (Exception e) {
      // Propagate the exception up using BuilderException
      throw new BuilderException("Building AESKeyVersion failed", e);
    }
  }

  /**
   * This class represents a key version builder for AES key versions.
   *
   * @author John Maheswaran (maheswaran@google.com)
   */
  public static class AESKeyVersionBuilder extends KeyVersionBuilder {
    /**
     * key size can be 16, 24 or 32
     */
    private int keyVersionLengthInBytes = 16;
    /**
     * Supported modes: CBC, ECB, OFB, CFB, CTR Unsupported modes: XTS, OCB
     */
    private Mode mode = Mode.CTR;

    /**
     * Supported paddings depends on Java implementation. Upgrade java implementation to support
     * more paddings. Supported padding: PKCS5PADDING Unsupported padding: PKCS7Padding,
     * ISO10126d2Padding, X932Padding, ISO7816d4Padding, ZeroBytePadding
     */
    private String padding = "PKCS5PADDING";

    /**
     * Byte array that will represent the key matter
     */
    private byte[] keyVersionMatter;
    /**
     * Byte array that will represent the initialization vector
     */
    private byte[] initVector;

    /**
     * Flag to indicate to the parent class (AESKeyVersion) whether the key matter and
     * initialization vector have been manually set (true if and only if they have been manually
     * set)
     */
    private boolean keyVersionMatterInitVectorProvided = false;

    /**
     * Set the key version length
     *
     * @param keyVersionLength Integer representing key version length in BYTES, can be 16, 24, 32
     * @return This object with keyVersionLength updated
     */
    public AESKeyVersionBuilder keyVersionLengthInBytes(int keyVersionLength) {
      this.keyVersionLengthInBytes = keyVersionLength;
      return this;
    }

    /**
     * Set the encryption mode
     *
     * @param mode representing the encryption mode. Supported modes: CBC, ECB, OFB, CFB, CTR
     * @return This object with mode updated
     */
    public AESKeyVersionBuilder mode(Mode mode) {
      this.mode = mode;
      return this;
    }

    /**
     * Set the padding
     *
     * @param padding String representing the padding. Supported padding: PKCS5PADDING
     * @return This object with padding updated
     */
    public AESKeyVersionBuilder padding(String padding) {
      this.padding = padding;
      return this;
    }

    /**
     *
     * @param keyVersionMatter Byte array representing the key matter
     * @param initVector Byte array representing the initialization vector
     * @return This object with key matter, initialization vector set
     */
    public AESKeyVersionBuilder matterVector(byte[] keyVersionMatter, byte[] initVector) {
      // This flag indicates to the parent class (AESKeyVersion) that the key matter and
      // initialization vector have been manually set
      keyVersionMatterInitVectorProvided = true;
      // set the key matter
      this.keyVersionMatter = keyVersionMatter;
      // set the initialization vector
      this.initVector = initVector;
      return this;
    }

    /**
     * Method to build a new AESKeyVersion
     *
     * @return An AESKeyVersion with the parameters set according to the AESKeyVersionBuilder
     * @throws BuilderException
     */
    public AESKeyVersion build() throws BuilderException {
      try {
        return new AESKeyVersion(this);
      } catch (Exception e) {
        throw new BuilderException("Building AESKeyVersion failed", e);
      }
    }
  }
}
