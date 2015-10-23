package dataEntryInterface;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.logging.*;

/**
 * 
 * @author Daniel
 *A wrapper class for basic file IO. 
 *Each instance is matched to one file.
 *Opening and closing the BufferedReader are built in to getLines()
 * Opening and closing the BufferedWriter is built in to write
 */
public class FileIO {

	private File file;
	private BufferedReader reader;
	private BufferedWriter writer;
	private static final Logger log = Logger.getLogger(FileIO.class.getName());
	
	/**
	 * 
	 * @param fileName The filename with which IO will be performed
	 * Gets a BufferedReader for the file
	 */
	public FileIO(String fileName)
	{
		file = new File(fileName);
	}
	
	
	/**
	 * 
	 */
	private Logger getLog() {
		return log;
	}
	
	/**
	 * 
	 * get a BufferedReader for the File file.
	 */
	private void getBufferedReader()
	{
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			getLog().warning("Error creating fileReader " + e);
		}
	}
	
	/**
	 * Closes the BufferedReader
	 */
	private void closeBufferedReader()
	{
		try {
			reader.close();
		} catch (IOException e) {
			getLog().warning("Error closing BufferedReader " + e);
		}
	}
	/**
	 * 
	 * @return ArrayList of every line from the file
	 * opens and closes the bufferedReader
	 */
	public ArrayList<String> getLines()
	{
		getBufferedReader();
		if (reader == null) {
			getLog().warning("File could not be found, so no lines can be read from the file");
			return null;
		}
		ArrayList<String> lineArray = new ArrayList<String>();
		String line;
		try {
			while ((line = reader.readLine()) != null)
			{
				lineArray.add(line);
			}
		}catch (IOException e) {
			getLog().warning("Error reading from file " + e);
		}
		closeBufferedReader();
		return lineArray;
	}
	
	
	private void getBufferedWriter(boolean append)
	{
		try {
			writer = new BufferedWriter(new FileWriter(file, append));
		} catch (IOException e) {
			getLog().warning("Error creating BufferedWriter "  +e);
		}
	}
	
	private void closeBufferedWriter()
	{
		try {
			writer.close();
		} catch (IOException e) {
			getLog().warning("Error closing BufferedWriter " + e);
		}
	}
	
	/**
	 * 
	 * @param lines An ArrayList where each element is printed on its own line
	 */
	public void write(ArrayList<String> lines, boolean append)
	{
		getBufferedWriter(append);
		try {
			for (String line: lines)
			{
				writer.write(line);
				writer.newLine();
			}
			
		} catch (IOException e) {
			getLog().warning("Error writing to file" + e);
		}
		closeBufferedWriter();
	}
	
}