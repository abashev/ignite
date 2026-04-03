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
import org.apache.ignite.plugin.extensions.communication.Message;
import org.apache.ignite.plugin.extensions.communication.MessageReader;
import org.apache.ignite.plugin.extensions.communication.MessageWriter;

/**
 * Legacy version of {@code CacheMetricsMessage} with inline {@code writeTo}/{@code readFrom}
 * methods (the old code-generated approach). Used for benchmarking against the new
 * {@code MessageSerializer}-based approach.
 *
 * <p>Contains the same 87 simple fields as the real {@code CacheMetricsMessage}.
 */
public class LegacyCacheMetricsMessage implements Message {
    /** */
    public long cacheGets;

    /** */
    public long cachePuts;

    /** */
    public long entryProcessorPuts;

    /** */
    public long entryProcessorReadOnlyInvocations;

    /** */
    public float entryProcessorAverageInvocationTime;

    /** */
    public long entryProcessorInvocations;

    /** */
    public long entryProcessorRemovals;

    /** */
    public long entryProcessorMisses;

    /** */
    public long entryProcessorHits;

    /** */
    public float entryProcessorMissPercentage;

    /** */
    public float entryProcessorHitPercentage;

    /** */
    public float entryProcessorMaxInvocationTime;

    /** */
    public float entryProcessorMinInvocationTime;

    /** */
    public long cacheHits;

    /** */
    public long cacheMisses;

    /** */
    public long cacheTxCommits;

    /** */
    public long cacheTxRollbacks;

    /** */
    public long cacheEvictions;

    /** */
    public long cacheRemovals;

    /** */
    public float averagePutTime;

    /** */
    public float averageGetTime;

    /** */
    public float averageRemoveTime;

    /** */
    public float averageTxCommitTime;

    /** */
    public float averageTxRollbackTime;

    /** */
    public String cacheName;

    /** */
    public long offHeapGets;

    /** */
    public long offHeapPuts;

    /** */
    public long offHeapRemoves;

    /** */
    public long offHeapEvicts;

    /** */
    public long offHeapHits;

    /** */
    public long offHeapMisses;

    /** */
    public long offHeapEntriesCnt;

    /** */
    public long heapEntriesCnt;

    /** */
    public long offHeapPrimaryEntriesCnt;

    /** */
    public long offHeapBackupEntriesCnt;

    /** */
    public long offHeapAllocatedSize;

    /** */
    public int size;

    /** */
    public long cacheSize;

    /** */
    public int keySize;

    /** */
    public boolean empty;

    /** */
    public int dhtEvictQueueCurrSize;

    /** */
    public int txThreadMapSize;

    /** */
    public int txXidMapSize;

    /** */
    public int txCommitQueueSize;

    /** */
    public int txPrepareQueueSize;

    /** */
    public int txStartVerCountsSize;

    /** */
    public int txCommittedVersionsSize;

    /** */
    public int txRolledbackVersionsSize;

    /** */
    public int txDhtThreadMapSize;

    /** */
    public int txDhtXidMapSize;

    /** */
    public int txDhtCommitQueueSize;

    /** */
    public int txDhtPrepareQueueSize;

    /** */
    public int txDhtStartVerCountsSize;

    /** */
    public int txDhtCommittedVersionsSize;

    /** */
    public int txDhtRolledbackVersionsSize;

    /** */
    public boolean writeBehindEnabled;

    /** */
    public int writeBehindFlushSize;

    /** */
    public int writeBehindFlushThreadCnt;

    /** */
    public long writeBehindFlushFreq;

    /** */
    public int writeBehindStoreBatchSize;

    /** */
    public int writeBehindTotalCriticalOverflowCnt;

    /** */
    public int writeBehindCriticalOverflowCnt;

    /** */
    public int writeBehindErrorRetryCnt;

    /** */
    public int writeBehindBufSize;

    /** */
    public int totalPartitionsCnt;

    /** */
    public int rebalancingPartitionsCnt;

