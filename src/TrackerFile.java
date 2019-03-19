import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class TrackerFile {
	
	private BufferedReader br;
	private String fileName;
	
	public TrackerFile(String fileName) throws FileNotFoundException {
		File f = new File(fileName);
		if (!f.exists() || f.isDirectory() || !fileName.contains("MyNotes.txt")) throw new FileNotFoundException();
		
		this.fileName = fileName;
	}
	
	public String readLastLine() throws IOException {
		String currentLine, lastLine = "";
		br = new BufferedReader(new FileReader(fileName));
		// Keep reading until the last line.
	    while ((currentLine = br.readLine()) != null) {
	        lastLine = currentLine;
	    }
	    // Delete file contents.
		new PrintWriter(fileName).close();
		return lastLine;
	}

}
