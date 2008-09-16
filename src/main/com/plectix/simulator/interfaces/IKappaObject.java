package com.plectix.simulator.interfaces;

import java.io.Serializable;

// import sun.org.mozilla.javascript.internal.xml.XMLObject;

public interface IKappaObject extends Serializable {

	public String toKappaString();

	// public XMLObject toXML();

	public String toPrettyString();  //TODO like after compilation

	//TODO www.graphviz.org
	public void toDotFormat();
}
