package application.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextInputDialog;

/**
 * MusicPlaylist. This is where custom user playlists are stored
 * 
 * @author Rocky Robson
 * @version Dec 8, 2016
 */
public class MusicPlaylist {


	public static final String PLAYLIST_EXT = ".ply";
	public static final String PLAYLIST_PATH = "src/application/database/playlists/";

	private static String currentPlaylist;


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


	private static void storePlaylist(String name) {

		File playlist = new File(PLAYLIST_PATH + name + PLAYLIST_EXT);

		try {
			playlist.createNewFile();
		} catch (IOException e) {
			System.out.println("Error: Unable to create playlist file");
			e.printStackTrace();
		}
	}


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


	public static ObservableList<File> populatePlaylistMenu() {

		ObservableList<File> playlists = FXCollections.observableArrayList();
		File directory = new File(PLAYLIST_PATH);

		playlists.addAll(directory.listFiles());

		return playlists;
	}


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


	public static void setCurrentPlaylist(File file) {
		if (file != null)
			currentPlaylist = file.getPath();

	}

}
