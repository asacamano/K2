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
package com.google.k2.internal.monitor;

import com.google.k2.api.Monitor;
import com.google.k2.internal.common.K2Objects;

/**
 * A NoOpMonitor is the default {@link Monitor}, and it does not track anything.
 *
 * <p>All methods of NoOpMonitor should immediately return and do no work.
 */
public class NoOpMonitor implements Monitor {
  @Override
  public void setK2Objects(K2Objects k2Objects) {
    // do nothing
  }
}
