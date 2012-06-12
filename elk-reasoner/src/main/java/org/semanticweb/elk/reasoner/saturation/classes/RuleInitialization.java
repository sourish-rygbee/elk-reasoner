/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.classes;

import org.semanticweb.elk.reasoner.saturation.rulesystem.InferenceRule;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationShared;

/**
 * @author Frantisek Simancik
 * 
 */
public class RuleInitialization<C extends ContextElClassSaturation> implements
		InferenceRule<C> {

	public void init(C context, RuleApplicationShared engine) {
		engine.enqueue(context,
				new PositiveSuperClassExpression<ContextElClassSaturation>(
						context.getRoot()));

		if (engine.owlThing.occursNegatively())
			engine.enqueue(context,
					new PositiveSuperClassExpression<ContextElClassSaturation>(
							engine.owlThing));

	}

}
