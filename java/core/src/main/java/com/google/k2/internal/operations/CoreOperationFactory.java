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

import com.google.k2.api.SecurityProperty;
import com.google.k2.api.SecurityPropertySet;
import com.google.k2.api.exceptions.NoSuchOperationException;
import com.google.k2.internal.common.K2Objects;
import com.google.k2.internal.common.Util;
import com.google.k2.internal.primitives.Operation;
import com.google.k2.internal.primitives.OperationFactory;

import java.util.Set;

/**
 * An {@link CoreOperationFactory} builds Operation objects from {@link SecurityProperty}s and
 * {@link SecurityPropertySet}s.
 *
 * <p>In the future, K2 add-ons could provide their own {@link Operation}s and
 * {@link CoreOperationFactory}s to extend the core cryptographic functionality of K2.
 */
public class CoreOperationFactory implements OperationFactory {

  private K2Objects k2Objects = null;

  /* (non-Javadoc)
   * @see com.google.k2.internal.core.OperationFactory#setK2(com.google.k2.core.K2)
   */
  @Override
  public void setK2Objects(K2Objects k2Objects) {
    if (this.k2Objects != null) {
      throw new IllegalStateException("Can't call setK2Objects twice");
    }
    this.k2Objects = Util.checkNotNull(k2Objects, "k2Objects");
  }

  /* (non-Javadoc)
   * @see com.google.k2.internal.core.OperationFactory#getWrapOperation(com.google.k2.core.SecurityProperty)
   */
  @Override
  public Operation getWrapOperation(SecurityProperty... properties)
      throws NoSuchOperationException {
    // Convert to a standard format
    Set<SecurityProperty> propertySet = Util.arrayToSet(properties);
    return getWrapOperation(propertySet);
  }

  /* (non-Javadoc)
   * @see com.google.k2.internal.core.OperationFactory#getUnwrapOperation(com.google.k2.core.SecurityProperty)
   */
  @Override
  public Operation getUnwrapOperation(SecurityProperty... properties)
      throws NoSuchOperationException {
    // Convert to a standard format
    Set<SecurityProperty> propertySet = Util.arrayToSet(properties);
    return getUnwrapOperation(propertySet);
  }

  // The internal method which other versions of the method wrap
  private Operation getWrapOperation(Set<SecurityProperty> properties)
      throws NoSuchOperationException {
    // TODO(asacamano@gmail.com) Make this more elegant
    if (properties.size() == 1 && properties.contains(SecurityProperty.CONFIDENTIALITY)) {
      return new SymmetricCryptWrap(k2Objects);
    } else if (properties.size() == 2 && properties.contains(SecurityProperty.CONFIDENTIALITY)
        && properties.contains(SecurityProperty.SOURCE_AUTHENTICITY)) {
        return new SymmetricCryptAndHmacWrap(k2Objects);
    } else {
      throw new NoSuchOperationException("No operation can provide " + properties);
    }
  }

  // The internal method which other versions of the method wrap
  private Operation getUnwrapOperation(Set<SecurityProperty> properties)
      throws NoSuchOperationException {
    // TODO(asacamano@gmail.com) Make this more elegant
    if (properties.size() == 1 && properties.contains(SecurityProperty.CONFIDENTIALITY)) {
      return new SymmetricCryptUnwrap(k2Objects);
    } else if (properties.size() == 2 && properties.contains(SecurityProperty.CONFIDENTIALITY)
        && properties.contains(SecurityProperty.SOURCE_AUTHENTICITY)) {
        return new SymmetricCryptAndHmacUnwrap(k2Objects);
    } else {
      throw new NoSuchOperationException("No operation can provide " + properties);
    }
  }
}
