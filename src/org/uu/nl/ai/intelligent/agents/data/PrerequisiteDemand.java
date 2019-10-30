package org.uu.nl.ai.intelligent.agents.data;

public class PrerequisiteDemand {
	private final String prerequisite;
	private final int utility;
	private final int period;

	public PrerequisiteDemand(final String prerequisite, final int utility, final int period) {
		super();
		this.prerequisite = prerequisite;
		this.utility = utility;
		this.period = period;
	}

	public String getPrerequisite() {
		return this.prerequisite;
	}

	public int getUtility() {
		return this.utility;
	}

	public int getPeriod() {
		return this.period;
	}

	@Override
	public String toString() {
		return "PrerequisiteDemand [prerequisite=" + this.prerequisite + ", utility=" + this.utility + ", period="
				+ this.period + "]";
	}

}
