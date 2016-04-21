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

import com.google.k2.api.exceptions.CantReadException;
import com.google.k2.api.exceptions.CantWriteException;
import com.google.k2.api.exceptions.InsufficientSecurityException;
import com.google.k2.api.exceptions.InvalidKeyException;
import com.google.k2.api.exceptions.UnimplementedPrimitiveException;
import com.google.k2.internal.common.Util;
import com.google.k2.internal.messages.DestinationMessage;
import com.google.k2.internal.messages.PlaintextReadable;
import com.google.k2.internal.messages.SourceMessage;
import com.google.k2.internal.messages.WrappedDataFormat;
import com.google.k2.internal.primitives.Operation;
import com.google.k2.internal.primitives.Primitive;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A Wrapper takes unprotected input and wraps it in layers of protection such as encryption,
 * signing, etc.
 *
 * <p>Implementations of this interface must ensure that calling any of the wrap methods from
 * multiple threads is safe.
 */
public class Wrapper {

  private final CryptoGuidelines guidelines;
  private final Key key;
  private final Operation operation;
  private final WrappedDataFormat wrappedDataFormat;

  /* package */ Wrapper(CryptoGuidelines guidelines, Key key, Operation operation,
      WrappedDataFormat wrappedDataFormat) {
    this.guidelines = Util.checkNotNull(guidelines, "guidelines");
    this.key = Util.checkNotNull(key, "key");
    this.operation = Util.checkNotNull(operation, "operation");
    this.wrappedDataFormat = Util.checkNotNull(wrappedDataFormat, "wrappedDataFormat");
  }

