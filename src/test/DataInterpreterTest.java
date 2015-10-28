package test;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.joda.time.DateTimeComparator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataEntryInterface.DataInterpreter;

public class DataInterpreterTest {

	DataInterpreter di;
	Hashtable<String, String> valuesHash;
	private final String titleKey = "title";
	private final String artistKey = "artist";
	private final String albumKey = "album";
	private final String trackKey = "track";
	private final String dateKey = "date";
	
	@Before
	public void setUp() throws Exception {
		di = new DataInterpreter("test");
		valuesHash = new Hashtable<String, String>();
		di.getFileData().setValuesHash(valuesHash);
	}

	@After
	public void tearDown() throws Exception {
		valuesHash.clear();
	}

	/*----------------------------------------------------------------------------------
	 * Title tests
	 */
	@Test
	public void readTitleEmptyTest() {
		di.getFileData().getValuesHash().put(titleKey, "");
		assertTrue(di.readTitle() == null);
	}
	
	@Test
	public void readTitleMissingTest() {
		assertTrue(di.readTitle() == null);
	}
	
	@Test
	public void readTitleTooLongTest() {
		di.getFileData().getValuesHash().put(titleKey, "Title of a song that is too long");
		assertTrue(di.readTitle() == null);
	}
	
	@Test
	public void readTitleSuccessTest() {
		di.getFileData().getValuesHash().put(titleKey, "Stairway to Heaven");
		assertTrue(di.readTitle().equals("Stairway to Heaven"));
	}
	
	/*----------------------------------------------------------------------------------
	 * Artist tests	
	 */
	@Test
	public void readArtistEmptyTest() {
		di.getFileData().getValuesHash().put(artistKey, "");
		assertTrue(di.readArtist().equalsIgnoreCase("Unknown"));
	}
	
	@Test
	public void readArtistMissingTest() {
		assertTrue(di.readArtist().equalsIgnoreCase("Unknown"));
	}
	
	@Test
	public void readArtistSuccessTest() {
		di.getFileData().getValuesHash().put(artistKey, "The Beatles");
		assertTrue(di.readArtist().equals("The Beatles"));
	}
	
	/*----------------------------------------------------------------------------------
	 * Album tests
	 */
	@Test
	public void readAlbumEmptyTest() {
		di.getFileData().getValuesHash().put(albumKey, "");
		assertTrue(di.readAlbum().equalsIgnoreCase("Unknown"));
	}
	
	@Test
	public void readAlbumMissingTest() {
		assertTrue(di.readAlbum().equalsIgnoreCase("Unknown"));
	}
	
	@Test
	public void readAlbumSuccessTest() {
		di.getFileData().getValuesHash().put(albumKey, "The White Album");
		assertTrue(di.readAlbum().equalsIgnoreCase("The White Album"));
	}
	
	/*----------------------------------------------------------------------------------
	 * Track tests
	 */
	
	@Test
	public void readTrackEmptyTest() {
		di.getFileData().getValuesHash().put(trackKey, "");
		assertTrue(di.readTrack() == 0);
	}
	
	@Test
	public void readTrackMissingTest() {
		assertTrue(di.readTrack() == 0);
	}
	
	@Test
	public void readTrackTooLow() {
		di.getFileData().getValuesHash().put(trackKey, "-2");
		assertTrue(di.readTrack() == 0);
	}
	
	@Test
	public void readTrackZero() {
		di.getFileData().getValuesHash().put(trackKey, "0");
		assertTrue(di.readTrack() == 0);
	}
	
	@Test
	public void readTrackNotANumber() {
		di.getFileData().getValuesHash().put(trackKey, "two");
		assertTrue(di.readTrack() == 0);
	}
	
	@Test
	public void readTrackSuccess() {
		di.getFileData().getValuesHash().put(trackKey, "3");
		assertTrue(di.readTrack() == 3);
	}
	
	/*----------------------------------------------------------------------------------
	 * Date tests
	 */
	@Test
	public void readDateEmptyTest() {
		di.getFileData().getValuesHash().put(dateKey, "");
		assertTrue(di.readDate() == null);
	}
	
	@Test
	public void readDateMissingTest() {
		assertTrue(di.readDate() == null);
	}
	
	@Test
	public void readDateBadFormatTest() {
		di.getFileData().getValuesHash().put(dateKey, "Wednesday October 28, 2015");
		assertTrue(di.readDate() == null);
	}
	
	@Test
	public void readDateSuccessTest() {
		di.getFileData().getValuesHash().put(dateKey, "2015-10-28");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date releaseDate = null;
		try {
			releaseDate = format.parse(("2015-10-28"));
		} catch (ParseException e) {
			System.out.println("This shouldn't happen since the date value was manually added");
		}
		assertTrue(DateTimeComparator.getDateOnlyInstance().compare(releaseDate, di.readDate()) == 0);
	}
}
