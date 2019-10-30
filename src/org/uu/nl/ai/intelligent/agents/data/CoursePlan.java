package org.uu.nl.ai.intelligent.agents.data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CoursePlan {
	private static final int NUM_OF_COURSES_PER_PERIOD = 2;

	private Set<String> coursesInPeriod1;
	private Set<String> coursesCausingBranchInPeriod1;
	private Set<String> coursesInPeriod2;
	private Set<String> coursesCausingBranchInPeriod2;
	private Set<String> coursesInPeriod3;
	private Set<String> coursesCausingBranchInPeriod3;
	private Set<String> coursesInPeriod4;
	private Set<String> coursesCausingBranchInPeriod4;

	private int utility = 0;

	public CoursePlan() {
		super();
	}

	public Set<String> getAllCourses() {
		final Set<String> allCourses = new HashSet<>();
		allCourses.addAll(this.coursesInPeriod1);
		allCourses.addAll(this.coursesInPeriod2);
		allCourses.addAll(this.coursesInPeriod3);
		allCourses.addAll(this.coursesInPeriod4);
		return allCourses;
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
		return this.coursesInPeriod1;
	}

	public void addCourseInPeriod1(final String course, final int utility) {
		Objects.requireNonNull(course);

		if ((this.coursesInPeriod1.size() == NUM_OF_COURSES_PER_PERIOD) || getAllCourses().contains(course)
				|| this.coursesCausingBranchInPeriod1.contains(course)) {
			throw new IllegalStateException();
		}
		this.coursesInPeriod1.add(course);
		this.utility += utility;
	}

	public Set<String> getCoursesCausingBranchInPeriod1() {
		return this.coursesCausingBranchInPeriod1;
	}

	public void addCourseCausingBranchInPeriod1(final String course) {
		Objects.requireNonNull(course);
		this.coursesCausingBranchInPeriod1.add(course);
	}

	public Set<String> getCoursesInPeriod2() {
		return this.coursesInPeriod2;
	}

	public void addCourseInPeriod2(final String course, final int utility) {
		Objects.requireNonNull(course);

		if ((this.coursesInPeriod1.size() != NUM_OF_COURSES_PER_PERIOD)
				|| (this.coursesInPeriod2.size() == NUM_OF_COURSES_PER_PERIOD) || getAllCourses().contains(course)
				|| this.coursesCausingBranchInPeriod1.contains(course)) {
			throw new IllegalStateException();
		}
		this.coursesInPeriod2.add(course);
		this.utility += utility;
	}

	public Set<String> getCoursesCausingBranchInPeriod2() {
		return this.coursesCausingBranchInPeriod2;
	}

	public void addCourseCausingBranchInPeriod2(final String course) {
		Objects.requireNonNull(course);
		this.coursesCausingBranchInPeriod2.add(course);
	}

	public Set<String> getCoursesInPeriod3() {
		return this.coursesInPeriod3;
	}

	public void addCourseInPeriod3(final String course, final int utility) {
		Objects.requireNonNull(course);

		if ((this.coursesInPeriod1.size() != NUM_OF_COURSES_PER_PERIOD)
				|| (this.coursesInPeriod2.size() != NUM_OF_COURSES_PER_PERIOD)
				|| (this.coursesInPeriod3.size() == NUM_OF_COURSES_PER_PERIOD) || getAllCourses().contains(course)
				|| this.coursesCausingBranchInPeriod1.contains(course)) {
			throw new IllegalStateException();
		}
		this.coursesInPeriod3.add(course);
		this.utility += utility;
	}

	public Set<String> getCoursesCausingBranchInPeriod3() {
		return this.coursesCausingBranchInPeriod3;
	}

	public void addCourseCausingBranchInPeriod3(final String course) {
		Objects.requireNonNull(course);
		this.coursesCausingBranchInPeriod3.add(course);
	}

	public Set<String> getCoursesInPeriod4() {
		return this.coursesInPeriod4;
	}

	public void addCourseInPeriod4(final String course, final int utility) {
		Objects.requireNonNull(course);

		if ((this.coursesInPeriod1.size() != NUM_OF_COURSES_PER_PERIOD)
				|| (this.coursesInPeriod2.size() != NUM_OF_COURSES_PER_PERIOD)
				|| (this.coursesInPeriod3.size() != NUM_OF_COURSES_PER_PERIOD)
				|| (this.coursesInPeriod4.size() == NUM_OF_COURSES_PER_PERIOD) || getAllCourses().contains(course)
				|| this.coursesCausingBranchInPeriod1.contains(course)) {
			throw new IllegalStateException();
		}
		this.coursesInPeriod4.add(course);
		this.utility += utility;
	}

	public Set<String> getCoursesCausingBranchInPeriod4() {
		return this.coursesCausingBranchInPeriod4;
	}

	public void addCourseCausingBranchInPeriod4(final String course) {
		Objects.requireNonNull(course);
		this.coursesCausingBranchInPeriod4.add(course);
	}

	public int getUtility() {
		return this.utility;
	}

	/**
	 * @return 1 for Period 1, 2 for Period 2, 3 for Period 3, 4 for Period 4 and -1
	 *         if the CoursePlan is finished
	 */
	public int getFirstIncompletePeriod() {
		if (this.coursesInPeriod1.size() == NUM_OF_COURSES_PER_PERIOD) {
			if (this.coursesInPeriod2.size() == NUM_OF_COURSES_PER_PERIOD) {
				if (this.coursesInPeriod3.size() == NUM_OF_COURSES_PER_PERIOD) {
					if (this.coursesInPeriod4.size() == NUM_OF_COURSES_PER_PERIOD) {
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

	@Override
	public String toString() {
		return "CoursePlan [coursesInPeriod1=" + this.coursesInPeriod1 + ", coursesInPeriod2=" + this.coursesInPeriod2
				+ ", coursesInPeriod3=" + this.coursesInPeriod3 + ", coursesInPeriod4=" + this.coursesInPeriod4 + "]";
	}

}
