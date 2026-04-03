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

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ignite.internal.processors.cluster.CacheMetricsMessage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Correctness test for {@link JmhCacheMetricsSerializationBenchmark}.
 *
 * <p>Verifies that both the new (MessageSerializer) and legacy (inline writeTo/readFrom)
 * approaches produce identical round-trip results, and that all benchmark methods
 * complete without errors.
 */
public class JmhCacheMetricsSerializationBenchmarkTest {
    /** */
    private JmhCacheMetricsSerializationBenchmark benchmark;

    /** */
    @Before
    public void setUp() throws Exception {
        benchmark = new JmhCacheMetricsSerializationBenchmark();
        benchmark.setup();
    }

    /** New writeTo must complete in a single call. */
    @Test
    public void testNewWriteToCompletes() {
        assertTrue("newWriteTo must return true", benchmark.newWriteTo());
    }

    /** New readFrom must complete in a single call. */
    @Test
    public void testNewReadFromCompletes() {
        assertTrue("newReadFrom must return true", benchmark.newReadFrom());
    }

    /** Legacy writeTo must complete in a single call. */
    @Test
    public void testLegacyWriteToCompletes() {
        assertTrue("legacyWriteTo must return true", benchmark.legacyWriteTo());
    }

    /** Legacy readFrom must complete in a single call. */
    @Test
    public void testLegacyReadFromCompletes() {
        assertTrue("legacyReadFrom must return true", benchmark.legacyReadFrom());
    }

    /** Round-trip for new approach preserves all fields. */
    @Test
    public void testNewRoundTripPreservesFields() throws Exception {
        assertTrue(benchmark.newWriteTo());
        assertTrue(benchmark.newReadFrom());

        var src = (CacheMetricsMessage)FieldUtils.readField(benchmark, "newMsg", true);
        var dst = (CacheMetricsMessage)FieldUtils.readField(benchmark, "newReadTarget", true);

        assertCacheMetricsEqual(src, dst);
    }

    /** Round-trip for legacy approach preserves all fields. */
    @Test
    public void testLegacyRoundTripPreservesFields() throws Exception {
        assertTrue(benchmark.legacyWriteTo());
        assertTrue(benchmark.legacyReadFrom());

        var src = (LegacyCacheMetricsMessage)FieldUtils.readField(benchmark, "legacyMsg", true);
        var dst = (LegacyCacheMetricsMessage)FieldUtils.readField(benchmark, "legacyReadTarget", true);

        assertLegacyCacheMetricsEqual(src, dst);
    }

    // ----- helpers -----

