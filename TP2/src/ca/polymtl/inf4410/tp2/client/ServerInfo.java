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
import ca.polymtl.inf4410.tp2.shared.ServerOverloadException;

public class ServerInfo implements Callable<Vector<OperationResult>> {
	public String ip;
	public int port;
	public int id;
	public ServerInterface localServerStub = null;
	public ServerJob currentJob = null;	// Set dynamically by the client (list of operation to be executed on the server)
	
	public ServerInfo(String ip, int port, int id) {
		this.ip = ip;
		this.port = port;
		this.id = id;
		System.out.println("Initializing server: " + id);
		localServerStub = loadServerStub(this.ip, this.port);
	}
	
	/* Connect with the server */
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

	/* Execute the current set of operations on the given server. */
	public Vector<OperationResult> call() {
		Vector<OperationResult> result = new Vector<OperationResult>();
		OperationResult opRes;
		Vector<Integer> res;
		try {
			res = localServerStub.executeTask(currentJob.currentJob);
		} catch (RemoteException e) {
			System.out.println("Server " + this.id + " is dead.");
			currentJob.isDead = true;
			return null;
		} catch (ServerOverloadException e) {
			System.out.println("Server " + this.id + " overloaded.");
			currentJob.lastSucceed = false;
			return null;
		}
		if(res != null)
		{
			for(int i = 0; i < res.size(); i++) {
				opRes = new OperationResult(res.get(i), this.id, currentJob.currentJob.get(i).id);
				result.add(opRes);
			}
			return result;
		}
		return null;
	}
}