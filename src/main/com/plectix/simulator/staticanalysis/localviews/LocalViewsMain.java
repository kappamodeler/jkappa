package com.plectix.simulator.staticanalysis.localviews;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import com.plectix.simulator.simulator.XMLSimulatorWriter;

import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.abstracting.AbstractSite;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.staticanalysis.subviews.storage.SubViewsInterface;
import com.plectix.simulator.util.NameDictionary;

public final class LocalViewsMain {
	private final AllSubViewsOfAllAgentsInterface subviews;
	private final Map<String, List<AbstractAgent>> localViews;

	public LocalViewsMain(AllSubViewsOfAllAgentsInterface subViews) {
		this.subviews = subViews;
		localViews = new LinkedHashMap<String, List<AbstractAgent>>();
	}

	public final void buildLocalViews() {
		Map<String, AbstractAgent> agentsMap = subviews.getFullMapOfAgents();
		Set<String> keys = agentsMap.keySet();

		for (String agentName : keys) {
			List<AbstractAgent> setOfLocalViews = new LinkedList<AbstractAgent>();
			List<AbstractAgent> temp = new LinkedList<AbstractAgent>();
			for (SubViewsInterface subView : subviews
					.getAllSubViewsByType(agentName)) {
				if (setOfLocalViews.isEmpty()) {
					temp.addAll(subView.getAllSubViewsCoherent(null));
				} else {
					for (AbstractAgent agent : setOfLocalViews) {
						temp.addAll(subView.getAllSubViewsCoherent(agent));
					}
				}
				setOfLocalViews.clear();
				for (int i = 0; i < temp.size(); i++) {
					setOfLocalViews.add(temp.get(i).clone());
				}
				temp.clear();
			}
			localViews.put(agentName, setOfLocalViews);
		}
	}

	public final void addLocalView(AbstractAgent view) {
		if (localViews.get(view.getName()) == null) {
			List<AbstractAgent> setOfLocalViews = new LinkedList<AbstractAgent>();
			localViews.put(view.getName(), setOfLocalViews);
		}
		localViews.get(view.getName()).add(view);
	}

	public final int getCountOfCoherentAgent(AbstractAgent mask) {
		int answer = 0;
		if (localViews.get(mask.getName()) == null) {
			return answer;
		}
		for (AbstractAgent agent : localViews.get(mask.getName())) {
			if (mask.isFit(agent))
				answer++;
		}
		return answer;
	}

	/**
	 * 
	 * @param masks
	 * @param shadow
	 * @return if reachables of masks and shadow are different by information of
	 *         site s<br>
	 *         then return id of s.<br>
	 *         If there is no such site then return any siteId with dividing
	 *         property
	 */
	public final String getObstructionSiteForCoherentAgentAndList(
			List<AbstractAgent> masks, AbstractAgent shadow) {

		if (localViews.get(shadow.getName()) == null) {
			return null;
		}

		for (AbstractAgent agent : localViews.get(shadow.getName())) {
			if (shadow.isFit(agent)) {
				boolean stop = false;
				for (AbstractAgent mask : masks) {
					if (mask.isFit(agent)) {
						stop = true;
						break;
					}
				}
				if (!stop) {
					for (AbstractSite as : agent.getSitesMap().values()) {
						AbstractAgent copy = new AbstractAgent(agent
								.getName());
						copy.addSite(as);
						boolean stop2 = false;
						for (AbstractAgent mask : masks) {
							if (copy.isFitTwo(mask)) {
								stop2 = true;
								break;
							}
						}
						if (!stop2) {
							return as.getName();
						}
					}
					for (AbstractSite as : agent.getSitesMap().values()) {
						AbstractAgent copy = new AbstractAgent(agent
								.getName());
						copy.addSite(as);
						for (AbstractAgent mask : masks) {
							if (!copy.isFit(mask)) {
								return as.getName();
							}
						}
					}
				}
			}
		}
		return null;
	}

	public final Map<String, List<AbstractAgent>> getLocalViews() {
		return localViews;
	}

	public final void writeToXML(XMLSimulatorWriter streamWriter) throws XMLStreamException {
		streamWriter.writeStartElement("Reachables");
		streamWriter.writeAttribute("Name", "Views");
		for (Map.Entry<String, List<AbstractAgent>> entry : localViews
				.entrySet()) {
			String agentName = entry.getKey();
			if (NameDictionary.isDefaultAgentName(agentName))
				continue;
			List<AbstractAgent> list = entry.getValue();
			streamWriter.writeStartElement("Set");
			streamWriter.writeAttribute("Agent", agentName);
			for (AbstractAgent agent : list) {
				streamWriter.writeStartElement("Entry");
				streamWriter.writeAttribute("Data", agent.toStringForXML());
				streamWriter.writeEndElement();
			}
			streamWriter.writeEndElement();
		}
		streamWriter.writeEndElement();
	}

	public final List<AbstractAgent> getCoherentAgents(AbstractAgent mask) {
		List<AbstractAgent> answer = new LinkedList<AbstractAgent>();
		for (AbstractAgent agent : localViews.get(mask.getName())) {
			if (mask.isFit(agent))
				answer.add(agent);
		}
		return answer;
	}
}
