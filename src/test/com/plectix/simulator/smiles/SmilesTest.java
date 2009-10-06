package com.plectix.simulator.smiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.ConnectedComponent;
import com.plectix.simulator.component.Site;
import com.plectix.simulator.component.string.ConnectedComponentToSmilesString;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.util.DecimalFormatter;
import com.plectix.simulator.util.PlxLogger;
import com.plectix.simulator.util.PlxTimer;

public class SmilesTest {

	private static final PlxLogger LOGGER = ThreadLocalData
			.getLogger(SmilesTest.class);

	private static final int MAXIMUM_NUMBER_OF_SHUFFLES = 1000;

	private ConnectedComponent ccomponent;
	private String uniqueKappaString;
	private ConnectedComponentToSmilesString connectedComponentToSmilesString;

	public SmilesTest(ConnectedComponent cc) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("New ConnectedComponent with " + cc.getAgents().size()
					+ " agents");
		}
		ccomponent = cc;
	}

	public String test() {
		String message = "";
		int size = ccomponent.getAgents().size();
		connectedComponentToSmilesString = ConnectedComponentToSmilesString
				.getInstance();
		uniqueKappaString = connectedComponentToSmilesString
				.toUniqueString(ccomponent);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("uniqueKappaString " + uniqueKappaString);
		}
		message = testAgents(size).toString() + testLinkIndexes();
		return message;

	}

	private StringBuffer testAgents(int size) {
		List<Agent> list = copy(ccomponent.getAgents());
		StringBuffer fails = new StringBuffer();
		if (size == 2) {
			list = reverse(list);
			checkit(list, fails);

		} else if (size > 2) {
			int numberOfTrials = Math.min(size * size,
					MAXIMUM_NUMBER_OF_SHUFFLES);
			PlxTimer plxTimer = new PlxTimer();
			plxTimer.startTimer();
			for (int i = 0; i < numberOfTrials; i++) {
				Collections.shuffle(list);
				if (!checkit(list, fails))
					break;
			}

			plxTimer.stopTimer();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Timing: CC has "
						+ size
						+ " agents -> "
						+ numberOfTrials
						+ " iterations took "
						+ DecimalFormatter
								.toStringWithSetNumberOfFractionDigits(
										1000.0 * plxTimer
												.getThreadTimeInSeconds(), 6)
						+ " msecs -> "
						+ DecimalFormatter
								.toStringWithSetNumberOfFractionDigits(1000.0
										* plxTimer.getThreadTimeInSeconds()
										/ numberOfTrials, 6)
						+ " msecs/string -> "
						+ DecimalFormatter
								.toStringWithSetNumberOfFractionDigits(1000.0
										* plxTimer.getThreadTimeInSeconds()
										/ numberOfTrials / size, 6)
						+ " msecs/string/agent ");
			}
		}
		return fails;
	}

	private boolean checkit(List<Agent> list, StringBuffer fails) {
		String smilesString = connectedComponentToSmilesString
				.toUniqueString(new ConnectedComponent(list));

		if (!smilesString.equals(uniqueKappaString)) {
			String message = "\ntestAgents:\nexpected\t" + uniqueKappaString
					+ ",\nbut\t\t" + smilesString + "\n";
			fails.append(message);
			return false;
		}

		return true;
	}

	private String testLinkIndexes() {
		StringBuffer fails = new StringBuffer();
		LinkedHashMap<Integer, Integer> links = new LinkedHashMap<Integer, Integer>();
		Random rnd = new Random();
		int r;
		for (Agent agent : ccomponent.getAgents()) {
			for (Site site : agent.getSites()) {
				int index = site.getLinkIndex();
				if (index != -1) {
					if (!links.containsKey(index)) {
						r = rnd.nextInt(100);
						links.put(index, r);
					} else {
						r = links.get(index);
					}
					site.setLinkIndex(r);
				}
			}
		}
		String smilesString = connectedComponentToSmilesString
				.toUniqueString(ccomponent);

		if (!smilesString.equals(uniqueKappaString)) {
			fails.append("\ntestLinkIndex:\nexpected\t" + uniqueKappaString
					+ ",\nbut\t\t" + smilesString + "\n");
		}

		return fails.toString();
	}

	private List<Agent> reverse(List<Agent> list) {
		ArrayList<Agent> reverse = new ArrayList<Agent>(2);
		reverse.add(list.get(1));
		reverse.add(list.get(0));
		return reverse;
	}

	private <E> List<E> copy(List<E> agents) {
		List<E> copy = new ArrayList<E>();
		for (E agent : agents) {
			copy.add(agent);
		}
		return copy;
	}

}