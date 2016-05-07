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
import com.google.k2.internal.messages.PlaintextDestinationMessage;
import com.google.k2.internal.messages.SourceMessage;
import com.google.k2.internal.messages.WrappedDataFormat;
import com.google.k2.internal.primitives.Operation;
import com.google.k2.internal.primitives.Primitive;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An Unwrapper wrapper takes protected input and unwraps the layers of protection such as
 * encryption, signing, etc, to return the unprotected data.
 *
 * <p>Implementations of this interface must ensure that calling any of the unwrap methods from
 * multiple threads is safe.
 */
public class Unwrapper {

  private final CryptoGuidelines guidelines;
  private final Key key;
  private final Operation operation;
  private final WrappedDataFormat wrappedDataFormat;

  /* package */ Unwrapper(CryptoGuidelines guidelines, Key key, Operation operation,
      WrappedDataFormat wrappedDataFormat) {
    this.guidelines = Util.checkNotNull(guidelines, "guidelines");
    this.key = Util.checkNotNull(key, "key");
    this.operation = Util.checkNotNull(operation, "operation");
    this.wrappedDataFormat = Util.checkNotNull(wrappedDataFormat, "wrappedDataFormat");
  }

  /**
   * Unprotects the input in a unwrapper-specific way and returns the unprotected data as a byte
   * array.
   *
   * @param input the data to be unprotected
   * @return the input unprotected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this unwrapper.
   * @throws CantWriteException if there are problems creating the destination byte array
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public byte[] unwrap(byte[] input)
      throws InvalidKeyException, CantWriteException, UnimplementedPrimitiveException {
    ByteArrayOutputStream destination = new ByteArrayOutputStream();
    try {
      unwrap(byteArrayReadable(input), streamWritable(destination));
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
   * Unprotects the input data in the input in a unwrapper-specific way and writes the unprotected
   * data to the output.
   *
   * @param input where to read the input form
   * @param output where to write the output to
   * @return the input unprotected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this unwrapper.
   * @throws CantWriteException if there are problems writing to the output, such as it not being
   * large enough
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public void unwrap(byte[] input, byte[] output)
      throws InvalidKeyException, CantWriteException, UnimplementedPrimitiveException {
    try {
      unwrap(byteArrayReadable(input), byteArrayWritable(output));
    } catch (InsufficientSecurityException e) {
      Util.reportBugInK2("InsufficentSecuirtyException when no properties were provided", e);
    } catch (CantReadException e) {
      Util.reportBugInK2("Should always be able to read from a byte array", e);
    }
  }

  /**
   * Unprotects the data in the input stream in a unwrapper-specific way and writes the unprotected
   * data to the output stream.
   *
   * @param input where to read the input form
   * @param output where to write the output to
   * @return the input unprotected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this unwrapper.
   * @throws CantWriteException if there are problems writing to the output
   * @throws CantReadException if there are problems reading from the input
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public void unwrap(InputStream input, OutputStream output) throws InvalidKeyException,
      CantReadException, CantWriteException, UnimplementedPrimitiveException {
    try {
      unwrap(streamReadable(input), streamWritable(output));
    } catch (InsufficientSecurityException e) {
      Util.reportBugInK2("InsufficentSecuirtyException when no properties were provided", e);
    }
  }

  /**
   * Unprotects the input in a unwrapper-specific way and returns the unprotected data as a byte
   * array, and ensures that the unwrapping provides the specified security properties.
   *
   * <p>These properties may be any subset of the complete set of properties provided by this
   * unwrapper.
   *
   * @param input the data to be unprotected
   * @param securityProperties the properties that must be provided by this unwrapper
   * @return the input unprotected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this unwrapper.
   * @throws InsufficientSecurityException if the specified properties can not be met by this
   * object.
   * @throws CantWriteException if there are problems creating the destination byte array
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public byte[] unwrap(byte[] input, SecurityProperty... securityProperties)
      throws InvalidKeyException, InsufficientSecurityException, CantWriteException,
      UnimplementedPrimitiveException {
    ByteArrayOutputStream destination = new ByteArrayOutputStream();
    try {
      unwrap(byteArrayReadable(input), streamWritable(destination), securityProperties);
      return destination.toByteArray();
    } catch (CantReadException e) {
      Util.reportBugInK2("Should always be able to read from a byte array", e);
      return null;
    }
  }

  /**
   * Unprotects the input data in the input in a unwrapper-specific way and writes the unprotected
   * data to the output, and ensures that the unwrapping provides the specified security properties.
   *
   * <p>These properties may be any subset of the complete set of properties provided by this
   * unwrapper.
   *
   * @param input where to read the input form
   * @param output where to write the output to
   * @return the input unprotected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this unwrapper.
   * @throws InsufficientSecurityException if the specified properties can not be met by this
   * object
   * @throws CantWriteException if there are problems writing to the output, such as it not being
   * large enough
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public void unwrap(byte[] input, byte[] output, SecurityProperty... securityProperties)
      throws InvalidKeyException, InsufficientSecurityException, CantWriteException,
      UnimplementedPrimitiveException {
    try {
      unwrap(byteArrayReadable(input), byteArrayWritable(output), securityProperties);
    } catch (CantReadException e) {
      Util.reportBugInK2("Should always be able to read from a byte array", e);
    }
  }

  /**
   * Unprotects the data in the input stream in a unwrapper-specific way and writes the unprotected
   * data to the output stream, and ensures that the unwrapping provides the specified security
   * properties.
   *
   * <p>These properties may be any subset of the complete set of properties provided by this
   * unwrapper.
   *
   * @param input where to read the input form
   * @param output where to write the output to
   * @return the input unprotected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this unwrapper.
   * @throws InsufficientSecurityException if the specified properties can not be met by this
   * object.
   * @throws CantWriteException if there are problems writing to the output
   * @throws CantReadException if there are problems reading from the input
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public void unwrap(InputStream input, OutputStream output, SecurityProperty... securityProperties)
      throws InvalidKeyException, InsufficientSecurityException, CantWriteException,
      CantReadException, UnimplementedPrimitiveException {
    unwrap(streamReadable(input), streamWritable(output), securityProperties);
  }

  /**
   * Unprotects the input in a unwrapper-specific way and returns the unprotected data as a byte
   * array, and ensures that the unwrapping provides the specified security properties.
   *
   * <p>These properties may be any subset of the complete set of properties provided by this
   * unwrapper.
   *
   * @param input the data to be unprotected
   * @param securityProperties the properties that must be provided by this unwrapper
   * @return the input unprotected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this unwrapper.
   * @throws InsufficientSecurityException if the specified properties can not be met by this
   * object.
   * @throws CantWriteException if there are problems writing to or creating the resulting
   * byte array
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public byte[] unwrap(byte[] input, SecurityPropertySet securityProperties)
      throws InvalidKeyException, InsufficientSecurityException, CantWriteException,
      UnimplementedPrimitiveException {
    ByteArrayOutputStream destination = new ByteArrayOutputStream();
    try {
      unwrap(byteArrayReadable(input), streamWritable(destination),
          securityProperties.getPropertiesArray());
      return destination.toByteArray();
    } catch (CantReadException e) {
      Util.reportBugInK2("Should always be able to read from a byte array", e);
      return null;
    }
  }

  /**
   * Unprotects the input data in the input in a unwrapper-specific way and writes the unprotected
   * data to the output, and ensures that the unwrapping provides the specified security properties.
   *
   * <p>These properties may be any subset of the complete set of properties provided by this
   * unwrapper.
   *
   * @param input where to read the input form
   * @param output where to write the output to
   * @return the input unprotected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this unwrapper.
   * @throws InsufficientSecurityException if the specified properties can not be met by this
   * object.
   * @throws CantWriteException if there are problems writing to the output, such as it not being
   * large enough
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public void unwrap(byte[] input, byte[] output, SecurityPropertySet securityProperties)
      throws InvalidKeyException, InsufficientSecurityException, CantWriteException,
      UnimplementedPrimitiveException {
    try {
      unwrap(byteArrayReadable(input), byteArrayWritable(output),
          securityProperties.getPropertiesArray());
    } catch (CantReadException e) {
      Util.reportBugInK2("Should always be able to read from a byte array", e);
    }
  }

  /**
   * Unprotects the data in the input stream in a unwrapper-specific way and writes the unprotected
   * data to the output stream, and ensures that the unwrapping provides the specified security
   * properties.
   *
   * <p>These properties may be any subset of the complete set of properties provided by this
   * unwrapper.
   *
   * @param input where to read the input form
   * @param output where to write the output to
   * @return the input unprotected according to the specific implementations protection
   * @throws InvalidKeyException if the key is not valid for this unwrapper.
   * @throws InsufficientSecurityException if the specified properties can not be met by this
   * object.
   * @throws CantWriteException if there are problems writing to the output
   * @throws CantReadException if there are problems reading from the input
   * @throws UnimplementedPrimitiveException if a required {@link Primitive} is not available in
   * this configuration
   */
  public void unwrap(InputStream input, OutputStream output, SecurityPropertySet securityProperties)
      throws InvalidKeyException, InsufficientSecurityException, CantReadException,
      CantWriteException, UnimplementedPrimitiveException {
    unwrap(streamReadable(input), streamWritable(output), securityProperties.getPropertiesArray());
  }

  //
  // This is the common internal logic for all unwrap calls
  //
  private void unwrap(SourceMessage input, DestinationMessage output,
      SecurityProperty... properties) throws InsufficientSecurityException, InvalidKeyException,
      CantReadException, CantWriteException, UnimplementedPrimitiveException {
    guidelines.validateOperationAndKey(key, operation, properties);
    operation.perform(key, input, output);
  }

  // Utility to get a readable InternalMessage from the source bytes parsed by the
  // wrappedDataFormat
  private SourceMessage byteArrayReadable(byte[] source) throws CantReadException {
    return wrappedDataFormat.getReadableMessage(source, operation, key);
  }

  // Utility to get a readable InternalMessage from the source InputStream parsed by the
  // wrappedDataFormat
  private SourceMessage streamReadable(InputStream source) throws CantReadException {
    return wrappedDataFormat.getReadableMessage(source, operation, key);
  }

  // Utility get a writable InputMessage that writes to a byte array
  private DestinationMessage byteArrayWritable(byte[] destination) {
    return new PlaintextDestinationMessage(destination);
  }

  // Utility get a writable InputMessage that writes to an OutputStream
  private DestinationMessage streamWritable(OutputStream destination) {
    return new PlaintextDestinationMessage(destination);
  }
}
