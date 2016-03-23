package ca.polymtl.inf4410.tp2.server;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import ca.polymtl.inf4410.tp2.shared.ServerInterface;

public class Server implements ServerInterface {

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	public Server() {
		super();
	}

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry(5035);
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
