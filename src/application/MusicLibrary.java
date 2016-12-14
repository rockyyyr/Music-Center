package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

/**
 * MusicLibrary. This class contains static methods used for importing a library
 * directory and proper displaying of its contents.
 * 
 * @author Rocky Robson - A00914509
 * @version Dec 8, 2016
 */
public class MusicLibrary {


	/**
	 * File path where the library directory is stored
	 */
	public static final String LIBRARY_DIRECTORY = "LibraryDirectory.lib";


	/**
	 * Retrieves the users music library directory path if one has been set
	 * 
	 * @return The users music library directory path as a string or null if a
	 *         library hasn't been set
	 */
	public static String retrieveLibraryDirectory() {

		String directory = null;
		List<String> lines = new ArrayList<>();
		File libraryDirectory = new File(LIBRARY_DIRECTORY);

		try {
			lines = Files.readAllLines(libraryDirectory.toPath());
		} catch (IOException e) {
			System.out.println("ERROR: Unable to read file.");
			e.printStackTrace();
		}

		if (lines != null && lines.isEmpty() == false) {
			directory = lines.get(0);
		}

		return directory;
	}


	/**
	 * Saves the directory path of the users music library to a local file
	 * 
	 * @param directoryPath The users music library directory path
	 */
	public static void storeLibraryDirectory(String directoryPath) {

		File libraryDirectory = new File(LIBRARY_DIRECTORY);

		try {
			FileWriter writer = new FileWriter(libraryDirectory, false);
			writer.write(directoryPath);
			writer.close();
		} catch (IOException e) {
			System.out.println("ERROR: Unable to write to file.");
			e.printStackTrace();
		}
	}


	/**
	 * Opens a dialog window where the user can select their music library
	 * 
	 * @param primaryStage The primary stage of the interface
	 */
	public static ObservableList<File> showDialogWindow(Window primaryStage) {

		DirectoryChooser directoryChooser = new DirectoryChooser();
		File directory = directoryChooser.showDialog(primaryStage);

		return populateArtistList(directory);
	}


	/**
	 * Opens a window where the user is prompted to choose the directory
	 * containing their music files.
	 * 
	 * @param primaryStage The main stage used for this user interface.
	 * @return An observable list of folders containing music files. This list
	 *         is used to populate the artist list view
	 */
	public static ObservableList<File> populateArtistList(File directory) {

		storeLibraryDirectory(directory.getPath());

		File[] files = directory.listFiles();
		ObservableList<File> fileList = FXCollections.observableArrayList();

		for (int i = 0; i < files.length; i++)
			if (files[i] != null && files[i].isDirectory())
				fileList.add(files[i]);

		return fileList;
	}


	/**
	 * Populates an observable list with files from a directory file.
	 * 
	 * @param file The directory that contains music files
	 * @return An ObesrvableList<File> containing music files
	 */
	public static ObservableList<File> populateTrackList(File file) {

		ObservableList<File> trackList = FXCollections.observableArrayList();

		if (file != null && file.isDirectory()) {

			File[] fileList = file.listFiles();

			for (int i = 0; i < fileList.length; i++) {

				if (MusicPlayer.isAcceptableFileType(fileList[i]))
					trackList.add(fileList[i]);

			}
		}
		return trackList;
	}


	/**
	 * Sets file names from File and re-populates a ListView<File> with the
	 * formatted names.
	 * 
	 * @param list The list to be re-populated with File names
	 */
	public static void setFileNames(ListView<File> list) {

		list.setCellFactory(param -> new ListCell<File>() {


			@Override
			protected void updateItem(File file, boolean empty) {
				super.updateItem(file, empty);

				if (file != null) {

					if (file.isDirectory()) {
						setText(file.getName());
						setItem(file);

					} else if (file.isFile()) {
						setText(file.getName());
						setItem(file);
					}
				}
			}
		});
	}


	/**
	 * Returns a jpeg File from a directory containing multiple file types.
	 * 
	 * @param file The directory containing a jpeg used for album art
	 * @return An Image object containing album art
	 */
	public static Image setAlbumArt(File file) {

		String path = "application/Music_Library.png";
		File[] fileList = file.listFiles();

		for (int i = 0; i < fileList.length; i++) {

			if (isJPEG(fileList[i].getPath())) {

				try {
					path = fileList[i].toURI().toURL().toString();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		return new Image(path);
	}


	/**
	 * Checks to see if this parameter file is a jpeg. The file is checked using
	 * it's path
	 * 
	 * @param filePath The file path to be checked.
	 * @return True if and only if the file is a jpeg
	 */
	public static boolean isJPEG(String filePath) {
		return FilenameUtils.isExtension(filePath, "jpg");
	}

}
