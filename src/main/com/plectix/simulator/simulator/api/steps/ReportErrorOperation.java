package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.api.OperationType;

public class ReportErrorOperation extends AbstractOperation<Object> {
	private String message = null;
	private Exception exception = null;
		
	public ReportErrorOperation(String message) {
		super(null, OperationType.DO_NOTHING);
		this.message = message;
	}

	public ReportErrorOperation(Exception exception) {
		super(null, OperationType.DO_NOTHING);
		this.exception  = exception;
		this.message = exception.getMessage();
	}
	
	protected final Object performDry() throws RuntimeException {
		if (exception != null) {
			throw new RuntimeException(exception);
		} else {
			throw new RuntimeException(message);
		}
	}

	@Override
	protected boolean noNeedToPerform() {
		return false;
	}

	@Override
	protected Object retrievePreparedResult() {
		return null;
	}
}
