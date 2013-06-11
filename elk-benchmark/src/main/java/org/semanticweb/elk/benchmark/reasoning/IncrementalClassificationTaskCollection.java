/**
 * 
 */
package org.semanticweb.elk.benchmark.reasoning;
/*
 * #%L
 * ELK Benchmarking Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.Arrays;
import java.util.Collection;

import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskCollection;
import org.semanticweb.elk.benchmark.TaskException;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalClassificationTaskCollection implements TaskCollection {

	private final IncrementalClassificationTask icTask_;
	private final RandomWalkIncrementalClassificationTask randomWalkTask_;
	
	
	public IncrementalClassificationTaskCollection(String[] args) {
		icTask_ = new IncrementalClassificationTask(args);
		randomWalkTask_ = new RandomWalkIncrementalClassificationTask(args);
	}
	
	@Override
	public Collection<Task> getTasks() throws TaskException {
		return Arrays.asList(icTask_, randomWalkTask_);
	}
	
	@Override
	public void dispose() {
	}

	@Override
	public Metrics getMetrics() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
