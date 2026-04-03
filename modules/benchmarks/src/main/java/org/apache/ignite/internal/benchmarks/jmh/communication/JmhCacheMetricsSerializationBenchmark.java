/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.benchmarks.jmh.communication;

import java.nio.ByteBuffer;
import org.apache.ignite.internal.direct.DirectMessageReader;
import org.apache.ignite.internal.direct.DirectMessageWriter;
import org.apache.ignite.internal.managers.communication.GridIoMessageFactory;
import org.apache.ignite.internal.managers.communication.IgniteMessageFactoryImpl;
import org.apache.ignite.internal.processors.cluster.CacheMetricsMessage;
import org.apache.ignite.internal.processors.cluster.CacheMetricsMessageSerializer;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.plugin.extensions.communication.MessageFactory;
import org.apache.ignite.plugin.extensions.communication.MessageFactoryProvider;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.ignite.marshaller.Marshallers.jdk;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

/**
 * Benchmark comparing legacy (inline {@code writeTo}/{@code readFrom}) vs new ({@code MessageSerializer})
 * serialization approaches for {@code CacheMetricsMessage} with 87 simple fields.
 *
 * <p>Run via IDE with {@link #main} or as an uber-jar:
 * <pre>
 *   java -jar target/benchmarks.jar JmhCacheMetricsSerializationBenchmark
 * </pre>
 */
@State(Scope.Thread)
@OutputTimeUnit(NANOSECONDS)
@BenchmarkMode(AverageTime)
@Warmup(iterations = 3, time = 10, timeUnit = SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = SECONDS)
public class JmhCacheMetricsSerializationBenchmark {
    /** Buffer capacity — large enough for a fully serialized CacheMetricsMessage. */
    private static final int BUF_CAPACITY = 8 * 1024;

    // ----- New approach (MessageSerializer) -----

    /** New-style message. */
    private CacheMetricsMessage newMsg;

    /** New-style serializer. */
    private CacheMetricsMessageSerializer newSerializer;

    /** Writer for new-style benchmarks. */
    private DirectMessageWriter newWriter;

    /** Reader for new-style benchmarks. */
    private DirectMessageReader newReader;

    /** Write buffer for new-style benchmarks. */
    private ByteBuffer newWriteBuf;

    /** Pre-filled read buffer for new-style benchmarks. */
    private ByteBuffer newReadBuf;

    /** Reusable deserialization target for new-style benchmarks. */
    private CacheMetricsMessage newReadTarget;

    // ----- Legacy approach (inline writeTo/readFrom) -----

    /** Legacy message. */
    private LegacyCacheMetricsMessage legacyMsg;

    /** Writer for legacy benchmarks. */
    private DirectMessageWriter legacyWriter;

    /** Reader for legacy benchmarks. */
    private DirectMessageReader legacyReader;

    /** Write buffer for legacy benchmarks. */
    private ByteBuffer legacyWriteBuf;

    /** Pre-filled read buffer for legacy benchmarks. */
    private ByteBuffer legacyReadBuf;

    /** Reusable deserialization target for legacy benchmarks. */
    private LegacyCacheMetricsMessage legacyReadTarget;

