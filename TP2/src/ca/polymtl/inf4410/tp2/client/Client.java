package ca.polymtl.inf4410.tp2.client;

import java.util.List;
import java.util.Vector;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

import ca.polymtl.inf4410.tp2.shared.OperationResult;
import ca.polymtl.inf4410.tp2.shared.FileManager;
import ca.polymtl.inf4410.tp2.shared.OperationInfo;


public class Client {
	public static void main(String[] args) {
		/* Retrieving the args */
		String clientConfigFile = null;
		String donneeFile = null;
		if (args.length > 0) {
			clientConfigFile = args[0];
			if(args.length > 1) {
				donneeFile = args[1];
			}
		}
		else {
			System.out.println("There were no command...");
			return;
		}

		Client client = new Client(clientConfigFile, donneeFile);
		System.out.println("\nInitializing the calculation.\n");
		long start = System.currentTimeMillis();
		client.run();
		client.calculateAnswer();
		long end = System.currentTimeMillis();
		System.out.println("\nCalculation done.");
		System.out.println("Took " + (end - start)/1000 + " seconds.");
		return;
	}
	
	// General stuff of the client
	private FileManager fileManager = null;
	private Vector<ServerInfo> serverList = null;
	private int serverCount = 0;
	private boolean isSafeMode = false;
	
	// Job to do
	private Vector<OperationInfo> operationList = null;
	private Vector<ServerJob> serverJob = null;
	
	// Getting the results
	private ExecutorService threadPool = null;
	private Vector<Future<Vector<OperationResult>>> futures = null;
	
	// Final results
	private Vector<ResultGroup> results = null;

	public Client(String clientConfigFile, String donneeFile) {
		super();
		
		/* Initialize stuffs... */
		fileManager = new FileManager("./config_dir/");
		serverList = new Vector<ServerInfo>();
		operationList = new Vector<OperationInfo>();
		threadPool = Executors.newFixedThreadPool(3);
		futures = new Vector<Future<Vector<OperationResult>>>();
		
		/* Extract the config */
		List<String> clientConfig = null;
		try {
			clientConfig = fileManager.readConfig(clientConfigFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		if(clientConfig.get(0).equals("1")) {
			isSafeMode = true;
		}
		// Start all the servers
		for(int i = 1; i < clientConfig.size(); i++) {
			String line = clientConfig.get(i);
			String ip = line.substring(0, line.indexOf(" "));
			int port = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
			ServerInfo si = new ServerInfo(ip, port, i-1);
			serverList.add(si);
			serverCount++;
		}
		
		/* Extract the operations */
		List<String> operations = null;
		try {
			operations = fileManager.readOperations(donneeFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < operations.size(); i++) {
			String line = operations.get(i);
			String command = line.substring(0, line.indexOf(" "));
			String operand = line.substring(line.indexOf(" ") + 1, line.length());
			OperationInfo oi = new OperationInfo(command, operand, i);
			operationList.add(oi);
		}
		
		// Initialize the vector of results
		results = new Vector<ResultGroup>();
		for(int i = 0; i < operationList.size(); i++) {
			ResultGroup resultGroup = new ResultGroup(serverCount, isSafeMode);
			results.add(resultGroup);
		}
		
		// Initialize the ServerJob vector
		serverJob = new Vector<ServerJob>();
		for(int i = 0; i < serverCount; i++) {
			ServerJob sj = new ServerJob(operationList.size(), i, serverCount);
			serverJob.add(sj);
		}
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
	}
	
	private boolean isAllResultValid() {
		for(int i = 0; i < results.size(); i++) {
			if(!results.get(i).isResultValid()) {
				return false;
			}
		}
		return true;
	}
	
	private void calculateAnswer() {
		int result = 0;
		for(int i = 0; i < results.size(); i++) {
			result = (result + results.get(i).result) % 5000;
		}
		System.out.println("\nThe final result is:" + result);
	}
	
	private void run() {
		boolean wait = false;
		while(!isAllResultValid()) {
			// Starting the threads
			if(!wait) {
				for(int i = 0; i < serverCount; i++ ) {
					serverJob.get(i).setNewJob(results, operationList);
					serverList.get(i).currentJob = serverJob.get(i);
					System.out.println("Requesting to server " + serverList.get(i).id + " the amount of " + serverList.get(i).currentJob.currentJob.size() + " operation(s):");
					for(int j = 0; j < serverList.get(i).currentJob.currentJob.size(); j++) {
						System.out.println("    Operation: " + serverList.get(i).currentJob.currentJob.get(j).id);
					}
					Callable<Vector<OperationResult>> callable = serverList.get(i);
					Future<Vector<OperationResult>> future = threadPool.submit(callable);
					futures.add(future);
				}
			}
			
			// Waiting for response...
			boolean isAllDone = false;
			while(!isAllDone) {
				isAllDone = true;
				// Check all the future values
				for(int i = 0; i < futures.size(); i++) {
					if(futures.get(i).isDone()) {
						// Extract the results
						OperationResult opRes;
						try {
							if(futures.get(i).get() != null) {
								for(int j = 0; j < futures.get(i).get().size(); j++) {
									opRes = futures.get(i).get().get(j);
									results.get(opRes.operationId).addResult(opRes);
								}
							}
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else {
						isAllDone = false;
					}
				}
				// Sleep 1 second
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		threadPool.shutdown();
	}
}
