package org.uu.nl.ai.intelligent.agents.data;

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

public class Preferences {
	private static final short MIN_RATING = 1;
	private static final short MAX_RATING = 10;

	private BufferedReader reader;

	private Set<String> preferredCourses;
	private Set<String> friends;
	private Set<String> preferredTopics;
	private Set<String> preferredLecturers;
	private Set<String> preferredDays;

	private short friendsWeight;
	private short coursesWeight;
	private short topicsWeight;
	private short lecturersWeight;
	private short daysWeight;

	public Preferences() {
		super();
	}

	public Set<String> getFriends() {
		return this.friends;
	}

	public Set<String> getPreferredCourses() {
		return this.preferredCourses;
	}

	public Set<String> getPreferredTopics() {
		return this.preferredTopics;
	}

	public Set<String> getPreferredLecturers() {
		return this.preferredLecturers;
	}

	public Set<String> getPreferredDays() {
		return this.preferredDays;
	}

	public short getFriendsWeight() {
		return this.friendsWeight;
	}

	public short getCoursesWeight() {
		return this.coursesWeight;
	}

	public short getTopicsWeight() {
		return this.topicsWeight;
	}

	public short getLecturersWeight() {
		return this.lecturersWeight;
	}

	public short getDaysWeight() {
		return this.daysWeight;
	}

	public void askForPreferences() throws IOException, OWLOntologyCreationException {
		this.reader = new BufferedReader(new InputStreamReader(System.in));
		askForPreferredCourses();
		askForFriends();
		askForPreferredTopics();
		askForPreferredLecturers();
		askForPreferredDays();
		this.reader.close();
	}

	private void askForPreferredCourses() throws IOException, OWLOntologyCreationException {
		final Set<String> courses = QueryEngine.getInstance().getInstancesShortForm("Course", false);
		Set<String> preferredCourses;
		do {
			System.out.println("Please enter your preferred courses (comma-separated): ");
			printRange(courses);
			preferredCourses = convertInput(this.reader.readLine());
		} while (!isInputValid(preferredCourses, courses));
		this.preferredCourses = preferredCourses;

		System.out.println("How important is it to take courses that you prefer? Enter a value between " + MIN_RATING
				+ " and " + MAX_RATING + ": ");
		this.coursesWeight = getWeightRating();
	}

	private void askForFriends() throws IOException, OWLOntologyCreationException {
		final Set<String> students = QueryEngine.getInstance().getInstancesShortForm("Student", false);
		Set<String> friends;
		do {
			System.out.println("Please enter your friends names (comma-separated): ");
			printRange(students);
			friends = convertInput(this.reader.readLine());
		} while (!isInputValid(friends, students));
		this.friends = friends;

		System.out.println("How important is it to take courses that your friends take? Enter a value between "
				+ MIN_RATING + " and " + MAX_RATING + ": ");
		this.friendsWeight = getWeightRating();
	}

	private void askForPreferredTopics() throws IOException, OWLOntologyCreationException {
		final Set<String> topics = QueryEngine.getInstance().getInstancesShortForm("Topic", false);
		Set<String> preferredTopics;
		do {
			System.out.println("Please enter your preferred courses (comma-separated): ");
			printRange(topics);
			preferredTopics = convertInput(this.reader.readLine());
		} while (!isInputValid(preferredTopics, topics));
		this.preferredTopics = preferredTopics;

		System.out.println("How important is it to take courses that are on preferred topics? Enter a value between "
				+ MIN_RATING + " and " + MAX_RATING + ": ");
		this.topicsWeight = getWeightRating();
	}

	private void askForPreferredLecturers() throws IOException, OWLOntologyCreationException {
		final Set<String> lecturers = QueryEngine.getInstance().getInstancesShortForm("Lecturer", false);
		Set<String> preferredLecturers;
		do {
			System.out.println("Please enter your preferred lecturers (comma-separated): ");
			printRange(lecturers);
			preferredLecturers = convertInput(this.reader.readLine());
		} while (!isInputValid(preferredLecturers, lecturers));
		this.preferredLecturers = preferredLecturers;

		System.out.println(
				"How important is it to take courses that are taught by lecturers you prefer? Enter a value between "
						+ MIN_RATING + " and " + MAX_RATING + ": ");
		this.lecturersWeight = getWeightRating();
	}

	private void askForPreferredDays() throws IOException {
		final List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
				"Sunday");
		Set<String> preferredDays;
		do {
			System.out.println("Please enter your preferred days (comma-separated): ");
			printRange(days);
			preferredDays = convertInput(this.reader.readLine());
		} while (!isInputValid(preferredDays, days));
		this.preferredDays = preferredDays;

		System.out.println(
				"How important is it to take courses that are taught on days you prefer? Enter a value between "
						+ MIN_RATING + " and " + MAX_RATING + ": ");
		this.daysWeight = getWeightRating();
	}

	private short getWeightRating() throws IOException {
		short weight = MIN_RATING - 1;
		while (weight < MIN_RATING) {
			try {
				weight = Short.parseShort(this.reader.readLine());
			} catch (final NumberFormatException exception) {
				System.out.println("Input is not valid, please try again.");
			}
		}
		return weight;
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
			System.out.println("Input is not valid, please try again.");
		}
		return isValid;
	}
}
