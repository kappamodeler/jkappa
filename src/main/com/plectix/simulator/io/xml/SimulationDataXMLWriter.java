package com.plectix.simulator.io.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	
	private final String getXmlSessionPath() {
		SimulationArguments simulationArguments = simulationData.getSimulationArguments();
		
		if (simulationArguments.getXmlSessionPath().length() > 0) {
			return simulationArguments.getXmlSessionPath() + File.separator
					+ simulationArguments.getXmlSessionName();
		} else {
			return simulationArguments.getXmlSessionName();
		}
	}
	
	private final void writeRulesToXML(OurXMLWriter writer) throws XMLStreamException{
		SimulationArguments simulationArguments = simulationData.getSimulationArguments();
		KappaSystem kappaSystem = simulationData.getKappaSystem();
		
		writer.writeStartElement("RuleSet");
		writer.writeAttribute("Name", "Original");
		int size = kappaSystem.getRules().size();
		InfluenceMapXMLWriter.addRulesToXML(size, writer, size, 
				simulationArguments.isOcamlStyleNameingInUse(), kappaSystem, false);
		writer.writeEndElement();
	}

	public final void createXMLOutput(Writer outstream)
			throws ParserConfigurationException, TransformerException,
			XMLStreamException, StoryStorageException {
		PlxTimer timer = new PlxTimer();
		SimulationArguments simulationArguments = simulationData.getSimulationArguments();
		KappaSystem kappaSystem = simulationData.getKappaSystem();
		SimulationClock clock = simulationData.getClock();
		
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

		writer.writeAttribute("CommandLine", simulationArguments
				.getCommandLineString());

		writer.writeAttribute("InputFile", simulationArguments
				.getInputFilename());
		Date d = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		writer.writeAttribute("TimeStamp", df.format(d));

		timer.startTimer();

		// TODO check it
		if (simulationArguments.createSubViews()) {
			new SubviewsXMLWriter(kappaSystem.getSubViews()).write(writer);
		}

		if (simulationArguments.useEnumerationOfSpecies()) {
			new SpeciesXMLWriter(kappaSystem.getEnumerationOfSpecies()).write(writer);
		}

		if (simulationArguments.createLocalViews()) {
			new LocalViewsXMLWriter(kappaSystem.getLocalViews()).write(writer);
		}

		if (simulationArguments.runQualitativeCompression()
				|| simulationArguments.runQuantitativeCompression()) {
			kappaSystem.getRuleCompressionBuilder().writeToXML(writer,
					simulationArguments.isOcamlStyleNameingInUse());
		}

		// TODO check it
		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
			writeRulesToXML(writer);
			new ContactMapXMLWriter(kappaSystem.getContactMap()).write(writer);
		}

		if (simulationArguments.isActivationMap()) {
			new InfluenceMapXMLWriter(kappaSystem.getInfluenceMap()).write(
					writer,
					kappaSystem.getRules().size(),
					kappaSystem.getObservables()
							.getConnectedComponentListForXMLOutput(),
					simulationArguments.isInhibitionMap(), kappaSystem,
					simulationArguments.isOcamlStyleNameingInUse());
			clock.stopTimer(InfoType.OUTPUT, timer,
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
			clock.stopTimer(InfoType.OUTPUT, timer,
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
							simulationArguments.getTimeLength(),
							NUMBER_OF_SIGNIFICANT_DIGITS).replace(",", "."));
			writer.writeAttribute("InitTime", DecimalFormatter
					.toStringWithSetNumberOfSignificantDigits(
							simulationArguments.getInitialTime(),
							NUMBER_OF_SIGNIFICANT_DIGITS).replace(",", "."));

			writer.writeAttribute("TimeSample", DecimalFormatter
					.toStringWithSetNumberOfSignificantDigits(
							kappaSystem.getObservables().getTimeSampleMin(),
							NUMBER_OF_SIGNIFICANT_DIGITS).replace(",", "."));

			List<ObservableInterface> list = kappaSystem.getObservables()
					.getUniqueComponentList();

			for (int i = list.size() - 1; i >= 0; i--) {
				createElement(list.get(i), writer);
			}

			// CData right
			timer.startTimer();
			writer.writeStartElement("CSV");
			StringBuffer cdata = new StringBuffer();
			for (int i = 0; i < obsCountTimeListSize; i++) {
				cdata.append(appendData(kappaSystem.getObservables(), list, i));
			}
			writer.writeCData(cdata.toString());
			writer.writeEndElement();
			writer.writeEndElement();
			clock.stopTimer(InfoType.OUTPUT, timer,
					"-Building xml tree for data points:");
		}

		appendInfo(writer);

		writer.writeEndElement();
		writer.writeEndDocument();
		writer.flush();

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
			XMLStreamException, IOException, StoryStorageException {
		outputXMLData(new BufferedWriter(new FileWriter(getXmlSessionPath())));
	}

	public final void outputXMLData(Writer writer)
			throws ParserConfigurationException, TransformerException,
			XMLStreamException, IOException, StoryStorageException {
		PlxTimer timerOutput = new PlxTimer();
		timerOutput.startTimer();
		try {
			createXMLOutput(writer);
		}
		finally {
			writer.close();
		}
		simulationData.getClock().stopTimer(InfoType.OUTPUT, timerOutput,
				"-Results outputted in xml session:");
	}

	private final StringBuffer appendData(Observables obs,
			List<ObservableInterface> list, int index) {
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
			
		for (int j = list.size() - 1; j >= 0; j--) {
			cdata.append(",");
			ObservableInterface oCC = list.get(j);
			cdata.append(getItem(obs, index, oCC));
		}
		cdata.append("\n");
		return cdata;
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
