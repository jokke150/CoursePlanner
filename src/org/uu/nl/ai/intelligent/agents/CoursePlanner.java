package org.uu.nl.ai.intelligent.agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.uu.nl.ai.intelligent.agents.data.CoursePlan;
import org.uu.nl.ai.intelligent.agents.data.Preferences;
import org.uu.nl.ai.intelligent.agents.query.QueryEngine;

public class CoursePlanner {
	public static final String ONTOLOGY_PATH = "ontology/CoursePlanner.owl";
	public static final boolean READ_CACHE = true;

	public static void main(final String[] args)
			throws IOException, OWLOntologyCreationException, ClassNotFoundException {
		System.out.println("Hello World");

		if (READ_CACHE) {
			QueryEngine.getInstance().readInstancesShortFormCache();
		}

		final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

//		final String student = askForStudent(reader);

		final String student = "Student1";

//		final Preferences preferences = new Preferences(reader);
//		preferences.askForPreferences();

		final Set<String> preferredCourses = Arrays.asList("IntroductiontoNaturalSciences", "IntelligentAgents",
				"LogicAndLanguage", "MachineLearning", "LogicAndComputation", "Statistics", "NaturalLanguageProcessing",
				"BusinessIntelligence", "ComputerVision").stream().collect(Collectors.toSet());
		final Set<String> preferredTopics = Arrays.asList("DescriptionLogic", "Optimisation").stream()
				.collect(Collectors.toSet());
		final Set<String> preferredLecturers = Arrays.asList("Lecturer1").stream().collect(Collectors.toSet());
		final Set<String> preferredDays = Arrays.asList("Monday", "Tuesday").stream().collect(Collectors.toSet());

		final Set<String> dislikedCourses = Arrays.asList("MachineLearning", "LogicAndComputation").stream()
				.collect(Collectors.toSet());
		final Set<String> dislikedTopics = Arrays.asList("Multi-AgentSystems", "ResearchInternshipAI").stream()
				.collect(Collectors.toSet());
		final Set<String> dislikedLecturers = Arrays.asList("Lecturer2").stream().collect(Collectors.toSet());
		final Set<String> dislikedDays = Arrays.asList("Thursday", "Wednesday").stream().collect(Collectors.toSet());

		final Preferences preferences = new Preferences(preferredCourses, preferredTopics, preferredLecturers,
				preferredDays, 3, 6, 5, 8, dislikedCourses, dislikedTopics, dislikedLecturers, dislikedDays, 7, 2, 1,
				8);

		reader.close();

		final Agent agent = new Agent(student, preferences);
		final Set<CoursePlan> bestCoursePlans = agent.getBestCoursePlans();

		System.out.println(bestCoursePlans);

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

}
