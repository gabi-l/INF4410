package ca.polymtl.inf4410.tp2.shared;

public class AlreadyExistingFileException extends Exception {
	public AlreadyExistingFileException() {}
	
	public AlreadyExistingFileException(String message) {
		super(message);
	}
}