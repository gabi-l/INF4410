package ca.polymtl.inf4410.tp2.shared;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;

public class FileManager {
	
	private static String path;
	
	public FileManager(String path) {
		this.path = path;
	}
	
	public List<String> readConfig(String fileName) throws IOException {
		return Files.readAllLines(Paths.get(path + "config/" + fileName), StandardCharsets.US_ASCII);	
	}
	
	public List<String> readOperations(String fileName) throws IOException {
		return Files.readAllLines(Paths.get(path + "donnees/" + fileName), StandardCharsets.US_ASCII);
	}
	
}