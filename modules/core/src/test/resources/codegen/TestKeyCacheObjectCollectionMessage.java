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

package org.apache.ignite.internal;

import java.util.Collection;
import org.apache.ignite.plugin.extensions.communication.Message;

/**
 * Test Message demonstrating the safe replacement for an {@code @Order Map<KeyCacheObject, ?>} field.
 * <p>
 * Instead of a {@code Map}, the wire representation is a {@code Collection<KeyCacheObjectEntryMsg>}. The
 * APT-generated serializer does not assemble any {@code HashMap} on the NIO thread, so the fact that
 * {@code KeyCacheObject.hashCode} is unstable until {@link org.apache.ignite.internal.processors.cache.CacheObject#finishUnmarshal}
 * has run is harmless. The generated {@code finishUnmarshalCacheObjects} walks every entry and calls
 * {@code KeyCacheObject#finishUnmarshal} on the user thread — so by the time the application code reassembles a
 * {@code HashMap<KeyCacheObject, ?>} from {@link #entries}, every key's hashCode is already stable.
 */
public class TestKeyCacheObjectCollectionMessage implements Message {
    @Order(0)
    Collection<KeyCacheObjectEntryMsg> entries;

    public short directType() {
        return 1;
    }
}
