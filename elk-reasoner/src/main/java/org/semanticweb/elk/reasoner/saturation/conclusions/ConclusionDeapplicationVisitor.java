package org.semanticweb.elk.reasoner.saturation.conclusions;

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

import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ConclusionDeapplicationVisitor implements
		ConclusionVisitor<Boolean> {

	private final BasicSaturationStateWriter writer_;
	private final RuleApplicationVisitor ruleAppVisitor_;
	private final DecompositionRuleApplicationVisitor decompRuleAppVisitor_;

	public ConclusionDeapplicationVisitor(BasicSaturationStateWriter writer, RuleApplicationVisitor ruleAppVisitor, DecompositionRuleApplicationVisitor decompVisitor) {
		this.writer_ = writer;
		this.ruleAppVisitor_ = ruleAppVisitor;
		this.decompRuleAppVisitor_ = decompVisitor;
	}

	@Override
	public Boolean visit(NegativeSubsumer negSCE, Context context) {
		negSCE.applyDecompositionRules(context, decompRuleAppVisitor_);
		negSCE.apply(writer_, context, ruleAppVisitor_);
		
		return true;
	}

	@Override
	public Boolean visit(PositiveSubsumer posSCE, Context context) {
		posSCE.apply(writer_, context, ruleAppVisitor_, decompRuleAppVisitor_);
		return true;
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		link.apply(writer_, context, ruleAppVisitor_);
		return true;
	}

	@Override
	public Boolean visit(ForwardLink link, Context context) {
		link.apply(writer_, context);
		return true;
	}

	@Override
	public Boolean visit(Contradiction bot, Context context) {
		bot.deapply(writer_, context);
		return true;
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		propagation.apply(writer_, context);
		return true;
	}

	@Override
	public Boolean visit(DisjointnessAxiom disjointnessAxiom, Context context) {
		disjointnessAxiom.apply(writer_, context);
		return true;
	}
}
