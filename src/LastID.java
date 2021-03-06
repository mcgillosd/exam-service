/*
 * LastID.java
 * 
 * Created on 2013-06-11 12:00:10 PM
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Reads a file with the last id from the previous update and
 * writes to the same file a new id after update was done 
 * 
 * @author Olga Tsibulevskaya
 */
public class LastID {
	//private final File idfile = new File("id.txt");
	private final File idfile = new File("F:\\Exams\\Files\\id.txt");
	private int id;
	
	/**
	 * Creates the last id by reading the file 
	 * @throws FileNotFoundException 
	 */
	public LastID() throws FileNotFoundException {
		if (! idfile.exists()) {
			new Message("File id.txt doesn't exists");
			throw new FileNotFoundException();
		}
		else {
			BufferedReader br = null;
			try {
				String line;
				br = new BufferedReader(new FileReader(idfile));
	 
				if ((line = br.readLine()) != null) {
					id = Integer.parseInt(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public int getID() {
		return id;
	}
	public File getIDFile() {
		return idfile;
	}
	public void setLastID(int id) {
		this.id = id;
		writeID();
	}
	/* Writes the new id to the file after update is done */
	private void writeID() {
		try {
			Process p = Runtime.getRuntime().exec("attrib -h \"" + idfile.getPath() + "\"");
			p.waitFor();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		BufferedWriter br = null;
		try {
			br = new BufferedWriter(new FileWriter(idfile));
			br.write(new Integer(id).toString());
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			if (br != null) {
				try {
					br.flush();
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			Process p = Runtime.getRuntime().exec("attrib +h \"" + idfile.getPath() + "\"");
			p.waitFor();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
}
