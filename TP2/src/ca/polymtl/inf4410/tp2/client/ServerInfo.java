package ca.polymtl.inf4410.tp2.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Callable;

import ca.polymtl.inf4410.tp2.shared.OperationInfo;
import ca.polymtl.inf4410.tp2.shared.OperationResult;
import ca.polymtl.inf4410.tp2.shared.ServerInterface;

public class ServerInfo implements Callable<OperationResult> {
	public String ip;
	public int port;
	public int id;
	public ServerInterface localServerStub = null;
	public OperationInfo currentOperation = null;
	
	public ServerInfo(String ip, int port, int id) {
		this.ip = ip;
		this.port = port;
		this.id = id;
		System.out.println("ServerStub..");
		localServerStub = loadServerStub(this.ip, this.port);
	}
	
	private ServerInterface loadServerStub(String hostname, int port) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname, port);
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

	public OperationResult call() throws Exception {
		System.out.println("Calling...");
		int result;
		if( currentOperation.command.equals("fib")) {
			result = localServerStub.fib(1);
		}
		else {
			result = 0;
		}
		OperationResult operationResult = new OperationResult(result, id);
		return operationResult;
	}
}