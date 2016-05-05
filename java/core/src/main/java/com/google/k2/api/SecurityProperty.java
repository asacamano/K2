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

/**
 * These identify the security properties that can be provided when protecting data by wrapping it,
 * and required when unprotecting data by unwrapping it.
 *
 * <p>Note: all of these are "guarantees", but only with the caveat "given the limits of the
 * key strength and algorithm and the state of the art of cryptography."
 *
 * <p>Note: The definitions of these properties are one of the key features of K2 - and we should
 * make sure these are clear, accurate, and precise.
 *
 * <p>Final note - in general we prefer the terms used in NIST SP800-57. These names are based on
 * Revision 4 dated 2016/01/28 - however, we also provide properties not defined as such in that
 * document.
 */
//TODO(Andrew) These should come from the proto files.
public enum SecurityProperty {

  /**
   * Guarantees that only those with the key can see the unprotected data.
   *
   * <p>This is a NIST SP800-57 term.
   */
  CONFIDENTIALITY,

  /**
   * Guarantees that modifications to the data can be detected.
   *
   * <p>This is a NIST SP800-57 term.
   */
  DATA_INTEGRITY,

  /**
   * Guarantees that the data comes from someone with the key.
   *
   * <p>The combination of {@link SecurityProperty#AUTHENTICITY} and
   * {@link SecurityProperty#ASYMMETRY} can be the cryptographic basis for the legal concept of
   * "non-repudiation", which when someone can not deny they generated the message. However, this
   * assumed a good deal of legal recognition of the system and of the key management processes,
   * so "non-repudiation" is not a K2 {@link SecurityProperty}.
   *
   * <p>The combination of {@link SecurityProperty#AUTHENTICITY} and
   * {@link SecurityProperty#DATA_INTEGRITY} is sometimes called "Data Authentication" - the
   * guarantee that the data is exactly as it came from a party with the key.
   *
   * <p>This is a NIST SP800-57 term.
   */
  SOURCE_AUTHENTICITY,

  /**
   * Guarantees that there is a way to determine which of several key versions should be used when
   * performing an operation. This is essential to allowing keys to be rotated without disrupting
   * operations.
   */
  KEY_ROTATABILITY,

  /**
   * Guarantees that a hash function provides pre-image resistance: that for a hash function h, and
   * a hashed value h, that it is difficult to find an input m such that h(m) = h.
   */
  PREIMAGE_RESISTANCE,

  /**
   * Guarantees that a hash function provides second pre-image resistance: that for a hash function
   * h, and a given input m1, that it is difficult to find a second input m2 such that h(m1) = h(m2)
   */
  SECOND_PREIMAGE_RESISTANCE,

  /**
   * Guarantees that a hash function provides collision resistance: that for a hash function h, it
   * is difficult to find any two inputs m1 and m2 such that h(m1) = h(m2)
   */
  COLLISION_RESISTANCE,

  /**
   * Guarantees that a hash function is hard to implement in ASIC hardware, making it difficult to
   * use bit-coin farming machinery to attack the hash.
   */
  HARDWARE_OPTIMIZATION_RESISTANCE,

  /**
   * Guarantees that the operations as performed in such a way that keys are split into public
   * and private keys, and not all operations can be performed with the public key.
   */
  ASYMMETRY,

  /**
   * Guarantees that an eavesdropper who compromises a long-term key does not thereby gain access
   * to session keys from past transmissions.
   *
   * <p>Note that generally FORWARD_SECRECY requires some kind of interactive establishment of a
   * session, which currently K2 does not support, but should in the future.
   */
  FORWARD_SECRECY,

  /**
   * Guarantees that the other party's public key is signed by a trusted third party who asserts
   * the identity of the other party.
   *
   * <p>Note THIRD_PARTY_IDENTITY requires managing trusted certificates, which K2 does not
   * currently support, but should in the future.
   */
  THIRD_PARTY_IDENTITY,

  /**
   * Guarantees that the keys used are stored in hardware that offers some form of protection
   * against extraction.
   *
   * <p>Note the type of protection against extraction varies widely between devices, and K2
   * makes no effort to differentiate between the levels of protection.
   */
  HARDWARE_BINDING,

  /**
   * TODO(Andrew) I'm not sure what this means - I think it has to do with how much data is
   * encrypted with any particular key, but....
   */
  DIVERSITY,

  /**
   * Guarantees that the protection is in accord with the cryptographic guidelines in use, and not
   * using any of the keys that have exceptions defined.
   */
  NO_EXCEPTIONS
}
