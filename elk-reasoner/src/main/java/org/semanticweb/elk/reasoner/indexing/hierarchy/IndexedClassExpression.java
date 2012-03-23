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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.rules.Derivable;
import org.semanticweb.elk.reasoner.rules.QueueableVisitor;
import org.semanticweb.elk.reasoner.rules.SaturatedClassExpression;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Represents all occurrences of an ElkClassExpression in an ontology. To this
 * end, objects of this class keeps a number of lists to describe the
 * relationships to other (indexed) class expressions. The data structures are
 * optimized for quickly retrieving the relevant relationships during
 * inferencing.
 * 
 * This class is mainly a data container that provides direct public access to
 * its content. The task of updating index structures consistently in a global
 * sense is left to callers.
 * 
 * @author "Frantisek Simancik"
 * @author "Markus Kroetzsch"
 * @author "Yevgeny Kazakov"
 */
abstract public class IndexedClassExpression implements Derivable {

	/**
	 * Correctness of axioms deletions requires that toldSuperClassExpressions
	 * is a List.
	 */
	protected List<IndexedClassExpression> toldSuperClassExpressions;
	protected Map<IndexedClassExpression, IndexedObjectIntersectionOf> negConjunctionsByConjunct;
	protected List<IndexedObjectSomeValuesFrom> negExistentials;

	/**
	 * This counts how often this object occurred positively. Some indexing
	 * operations are only needed when encountering objects positively for the
	 * first time.
	 */
	protected int positiveOccurrenceNo = 0;

	/**
	 * This counts how often this object occurred negatively. Some indexing
	 * operations are only needed when encountering objects negatively for the
	 * first time.
	 */
	protected int negativeOccurrenceNo = 0;

	/**
	 * This method should always return true apart from intermediate steps
	 * during the indexing.
	 * 
	 * @return true if the represented class expression occurs in the ontology
	 */
	public boolean occurs() {
		return positiveOccurrenceNo > 0 || negativeOccurrenceNo > 0;
	}

	/**
	 * @return true if the represented class expression occurs negatively in the
	 *         ontology
	 */
	public boolean occursNegatively() {
		return negativeOccurrenceNo > 0;
	}

	/**
	 * @return true if the represented class expression occurs positively in the
	 *         ontologu
	 */
	public boolean occursPositively() {
		return positiveOccurrenceNo > 0;
	}

	/**
	 * Non-recursively. The recursion is implemented in indexing visitors.
	 */
	protected abstract void updateOccurrenceNumbers(IndexUpdater indexUpdater,
			int increment, int positiveIncrement, int negativeIncrement);

	/**
	 * @return All told super class expressions of this class expression,
	 *         possibly null.
	 */
	public List<IndexedClassExpression> getToldSuperClassExpressions() {
		return toldSuperClassExpressions;
	}

	/**
	 * @return Indexed conjunctions that occur negatively and contain this class
	 *         expression, indexed by the second conjunct, possibly null.
	 */
	public Map<IndexedClassExpression, IndexedObjectIntersectionOf> getNegConjunctionsByConjunct() {
		return negConjunctionsByConjunct;
	}

	/**
	 * @return Indexed existentials that occur negatively and have this class
	 *         expression as the filler, possibly null.
	 */
	public List<IndexedObjectSomeValuesFrom> getNegExistentials() {
		return negExistentials;
	}

	// TODO: replace pointers to contexts by a mapping

	/**
	 * Used for efficient retrieval of the Context corresponding to this class
	 * expression.
	 */
	protected final AtomicReference<SaturatedClassExpression> saturated = new AtomicReference<SaturatedClassExpression>();

	/**
	 * @return The corresponding SaturatedClassExpression, null if none was
	 *         assigned.
	 */
	public SaturatedClassExpression getSaturated() {
		return saturated.get();
	}

	/**
	 * Sets the corresponding SaturatedClassExpression if none was yet assigned.
	 * 
	 * @return True if the operation succeeded.
	 */
	public boolean setSaturated(
			SaturatedClassExpression saturatedClassExpression) {
		return saturated.compareAndSet(null, saturatedClassExpression);
	}

	/**
	 * Resets the corresponding SaturatedClassExpression to null.
	 */
	public void resetSaturated() {
		saturated.set(null);
	}

	/** Hash code for this object. */
	private final int hashCode_ = HashGenerator.generateNextHashCode();

	/**
	 * Get an integer hash code to be used for this object.
	 * 
	 * @return Hash code.
	 */
	@Override
	public final int hashCode() {
		return hashCode_;
	}

	public abstract <O> O accept(IndexedClassExpressionVisitor<O> visitor);

	public <O> O accept(QueueableVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public abstract String toString();
}
