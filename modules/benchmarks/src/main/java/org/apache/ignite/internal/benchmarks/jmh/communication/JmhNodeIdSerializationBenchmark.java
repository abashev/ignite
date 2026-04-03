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
import java.util.UUID;
import org.apache.ignite.internal.direct.DirectMessageReader;
import org.apache.ignite.internal.direct.DirectMessageWriter;
import org.apache.ignite.internal.managers.communication.GridIoMessageFactory;
import org.apache.ignite.internal.managers.communication.IgniteMessageFactoryImpl;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.plugin.extensions.communication.MessageFactory;
import org.apache.ignite.plugin.extensions.communication.MessageFactoryProvider;
import org.apache.ignite.spi.communication.tcp.messages.NodeIdMessage;
import org.apache.ignite.spi.communication.tcp.messages.NodeIdMessageSerializer;
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
 * serialization approaches for {@code NodeIdMessage} — the minimal possible message with a single UUID field.
 *
 * <p>This benchmark isolates the per-message overhead of both approaches by using the smallest
 * possible message, complementing the {@link JmhCacheMetricsSerializationBenchmark} which measures
 * throughput on a large (87-field) message.
 *
 * <p>Run via IDE with {@link #main} or as an uber-jar:
 * <pre>
 *   java -jar target/benchmarks.jar JmhNodeIdSerializationBenchmark
 * </pre>
 */
@State(Scope.Thread)
@OutputTimeUnit(NANOSECONDS)
@BenchmarkMode(AverageTime)
@Warmup(iterations = 3, time = 10, timeUnit = SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = SECONDS)
public class JmhNodeIdSerializationBenchmark {
    /** Buffer capacity — generous for a tiny message. */
    private static final int BUF_CAPACITY = 1024;

    // ----- New approach (MessageSerializer) -----

    /** New-style message. */
    private NodeIdMessage newMsg;

    /** New-style serializer. */
    private NodeIdMessageSerializer newSerializer;

    /** Writer for new-style benchmarks. */
    private DirectMessageWriter newWriter;

    /** Reader for new-style benchmarks. */
    private DirectMessageReader newReader;

    /** Write buffer for new-style benchmarks. */
    private ByteBuffer newWriteBuf;

    /** Pre-filled read buffer for new-style benchmarks. */
    private ByteBuffer newReadBuf;

    /** Reusable deserialization target for new-style benchmarks. */
    private NodeIdMessage newReadTarget;

    // ----- Legacy approach (inline writeTo/readFrom) -----

    /** Legacy message. */
    private LegacyNodeIdMessage legacyMsg;

    /** Writer for legacy benchmarks. */
    private DirectMessageWriter legacyWriter;

    /** Reader for legacy benchmarks. */
    private DirectMessageReader legacyReader;

    /** Write buffer for legacy benchmarks. */
    private ByteBuffer legacyWriteBuf;

    /** Pre-filled read buffer for legacy benchmarks. */
    private ByteBuffer legacyReadBuf;

    /** Reusable deserialization target for legacy benchmarks. */
    private LegacyNodeIdMessage legacyReadTarget;

    /** */
    @Setup
    public void setup() throws Exception {
        MessageFactory factory = new IgniteMessageFactoryImpl(
            new MessageFactoryProvider[]{new GridIoMessageFactory(jdk(), U.gridClassLoader())}
        );

        var uuid = UUID.randomUUID();

        // --- New approach setup ---
        newMsg = new NodeIdMessage(uuid);
        newSerializer = new NodeIdMessageSerializer();
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

        newReadTarget = new NodeIdMessage();

        // --- Legacy approach setup ---
        legacyMsg = new LegacyNodeIdMessage(uuid);
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

        legacyReadTarget = new LegacyNodeIdMessage();
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
        org.openjdk.jmh.Main.main(new String[]{JmhNodeIdSerializationBenchmark.class.getSimpleName()});
    }
}
