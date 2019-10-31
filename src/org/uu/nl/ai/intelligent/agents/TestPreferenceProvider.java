package org.uu.nl.ai.intelligent.agents;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.uu.nl.ai.intelligent.agents.data.Preferences;

public class TestPreferenceProvider {
	public static String getStudentForScenarioPrereqBranching() {
		return "Student1";
	}

	public static Preferences getPreferencesForScenarioPrereqBranching() {
		final Set<String> preferredCourses = Arrays.asList("IntelligentAgents").stream().collect(Collectors.toSet());
		final Set<String> preferredTopics = Arrays.asList("DescriptionLogic", "Optimisation").stream()
				.collect(Collectors.toSet());
		final Set<String> preferredLecturers = Arrays.asList("Lecturer1").stream().collect(Collectors.toSet());
		final Set<String> preferredDays = Arrays.asList("Monday", "Tuesday").stream().collect(Collectors.toSet());

		final Set<String> dislikedCourses = Arrays.asList("BusinessIntelligence").stream().collect(Collectors.toSet());
		final Set<String> dislikedTopics = Arrays.asList("Multi-AgentSystems", "ResearchInternshipAI").stream()
				.collect(Collectors.toSet());
		final Set<String> dislikedLecturers = Arrays.asList("Lecturer2").stream().collect(Collectors.toSet());
		final Set<String> dislikedDays = Arrays.asList("Thursday", "Wednesday").stream().collect(Collectors.toSet());

		return new Preferences(preferredCourses, preferredTopics, preferredLecturers, preferredDays, 10, 1, 5, 8,
				dislikedCourses, dislikedTopics, dislikedLecturers, dislikedDays, 2, 2, 1, 8);
	}

	public static String getStudentForScenarioZeroUtility() {
		return "StudentA";
	}

	public static Preferences getPreferencesForScenarioZeroUtility() {
		final Set<String> preferredCourses = Arrays.asList("IntelligentAgents").stream().collect(Collectors.toSet());
		final Set<String> preferredTopics = Arrays.asList("GameTheory").stream().collect(Collectors.toSet());
		final Set<String> preferredLecturers = Arrays.asList("FlorisBex").stream().collect(Collectors.toSet());
		final Set<String> preferredDays = Arrays.asList("Wednesday").stream().collect(Collectors.toSet());

		final Set<String> dislikedCourses = new HashSet<>();
		final Set<String> dislikedTopics = new HashSet<>();
		final Set<String> dislikedLecturers = new HashSet<>();
		final Set<String> dislikedDays = new HashSet<>();

		return new Preferences(preferredCourses, preferredTopics, preferredLecturers, preferredDays, 6, 8, 5, 7,
				dislikedCourses, dislikedTopics, dislikedLecturers, dislikedDays, 1, 1, 1, 1);
	}

	public static String getStudentForScenarioPrereqInception() {
		return "StudentB";
	}

	public static Preferences getPreferencesForScenarioPrereqInception() {
		final Set<String> preferredCourses = Arrays.asList("ResearchInternshipAI").stream().collect(Collectors.toSet());
		final Set<String> preferredTopics = Arrays.asList("Logic").stream().collect(Collectors.toSet());
		final Set<String> preferredLecturers = Arrays.asList("PinarYolum").stream().collect(Collectors.toSet());
		final Set<String> preferredDays = Arrays.asList("Thursday").stream().collect(Collectors.toSet());

		final Set<String> dislikedCourses = new HashSet<>();
		final Set<String> dislikedTopics = new HashSet<>();
		final Set<String> dislikedLecturers = new HashSet<>();
		final Set<String> dislikedDays = new HashSet<>();

		return new Preferences(preferredCourses, preferredTopics, preferredLecturers, preferredDays, 10, 10, 10, 10,
				dislikedCourses, dislikedTopics, dislikedLecturers, dislikedDays, 1, 1, 1, 1);
	}

	public static String getStudentForScenarioImpossibleOntology() {
		return "StudentC";
	}

	public static Preferences getPreferencesForScenarioImpossibleOntology() {
		final Set<String> preferredCourses = new HashSet<>();
		final Set<String> preferredTopics = new HashSet<>();
		final Set<String> preferredLecturers = new HashSet<>();
		final Set<String> preferredDays = new HashSet<>();

		final Set<String> dislikedCourses = new HashSet<>();
		final Set<String> dislikedTopics = new HashSet<>();
		final Set<String> dislikedLecturers = new HashSet<>();
		final Set<String> dislikedDays = new HashSet<>();

		return new Preferences(preferredCourses, preferredTopics, preferredLecturers, preferredDays, 1, 1, 1, 1,
				dislikedCourses, dislikedTopics, dislikedLecturers, dislikedDays, 1, 1, 1, 1);
	}

	public static String getStudentForScenarioUseCase1() {
		return "StudentD";
	}

	public static Preferences getPreferencesForScenarioUseCase1() {
		final Set<String> preferredCourses = Arrays.asList("GameDesign").stream().collect(Collectors.toSet());
		final Set<String> preferredTopics = Arrays.asList("DatabaseSystems").stream().collect(Collectors.toSet());
		final Set<String> preferredLecturers = Arrays.asList("BobMarley").stream().collect(Collectors.toSet());
		final Set<String> preferredDays = Arrays.asList("Wednesday").stream().collect(Collectors.toSet());

		final Set<String> dislikedCourses = Arrays.asList("ResearchInternshipAI").stream().collect(Collectors.toSet());
		final Set<String> dislikedTopics = Arrays.asList("Logic").stream().collect(Collectors.toSet());
		final Set<String> dislikedLecturers = Arrays.asList("ParisHilton").stream().collect(Collectors.toSet());
		final Set<String> dislikedDays = Arrays.asList("Monday").stream().collect(Collectors.toSet());

		return new Preferences(preferredCourses, preferredTopics, preferredLecturers, preferredDays, 8, 6, 9, 5,
				dislikedCourses, dislikedTopics, dislikedLecturers, dislikedDays, 3, 5, 5, 7);
	}
}
