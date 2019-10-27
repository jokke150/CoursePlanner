package org.uu.nl.ai.intelligent.agents;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.uu.nl.ai.intelligent.agents.data.CoursePlan;
import org.uu.nl.ai.intelligent.agents.data.Preferences;
import org.uu.nl.ai.intelligent.agents.query.QueryEngine;

public class Agent {
	private static final List<String> PERIODS = Arrays.asList("CoursePerPeriod1", "CoursePerPeriod2",
			"CoursePerPeriod2", "CoursePerPeriod2");

	private final Preferences preferences;

	public Agent(final Preferences preferences) {
		super();
		this.preferences = preferences;
	}

	public List<CoursePlan> getBestCoursePlans()
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		return getBestCoursePlans(new CoursePlan());
	}

	private List<CoursePlan> getBestCoursePlans(final CoursePlan coursePlan)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {

		final List<CoursePlan> coursePlans = new ArrayList<>();

		for (int period = coursePlan.getFirstIncompletePeriod(); period <= PERIODS.size(); period++) {
			// 1. For each period query the available courses
			final Set<String> coursesInPeriod = QueryEngine.getInstance().getInstancesShortForm(PERIODS.get(period),
					false);
			final SortedMap<Integer, Set<String>> coursesByUtility = getCoursesByUtility(coursesInPeriod);
			for (final Entry<Integer, Set<String>> coursesByUtilityEntry : coursesByUtility.entrySet()) {
				final int utility = coursesByUtilityEntry.getKey();
				// TODO: Check for prerequisites differently and branch if it is feasible to
				// take one in a previous period
				final Set<String> validCoursesForUtility = coursesByUtilityEntry.getValue().stream()
						.filter(i -> arePreconditionsFulfilled(i)).collect(Collectors.toSet());

				for (final String course : validCoursesForUtility) {

					if (validCoursesForUtility.size() > 1) {
						// When a student has an option between two courses that are equally
						// preferable, the student would like to take a course that her friend takes.

						// Assumption: The more friends take a course the more preferable it is
						// TODO: Correct? If yes, include in report!

						final int numOfFriendsTakingCourse = getNumOfFriendsTakingCourse(course);

						// TODO: branch if same utility and same numOfFriends
						// bestCoursePlans.addAll(getBestCoursePlans(coursePlan));
					}

					coursePlan.addCourseInPeriod(course, period, utility);
				}
			}
		}

		// 2. Calculate utility taking into account preference and previous periods
		// - have a class for schedule which contains a class for period which contains
		// 2 courses which will have name lecturer blabla from the knowledge base
		// and the calculated utility based on query???
		// - populate the schedule period by period, find a clash, start a new branch
		// where sth is changed that clash (prerequisites needed for highest utility
		// course)
		// - compare determined utilities
		// 3. Write max function

		return coursePlans;

	}

	private SortedMap<Integer, Set<String>> getCoursesByUtility(final Set<String> courses)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		final SortedMap<Integer, Set<String>> coursesByUtility = new TreeMap<>();
		for (final String course : courses) {
			final int utility = calculateUtility(course);
			Set<String> coursesForUtility;
			if (coursesByUtility.containsKey(utility)) {
				coursesForUtility = coursesByUtility.get(utility);
			} else {
				coursesForUtility = new HashSet<String>();
			}
			coursesForUtility.add(course);
			coursesByUtility.put(utility, coursesForUtility);
		}
		return coursesByUtility;
	}

	private int calculateUtility(final String course)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		final QueryEngine queryEngine = QueryEngine.getInstance();

		final Set<String> preferredCourses = this.preferences.getPreferredCourses();
		final Set<String> friends = this.preferences.getFriends();
		final Set<String> preferredTopics = this.preferences.getPreferredTopics();
		final Set<String> preferredLecturers = this.preferences.getPreferredLecturers();
		final Set<String> preferredDays = this.preferences.getPreferredDays();

		int utility = 0;

		if (preferredCourses.contains(course)) {
			utility += this.preferences.getCoursesWeight();
		}

		final Set<String> courseTopics = queryEngine.getInstancesShortForm("taughtIn value " + course, false);
		// Assumption: If a student prefers a parent topic he also prefers all subtopics
		// TODO: Correct? If yes, include in report!
		courseTopics.addAll(findParentTopics(courseTopics));

		// TODO: Is the utility the same if the student prefers only one versus multiple
		// of the topics taught in the course?
		if (preferredTopics.stream().anyMatch(i -> courseTopics.contains(i))) {
			utility += this.preferences.getTopicsWeight();
		}

		// TODO: Is the utility the same if the student prefers only one versus multiple
		// of the lecturers teaching the course?
		final Set<String> courseLecturers = queryEngine.getInstancesShortForm("teaches value " + course, false);
		if (preferredLecturers.stream().anyMatch(i -> courseLecturers.contains(i))) {
			utility += this.preferences.getLecturersWeight();
		}

		// TODO: Is the utility the same if the student prefers only one versus multiple
		// of the days the course is taught on?
		final Set<String> courseDays = queryEngine.getInstancesShortForm("comprisesCourse value " + course, false);
		if (preferredDays.stream().anyMatch(i -> courseDays.contains(i))) {
			utility += this.preferences.getDaysWeight();
		}

		return utility;
	}

	private Set<String> findParentTopics(final Set<String> topics)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		final Set<String> allParentTopics = new HashSet<String>();

		for (final String topic : topics) {
			final Set<String> topicParentTopics = QueryEngine.getInstance()
					.getInstancesShortForm("isParentTopicOf value " + topic, false);
			allParentTopics.addAll(topicParentTopics);
			allParentTopics.addAll(findParentTopics(topicParentTopics));
		}

		return allParentTopics;
	}

	private int getNumOfFriendsTakingCourse(final String course)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		final Set<String> personsTakingCourse = QueryEngine.getInstance().getInstancesShortForm("takes value " + course,
				false);
		final Set<String> friends = this.preferences.getFriends();

		final Set<String> friendsTakingCourse = new HashSet<String>(personsTakingCourse);
		friendsTakingCourse.retainAll(friends);

		return friendsTakingCourse.size();
	}

	private boolean arePreconditionsFulfilled(final String course) {
		// A student cannot register for a course more than once. A student can take a
		// course only if she has taken the prerequisite (if there is one) or a course
		// similar to a prerequisite.

		return false;

	}

}
