package application.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextInputDialog;

/**
 * MusicPlaylist. This is where custom user playlists are created and stored.
 * 
 * @author Rocky Robson
 * @version Dec 8, 2016
 */
public class MusicPlaylist {


	public static final String PLAYLIST_EXT = ".ply";
	public static final String PLAYLIST_PATH = "src/application/database/playlists/";
	public static final String PLAYLIST_DIR = "src/application/database/PlaylistDirectory.lib";

	private static String currentPlaylist;


	/**
	 * Retrieves the last playlist used from the application database if one has
	 * previously been created.
	 * 
	 * @return The last used playlist path as a string
	 */
	public static String retrieveCurrentPlaylist() {
		String directory = null;
		List<String> lines = new ArrayList<>();
		File playlistDirectory = new File(PLAYLIST_DIR);

		try {
			lines = Files.readAllLines(playlistDirectory.toPath());
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
	 * Saves the current playlist to the application database
	 */
	public static void saveCurrentPlaylistSelection() {
		File libraryDirectory = new File(PLAYLIST_DIR);

		try {
			FileWriter writer = new FileWriter(libraryDirectory, false);
			writer.write(currentPlaylist);
			writer.close();
		} catch (IOException e) {
			System.out.println("ERROR: Unable to write to file.");
			e.printStackTrace();
		}
	}


	/**
	 * Opens a dialog window where the user can input the name for a new
	 * playlist. When a valid name has been entered and the user pressed "Ok" a
	 * new playlist file is created and stored in the application database.
	 */
	public static void createPlaylist() {

		String playlistName = "new playlist";

		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Music Center");
		dialog.setHeaderText("Create a new Playlist");
		dialog.setContentText("Enter new playlist name: ");

		Optional<String> result = dialog.showAndWait();

		if (result.isPresent())
			playlistName = result.get();

		storePlaylist(playlistName);
	}


	/*
	 * Creates a new playlist file with the user's specified name
	 */
	private static void storePlaylist(String name) {

		File playlist = new File(PLAYLIST_PATH + name + PLAYLIST_EXT);

		try {
			playlist.createNewFile();
		} catch (IOException e) {
			System.out.println("Error: Unable to create playlist file");
			e.printStackTrace();
		}
	}


	/**
	 * Adds a file path to the current playlist
	 * 
	 * @param file The track to be added be added to the playlist
	 */
	public static void addToPlaylist(File file) {
		File playlist = new File(currentPlaylist);

		try {
			FileWriter writer = new FileWriter(playlist, true);
			writer.write(file.getPath() + "\n");
			writer.close();
		} catch (IOException e) {
			System.out.println("ERROR: Unable to write to file.");
			e.printStackTrace();
		}

	}


	/**
	 * Populates the menu of playlists from the list of playlists in the
	 * application database
	 * 
	 * @return All created playlists in the database as an ObservableList<File>
	 */
	public static ObservableList<File> populatePlaylistMenu() {

		ObservableList<File> playlists = FXCollections.observableArrayList();
		File directory = new File(PLAYLIST_PATH);

		playlists.addAll(directory.listFiles());

		return playlists;
	}


	/**
	 * Populates the playlist track view with all the tracks in the currently
	 * selected playlist.
	 * 
	 * @return The tracks in the currently selected playlist as an
	 *         ObservableList<File>
	 */
	public static ObservableList<File> populatePlaylistView() {
		ObservableList<File> playlist = FXCollections.observableArrayList();
		File directory = new File(currentPlaylist);

		if (directory.length() != 0) {
			try {
				FileReader fr = new FileReader(directory);
				BufferedReader reader = new BufferedReader(fr);

				while (reader.ready()) {
					playlist.add(new File(reader.readLine()));
				}

				reader.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		return playlist;
	}


	/**
	 * Sets file names from File and re-populates a ComboBox<File> with the
	 * formatted names.
	 * 
	 * @param list The ComboBox list view to be re-populated with File names
	 */
	public static void setFileNames(ComboBox<File> list) {

		list.setCellFactory(param -> new ListCell<File>() {


			@Override
			protected void updateItem(File file, boolean empty) {
				super.updateItem(file, empty);

				if (file != null) {
					String name = file.getName().replace(".ply", "");
					setText(name);
					setItem(file);
				}
			}
		});

		ListCell<File> cell = new ListCell<File>() {


			protected void updateItem(File file, boolean empty) {
				super.updateItem(file, empty);

				if (file != null) {
					String name = file.getName().replace(".ply", "");
					setText(name);
					setItem(file);
				}
			}
		};

		list.setButtonCell(cell);
	}


	/**
	 * Sets the user selected playlist to this.currentPlaylist which is used to
	 * populate the playlist view or when adding tracks to a playlist
	 * 
	 * @param file
	 */
	public static void setCurrentPlaylist(File file) {
		if (file != null)
			currentPlaylist = file.getPath();
	}

}
