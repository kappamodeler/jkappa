package com.plectix.simulator.io.xml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;
import com.plectix.simulator.interfaces.ObservableInterface;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationClock;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.staticanalysis.Observables;
import com.plectix.simulator.staticanalysis.Snapshot;
import com.plectix.simulator.staticanalysis.SnapshotElement;
import com.plectix.simulator.staticanalysis.StaticAnalysisException;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;
import com.plectix.simulator.util.DecimalFormatter;
import com.plectix.simulator.util.Info;
import com.plectix.simulator.util.ObservableState;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.Info.InfoType;

public class SimulationDataXMLWriter {
	public static final int NUMBER_OF_SIGNIFICANT_DIGITS = 6;
	private final SimulationData simulationData;
	
	public SimulationDataXMLWriter(SimulationData simulationData) {
		this.simulationData = simulationData;
	}
	
	private final void writeRulesToXML(OurXMLWriter writer) throws XMLStreamException{
		SimulationArguments simulationArguments = simulationData.getSimulationArguments();
		KappaSystem kappaSystem = simulationData.getKappaSystem();
		
		writer.writeStartElement("RuleSet");
		writer.writeAttribute("Name", "ContactMap");
		int size = kappaSystem.getRules().size();
		InfluenceMapXMLWriter.addRulesToXML(size, writer, size, 
				simulationArguments.isOcamlStyleNameingInUse(), kappaSystem, false);
		writer.writeEndElement();
	}

	final void createXMLOutput(Writer outstream)
			throws ParserConfigurationException, TransformerException,
			XMLStreamException, StoryStorageException, StaticAnalysisException {
		PlxTimer timer = new PlxTimer();
		SimulationArguments simulationArguments = simulationData.getSimulationArguments();
		KappaSystem kappaSystem = simulationData.getKappaSystem();
//		SimulationClock clock = simulationData.getClock();
		
//		XMLOutputFactory output = XMLOutputFactory.newInstance();
//		XMLSimulatorWriter writer = output.createXMLSimulatorWriter(outstream);
		OurXMLWriter writer = new OurXMLWriter(outstream);
		writer.writeStartDocument();
		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
			writer.writeStartElement("ComplxSession");
		} else {
			writer.writeStartElement("SimplxSession");
			writer.writeAttribute("xsi:schemaLocation",
					"http://plectix.synthesisstudios.com SimplxSession.xsd");
		}
		writer.setDefaultNamespace("http://plectix.synthesisstudios.com/schemas/kappasession");
		writer.writeDefaultNamespace("http://plectix.synthesisstudios.com/schemas/kappasession");
		writer.writeNamespace("xsi",
				"http://www.w3.org/2001/XMLSchema-instance");

		if (simulationArguments.getCommandLineString() != null) {
			writer.writeAttribute("CommandLine", simulationArguments
					.getCommandLineString());
		} else {
			// TODO implement
			writer.writeAttribute("CommandLine", "custom command line");
		}

		writer.writeAttribute("InputFile", simulationArguments
				.getInputFileName());
		Date d = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		writer.writeAttribute("TimeStamp", df.format(d));

		timer.startTimer();

		// TODO check it
		if (simulationArguments.createSubViews()) {
			new SubviewsXMLWriter(kappaSystem.getSubViews()).write(writer);
		}

		if (simulationArguments.needToEnumerationOfSpecies()) {
			new SpeciesXMLWriter(kappaSystem.getEnumerationOfSpecies()).write(writer);
		}

		if (simulationArguments.createLocalViews()) {
			new LocalViewsXMLWriter(kappaSystem.getLocalViews()).write(writer);
		}

		if (simulationArguments.needToRunQualitativeCompression()
				|| simulationArguments.needToRunQuantitativeCompression()) {
			kappaSystem.getRuleCompressionBuilder().writeToXML(writer,
					simulationArguments.isOcamlStyleNameingInUse());
		}

