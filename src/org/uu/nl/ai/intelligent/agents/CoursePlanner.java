package org.uu.nl.ai.intelligent.agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.uu.nl.ai.intelligent.agents.query.QueryEngine;

public class CoursePlanner {
	public static final String ONTOLOGY_PATH = "ontology/CoursePlanner.owl";

	public static void main(final String[] args) throws IOException, OWLOntologyCreationException {
		System.out.println("Hello World");

		final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		final Set<String> friends = askForFriends(reader);
		final Set<String> preferredCourses = askForPreferredCourses(reader);
		final Set<String> preferredTopics = askForPreferredTopics(reader);
		final Set<String> preferredLecturers = askForPreferredLecturers(reader);
		final Set<String> preferredDays = askForPreferredDays(reader);

		final short friendsImportance;
		final short coursesImportance;
		final short topicsImportance;
		final short lecturersImportance;
		final short daysImportance;
	}

	private static Set<String> askForFriends(final BufferedReader reader)
			throws IOException, OWLOntologyCreationException {
		final Set<String> students = QueryEngine.getInstance().getInstancesShortForm("Student", false);
		Set<String> friends;
		do {
			System.out.println("Please enter your friends names (comma-separated): ");
			printRange(students);
			friends = convertInput(reader.readLine());
		} while (!isInputValid(friends, students));
		return friends;
	}

	private static Set<String> askForPreferredCourses(final BufferedReader reader)
			throws IOException, OWLOntologyCreationException {
		final Set<String> courses = QueryEngine.getInstance().getInstancesShortForm("Course", false);
		Set<String> preferredCourses;
		do {
			System.out.println("Please enter your preferred courses (comma-separated): ");
			printRange(courses);
			preferredCourses = convertInput(reader.readLine());
		} while (!isInputValid(preferredCourses, courses));
		return preferredCourses;
	}

	private static Set<String> askForPreferredTopics(final BufferedReader reader)
			throws IOException, OWLOntologyCreationException {
		final Set<String> topics = QueryEngine.getInstance().getInstancesShortForm("Topic", false);
		Set<String> preferredTopics;
		do {
			System.out.println("Please enter your preferred courses (comma-separated): ");
			printRange(topics);
			preferredTopics = convertInput(reader.readLine());
		} while (!isInputValid(preferredTopics, topics));
		return preferredTopics;
	}

	private static Set<String> askForPreferredLecturers(final BufferedReader reader)
			throws IOException, OWLOntologyCreationException {
		final Set<String> lecturers = QueryEngine.getInstance().getInstancesShortForm("Lecturer", false);
		Set<String> preferredLecturers;
		do {
			System.out.println("Please enter your preferred lecturers (comma-separated): ");
			printRange(lecturers);
			preferredLecturers = convertInput(reader.readLine());
		} while (!isInputValid(preferredLecturers, lecturers));
		return preferredLecturers;
	}

	private static Set<String> askForPreferredDays(final BufferedReader reader) throws IOException {
		final List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
				"Sunday");
		Set<String> preferredDays;
		do {
			System.out.println("Please enter your preferred days (comma-separated): ");
			printRange(days);
			preferredDays = convertInput(reader.readLine());
		} while (!isInputValid(preferredDays, days));
		return preferredDays;
	}

	private static void printRange(final Collection<String> range) {
		System.out.println("Valid values: " + Arrays.toString(range.toArray()));
	}

	private static Set<String> convertInput(final String input) {
		final Set<String> output = new HashSet<>();
		for (final String in : input.split("[ ]*,[ ]*")) {
			if (!in.isBlank()) {
				output.add(in);
			}
		}
		return output;
	}

	private static boolean isInputValid(final Collection<String> input, final Collection<String> range) {
		final boolean isValid = input.stream().allMatch(i -> range.contains(i));
		if (!isValid) {
			System.out.println("Input is not valid, please try again. ");
		}
		return isValid;
	}

}
