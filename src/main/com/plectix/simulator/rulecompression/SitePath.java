package com.plectix.simulator.rulecompression;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CSite;

public class SitePath implements Comparable<SitePath> {
	private final LinkedList<CSite> data = new LinkedList<CSite>();
	private CAgent root;
	
	public SitePath(CAgent root) {
		this.root = root;
	}
	
	public List<CSite> getValue() {
		return data;
	}
	
	public String toString() {
		return "[ROOT = " + root.skeletonString() + ", PATH = " + data + "]";
	}
	
	public String hash() {
		StringBuffer sb = new StringBuffer();
		for (CSite si : data) {
			sb.append(si + "_");
		}
		return sb.toString();
	}
	
	public CSite getAndRemoveLast() {
		return data.remove(data.size() - 1);
	}
	
	public void addSite(CSite si) {
		data.addFirst(si);
	}

	public void removeLast() {
		data.removeLast();
	}
	
	public CAgent getRoot() {
		return root;
	}
	
	public boolean isTrivial() {
		return data.size() == 1 && data.get(0).getNameId() == -1;
	}

	public void setRoot(CAgent agent) {
		root = agent;
	}

	@Override
	public int compareTo(SitePath o) {
		return this.toString().compareTo(o.toString());
	}
}
