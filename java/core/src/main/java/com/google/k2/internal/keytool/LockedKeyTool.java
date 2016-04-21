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
package com.google.k2.internal.keytool;

import com.google.k2.api.KeyTool;
import com.google.k2.internal.common.K2Objects;

/**
 * A LockedKeyTool is the default {@link KeyTool}, and it has no support for performing
 * any KeyTool operation.
 *
 * <p>This is the default for use in contexts that just need to perform crypto operations, and
 * not key manipulation.
 *
 * <p>All methods of UnimplementedKeyTool should throw {@link UnsupportedOperationException}
 */
public class LockedKeyTool implements KeyTool {
  @Override
  public void setK2Objects(K2Objects k2Objects) {
    // do nothing
  }
}
