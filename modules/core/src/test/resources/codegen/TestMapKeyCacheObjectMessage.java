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

import java.util.Map;
import org.apache.ignite.internal.processors.cache.KeyCacheObject;
import org.apache.ignite.internal.processors.cache.version.GridCacheVersion;
import org.apache.ignite.plugin.extensions.communication.Message;

/**
 * Test Message demonstrating that {@code @Order Map<KeyCacheObject, GridCacheVersion>} is safe to use as-is.
 * <p>
 * The APT-generated {@code readFrom} leaves {@code entries} as a
 * {@link org.apache.ignite.internal.direct.stream.PendingMap} on the NIO thread (no {@code hashCode}
 * invocations on keys). The generated {@code finishUnmarshalCacheObjects} walks the staged keys and values
 * via {@code PendingMap.keysOf} / {@code PendingMap.valuesOf} and calls {@code KeyCacheObject#finishUnmarshal}
 * / the nested {@code GridCacheVersionSerializer#finishUnmarshalCacheObjects} on each — so by the time user
 * code accesses the map and triggers real {@code HashMap} assembly, every key's {@code hashCode} is stable.
 */
public class TestMapKeyCacheObjectMessage implements Message {
    @Order(0)
    Map<KeyCacheObject, GridCacheVersion> entries;

    public short directType() {
        return 0;
    }
}
