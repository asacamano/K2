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

import com.google.k2.api.exceptions.UnimplementedPrimitiveException;
import com.google.k2.internal.common.K2Id;
import com.google.k2.internal.common.K2Objects;
import com.google.k2.internal.primitives.CorePrimitives;
import com.google.k2.internal.primitives.Primitive;
import com.google.k2.internal.primitives.PrimitiveFactory;

/**
 * A PrimitivFactory that is used for unit tests - note that the primitives it returns are not
 * actually real encryption.
 */
public class TestPrimitiveFactory implements PrimitiveFactory {

  @Override
  public void setK2Objects(K2Objects k2Objects) {
    // Not used yet
  }

  @Override
  public Primitive makeNewPrimitive(K2Id primitive) throws UnimplementedPrimitiveException {
    if (primitive.equals(CorePrimitives.SYMMETRIC_CRYPT)) {
      return new TestSymmetricCryptPrimitive();
    } else if (primitive.equals(CorePrimitives.KEYED_HASH)) {
      return new TestKeyedHashPrimitive();
    } else {
      throw new UnimplementedPrimitiveException(
          "TestPrimitiveFactory does not implement " + primitive);
    }
  }

}
