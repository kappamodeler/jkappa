package com.plectix.simulator.util;

public class MessageConstructor {
	private StringBuffer myMessage = new StringBuffer();
	private boolean myEmpty = true;

	public MessageConstructor() {
		myMessage.append("Failed on following tests : \n");
	}

	public void addValue(String value) {
		if (myEmpty) {
			myEmpty = false;
		} else {
			myMessage.append(",\n");
		}
		myMessage.append(value);
	}

	public void addComment(String message) {
		myMessage.append(" (" + message + ")");
	}

	public boolean isEmpty() {
		return myEmpty;
	}

	public String getMessage() {
		if (isEmpty()) {
			return "OK";
		} else {
			return myMessage.toString();
		}
	}
}
