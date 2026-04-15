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

/**
 * Interface for message serialization logic.
 */
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
     * Prepares {@link org.apache.ignite.internal.processors.cache.CacheObject} fields of the given message for
     * marshalling. This is the first phase of the two-phase marshalling introduced by IGNITE-28520: it is expected
     * to be invoked on a <b>user thread</b> before the message is handed off to the NIO worker, so that
     * {@link org.apache.ignite.internal.processors.cache.CacheObject#prepareMarshal(CacheObjectValueContext)} never
     * runs on the NIO communication thread (or, worse, the Discovery thread where it could deadlock).
     *
     * <p>The default implementation is a no-op. The generated serializer overrides it for messages that contain
     * at least one {@code @Order}-annotated field whose declared type is {@code CacheObject} / {@code KeyCacheObject}
     * (including {@code Collection<CacheObject>} and {@code CacheObject[]}). Only the top level is traversed — the
     * traversal does <b>not</b> recurse into nested messages or map values.
     *
     * @param msg Message instance.
     * @param ctx Cache object value context.
     * @throws IgniteCheckedException If marshalling of any contained cache object fails.
     */
    public default void prepareMarshalCacheObjects(M msg, CacheObjectValueContext ctx) throws IgniteCheckedException {
        // No-op by default.
    }

    /**
     * Finishes unmarshalling of {@link org.apache.ignite.internal.processors.cache.CacheObject} fields of the given
     * message. Intended to be invoked on a <b>user thread</b> (for example, on a listener dispatch thread) after
     * {@link #readFrom(Message, MessageReader)} returns {@code true} on the NIO worker, so that
     * {@link org.apache.ignite.internal.processors.cache.CacheObject#finishUnmarshal(CacheObjectValueContext, ClassLoader)}
     * never runs on the NIO communication thread.
     *
     * <p>The default implementation is a no-op. Mirrors the traversal rules of
     * {@link #prepareMarshalCacheObjects(Message, CacheObjectValueContext)}.
     *
     * @param msg Message instance.
     * @param ctx Cache object value context.
     * @param ldr Class loader to use for value deserialization.
     * @throws IgniteCheckedException If unmarshalling of any contained cache object fails.
     */
    public default void finishUnmarshalCacheObjects(M msg, CacheObjectValueContext ctx, ClassLoader ldr)
        throws IgniteCheckedException {
        // No-op by default.
    }
}
