package com.plectix.simulator.components;

import org.w3c.dom.Element;

public class CStoryType {
	public static final byte TYPE_INTRO = 0;
	public static final byte TYPE_RULE = 1;

	public static final String STRING_INTRO = "INTRO";
	public static final String STRING_RULE = "RULE";
	public static final String STRING_OBS = "OBSERVABLE";
	private byte type;
	private String text;

	public String getText() {
		return text;
	}

	public String getData() {
		return data;
	}

	private String data;

	public byte getType() {
		return type;
	}

	public int getTraceID() {
		return traceID;
	}

	public int getId() {
		return id;
	}

	private int traceID;
	private int id;
	private int depth;

	public CStoryType(byte type, int traceID, int id, String text,
			String data, int depth) {
		this.traceID = traceID;
		this.id = id;
		this.type = type;
		this.text = text;
		this.data = data;
		this.depth = depth;
	}

	public void fillNode(Element node, String strType) {
		node.setAttribute("Id", Integer.toString(id));
		node.setAttribute("Type", strType);
		node.setAttribute("Text", text);
		node.setAttribute("Data", data);
		node.setAttribute("Depth", Integer.toString(depth));
	}

	public void fillConnection(Element node, int toNode) {
		node.setAttribute("FromNode", Integer.toString(this.id));
		node.setAttribute("ToNode", Integer.toString(toNode));
		node.setAttribute("Relation", "STRONG");
	}

}
