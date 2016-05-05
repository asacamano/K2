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

import java.util.Set;

/**
 * The highest level of abstract around a cryptographic key.
 *
 * <p>This can represent something as simple as a single AES key, or as complex as a pair of a
 * rotatable signing key and a rotatable asymmetric encryption key.
 */
public interface Key {

  /**
   * Returns the ID used to find this key in the {@link KeyStorage} - also used for error messages,
   * logging, and monitoring.
   *
   * @return the ID that can be used to find this key.
   */
  String getId();

  /**
   * Returns the purposes for this key.
   */
  Set<KeyPurpose> getKeyPurposes();
}
