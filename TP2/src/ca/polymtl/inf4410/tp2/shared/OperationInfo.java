package ca.polymtl.inf4410.tp2.shared;

import java.io.Serializable;

public class OperationInfo implements Serializable {
	/**
	 * Automatically generated serialVersionUID
	 */
	private static final long serialVersionUID = 768559023527339212L;
	public String command;
	public String operand;
	public int id;
	
	public OperationInfo(String command, String operand, int id) {
		this.command = command;
		this.operand = operand;
		this.id = id;
	}
}