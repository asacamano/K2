package com.google.k2.testimpls;

import com.google.k2.api.exceptions.CantReadException;
import com.google.k2.internal.keys.HashKey;
import com.google.k2.internal.messages.Readable;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A {@link Readable} that updates a "hash" as things are written
 */
public class TestKeyedHashReadable implements Readable {

  private final Readable core;
  private final AtomicLong bytes;

  public TestKeyedHashReadable(HashKey key, Readable core, AtomicLong bytes) {
    this.core = core;
    this.bytes = bytes;
  }

  @Override
  public void writeTo(ByteBuffer buffer) throws CantReadException {
    int available = buffer.remaining();
    core.writeTo(buffer);
    int written = available - buffer.remaining();
    bytes.addAndGet(written);
  }

  @Override
  public boolean hasMoreToRead() throws CantReadException {
    return core.hasMoreToRead();
  }
}
