/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.ehcache;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;

public class MapBenchmark {

  public static final int DATASET_SIZE = 2_000_000;
  static Random random = new Random();

  @State(Scope.Benchmark)
  public static class CacheHolder {
    private ConcurrentHashMap<Long, String> map;

    @Setup
    public void setUp() {
      map = new ConcurrentHashMap<Long, String>();
      for (long l = 1L; l < DATASET_SIZE; l++) {
        map.put(l, "aValue" + l);
      }
    }

  }

  @State(Scope.Thread)
  public static class SequenceHolder {
    long index = random.nextLong() % DATASET_SIZE;
    public long getKey() {
      return abs(index++ % DATASET_SIZE);
    }
  }


  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  public Object testComputeIfAbsentCHM(CacheHolder cacheHolder, SequenceHolder sequenceHolder) {
    long key = sequenceHolder.getKey();
    return cacheHolder.map.computeIfAbsent(key, k -> "SomeValue" + k);
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  public Object testComputeIfPresentCHM(CacheHolder cacheHolder, SequenceHolder sequenceHolder) {
    long key = sequenceHolder.getKey();
    return cacheHolder.map.computeIfPresent(key, (k, v) -> v);
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  public Object testGetCHM(CacheHolder cacheHolder, SequenceHolder sequenceHolder) {
    return cacheHolder.map.get(sequenceHolder.getKey());
  }
}
