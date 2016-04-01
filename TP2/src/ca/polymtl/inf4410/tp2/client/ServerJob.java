package ca.polymtl.inf4410.tp2.client;

import java.util.Vector;

import ca.polymtl.inf4410.tp2.shared.OperationInfo;

public class ServerJob {
	public Vector<OperationInfo> currentJob = null;
	public Vector<Boolean> jobDone = null;
	private int nOperationToSend = 0;
	private int jobIndex = 0;
	public boolean lastSucceed = true;
	private int serverID;
	private int nServers;
	
	public ServerJob(int nOperations, int serverID, int nServers) {
		currentJob = new Vector<OperationInfo>();
		jobDone = new Vector<Boolean>();
		for(int i = 0; i < nOperations; i++) {
			jobDone.add(false);
		}
		this.serverID = serverID;
		this.nServers = nServers;
	}
	
	public void setNewJob(Vector<ResultGroup> results, Vector<OperationInfo> operations) {
		// Increment or decrement the amount of operation to send
		if(lastSucceed) {
			nOperationToSend++;
			// Set the last job to done
			for(int i = 0; i < currentJob.size(); i++) {
				results.get(currentJob.get(i).id).serverHasDoneTheJob.set(serverID, true);
				jobIndex++;
			}
			// Clear the last job and reset the new one
			currentJob.removeAllElements();
			for(int i = jobIndex; i < operations.size() && i < jobIndex + nOperationToSend; i++) {
				System.out.println("AlloBob..");
				currentJob.add(operations.get(i));
			}
		}
		else {
			nOperationToSend--;
			// Remove last element for the failed job
			currentJob.removeElementAt(currentJob.size()-1);
		}		
	}
}
