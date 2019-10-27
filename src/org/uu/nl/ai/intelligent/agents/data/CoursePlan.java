package org.uu.nl.ai.intelligent.agents.data;

import java.util.Objects;
import java.util.Set;

public class CoursePlan {
	private static final int NUM_OF_COURSES_PER_PERIOD = 2;

	private Set<String> coursesInPeriod1;
	private Set<String> coursesInPeriod2;
	private Set<String> coursesInPeriod3;
	private Set<String> coursesInPeriod4;

	private int utility = 0;

	public CoursePlan() {
		super();
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
			addCourseInPeriod2(course, utility);
			break;
		case 2:
			addCourseInPeriod2(course, utility);
			break;
		case 3:
			addCourseInPeriod2(course, utility);
			break;
		case 4:
			addCourseInPeriod2(course, utility);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	public Set<String> getCoursesInPeriod1() {
		return this.coursesInPeriod1;
	}

	public void addCourseInPeriod1(final String courseInPeriod1, final int utility) {
		Objects.requireNonNull(courseInPeriod1);

		if (this.coursesInPeriod1.size() == NUM_OF_COURSES_PER_PERIOD) {
			throw new IllegalStateException();
		}
		this.coursesInPeriod4.add(courseInPeriod1);
		this.utility += utility;
	}

	public Set<String> getCoursesInPeriod2() {
		return this.coursesInPeriod2;
	}

	public void addCourseInPeriod2(final String courseInPeriod2, final int utility) {
		Objects.requireNonNull(courseInPeriod2);

		if ((this.coursesInPeriod1.size() != NUM_OF_COURSES_PER_PERIOD)
				|| (this.coursesInPeriod2.size() == NUM_OF_COURSES_PER_PERIOD)) {
			throw new IllegalStateException();
		}
		this.coursesInPeriod4.add(courseInPeriod2);
		this.utility += utility;
	}

	public Set<String> getCoursesInPeriod3() {
		return this.coursesInPeriod3;
	}

	public void addCourseInPeriod3(final String courseInPeriod3, final int utility) {
		Objects.requireNonNull(courseInPeriod3);

		if ((this.coursesInPeriod1.size() != NUM_OF_COURSES_PER_PERIOD)
				|| (this.coursesInPeriod2.size() != NUM_OF_COURSES_PER_PERIOD)
				|| (this.coursesInPeriod3.size() == NUM_OF_COURSES_PER_PERIOD)) {
			throw new IllegalStateException();
		}
		this.coursesInPeriod4.add(courseInPeriod3);
		this.utility += utility;
	}

	public Set<String> getCoursesInPeriod4() {
		return this.coursesInPeriod4;
	}

	public void addCourseInPeriod4(final String courseInPeriod4, final int utility) {
		Objects.requireNonNull(courseInPeriod4);

		if ((this.coursesInPeriod1.size() != NUM_OF_COURSES_PER_PERIOD)
				|| (this.coursesInPeriod2.size() != NUM_OF_COURSES_PER_PERIOD)
				|| (this.coursesInPeriod3.size() != NUM_OF_COURSES_PER_PERIOD)
				|| (this.coursesInPeriod4.size() == NUM_OF_COURSES_PER_PERIOD)) {
			throw new IllegalStateException();
		}
		this.coursesInPeriod4.add(courseInPeriod4);
		this.utility += utility;
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
