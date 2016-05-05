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

import com.google.k2.api.exceptions.InsufficientSecurityException;
import com.google.k2.api.exceptions.InvalidKeyException;
import com.google.k2.internal.common.K2CollaborationParticipant;
import com.google.k2.internal.primitives.Operation;

/**
 * Objects implementing this interface provide guidance for default and acceptable algorithms,
 * modes of operation, keys lengths for various security operations.
 */
public interface CryptoGuidelines extends K2CollaborationParticipant {

  /**
   * Validates that the operation and the key are allowed (together) under these guidelines.
   *
   * <p>This includes making sure that the key purposes are acceptable, that they key and the
   * operation are compatible, and that the operation provides the requested security properties.
   *
   * @param key the key to use as part of the validation
   * @param operation the operation to use as part of the validation
   * @param properties the properties to ensure hat the operation provides. If empty, then
   * only the key and operation will be checked against the guidelines.
   * @throws InsufficientSecurityException if the specified {@link Operation} can not provide any
   * of the specified {@link SecurityProperty}s.
   * @throws InvalidKeyException if the {@link Key} is not suitable for with the given
   * {@link Operation}
   */
  void validateOperationAndKey(Key key, Operation operation, SecurityProperty... properties)
      throws InsufficientSecurityException, InvalidKeyException;
}
