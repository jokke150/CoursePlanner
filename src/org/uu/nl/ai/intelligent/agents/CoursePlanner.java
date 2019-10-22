package org.uu.nl.ai.intelligent.agents;

import java.io.IOException;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.uu.nl.ai.intelligent.agents.data.CoursePlan;
import org.uu.nl.ai.intelligent.agents.data.Preferences;

public class CoursePlanner {
	public static final String ONTOLOGY_PATH = "ontology/CoursePlanner.owl";

	public static void main(final String[] args) throws IOException, OWLOntologyCreationException {
		System.out.println("Hello World");

		final Preferences preferences = new Preferences();
		preferences.askForPreferences();

		final Agent agent = new Agent(preferences);
		final List<CoursePlan> bestCoursePlans = agent.getBestCoursePlans();
	}

}
