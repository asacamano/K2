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

import com.google.k2.internal.common.K2Id;

/**
 * These are the IDs of the standard {@link Primitive}s that all K2 implementations should support.
 */
//TODO(asacamano@gmail.com) Expand this with more Primitives once the basic layout is validated by
//smarter people than me
public enum CorePrimitives implements K2Id {
  /**
   * A bare bones symmetric crypt primitive - does both encryption and decryption, with an IV
   */
  SYMMETRIC_CRYPT,
  /**
   * A bare bones keyed hash primitive
   */
  KEYED_HASH,
}
