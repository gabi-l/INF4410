package ca.polymtl.inf4410.tp2.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

import ca.polymtl.inf4410.tp2.shared.ServerInterface;
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
// TODO: Callable...isDone
	private boolean isValidState = false;
	private Vector<Vector<OperationResult>> results = null;
	private FileManager fileManager = null;
	private Vector<ServerInfo> serverList = null;
	private Vector<OperationInfo> operationList = null;
	private int serverCount = 0;
	private ExecutorService threadPool = null;
	private Vector<Future<OperationResult>> futures = null;

	public Client(String clientConfigFile, String donneeFile) {
		super();
		
		/* Initialize stuffs... */
		fileManager = new FileManager("./config_dir/");
		serverList = new Vector<ServerInfo>();
		operationList = new Vector<OperationInfo>();
		threadPool = Executors.newFixedThreadPool(3);
		futures = new Vector<Future<OperationResult>>();
		
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
			OperationInfo oi = new OperationInfo(command, operand);
			operationList.add(oi);
		}
		
		// Initialize the vector of results
		results = new Vector<Vector<OperationResult>>();
		for(int i = 0; i < operationList.size(); i++) {
			Vector<OperationResult> resultList = new Vector<OperationResult>();
			results.add(resultList);
		}		
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		isValidState = true;
	}
	
	private void run() {
		
		// Starting the threads
		OperationInfo operation = new OperationInfo("fib","1");
		for(int i = 0; i < serverList.size(); i++ ) {
			serverList.get(i).currentOperation = operation;
			Callable<OperationResult> callable = serverList.get(i);
			Future<OperationResult> future = threadPool.submit(callable);
			futures.add(future);
		}
		
		// Waiting for response...
		int isAllDone = 0;
		while(isAllDone < futures.size()) {
			for(int i = 0; i < futures.size(); i++) {
				if(futures.get(i).isDone()) {
					isAllDone++;
					OperationResult opRes;
					try {
						opRes = futures.get(i).get();
						results.get(0).add(opRes);
						System.out.println("Result:" + opRes.result);
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
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
