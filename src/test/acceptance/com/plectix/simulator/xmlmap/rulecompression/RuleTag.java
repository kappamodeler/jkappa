package com.plectix.simulator.xmlmap.rulecompression;

public class RuleTag {

	private int id;
	private String data;
	private String name;

	public RuleTag(int _id, String _data, String _name) {

		id = _id;
		data = _data.replaceAll("&gt;", ">");
		name = _name;
	}



	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof RuleTag))
			return false;
		RuleTag r = (RuleTag) obj;
		return (id == r.id && data.equals(r.data));
//		return (id == r.id && name.equals(r.name));
	}

	@Override
	public int hashCode() {
		int result = 101;
		result = getResult(result, id);
		result = getResult(result, data.hashCode());
//		result = getResult(result, name.hashCode());
		return result;
	}

	private static int getResult(int result, int c) {
		return 37 * result + c;
	}

	@Override
	public String toString() {

		return "Id=\"" + id + "\" Data=\"" + data + "\" Name=\"" + name + "\"";
	}

}
