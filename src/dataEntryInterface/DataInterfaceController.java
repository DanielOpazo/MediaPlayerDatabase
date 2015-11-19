package dataEntryInterface;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import database.CoreDataAccess;

public class DataInterfaceController {

	private static final Logger log = Logger.getLogger(DataInterfaceController.class.getName());
	private final String metadataFileName = "Metadata.md";
	private final String finishedFileName = "Finish.md";
	private final String supportedMediaFileTypesGlob = "*.{mp3,mkv,mp4}";
	/*
	 * Windows paths
	 */
	private final String landingDirectoryFullPath = "C:\\Users\\Daniel\\workspace\\fileTests\\landingDir\\";
	private final String storageDirectoryFullPath = "C:\\Users\\Daniel\\workspace\\fileTests\\storageDir\\";
	/*
	 * Linux paths
	 */
	//private final String landingDirectoryFullPath = "/home/pi/sftp_dump/";
	//private final String storageDirectoryFullPath = "/home/pi/media_storage/";
	/**
	 * 
	 */
	private void readFileAndWriteToDatabase() {
		if (checkIfValidFile()) {
			CoreDataAccess cda = new CoreDataAccess();
			DataInterpreter di = new DataInterpreter(landingDirectoryFullPath + metadataFileName);
			di.getValuesFromFile();
			Path mediaFile = getMediaFileName(landingDirectoryFullPath);
			if (mediaFile != null) {
				try {
					Files.move(mediaFile, FileSystems.getDefault().getPath(storageDirectoryFullPath + mediaFile.getFileName().toString()), StandardCopyOption.ATOMIC_MOVE);
					di.getFileInfo().setFilePath(storageDirectoryFullPath + mediaFile.getFileName().toString());
					getLog().info(di.getFileInfo().getFilePath());
				} catch (IOException e) {
					getLog().log(Level.SEVERE, "Error transferring media file to storage directory", e);
				}
			}else {
				getLog().log(Level.SEVERE, "Media file is null");
			}
			
			try {
				Files.delete(FileSystems.getDefault().getPath(landingDirectoryFullPath + metadataFileName));
				Files.delete(FileSystems.getDefault().getPath(landingDirectoryFullPath + finishedFileName));
			}catch (Exception e) {
				getLog().log(Level.SEVERE, "Error deleting meta files", e);
			}
			
			
			if (di.getFileInfo().isValid()) {
				if (di.isSong()) {
					cda.insertSong((SongFileInfo) di.getFileInfo());
				}else {
					cda.insertVideo((VideoFileInfo) di.getFileInfo());
				}
				getLog().info("Media File is valid");
			}else {
				String reasons = "";
				if (di.getFileInfo().getTitle() == null) {
					reasons += "title is null ";
				}
				if (di.getFileInfo().getFilePath() == null){
					reasons += "filePath is null ";
				}
				getLog().log(Level.WARNING, "Not entering file into database because: " + reasons);
			}
		}
		
		
	}
	
	public Path getMediaFileName(String directory) {
		Path dir = FileSystems.getDefault().getPath(directory);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, supportedMediaFileTypesGlob)) {
			for (Path file: stream) {
				getLog().log(Level.INFO, file.getFileName().toString());
				return file;
			}
		}catch (IOException | DirectoryIteratorException e) {
			getLog().log(Level.SEVERE, "Error with directory iterator", e);
		}
		return null;

	}
	
	/**
	 * check if there is a metadata file.
	 * Then check if the finished file is there.
	 */
	private boolean checkIfValidFile() {
		boolean valid = false;
		try {
			new FileReader(landingDirectoryFullPath + metadataFileName).close();;
			new FileReader(landingDirectoryFullPath + finishedFileName).close();;
			valid = true;
		} catch (FileNotFoundException e) {
			//Don't log the exception because most of the time when this is called, there will be no file
			//getLog().log(Level.INFO, "No file found");
		}catch (IOException e) {
			getLog().log(Level.SEVERE, "problem closing file readers", e);
		}
		return valid;
	}
	
	public static Logger getLog() {
		return log;
	}

	public static void main(String[] args) {
		DataInterfaceController dc = new DataInterfaceController();
		while (true) {
			dc.readFileAndWriteToDatabase();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				getLog().log(Level.SEVERE, "DataInterfaceController thread got interrupted", e);
			}
		}
		

	}

}