    /** Asserts all 87 fields of {@link CacheMetricsMessage} are equal. */
    private static void assertCacheMetricsEqual(CacheMetricsMessage src, CacheMetricsMessage dst) {
        assertEquals("cacheGets", src.cacheGets, dst.cacheGets);
        assertEquals("cachePuts", src.cachePuts, dst.cachePuts);
        assertEquals("entryProcessorPuts", src.entryProcessorPuts, dst.entryProcessorPuts);
        assertEquals("entryProcessorReadOnlyInvocations", src.entryProcessorReadOnlyInvocations, dst.entryProcessorReadOnlyInvocations);
        assertEquals("entryProcessorAverageInvocationTime",
            src.entryProcessorAverageInvocationTime, dst.entryProcessorAverageInvocationTime, 0.0f);
        assertEquals("entryProcessorInvocations", src.entryProcessorInvocations, dst.entryProcessorInvocations);
        assertEquals("entryProcessorRemovals", src.entryProcessorRemovals, dst.entryProcessorRemovals);
        assertEquals("entryProcessorMisses", src.entryProcessorMisses, dst.entryProcessorMisses);
        assertEquals("entryProcessorHits", src.entryProcessorHits, dst.entryProcessorHits);
        assertEquals("entryProcessorMissPercentage",
            src.entryProcessorMissPercentage, dst.entryProcessorMissPercentage, 0.0f);
        assertEquals("entryProcessorHitPercentage",
            src.entryProcessorHitPercentage, dst.entryProcessorHitPercentage, 0.0f);
        assertEquals("entryProcessorMaxInvocationTime", src.entryProcessorMaxInvocationTime, dst.entryProcessorMaxInvocationTime, 0.0f);
        assertEquals("entryProcessorMinInvocationTime", src.entryProcessorMinInvocationTime, dst.entryProcessorMinInvocationTime, 0.0f);
        assertEquals("cacheHits", src.cacheHits, dst.cacheHits);
        assertEquals("cacheMisses", src.cacheMisses, dst.cacheMisses);
        assertEquals("cacheTxCommits", src.cacheTxCommits, dst.cacheTxCommits);
        assertEquals("cacheTxRollbacks", src.cacheTxRollbacks, dst.cacheTxRollbacks);
        assertEquals("cacheEvictions", src.cacheEvictions, dst.cacheEvictions);
        assertEquals("cacheRemovals", src.cacheRemovals, dst.cacheRemovals);
        assertEquals("averagePutTime", src.averagePutTime, dst.averagePutTime, 0.0f);
        assertEquals("averageGetTime", src.averageGetTime, dst.averageGetTime, 0.0f);
        assertEquals("averageRemoveTime", src.averageRemoveTime, dst.averageRemoveTime, 0.0f);
        assertEquals("averageTxCommitTime", src.averageTxCommitTime, dst.averageTxCommitTime, 0.0f);
        assertEquals("averageTxRollbackTime", src.averageTxRollbackTime, dst.averageTxRollbackTime, 0.0f);
        assertEquals("cacheName", src.cacheName, dst.cacheName);
        assertEquals("offHeapGets", src.offHeapGets, dst.offHeapGets);
        assertEquals("offHeapPuts", src.offHeapPuts, dst.offHeapPuts);
        assertEquals("offHeapRemoves", src.offHeapRemoves, dst.offHeapRemoves);
        assertEquals("offHeapEvicts", src.offHeapEvicts, dst.offHeapEvicts);
        assertEquals("offHeapHits", src.offHeapHits, dst.offHeapHits);
        assertEquals("offHeapMisses", src.offHeapMisses, dst.offHeapMisses);
        assertEquals("offHeapEntriesCnt", src.offHeapEntriesCnt, dst.offHeapEntriesCnt);
        assertEquals("heapEntriesCnt", src.heapEntriesCnt, dst.heapEntriesCnt);
        assertEquals("offHeapPrimaryEntriesCnt", src.offHeapPrimaryEntriesCnt, dst.offHeapPrimaryEntriesCnt);
        assertEquals("offHeapBackupEntriesCnt", src.offHeapBackupEntriesCnt, dst.offHeapBackupEntriesCnt);
        assertEquals("offHeapAllocatedSize", src.offHeapAllocatedSize, dst.offHeapAllocatedSize);
        assertEquals("size", src.size, dst.size);
        assertEquals("cacheSize", src.cacheSize, dst.cacheSize);
        assertEquals("keySize", src.keySize, dst.keySize);
        assertEquals("empty", src.empty, dst.empty);
        assertEquals("dhtEvictQueueCurrSize", src.dhtEvictQueueCurrSize, dst.dhtEvictQueueCurrSize);
        assertEquals("txThreadMapSize", src.txThreadMapSize, dst.txThreadMapSize);
        assertEquals("txXidMapSize", src.txXidMapSize, dst.txXidMapSize);
        assertEquals("txCommitQueueSize", src.txCommitQueueSize, dst.txCommitQueueSize);
        assertEquals("txPrepareQueueSize", src.txPrepareQueueSize, dst.txPrepareQueueSize);
        assertEquals("txStartVerCountsSize", src.txStartVerCountsSize, dst.txStartVerCountsSize);
        assertEquals("txCommittedVersionsSize", src.txCommittedVersionsSize, dst.txCommittedVersionsSize);
        assertEquals("txRolledbackVersionsSize", src.txRolledbackVersionsSize, dst.txRolledbackVersionsSize);
        assertEquals("txDhtThreadMapSize", src.txDhtThreadMapSize, dst.txDhtThreadMapSize);
        assertEquals("txDhtXidMapSize", src.txDhtXidMapSize, dst.txDhtXidMapSize);
        assertEquals("txDhtCommitQueueSize", src.txDhtCommitQueueSize, dst.txDhtCommitQueueSize);
        assertEquals("txDhtPrepareQueueSize", src.txDhtPrepareQueueSize, dst.txDhtPrepareQueueSize);
        assertEquals("txDhtStartVerCountsSize", src.txDhtStartVerCountsSize, dst.txDhtStartVerCountsSize);
        assertEquals("txDhtCommittedVersionsSize", src.txDhtCommittedVersionsSize, dst.txDhtCommittedVersionsSize);
        assertEquals("txDhtRolledbackVersionsSize", src.txDhtRolledbackVersionsSize, dst.txDhtRolledbackVersionsSize);
        assertEquals("writeBehindEnabled", src.writeBehindEnabled, dst.writeBehindEnabled);
        assertEquals("writeBehindFlushSize", src.writeBehindFlushSize, dst.writeBehindFlushSize);
        assertEquals("writeBehindFlushThreadCnt", src.writeBehindFlushThreadCnt, dst.writeBehindFlushThreadCnt);
        assertEquals("writeBehindFlushFreq", src.writeBehindFlushFreq, dst.writeBehindFlushFreq);
        assertEquals("writeBehindStoreBatchSize", src.writeBehindStoreBatchSize, dst.writeBehindStoreBatchSize);
        assertEquals("writeBehindTotalCriticalOverflowCnt",
            src.writeBehindTotalCriticalOverflowCnt, dst.writeBehindTotalCriticalOverflowCnt);
        assertEquals("writeBehindCriticalOverflowCnt", src.writeBehindCriticalOverflowCnt, dst.writeBehindCriticalOverflowCnt);
        assertEquals("writeBehindErrorRetryCnt", src.writeBehindErrorRetryCnt, dst.writeBehindErrorRetryCnt);
        assertEquals("writeBehindBufSize", src.writeBehindBufSize, dst.writeBehindBufSize);
        assertEquals("totalPartitionsCnt", src.totalPartitionsCnt, dst.totalPartitionsCnt);
        assertEquals("rebalancingPartitionsCnt", src.rebalancingPartitionsCnt, dst.rebalancingPartitionsCnt);
        assertEquals("rebalancedKeys", src.rebalancedKeys, dst.rebalancedKeys);
        assertEquals("estimatedRebalancingKeys", src.estimatedRebalancingKeys, dst.estimatedRebalancingKeys);
        assertEquals("keysToRebalanceLeft", src.keysToRebalanceLeft, dst.keysToRebalanceLeft);
        assertEquals("rebalancingKeysRate", src.rebalancingKeysRate, dst.rebalancingKeysRate);
        assertEquals("rebalancingBytesRate", src.rebalancingBytesRate, dst.rebalancingBytesRate);
        assertEquals("rebalanceStartTime", src.rebalanceStartTime, dst.rebalanceStartTime);
        assertEquals("rebalanceFinishTime", src.rebalanceFinishTime, dst.rebalanceFinishTime);
        assertEquals("rebalanceClearingPartitionsLeft", src.rebalanceClearingPartitionsLeft, dst.rebalanceClearingPartitionsLeft);
        assertEquals("keyType", src.keyType, dst.keyType);
        assertEquals("valType", src.valType, dst.valType);
        assertEquals("storeByVal", src.storeByVal, dst.storeByVal);
        assertEquals("statisticsEnabled", src.statisticsEnabled, dst.statisticsEnabled);
        assertEquals("managementEnabled", src.managementEnabled, dst.managementEnabled);
        assertEquals("readThrough", src.readThrough, dst.readThrough);
        assertEquals("writeThrough", src.writeThrough, dst.writeThrough);
        assertEquals("validForReading", src.validForReading, dst.validForReading);
        assertEquals("validForWriting", src.validForWriting, dst.validForWriting);
        assertEquals("txKeyCollisions", src.txKeyCollisions, dst.txKeyCollisions);
        assertEquals("idxRebuildInProgress", src.idxRebuildInProgress, dst.idxRebuildInProgress);
        assertEquals("idxRebuildKeyProcessed", src.idxRebuildKeyProcessed, dst.idxRebuildKeyProcessed);
        assertEquals("idxBuildPartitionsLeftCount", src.idxBuildPartitionsLeftCount, dst.idxBuildPartitionsLeftCount);
    }

