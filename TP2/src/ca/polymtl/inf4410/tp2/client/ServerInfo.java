package ca.polymtl.inf4410.tp2.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import ca.polymtl.inf4410.tp2.shared.ServerInterface;

public class ServerInfo {
	public String ip;
	public int port;
	public ServerInterface localServerStub = null;
	
	public ServerInfo(String ip, int port) {
		this.ip = ip;
		this.port = port;
		
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
}