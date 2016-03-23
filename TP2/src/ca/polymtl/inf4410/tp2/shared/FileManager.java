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
	
	public static void writeFile(CustomFile file) {
		Path cPath = Paths.get(path + file.fileName);
	    try {
	    	Files.deleteIfExists(cPath);
	    	if(file.data != null) {
	    		Files.write(cPath, file.data);
	    	}
	    	else {
	    		Files.createFile(cPath);
	    	}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> findFiles() {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		List<String> filesName = new Vector<String>();
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile() && listOfFiles[i].canRead() && !listOfFiles[i].canExecute()) {	// Excluding the file that can be execute to prevent changing their permission...
	    	  filesName.add(listOfFiles[i].getName());
	      }
	    }
	    return filesName;
	}
	
	public CustomFile readFile(String fileName) {
		try {
    		/* Read a file */
			byte[] data = Files.readAllBytes(Paths.get(path + fileName));
			CustomFile file = new CustomFile(fileName);
			file.data = new byte[data.length];
			for(int j = 0; j < data.length; j++) {
				file.data[j] = data[j];
			}
			return file;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}