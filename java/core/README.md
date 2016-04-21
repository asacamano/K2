# K2 Crypto Library Java Core

Copyright 2014 Google. Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

# What is this project

This directory contains the standard parts of K2, that will be the same for every implementation.
It also provides the default set of cryptographic operations that an implementation should support
to be considered complete.

# How is this project organized

There are two main parts to the classes here : com.google.k2.api and com.google.k2.internal.

# Client facing API - com.google.k2.api

Most uses of K2 will only even need to call the classes an com.google.k2.api.  These classes are
how K2 is configured, how keys are manipulated, and how data is protected or unprotected with
cryptographic operations.

## Example usage

This example assumes a real implementation with a file base key storage K2 plugin, and a JCE based
primitive provider K2 plugin. (TODO: asacamano@gmail.com - write these)

```
K2 k2 = JceK2LBuilder.latestGuidelines().withFileStorage("/keydir").build();

Wrapper encrypter = k2.getWrapper(TestKeyStorage.SYM_KEY_ID, SecurityProperty.CONFIDENTIALITY);
Unwrapper decrypter = k2.getUnwrapper(TestKeyStorage.SYM_KEY_ID, SecurityProperty.CONFIDENTIALITY);

// Example encryption call
byte[] ciphertext = encrypter.wrap(plaintext);

// Example decryption call
byte[] plaintext = decrypter.unwrap(ciphertext);
```

See more examples in src/test/java/com/google/k2/api.K2CanonicalExampleTest

# K2 Implementation API - com.google.k2.internal

K2's internal code is all organized in packages under com.google.k2.internal. The following sections
describe how K2 is structured internally.

## K2 core cryptographic objects

The basic logic of K2 is that data is wrapped to provide protection. That protection is described
as a set of SecurityPropertys. These property are used to create the Wrapper object that performs
the wrapping, and can also be provided when the wrap operation is called, making it very easy to see
exactly what properties are being provided by this operation.  Similarly, when creating an
Unwrapper, and when calling the unwrap operation, security properties can be provided to assert that
the data was protected in a way that provided those properties.

The actual protection is done through a collaboration of four key types of objects.

* The Operation describe the entire pipeline: for example generating an IV, applying a symmetric
cipher with a particular mode of operation and padding, and appending an HMAC.
* One or more Primitives apply specific cryptographic operations.  This is roughly equivalent
to the strict use of term "cryptographic primitive" - it is a transformation of data through
application of a function that provides some cryptographic property. Notes that a single Operation
might involve multiple Primitives.
* One or more Key objects that provide key material used by the primitives. In some cases, a Key
may wrap several other keys, for instance a key might wrap an public key for encryption, and a
private key for signing.
* Two messages: a SourceMessage and a DestinationMessage.  These objects provide a very simple API
by which data can be read and data can be written.

## Internal Message Structure

Another important K2 [design goal](https://github.com/google/K2/wiki/What-is-K2) is that the format
of keys and the format of protected messages needs to be plugable. Forthermore, that format needs
to support three modes, operating on data in memory, operation on streams of data (files, network
connections, etc), and true cryptographic streaming operations (in which, for example, HMACS are
calculcated and included periodically throughout the data, so that you don't need to wait for the
entire data stream to be able to start using it).

To this end, the internal message structure is very simple:

* a message is an ordered list of parts
* and each part is a specific type: integer, fixed length byte array, variable length byte array
* a message may have at most one variable length byte array

The last requirement is to make performing whole-data operations possible on more data than can
fit in memory.  This is a corner case - but an important one - it makes it possible, for instance,
to decrypt a file using ciphers other stream-ciphers.

Handling true streaming ciphers also requires that a block of parts be allowed to repeat.

## Converting from the internal message structure to formatted data

Implementations of the WrappedDataFormat interface are responsible for converting data to and from
the internal message structure. These will be provided by plugins - for instance a K2-keyczar plugin
could provide a WrappedDataFormat that knows how to parse Keyczar's JSON structured messages, and a
K2-PGP plugin could be written to parse PGP data.

## Internal organization

K2 is extremely modular - this is one of the
[main goals](https://github.com/google/K2/wiki/What-is-K2) of the K2 project. That modularity is
demonstrated by the layout of the code under com.google.k2.internal:

* common: This is where internal utilities go and classes used widely across all
parts of K2 and plugins.
* keys: This package defines the structure of cryptographic keys supported by K2. There is
a core set of key types that are supported. But plugins may extend this by extending KeyStorage.
* keytool: This package includes the default KeyTool, which does not allow manipulation of keys. A
real KeyTool is defined in a plugin, so that clients may only link in that functionality if they
choose.
* logger: This package includes the default NoOpLogger. Logging plugins should put their code in
this package.
* messages: This package provides the interfaces and some standard implementations for handling
K2's internal message structure.
* monitor: This package includes the default NoOpMonitor. Monitoring plugins should put their code
in this package.
* operations: This package provides the default K2 operations and their implementations.  Plugin
authors and extend OperationFactory and provide their own operations, although this is strongly
discouraged unless your operations get reviewed by competent cryptographers. Many attacks on
protected data actually attack the operation, and not the primitives involved.
* primitives: This packages provides the default K2 primitives.  Implementations are provided by
plugins - for instance k2-jce provides primitives that use the JCE library. Someone could write
a K2-bouncycastle plugin to use bouncycastle instead. Be very careful about creating your own
implementations of cryptographic primitives - this is notoriously tricky programming, easy to get
wrong, and mistakes can totally invalidated the security of a system.
