package org.uu.nl.ai.intelligent.agents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

public class DLQuery {

	public static void main(final String[] args) throws Exception {

		final String wine = new String(Files.readAllBytes(Paths.get("lib/wine.rdf")), "UTF-8");
		// Load an example ontology.
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		final OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new StringDocumentSource(wine));

		// We need a reasoner to do our query answering
		// This example uses HermiT: http://hermit-reasoner.com/
		final OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(ontology);

		final ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		// Create the DLQueryPrinter helper class. This will manage the
		// parsing of input and printing of results
		final DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(new DLQueryEngine(reasoner, shortFormProvider),
				shortFormProvider);

		// Here is an example for getting instances, subclasses and superclasses with a
		// DL query
		// You can comment out the line below to check the results
		// dlQueryPrinter.printExample();

		// Method for writing down the queries and printing the quiz
		dlQueryPrinter.printExample();

	}

}

class DLQueryEngine {
	private final OWLReasoner reasoner;
	private final DLQueryParser parser;

	public DLQueryEngine(final OWLReasoner reasoner, final ShortFormProvider shortFormProvider) {
		this.reasoner = reasoner;
		this.parser = new DLQueryParser(reasoner.getRootOntology(), shortFormProvider);
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
}

class DLQueryParser {
	private final OWLOntology rootOntology;
	private final BidirectionalShortFormProvider bidiShortFormProvider;

	public DLQueryParser(final OWLOntology rootOntology, final ShortFormProvider shortFormProvider) {
		this.rootOntology = rootOntology;
		final OWLOntologyManager manager = rootOntology.getOWLOntologyManager();
		final Set<OWLOntology> importsClosure = rootOntology.getImportsClosure();
		// Create a bidirectional short form provider to do the actual mapping.
		// It will generate names using the input
		// short form provider.
		this.bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager, importsClosure,
				shortFormProvider);
	}

	public OWLClassExpression parseClassExpression(final String classExpressionString) {
		final OWLDataFactory dataFactory = this.rootOntology.getOWLOntologyManager().getOWLDataFactory();
		final ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(dataFactory,
				classExpressionString);
		parser.setDefaultOntology(this.rootOntology);
		final OWLEntityChecker entityChecker = new ShortFormEntityChecker(this.bidiShortFormProvider);
		parser.setOWLEntityChecker(entityChecker);
		return parser.parseClassExpression();
	}
}

class DLQueryPrinter {
	private final DLQueryEngine dlQueryEngine;
	private final ShortFormProvider shortFormProvider;

	public DLQueryPrinter(final DLQueryEngine engine, final ShortFormProvider shortFormProvider) {
		this.shortFormProvider = shortFormProvider;
		this.dlQueryEngine = engine;
	}

	public void printExample() throws IOException {
		final String example = "Wines that have medium body";
		System.out.println(example);

		final String query = "Wine and hasBody value Medium";

		System.out.println("\nInstances:");
		final Set<OWLNamedIndividual> individuals = this.dlQueryEngine.getInstances(query, false);
		for (final OWLEntity entity : individuals) {
			System.out.println(this.shortFormProvider.getShortForm(entity));
		}

		System.out.println("\nSuperClasses:");
		final Set<OWLClass> superClasses = this.dlQueryEngine.getSuperClasses(query, false);
		for (final OWLClass class_ : superClasses) {
			System.out.println(this.shortFormProvider.getShortForm(class_));
		}

		System.out.println("\nSubClasses:");
		final Set<OWLClass> subClasses = this.dlQueryEngine.getSubClasses(query, false);
		for (final OWLClass class_ : subClasses) {
			System.out.println(this.shortFormProvider.getShortForm(class_));
		}

	}

	public String returnEntity(final String query) {
		String returnString = "";

		for (final OWLEntity entity : quizQuery(query)) {
			returnString = this.shortFormProvider.getShortForm(entity);
			return returnString;
		}
		return "";
	}

	public Set<OWLNamedIndividual> quizQuery(final String classExpression) {
		final Set<OWLNamedIndividual> individuals = this.dlQueryEngine.getInstances(classExpression, false);
		return individuals;
	}

}