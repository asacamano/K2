package com.google.k2.internal.primitives;

/**
 * An Primitive is an object that performs a specific transformation on raw bytes - this is roughly
 * analagous to the idea of a cryptographic primitive.
 *
 * <p>K2 defines the standard structure of keys for various primitives - and in theory, any
 * implementation of a specific primitive should be able to operate with any given key - allowing
 * the mixing and matching of cryptographic primitive implementations with storage and key
 * management implementations.
 *
 * <p>An primitive is not a thread safe object. They are designed to be lightweight and disposable,
 * created by the Operation class per invocation and then disposed of.
 */
public interface Primitive {
}