    /** Asserts all 87 fields of {@link LegacyCacheMetricsMessage} are equal. */
    private static void assertLegacyCacheMetricsEqual(LegacyCacheMetricsMessage src, LegacyCacheMetricsMessage dst) {
        assertEquals("cacheGets", src.cacheGets, dst.cacheGets);
        assertEquals("cachePuts", src.cachePuts, dst.cachePuts);
        assertEquals("entryProcessorPuts", src.entryProcessorPuts, dst.entryProcessorPuts);
        assertEquals("entryProcessorReadOnlyInvocations",
            src.entryProcessorReadOnlyInvocations, dst.entryProcessorReadOnlyInvocations);
        assertEquals("entryProcessorAverageInvocationTime",
            src.entryProcessorAverageInvocationTime, dst.entryProcessorAverageInvocationTime, 0.0f);
        assertEquals("entryProcessorInvocations", src.entryProcessorInvocations, dst.entryProcessorInvocations);
        assertEquals("entryProcessorRemovals", src.entryProcessorRemovals, dst.entryProcessorRemovals);
        assertEquals("entryProcessorMisses", src.entryProcessorMisses, dst.entryProcessorMisses);
        assertEquals("entryProcessorHits", src.entryProcessorHits, dst.entryProcessorHits);
        assertEquals("cacheName", src.cacheName, dst.cacheName);
        assertEquals("size", src.size, dst.size);
        assertEquals("cacheSize", src.cacheSize, dst.cacheSize);
        assertEquals("keySize", src.keySize, dst.keySize);
        assertEquals("empty", src.empty, dst.empty);
        assertEquals("writeBehindEnabled", src.writeBehindEnabled, dst.writeBehindEnabled);
        assertEquals("keyType", src.keyType, dst.keyType);
        assertEquals("valType", src.valType, dst.valType);
        assertEquals("txKeyCollisions", src.txKeyCollisions, dst.txKeyCollisions);
        assertEquals("idxRebuildInProgress", src.idxRebuildInProgress, dst.idxRebuildInProgress);
        assertEquals("idxRebuildKeyProcessed", src.idxRebuildKeyProcessed, dst.idxRebuildKeyProcessed);
        assertEquals("idxBuildPartitionsLeftCount", src.idxBuildPartitionsLeftCount, dst.idxBuildPartitionsLeftCount);
    }
}
