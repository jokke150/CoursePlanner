package org.uu.nl.ai.intelligent.agents.query;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.uu.nl.ai.intelligent.agents.CoursePlanner;

public class QueryEngine {
	private QueryEngine instance = null;

	private final OWLReasoner reasoner;
	private final QueryParser parser;
	private final ShortFormProvider shortFormProvider;

	private QueryEngine() throws UnsupportedEncodingException, IOException, OWLOntologyCreationException {
		// singleton

		// Load our ontology
		final String wine = new String(Files.readAllBytes(Paths.get(CoursePlanner.ONTOLOGY_PATH)), "UTF-8");
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		final OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new StringDocumentSource(wine));

		// We need a reasoner to do our query answering
		// This example uses HermiT: http://hermit-reasoner.com/
		final OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(ontology);

		this.shortFormProvider = new SimpleShortFormProvider();

		// We need a reasoner to do our query answering
		// This example uses HermiT: http://hermit-reasoner.com/
		this.reasoner = new Reasoner.ReasonerFactory().createReasoner(ontology);
		this.parser = new QueryParser(reasoner.getRootOntology(), this.shortFormProvider);
	}

	public QueryEngine getInstance() throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		if (this.instance == null) {
			this.instance = new QueryEngine();
		}
		return this.instance;
	}

	public Set<OWLClass> getSuperClasses(final String classExpressionString, final boolean direct) {
		if (classExpressionString.trim().length() == 0) {
			return Collections.emptySet();
		}
		final OWLClassExpression classExpression = this.parser.parseClassExpression(classExpressionString);
		final NodeSet<OWLClass> superClasses = this.reasoner.getSuperClasses(classExpression, direct);
		return superClasses.getFlattened();
	}

	public Set<OWLClass> getEquivalentClasses(final String classExpressionString) {
		if (classExpressionString.trim().length() == 0) {
			return Collections.emptySet();
		}
		final OWLClassExpression classExpression = this.parser.parseClassExpression(classExpressionString);
		final Node<OWLClass> equivalentClasses = this.reasoner.getEquivalentClasses(classExpression);
		Set<OWLClass> result = null;
		if (classExpression.isAnonymous()) {
			result = equivalentClasses.getEntities();
		} else {
			result = equivalentClasses.getEntitiesMinus(classExpression.asOWLClass());
		}
		return result;
	}

	public Set<OWLClass> getSubClasses(final String classExpressionString, final boolean direct) {
		if (classExpressionString.trim().length() == 0) {
			return Collections.emptySet();
		}
		final OWLClassExpression classExpression = this.parser.parseClassExpression(classExpressionString);
		final NodeSet<OWLClass> subClasses = this.reasoner.getSubClasses(classExpression, direct);
		return subClasses.getFlattened();
	}

	public Set<OWLNamedIndividual> getInstances(final String classExpressionString, final boolean direct) {
		if (classExpressionString.trim().length() == 0) {
			return Collections.emptySet();
		}
		final OWLClassExpression classExpression = this.parser.parseClassExpression(classExpressionString);
		final NodeSet<OWLNamedIndividual> individuals = this.reasoner.getInstances(classExpression, direct);
		return individuals.getFlattened();
	}

	public Set<String> getInstancesShortForm(final String classExpressionString, final boolean direct) {
		final Set<OWLNamedIndividual> instances = getInstances(classExpressionString, direct);

		final Set<String> instancesShortForm = new HashSet<>();
		instancesShortForm.addAll(
				instances.stream().map(i -> this.shortFormProvider.getShortForm(i)).collect(Collectors.toSet()));

		return instancesShortForm;
	}
}
