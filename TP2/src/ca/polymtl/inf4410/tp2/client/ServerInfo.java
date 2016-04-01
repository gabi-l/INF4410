package ca.polymtl.inf4410.tp2.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Callable;
import java.util.Vector;

import ca.polymtl.inf4410.tp2.shared.OperationResult;
import ca.polymtl.inf4410.tp2.shared.ServerInterface;

public class ServerInfo implements Callable<Vector<OperationResult>> {
	public String ip;
	public int port;
	public int id;
	public ServerInterface localServerStub = null;
	public ServerJob currentJob = null;
	
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

	public Vector<OperationResult> call() throws Exception {
		System.out.println("Calling...");
		Vector<OperationResult> result = new Vector<OperationResult>();
		OperationResult opRes;
		int res;
		for(int i = 0; i < currentJob.currentJob.size(); i++) {
			res = localServerStub.fib(1);
			opRes = new OperationResult(res, this.id, currentJob.currentJob.get(i).id);
			result.add(opRes);
		}
		return result;
	}
}