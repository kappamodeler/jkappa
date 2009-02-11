package com.plectix.simulator.components.stories;

import java.util.Collection;

import org.w3c.dom.Element;

import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.ISite;

public class CStoryType {

	public enum StoryOutputType {
		INTRO, RULE, OBS;
	}

	public static final String STRING_INTRO = "INTRO";
	public static final String STRING_RULE = "RULE";
	public static final String STRING_OBS = "OBSERVABLE";

	public static final String RELATION_STRONG = "STRONG";
	public static final String RELATION_WEAK = "WEAK";

	private StoryOutputType type;
	private String text;

	public String getText() {
		return text;
	}

	public String getData() {
		return data;
	}

	private String data;

	public StoryOutputType getType() {
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

	public CStoryType(StoryOutputType type, int traceID, int id, String text,
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

	public void fillConnection(Element node, int toNode, String relationType) {
		node.setAttribute("Relation", relationType);
		node.setAttribute("FromNode", Integer.toString(this.id));
		node.setAttribute("ToNode", Integer.toString(toNode));
	}

	public final boolean equalz(CStoryType type) {
		if (this == type) {
			return true;
		}
		if (this.id == type.getId())
			return true;
		return false;
	}

	public final boolean includedInCollection(Collection<CStoryType> collection) {
		for (CStoryType st : collection) {
			if (this.equalz(st)) {
				return true;
			}
		}
		return false;
	}
}
