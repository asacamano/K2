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

import com.google.k2.api.exceptions.K2BuilderException;
import com.google.k2.internal.common.Util;
import com.google.k2.internal.keytool.LockedKeyTool;
import com.google.k2.internal.logger.NoOpLogger;
import com.google.k2.internal.messages.WrappedDataFormat;
import com.google.k2.internal.monitor.NoOpMonitor;
import com.google.k2.internal.operations.CoreOperationFactory;
import com.google.k2.internal.primitives.Operation;
import com.google.k2.internal.primitives.OperationFactory;
import com.google.k2.internal.primitives.PrimitiveFactory;

public class K2Builder {

  /* package */ CryptoGuidelines guidelines;
  /* package */ KeyStorage keyStorage;
  /* package */ WrappedDataFormat wrappedDataFormat;
  /* package */ OperationFactory operationFactory;
  /* package */ PrimitiveFactory primitiveFactory;
  /* package */ Logger logger;
  /* package */ Monitor monitor;
  /* package */ KeyTool keyTool;

  /**
   * See {@link K2#fromGuidelines(CryptoGuidelines)}
   */
  /* package */ K2Builder(CryptoGuidelines guidelines) {
    this.guidelines = Util.checkNotNull(guidelines, "guidelines");
    operationFactory = new CoreOperationFactory();
    keyTool = new LockedKeyTool();
    logger = new NoOpLogger();
    monitor = new NoOpMonitor();
  }

  /**
   * Sets the {@link KeyStorage} to use for this K2.
   *
   * <p>This defines where and how keys are stored and retrieved when needed by K2.
   *
   * @param keyStorage a fully initialized {@link KeyStorage} implementation.
   * @return this builder
   */
  public K2Builder withKeyStorage(KeyStorage keyStorage) {
    this.keyStorage = Util.checkNotNull(keyStorage, "keyStorage");
    return this;
  }

  /**
   * Sets the {@link PrimitiveFactory} to use for this K2.
   *
   * <p>This defines which K2 Primitives are created for specific {@link Operation}s.
   *
   * @param keyStorage a fully initialized {@link PrimitiveFactory} implementation.
   * @return this builder
   */
  public K2Builder withPrimitiveFactory(PrimitiveFactory primitiveFactory) {
    this.primitiveFactory = Util.checkNotNull(primitiveFactory, "primitiveFactory");
    return this;
  }

  /**
   * Sets the {@link WrappedDataFormat} to use for this K2.
   *
   * <p>This defines where and K2s internal data structures for {@link WrappedData} are mapped to
   * bytes or other representations.
   *
   * @param wrappedDataFormat a fully initialized {@link WrappedDataFormat} implementation.
   * @return this builder
   */
  public K2Builder withWrappedDataFormat(WrappedDataFormat wrappedDataFormat) {
    this.wrappedDataFormat = Util.checkNotNull(wrappedDataFormat, "wrappedDataFormat");
    return this;
  }

  /**
   * Sets the {@link Logger} to use for this K2.
   *
   * <p>This defines how actions and errors are logged.
   *
   * @param logger a fully initialized {@link Logger logger} implementation.
   * @return this builder
   */
  public K2Builder withLogger(Logger logger) {
    this.logger = Util.checkNotNull(logger, "logger");
    return this;
  }

  /**
   * Sets the {@link Monitor} to use for this K2.
   *
   * <p>This defines how rate and latency of crypto operations is reported outside of K2. Anyone
   * wiching to monitor this can create their own Monitor implementation and use it here.
   *
   * @param monitor a fully initialized {@link Monitor} implementation.
   * @return this builder
   */
  public K2Builder withMonitor(Monitor monitor) {
    this.monitor = Util.checkNotNull(monitor, "monitor");
    return this;
  }

  /**
   * Sets the {@link KeyTool} to use for this K2.
   *
   * <p>This defines how keys are manipulated by K2 - generated, rotated, deprecated, imported,
   * and exported.
   *
   * @param keyTool a fully initialized {@link KeyTool} implementation.
   * @return this builder
   */
  public K2Builder withKeyTool(KeyTool keyTool) {
    this.keyTool = Util.checkNotNull(keyTool, "keyTool");
    return this;
  }

  /**
   * Builds a {@link K2} instance with the characteristics that have been specified in the other
   * builder methods.
   *
   * <p>This may throw NullPointerExceptions if any of the required parameters are missing.
   *
   * @return the newly built {@link K2}
   */
  public K2 build() throws K2BuilderException {
    try {
      return new K2(this);
    } catch (NullPointerException e) {
      throw new K2BuilderException("Missing required value " + e.getMessage(), e);
    }
  }
}
