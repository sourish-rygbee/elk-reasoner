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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationShared;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * Abstract super class of all rules that require that BackwardLinks are fully
 * derived.
 * 
 * @author Frantisek Simancik
 * 
 */
public abstract class RuleWithBackwardLinks<C extends ContextElClassSaturation> {

	/**
	 * Triggers composition rules for the backward links in the given context,
	 * which are otherwise ommitted for efficiency.
	 * 
	 */
	protected void initializeCompositionOfBackwardLinks(C context,
			RuleApplicationShared engine) {
		if (context.composeBackwardLinks)
			return;

		context.composeBackwardLinks = true;

		if (context.backwardLinksByObjectProperty != null) {

			Multimap<IndexedPropertyChain, ? extends ContextElClassSaturation> backLinks = context.backwardLinksByObjectProperty;

			for (IndexedPropertyChain linkRelation : backLinks.keySet())
				if (linkRelation.getSaturated()
						.getCompositionsByLeftSubProperty() != null)
					for (ContextElClassSaturation target : backLinks
							.get(linkRelation))
						engine.enqueue(target,
								new ForwardLink<ContextElClassSaturation>(
										linkRelation, context));
		}
	}

}
