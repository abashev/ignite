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

package org.apache.ignite.plugin.extensions.communication;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.processors.cache.CacheObjectValueContext;

/** Message serialization logic. */
public interface MessageSerializer<M extends Message> {
    /**
     * Writes this message to provided byte buffer.
     *
     * @param msg Message instance.
     * @param writer Writer.
     * @return Whether message was fully written.
     */
    public boolean writeTo(M msg, MessageWriter writer);

    /**
     * Reads this message from provided byte buffer.
     *
     * @param msg Message instance.
     * @param reader Reader.
     * @return Whether message was fully read.
     */
    public boolean readFrom(M msg, MessageReader reader);

    /**
     * Phase 1 of two-phase marshalling (IGNITE-28520): invoked on the <b>user thread</b> before the message is
     * handed off to the NIO worker. The generated implementation walks {@code @Order}-annotated
     * {@code CacheObject}/{@code KeyCacheObject} fields (direct, {@code Collection<>}, and array — single level,
     * no maps, no recursion into nested messages) and calls {@code CacheObject.prepareMarshal} on each, so the NIO
     * thread never does it. Default is a no-op.
     */
    public default void prepareMarshalCacheObjects(M msg, CacheObjectValueContext ctx) throws IgniteCheckedException {
        // No-op by default.
    }

    /**
     * Receive-side mirror of {@link #prepareMarshalCacheObjects}: called on a user (listener-dispatch) thread to
     * run {@code CacheObject.finishUnmarshal} for the same set of fields. Default is a no-op.
     */
    public default void finishUnmarshalCacheObjects(M msg, CacheObjectValueContext ctx, ClassLoader ldr)
        throws IgniteCheckedException {
        // No-op by default.
    }
}
