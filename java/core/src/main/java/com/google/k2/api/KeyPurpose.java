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

import com.google.k2.internal.common.K2Id;

/**
 * These identify the purposes of keys - what function the key provides in a cryptogrphic setting.
 *
 * <p>Threre are two main reasons why a key purpose is relevant - first, certain kinds of
 * {@link Wrapper} and {@link Unwrapper} classes use primitives that require keys of a specific
 * purpose.
 *
 * <p>Secondly, when dealing key rotation, it is required that the keys have the same purpose.
 */
public enum KeyPurpose implements K2Id {
  /**
   * Purpose of key: decryption.
   */
  DECRYPT,

  /**
   * Purpose of key: encryption.
   */
  ENCRYPT,

  /**
   * Purpose of key: signing or generating a Message Authentication Code (MAC).
   */
  SIGN,

  /**
   * Purpose of key: signature or Message Authentication Code (MAC) verification.
   */
  VERIFY,
}
