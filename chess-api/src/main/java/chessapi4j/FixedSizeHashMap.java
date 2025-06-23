/*
 * Copyright 2025 Miguel Angel Luna Lobos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/lunalobos/chessapi4j/blob/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package chessapi4j;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * A LinkedHashMap with a fixed size.
 * 
 * @param <K> the key type
 * @param <V> the value type
 * @author lunalobos
 * @since 1.2.8
 */
class FixedSizeHashMap<K, V> extends LinkedHashMap<K, V> {
	/**
	 *
	 */
	private static final long serialVersionUID = 8627211931738255730L;
	private int capacity;

	public FixedSizeHashMap(int capacity, float loadFactor, boolean accessOrder) {
		super(capacity, loadFactor, accessOrder);
		this.capacity = capacity;
	}

	public FixedSizeHashMap(int capacity, float loadFactor) {
		super(capacity, loadFactor);
		this.capacity = capacity;
	}

	public FixedSizeHashMap(int capacity) {
		super(capacity);
		this.capacity = capacity;
	}

	@Override
	protected boolean removeEldestEntry(Entry<K, V> eldest) {
		return size() > capacity;
	}

}
