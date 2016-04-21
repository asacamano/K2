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

import com.google.k2.api.CryptoGuidelines;
import com.google.k2.api.Key;
import com.google.k2.api.SecurityProperty;
import com.google.k2.api.exceptions.InsufficientSecurityException;
import com.google.k2.api.exceptions.InvalidKeyException;
import com.google.k2.internal.common.K2Objects;
import com.google.k2.internal.primitives.Operation;

import java.util.Set;

/**
 * Some {@link CryptoGuidelines} for use in tests.
 */
public class TestCryptoGuidelines implements CryptoGuidelines {


  @Override
  public void setK2Objects(K2Objects k2Objects) {
    // Not currently needed
  }

  @Override
  public void validateOperationAndKey(Key key, Operation operation, SecurityProperty... properties)
      throws InsufficientSecurityException, InvalidKeyException {
    if (properties.length > 0) {
      Set<SecurityProperty> operationProvidedProperties = operation.getProvidedProperties();
      for (SecurityProperty property : properties) {
        if (!operationProvidedProperties.contains(property)) {
          throw new InsufficientSecurityException(
              "Requested property " + property + " is not provided by " + operation.getOpId());
        }
      }
    }
    // This one accepts (until we write more tests) all keys and opertaions
    if (!operation.canUseKey(key)) {
      throw new InvalidKeyException(
          "Key " + key.getId() + " can not be used for " + operation.getOpId());
    }
  }
}
