package com.plectix.simulator.XMLmaps;

public class Connection {
	private String FromNode;
	private String ToNode;
	private String Relation;

	
	public Connection(String FromNode,String ToNode, String Relation) {
		this.FromNode = FromNode;
		this.ToNode = ToNode;
		this.Relation = Relation;
	}

	public String getFromNode() {
		return FromNode;
	}

	public String getToNode() {
		return ToNode;
	}

	public String getRelation() {
		return Relation;
	}
	
	
	public boolean equals(Connection c){
		
		if ((c.FromNode.equals(this.FromNode))&&
				(c.ToNode.equals(this.ToNode))&&
				(c.Relation.equals(this.Relation)))
			return true;
		return false;
	}
}
