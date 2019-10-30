package org.uu.nl.ai.intelligent.agents.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CoursePlan {
	private static final int NUM_OF_COURSES_PER_PERIOD = 2;

	Map<String, Integer> utilityByCourseInPeriod1 = new HashMap<>();
	Map<String, Integer> utilityByCourseInPeriod2 = new HashMap<>();
	Map<String, Integer> utilityByCourseInPeriod3 = new HashMap<>();
	Map<String, Integer> utilityByCourseInPeriod4 = new HashMap<>();

	private final Set<String> coursesCausingBranchInPeriod1 = new HashSet<>();
	private final Set<String> coursesCausingBranchInPeriod2 = new HashSet<>();
	private final Set<String> coursesCausingBranchInPeriod3 = new HashSet<>();
	private final Set<String> coursesCausingBranchInPeriod4 = new HashSet<>();

	public CoursePlan() {
		super();
	}

	public CoursePlan(final CoursePlan coursePlan) {
		super();

		// Copy constructor
		this.utilityByCourseInPeriod1 = coursePlan.utilityByCourseInPeriod1;
		this.utilityByCourseInPeriod2 = coursePlan.utilityByCourseInPeriod2;
		this.utilityByCourseInPeriod3 = coursePlan.utilityByCourseInPeriod3;
		this.utilityByCourseInPeriod4 = coursePlan.utilityByCourseInPeriod4;

		// TODO: causesBranch include
	}

	public Set<String> getAllCourses() {
		final Set<String> allCourses = new HashSet<>();
		allCourses.addAll(this.utilityByCourseInPeriod1.keySet());
		allCourses.addAll(this.utilityByCourseInPeriod2.keySet());
		allCourses.addAll(this.utilityByCourseInPeriod3.keySet());
		allCourses.addAll(this.utilityByCourseInPeriod4.keySet());
		return Collections.unmodifiableSet(allCourses);
	}

	public Set<String> getCoursesInPeriod(final int period) {
		switch (period) {
		case 1:
			return getCoursesInPeriod1();
		case 2:
			return getCoursesInPeriod2();
		case 3:
			return getCoursesInPeriod3();
		case 4:
			return getCoursesInPeriod4();
		default:
			throw new IllegalArgumentException();
		}
	}

	public Set<String> getCoursesCausingBranchInPeriod(final int period) {
		switch (period) {
		case 1:
			return getCoursesCausingBranchInPeriod1();
		case 2:
			return getCoursesCausingBranchInPeriod2();
		case 3:
			return getCoursesCausingBranchInPeriod3();
		case 4:
			return getCoursesCausingBranchInPeriod4();
		default:
			throw new IllegalArgumentException();
		}
	}

	public static CoursePlan branchByRemovingCourse(final CoursePlan coursePlan, final String course) {
		Objects.requireNonNull(course);

		final CoursePlan branch = new CoursePlan(coursePlan);

		if (branch.utilityByCourseInPeriod1.remove(course) != null) {
			branch.coursesCausingBranchInPeriod1.add(course);
			branch.utilityByCourseInPeriod2.clear();
			branch.utilityByCourseInPeriod3.clear();
			branch.utilityByCourseInPeriod4.clear();
		} else if (branch.utilityByCourseInPeriod2.remove(course) != null) {
			branch.coursesCausingBranchInPeriod2.add(course);
			branch.utilityByCourseInPeriod3.clear();
			branch.utilityByCourseInPeriod4.clear();
		} else if (branch.utilityByCourseInPeriod3.remove(course) != null) {
			branch.coursesCausingBranchInPeriod3.add(course);
			branch.utilityByCourseInPeriod4.clear();
		} else if (branch.utilityByCourseInPeriod4.remove(course) != null) {
			branch.coursesCausingBranchInPeriod4.add(course);
		} else {
			throw new IllegalArgumentException("course not in course plan");
		}

		return branch;
	}

	public static CoursePlan branchByDemandingPrerequisites(final CoursePlan coursePlan,
			final Set<PrerequisiteDemand> prerequisiteDemands) {
		Objects.requireNonNull(prerequisiteDemands);

		final CoursePlan branch = new CoursePlan(coursePlan);

		final Set<String> prerequisites = prerequisiteDemands.stream().map(i -> i.getPrerequisite())
				.collect(Collectors.toSet());

		for (final PrerequisiteDemand prerequisiteDemand : prerequisiteDemands) {
			switch (prerequisiteDemand.getPeriod()) {
			case 1:
				switchCourseWithLowestUtilCourse(prerequisiteDemand.getPrerequisite(), prerequisiteDemand.getUtility(),
						branch.utilityByCourseInPeriod1, prerequisites);
			case 2:
				switchCourseWithLowestUtilCourse(prerequisiteDemand.getPrerequisite(), prerequisiteDemand.getUtility(),
						branch.utilityByCourseInPeriod2, prerequisites);
			case 3:
				switchCourseWithLowestUtilCourse(prerequisiteDemand.getPrerequisite(), prerequisiteDemand.getUtility(),
						branch.utilityByCourseInPeriod3, prerequisites);
			case 4:
				switchCourseWithLowestUtilCourse(prerequisiteDemand.getPrerequisite(), prerequisiteDemand.getUtility(),
						branch.utilityByCourseInPeriod4, prerequisites);
			default:
				throw new IllegalArgumentException();
			}
		}
		return branch;
	}

	private static void switchCourseWithLowestUtilCourse(final String course, final int utility,
			final Map<String, Integer> utilityByCourse, final Set<String> prerequisites) {
		int lowestUtil = Integer.MAX_VALUE;
		String lowestUtilCourse = null;
		for (final Entry<String, Integer> utilityByCourseEntry : utilityByCourse.entrySet()) {
			final String oldCourse = utilityByCourseEntry.getKey();
			if (prerequisites.contains(oldCourse)) {
				// Do not switch out previously switched out prerequisite with other
				// prerequisite
				continue;
			}
			final int oldUtility = utilityByCourseEntry.getValue();
			if (oldUtility < lowestUtil) {
				// TODO: When two courses have the same utility, we should branch again...
				lowestUtil = utility;
				lowestUtilCourse = oldCourse;
			}
		}
		utilityByCourse.remove(lowestUtilCourse);
		utilityByCourse.put(course, utility);
	}

	/**
	 * @return 1 for Period 1, 2 for Period 2, 3 for Period 3, 4 for Period 4 and -1
	 *         if the CoursePlan is finished
	 */
	public int getFirstIncompletePeriod() {
		if (isPeriod1Full()) {
			if (isPeriod2Full()) {
				if (isPeriod3Full()) {
					if (isPeriod4Full()) {
						throw new IllegalStateException();
					} else {
						return 4;
					}
				} else {
					return 3;
				}
			} else {
				return 2;
			}
		} else {
			return 1;
		}
	}

	public boolean isPeriodFull(final int period) {
		switch (period) {
		case 1:
			return isPeriod1Full();
		case 2:
			return isPeriod2Full();
		case 3:
			return isPeriod3Full();
		case 4:
			return isPeriod4Full();
		default:
			throw new IllegalArgumentException();
		}
	}

	public boolean isPeriod1Full() {
		return this.utilityByCourseInPeriod1.size() == NUM_OF_COURSES_PER_PERIOD;
	}

	public boolean isPeriod2Full() {
		return this.utilityByCourseInPeriod2.size() == NUM_OF_COURSES_PER_PERIOD;
	}

	public boolean isPeriod3Full() {
		return this.utilityByCourseInPeriod3.size() == NUM_OF_COURSES_PER_PERIOD;
	}

	public boolean isPeriod4Full() {
		return this.utilityByCourseInPeriod4.size() == NUM_OF_COURSES_PER_PERIOD;
	}

	public void addCourseInPeriod(final String course, final int period, final int utility) {
		switch (period) {
		case 1:
			addCourseInPeriod1(course, utility);
			break;
		case 2:
			addCourseInPeriod2(course, utility);
			break;
		case 3:
			addCourseInPeriod3(course, utility);
			break;
		case 4:
			addCourseInPeriod4(course, utility);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	public Set<String> getCoursesInPeriod1() {
		return Collections.unmodifiableSet(this.utilityByCourseInPeriod1.keySet());
	}

	public void addCourseInPeriod1(final String course, final int utility) {
		Objects.requireNonNull(course);

		if ((this.utilityByCourseInPeriod1.size() == NUM_OF_COURSES_PER_PERIOD) || getAllCourses().contains(course)
				|| this.coursesCausingBranchInPeriod1.contains(course)) {
			throw new IllegalStateException();
		}
		this.utilityByCourseInPeriod1.put(course, utility);
	}

	public Set<String> getCoursesCausingBranchInPeriod1() {
		return Collections.unmodifiableSet(this.coursesCausingBranchInPeriod1);
	}

	public void addCourseCausingBranchInPeriod1(final String course) {
		Objects.requireNonNull(course);
		this.coursesCausingBranchInPeriod1.add(course);
	}

	public Set<String> getCoursesInPeriod2() {
		return Collections.unmodifiableSet(this.utilityByCourseInPeriod2.keySet());
	}

	public void addCourseInPeriod2(final String course, final int utility) {
		Objects.requireNonNull(course);

		if ((this.utilityByCourseInPeriod2.size() == NUM_OF_COURSES_PER_PERIOD) || getAllCourses().contains(course)
				|| this.coursesCausingBranchInPeriod2.contains(course)) {
			throw new IllegalStateException();
		}
		this.utilityByCourseInPeriod2.put(course, utility);
	}

	public Set<String> getCoursesCausingBranchInPeriod2() {
		return Collections.unmodifiableSet(this.coursesCausingBranchInPeriod2);
	}

	public void addCourseCausingBranchInPeriod2(final String course) {
		Objects.requireNonNull(course);
		this.coursesCausingBranchInPeriod2.add(course);
	}

	public Set<String> getCoursesInPeriod3() {
		return Collections.unmodifiableSet(this.utilityByCourseInPeriod3.keySet());
	}

	public void addCourseInPeriod3(final String course, final int utility) {
		Objects.requireNonNull(course);

		if ((this.utilityByCourseInPeriod3.size() == NUM_OF_COURSES_PER_PERIOD) || getAllCourses().contains(course)
				|| this.coursesCausingBranchInPeriod3.contains(course)) {
			throw new IllegalStateException();
		}
		this.utilityByCourseInPeriod3.put(course, utility);
	}

	public Set<String> getCoursesCausingBranchInPeriod3() {
		return Collections.unmodifiableSet(this.coursesCausingBranchInPeriod3);
	}

	public void addCourseCausingBranchInPeriod3(final String course) {
		Objects.requireNonNull(course);
		this.coursesCausingBranchInPeriod3.add(course);
	}

	public Set<String> getCoursesInPeriod4() {
		return Collections.unmodifiableSet(this.utilityByCourseInPeriod4.keySet());
	}

	public void addCourseInPeriod4(final String course, final int utility) {
		Objects.requireNonNull(course);

		if ((this.utilityByCourseInPeriod4.size() == NUM_OF_COURSES_PER_PERIOD) || getAllCourses().contains(course)
				|| this.coursesCausingBranchInPeriod4.contains(course)) {
			throw new IllegalStateException();
		}
		this.utilityByCourseInPeriod4.put(course, utility);
	}

	public Set<String> getCoursesCausingBranchInPeriod4() {
		return Collections.unmodifiableSet(this.coursesCausingBranchInPeriod4);
	}

	public void addCourseCausingBranchInPeriod4(final String course) {
		Objects.requireNonNull(course);
		this.coursesCausingBranchInPeriod4.add(course);
	}

	public int getUtility() {
		return this.utilityByCourseInPeriod1.values().stream().mapToInt(Integer::valueOf).sum()
				+ this.utilityByCourseInPeriod2.values().stream().mapToInt(Integer::valueOf).sum()
				+ this.utilityByCourseInPeriod3.values().stream().mapToInt(Integer::valueOf).sum()
				+ this.utilityByCourseInPeriod4.values().stream().mapToInt(Integer::valueOf).sum();
	}

	@Override
	public String toString() {
		return "CoursePlan [utilityByCourseInPeriod1=" + this.utilityByCourseInPeriod1 + ", utilityByCourseInPeriod2="
				+ this.utilityByCourseInPeriod2 + ", utilityByCourseInPeriod3=" + this.utilityByCourseInPeriod3
				+ ", utilityByCourseInPeriod4=" + this.utilityByCourseInPeriod4 + "]";
	}

}
