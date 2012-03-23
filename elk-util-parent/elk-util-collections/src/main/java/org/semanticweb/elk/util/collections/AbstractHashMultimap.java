/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.semanticweb.elk.util.collections;

import java.util.Collection;

/**
 * 
 * Implementation of Multimap backed by an ArrayHashMap
 * 
 * @author Frantisek Simancik
 * 
 * @param <Key>
 * @param <Value>
 */

public abstract class AbstractHashMultimap<Key, Value> extends
		ArrayHashMap<Key, Collection<Value>> implements Multimap<Key, Value> {

	protected abstract Collection<Value> newRecord();

	public AbstractHashMultimap() {
		super();
	}

	public AbstractHashMultimap(int i) {
		super(i);
	}

	public boolean contains(Object key, Object value) {
		Collection<Value> record = get(key);
		if (record == null)
			return false;
		else
			return record.contains(value);
	}

	public boolean add(Key key, Value value) {
		Collection<Value> record = get(key);
		if (record == null) {
			record = newRecord();
			put(key, record);
		}
		return record.add(value);
	}

	public boolean remove(Object key, Object value) {
		Collection<Value> record = get(key);
		if (record == null)
			return false;
		return record.remove(value);
	}

}
