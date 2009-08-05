package com.plectix.simulator.components.stories.storage.graphs;

import com.plectix.simulator.components.stories.storage.WireHashKey;

public class Connection {
	
	private Long from;
	private Long to;
	
	
	public Connection(Long fromNode, Long toNode) {
		from = fromNode;
		to = toNode;
	}
	
	

	public Long getFrom() {
		return from;
	}



	public Long getTo() {
		return to;
	}



	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof WireHashKey))
			return false;
		Connection in = (Connection) obj;
		if (this.from.equals(in.from) && this.to.equals(in.to))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		int result = 101;
		result = getResult(result, (int) (from ^ (from >>> 32)));
		result = getResult(result, (int) (to ^ (to >>> 32)));
		return result;
	}

	private static int getResult(int result, int c) {
		return 37 * result + c;
	}


}
