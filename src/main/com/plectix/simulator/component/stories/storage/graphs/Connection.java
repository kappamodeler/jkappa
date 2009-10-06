package com.plectix.simulator.component.stories.storage.graphs;


public final class Connection {

	private final long source;
	private final long target;

	public Connection(Long fromNode, Long toNode) {
		source = fromNode;
		target = toNode;
	}

	public final long getFrom() {
		return source;
	}

	public final long getTo() {
		return target;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Connection))
			return false;
		Connection in = (Connection) obj;
		if (this.source == in.source && this.target == in.target)
			return true;
		return false;
	}

	@Override
	public final int hashCode() {
		int result = 101;
		result = getResult(result, (int) (source ^ (source >>> 32)));
		result = getResult(result, (int) (target ^ (target >>> 32)));
		return result;
	}

	private static final int getResult(int result, int constant) {
		return 37 * result + constant;
	}
}
