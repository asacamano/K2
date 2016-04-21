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

/**
 * This allows the central K2 objects to access each other through the shared {@link K2Objects} that
 * provides access to all of the other K2CollaborationParticipants.
 */
public interface K2CollaborationParticipant {
  /**
   * This is used internally to provide access to all of the futures of a K2 (such as logging,
   * monitoring, etc) within this object.
   *
   * This should only be called once - when the {@link K2Objects} instances is built.
   * Implementations may throw and IllegalStateException if this is called more than once.
   *
   * @param k2Objects the K2Objects instance this is associated with.
   * @throws IllegalStateException if this is called more than once on the same instance.
   */
  public void setK2Objects(K2Objects k2Objects);

}
