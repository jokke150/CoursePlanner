package org.uu.nl.ai.intelligent.agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.uu.nl.ai.intelligent.agents.data.CoursePlan;
import org.uu.nl.ai.intelligent.agents.data.Preferences;
import org.uu.nl.ai.intelligent.agents.query.QueryEngine;

public class CoursePlanner {
	private static final boolean USE_SCENARIO_DATA = true;

	public static final String ONTOLOGY_PATH = "ontology/CoursePlanner.owl";

	// Needs to be set to false after every ontology change to rebuild query cache
	public static final boolean READ_CACHE = true;

	public static final int CACHE_WRITE_QUERIES = 5; // Number of queries after which cache is persisted

	public static void main(final String[] args)
			throws IOException, OWLOntologyCreationException, ClassNotFoundException {

		if (READ_CACHE) {
			QueryEngine.getInstance().readInstancesShortFormCache();
		}

		final String student;
		final Preferences preferences;
		if (USE_SCENARIO_DATA) {
			// UNCOMMENT TO USE ;)

			// SCENARIO 1: Zero Utility
			// student = TestPreferenceProvider.getStudentForScenarioZeroUtility();
			// preferences = TestPreferenceProvider.getPreferencesForScenarioZeroUtility();

			// SCENARIO 2: Prerequisite Inception
			student = TestPreferenceProvider.getStudentForScenarioPrereqInception();
			preferences = TestPreferenceProvider.getPreferencesForScenarioPrereqInception();

			// SCENARIO 3: Impossible Ontology
			// student = TestPreferenceProvider.getStudentForScenarioImpossibleOntology();
			// preferences =
			// TestPreferenceProvider.getPreferencesForScenarioImpossibleOntology();

			// SCENARIO 4: Use Case 1
			// student = TestPreferenceProvider.getStudentForScenarioUseCase1();
			// preferences = TestPreferenceProvider.getPreferencesForScenarioUseCase1();
		} else {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			student = askForStudent(reader);
			preferences = new Preferences(reader);
			preferences.askForPreferences();
			reader.close();
		}

		final Agent agent = new Agent(student, preferences);
		final Set<CoursePlan> bestCoursePlans = agent.getBestCoursePlans();

		printCoursePlans(bestCoursePlans);

	}

	private static String askForStudent(final BufferedReader reader) throws IOException, OWLOntologyCreationException {
		Optional<String> student;
		do {
			System.out.println("Please enter your student ID: ");
			final String studentId = reader.readLine();

			try {
				Integer.parseInt(studentId);
				student = QueryEngine.getInstance().getInstancesShortForm("student_ID value " + studentId, false)
						.stream().findFirst();
			} catch (final NumberFormatException exception) {
				// Not a number
				student = Optional.empty();
			}
		} while (!isStudentIdValid(student));
		return student.get();
	}

	private static boolean isStudentIdValid(final Optional<String> student) {
		final boolean isValid = student.isPresent();

		if (!isValid) {
			System.out.println("Student Id could not have been found.");
		}

		return isValid;
	}

	private static void printCoursePlans(final Set<CoursePlan> coursePlans)
			throws IOException, OWLOntologyCreationException, ClassNotFoundException {
		final QueryEngine queryEngine = QueryEngine.getInstance();
		Set<String> courses;
		String building;
		String classroom;
		String day;

		final int numOfCoursePlan = 1;
		for (final CoursePlan coursePlan : coursePlans) {
			System.out.println("------------------------------");
			System.out.println("This is your study plan number " + numOfCoursePlan + ":\n");
			for (int i = 1; i <= 4; i++) {
				System.out.println("PERIOD " + i + ":");
				courses = coursePlan.getCoursesInPeriod(i);

				for (final String course : courses) {
					building = String.join(" , ",
							queryEngine.getInstancesShortForm("Building and houses value " + course, false));
					classroom = String.join(" , ",
							queryEngine.getInstancesShortForm("Classroom and houses value " + course, false));
					day = String.join(" and ",
							queryEngine.getInstancesShortForm("Day and comprisesCourse value " + course, false));
					System.out.println("*** " + course + " - every " + day + " in " + classroom + ", " + building);
				}
				System.out.println("\n");
			}
			System.out.println("------------------------------");
			System.out.println("In total the agent found " + coursePlans.size() + " study plan(s).");
		}
	}

}
