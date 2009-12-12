package com.plectix.simulator.io.xml;

import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/*package*/ class OurXMLWriter {

	private XMLStreamWriter writer;

	private static final String ENTER = "\n";
	private static final String TAB = "\t";

	private static int tabCounter = 0;

	public OurXMLWriter(Writer outstream) throws XMLStreamException {

		XMLOutputFactory output = XMLOutputFactory.newInstance();
		writer = output.createXMLStreamWriter(outstream);

	}

	public void writeStartDocument() throws XMLStreamException {
		writer.writeStartDocument("utf-8", "1.0");
	}

	public void writeStartElement(String localName) throws XMLStreamException {
		tabCounter++;
		writeTabs();
		writer.writeStartElement(localName);
	}

	private void writeTabs() throws XMLStreamException {
		writer.writeCharacters(ENTER);
		for (int i = 0; i < tabCounter; i++) {
			writer.writeCharacters(TAB);
		}

	}

	public void writeEndElement() throws XMLStreamException {
		writeTabs();
		tabCounter--;
		writer.writeEndElement();
	}

	public void writeNamespace(String prefix, String namespaceURI)
			throws XMLStreamException {
		writer.writeNamespace(prefix, namespaceURI);
	}

	public void writeDefaultNamespace(String namespaceURI)
			throws XMLStreamException {
		writer.writeDefaultNamespace(namespaceURI);
	}

	public void setDefaultNamespace(String uri) throws XMLStreamException {
		writer.setDefaultNamespace(uri);
	}

	public void writeAttribute(String localName, String value)
			throws XMLStreamException {
		writer.writeAttribute(localName, value);
	}

	public void writeCData(String data) throws XMLStreamException {
		writer.writeCData(data);
	}

	public void writeEndDocument() throws XMLStreamException {
		writer.writeEndDocument();
	}

	public void flush() throws XMLStreamException {
		writer.flush();
	}

	public void close() throws XMLStreamException {
		writer.close();
	}
}
