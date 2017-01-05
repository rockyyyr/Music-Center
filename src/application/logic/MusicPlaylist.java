package application.logic;

import java.io.File;
import java.util.Optional;

import application.database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;

/**
 * MusicPlaylist. This is where custom user playlists are created and stored.
 * 
 * @author Rocky Robson
 * @version Dec 8, 2016
 */
public class MusicPlaylist {


	public static final String PLAYLIST_EXT = ".ply";
	public static final String PLAYLIST_PATH = "src/application/database/";
	public static final String PLAYLIST_DIR = "src/application/database/PlaylistDirectory.lib";

	/*
	 * The currently selected playlist name. This playlist name is used when
	 * retrieving last used playlist on start up, saving last used playlist on
	 * shutdown and other playlist utilities
	 */
	private static String currentPlaylist;


	/**
	 * Retrieves the last playlist used from the application database if one has
	 * previously been created.
	 * 
	 * @return The last used playlist path as a string
	 */
	public static String retrieveCurrentPlaylist() {
		return Database.retrievePlaylistDirectory();
	}


	/**
	 * Saves the current playlist to the application database. This runs on
	 * application shut down so that on the next application start up the
	 * current playlist is retrieved.
	 */
	public static void saveCurrentPlaylistSelection() {

		if (currentPlaylist != null)
			Database.savePlaylistDirectory(currentPlaylist);
	}


	/**
	 * Opens a dialog window where the user can input the name for a new
	 * playlist.
	 * 
	 * When a valid name has been entered and the user pressed "Ok" a new
	 * playlist file is created and stored in the application database.
	 */
	public static void openPlaylistCreationDialog() {

		String playlistName = "new playlist";

		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Music Center");
		dialog.setHeaderText("Create a new Playlist");
		dialog.setContentText("Enter new playlist name: ");

		Optional<String> result = dialog.showAndWait();

		if (result.isPresent())
			playlistName = result.get();

		storePlaylist(playlistName);
		setCurrentPlaylist(playlistName);
	}


	/*
	 * Creates a new playlist file with the user's specified name
	 */
	private static void storePlaylist(String name) {
		Database.createPlaylist(name);

	}


	/**
	 * Adds a file path to the current playlist
	 * 
	 * @param file The track to be added be added to the playlist
	 */
	public static void addToPlaylist(File file) {
		if (file != null) {
			Database.addToPlaylist(currentPlaylist, file.getPath());
		}
	}


	/**
	 * Removes the specified track from the current playlist. This feature is
	 * access through the right click context menu
	 * 
	 * @param file The track to be removed
	 */
	public static void removeTrackFromPlaylist(File file) {
		if (file != null) {
			Database.deleteFromPlaylist(currentPlaylist, file.getPath());
		}
	}


	/**
	 * Populates the menu of playlists from the list of playlists in the
	 * application database
	 * 
	 * @return All created playlists in the database as an ObservableList<File>
	 */
	public static ObservableList<String> populatePlaylistMenu() {

		ObservableList<String> playlists = FXCollections.observableArrayList();
		String[] tables = Database.listAllPlaylists();

		if (tables.length > 0) {
			for (String playlist : tables)
				playlists.add(playlist);
		}
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
		String[] tracks = Database.retrievePlaylist(currentPlaylist);

		for (String track : tracks) {
			playlist.add(new File(track));
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
	 * @param playlistName The name of the playlist to set
	 */
	public static void setCurrentPlaylist(String playlistName) {
		if (playlistName != null)
			currentPlaylist = playlistName;
	}


	/**
	 * Sets up the right click context menu for the playlist list view. When the
	 * user right clicks on a track, the option to remove the track from the
	 * playlist is presented
	 * 
	 * @param playlist The listview where this context menu will be set
	 */
	public static void setContextMenu(ListView<File> playlist) {

		MenuItem addPlaylist = new MenuItem();
		addPlaylist.setText("Remove from playlist");

		addPlaylist.setOnAction(new EventHandler<ActionEvent>() {


			@Override
			public void handle(ActionEvent e) {
				removeTrackFromPlaylist(playlist.getSelectionModel().getSelectedItem());
				playlist.setItems(null);
				playlist.setItems(populatePlaylistView());
			}
		});

		playlist.setContextMenu(new ContextMenu(addPlaylist));

	}

}
