package org.uu.nl.ai.intelligent.agents;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.uu.nl.ai.intelligent.agents.data.Preferences;

public class TestPreferenceProvider {
	public static Preferences getPreferencesForPrerequisiteBranching() {
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
}
