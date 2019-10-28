package org.uu.nl.ai.intelligent.agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.uu.nl.ai.intelligent.agents.data.CoursePlan;
import org.uu.nl.ai.intelligent.agents.data.Preferences;
import org.uu.nl.ai.intelligent.agents.query.QueryEngine;

public class CoursePlanner {
	public static final String ONTOLOGY_PATH = "ontology/CoursePlanner.owl";

	public static void main(final String[] args) throws IOException, OWLOntologyCreationException {
		System.out.println("Hello World");

		final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		final String studentId = askForStudentId(reader);

		final Preferences preferences = new Preferences(reader);
		preferences.askForPreferences();

		reader.close();

		final Agent agent = new Agent(studentId, preferences);
		final List<CoursePlan> bestCoursePlans = agent.getBestCoursePlans();

	}

	private static String askForStudentId(final BufferedReader reader)
			throws IOException, OWLOntologyCreationException {
		final Set<String> studentIds = QueryEngine.getInstance().getInstancesShortForm("Topic", false);

		String studentId;
		do {
			System.out.println("Please enter your student ID: ");
			studentId = reader.readLine();
		} while (!isStudentIdValid(studentId, studentIds));
		return studentId;
	}

	private static boolean isStudentIdValid(final String studentId, final Set<String> studentIds) {
		final boolean isValid = studentIds.contains(studentId);

		if (!isValid) {
			System.out.println("Input is not valid, please try again.");
		}

		return isValid;
	}

}