    /** */
    public long rebalancedKeys;

    /** */
    public long estimatedRebalancingKeys;

    /** */
    public long keysToRebalanceLeft;

    /** */
    public long rebalancingKeysRate;

    /** */
    public long rebalancingBytesRate;

    /** */
    public long rebalanceStartTime;

    /** */
    public long rebalanceFinishTime;

    /** */
    public long rebalanceClearingPartitionsLeft;

    /** */
    public String keyType;

    /** */
    public String valType;

    /** */
    public boolean storeByVal;

    /** */
    public boolean statisticsEnabled;

    /** */
    public boolean managementEnabled;

    /** */
    public boolean readThrough;

    /** */
    public boolean writeThrough;

    /** */
    public boolean validForReading;

    /** */
    public boolean validForWriting;

    /** */
    public String txKeyCollisions;

    /** */
    public boolean idxRebuildInProgress;

    /** */
    public long idxRebuildKeyProcessed;

    /** */
    public int idxBuildPartitionsLeftCount;

    /** {@inheritDoc} */
    @Override public boolean writeTo(ByteBuffer buf, MessageWriter writer) {
        writer.setBuffer(buf);

        if (!writer.isHeaderWritten()) {
            if (!writer.writeHeader(directType()))
                return false;

            writer.onHeaderWritten();
        }

        switch (writer.state()) {
            case 0:
                if (!writer.writeLong(cacheGets))
                    return false;

                writer.incrementState();

            case 1:
                if (!writer.writeLong(cachePuts))
                    return false;

                writer.incrementState();

            case 2:
                if (!writer.writeLong(entryProcessorPuts))
                    return false;

                writer.incrementState();

            case 3:
                if (!writer.writeLong(entryProcessorReadOnlyInvocations))
                    return false;

                writer.incrementState();

            case 4:
                if (!writer.writeFloat(entryProcessorAverageInvocationTime))
                    return false;

                writer.incrementState();

            case 5:
                if (!writer.writeLong(entryProcessorInvocations))
                    return false;

                writer.incrementState();

            case 6:
                if (!writer.writeLong(entryProcessorRemovals))
                    return false;

                writer.incrementState();

            case 7:
                if (!writer.writeLong(entryProcessorMisses))
                    return false;

                writer.incrementState();

            case 8:
                if (!writer.writeLong(entryProcessorHits))
                    return false;

                writer.incrementState();

            case 9:
                if (!writer.writeFloat(entryProcessorMissPercentage))
                    return false;

                writer.incrementState();

            case 10:
                if (!writer.writeFloat(entryProcessorHitPercentage))
                    return false;

                writer.incrementState();

            case 11:
                if (!writer.writeFloat(entryProcessorMaxInvocationTime))
                    return false;

                writer.incrementState();

            case 12:
                if (!writer.writeFloat(entryProcessorMinInvocationTime))
                    return false;

                writer.incrementState();

            case 13:
                if (!writer.writeLong(cacheHits))
                    return false;

                writer.incrementState();

            case 14:
                if (!writer.writeLong(cacheMisses))
                    return false;

                writer.incrementState();

            case 15:
                if (!writer.writeLong(cacheTxCommits))
                    return false;

                writer.incrementState();

            case 16:
                if (!writer.writeLong(cacheTxRollbacks))
                    return false;

                writer.incrementState();

            case 17:
                if (!writer.writeLong(cacheEvictions))
                    return false;

                writer.incrementState();

            case 18:
                if (!writer.writeLong(cacheRemovals))
                    return false;

                writer.incrementState();

            case 19:
                if (!writer.writeFloat(averagePutTime))
                    return false;

                writer.incrementState();

            case 20:
                if (!writer.writeFloat(averageGetTime))
                    return false;

                writer.incrementState();

            case 21:
                if (!writer.writeFloat(averageRemoveTime))
                    return false;

                writer.incrementState();

            case 22:
                if (!writer.writeFloat(averageTxCommitTime))
                    return false;

                writer.incrementState();

            case 23:
                if (!writer.writeFloat(averageTxRollbackTime))
                    return false;

                writer.incrementState();

            case 24:
                if (!writer.writeString(cacheName))
                    return false;

                writer.incrementState();

            case 25:
                if (!writer.writeLong(offHeapGets))
                    return false;

                writer.incrementState();

            case 26:
                if (!writer.writeLong(offHeapPuts))
                    return false;

                writer.incrementState();

            case 27:
                if (!writer.writeLong(offHeapRemoves))
                    return false;

                writer.incrementState();

            case 28:
                if (!writer.writeLong(offHeapEvicts))
                    return false;

                writer.incrementState();

            case 29:
                if (!writer.writeLong(offHeapHits))
                    return false;

                writer.incrementState();

            case 30:
                if (!writer.writeLong(offHeapMisses))
                    return false;

                writer.incrementState();

            case 31:
                if (!writer.writeLong(offHeapEntriesCnt))
                    return false;

                writer.incrementState();

            case 32:
                if (!writer.writeLong(heapEntriesCnt))
                    return false;

                writer.incrementState();

            case 33:
                if (!writer.writeLong(offHeapPrimaryEntriesCnt))
                    return false;

                writer.incrementState();

            case 34:
                if (!writer.writeLong(offHeapBackupEntriesCnt))
                    return false;

                writer.incrementState();

            case 35:
                if (!writer.writeLong(offHeapAllocatedSize))
                    return false;

                writer.incrementState();

            case 36:
                if (!writer.writeInt(size))
                    return false;

                writer.incrementState();

            case 37:
                if (!writer.writeLong(cacheSize))
                    return false;

                writer.incrementState();

            case 38:
                if (!writer.writeInt(keySize))
                    return false;

                writer.incrementState();

            case 39:
                if (!writer.writeBoolean(empty))
                    return false;

                writer.incrementState();

            case 40:
                if (!writer.writeInt(dhtEvictQueueCurrSize))
                    return false;

                writer.incrementState();

            case 41:
                if (!writer.writeInt(txThreadMapSize))
                    return false;

                writer.incrementState();

            case 42:
                if (!writer.writeInt(txXidMapSize))
                    return false;

                writer.incrementState();

            case 43:
                if (!writer.writeInt(txCommitQueueSize))
                    return false;

                writer.incrementState();

            case 44:
                if (!writer.writeInt(txPrepareQueueSize))
                    return false;

                writer.incrementState();

            case 45:
                if (!writer.writeInt(txStartVerCountsSize))
                    return false;

                writer.incrementState();

            case 46:
                if (!writer.writeInt(txCommittedVersionsSize))
                    return false;

                writer.incrementState();

            case 47:
                if (!writer.writeInt(txRolledbackVersionsSize))
                    return false;

                writer.incrementState();

            case 48:
                if (!writer.writeInt(txDhtThreadMapSize))
                    return false;

                writer.incrementState();

            case 49:
                if (!writer.writeInt(txDhtXidMapSize))
                    return false;

                writer.incrementState();

            case 50:
                if (!writer.writeInt(txDhtCommitQueueSize))
                    return false;

                writer.incrementState();

            case 51:
                if (!writer.writeInt(txDhtPrepareQueueSize))
                    return false;

                writer.incrementState();

            case 52:
                if (!writer.writeInt(txDhtStartVerCountsSize))
                    return false;

                writer.incrementState();

            case 53:
                if (!writer.writeInt(txDhtCommittedVersionsSize))
                    return false;

                writer.incrementState();

            case 54:
                if (!writer.writeInt(txDhtRolledbackVersionsSize))
                    return false;

                writer.incrementState();

            case 55:
                if (!writer.writeBoolean(writeBehindEnabled))
                    return false;

                writer.incrementState();

            case 56:
                if (!writer.writeInt(writeBehindFlushSize))
                    return false;

                writer.incrementState();

            case 57:
                if (!writer.writeInt(writeBehindFlushThreadCnt))
                    return false;

                writer.incrementState();

            case 58:
                if (!writer.writeLong(writeBehindFlushFreq))
                    return false;

                writer.incrementState();

            case 59:
                if (!writer.writeInt(writeBehindStoreBatchSize))
                    return false;

                writer.incrementState();

            case 60:
                if (!writer.writeInt(writeBehindTotalCriticalOverflowCnt))
                    return false;

                writer.incrementState();

            case 61:
                if (!writer.writeInt(writeBehindCriticalOverflowCnt))
                    return false;

                writer.incrementState();

            case 62:
                if (!writer.writeInt(writeBehindErrorRetryCnt))
                    return false;

                writer.incrementState();

            case 63:
                if (!writer.writeInt(writeBehindBufSize))
                    return false;

                writer.incrementState();

            case 64:
                if (!writer.writeInt(totalPartitionsCnt))
                    return false;

                writer.incrementState();

            case 65:
                if (!writer.writeInt(rebalancingPartitionsCnt))
                    return false;

                writer.incrementState();

            case 66:
                if (!writer.writeLong(rebalancedKeys))
                    return false;

                writer.incrementState();

            case 67:
                if (!writer.writeLong(estimatedRebalancingKeys))
                    return false;

                writer.incrementState();

            case 68:
                if (!writer.writeLong(keysToRebalanceLeft))
                    return false;

                writer.incrementState();

            case 69:
                if (!writer.writeLong(rebalancingKeysRate))
                    return false;

                writer.incrementState();

            case 70:
                if (!writer.writeLong(rebalancingBytesRate))
                    return false;

                writer.incrementState();

            case 71:
                if (!writer.writeLong(rebalanceStartTime))
                    return false;

                writer.incrementState();

            case 72:
                if (!writer.writeLong(rebalanceFinishTime))
                    return false;

                writer.incrementState();

            case 73:
                if (!writer.writeLong(rebalanceClearingPartitionsLeft))
                    return false;

                writer.incrementState();

            case 74:
                if (!writer.writeString(keyType))
                    return false;

                writer.incrementState();

            case 75:
                if (!writer.writeString(valType))
                    return false;

                writer.incrementState();

            case 76:
                if (!writer.writeBoolean(storeByVal))
                    return false;

                writer.incrementState();

            case 77:
                if (!writer.writeBoolean(statisticsEnabled))
                    return false;

                writer.incrementState();

            case 78:
                if (!writer.writeBoolean(managementEnabled))
                    return false;

                writer.incrementState();

            case 79:
                if (!writer.writeBoolean(readThrough))
                    return false;

                writer.incrementState();

            case 80:
                if (!writer.writeBoolean(writeThrough))
                    return false;

                writer.incrementState();

            case 81:
                if (!writer.writeBoolean(validForReading))
                    return false;

                writer.incrementState();

            case 82:
                if (!writer.writeBoolean(validForWriting))
                    return false;

                writer.incrementState();

            case 83:
                if (!writer.writeString(txKeyCollisions))
                    return false;

                writer.incrementState();

            case 84:
                if (!writer.writeBoolean(idxRebuildInProgress))
                    return false;

                writer.incrementState();

            case 85:
                if (!writer.writeLong(idxRebuildKeyProcessed))
                    return false;

                writer.incrementState();

            case 86:
                if (!writer.writeInt(idxBuildPartitionsLeftCount))
                    return false;

                writer.incrementState();
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override public boolean readFrom(ByteBuffer buf, MessageReader reader) {
        reader.setBuffer(buf);

        switch (reader.state()) {
            case 0:
                cacheGets = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 1:
                cachePuts = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 2:
                entryProcessorPuts = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 3:
                entryProcessorReadOnlyInvocations = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 4:
                entryProcessorAverageInvocationTime = reader.readFloat();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 5:
                entryProcessorInvocations = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 6:
                entryProcessorRemovals = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 7:
                entryProcessorMisses = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 8:
                entryProcessorHits = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 9:
                entryProcessorMissPercentage = reader.readFloat();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 10:
                entryProcessorHitPercentage = reader.readFloat();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 11:
                entryProcessorMaxInvocationTime = reader.readFloat();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 12:
                entryProcessorMinInvocationTime = reader.readFloat();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 13:
                cacheHits = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 14:
                cacheMisses = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 15:
                cacheTxCommits = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 16:
                cacheTxRollbacks = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 17:
                cacheEvictions = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 18:
                cacheRemovals = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 19:
                averagePutTime = reader.readFloat();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 20:
                averageGetTime = reader.readFloat();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 21:
                averageRemoveTime = reader.readFloat();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 22:
                averageTxCommitTime = reader.readFloat();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 23:
                averageTxRollbackTime = reader.readFloat();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 24:
                cacheName = reader.readString();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 25:
                offHeapGets = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 26:
                offHeapPuts = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 27:
                offHeapRemoves = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 28:
                offHeapEvicts = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 29:
                offHeapHits = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 30:
                offHeapMisses = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 31:
                offHeapEntriesCnt = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 32:
                heapEntriesCnt = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 33:
                offHeapPrimaryEntriesCnt = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 34:
                offHeapBackupEntriesCnt = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 35:
                offHeapAllocatedSize = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 36:
                size = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 37:
                cacheSize = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 38:
                keySize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 39:
                empty = reader.readBoolean();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 40:
                dhtEvictQueueCurrSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 41:
                txThreadMapSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 42:
                txXidMapSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 43:
                txCommitQueueSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 44:
                txPrepareQueueSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 45:
                txStartVerCountsSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 46:
                txCommittedVersionsSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 47:
                txRolledbackVersionsSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 48:
                txDhtThreadMapSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 49:
                txDhtXidMapSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 50:
                txDhtCommitQueueSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 51:
                txDhtPrepareQueueSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 52:
                txDhtStartVerCountsSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 53:
                txDhtCommittedVersionsSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 54:
                txDhtRolledbackVersionsSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 55:
                writeBehindEnabled = reader.readBoolean();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 56:
                writeBehindFlushSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 57:
                writeBehindFlushThreadCnt = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 58:
                writeBehindFlushFreq = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 59:
                writeBehindStoreBatchSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 60:
                writeBehindTotalCriticalOverflowCnt = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 61:
                writeBehindCriticalOverflowCnt = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 62:
                writeBehindErrorRetryCnt = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 63:
                writeBehindBufSize = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 64:
                totalPartitionsCnt = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 65:
                rebalancingPartitionsCnt = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 66:
                rebalancedKeys = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 67:
                estimatedRebalancingKeys = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 68:
                keysToRebalanceLeft = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 69:
                rebalancingKeysRate = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 70:
                rebalancingBytesRate = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 71:
                rebalanceStartTime = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 72:
                rebalanceFinishTime = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 73:
                rebalanceClearingPartitionsLeft = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 74:
                keyType = reader.readString();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 75:
                valType = reader.readString();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 76:
                storeByVal = reader.readBoolean();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 77:
                statisticsEnabled = reader.readBoolean();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 78:
                managementEnabled = reader.readBoolean();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 79:
                readThrough = reader.readBoolean();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 80:
                writeThrough = reader.readBoolean();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 81:
                validForReading = reader.readBoolean();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 82:
                validForWriting = reader.readBoolean();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 83:
                txKeyCollisions = reader.readString();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 84:
                idxRebuildInProgress = reader.readBoolean();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 85:
                idxRebuildKeyProcessed = reader.readLong();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();

            case 86:
                idxBuildPartitionsLeftCount = reader.readInt();

                if (!reader.isLastRead())
                    return false;

                reader.incrementState();
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override public short directType() {
        return 136;
    }
}
