package org.uu.nl.ai.intelligent.agents.data;

import java.util.Set;

public class CoursePlan {
	private static final int NUM_OF_COURSES_PER_PERIOD = 2;

	private Set<String> coursesInPeriod1;
	private Set<String> coursesInPeriod2;
	private Set<String> coursesInPeriod3;
	private Set<String> coursesInPeriod4;

	public CoursePlan() {
		super();
	}

	public Set<String> getCoursesInPeriod1() {
		return this.coursesInPeriod1;
	}

	public void addCourseInPeriod1(final String courseInPeriod1) {
		if (this.coursesInPeriod1.size() < NUM_OF_COURSES_PER_PERIOD) {
			this.coursesInPeriod1.add(courseInPeriod1);
		}
	}

	public Set<String> getCoursesInPeriod2() {
		return this.coursesInPeriod2;
	}

	public void addCourseInPeriod2(final String courseInPeriod2) {
		if (this.coursesInPeriod2.size() < NUM_OF_COURSES_PER_PERIOD) {
			this.coursesInPeriod2.add(courseInPeriod2);
		}
	}

	public Set<String> getCoursesInPeriod3() {
		return this.coursesInPeriod3;
	}

	public void addCourseInPeriod3(final String courseInPeriod3) {
		if (this.coursesInPeriod3.size() < NUM_OF_COURSES_PER_PERIOD) {
			this.coursesInPeriod3.add(courseInPeriod3);
		}
	}

	public Set<String> getCoursesInPeriod4() {
		return this.coursesInPeriod4;
	}

	public void addCourseInPeriod4(final String courseInPeriod4) {
		if (this.coursesInPeriod4.size() < NUM_OF_COURSES_PER_PERIOD) {
			this.coursesInPeriod4.add(courseInPeriod4);
		}
	}

	@Override
	public String toString() {
		return "CoursePlan [coursesInPeriod1=" + this.coursesInPeriod1 + ", coursesInPeriod2=" + this.coursesInPeriod2
				+ ", coursesInPeriod3=" + this.coursesInPeriod3 + ", coursesInPeriod4=" + this.coursesInPeriod4 + "]";
	}

}
