package ca.polymtl.inf4410.tp2.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import ca.polymtl.inf4410.tp2.shared.OperationResult;

public class ResultGroup {
	private boolean isResultValid = false;
	private int nServer = 0;
	private int majorityLevel = 0;
	public int result;
	private Vector<OperationResult> finishedResultGroup = null;
	public Vector<Boolean> serverHasBeenTask = null;
	
	public ResultGroup(int nServer) {
		finishedResultGroup = new Vector<OperationResult>();
		for(int i = 0; i < nServer; i++) {
			finishedResultGroup.add(null);
		}
		this.nServer = nServer;
		majorityLevel = (int)Math.ceil((double)nServer / 2);
		serverHasBeenTask = new Vector<Boolean>();
		for(int i = 0; i < nServer; i++) {
			serverHasBeenTask.add(false);
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
		if(finishedResultGroup.get(0) != null) {
			int count = 1;
			int tempCount;
			int popular = finishedResultGroup.get(0).result;
			int temp = -1;
			for(int i = 0; i < finishedResultGroup.size(); i++) {
				if(finishedResultGroup.get(i) != null) {
					temp = finishedResultGroup.get(i).result;
					tempCount = 0;
					for(int j = 1; j < finishedResultGroup.size(); j++) {
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
			System.out.println("Popular/count:" + popular + "/" + count);
			Pair pair = new Pair(popular, count);
			return pair;
		}
		return null;
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
		System.out.println("addResult_serverId" + or.serverId);
		// Only add if the given result was not already added
		if(finishedResultGroup.get(or.serverId) == null) {
			finishedResultGroup.set(or.serverId, or);
		}
	}
	
}
