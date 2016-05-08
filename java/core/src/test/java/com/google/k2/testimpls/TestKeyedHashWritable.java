package com.google.k2.testimpls;

import com.google.k2.api.exceptions.CantWriteException;
import com.google.k2.internal.keys.HashKey;
import com.google.k2.internal.messages.Writable;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A {@link Writable} that updates a "hash" as things are written
 */
public class TestKeyedHashWritable implements Writable {

  private final Writable core;
  private final AtomicLong bytes;

  public TestKeyedHashWritable(HashKey key, Writable core, AtomicLong bytes) {
    this.core = core;
    this.bytes = bytes;
  }

  @Override
  public void fillFrom(ByteBuffer buffer) throws CantWriteException {
    int available = buffer.remaining();
    core.fillFrom(buffer);
    int written = available - buffer.remaining();
    bytes.addAndGet(written);
  }
}
