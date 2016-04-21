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
package com.google.k2.internal.common;

import com.google.k2.internal.primitives.Operation;
import com.google.k2.internal.primitives.Primitive;

/**
 * This interface marks an object that is an ID used internally by K2, for example to identify a
 * {@link Primitive} or an {@link Operation}.
 *
 * <p>These IDs need to be unique among all IDs for {@link Primitive}s or {@link Operation}s - so
 * application specific IDs should start with a domain name or other reasonably unique prefix.
 *
 * <p>There is no K2 registry of these, so it is possible that adding multiple K2 plugins from
 * different sources will result in collisions.
 */
public interface K2Id {

  /**
   * The actual ID is the result of calling toString on the K2Id object.
   *
   * @return the ID
   */
  @Override
  public String toString();
}
