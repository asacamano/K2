package com.google.k2.testimpls;

import com.google.k2.api.exceptions.CantReadException;
import com.google.k2.api.exceptions.CantWriteException;
import com.google.k2.api.exceptions.MessageAuthenticationException;
import com.google.k2.internal.common.Util;
import com.google.k2.internal.keys.HashKey;
import com.google.k2.internal.messages.Readable;
import com.google.k2.internal.messages.Writable;
import com.google.k2.internal.primitives.KeyedHashPrimitive;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

public class TestKeyedHashPrimitive implements KeyedHashPrimitive {

  private HashKey key = null;
  private ByteBuffer hash = null;
  AtomicLong bytes = new AtomicLong(0);

  @Override
  public void setKey(HashKey key) {
    if (this.key != null) {
      throw new IllegalStateException("Can't set key more than once");
    }
    this.key = Util.checkNotNull(key, "key");
    this.hash = ByteBuffer.allocate(key.getHmacSize() / 8);
  }

  @Override
  public Writable wrap(Writable core) {
    return new TestKeyedHashWritable(key, core, bytes);
  }

  @Override
  public Readable wrap(Readable core) {
    return new TestKeyedHashReadable(key, core, bytes);
  }

  @Override
  public int getHashLength() {
    return key.getHmacSize();
  }

  @Override
  public void writeHash(Writable hashPart) throws CantWriteException {
    hash.putLong(bytes.get());
    hash.rewind();
    hashPart.fillFrom(hash);
  }

  @Override
  public void verify(Readable providedHash)
      throws MessageAuthenticationException, CantReadException {
    ByteBuffer provided = ByteBuffer.allocate(key.getHmacSize() / 8);
    providedHash.writeTo(provided);
    // TODO(asacamano@gmail.com) Think about making this a utility method - comparing ByteBUffers
    // in a timing attack resistent way
    provided.rewind();
    hash.putLong(bytes.get());
    hash.rewind();
    // Don't be an oracle
    boolean match = true;
    if (hash.limit() != provided.limit()) {
      match = false;
    }
    // TODO(asacamano@gmail.com) Could this be optimized away by the JIT
    int limit = Math.min(hash.limit(), provided.limit());
    for (int i = 0; i < limit; i++) {
      if (provided.get() != hash.get()) {
        match = false;
      }
    }
    if (!match) {
      throw new MessageAuthenticationException(
          "MAC doesn't match - message source or integrity is not verified.");
    }
  }

}
