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

import com.google.k2.api.SecurityProperty;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Lightweight utilities class used internally - instead of bringing in a dependency like
 * Guava or Apache-commons.
 */
public class Util {

  public static Charset UTF_8 = Charset.forName("UTF-8");

  /**
   * Throws a {@link NullPointerException} with a meaningful message if obj is null.
   *
   * @param obj the input to be tested
   * @param message the message for the NullPointerException if obj is null.
   * @return obj if it is not null
   */
  public static <T> T checkNotNull(T obj, String message) {
    if (obj == null) {
      throw new NullPointerException(message);
    }
    return obj;
  }

  /**
   * Throws a {@link NullPointerException} with a meaningful message if value is null, empty,
   * or trims to an empty string.
   *
   * @param value the input to be tested
   * @param message the message for the NullPointerException if value is null or empty.
   * @return value if it is not null or empty
   */
  public static String checkNotNullOrEmpty(String value, String message) {
    if (value == null) {
      throw new NullPointerException(message);
    }
    return value;
  }

  /**
   * Converts an array or varags of {@link SecurityProperty}s to an unmodifiable set.
   * @param properties The properties to add to the set
   * @return An unmodifiable set containing all the properties
   */
  public static Set<SecurityProperty> arrayToSet(SecurityProperty... properties) {
    Set<SecurityProperty> base = new HashSet<SecurityProperty>();
    for (SecurityProperty property : properties) {
      base.add(property);
    }
    return Collections.unmodifiableSet(base);
  }

  /**
   * This is used internally to report a bug in K2.
   *
   * <p>For now we throw, but this behavior could be configured differently someday.
   */
  public static void reportBugInK2(String message, Exception e) {
    throw new RuntimeException("Please report this as a bug in K2 : " + message, e);
  }
}
