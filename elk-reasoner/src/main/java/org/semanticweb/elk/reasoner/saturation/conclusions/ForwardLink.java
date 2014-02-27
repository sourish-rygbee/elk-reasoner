/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.BackwardLinkFromForwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.NonReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.ReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedExistentialBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedExistentialForwardLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Conclusion} representing derived existential restrictions from this
 * source {@link IndexedClassExpression} to a target
 * {@link IndexedClassExpression}. Intuitively, if a subclass axiom
 * {@code SubClassOf(:A ObjectSomeValuesFrom(:r :B))} is derived by inference
 * rules, then a {@link ForwardLink} with the relation {@code :r} and the target
 * {@code :B} can be produced for {@code :A}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class ForwardLink extends AbstractConclusion {

	static final Logger LOGGER_ = LoggerFactory.getLogger(ForwardLink.class);

	public static final String NAME = "Forward Link";

	/**
	 * the {@link IndexedPropertyChain} in the existential restriction
	 * corresponding to this {@link ForwardLink}
	 */
	final IndexedPropertyChain relation_;

	/**
	 * the {@link IndexedClassExpression}, which root is the filler of the
	 * existential restriction corresponding to this {@link ForwardLink}
	 */
	final IndexedClassExpression target_;

	public ForwardLink(IndexedPropertyChain relation,
			IndexedClassExpression target) {
		this.relation_ = relation;
		this.target_ = target;
	}

	public IndexedPropertyChain getRelation() {
		return relation_;
	}

	public IndexedClassExpression getTarget() {
		return target_;
	}

	@Override
	public void applyNonRedundantLocalRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		// generate backward links
		ruleAppVisitor.visit(BackwardLinkFromForwardLinkRule.getInstance(),
				this, premises, producer);
		// compose only with reflexive backward links
		ruleAppVisitor.visit(
				ReflexiveBackwardLinkCompositionRule.getRuleFor(this), this,
				premises, producer);
	}

	@Override
	public void applyNonRedundantRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		applyNonRedundantLocalRules(ruleAppVisitor, premises, producer);
		// in addition, compose with non-reflexive backward links
		ruleAppVisitor.visit(
				NonReflexiveBackwardLinkCompositionRule.getRuleFor(this), this,
				premises, producer);
	}

	@Override
	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public String toString() {
		return relation_ + "->" + target_;
	}

	// TODO: find a better place for the following methods

	public static void produceDecomposedExistentialLink(
			ConclusionProducer producer, IndexedClassExpression source,
			IndexedObjectSomeValuesFrom existential) {
		IndexedObjectProperty relation = existential.getRelation();
		if (relation.getSaturated().getCompositionsByLeftSubProperty()
				.isEmpty()) {
			producer.produce(existential.getFiller(),
					new DecomposedExistentialBackwardLink(source, existential));
		} else {
			producer.produce(source, new DecomposedExistentialForwardLink(
					existential));
		}
	}

	public static void produceComposedLink(ConclusionProducer producer,
			IndexedClassExpression source,
			IndexedObjectProperty backwardRelation,
			IndexedClassExpression inferenceRoot,
			IndexedPropertyChain forwardRelation,
			IndexedClassExpression target,
			IndexedBinaryPropertyChain composition) {

		if (composition.getSaturated().getCompositionsByLeftSubProperty()
				.isEmpty()) {
			for (IndexedObjectProperty toldSuper : composition
					.getToldSuperProperties()) {
				producer.produce(target, new ComposedBackwardLink(source,
						backwardRelation, inferenceRoot, forwardRelation,
						target, toldSuper));
			}
		} else {
			producer.produce(source, new ComposedForwardLink(source,
					backwardRelation, inferenceRoot, forwardRelation, target,
					composition));
		}
	}
}
