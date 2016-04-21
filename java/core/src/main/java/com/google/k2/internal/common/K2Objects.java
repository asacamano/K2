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

import com.google.k2.api.CryptoGuidelines;
import com.google.k2.api.K2;
import com.google.k2.api.KeyStorage;
import com.google.k2.api.KeyTool;
import com.google.k2.api.Logger;
import com.google.k2.api.Monitor;
import com.google.k2.internal.messages.WrappedDataFormat;
import com.google.k2.internal.primitives.OperationFactory;
import com.google.k2.internal.primitives.PrimitiveFactory;

/**
 * K2Objects is the interneral reference to the objects that make up the K2 environment,
 * {@link CryptoGuidelines}, {@link OperationFactory}s, {@link PrimitiveFactory}, etc.
 *
 * <p>The other purpose of this class is to keep the core interfaces in com.google.K2.core clean,
 * so that only internal code has access to these objects.
 */
public class K2Objects {

  /**
   * Makes a new K2Objects instance, populates it, and gives it to all of the other objects, so that
   * any of the core K2 instances can use any of the others.
   * @param guidelines the {@link CryptoGuidelines} to use
   * @param keyStorage the {@link KeyStorage} to use
   * @param operationFactory the {@link OperationFactory} to use
   * @param primitiveFactory the {@link PrimitiveFactory} to use
   * @param wrappedDataFormat the {@link WrappedDataFormat} to use
   * @param key
   * @param k2 the K2 instance to use
   */
  public static void linkObjects(CryptoGuidelines guidelines, KeyStorage keyStorage,
      OperationFactory operationFactory, PrimitiveFactory primitiveFactory,
      WrappedDataFormat wrappedDataFormat, Logger logger, Monitor monitor, KeyTool keyTool, K2 k2) {
    K2Objects objects = new K2Objects(guidelines, keyStorage, operationFactory, primitiveFactory,
        wrappedDataFormat, logger, monitor, keyTool, k2);
    guidelines.setK2Objects(objects);
    keyStorage.setK2Objects(objects);
    operationFactory.setK2Objects(objects);
    primitiveFactory.setK2Objects(objects);
    wrappedDataFormat.setK2Objects(objects);
    logger.setK2Objects(objects);
    monitor.setK2Objects(objects);
    keyTool.setK2Objects(objects);
  }

  private final CryptoGuidelines guidelines;
  private final KeyStorage keyStorage;
  private final OperationFactory operationFactory;
  private final PrimitiveFactory primitiveFactory;
  private final WrappedDataFormat wrappedDataFormat;
  private final Logger logger;
  private final Monitor monitor;
  private final KeyTool keyTool;
  private final K2 k2;

  private K2Objects(CryptoGuidelines guidelines, KeyStorage keyStorage,
      OperationFactory operationFactory, PrimitiveFactory primitiveFactory,
      WrappedDataFormat wrappedDataFormat, Logger logger, Monitor monitor, KeyTool keyTool, K2 k2) {
    this.guidelines = Util.checkNotNull(guidelines, "guidelines");
    this.keyStorage = Util.checkNotNull(keyStorage, "keyStorage");
    this.operationFactory = Util.checkNotNull(operationFactory, "operationFactory");
    this.primitiveFactory = Util.checkNotNull(primitiveFactory, "primitiveFactory");
    this.wrappedDataFormat = Util.checkNotNull(wrappedDataFormat, "wrappedDataFormat");
    this.logger = Util.checkNotNull(logger, "logger");
    this.monitor = Util.checkNotNull(monitor, "monitor");
    this.keyTool = Util.checkNotNull(keyTool, "keyTool");
    this.k2 = Util.checkNotNull(k2, "k2");
  }

  /**
   * Returns the {@link CryptoGuidelines} currently in use.
   * @return the current {@link CryptoGuidelines}
   */
  public CryptoGuidelines getGuidelines() {
    return guidelines;
  }

  /**
   * Returns the {@link KeyStorage} currently in use.
   * @return the current {@link KeyStorage}
   */
  public KeyStorage getKeyStorage() {
    return keyStorage;
  }

  /**
   * Returns the {@link OperationFactory} currently in use.
   * @return the current {@link OperationFactory}
   */
  public OperationFactory getOperationFactory() {
    return operationFactory;
  }

  /**
   * Returns the {@link PrimitiveFactory} currently in use.
   * @return the current {@link PrimitiveFactory}
   */
  public PrimitiveFactory getPrimitiveFactory() {
    return primitiveFactory;
  }

  /**
   * Returns the {@link WrappedDataFormat} currently in use.
   * @return the current {@link WrappedDataFormat}
   */
  public WrappedDataFormat getWrappedDataFormat() {
    return wrappedDataFormat;
  }

  /**
   * Returns the {@link Logger} currently in use.
   * @return the current {@link Logger}
   */
  public Logger getLogger() {
    return logger;
  }

  /**
   * Returns the {@link Monitor} currently in use.
   * @return the current {@link Monitor}
   */
  public Monitor getMonitor() {
    return monitor;
  }

  /**
   * Returns the {@link KeyTool} currently in use.
   * @return the current {@link KeyTool}
   */
  public KeyTool getKeyTool() {
    return keyTool;
  }

  /**
   * Returns the {@link K2} currently in use.
   * @return the current {@link K2}
   */
  public K2 getK2() {
    return k2;
  }
}
