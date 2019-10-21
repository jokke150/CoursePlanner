package org.uu.nl.ai.intelligent.agents.query;

import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;

public class QueryParser {
	private final OWLOntology rootOntology;
	private final BidirectionalShortFormProvider bidiShortFormProvider;

	public QueryParser(final OWLOntology rootOntology, final ShortFormProvider shortFormProvider) {
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