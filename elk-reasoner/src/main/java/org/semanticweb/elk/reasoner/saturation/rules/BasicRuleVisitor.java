/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

/**
 * A {@link RuleVisitor} which simply applies the rules that it visits to the
 * given arguments.
 * 
 * @see {@link Rule#apply(Object, Context, ConclusionProducer)}
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 */
public class BasicRuleVisitor extends AbstractRuleVisitor {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(BasicRuleVisitor.class);

	@Override
	<P> void defaultVisit(Rule<P> rule, P premise, Context context,
			ConclusionProducer producer) {
		LOGGER_.trace("{}: process {} by {}", context, premise, rule.getName());
		rule.apply(premise, context, producer);
	}
}