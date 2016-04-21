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

import static com.google.k2.api.SecurityProperty.ASYMMETRY;
import static com.google.k2.api.SecurityProperty.COLLISION_RESISTANCE;
import static com.google.k2.api.SecurityProperty.CONFIDENTIALITY;
import static com.google.k2.api.SecurityProperty.DATA_INTEGRITY;
import static com.google.k2.api.SecurityProperty.HARDWARE_BINDING;
import static com.google.k2.api.SecurityProperty.HARDWARE_OPTIMIZATION_RESISTANCE;
import static com.google.k2.api.SecurityProperty.KEY_ROTATABILITY;
import static com.google.k2.api.SecurityProperty.NO_EXCEPTIONS;
import static com.google.k2.api.SecurityProperty.PREIMAGE_RESISTANCE;
import static com.google.k2.api.SecurityProperty.SECOND_PREIMAGE_RESISTANCE;
import static com.google.k2.api.SecurityProperty.SOURCE_AUTHENTICITY;

import com.google.k2.internal.common.Util;

import java.util.Set;

/**
 * These identify groups of {@link SecurityProperty}s for for common cryptographic use cases.
 *
 * <p>In general, all of these specify NO_EXCEPTION so that when using them one is assured of
 * compliance with the standard.
 *
 *
 */
//TODO(Andrew) This data should come from the proto files.
public enum SecurityPropertySet {

  /**
   * Performs symmetric encryption with integrity and authenticity.
   */
  CRYPT(CONFIDENTIALITY, DATA_INTEGRITY, SOURCE_AUTHENTICITY, KEY_ROTATABILITY, NO_EXCEPTIONS),

  /**
   * Performs symmetric encryption with integrity and authenticity.
   */
  CRYPT_IN_HARDWARE(CONFIDENTIALITY, DATA_INTEGRITY, SOURCE_AUTHENTICITY, KEY_ROTATABILITY, NO_EXCEPTIONS, HARDWARE_BINDING),

  /**
   * Performs asymmetric encryption with integrity and authenticity
   */
  ASYMMETRIC_CRYPT_WITH_SIGNATURE(ASYMMETRY, CONFIDENTIALITY, DATA_INTEGRITY, SOURCE_AUTHENTICITY, KEY_ROTATABILITY, NO_EXCEPTIONS),

  /**
   * Performs asymmetric encryption with integrity and authenticity
   */
  ASYMMETRIC_CRYPT_WITH_SIGNATURE_IN_HARDWARE(ASYMMETRY, CONFIDENTIALITY, DATA_INTEGRITY, SOURCE_AUTHENTICITY, KEY_ROTATABILITY, NO_EXCEPTIONS, HARDWARE_BINDING),

  /**
   * Secure password hashing for storage
   */
  PASSWORD_STORAGE(PREIMAGE_RESISTANCE, SECOND_PREIMAGE_RESISTANCE, COLLISION_RESISTANCE, HARDWARE_OPTIMIZATION_RESISTANCE, KEY_ROTATABILITY, NO_EXCEPTIONS),

  /**
   * Secure password hashing for storage
   */
  PASSWORD_STORAGE_HARDWARE(PREIMAGE_RESISTANCE, SECOND_PREIMAGE_RESISTANCE, COLLISION_RESISTANCE, HARDWARE_OPTIMIZATION_RESISTANCE, KEY_ROTATABILITY, NO_EXCEPTIONS, HARDWARE_BINDING);

  private final Set<SecurityProperty> properties;
  private final SecurityProperty[] propertiesArray;

  private SecurityPropertySet(SecurityProperty... properties) {
    this.properties = Util.arrayToSet(properties);
    propertiesArray = new SecurityProperty[properties.length];
    System.arraycopy(properties, 0, propertiesArray, 0, properties.length);
  }

  /**
   * Returns an unmodifiable set of the {@link SecurityProperty}s associated with this
   * {@link SecurityPropertySet}
   */
  public Set<SecurityProperty> getProperties() {
    return properties;
  }

  /**
   * Returns an array of the {@link SecurityProperty}s associated with this
   * {@link SecurityPropertySet}.
   *
   * <p>To prevent shenanigans, this methods returns a new array each time, with the values copied
   * from a master array.
   */
  public SecurityProperty[] getPropertiesArray() {
    SecurityProperty[] newProperties = new SecurityProperty[propertiesArray.length];
    System.arraycopy(propertiesArray, 0, newProperties, 0, propertiesArray.length);
    return newProperties;
  }
}
