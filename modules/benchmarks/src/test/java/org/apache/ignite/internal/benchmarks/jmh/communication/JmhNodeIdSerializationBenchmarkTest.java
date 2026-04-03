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
import org.apache.ignite.spi.communication.tcp.messages.NodeIdMessage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Correctness test for {@link JmhNodeIdSerializationBenchmark}.
 *
 * <p>Verifies that both the new (MessageSerializer) and legacy (inline writeTo/readFrom)
 * approaches produce identical round-trip results for the minimal single-field message.
 */
public class JmhNodeIdSerializationBenchmarkTest {
    /** */
    private JmhNodeIdSerializationBenchmark benchmark;

    /** */
    @Before
    public void setUp() throws Exception {
        benchmark = new JmhNodeIdSerializationBenchmark();
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

    /** Round-trip for new approach preserves the UUID field. */
    @Test
    public void testNewRoundTripPreservesField() throws Exception {
        assertTrue(benchmark.newWriteTo());
        assertTrue(benchmark.newReadFrom());

        var src = (NodeIdMessage)FieldUtils.readField(benchmark, "newMsg", true);
        var dst = (NodeIdMessage)FieldUtils.readField(benchmark, "newReadTarget", true);

        assertNotNull("readTarget must not be null after readFrom", dst);
        assertEquals("nodeId", src.nodeId(), dst.nodeId());
    }

    /** Round-trip for legacy approach preserves the UUID field. */
    @Test
    public void testLegacyRoundTripPreservesField() throws Exception {
        assertTrue(benchmark.legacyWriteTo());
        assertTrue(benchmark.legacyReadFrom());

        var src = (LegacyNodeIdMessage)FieldUtils.readField(benchmark, "legacyMsg", true);
        var dst = (LegacyNodeIdMessage)FieldUtils.readField(benchmark, "legacyReadTarget", true);

        assertNotNull("readTarget must not be null after readFrom", dst);
        assertEquals("nodeId", src.nodeId, dst.nodeId);
    }
}
