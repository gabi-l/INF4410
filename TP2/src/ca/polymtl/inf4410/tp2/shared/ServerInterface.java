package ca.polymtl.inf4410.tp2.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
	
	public int signIn() throws RemoteException;
	
	/* Operations */
	public int fib(int x) throws RemoteException;
	public int prime(int x) throws RemoteException;
}
