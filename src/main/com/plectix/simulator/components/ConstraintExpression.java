package com.plectix.simulator.components;

public class ConstraintExpression {
	public final static byte TYPE_CONSTRAINT_EXPRESSION_EQUALS = 0;
	public final static byte TYPE_CONSTRAINT_EXPRESSION_NOT_EQUALS = 1;
	public final static byte TYPE_CONSTRAINT_EXPRESSION_ID = 2;
	
	private byte typeExpression;
	private long idLeft;
	private long idRight;

	public ConstraintExpression(byte typeExpression,long idLeft, long idRight) {
		switch (typeExpression) {
		case TYPE_CONSTRAINT_EXPRESSION_EQUALS:
			this.typeExpression = TYPE_CONSTRAINT_EXPRESSION_EQUALS;
			break;
		case TYPE_CONSTRAINT_EXPRESSION_NOT_EQUALS:
			this.typeExpression = TYPE_CONSTRAINT_EXPRESSION_NOT_EQUALS;
			break;
		case TYPE_CONSTRAINT_EXPRESSION_ID:
			this.typeExpression = TYPE_CONSTRAINT_EXPRESSION_ID;
			break;
		}
		
		this.idLeft = idLeft;
		this.idRight = idRight;
	}
	
	public byte getTypeExpression() {
		return typeExpression;
	}
	public void setTypeExpression(byte typeExpression) {
		this.typeExpression = typeExpression;
	}
	public long getIdLeft() {
		return idLeft;
	}
	public void setIdLeft(long idLeft) {
		this.idLeft = idLeft;
	}
	public long getIdRight() {
		return idRight;
	}
	public void setIdRight(long idRight) {
		this.idRight = idRight;
	}
}