    /** */
    @Setup
    public void setup() throws Exception {
        MessageFactory factory = new IgniteMessageFactoryImpl(
            new MessageFactoryProvider[]{new GridIoMessageFactory(jdk(), U.gridClassLoader())}
        );

        // --- New approach setup ---
        newMsg = buildNewMessage();
        newSerializer = new CacheMetricsMessageSerializer();
        newWriteBuf = ByteBuffer.allocateDirect(BUF_CAPACITY);
        newReadBuf = ByteBuffer.allocateDirect(BUF_CAPACITY);
        newWriter = new DirectMessageWriter(factory);
        newReader = new DirectMessageReader(factory, null);

        newWriter.setBuffer(newWriteBuf);

        if (!newSerializer.writeTo(newMsg, newWriter))
            throw new IllegalStateException("Write buffer is too small for new message");

        newWriteBuf.flip();
        newWriteBuf.position(Short.BYTES); // skip directType header
        newReadBuf.put(newWriteBuf);
        newReadBuf.flip();

        newWriteBuf.clear();
        newWriter.reset();

        newReadTarget = new CacheMetricsMessage();

        // --- Legacy approach setup ---
        legacyMsg = buildLegacyMessage();
        legacyWriteBuf = ByteBuffer.allocateDirect(BUF_CAPACITY);
        legacyReadBuf = ByteBuffer.allocateDirect(BUF_CAPACITY);
        legacyWriter = new DirectMessageWriter(factory);
        legacyReader = new DirectMessageReader(factory, null);

        legacyWriter.setBuffer(legacyWriteBuf);

        if (!legacyMsg.writeTo(legacyWriteBuf, legacyWriter))
            throw new IllegalStateException("Write buffer is too small for legacy message");

        legacyWriteBuf.flip();
        legacyWriteBuf.position(Short.BYTES); // skip directType header
        legacyReadBuf.put(legacyWriteBuf);
        legacyReadBuf.flip();

        legacyWriteBuf.clear();
        legacyWriter.reset();

        legacyReadTarget = new LegacyCacheMetricsMessage();
    }

    // ----- New approach benchmarks -----

    /** Measures serialization cost using the new {@code MessageSerializer} approach. */
    @Benchmark
    public boolean newWriteTo() {
        newWriteBuf.clear();
        newWriter.reset();
        newWriter.setBuffer(newWriteBuf);

        return newSerializer.writeTo(newMsg, newWriter);
    }

    /** Measures deserialization cost using the new {@code MessageSerializer} approach. */
    @Benchmark
    public boolean newReadFrom() {
        newReadBuf.rewind();
        newReader.reset();
        newReader.setBuffer(newReadBuf);

        return newSerializer.readFrom(newReadTarget, newReader);
    }

    // ----- Legacy approach benchmarks -----

    /** Measures serialization cost using the legacy inline {@code writeTo} approach. */
    @Benchmark
    public boolean legacyWriteTo() {
        legacyWriteBuf.clear();
        legacyWriter.reset();

        return legacyMsg.writeTo(legacyWriteBuf, legacyWriter);
    }

    /** Measures deserialization cost using the legacy inline {@code readFrom} approach. */
    @Benchmark
    public boolean legacyReadFrom() {
        legacyReadBuf.rewind();
        legacyReader.reset();

        return legacyReadTarget.readFrom(legacyReadBuf, legacyReader);
    }

