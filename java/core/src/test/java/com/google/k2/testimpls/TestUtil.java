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

import com.google.k2.internal.common.Util;

/**
 * Some utilities for test cases.
 */
public class TestUtil {

  private static final int INT_0 = '0';

  /**
   * Convert a string of hex into a byte array.
   */
  public static byte[] hexToByteArray(String hex) {
    Util.checkNotNull(hex, "hex");
    if (hex.length() % 1 == 1) {
      throw new IllegalArgumentException("hex for bytes must have an even number of digits");
    }
    byte[] bytes = new byte[hex.length() / 2];
    for (int i = 0; i < hex.length() / 2; i++) {
      int high = hex.charAt(i) - INT_0;
      if (high < 0 || high > 15) {
        throw new IllegalArgumentException("Bad hex digit " + hex.charAt(i));
      }
      int low = hex.charAt(i + 1) - INT_0;
      if (low < 0 || low > 15) {
        throw new IllegalArgumentException("Bad hex digit " + hex.charAt(i + 1));
      }
      bytes[i] = (byte) (((high << 4) + low) & 0x00FF);
    }
    return bytes;
  }

  /**
   * Convert a byte array into a string of hex.
   */
  public static String byteArrayToHex(byte[] bytes) {
    StringBuilder out = new StringBuilder();
    for (byte b : bytes) {
      out.append((char) ('0' + ((b & 0x00F0) >> 4)));
      out.append((char) ('0' + (b & 0x000F)));
    }
    return out.toString();
  }
}
