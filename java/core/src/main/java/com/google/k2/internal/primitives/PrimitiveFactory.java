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

import com.google.k2.api.exceptions.UnimplementedPrimitiveException;
import com.google.k2.internal.common.K2CollaborationParticipant;
import com.google.k2.internal.common.K2Id;

/**
 * An {@link PrimitiveFactory} builds {@link Primitive} objects for specific primitive IDs.
 *
 * <p>To use various low level crypto libraries in K2, it is generally necessary to create a
 * PrivitiveFactory and implement the {@link Primitive} classes that your implementation supports.
 */
public interface PrimitiveFactory extends K2CollaborationParticipant {

  /**
   * Builds a new SymetricPritive for the given K2.
   *
   * <p>This should be called once per operation, since the {@link Primitive} is what holds all
   * of the state about a particular crypto operation.
   *
   * @param primitive the ID of the requested {@link Primitive}
   * @return a new instance of the requested {@link Primitive}
   * @throws UnimplementedPrimitiveException if no such primitive is available
   */
  Primitive makeNewPrimitive(K2Id primitive) throws UnimplementedPrimitiveException;
}
