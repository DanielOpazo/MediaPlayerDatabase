package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Hashtable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataEntryInterface.DataInterpreter;
import dataEntryInterface.FileIO;

public class DataInterpreterTest {

	DataInterpreter di;
	Hashtable<String, String> valuesHash;
	private FileIO fileIO;
	private ArrayList<String> lines;
	private final String titleKey = "title";
	@Before
	public void setUp() throws Exception {
		di = new DataInterpreter("test");
		valuesHash = new Hashtable<String, String>();
		fileIO = new FileIO("testLog.log");
		lines = new ArrayList<String>();
	}

	@After
	public void tearDown() throws Exception {
		fileIO.write(lines, true);
	}

	@Test
	public void readTitleEmptyTest() {
		valuesHash.clear();
		valuesHash.put(titleKey, "");
		di.getFileData().setValuesHash(valuesHash);
		String result = (di.readTitle() == null) ? "SUCCESS" : "FAIL";
		lines.add("readTitleEmptyTest: " + result);
		assertTrue(di.readTitle() == null);
	}
	
	@Test
	public void readTitleTooLongTest() {
		valuesHash.clear();
		valuesHash.put(titleKey, "Title of a song that is too long");
		di.getFileData().setValuesHash(valuesHash);
		String result = (di.readTitle() == null) ? "SUCCESS" : "FAIL";
		lines.add("readTitleTooLongTest: " + result);
		assertTrue(di.readTitle() == null);
	}
	
	@Test
	public void readTitleSuccessTest() {
		valuesHash.clear();
		valuesHash.put(titleKey, "Stairway to Heaven");
		di.getFileData().setValuesHash(valuesHash);
		String result = (di.readTitle().equalsIgnoreCase("Stairway to Heaven")) ? "SUCCESS" : "FAIL";
		lines.add("readTitleSuccessTest: " + result);
		assertTrue(di.readTitle().equalsIgnoreCase("Stairway to Heaven"));
	}

}
