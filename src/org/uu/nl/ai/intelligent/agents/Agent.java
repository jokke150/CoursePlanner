package org.uu.nl.ai.intelligent.agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Agent {

	public static void main(final String[] args) throws IOException {

		System.out.println("Hello World");

		final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		final List<String> friends = askForFriends(reader);
		final List<String> preferredCourses = askForPreferredCourses(reader);
		final List<String> preferredTopics = askForPreferredTopics(reader);
		final List<String> preferredLecturers = askForPreferredLecturers(reader);
		final List<String> preferredDays = askForPreferredDays(reader);
	}

	private static List<String> askForFriends(final BufferedReader reader) throws IOException {
		// TODO: Query range and validate
		System.out.println("Please enter your friends names (comma-separated): ");
		final String friends = reader.readLine();
		return Arrays.asList(friends.split("[ ]*,[ ]*"));
	}

	private static List<String> askForPreferredCourses(final BufferedReader reader) throws IOException {
		// TODO: Query range and validate
		System.out.println("Please enter your preferred courses (comma-separated): ");
		final String courses = reader.readLine();
		return Arrays.asList(courses.split("[ ]*,[ ]*"));
	}

	private static List<String> askForPreferredTopics(final BufferedReader reader) throws IOException {
		// TODO: Query range and validate
		System.out.println("Please enter your preferred topics (comma-separated): ");
		final String topics = reader.readLine();
		return Arrays.asList(topics.split("[ ]*,[ ]*"));
	}

	private static List<String> askForPreferredLecturers(final BufferedReader reader) throws IOException {
		// TODO: Query range and validate
		System.out.println("Please enter your preferred lecturers (comma-separated): ");
		return convertInput(reader.readLine());
	}

	private static List<String> askForPreferredDays(final BufferedReader reader) throws IOException {
		final List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
		List<String> preferredDayStrings;
		do {
			System.out.println("Please enter your preferred days (comma-separated): ");
			printRange(days);
			preferredDayStrings = convertInput(reader.readLine());
		} while (!isInputValid(preferredDayStrings, days));
		return preferredDayStrings;
	}

	private static void printRange(final List<String> range) {
		System.out.println("Valid values: " + Arrays.toString(range.toArray()));
	}

	private static List<String> convertInput(final String input) {
		final List<String> output = new ArrayList<>();
		for (final String in : input.split("[ ]*,[ ]*")) {
			if (!in.isBlank()) {
				output.add(in);
			}
		}
		return output;
	}

	private static boolean isInputValid(final List<String> input, final List<String> range) {
		final boolean isValid = input.stream().allMatch(i -> range.contains(i));
		if (!isValid) {
			System.out.println("Input is not valid, please try again. ");
		}
		return isValid;
	}

}
