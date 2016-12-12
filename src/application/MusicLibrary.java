package application;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.commons.io.FilenameUtils;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

/**
 * MusicLibrary. This class chooses the users local music folder and prepares
 * them for playback in a MusicPlayer.
 * 
 * @author Rocky Robson - A00914509
 * @version Dec 8, 2016
 */
public class MusicLibrary {

	public static ObservableList<File> populateArtistList(Window primaryStage) {

		DirectoryChooser directoryChooser = new DirectoryChooser();
		File directory = directoryChooser.showDialog(primaryStage);

		File[] files = directory.listFiles();
		ObservableList<File> fileList = FXCollections.observableArrayList();

		for (int i = 0; i < files.length; i++)
			if (files[i] != null && files[i].isDirectory())
				fileList.add(files[i]);

		return fileList;
	}


	public static ObservableList<MediaPlayer> createTrackMedia(File file) {

		ObservableList<MediaPlayer> mediaPlayers = FXCollections.observableArrayList();

		if (file != null && file.isDirectory()) {

			File[] fileList = file.listFiles();

			for (int i = 0; i < fileList.length; i++) {

				if (fileList[i] != null) {

					try {
						mediaPlayers.add(new MediaPlayer(new Media(fileList[i].toURI().toString())));
					} catch (Exception e) {
						System.out.println("ERROR: " + fileList[i] + " is an unsupported File Type");
					}
				}
			}
		}
		return mediaPlayers;
	}


	/***************************
	 * Producing Null List Items
	 ***************************/
	public static void setArtistNames(ListView<File> artistList) {

		artistList.setCellFactory(param -> new ListCell<File>() {

			@Override
			protected void updateItem(File file, boolean empty) {
				super.updateItem(file, empty);
				if (file != null) {
					setText(file.getName());
					setItem(file);
				}
			}
		});
	}


	/***************************
	 * Producing Null List Items
	 ***************************/
	public static void setTrackNames(ListView<MediaPlayer> trackList) {

		trackList.setCellFactory(param -> new ListCell<MediaPlayer>() {

			@Override
			protected void updateItem(MediaPlayer player, boolean empty) {
				super.updateItem(player, empty);

				if (player != null) {
					player.getMedia().getMetadata().addListener(new MapChangeListener<String, Object>() {

						@Override
						public void onChanged(Change<? extends String, ? extends Object> ch) {
							if (ch.wasAdded() && ch.getKey().equals("title")) {
								setText(ch.getValueAdded().toString());
								setItem(player);
							}
						}
					});
				}
			}
		});
	}


	public static Image setAlbumArt(File file) {

		String path = "";
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


	private static boolean isJPEG(String filePath) {
		return FilenameUtils.isExtension(filePath, "jpg");
	}

}
