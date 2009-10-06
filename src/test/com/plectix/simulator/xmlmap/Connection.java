package com.plectix.simulator.xmlmap;

public class Connection {
	private String FromNode;
	private String ToNode;
	private String Relation;

	public Connection(String FromNode, String ToNode, String Relation) {
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

	@Override
	public boolean equals(Object aConnection) {

		if (this == aConnection)
			return true;

		if (aConnection == null)
			return false;

		if (getClass() != aConnection.getClass())
			return false;

		Connection connection = (Connection) aConnection;

		if ((connection.FromNode.equals(this.FromNode))
				&& (connection.ToNode.equals(this.ToNode))
				&& (connection.Relation.equals(this.Relation)))
			return true;
		return false;
	}
}
