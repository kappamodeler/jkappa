package com.plectix.simulator.components.complex.subviews;

import com.plectix.simulator.components.complex.subviews.storage.ISubViewsStorage;

public class CSubViews {
	private CSubViewClass subViewClass;
	private ISubViewsStorage storage;

	public CSubViews(CSubViewClass subViewClass, ISubViewsStorage storage) {
		this.subViewClass = subViewClass;
		this.storage = storage;
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof CSubViewClass))
			return false;
		
		CSubViewClass inClass = (CSubViewClass)obj;
		if(!subViewClass.equals(inClass))
			return false;
		return true;
	}
	
	public int hashCode() {
		return subViewClass.hashCode();
	}
	
	public ISubViewsStorage getStorage(){
		return storage;
	}
	
	public String toString() {
		return subViewClass.toString();
	}
}
