package ca.polymtl.inf4410.tp2.shared;

public class OperationResult {
	public int result;
	public int serverId;
	public int operationId;
	
	public OperationResult(int result, int serverId, int operationId) {
		this.result = result;
		this.serverId = serverId;
		this.operationId = operationId;
	}
}