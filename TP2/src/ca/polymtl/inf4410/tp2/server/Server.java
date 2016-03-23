package ca.polymtl.inf4410.tp2.server;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import ca.polymtl.inf4410.tp2.client.ServerInfo;
import ca.polymtl.inf4410.tp2.shared.FileManager;
import ca.polymtl.inf4410.tp2.shared.ServerInterface;

public class Server implements ServerInterface {

	public static void main(String[] args) {
		/* Retrieving the args */
		String configFileName = null;
		if (args.length > 0) {
			configFileName = args[0];
		}
		else {
			System.out.println("There were no command...");
			return;
		}
		Server server = new Server(configFileName);
		server.run();
	}

	private int port;
	private FileManager fileManager = null;
	
	public Server(String configFileName) {
		super();
		
		/* Initialize stuffs... */
		fileManager = new FileManager("./config_dir/");
		
		/* Extract the config */
		List<String> serverConfig = null;
		try {
			serverConfig = fileManager.readConfig(configFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		this.port = Integer.parseInt(serverConfig.get(0));
	}

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry(this.port);
			registry.rebind("server", stub);
			System.out.println("Server ready.");
		} catch (ConnectException e) {
			System.err
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}

	@Override
	public int signIn() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int fib(int x) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Fib was called...");
		return 42;
	}

	@Override
	public int prime(int x) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int isPrime(int x) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
}
