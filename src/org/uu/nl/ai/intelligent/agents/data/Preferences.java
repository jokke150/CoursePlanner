package org.uu.nl.ai.intelligent.agents.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.uu.nl.ai.intelligent.agents.query.QueryEngine;

public class Preferences {
	private static final short MIN_RATING = 1;
	private static final short MAX_RATING = 10;

	private final BufferedReader reader;

	private Set<String> preferredCourses;
	private Set<String> preferredTopics;
	private Set<String> preferredLecturers;
	private Set<String> preferredDays;

	private short preferredCoursesWeight;
	private short preferredTopicsWeight;
	private short preferredLecturersWeight;
	private short preferredDaysWeight;

	private Set<String> dislikedCourses;
	private Set<String> dislikedTopics;
	private Set<String> dislikedLecturers;
	private Set<String> dislikedDays;

	private short dislikedCoursesWeight;
	private short dislikedTopicsWeight;
	private short dislikedLecturersWeight;
	private short dislikedDaysWeight;

	public Preferences(final BufferedReader reader) {
		super();
		this.reader = reader;
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

	public short getPreferredCoursesWeight() {
		return this.preferredCoursesWeight;
	}

	public short getPreferredTopicsWeight() {
		return this.preferredTopicsWeight;
	}

	public short getPreferredLecturersWeight() {
		return this.preferredLecturersWeight;
	}

	public short getPreferredDaysWeight() {
		return this.preferredDaysWeight;
	}

	public Set<String> getDislikedCourses() {
		return this.dislikedCourses;
	}

	public Set<String> getDislikedTopics() {
		return this.dislikedTopics;
	}

	public Set<String> getDislikedLecturers() {
		return this.dislikedLecturers;
	}

	public Set<String> getDislikedDays() {
		return this.dislikedDays;
	}

	public short getDislikedCoursesWeight() {
		return this.dislikedCoursesWeight;
	}

	public short getDislikedTopicsWeight() {
		return this.dislikedTopicsWeight;
	}

	public short getDislikedLecturersWeight() {
		return this.dislikedLecturersWeight;
	}

	public short getDislikedDaysWeight() {
		return this.dislikedDaysWeight;
	}

	public void askForPreferences() throws IOException, OWLOntologyCreationException {
		askForCoursePreferences();
		askForTopicPreferences();
		askForLecturerPreferences();
		askForDayPreferences();
	}

	private void askForCoursePreferences() throws IOException, OWLOntologyCreationException {
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
		this.preferredCoursesWeight = getWeightRating();

		Set<String> dislikedCourses;
		do {
			System.out.println("Please enter your disliked courses (comma-separated): ");
			printRange(courses.stream().filter(i -> this.preferredCourses.contains(i)).collect(Collectors.toSet()));
			dislikedCourses = convertInput(this.reader.readLine());
		} while (!isInputValid(dislikedCourses, courses, preferredCourses));
		this.dislikedCourses = dislikedCourses;

		System.out.println("How important is it to not take courses that you dislike? Enter a value between "
				+ MIN_RATING + " and " + MAX_RATING + ": ");
		this.dislikedCoursesWeight = getWeightRating();
	}

	private void askForTopicPreferences() throws IOException, OWLOntologyCreationException {
		final Set<String> topics = QueryEngine.getInstance().getInstancesShortForm("Topic", false);
		Set<String> preferredTopics;
		do {
			System.out.println("Please enter your preferred topics (comma-separated): ");
			printRange(topics);
			preferredTopics = convertInput(this.reader.readLine());
		} while (!isInputValid(preferredTopics, topics));
		this.preferredTopics = preferredTopics;

		System.out.println("How important is it to take courses that are on preferred topics? Enter a value between "
				+ MIN_RATING + " and " + MAX_RATING + ": ");
		this.preferredTopicsWeight = getWeightRating();

		Set<String> dislikedTopics;
		do {
			System.out.println("Please enter your disliked topics (comma-separated): ");
			printRange(topics.stream().filter(i -> this.preferredTopics.contains(i)).collect(Collectors.toSet()));
			dislikedTopics = convertInput(this.reader.readLine());
		} while (!isInputValid(dislikedTopics, topics, preferredTopics));
		this.dislikedTopics = dislikedTopics;

		System.out.println("How important is it to not take courses that are on disliked topics? Enter a value between "
				+ MIN_RATING + " and " + MAX_RATING + ": ");
		this.dislikedTopicsWeight = getWeightRating();
	}

	private void askForLecturerPreferences() throws IOException, OWLOntologyCreationException {
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
		this.preferredLecturersWeight = getWeightRating();

		Set<String> dislikedLecturers;
		do {
			System.out.println("Please enter your disliked lecturers (comma-separated): ");
			printRange(lecturers.stream().filter(i -> this.preferredLecturers.contains(i)).collect(Collectors.toSet()));
			dislikedLecturers = convertInput(this.reader.readLine());
		} while (!isInputValid(dislikedLecturers, lecturers, preferredLecturers));
		this.dislikedLecturers = dislikedLecturers;

		System.out.println(
				"How important is it to not take courses that are taught by lecturers you dislike? Enter a value between "
						+ MIN_RATING + " and " + MAX_RATING + ": ");
		this.dislikedDaysWeight = getWeightRating();
	}

	private void askForDayPreferences() throws IOException {
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
		this.preferredDaysWeight = getWeightRating();

		Set<String> dislikedDays;
		do {
			System.out.println("Please enter your disliked days (comma-separated): ");
			printRange(days.stream().filter(i -> this.preferredDays.contains(i)).collect(Collectors.toSet()));
			dislikedDays = convertInput(this.reader.readLine());
		} while (!isInputValid(dislikedDays, days, preferredDays));
		this.dislikedDays = dislikedDays;

		System.out.println(
				"How important is it not to take courses that are taught on days you dislike? Enter a value between "
						+ MIN_RATING + " and " + MAX_RATING + ": ");
		this.dislikedDaysWeight = getWeightRating();
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
		return isInputValid(input, range, new ArrayList<>());
	}

	private static boolean isInputValid(final Collection<String> input, final Collection<String> range,
			final Collection<String> preferences) {
		final boolean withinRange = input.stream().allMatch(i -> range.contains(i));
		if (!withinRange) {
			System.out.println("Input is not valid, please try again.");
		}
		final Set<String> dislikedAndPreferred = input.stream().filter(i -> preferences.contains(i))
				.collect(Collectors.toSet());
		if (!dislikedAndPreferred.isEmpty()) {
			System.out.println(
					"You cannot dislike and prefer " + dislikedAndPreferred.iterator().next() + " at the same time..");
		}
		return withinRange && dislikedAndPreferred.isEmpty();
	}
}
