package org.uu.nl.ai.intelligent.agents;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
		final int util = 0;

		// 1. For each period query the available courses
		for (final String period : PERIODS) {
			final Set<String> coursesInPeriod = QueryEngine.getInstance().getInstancesShortForm(period, false);

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

		return null;
	}

	private int calculateUtility(final String course) {
		// TODO
		return 0;
	}
}
