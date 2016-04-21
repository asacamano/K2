package com.google.k2.testimpls;

import com.google.k2.api.exceptions.CantReadException;
import com.google.k2.api.exceptions.CantWriteException;
import com.google.k2.internal.common.Util;
import com.google.k2.internal.keys.SymmetricCryptKey;
import com.google.k2.internal.messages.Readable;
import com.google.k2.internal.messages.Writable;
import com.google.k2.internal.primitives.SymmetricCryptPrimitive;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

public class TestSymmetricCryptPrimitive implements SymmetricCryptPrimitive {

  private static AtomicLong iv = new AtomicLong();

  @Override
  public void crypt(SymmetricCryptKey key, byte[] iv, Readable input, Writable output)
      throws CantReadException, CantWriteException {
    if (iv.length * 8 != key.getIvSize()) {
      throw new CantReadException(
          "Bad IV length was " + (iv.length * 8) + ", expected " + key.getIvSize());
    }
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    // Start by checking for "encrypt:" to determine the action here, because unlike real symmetric
    // crypto, test crypto doesn't reverse itself.
    if (input.hasMoreToRead()) {
      // Put the input in a temporary buffer
      input.writeTo(buffer);
      buffer.flip();

      // Now figure out if it starts with "encrypt:"
      boolean encrypt = true;
      if (buffer.limit() >= 8) {
        byte[] opening8 = new byte[8];
        buffer.get(opening8);
        if ("encrypt:".equals(new String(opening8, Util.UTF_8))) {
          // It does start with "encrypt:" - so this is a decryption operation, so leave the buffer
          // where it is - just past "encrypt:"
          encrypt = false;
        } else {
          // It does not start with "encrypt:" - so this is an encryption operation, so rewind the
          // buffer
          buffer.rewind();
        }
      }
      if (encrypt) {
        output.fillFrom(ByteBuffer.wrap("encrypt:".getBytes(Util.UTF_8)));
      }
      output.fillFrom(buffer);
    }
    // Now just copy the rest
    while (input.hasMoreToRead()) {
      buffer.rewind();
      input.writeTo(buffer);
      output.fillFrom(buffer);
    }
  }

  @Override
  public byte[] generateNewIv(SymmetricCryptKey key) {
    return ByteBuffer.allocate(16).putLong(iv.getAndIncrement()).array();
  }

}
