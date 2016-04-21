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

import com.google.k2.api.exceptions.NoSuchKeyException;
import com.google.k2.internal.common.K2CollaborationParticipant;

/**
 * KeyStorage is how keys are stored and retrieved in K2.
 */
public interface KeyStorage extends K2CollaborationParticipant {

  /**
   * Returns the {@link Key} with the given ID.
   * @param keyId which specific key is needed
   * @return
   */
  public Key getKey(String keyId) throws NoSuchKeyException;
}
