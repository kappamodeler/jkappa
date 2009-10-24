package com.plectix.simulator.staticanalysis;

public abstract class NamedEntity {
	
	protected abstract String getDefaultName();
	
	public abstract String getName();
	
	public final boolean hasDefaultName() { 
		return this.getDefaultName().equals(this.getName());
	}
	
	public final boolean hasSimilarName(NamedEntity entity) {
		return this.getName().equals(entity.getName());
	}
}
