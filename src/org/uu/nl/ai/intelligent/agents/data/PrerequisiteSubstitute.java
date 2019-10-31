package org.uu.nl.ai.intelligent.agents.data;

public class PrerequisiteSubstitute {
	private final int period;
	private final String course;

	public PrerequisiteSubstitute(final int period, final String course) {
		super();
		this.period = period;
		this.course = course;
	}

	public int getPeriod() {
		return this.period;
	}

	public String getCourse() {
		return this.course;
	}

}
