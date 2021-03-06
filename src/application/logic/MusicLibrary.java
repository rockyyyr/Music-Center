package application.logic;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.commons.io.FilenameUtils;

import application.database.Database;
import application.utils.MetaDataParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

/**
 * MusicLibrary. This class contains static methods used for importing a library
 * directory and proper displaying of its contents.
 * 
 * @author Rocky Robson
 * @version Dec 8, 2016
 */
public class MusicLibrary {


	/**
	 * File path where the library directory is stored
	 */
	public static final String LIBRARY_DIRECTORY = "src/application/database/LibraryDirectory.lib";


	/**
	 * Retrieves the users music library directory path if one has been set
	 * 
	 * @return The users music library directory path as a string or null if a
	 *         library hasn't been set
	 */
	public static String retrieveLibraryDirectory() {
		return Database.retrieveLibraryDirectory();
	}


	/**
	 * Saves the directory path of the users music library to a local file
	 * 
	 * @param directoryPath The users music library directory path
	 */
	public static void storeLibraryDirectory(String directoryPath) {

		if (directoryPath != null && directoryPath.trim().length() > 0)
			Database.saveLibraryDirectory(directoryPath);
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
	 * Populates the artist list using the directory to the music library
	 * 
	 * @param file The directory which contains the music library
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
	public static void setFileNames(ListView<File> list, boolean isPlaylist) {

		list.setCellFactory(param -> new ListCell<File>() {


			@Override
			protected void updateItem(File file, boolean empty) {
				super.updateItem(file, empty);

				if (file != null) {

					if (file.isDirectory()) {
						setText(file.getName());
						setItem(file);

					} else if (file.isFile() && isPlaylist == false) {
						setText(MetaDataParser.getTitle(file));
						setItem(file);

					} else if (isPlaylist) {
						setText(MetaDataParser.getArtist(file) + " - " + MetaDataParser.getTitle(file));
						setItem(file);
					}
				}
			}
		});
	}


	/**
	 * Sets the right click context menu for the tracklist list view. When the
	 * user right clicks on a track an option to add a track to the current
	 * playlist is presented
	 * 
	 * @param list The listview where the context menu will be set
	 * @param playlist The playlist where the selected track will be added
	 */
	public static void setContextMenu(ListView<File> list, ListView<File> playlist) {

		MenuItem addPlaylist = new MenuItem();
		addPlaylist.setText("Add to playlist");

		addPlaylist.setOnAction(new EventHandler<ActionEvent>() {


			@Override
			public void handle(ActionEvent e) {
				MusicPlaylist.addToPlaylist(list.getSelectionModel().getSelectedItem());
				playlist.setItems(MusicPlaylist.populatePlaylistView());
			}

		});

		list.setContextMenu(new ContextMenu(addPlaylist));
	}


	/**
	 * Returns a jpeg File from a directory containing multiple file types.
	 * 
	 * @param file The directory containing a jpeg used for album art
	 * @return An Image object containing album art
	 */
	public static Image setAlbumArt(File file) {

		String path = "images/Music_Library.png";
		File[] fileList = file.listFiles();

		for (int i = 0; i < fileList.length; i++) {

			if (isJPEG(fileList[i].getPath())) {

				try {
					path = fileList[i].toURI().toURL().toString();
				} catch (MalformedURLException e) {
					System.out.println("Error: Unable to load album art");
					e.printStackTrace();
				}
			}
		}

		return new Image(path);
	}


	/**
	 * Checks to see if this parameter file is a jpeg.
	 * 
	 * @param filePath The file path to be checked.
	 * @return True if and only if the file is a jpeg
	 */
	public static boolean isJPEG(String filePath) {
		return FilenameUtils.isExtension(filePath, "jpg");
	}

}