  /**
   * Protects the input in a wrapper-specific way and returns the protected data as a byte array.
   *
   * @param input the data to be protected
   * @return the input protected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this wrapper.
   * @throws CantWriteException if there are problems creating the destination byte array
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public byte[] wrap(byte[] input)
      throws InvalidKeyException, CantWriteException, UnimplementedPrimitiveException {
    ByteArrayOutputStream destination = new ByteArrayOutputStream();
    try {
      wrap(byteArrayReadable(input), streamWritable(destination));
      return destination.toByteArray();
    } catch (InsufficientSecurityException e) {
      Util.reportBugInK2("InsufficentSecuirtyException when no properties were provided", e);
      return null;
    } catch (CantReadException e) {
      Util.reportBugInK2("Should always be able to read from a byte array", e);
      return null;
    }
  }

  /**
   * Protects the input data in the input in a wrapper-specific way and writes the protected data
   * to the output.
   *
   * @param input where to read the input form
   * @param output where to write the output to
   * @return the input protected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this wrapper.
   * @throws CantWriteException if there are problems writing to the output, such as it not being
   * large enough
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public void wrap(byte[] input, byte[] output)
      throws InvalidKeyException, CantWriteException, UnimplementedPrimitiveException {
    try {
      wrap(byteArrayReadable(input), byteArrayWritable(output));
    } catch (InsufficientSecurityException e) {
      Util.reportBugInK2("InsufficentSecuirtyException when no properties were provided", e);
    } catch (CantReadException e) {
      Util.reportBugInK2("Should always be able to read from a byte array", e);
    }
  }

  /**
   * Protects the data in the input stream in a wrapper-specific way and writes the protected data
   * to the output stream.
   *
   * @param input where to read the input form
   * @param output where to write the output to
   * @return the input protected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this wrapper.
   * @throws CantWriteException if there are problems writing to the output
   * @throws CantReadException if there are problems reading from the input
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public void wrap(InputStream input, OutputStream output) throws InvalidKeyException,
      CantReadException, CantWriteException, UnimplementedPrimitiveException {
    try {
      wrap(streamReadable(input), streamWritable(output));
    } catch (InsufficientSecurityException e) {
      Util.reportBugInK2("InsufficentSecuirtyException when no properties were provided", e);
    }
  }

  /**
   * Protects the input in a wrapper-specific way and returns the protected data as a byte array,
   * and ensures that the wrapping provides the specified security properties.
   *
   * <p>These properties may be any subset of the complete set of properties provided by this
   * wrapper.
   *
   * @param input the data to be protected
   * @param securityProperties the properties that must be provided by this wrapper
   * @return the input protected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this wrapper.
   * @throws InsufficientSecurityException if the specified properties can not be met by this
   * object.
   * @throws CantWriteException if there are problems creating the destination byte array
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public byte[] wrap(byte[] input, SecurityProperty... securityProperties)
      throws InvalidKeyException, InsufficientSecurityException, CantWriteException,
      UnimplementedPrimitiveException {
    ByteArrayOutputStream destination = new ByteArrayOutputStream();
    try {
      wrap(byteArrayReadable(input), streamWritable(destination), securityProperties);
      return destination.toByteArray();
    } catch (CantReadException e) {
      Util.reportBugInK2("Should always be able to read from a byte array", e);
      return null;
    }
  }

  /**
   * Protects the input data in the input in a wrapper-specific way and writes the protected data
   * to the output, and ensures that the wrapping provides the specified security properties.
   *
   * <p>These properties may be any subset of the complete set of properties provided by this
   * wrapper.
   *
   * @param input where to read the input form
   * @param output where to write the output to
   * @return the input protected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this wrapper.
   * @throws InsufficientSecurityException if the specified properties can not be met by this
   * object.
   * @throws CantWriteException if there are problems writing to the output, such as it not being
   * large enough
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public void wrap(byte[] input, byte[] output, SecurityProperty... securityProperties)
      throws InvalidKeyException, InsufficientSecurityException, CantWriteException,
      UnimplementedPrimitiveException {
    try {
      wrap(byteArrayReadable(input), byteArrayWritable(output), securityProperties);
    } catch (CantReadException e) {
      Util.reportBugInK2("Should always be able to read from a byte array", e);
    }
  }

  /**
   * Protects the data in the input stream in a wrapper-specific way and writes the protected data
   * to the output stream, and ensures that the wrapping provides the specified security properties.
   *
   * <p>These properties may be any subset of the complete set of properties provided by this
   * wrapper.
   *
   * @param input where to read the input form
   * @param output where to write the output to
   * @return the input protected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this wrapper.
   * @throws InsufficientSecurityException if the specified properties can not be met by this
   * object.
   * @throws CantWriteException if there are problems writing to the output
   * @throws CantReadException if there are problems reading from the input
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public void wrap(InputStream input, OutputStream output, SecurityProperty... securityProperties)
      throws InvalidKeyException, InsufficientSecurityException, CantReadException,
      CantWriteException, UnimplementedPrimitiveException {
    wrap(streamReadable(input), streamWritable(output), securityProperties);
  }

  /**
   * Protects the input in a wrapper-specific way and returns the protected data as a byte array,
   * and ensures that the wrapping provides the specified security properties.
   *
   * <p>These properties may be any subset of the complete set of properties provided by this
   * wrapper.
   *
   * @param input the data to be protected
   * @param securityProperties the properties that must be provided by this wrapper
   * @return the input protected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this wrapper.
   * @throws InsufficientSecurityException if the specified properties can not be met by this
   * object.
   * @throws CantWriteException if there are problems creating the destination byte array
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public byte[] wrap(byte[] input, SecurityPropertySet securityProperties)
      throws InvalidKeyException, InsufficientSecurityException, CantWriteException,
      UnimplementedPrimitiveException {
    ByteArrayOutputStream destination = new ByteArrayOutputStream();
    try {
      wrap(byteArrayReadable(input), streamWritable(destination),
          securityProperties.getPropertiesArray());
      return destination.toByteArray();
    } catch (CantReadException e) {
      Util.reportBugInK2("Should always be able to read from a byte array", e);
      return null;
    }
  }

  /**
   * Protects the input data in the input in a wrapper-specific way and writes the protected data
   * to the output, and ensures that the wrapping provides the specified security properties.
   *
   * <p>These properties may be any subset of the complete set of properties provided by this
   * wrapper.
   *
   * @param input where to read the input form
   * @param output where to write the output to
   * @return the input protected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this wrapper.
   * @throws InsufficientSecurityException if the specified properties can not be met by this
   * object.
   * @throws CantWriteException if there are problems writing to the output, such as it not being
   * large enough
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public void wrap(byte[] input, byte[] output, SecurityPropertySet securityProperties)
      throws InvalidKeyException, InsufficientSecurityException, CantWriteException,
      UnimplementedPrimitiveException {
    try {
      wrap(byteArrayReadable(input), byteArrayWritable(output),
          securityProperties.getPropertiesArray());
    } catch (CantReadException e) {
      Util.reportBugInK2("Should always be able to read from a byte array", e);
    }
  }

  /**
   * Protects the data in the input stream in a wrapper-specific way and writes the protected data
   * to the output stream, and ensures that the wrapping provides the specified security properties.
   *
   * <p>These properties may be any subset of the complete set of properties provided by this
   * wrapper.
   *
   * @param input where to read the input form
   * @param output where to write the output to
   * @return the input protected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this wrapper.
   * @throws InsufficientSecurityException if the specified properties can not be met by this
   * object.
   * @throws CantWriteException if there are problems writing to the output
   * @throws CantReadException if there are problems reading from the input
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public void wrap(InputStream input, OutputStream output, SecurityPropertySet securityProperties)
      throws InvalidKeyException, InsufficientSecurityException, CantReadException,
      CantWriteException, UnimplementedPrimitiveException {
    wrap(streamReadable(input), streamWritable(output), securityProperties.getPropertiesArray());
  }

  //
  // This is the common internal logic for all wrap calls
  //
  private void wrap(SourceMessage input, DestinationMessage output, SecurityProperty... properties)
      throws InsufficientSecurityException, InvalidKeyException, CantReadException,
      CantWriteException, UnimplementedPrimitiveException {
    guidelines.validateOperationAndKey(key, operation, properties);
    operation.perform(key, input, output);
  }

  // Utility to get a readable InternalMessage from a byte array
  private SourceMessage byteArrayReadable(byte[] source) {
    return new PlaintextReadable(source);
  }

  // Utility to get a readable InternalMessage from an InputStream
  private SourceMessage streamReadable(InputStream source) {
    return new PlaintextReadable(source);
  }

  // Utility pass written data through the wrappedDataFormat to a byte array
  private DestinationMessage byteArrayWritable(byte[] destination) throws CantWriteException {
    return wrappedDataFormat.getWritableMessage(destination, operation, key);
  }

  // Utility pass written data through the wrappedDataFormat to an OutputStream
  private DestinationMessage streamWritable(OutputStream destination) throws CantWriteException {
    return wrappedDataFormat.getWritableMessage(destination, operation, key);
  }
}
