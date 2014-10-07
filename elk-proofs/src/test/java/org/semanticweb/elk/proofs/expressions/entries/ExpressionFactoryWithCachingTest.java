/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.entries;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.semanticweb.elk.proofs.expressions.TestEntities.a;
import static org.semanticweb.elk.proofs.expressions.TestEntities.b;
import static org.semanticweb.elk.proofs.expressions.TestEntities.c;
import static org.semanticweb.elk.proofs.expressions.TestEntities.d;
import static org.semanticweb.elk.proofs.expressions.TestEntities.q;
import static org.semanticweb.elk.proofs.expressions.TestEntities.r;
import static org.semanticweb.elk.proofs.expressions.TestEntities.s;

import java.util.Arrays;

import org.junit.Test;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.proofs.InferenceReader;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactory;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactoryWithCaching;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaObjectFactory;
import org.semanticweb.elk.proofs.expressions.lemmas.impl.ElkLemmaObjectFactoryImpl;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ExpressionFactoryWithCachingTest {

	private static final ElkObjectFactory objFactory_ = new ElkObjectFactoryImpl();
	private static final ElkLemmaObjectFactory lemmaFactory_ = new ElkLemmaObjectFactoryImpl();
	
	@Test
	public void axiomCaching() {
		DerivedExpressionFactory factory = new DerivedExpressionFactoryWithCaching(InferenceReader.DUMMY);
		
		Expression first = factory.create(objFactory_.getSubClassOfAxiom(a, b));
		Expression second = factory.create(objFactory_.getSubClassOfAxiom(a, b));
		
		assertSame(first, second);
		
		first = factory.create(objFactory_.getEquivalentClassesAxiom(a, objFactory_.getObjectSomeValuesFrom(r, b)));
		second = factory.create(objFactory_.getEquivalentClassesAxiom(a, objFactory_.getObjectSomeValuesFrom(r, b)));
		
		assertSame(first, second);
		
		first = factory.create(objFactory_.getDisjointClassesAxiom(a, objFactory_.getObjectSomeValuesFrom(r, b), b, c, d));
		second = factory.create(objFactory_.getDisjointClassesAxiom(a, objFactory_.getObjectSomeValuesFrom(r, b), d, b, c));
		
		assertSame(first, second);
		
		first = factory.create(objFactory_.getDisjointClassesAxiom(a, objFactory_.getObjectSomeValuesFrom(r, b), b, b, c, d));
		second = factory.create(objFactory_.getDisjointClassesAxiom(a, objFactory_.getObjectSomeValuesFrom(r, b), d, b, c));
		
		assertNotSame(first, second);
	}
	
	@Test
	public void lemmaCaching() {
		DerivedExpressionFactory factory = new DerivedExpressionFactoryWithCaching(InferenceReader.DUMMY);
		// complex existentials
		ElkObjectPropertyChain rs = objFactory_.getObjectPropertyChain(Arrays.asList(r, s));
		Expression first = factory.create(lemmaFactory_.getSubClassOfLemma(a, lemmaFactory_.getComplexObjectSomeValuesFrom(rs, b)));
		Expression second = factory.create(lemmaFactory_.getSubClassOfLemma(a, lemmaFactory_.getComplexObjectSomeValuesFrom(rs, b)));
		
		assertSame(first, second);
		// reflexive chain
		first = factory.create(lemmaFactory_.getReflexivePropertyChainLemma(rs));
		second = factory.create(lemmaFactory_.getReflexivePropertyChainLemma(rs));
		
		assertSame(first, second);
		// complex chain subsumption
		ElkObjectPropertyChain rsq = objFactory_.getObjectPropertyChain(Arrays.asList(r, s, q));
		ElkObjectPropertyChain rsq2 = objFactory_.getObjectPropertyChain(Arrays.asList(r, s, q));
		ElkObjectPropertyChain rqs = objFactory_.getObjectPropertyChain(Arrays.asList(r, q, s));
		
		first = factory.create(lemmaFactory_.getSubPropertyChainOfLemma(rs, rsq));
		second = factory.create(lemmaFactory_.getSubPropertyChainOfLemma(rs, rsq2));
		
		assertSame(first, second);
		
		second = factory.create(lemmaFactory_.getSubPropertyChainOfLemma(rs, rqs));
		
		assertNotSame(first, second);
	}
	
}