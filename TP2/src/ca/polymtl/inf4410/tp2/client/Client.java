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
		client.run();
	}
// TODO: Callable...isDone
	private boolean isValidState = false;
	private ServerInterface localServerStub = null;
	private Vector<ArrayList<OperationResult>> results = null;
	private FileManager fileManager = null;
	private Vector<ServerInfo> serverList = null;
	private Vector<OperationInfo> operationList = null;

	public Client(String clientConfigFile, String donneeFile) {
		super();
		
		/* Initialize stuffs... */
		fileManager = new FileManager("./config_dir/");
		serverList = new Vector<ServerInfo>();
		operationList = new Vector<OperationInfo>();
		
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
			ServerInfo si = new ServerInfo(ip, port);
			serverList.add(si);
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
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		isValidState = true;
	}
	
	private void run() {
		
		/* Execute the right cmd */
		try {
			int allo = localServerStub.fib(1);
			System.out.println("Result receive: " + allo);
		}
		catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
			return;
		}
		
	}

	
	
	
/*try {
	isSucces = localServerStub.push(fileName, dataToSend, clientId);
}
catch (RemoteException e) {
	System.out.println("Erreur: " + e.getMessage());
	return;
}*/
}
