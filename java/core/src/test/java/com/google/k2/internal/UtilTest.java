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
package com.google.k2.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.k2.api.SecurityProperty;
import com.google.k2.internal.common.Util;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Unit tests for {@link Util}
 */
public class UtilTest {

  @Test
  public void testCheckNotNull_happyPath() {
    assertEquals("test", Util.checkNotNull("test", "fail message"));
  }

  @Test
  public void testCheckNotNull_thowsNPE() {
    try {
      Util.checkNotNull(null, "fail message");
      fail("Expected NPE");
    } catch (NullPointerException e) {
      assertEquals("fail message", e.getMessage());
    }
  }

  @Test
  public void testArrayToSet_empty() {
    Set<SecurityProperty> result = Util.arrayToSet();
    assertEquals(new HashSet<SecurityProperty>(), result);
    // Now make sure it's actually unmodifiable
    try {
      result.add(SecurityProperty.ASYMMETRY);
      fail("Expected an UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      // since it's the right type, nothing more to check
    }
  }

  @Test
  public void testArrayToSet_withData() {
    Set<SecurityProperty> result =
        Util.arrayToSet(SecurityProperty.ASYMMETRY, SecurityProperty.DATA_INTEGRITY);
    Set<SecurityProperty> expected = new HashSet<SecurityProperty>();
    expected.add(SecurityProperty.ASYMMETRY);
    expected.add(SecurityProperty.DATA_INTEGRITY);
    assertEquals(expected, result);
    // Now make sure it's actually unmodifiable
    try {
      result.add(SecurityProperty.HARDWARE_BINDING);
      fail("Expected an UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      // since it's the right type, nothing more to check
    }
  }
}
