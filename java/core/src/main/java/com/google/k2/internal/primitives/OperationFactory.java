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
package com.google.k2.internal.primitives;

import com.google.k2.api.SecurityProperty;
import com.google.k2.api.exceptions.NoSuchOperationException;
import com.google.k2.internal.common.K2CollaborationParticipant;

/**
 * An OperationFactory creates K2 operations that provide specific SecurityProperties.
 *
 * <p>In general, K2 provides certain core {@link Operation}s, however it is possible to provide
 * new operations by defining a new OperationFactory.
 */
public interface OperationFactory extends K2CollaborationParticipant {

  /**
   * The core purpose of this class - given an array of {@link SecurityProperty}, return an
   * {@link Operation} that provides - within the limits of the key strengths, etc - those
   * properties by wrapping its input data.
   *
   * @param properties properties that need to be provided
   * @return an operation that provides those properties
   * @throws NoSuchOperationException if no operation can be found to provide all requested
   * properties
   */
  public abstract Operation getWrapOperation(SecurityProperty... properties)
      throws NoSuchOperationException;

  /**
   * The core purpose of this class - given an array of {@link SecurityProperty}, return an
   * {@link Operation} that ensures - within the limits of the key strengths, etc - those
   * properties while unwrapping its input data.
   *
   * @param properties properties that need to be ensured
   * @return an operation that ensures those properties
   * @throws NoSuchOperationException if no operation can be found to provide all requested properties
   */
  public abstract Operation getUnwrapOperation(SecurityProperty... properties)
      throws NoSuchOperationException;
}
