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

package org.apache.ignite.internal.direct.stream;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.ignite.internal.util.typedef.internal.U;

/**
 * {@link Map} wrapper returned by {@link DirectByteBufferStream#readMap} while a message is being demarshaled
 * on the NIO thread.
 *
 * <p>Instead of assembling a {@link java.util.HashMap} on the NIO thread — which is unsafe when the keys are
 * {@link org.apache.ignite.internal.processors.cache.KeyCacheObject}s whose {@code hashCode()} is only stable
 * once {@link org.apache.ignite.internal.processors.cache.CacheObject#finishUnmarshal} has run — this class
 * accumulates {@code (key, value)} pairs into parallel arrays and defers construction of the real hash map
 * until the first access. That first access happens on the user thread after {@code finishUnmarshal} has been
 * applied to every {@code CacheObject} key/value, so bucket placement is correct.
 *
 * <p>Generated serializer code for an {@code @Order Map<K, V>} field walks the pending contents via
 * {@link #stagedKeys()} / {@link #stagedValues()} so the traversal does not trigger materialization
 * prematurely.
 */
public final class PendingMap<K, V> extends AbstractMap<K, V> {
    /** Minimum capacity of the staging arrays. */
    private static final int MIN_CAP = 4;

    /** Whether to produce a {@link java.util.LinkedHashMap} on materialization. */
    private final boolean linked;

    /** Staged keys; nulled out after {@link #materialize()}. */
    private Object[] keys;

    /** Staged values; nulled out after {@link #materialize()}. */
    private Object[] vals;

    /** Number of staged entries. */
    private int size;

    /** Materialized view; {@code null} until {@link #materialize()} runs. */
    private Map<K, V> real;

    /**
     * @param expSize Expected number of entries (hint for initial array capacity).
     * @param linked  Whether the materialized map should preserve insertion order.
     */
    public PendingMap(int expSize, boolean linked) {
        this.linked = linked;

        int cap = Math.max(expSize, MIN_CAP);

        keys = new Object[cap];
        vals = new Object[cap];
    }

    /**
     * Appends a raw {@code (key, value)} pair to the staging arrays. Does not invoke {@code key.hashCode()} or
     * {@code key.equals()}, so the method is safe to call on the NIO thread before any CacheObject key has had
     * {@code finishUnmarshal} applied.
     *
     * @param k Key.
     * @param v Value.
     */
    public void addRaw(K k, V v) {
        if (size == keys.length) {
            int newCap = keys.length << 1;

            keys = Arrays.copyOf(keys, newCap);
            vals = Arrays.copyOf(vals, newCap);
        }

        keys[size] = k;
        vals[size] = v;

        size++;
    }

    /**
     * Builds the real {@link java.util.HashMap} (or {@link java.util.LinkedHashMap}) from the staged entries.
     * Idempotent. After the first call the staging arrays are discarded and all subsequent {@link Map} accesses
     * hit the materialized instance.
     *
     * @return The materialized map.
     */
    @SuppressWarnings("unchecked")
    public Map<K, V> materialize() {
        if (real == null) {
            Map<K, V> m = linked ? U.newLinkedHashMap(size) : U.newHashMap(size);

            for (int i = 0; i < size; i++)
                m.put((K)keys[i], (V)vals[i]);

            real = m;
            keys = null;
            vals = null;
        }

        return real;
    }

    /**
     * Iterates staged keys without triggering {@link #materialize()}. Intended for consumption by generated
     * {@code prepareMarshalCacheObjects} / {@code finishUnmarshalCacheObjects} code that needs to walk
     * {@link org.apache.ignite.internal.processors.cache.CacheObject} key entries before the hash map is built.
     * If the map has already been materialized, falls back to iterating the materialized key set.
     *
     * @return Iterable of staged keys.
     */
    public Iterable<K> stagedKeys() {
        if (real != null)
            return real.keySet();

        return stagedIterable(keys, size);
    }

    /**
     * Iterates staged values without triggering {@link #materialize()}. If the map has already been materialized,
     * falls back to iterating the materialized values collection.
     *
     * @return Iterable of staged values.
     */
    public Iterable<V> stagedValues() {
        if (real != null)
            return real.values();

        return stagedIterable(vals, size);
    }

    /** {@inheritDoc} */
    @Override public Set<Entry<K, V>> entrySet() {
        return materialize().entrySet();
    }

    /** {@inheritDoc} */
    @Override public int size() {
        return real != null ? real.size() : size;
    }

    /** {@inheritDoc} */
    @Override public boolean isEmpty() {
        return size() == 0;
    }

    /** {@inheritDoc} */
    @Override public V get(Object key) {
        return materialize().get(key);
    }

    /** {@inheritDoc} */
    @Override public V put(K key, V val) {
        return materialize().put(key, val);
    }

    /** {@inheritDoc} */
    @Override public V remove(Object key) {
        return materialize().remove(key);
    }

    /** {@inheritDoc} */
    @Override public void putAll(Map<? extends K, ? extends V> m) {
        materialize().putAll(m);
    }

    /** {@inheritDoc} */
    @Override public void clear() {
        materialize().clear();
    }

    /** {@inheritDoc} */
    @Override public boolean containsKey(Object key) {
        return materialize().containsKey(key);
    }

    /** {@inheritDoc} */
    @Override public boolean containsValue(Object val) {
        return materialize().containsValue(val);
    }

    /** {@inheritDoc} */
    @Override public Set<K> keySet() {
        return materialize().keySet();
    }

    /** {@inheritDoc} */
    @Override public Collection<V> values() {
        return materialize().values();
    }

    /**
     * Returns an {@link Iterable} over the keys of {@code m} without triggering materialization if {@code m} is a
     * {@link PendingMap}. For any other {@link Map} implementation delegates to {@link Map#keySet()}.
     *
     * <p>Intended to be called from APT-generated {@code prepareMarshalCacheObjects} / {@code
     * finishUnmarshalCacheObjects}. Generated code does not know at compile time whether the field holds a
     * pending instance (receive-side) or a real map (send-side); this helper dispatches uniformly.
     *
     * @param m Map.
     * @param <K> Key type.
     * @return Iterable over keys.
     */
    @SuppressWarnings("unchecked")
    public static <K> Iterable<K> keysOf(Map<K, ?> m) {
        if (m instanceof PendingMap)
            return ((PendingMap<K, ?>)m).stagedKeys();

        return m.keySet();
    }

    /**
     * Returns an {@link Iterable} over the values of {@code m} without triggering materialization if {@code m} is a
     * {@link PendingMap}. For any other {@link Map} implementation delegates to {@link Map#values()}.
     *
     * @param m Map.
     * @param <V> Value type.
     * @return Iterable over values.
     */
    @SuppressWarnings("unchecked")
    public static <V> Iterable<V> valuesOf(Map<?, V> m) {
        if (m instanceof PendingMap)
            return ((PendingMap<?, V>)m).stagedValues();

        return m.values();
    }

    /** */
    @SuppressWarnings("unchecked")
    private static <E> Iterable<E> stagedIterable(Object[] arr, int sz) {
        if (sz == 0)
            return Collections.emptyList();

        return () -> new Iterator<E>() {
            private int i;

            @Override public boolean hasNext() {
                return i < sz;
            }

            @Override public E next() {
                if (i >= sz)
                    throw new NoSuchElementException();

                return (E)arr[i++];
            }
        };
    }
}
