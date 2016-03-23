package ca.polymtl.inf4410.tp2.shared;

public class AlreadyLockException extends Exception {
	public AlreadyLockException() {}
	
	public AlreadyLockException(String message) {
		super(message);
	}
}