package ca.polymtl.inf4410.tp2.shared;

public class OperationInfo {
	public String command;
	public String operand;
	public int id;
	
	public OperationInfo(String command, String operand, int id) {
		this.command = command;
		this.operand = operand;
		this.id = id;
	}
}