package ca.polymtl.inf4410.tp2.shared;

public class ServerOverloadException extends Exception{
	public ServerOverloadException() {}
	
	public ServerOverloadException(String message) {
		super(message);
	}
}
