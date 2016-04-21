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
import com.google.k2.api.exceptions.NoSuchKeyException;
import com.google.k2.api.exceptions.NoSuchOperationException;
import com.google.k2.internal.common.K2Objects;
import com.google.k2.internal.common.Util;
import com.google.k2.internal.messages.WrappedDataFormat;
import com.google.k2.internal.primitives.Operation;
import com.google.k2.internal.primitives.OperationFactory;

/**
 * A K2 is the root object which encapsulates all the major K2 operations.
 *
 * <p>All public methods of K2 are safe to call from multiple threads.
 */
public class K2 {

  /**
   * This is how to build a K2 instance - use this to make a K2Builder, adjust the properties
   * in the builder, and then call build() to get a K2 instance.
   * @param guidelines The base cryptographic guidelines, such as NIST SP800-57 Part 1,
   * @return a K2Builder which can be used to provide other required elements or adjust defaults
   * before constructing a usable K2 instance.
   */
  public static K2Builder fromGuidelines(CryptoGuidelines guidelines) {
    return new K2Builder(guidelines);
  }

  private final CryptoGuidelines guidelines;
  private final KeyStorage keyStorage;
  private final WrappedDataFormat wrappedDataFormat;
  private final OperationFactory operationFactory;

  // This constructor should be the only way to build a K2.
  /* package */ K2(K2Builder builder) {
    K2Objects.linkObjects(Util.checkNotNull(builder.guidelines, "guidelines"),
        Util.checkNotNull(builder.keyStorage, "keyStorage"),
        Util.checkNotNull(builder.operationFactory, "operationFactory"),
        Util.checkNotNull(builder.primitiveFactory, "primitiveFactory"),
        Util.checkNotNull(builder.wrappedDataFormat, "wrappedDataFormat"),
        Util.checkNotNull(builder.logger, "logger"), Util.checkNotNull(builder.monitor, "monitor"),
        Util.checkNotNull(builder.keyTool, "keyTool"), this);
    this.guidelines = builder.guidelines;
    this.keyStorage = builder.keyStorage;
    this.wrappedDataFormat = builder.wrappedDataFormat;
    this.operationFactory = builder.operationFactory;
  }

  /**
   * Returns a {@link Wrapper} that provides the given SecuirtyProperties that will produce the
   * default type of {@link WrappedData} for this {@link K2}.
   * @param keyId the ID of a key to use for the protection
   * @param properties the list of properties that should be guaranteed
   * @return a {@link Wrapper} that provides the protection specified by the properties
   * @throws NoSuchKeyException if a key with that ID could not be found
   * @throws NoSuchOperationException if no operation can be found to provide all requested
   * properties
   * @throws InvalidKeyException if the key is not valid for the operation that provides the
   * requested properties
   */
  public Wrapper getWrapper(String keyId, SecurityProperty... properties)
      throws NoSuchKeyException, NoSuchOperationException, InvalidKeyException {
    return makeWrapper(keyStorage.getKey(keyId), operationFactory.getWrapOperation(properties));
  }

  /**
   * Returns a {@link Wrapper} that provides the given SecuirtyProperties that will produce the
   * default type of {@link WrappedData} for this {@link K2}.
   * @param keyId the ID of a key to use for the protection
   * @param propertySet the set of {@link SecurityProperty}s that should be guaranteed
   * @return a {@link Wrapper} that provides the protection specified by the propertySet
   * @throws NoSuchKeyException if a key with that ID could not be found
   * @throws NoSuchOperationException if no operation can be found to provide all requested
   * properties
   * @throws InvalidKeyException if the key is not valid for the operation that provides the
   * requested properties
   */
  public Wrapper getWrapper(String keyId, SecurityPropertySet propertySet)
      throws NoSuchKeyException, NoSuchOperationException, InvalidKeyException {
    return makeWrapper(keyStorage.getKey(keyId),
        operationFactory.getWrapOperation(propertySet.getPropertiesArray()));
  }

  /**
   * Returns a {@link Unwrapper} that undoes the given SecuirtyProperties that will produce the
   * default type of {@link UnrappedData} for this {@link K2}.
   * @param keyId the ID of a key to use for the unprotection
   * @param properties the list of properties that should be guaranteed
   * @return an {@link Unwrapper} that provides the protection specified by the properties
   * @throws NoSuchKeyException if a key with that ID could not be found
   * @throws NoSuchOperationException if no operation can be found to provide all requested
   * properties
   * @throws InvalidKeyException if the key is not valid for the operation that provides the
   * requested properties
   */
  public Unwrapper getUnwrapper(String keyId, SecurityProperty... properties)
      throws NoSuchKeyException, NoSuchOperationException, InvalidKeyException {
    return makeUnwrapper(keyStorage.getKey(keyId), operationFactory.getUnwrapOperation(properties));
  }

  /**
   * Returns a {@link Unwrapper} that undoes the given SecuirtyProperties that will produce the
   * default type of {@link UnrappedData} for this {@link K2}.
   * @param keyId the ID of a key to use for the unprotection
   * @param propertySet the set of {@link SecurityProperty}s that should be guaranteed
   * @return an {@link Unwrapper} that provides the protection specified by the propertySet
   * @throws NoSuchKeyException if a key with that ID could not be found
   * @throws NoSuchOperationException if no operation can be found to provide all requested
   * properties
   * @throws InvalidKeyException if the key is not valid for the operation that provides the
   * requested properties
   */
  public Unwrapper getUnwrapper(String keyId, SecurityPropertySet propertySet)
      throws NoSuchKeyException, NoSuchOperationException, InvalidKeyException {
    return makeUnwrapper(keyStorage.getKey(keyId),
        operationFactory.getUnwrapOperation(propertySet.getPropertiesArray()));
  }

  /**
   * Builds a {@link Wrapper} that protects data using the given key
   * @param key The key to use when performing the operation.
   * @param operation The operation to perform.
   * @return a newly built Wrapper
   * @throws InvalidKeyException if the key is not valid for this kind of operation
   */
  private Wrapper makeWrapper(Key key, Operation operation) throws InvalidKeyException {
    try {
      guidelines.validateOperationAndKey(key, operation);
    } catch (InsufficientSecurityException e) {
      Util.reportBugInK2("Got InsufficientSecurityException without requesting specific properties",
          e);
    }
    return new Wrapper(guidelines, key, operation, wrappedDataFormat);
  }

  /**
   * Builds an {@link Unwrapper} that unprotects data using the given key and operation/
   * @param key The key to use when performing the operation.
   * @param operation The operation to perform.
   * @return a newly built Unwrapper
   * @throws InvalidKeyException if the key is not valid for this kind of operation
   */
  private Unwrapper makeUnwrapper(Key key, Operation operation) throws InvalidKeyException {
    try {
      guidelines.validateOperationAndKey(key, operation);
    } catch (InsufficientSecurityException e) {
      Util.reportBugInK2("Got InsufficientSecurityException without requesting specific properties",
          e);
    }
    return new Unwrapper(guidelines, key, operation, wrappedDataFormat);
  }
}
