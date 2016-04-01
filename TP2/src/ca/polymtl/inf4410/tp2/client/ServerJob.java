package ca.polymtl.inf4410.tp2.client;

import java.util.Vector;

import ca.polymtl.inf4410.tp2.shared.OperationInfo;

public class ServerJob {
	public Vector<OperationInfo> currentJob = null;
	public Vector<Boolean> jobDone = null;
	
	public ServerJob(int nOperations) {
		currentJob = new Vector<OperationInfo>();
		jobDone = new Vector<Boolean>();
		for(int i = 0; i < nOperations; i++) {
			jobDone.add(false);
		}
	}
	
	public void setNewJob(Vector<ResultGroup> results, Vector<OperationInfo> operations) {
		if(currentJob.size() == 0) {
			for(int i = 0; i < results.size(); i++) {
				System.out.println("Adding a operation to the job...");
				currentJob.add(operations.get(i));
			}
		}
	}
}