    /** */
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(new String[]{JmhCacheMetricsSerializationBenchmark.class.getSimpleName()});
    }

    // ----- helpers -----

    /** Builds a fully populated {@link CacheMetricsMessage}. */
    private static CacheMetricsMessage buildNewMessage() {
        var msg = new CacheMetricsMessage();

        populateFields(msg);

        return msg;
    }

    /** Builds a fully populated {@link LegacyCacheMetricsMessage}. */
    private static LegacyCacheMetricsMessage buildLegacyMessage() {
        var msg = new LegacyCacheMetricsMessage();

        populateFields(msg);

        return msg;
    }

    /**
     * Populates all 87 fields with realistic non-zero values.
     * Works for both new and legacy messages via direct field assignment
     * (both classes have the same public fields).
     */
    private static void populateFields(CacheMetricsMessage msg) {
        msg.cacheGets = 100_000L;
        msg.cachePuts = 50_000L;
        msg.entryProcessorPuts = 1_000L;
        msg.entryProcessorReadOnlyInvocations = 2_000L;
        msg.entryProcessorAverageInvocationTime = 0.5f;
        msg.entryProcessorInvocations = 3_000L;
        msg.entryProcessorRemovals = 500L;
        msg.entryProcessorMisses = 200L;
        msg.entryProcessorHits = 2_800L;
        msg.entryProcessorMissPercentage = 6.67f;
        msg.entryProcessorHitPercentage = 93.33f;
        msg.entryProcessorMaxInvocationTime = 10.0f;
        msg.entryProcessorMinInvocationTime = 0.01f;
        msg.cacheHits = 95_000L;
        msg.cacheMisses = 5_000L;
        msg.cacheTxCommits = 10_000L;
        msg.cacheTxRollbacks = 100L;
        msg.cacheEvictions = 1_000L;
        msg.cacheRemovals = 2_000L;
        msg.averagePutTime = 0.3f;
        msg.averageGetTime = 0.1f;
        msg.averageRemoveTime = 0.2f;
        msg.averageTxCommitTime = 1.5f;
        msg.averageTxRollbackTime = 0.8f;
        msg.cacheName = "test-cache";
        msg.offHeapGets = 80_000L;
        msg.offHeapPuts = 40_000L;
        msg.offHeapRemoves = 1_500L;
        msg.offHeapEvicts = 500L;
        msg.offHeapHits = 75_000L;
        msg.offHeapMisses = 5_000L;
        msg.offHeapEntriesCnt = 100_000L;
        msg.heapEntriesCnt = 10_000L;
        msg.offHeapPrimaryEntriesCnt = 70_000L;
        msg.offHeapBackupEntriesCnt = 30_000L;
        msg.offHeapAllocatedSize = 1_073_741_824L;
        msg.size = 100_000;
        msg.cacheSize = 100_000L;
        msg.keySize = 100_000;
        msg.empty = false;
        msg.dhtEvictQueueCurrSize = 0;
        msg.txThreadMapSize = 8;
        msg.txXidMapSize = 16;
        msg.txCommitQueueSize = 4;
        msg.txPrepareQueueSize = 2;
        msg.txStartVerCountsSize = 32;
        msg.txCommittedVersionsSize = 64;
        msg.txRolledbackVersionsSize = 8;
        msg.txDhtThreadMapSize = 8;
        msg.txDhtXidMapSize = 16;
        msg.txDhtCommitQueueSize = 4;
        msg.txDhtPrepareQueueSize = 2;
        msg.txDhtStartVerCountsSize = 32;
        msg.txDhtCommittedVersionsSize = 64;
        msg.txDhtRolledbackVersionsSize = 8;
        msg.writeBehindEnabled = true;
        msg.writeBehindFlushSize = 10_240;
        msg.writeBehindFlushThreadCnt = 4;
        msg.writeBehindFlushFreq = 5_000L;
        msg.writeBehindStoreBatchSize = 512;
        msg.writeBehindTotalCriticalOverflowCnt = 0;
        msg.writeBehindCriticalOverflowCnt = 0;
        msg.writeBehindErrorRetryCnt = 0;
        msg.writeBehindBufSize = 1_024;
        msg.totalPartitionsCnt = 1_024;
        msg.rebalancingPartitionsCnt = 0;
        msg.rebalancedKeys = 500_000L;
        msg.estimatedRebalancingKeys = 500_000L;
        msg.keysToRebalanceLeft = 0L;
        msg.rebalancingKeysRate = 10_000L;
        msg.rebalancingBytesRate = 1_048_576L;
        msg.rebalanceStartTime = System.currentTimeMillis() - 60_000L;
        msg.rebalanceFinishTime = System.currentTimeMillis();
        msg.rebalanceClearingPartitionsLeft = 0L;
        msg.keyType = "java.lang.Integer";
        msg.valType = "java.lang.String";
        msg.storeByVal = true;
        msg.statisticsEnabled = true;
        msg.managementEnabled = false;
        msg.readThrough = true;
        msg.writeThrough = true;
        msg.validForReading = true;
        msg.validForWriting = true;
        msg.txKeyCollisions = "";
        msg.idxRebuildInProgress = false;
        msg.idxRebuildKeyProcessed = 0L;
        msg.idxBuildPartitionsLeftCount = 0;
    }

    /** Populates all fields for the legacy message (identical values). */
    private static void populateFields(LegacyCacheMetricsMessage msg) {
        msg.cacheGets = 100_000L;
        msg.cachePuts = 50_000L;
        msg.entryProcessorPuts = 1_000L;
        msg.entryProcessorReadOnlyInvocations = 2_000L;
        msg.entryProcessorAverageInvocationTime = 0.5f;
        msg.entryProcessorInvocations = 3_000L;
        msg.entryProcessorRemovals = 500L;
        msg.entryProcessorMisses = 200L;
        msg.entryProcessorHits = 2_800L;
        msg.entryProcessorMissPercentage = 6.67f;
        msg.entryProcessorHitPercentage = 93.33f;
        msg.entryProcessorMaxInvocationTime = 10.0f;
        msg.entryProcessorMinInvocationTime = 0.01f;
        msg.cacheHits = 95_000L;
        msg.cacheMisses = 5_000L;
        msg.cacheTxCommits = 10_000L;
        msg.cacheTxRollbacks = 100L;
        msg.cacheEvictions = 1_000L;
        msg.cacheRemovals = 2_000L;
        msg.averagePutTime = 0.3f;
        msg.averageGetTime = 0.1f;
        msg.averageRemoveTime = 0.2f;
        msg.averageTxCommitTime = 1.5f;
        msg.averageTxRollbackTime = 0.8f;
        msg.cacheName = "test-cache";
        msg.offHeapGets = 80_000L;
        msg.offHeapPuts = 40_000L;
        msg.offHeapRemoves = 1_500L;
        msg.offHeapEvicts = 500L;
        msg.offHeapHits = 75_000L;
        msg.offHeapMisses = 5_000L;
        msg.offHeapEntriesCnt = 100_000L;
        msg.heapEntriesCnt = 10_000L;
        msg.offHeapPrimaryEntriesCnt = 70_000L;
        msg.offHeapBackupEntriesCnt = 30_000L;
        msg.offHeapAllocatedSize = 1_073_741_824L;
        msg.size = 100_000;
        msg.cacheSize = 100_000L;
        msg.keySize = 100_000;
        msg.empty = false;
        msg.dhtEvictQueueCurrSize = 0;
        msg.txThreadMapSize = 8;
        msg.txXidMapSize = 16;
        msg.txCommitQueueSize = 4;
        msg.txPrepareQueueSize = 2;
        msg.txStartVerCountsSize = 32;
        msg.txCommittedVersionsSize = 64;
        msg.txRolledbackVersionsSize = 8;
        msg.txDhtThreadMapSize = 8;
        msg.txDhtXidMapSize = 16;
        msg.txDhtCommitQueueSize = 4;
        msg.txDhtPrepareQueueSize = 2;
        msg.txDhtStartVerCountsSize = 32;
        msg.txDhtCommittedVersionsSize = 64;
        msg.txDhtRolledbackVersionsSize = 8;
        msg.writeBehindEnabled = true;
        msg.writeBehindFlushSize = 10_240;
        msg.writeBehindFlushThreadCnt = 4;
        msg.writeBehindFlushFreq = 5_000L;
        msg.writeBehindStoreBatchSize = 512;
        msg.writeBehindTotalCriticalOverflowCnt = 0;
        msg.writeBehindCriticalOverflowCnt = 0;
        msg.writeBehindErrorRetryCnt = 0;
        msg.writeBehindBufSize = 1_024;
        msg.totalPartitionsCnt = 1_024;
        msg.rebalancingPartitionsCnt = 0;
        msg.rebalancedKeys = 500_000L;
        msg.estimatedRebalancingKeys = 500_000L;
        msg.keysToRebalanceLeft = 0L;
        msg.rebalancingKeysRate = 10_000L;
        msg.rebalancingBytesRate = 1_048_576L;
        msg.rebalanceStartTime = System.currentTimeMillis() - 60_000L;
        msg.rebalanceFinishTime = System.currentTimeMillis();
        msg.rebalanceClearingPartitionsLeft = 0L;
        msg.keyType = "java.lang.Integer";
        msg.valType = "java.lang.String";
        msg.storeByVal = true;
        msg.statisticsEnabled = true;
        msg.managementEnabled = false;
        msg.readThrough = true;
        msg.writeThrough = true;
        msg.validForReading = true;
        msg.validForWriting = true;
        msg.txKeyCollisions = "";
        msg.idxRebuildInProgress = false;
        msg.idxRebuildKeyProcessed = 0L;
        msg.idxBuildPartitionsLeftCount = 0;
    }
}