		// TODO check it
		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
			writeRulesToXML(writer);
			new ContactMapXMLWriter(kappaSystem.getContactMap()).write(writer);
		}

		if (simulationArguments.needToBuildActivationMap()) {
			new InfluenceMapXMLWriter(kappaSystem.getInfluenceMap()).write(
					writer,
					kappaSystem.getRules().size(),
					kappaSystem.getObservables()
							.getConnectedComponentListForXMLOutput(),
					simulationArguments.needToBuildInhibitionMap(), kappaSystem,
					simulationArguments.isOcamlStyleNameingInUse());
			SimulationClock.stopTimer(simulationData, InfoType.OUTPUT, timer,
					"-Building xml tree for influence map:");
		}

		// //TODO STORIES!!!!!!!!!!!
		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.STORIFY) {

			new StoriesXMLWriter(kappaSystem.getStories()).write(writer, kappaSystem,
					simulationArguments.getIterations());

		}

		if (simulationData.getSnapshots() != null) {
			timer.startTimer();
			writeSnapshotsToXML(writer);
			SimulationClock.stopTimer(simulationData, InfoType.OUTPUT, timer,
					"-Building xml tree for snapshots:");
		}

		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.SIM) {
			int obsCountTimeListSize = kappaSystem.getObservables()
					.getCountTimeList().size();
			writer.writeStartElement("Simulation");
			writer.writeAttribute("TotalEvents", Long
					.toString(simulationArguments.getMaxNumberOfEvents()));
			writer.writeAttribute("TotalTime", DecimalFormatter
					.toStringWithSetNumberOfSignificantDigits(
							simulationArguments.getTimeLimit(),
							NUMBER_OF_SIGNIFICANT_DIGITS).replace(",", "."));
			writer.writeAttribute("InitTime", DecimalFormatter
					.toStringWithSetNumberOfSignificantDigits(
							simulationArguments.getInitialTime(),
							NUMBER_OF_SIGNIFICANT_DIGITS).replace(",", "."));

			writer.writeAttribute("TimeSample", DecimalFormatter
					.toStringWithSetNumberOfSignificantDigits(
							kappaSystem.getObservables().getTimeSampleMin(),
							NUMBER_OF_SIGNIFICANT_DIGITS).replace(",", "."));

			List<ObservableInterface> oiObs = new LinkedList<ObservableInterface>();
			List<ObservableInterface> oiRules = new LinkedList<ObservableInterface>();
			initOILists(kappaSystem.getObservables().getUniqueComponentList(), oiObs, oiRules);
			writePlots(writer,oiObs, oiRules);
			// CData right
			timer.startTimer();
			writer.writeStartElement("CSV");
			StringBuffer cdata = new StringBuffer();
			for (int i = 0; i < obsCountTimeListSize; i++) {
				cdata.append(appendData(kappaSystem.getObservables(), oiObs, oiRules, i));
			}
			writer.writeCData(cdata.toString());
			writer.writeEndElement();
			writer.writeEndElement();
			SimulationClock.stopTimer(simulationData, InfoType.OUTPUT, timer,
					"-Building xml tree for data points:");
		}

		appendInfo(writer);

		writer.writeEndElement();
		writer.writeEndDocument();
		writer.flush();

	}
	
	private void initOILists(List<ObservableInterface> list,
			List<ObservableInterface> oiObs,
			List<ObservableInterface> oiRules){
		for (int i = list.size() - 1; i >= 0; i--) {
			ObservableInterface oi = list.get(i); 
			if(oi instanceof ObservableConnectedComponentInterface)
				oiObs.add(oi);
			else
				oiRules.add(oi);
		}
	}
	
	private void writePlots(OurXMLWriter writer, 
			List<ObservableInterface> oiObs,
			List<ObservableInterface> oiRules) throws XMLStreamException{
		for (ObservableInterface oi : oiObs) {
			createElement(oi, writer);
		}
		for (ObservableInterface oi : oiRules) {
			createElement(oi, writer);
		}
	}
	
	private final void writeSnapshotsToXML(OurXMLWriter writer)
			throws XMLStreamException {
		for (Snapshot snapshot : simulationData.getSnapshots()) {
			writer.writeStartElement("FinalState");
			writer.writeAttribute("Time", String.valueOf(snapshot
					.getSnapshotTime()));
			List<SnapshotElement> snapshotElementList = snapshot
					.getSnapshotElements();
			for (SnapshotElement se : snapshotElementList) {
				writer.writeStartElement("Species");
				writer.writeAttribute("Kappa", se.getComponentsName());
				writer.writeAttribute("Number", String.valueOf(se.getCount()));
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}
	}

	private final void appendInfo(OurXMLWriter writer)
			throws XMLStreamException {
		writer.writeStartElement("Log");
		for (Info info : simulationData.getInfo()) {
			writer.writeStartElement("Entry");
			writer.writeAttribute("Position", info.getPosition());
			writer.writeAttribute("Count", info.getCount());
			writer.writeAttribute("Message", info.getMessageWithTime());
			writer.writeAttribute("Type", info.getType().toString());
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}

	private final void createElement(ObservableInterface observableComponent,
			OurXMLWriter writer) throws XMLStreamException {
		writer.writeStartElement("Plot");
		String obsName = observableComponent.getName();
		if (obsName == null)
			obsName = observableComponent.getLine();

		if (observableComponent instanceof ObservableConnectedComponentInterface) {
			writer.writeAttribute("Type", "OBSERVABLE");
			writer.writeAttribute("Text", '[' + obsName + ']');
		} else {
			writer.writeAttribute("Type", "RULE");
			writer.writeAttribute("Text", obsName);
		}
		writer.writeEndElement();
	}

	public final void outputXMLData()
			throws ParserConfigurationException, TransformerException,
			XMLStreamException, IOException, StoryStorageException, StaticAnalysisException {
		String destination = simulationData.getSimulationArguments().getXmlOutputDestination();
		outputXMLData(new BufferedWriter(new FileWriter(destination)));
	}

	public final void outputXMLData(Writer writer)
			throws ParserConfigurationException, TransformerException,
			XMLStreamException, IOException, StoryStorageException, StaticAnalysisException {
		PlxTimer timerOutput = new PlxTimer();
		timerOutput.startTimer();
		try {
			createXMLOutput(writer);
		} finally {
			writer.close();
		}
	}

	private final StringBuffer appendData(Observables obs,
			List<ObservableInterface> oiObs, List<ObservableInterface> oiRules, int index) {
		StringBuffer cdata = new StringBuffer();
		SimulationArguments simulationArguments = simulationData.getSimulationArguments();
		KappaSystem kappaSystem = simulationData.getKappaSystem();
		
		ObservableState state = kappaSystem.getObservables().getCountTimeList().get(index); 
		cdata.append(DecimalFormatter.toStringWithSetNumberOfSignificantDigits(state.getTime(),
				NUMBER_OF_SIGNIFICANT_DIGITS).replace(",", "."));
		
		if (simulationArguments.isUnifiedTimeSeriesOutput()){
			cdata.append(",");
			cdata.append(Long.toString(state.getEvent()));
		}
			
		writeOItoCData(cdata, obs, oiObs, index);
		writeOItoCData(cdata, obs, oiRules, index);
		cdata.append("\n");
		return cdata;
	}
	
	private final void writeOItoCData(StringBuffer cdata, 
			Observables obs,
			List<ObservableInterface> oi,
			int index){
		for (ObservableInterface oCC : oi) {
			cdata.append(",");
			cdata.append(getItem(obs, index, oCC));
		}
	}

	private final String getItem(Observables observables, int index,
			ObservableInterface oCC) {
		if (oCC.isUnique()) {
			return oCC.getStringItem(index, observables);
		}

		long value = 1;
		for (ObservableConnectedComponentInterface cc : observables
				.getConnectedComponentList()) {
			if (cc.getId() == oCC.getId()) {
				value *= cc.getItem(index, observables);
			}
		}

		return Double.valueOf(value).toString();
	}
}
