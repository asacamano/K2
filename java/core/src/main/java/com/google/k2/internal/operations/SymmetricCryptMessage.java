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
package com.google.k2.internal.operations;

import static com.google.k2.internal.messages.MessageFieldType.BYTES_FIXED;
import static com.google.k2.internal.messages.MessageFieldType.BYTES_VARIABLE;
import static com.google.k2.internal.messages.MessageFieldType.INTEGER;

import com.google.k2.api.Key;
import com.google.k2.api.exceptions.NoSuchMessageVersionException;
import com.google.k2.internal.keys.SymmetricCryptKey;
import com.google.k2.internal.messages.MessageField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the versions of the message used by {@link SymmetricCryptWrap} and
 * {@link SymmetricCryptUnwrap}
 */
public class SymmetricCryptMessage {

  /**
   * Defines the SymmetricCryptMessage structure for the given version and key.
   */
  public static List<MessageField> getVersion(int version, Key key)
      throws NoSuchMessageVersionException {
    if (version != 0) {
      throw new NoSuchMessageVersionException("Unknown version " + version + " for SymmetricCrypt");
    }
    if (!(key instanceof SymmetricCryptKey)) {
      throw new IllegalArgumentException(
          "Asked to perform SymmetricDecryption with the wrong kind of key");
    }
    SymmetricCryptKey symmetricCryptingKey = (SymmetricCryptKey) key;

    List<MessageField> fields = new ArrayList<MessageField>();
    fields.add(new MessageField("version", INTEGER, 1));
    fields.add(new MessageField("iv", BYTES_FIXED, symmetricCryptingKey.getIvSize()));
    fields.add(new MessageField("ciphertext", BYTES_VARIABLE, 1));
    return Collections.unmodifiableList(fields);
  }
}
