/*
 * #%L
 * ELK Bencharking Package
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
/**
 * 
 */
package org.semanticweb.elk.benchmark.reasoning;

import java.io.File;

import org.semanticweb.elk.benchmark.BenchmarkUtils;
import org.semanticweb.elk.benchmark.Result;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.loading.EmptyChangesLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * A task to classify an ontology
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ClassificationTask implements Task {

	private Reasoner reasoner_;
	private final String ontologyFile_;
	private final ReasonerConfiguration reasonerConfig_;
	
	public ClassificationTask(String[] args) {
		ontologyFile_ = args[0];
		reasonerConfig_ = BenchmarkUtils.getReasonerConfiguration(args);
	}
	
	@Override
	public String getName() {
		return "EL classification [" + ontologyFile_.substring(ontologyFile_.lastIndexOf('/')) + "]";
	}

	@Override
	public void prepare() throws TaskException {
		try {
			File ontologyFile = BenchmarkUtils.getFile(ontologyFile_);
			
			reasoner_ = new ReasonerFactory().createReasoner(new LoggingStageExecutor(), reasonerConfig_);
			reasoner_.registerOntologyLoader(new Owl2StreamLoader(
				new Owl2FunctionalStyleParserFactory(), ontologyFile));
			reasoner_.registerOntologyChangesLoader(new EmptyChangesLoader());
			reasoner_.loadOntology();
		} catch (Exception e) {
			throw new TaskException(e);
		}
	}

	@Override
	public Result run() throws TaskException {
		try {
			Taxonomy<ElkClass> t = reasoner_.getTaxonomy();
			
			System.out.println(TaxonomyHasher.hash(t));
			
		} catch (ElkException e) {
			throw new TaskException(e);
		}
		finally {
			try {
				reasoner_.shutdown();
			} catch (InterruptedException e) {}
		}
		
		return null;
	}

}