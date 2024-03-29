package org.uu.nl.ai.intelligent.agents;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.uu.nl.ai.intelligent.agents.data.CoursePlan;
import org.uu.nl.ai.intelligent.agents.data.Preferences;
import org.uu.nl.ai.intelligent.agents.data.PrerequisiteDemand;
import org.uu.nl.ai.intelligent.agents.data.PrerequisiteSubstitute;
import org.uu.nl.ai.intelligent.agents.query.QueryEngine;

public class Agent {
	private static final List<String> PERIOD_NAMES = Arrays.asList("CoursePerPeriod1", "CoursePerPeriod2",
			"CoursePerPeriod3", "CoursePerPeriod4");

	private final String student;
	private final Preferences preferences;

	public Agent(final String student, final Preferences preferences)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		super();
		this.student = student;
		this.preferences = preferences;
	}

	public Set<CoursePlan> getBestCoursePlans()
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		return getBestCoursePlans(new CoursePlan());
	}

	private Set<CoursePlan> getBestCoursePlans(final CoursePlan coursePlan)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {

		final Set<CoursePlan> coursePlans = new HashSet<>();

		// For each period query the available courses
		final int startPeriod = coursePlan.getFirstIncompletePeriod();
		final List<Set<String>> coursesInPeriods = getCoursesInPeriods(startPeriod);

		// Get the prerequisite for all courses
		final Map<String, Set<String>> prerequisitesByCourse = getPrerequisitesByCourse(
				coursesInPeriods.stream().flatMap(Set::stream).collect(Collectors.toSet()));

		// Get the best courses for each period
		for (int period = startPeriod; period <= PERIOD_NAMES.size(); period++) {
			if (!coursePlan.isPeriodFull(period)) {
				final Set<String> coursesInPeriod = coursesInPeriods.get(period - startPeriod);

				final SortedMap<Integer, Set<String>> coursesByUtility = getCoursesByUtility(coursesInPeriod);
				for (final Entry<Integer, Set<String>> coursesByUtilityEntry : coursesByUtility.entrySet()) {
					final int utility = coursesByUtilityEntry.getKey();
					final Set<String> courses = coursesByUtilityEntry.getValue();

					// A student cannot register for a course more than once.
					final Set<String> coursesAlreadyTaken = getCoursesAlreadyTaken();

					final Set<String> coursesNotAlreadyTaken = courses.stream()
							.filter(i -> !coursesAlreadyTaken.contains(i)).collect(Collectors.toSet());

					final Set<String> coursesPlannedForPrevPeriods = coursePlan.getAllCoursesUntilPeriod(period - 1);

					for (final String course : coursesNotAlreadyTaken) {
						// Course offered in multiple periods? Branch by removing planned course from
						// previous period
						if (coursesPlannedForPrevPeriods.contains(course)) {
							final CoursePlan branch = CoursePlan.branchByRemovingCourse(coursePlan, course);
							final Set<CoursePlan> alternativeCoursePlans = getBestCoursePlans(branch);
							coursePlans.addAll(alternativeCoursePlans);
						}

						// Check for prerequisites
						final boolean prerequisiesMet = hasTakenPrerequisites(prerequisitesByCourse.get(course),
								coursesAlreadyTaken, coursesPlannedForPrevPeriods);
						if (!prerequisiesMet) {
							// Prerequisites not met? Branch if it is feasible to take them in previous
							// periods and skip course
							final Set<PrerequisiteDemand> prerequisiteDemands = getPrerequisiteDemands(course,
									prerequisitesByCourse, startPeriod, period, coursesInPeriods, coursesAlreadyTaken,
									coursePlan);
							if (prerequisiteDemands != null) {
								// All prerequisites can be met in previous periods
								final CoursePlan branch = CoursePlan.branchByDemandingPrerequisites(coursePlan,
										prerequisiteDemands);
								final Set<CoursePlan> alternativeCoursePlans = getBestCoursePlans(branch);
								coursePlans.addAll(alternativeCoursePlans);
							}
							// Skip course since its prerequisites cannot be met with this course plan
							continue;
						}
					}

					final Set<String> validCourses = getValidCourses(coursesNotAlreadyTaken, coursesAlreadyTaken,
							coursePlan.getAllCourses(), coursesPlannedForPrevPeriods,
							coursePlan.getCoursesCausingBranchInPeriod(period), prerequisitesByCourse);

					if (validCourses.size() == 1) {
						// Only one valid course
						coursePlan.addCourseInPeriod(validCourses.iterator().next(), period, utility);
					} else if (validCourses.size() > 1) {
						// Multiple valid courses

						// When a student has an option between two courses that are equally
						// preferable, the student would like to take a course that her friend takes.

						// Assumption: The more friends take a course the more preferable it is

						// We just use one random course with the highest number of friends
						// and do not create branches for all of them
						final SortedMap<Integer, Set<String>> coursesByNumOfFriends = getCoursesByNumOfFriends(
								validCourses);
						final int highestNumOfFriends = coursesByNumOfFriends.lastKey();
						coursePlan.addCourseInPeriod(coursesByNumOfFriends.get(highestNumOfFriends).iterator().next(),
								period, utility);
					} else {
						// No valid courses with highest utility -> Next utility
						continue;
					}

					// Stop adding courses to the course plan for this period if it is already full
					if (coursePlan.isPeriodFull(period)) {
						break;
					}
				}
			}

		}

		// Add coursePlan to the collection of coursePlans to return
		coursePlans.add(coursePlan);

		return filterBestCoursePlans(coursePlans);

	}

	private boolean hasTakenPrerequisites(final Set<String> coursePrerequisites,
			final Set<String> coursesTakenByStudent, final Set<String> coursesPlannedForPrevPeriods)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {

		final Set<String> coursesTakenOrPlanned = new HashSet<>();
		coursesTakenOrPlanned.addAll(coursesTakenByStudent);
		coursesTakenOrPlanned.addAll(coursesPlannedForPrevPeriods);

		for (final String coursePrerequisite : coursePrerequisites) {
			if (!coursesTakenOrPlanned.contains(coursePrerequisite)) {
				// If the student took courses similar to a prerequisite that is also acceptable
				int similarity = 0;
				for (final String course : coursesTakenOrPlanned) {
					similarity = getSimilarity(coursePrerequisite, course);
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

	/**
	 * @param coursePlan
	 * @param coursesAlreadyTaken
	 * @return Set of PrerequisiteDemands or null if a prerequisite cannot be met in
	 *         any preveious period - similar courses are also considered!
	 */
	private Set<PrerequisiteDemand> getPrerequisiteDemands(final String course,
			final Map<String, Set<String>> prerequisitesByCourse, final int startPeriod, final int period,
			final List<Set<String>> coursesInPeriods, final Set<String> coursesAlreadyTaken,
			final CoursePlan coursePlan)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {

		final Set<String> coursePrerequisites = prerequisitesByCourse.get(course);

		final Set<PrerequisiteDemand> prerequisiteDemands = new HashSet<>();
		for (final String prerequisite : coursePrerequisites) {
			final PrerequisiteSubstitute prerequisiteSubstitute = checkForPrereqOrSubstInPrevPeriod(prerequisite,
					prerequisitesByCourse, startPeriod, period, coursesInPeriods, coursesAlreadyTaken, coursePlan);
			if (prerequisiteSubstitute != null) {
				final int prereqUtility = calculateUtility(prerequisite);
				final PrerequisiteDemand prerequisiteDemand = new PrerequisiteDemand(prerequisiteSubstitute.getCourse(),
						prereqUtility, prerequisiteSubstitute.getPeriod());
				prerequisiteDemands.add(prerequisiteDemand);
			} else {
				// Not all prerequisites can be met in previous periods
				return null;
			}
		}
		return prerequisiteDemands;
	}

	private PrerequisiteSubstitute checkForPrereqOrSubstInPrevPeriod(final String prerequisite,
			final Map<String, Set<String>> prerequisitesByCourse, final int startPeriod, final int currentPeriod,
			final List<Set<String>> coursesInPeriods, final Set<String> coursesAlreadyTaken,
			final CoursePlan coursePlan)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {

		// We will not return any possible period but just the first occurence of a
		// prerequisite
		for (int period = startPeriod; period < currentPeriod; period++) {
			final Set<String> coursesInPeriod = coursesInPeriods.get(period - startPeriod);
			// Check if prerequisite is offered in period
			final Set<String> coursesPlannedForPrevPeriods = coursePlan.getAllCoursesUntilPeriod(period - 1);
			if (!coursesAlreadyTaken.contains(prerequisite) && !coursesPlannedForPrevPeriods.contains(prerequisite)
					&& hasTakenPrerequisites(prerequisitesByCourse.get(prerequisite), coursesAlreadyTaken,
							coursesPlannedForPrevPeriods)
					&& coursesInPeriod.contains(prerequisite)) {
				return new PrerequisiteSubstitute(period, prerequisite);
			}

			// Check for similar courses alternatively
			for (final String courseInPeriod : coursesInPeriod) {
				if (!coursesAlreadyTaken.contains(courseInPeriod)
						&& !coursesPlannedForPrevPeriods.contains(courseInPeriod)
						&& hasTakenPrerequisites(prerequisitesByCourse.get(courseInPeriod), coursesAlreadyTaken,
								coursesPlannedForPrevPeriods)) {
					final int similarity = getSimilarity(courseInPeriod, prerequisite);
					if (similarity > 0) {
						return new PrerequisiteSubstitute(period, courseInPeriod);
					}
				}
			}
		}
		return null;
	}

	private List<Set<String>> getCoursesInPeriods(final int startPeriod)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		final List<Set<String>> coursesInPeriods = new ArrayList<>();
		for (int period = startPeriod; period <= PERIOD_NAMES.size(); period++) {
			final String periodName = PERIOD_NAMES.get(period - 1);
			final Set<String> coursesInPeriod = QueryEngine.getInstance().getInstancesShortForm(periodName, false);
			coursesInPeriods.add(coursesInPeriod);
		}
		return coursesInPeriods;
	}

	private SortedMap<Integer, Set<String>> getCoursesByUtility(final Set<String> courses)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		final SortedMap<Integer, Set<String>> coursesByUtility = new TreeMap<>(Collections.reverseOrder());
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
		courseTopics.addAll(findParentTopics(courseTopics));

		if (preferredTopics.stream().anyMatch(i -> courseTopics.contains(i))) {
			utility += this.preferences.getPreferredTopicsWeight();
		}
		if (dislikedTopics.stream().anyMatch(i -> courseTopics.contains(i))) {
			utility -= this.preferences.getDislikedTopicsWeight();
		}

		final Set<String> courseLecturers = queryEngine.getInstancesShortForm("teaches value " + course, false);

		if (preferredLecturers.stream().anyMatch(i -> courseLecturers.contains(i))) {
			utility += this.preferences.getPreferredLecturersWeight();
		}
		if (dislikedLecturers.stream().anyMatch(i -> courseLecturers.contains(i))) {
			utility -= this.preferences.getDislikedLecturersWeight();
		}

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

	private Set<String> getCoursesAlreadyTaken()
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		final Set<String> coursesAlreadyTaken = QueryEngine.getInstance()
				.getInstancesShortForm("hasBeenTakenBy value " + this.student, false);

		return coursesAlreadyTaken;
	}

	private Map<String, Set<String>> getPrerequisitesByCourse(final Set<String> courses)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		final Map<String, Set<String>> prerequisitesByCourse = new HashMap<>();
		for (final String course : courses) {
			final Set<String> coursePrerequisites = QueryEngine.getInstance()
					.getInstancesShortForm("isPrerequisite value " + course, false);
			prerequisitesByCourse.put(course, coursePrerequisites);
		}
		return prerequisitesByCourse;
	}

	private Set<String> getValidCourses(final Set<String> coursesNotAlreadyTaken, final Set<String> coursesAlreadyTaken,
			final Set<String> coursesAlreadyPlanned, final Set<String> coursesPlannedForPrevPeriods,
			final Set<String> coursesCausingBranch, final Map<String, Set<String>> prerequisitesByCourse)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {

		final Set<String> validCourses = new HashSet<>();
		for (final String course : coursesNotAlreadyTaken) {
			if (!coursesAlreadyPlanned.contains(course) // Student can only take a course once
					&& !coursesCausingBranch.contains(course) && hasTakenPrerequisites(
							prerequisitesByCourse.get(course), coursesAlreadyTaken, coursesPlannedForPrevPeriods)) {
				validCourses.add(course);
			}
		}
		return validCourses;
	}

	private SortedMap<Integer, Set<String>> getCoursesByNumOfFriends(final Set<String> courses)
			throws UnsupportedEncodingException, OWLOntologyCreationException, IOException {
		final SortedMap<Integer, Set<String>> coursesByNumOfFriends = new TreeMap<>();

		for (final String course : courses) {
			final int numOfFriends = getNumOfFriendsTakingCourse(course);
			Set<String> coursesForNumOfFriends;
			if (coursesByNumOfFriends.containsKey(numOfFriends)) {
				coursesForNumOfFriends = coursesByNumOfFriends.get(numOfFriends);
			} else {
				coursesForNumOfFriends = new HashSet<>();
			}
			coursesForNumOfFriends.add(course);
			coursesByNumOfFriends.put(numOfFriends, coursesForNumOfFriends);
		}

		return coursesByNumOfFriends;
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

	private Set<CoursePlan> filterBestCoursePlans(final Set<CoursePlan> coursePlans) {
		int bestUtility = Integer.MIN_VALUE;
		for (final CoursePlan coursePlan : coursePlans) {
			bestUtility = coursePlan.getUtility() > bestUtility ? coursePlan.getUtility() : bestUtility;
		}

		final Set<CoursePlan> bestCoursePlans = new HashSet<>();

		for (final CoursePlan coursePlan : coursePlans) {
			if (coursePlan.getUtility() == bestUtility) {
				bestCoursePlans.add(coursePlan);
			}
		}

		return bestCoursePlans;
	}

}
