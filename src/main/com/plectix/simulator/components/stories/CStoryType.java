package com.plectix.simulator.components.stories;

import java.util.Collection;

import org.w3c.dom.Element;

import com.plectix.simulator.simulator.xml.EntityModifier;
import com.plectix.simulator.simulator.xml.RelationModifier;

public class CStoryType {

	public enum StoryOutputType {
		INTRO, RULE, OBS;
	}

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

	public void fillNode(Element node, EntityModifier strType) {
		node.setAttribute("Id", Integer.toString(id));
		node.setAttribute("Type", strType.getString());
		node.setAttribute("Text", text);
		node.setAttribute("Data", data);
		node.setAttribute("Depth", Integer.toString(depth));
	}

	public void fillConnection(Element node, int toNode, RelationModifier modifier) {
		node.setAttribute("Relation", modifier.getString());
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
