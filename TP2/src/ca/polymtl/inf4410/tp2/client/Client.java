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
		System.out.println("Running...");
		client.run();
		System.out.println("Finishing...");
		return;
	}
	
	// Common stuff of the client
	private FileManager fileManager = null;
	private Vector<ServerInfo> serverList = null;
	private int serverCount = 0;
	
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
		for(int i = 0; i < clientConfig.size(); i++) {
			String line = clientConfig.get(i);
			String ip = line.substring(0, line.indexOf(" "));
			int port = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
			ServerInfo si = new ServerInfo(ip, port, i);
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
			ResultGroup resultGroup = new ResultGroup(serverCount);
			results.add(resultGroup);
		}
		
		// Initialize the ServerJob vector
		serverJob = new Vector<ServerJob>();
		for(int i = 0; i < serverCount; i++) {
			ServerJob sj = new ServerJob(operationList.size(), i);
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
	
	private void run() {
		boolean wait = false;
		while(!isAllResultValid()) {
			// Starting the threads
			if(!wait) {
				for(int i = 0; i < serverCount; i++ ) {
					serverJob.get(i).setNewJob(results, operationList);
					serverList.get(i).currentJob = serverJob.get(i);
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
							for(int j = 0; j < futures.get(i).get().size(); j++) {
								opRes = futures.get(i).get().get(j);
								results.get(opRes.operationId).addResult(opRes);
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
						System.out.println("Is not done: " + i);
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
		System.out.println("Je suis sortie...");
	}
	
/*try {
	isSucces = localServerStub.push(fileName, dataToSend, clientId);
}
catch (RemoteException e) {
	System.out.println("Erreur: " + e.getMessage());
	return;
}*/
}
