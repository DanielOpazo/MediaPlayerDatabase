package dataEntryInterface;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Daniel
 * Class that reads the metadata sent in a file from the Data Entry Program
 * For each line that is the proper format, an entry is created in the HashTable
 * Proper format for a line is:
 * <key><value> 
 */
public class DataFromFile {

	private FileIO file;
	private Hashtable<String, String> valuesHash;
	private static final Logger log = Logger.getLogger(DataFromFile.class.getName());
	private final String regexPattern = "<(\\w+)><(.*)>";
	
	public DataFromFile(String fileName) {
		valuesHash = new Hashtable<String, String>();
		file = new FileIO(fileName);
		getFileData(valuesHash, file);
	}
	
	private Logger getLog() {
		return log;
	}
	
	/**
	 * Reads the input file and sorts each line into the valuesHash field.
	 * @param returnTable Hash where the contents of the file are stored
	 * @param file The file with the information to be read
	 */
	void getFileData(Hashtable<String, String> returnTable, FileIO file) {
		ArrayList<String> lines = null;
		lines = file.getLines();
		if (lines == null) {
			getLog().warning("Could not read lines from file");
		}else {
			Pattern basicPattern = Pattern.compile(regexPattern);
			for (String line: lines) {
				Matcher matcher = basicPattern.matcher(line);
				if (matcher.find()) {
					returnTable.put(matcher.group(1), matcher.group(2));
				}else {
					getLog().warning("pattern " + line + "did not match the regex");
				}
			}
		}
	}
		
	public Hashtable<String, String> getValuesHash() {
			return valuesHash;
		}

	public void setValuesHash(Hashtable<String, String> hash) {
		this.valuesHash = hash;
	}
	
	public static void main(String[] args) {
		DataFromFile dff = new DataFromFile("C:\\Users\\Daniel\\workspace\\DatabaseProgram\\metadata.md");
		System.out.println(dff.getValuesHash().toString());
		

	}

}
