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

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.uu.nl.ai.intelligent.agents.data.CoursePlan;
import org.uu.nl.ai.intelligent.agents.data.Preferences;
import org.uu.nl.ai.intelligent.agents.query.QueryEngine;

public class Agent {
	private static final List<String> PERIODS = Arrays.asList("CoursePerPeriod1", "CoursePerPeriod2",
			"CoursePerPeriod2", "CoursePerPeriod2");

	private final String student;
	private final Preferences preferences;

	public Agent(final String studentId, final Preferences preferences)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		super();
		this.student = QueryEngine.getInstance().getInstancesShortForm("student_ID value " + studentId, false).stream()
				.findFirst().get();
		this.preferences = preferences;
	}

	public List<CoursePlan> getBestCoursePlans()
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		return getBestCoursePlans(new CoursePlan());
	}

	private List<CoursePlan> getBestCoursePlans(final CoursePlan coursePlan)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {

		// TODO: Add obligations to follow (e.g., each student must take Methods of AI
		// Research course) and infer them by study subject for example

		// TODO: Branch
		// 1. Prerequisite could be taken in previous period
		// 2. Two courses have the same utility and the same number of friends taking
		// them
		// 3. A course is offered in multiple different periods, create a course plan
		// for all scenarios

		final List<CoursePlan> coursePlans = new ArrayList<>();

		for (int period = coursePlan.getFirstIncompletePeriod(); period <= PERIODS.size(); period++) {
			// 1. For each period query the available courses
			final Set<String> coursesInPeriod = QueryEngine.getInstance().getInstancesShortForm(PERIODS.get(period),
					false);

			// TODO: A student cannot register for a course more than once.

			final SortedMap<Integer, Set<String>> coursesByUtility = getCoursesByUtility(coursesInPeriod);
			for (final Entry<Integer, Set<String>> coursesByUtilityEntry : coursesByUtility.entrySet()) {
				final int utility = coursesByUtilityEntry.getKey();
				// TODO: Check for prerequisites differently and branch if it is feasible to
				// take one in a previous period

				final Set<String> validCoursesForUtility = getValidCoursesForUtility(coursesByUtilityEntry.getValue(),
						coursePlan);

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
		final Set<String> preferredTopics = this.preferences.getPreferredTopics();
		final Set<String> preferredLecturers = this.preferences.getPreferredLecturers();
		final Set<String> preferredDays = this.preferences.getPreferredDays();

		final Set<String> dislikedCourses = this.preferences.getDislikedCourses();
		final Set<String> dislikedTopics = this.preferences.getDislikedTopics();
		final Set<String> dislikedLecturers = this.preferences.getDislikedLecturers();
		final Set<String> dislikedDays = this.preferences.getDislikedDays();

		int utility = 0;

		if (preferredCourses.contains(course)) {
			utility += this.preferences.getPreferredCoursesWeight();
		} else if (dislikedCourses.contains(course)) {
			utility -= this.preferences.getDislikedCoursesWeight();
		}

		final Set<String> courseTopics = queryEngine.getInstancesShortForm("taughtIn value " + course, false);
		// Assumption: If a student prefers/dislikes a parent topic he also
		// prefers/dislikes all subtopics
		// TODO: Correct? If yes, include in report!
		courseTopics.addAll(findParentTopics(courseTopics));

		// TODO: Is the utility the same if the student prefers/dislikes only one versus
		// multiple of the topics taught in the course?
		if (preferredTopics.stream().anyMatch(i -> courseTopics.contains(i))) {
			utility += this.preferences.getPreferredTopicsWeight();
		}
		if (dislikedTopics.stream().anyMatch(i -> courseTopics.contains(i))) {
			utility -= this.preferences.getDislikedTopicsWeight();
		}

		// TODO: Is the utility the same if the student prefers/dislikes only one versus
		// multiple of the lecturers teaching the course?
		final Set<String> courseLecturers = queryEngine.getInstancesShortForm("teaches value " + course, false);
		if (preferredLecturers.stream().anyMatch(i -> courseLecturers.contains(i))) {
			utility += this.preferences.getPreferredLecturersWeight();
		}
		if (dislikedLecturers.stream().anyMatch(i -> courseLecturers.contains(i))) {
			utility -= this.preferences.getDislikedLecturersWeight();
		}

		// TODO: Is the utility the same if the student prefers/dislikes only one versus
		// multiple of the days the course is taught on?
		final Set<String> courseDays = queryEngine.getInstancesShortForm("comprisesCourse value " + course, false);
		if (preferredDays.stream().anyMatch(i -> courseDays.contains(i))) {
			utility += this.preferences.getPreferredDaysWeight();
		}
		if (dislikedDays.stream().anyMatch(i -> courseDays.contains(i))) {
			utility -= this.preferences.getDislikedDaysWeight();
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

	private Set<String> getValidCoursesForUtility(final Set<String> courses, final CoursePlan coursePlan)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		final Set<String> coursesTakenByStudent = QueryEngine.getInstance()
				.getInstancesShortForm("hasBeenTakenBy value + " + this.student, false);
		coursesTakenByStudent.addAll(coursePlan.getAllCourses());

		final Set<String> validCoursesForUtility = new HashSet<>();
		for (final String course : courses) {
			if (!coursesTakenByStudent.contains(course) && hasTakenPrerequisites(coursesTakenByStudent, course)) {
				validCoursesForUtility.add(course);
			}
		}
		return validCoursesForUtility;
	}

	private int getNumOfFriendsTakingCourse(final String course)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {

		final Set<String> personsTakingCourse = QueryEngine.getInstance().getInstancesShortForm("takes value " + course,
				false);

		final Set<String> friends = QueryEngine.getInstance().getInstancesShortForm("hasFriend value " + this.student,
				false);

		final Set<String> friendsTakingCourse = new HashSet<String>(personsTakingCourse);
		friendsTakingCourse.retainAll(friends);

		return friendsTakingCourse.size();
	}

	private boolean hasTakenPrerequisites(final Set<String> coursesTakenByStudent, final String course)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		final Set<String> coursePrerequisites = QueryEngine.getInstance()
				.getInstancesShortForm("hasPrerequisite value " + course, false);

		// If the student took courses similar to a prerequisite that it also acceptable
		for (final String coursePrerequisite : coursePrerequisites) {
			if (!coursesTakenByStudent.contains(coursePrerequisite)) {
				int similarity = 0;
				for (final String courseTakenByStudent : coursesTakenByStudent) {
					similarity = getSimilarity(coursePrerequisite, courseTakenByStudent);
					if (similarity > 0) {
						break;
					}
				}
				if (similarity == 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * A course is considered similar to another course if there is an overlap on
	 * topics and the same research methodology is used. When more topics overlap,
	 * the similarity is higher.
	 *
	 * @return 0 if not similar, + 1 for every overlapping topic
	 */
	private int getSimilarity(final String course1, final String course2)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		//

		final QueryEngine queryEngine = QueryEngine.getInstance();

		final String researchMethod1 = queryEngine.getInstancesShortForm("isUsedIn value " + course1, false).iterator()
				.next();
		final String researchMethod2 = queryEngine.getInstancesShortForm("isUsedIn value " + course2, false).iterator()
				.next();

		if (!researchMethod1.equals(researchMethod2)) {
			return 0;
		}

		final Set<String> topics1 = queryEngine.getInstancesShortForm("taughtIn value " + course1, false);
		final Set<String> topics2 = queryEngine.getInstancesShortForm("taughtIn value " + course2, false);

		return (int) topics1.stream().filter(i -> topics2.contains(i)).count();
	}

}
