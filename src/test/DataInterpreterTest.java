package test;

import static org.junit.Assert.*;

import java.util.Hashtable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataEntryInterface.DataInterpreter;

public class DataInterpreterTest {

	DataInterpreter di;
	Hashtable<String, String> valuesHash;
	private final String titleKey = "title";
	@Before
	public void setUp() throws Exception {
		di = new DataInterpreter("test");
		valuesHash = new Hashtable<String, String>();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void readTitleEmptyTest() {
		valuesHash.clear();
		valuesHash.put(titleKey, "");
		di.getFileData().setValuesHash(valuesHash);
		assertTrue(di.readTitle() == null);
	}
	
	@Test
	public void readTitleTooLongTest() {
		valuesHash.clear();
		valuesHash.put(titleKey, "Title of a song that is too long");
		di.getFileData().setValuesHash(valuesHash);
		assertTrue(di.readTitle() == null);
	}
	
	@Test
	public void readTitleSuccessTest() {
		valuesHash.clear();
		valuesHash.put(titleKey, "Stairway to Heaven");
		di.getFileData().setValuesHash(valuesHash);
		assertTrue(di.readTitle().equalsIgnoreCase("Stairway to Heaven"));
	}

}
