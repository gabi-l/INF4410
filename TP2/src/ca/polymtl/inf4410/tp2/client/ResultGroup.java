package ca.polymtl.inf4410.tp2.client;

import java.util.Vector;

import ca.polymtl.inf4410.tp2.shared.OperationResult;

public class ResultGroup {
	private boolean isResultValid = false;
	private int nServer = 0;
	private int majorityLevel = 0;
	public int result;
	public Vector<OperationResult> finishedResultGroup = null;
	public Vector<Boolean> serverHasDoneTheJob = null;
	
	public ResultGroup(int nServer) {
		finishedResultGroup = new Vector<OperationResult>();
		for(int i = 0; i < nServer; i++) {
			finishedResultGroup.add(null);
		}
		this.nServer = nServer;
		majorityLevel = (int)Math.ceil((double)this.nServer / 2);
		serverHasDoneTheJob = new Vector<Boolean>();
		for(int i = 0; i < nServer; i++) {
			serverHasDoneTheJob.add(false);
		}
	}
	
	private class Pair {
		public int a;
		public int b;
		public Pair(int a, int b) {
			this.a = a;
			this.b = b;
		}
	}
	private Pair getMostPopularAnswer() {
		int k = 0;
		while(k < finishedResultGroup.size() && finishedResultGroup.get(k) == null) {
			k++;
		}
		if(k == finishedResultGroup.size()) {
			return null;
		}
		
		int count = 1;
		int tempCount;
		int popular = finishedResultGroup.get(k).result;
		int temp = -1;
		for(int i = k; i < finishedResultGroup.size(); i++) {
			if(finishedResultGroup.get(i) != null) {
				temp = finishedResultGroup.get(i).result;
				tempCount = 0;
				for(int j = k; j < finishedResultGroup.size(); j++) {
					if(finishedResultGroup.get(j) != null) {
						if(temp == finishedResultGroup.get(j).result) {
							tempCount++;
						}
						if(tempCount > count) {
							popular = temp;
							count = tempCount;
						}
					}
				}
			}
		}
		Pair pair = new Pair(popular, count);
		return pair;
	}
	
	public boolean isResultValid() {
		Pair popular = getMostPopularAnswer();
		if(popular != null) {
			if(popular.b >= majorityLevel) {
				result = popular.a;
				isResultValid = true;
			}
		}		
		return isResultValid;
	}
	
	public void addResult(OperationResult or) {
		// Only add if the given result was not already added
		if(finishedResultGroup.get(or.serverId) == null) {
			finishedResultGroup.set(or.serverId, or);
		}
	}
	
}
