package org.semanticweb.elk.reasoner.stages;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.saturation.properties.PropertyHierarchyCompositionComputation;

public class PropertyHierarchyCompositionComputationStage extends
		AbstractReasonerStage {

	/**
	 * the computation used for this stage
	 */
	private PropertyHierarchyCompositionComputation computation_ = null;

	public PropertyHierarchyCompositionComputationStage(
			AbstractReasonerState reasoner, AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Object Property Hierarchy and Composition Computation";
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute())
			return false;
		computation_ = new PropertyHierarchyCompositionComputation(
				reasoner.ontologyIndex, reasoner.getProcessExecutor(),
				workerNo, reasoner.getProgressMonitor());
		return true;
	}

	@Override
	public void executeStage() throws ElkException {
		for (;;) {
			computation_.process();
			if (!spuriousInterrupt())
				break;
		}
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute())
			return false;
		computation_ = null;
		return true;
	}

	@Override
	public void printInfo() {
		// TODO Auto-generated method stub

	}

}
