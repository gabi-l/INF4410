package ca.polymtl.inf4410.tp2.client;

import java.util.Vector;

import ca.polymtl.inf4410.tp2.shared.OperationInfo;

public class ServerJob {
	public Vector<OperationInfo> currentJob = null;
	public Vector<Boolean> jobDone = null;
	private int nOperationToSend = 0;
	public int jobIndex = 0;
	public boolean lastSucceed = true;
	public boolean isDead = false;
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
		this.jobIndex = (int)(nOperations * ((double)this.serverID/this.nServers));
	}
	
	private boolean isAllResultValid(Vector<ResultGroup> results) {
		for(int i = 0; i < results.size(); i++) {
			if(!results.get(i).isResultValid()) {
				return false;
			}
		}
		return true;
	}
	
	public void setNewJob(Vector<ResultGroup> results, Vector<OperationInfo> operations) {
		if(!isDead) {
			// Increment or decrement the amount of operation to send
			if(lastSucceed) {
				nOperationToSend++;
				// Set the last job to done
				for(int i = 0; i < currentJob.size(); i++) {
					results.get(currentJob.get(i).id).serverHasDoneTheJob.set(serverID, true);
					jobIndex++;
					if(jobIndex >= operations.size()) {
						jobIndex = 0;
					}
				}
				// Clear the last job
				currentJob.removeAllElements();
				
				// Set a new job
				int operationCurrentlySent = 0;
				for(int i = jobIndex; i < operations.size() && operationCurrentlySent < nOperationToSend;) {
					if(!isAllResultValid(results)) {
						int index = i;
						/* Find a result that is not yet valid */
						boolean isCompleteTurnDone = false;
						boolean isIndexValid = true;
						while(results.get(index).isResultValid() ||
							  results.get(index).finishedResultGroup.get(serverID) != null ||
							  currentJob.contains(operations.get(index)) ) {
							index++;
							if(index >= results.size()) {
								index = 0;
								if(isCompleteTurnDone) {
									isIndexValid = false;
									break;
								}
								isCompleteTurnDone = true;
							}
						}
						if(isIndexValid) {
							currentJob.add(operations.get(index));
						}
						index++;
						if(index >= results.size()) {
							index = 0;
						}
						i = index;
					}
						operationCurrentlySent++;
				}	
			}
			else {
				nOperationToSend--;
				// Remove last element for the failed job
				currentJob.removeElementAt(currentJob.size()-1);
				lastSucceed = true;
			}
		}
		else {
			currentJob.removeAllElements();
		}
	}
}
