package com.plectix.simulator.util.io;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.staticanalysis.stories.graphs.MergeStoriesGraphs;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;

/**
 * contains some util methods which give us
 * an idea about the future XML content
 * @author evlasov
 *
 */
public class XMLOutputOracle {
	public static final boolean simulationPlotDataIsNotEmpty(KappaSystem kappaSystem) {
		return !kappaSystem.getObservables().getCountTimeList().isEmpty();
	}
	
	public static final boolean finalStateDataIsNotEmpty(KappaSystem kappaSystem) {
		if (kappaSystem.getSimulationData().getSnapshots() == null) {
			return false;
		} else {
			return !kappaSystem.getSimulationData().getSnapshots().isEmpty();
		}
	}

	public static final boolean storiesDataIsNotEmpty(KappaSystem kappaSystem) {
		try {
			MergeStoriesGraphs merging = new MergeStoriesGraphs(kappaSystem.getStories());
			merging.merge();
			return !merging.getListUniqueGraph().isEmpty();
		} catch(StoryStorageException e) {
			e.printStackTrace();
			return false;
		}
	}
}
