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


public class Client {
	public static void main(String[] args) {
		/* Retrieving the cmd and the arg*/
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

	public Client(String clientConfigFile, String donneeFile) {
		super();
		
		/* Initialize stuffs... */
		fileManager = new FileManager("./config_dir");
		serverList = new Vector<ServerInfo>();
		
		/* Extract the config */
		List<String> clientConfig;
		try {
			clientConfig = fileManager.readClientConfig("client1.config");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		for(int i = 0; i < clientConfig.size(); i++) {
			String line = clientConfig.get(i);
			line.substring(0, line.indexOf(" "));
			
		}
		
		
		List<String> operations = fileManager.readOperations("donnees-2317.txt");
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		localServerStub = loadServerStub("132.207.12.43");
		
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

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname, 5035);
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}
	
	
/*try {
	isSucces = localServerStub.push(fileName, dataToSend, clientId);
}
catch (RemoteException e) {
	System.out.println("Erreur: " + e.getMessage());
	return;
}*/
}
