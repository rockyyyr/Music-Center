package application.ui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import application.logic.MusicLibrary;
import application.logic.MusicPlayer;
import application.logic.MusicPlaylist;
import application.utils.MetaDataParser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * MusicPlayerInterface.
 * 
 * @author Rocky Robson
 * @version Dec 8, 2016
 */
public class InterfaceController implements Initializable {


	@FXML
	private Button playButton;
	@FXML
	private Button pauseButton;
	@FXML
	private Button addLibraryButton;
	@FXML
	private Button addPlaylistButton;
	@FXML
	private ComboBox<String> playlists;
	@FXML
	private ListView<File> artistList;
	@FXML
	private ListView<File> trackList;
	@FXML
	private ListView<File> playlistView;
	@FXML
	private ImageView albumArt;
	@FXML
	private Slider volumeSlider;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label timeLabel;
	@FXML
	private Label artistLabel;
	@FXML
	private Label trackLabel;

	/*
	 * The currently selected media file. This file has functions for playing
	 * and pausing and is updated as the user clicks through the track list
	 * library.
	 */
	private MusicPlayer currentMedia;


	/**
	 * Initializes the UserInterface components
	 */
	@Override
	public void initialize(URL url, ResourceBundle rsrcs) {
		setupComponents();
	}


	/*
	 * Calls all component setup methods
	 */
	private void setupComponents() {
		initializeLibrary();
		initializePlaylist();
		setTimeLabel();
		setPlayButton();
		setPauseButton();
		setAddLibraryButton();
		setAddPlaylistButton();
		setVolumeSlider();
		setArtistListItemAction();
		setTrackListView();
	}


	/*
	 * Initializes the music library if a music library has been previously
	 * selected.
	 * 
	 * The library being used when the application was last shutdown will be
	 * automatically saved and retrieved on the next start up. If no library has
	 * been previously selected, no library is retrieved.
	 */
	private void initializeLibrary() {

		String directoryPath = MusicLibrary.retrieveLibraryDirectory();

		if (directoryPath != null && directoryPath.trim().length() > 0) {

			File directory = new File(directoryPath);

			if (directory != null) {
				artistList.setItems(MusicLibrary.populateArtistList(directory));
				MusicLibrary.setFileNames(artistList, false);
			}
		}
	}


	/*
	 * Initializes the music playlist if a playlist has been previously created.
	 * 
	 * The playlist being used when the application was last shut down will be
	 * automatically saved and retrieved on the next start up. If no playlist
	 * was previously created, no playlist will be retrieved
	 */
	private void initializePlaylist() {
		setPlaylists();
		String playlist = MusicPlaylist.retrieveCurrentPlaylist();

		if (playlist != null && playlist.trim().length() > 0) {
			MusicPlaylist.setCurrentPlaylist(playlist);
			playlists.getSelectionModel().select(playlist);
			playlistView.setItems(MusicPlaylist.populatePlaylistView());
			MusicLibrary.setFileNames(playlistView, true);
		}
	}


	private void setPlayButton() {
		Image playButtonImage = new Image("images/Play.png");
		playButton.setGraphic(new ImageView(playButtonImage));
		playButton.setStyle("-fx-background-color: transparent");

		playButton.setOnAction((ActionEvent e) -> {
			currentMedia.play();
		});
	}


	private void setPauseButton() {
		Image pauseButtonImage = new Image("images/Pause.png");
		pauseButton.setGraphic(new ImageView(pauseButtonImage));
		pauseButton.setStyle("-fx-background-color: transparent");

		pauseButton.setOnAction((ActionEvent e) -> {
			currentMedia.pause();
		});
	}


	/*
	 * Sets up the volume slider. Default value is 75%
	 */
	private void setVolumeSlider() {
		volumeSlider.setMax(1);
		volumeSlider.setValue(0.05);

		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {


			@Override
			public void changed(ObservableValue<? extends Number> volumeSlider, Number oldVal, Number newVal) {

				if (currentMedia != null)
					currentMedia.setVolume(newVal.doubleValue());
			}
		});
	}


	/*
	 * Returns the current volume determined by the position of the volume
	 * slider in the interface. Initial value is 75%
	 */
	private double getCurrentVoume() {
		return volumeSlider.getValue();
	}


	/*
	 * Sets the progress bar to display current track progress. This method is
	 * called every time a new track is selected.
	 */
	private void setProgressBar() {

		ChangeListener<Duration> progressChangeListener = new ChangeListener<Duration>() {


			@Override
			public void changed(ObservableValue<? extends Duration> value, Duration oldVal, Duration newVal) {
				progressBar.setProgress(currentMedia.getProgressValue());
			}
		};
		currentMedia.getTimeProperty().addListener(progressChangeListener);
	}


	/*
	 * The default time label to be displayed when no track is currently
	 * selected
	 */
	private void setTimeLabel() {
		timeLabel.setText("0:00 / 0:00");
	}


	/*
	 * Sets the artist and track information to be displayed beside the album
	 * art during playback
	 * 
	 * @param file The file whose info will be displayed
	 */
	private void setInfoLabels(File file) {
		setArtistLabel(file);
		setTrackLabel(file);
	}


	/*
	 * Track artist is retrieved from the MetaDataParser. This info is displayed
	 * next to the album art for currently playing media
	 */
	private void setArtistLabel(File file) {
		artistLabel.setText("[ " + MetaDataParser.getArtist(file) + " ]");
	}


	/*
	 * Track title is retrieved from the MetaDataParser. This info is displayed
	 * next to the album art for currently playing media
	 */
	private void setTrackLabel(File file) {
		trackLabel.setText("- " + MetaDataParser.getTitle(file));
	}


	/*
	 * A ChangeListener is used to update the elapsed/total time label using
	 * information from this.currentMedia.
	 */
	private void updateTimeLabel() {
		ChangeListener<Duration> timeListener = new ChangeListener<Duration>() {


			@Override
			public void changed(ObservableValue<? extends Duration> arg0, Duration arg1, Duration arg2) {
				timeLabel.setText(currentMedia.getTimeLabel());
			}
		};
		currentMedia.getTimeProperty().addListener(timeListener);
	}


	/*
	 * When Add library is clicked a dialog window opens asking for the music
	 * library directory.
	 * 
	 * When a directory is selected it is used to populate the artist list view
	 * and its files names are set.
	 */
	private void setAddLibraryButton() {
		addLibraryButton.setOnAction(new EventHandler<ActionEvent>() {


			@Override
			public void handle(ActionEvent e) {
				artistList
						.setItems(MusicLibrary.showDialogWindow(playButton.getScene().getWindow()));
				MusicLibrary.setFileNames(artistList, false);
			}
		});
	}


	/*
	 * When Add Playlist button is clicked the user is prompted to enter a new
	 * playlist name and a new file is created.
	 */
	private void setAddPlaylistButton() {
		addPlaylistButton.setOnAction(new EventHandler<ActionEvent>() {


			@Override
			public void handle(ActionEvent e) {
				MusicPlaylist.openPlaylistCreationDialog();
				setPlaylists();
			}
		});
	}


	/*
	 * Populates the playlist selection menu, sets the menu items to display
	 * their file names.
	 * 
	 * When a menu item is selected the playlist view gets cleared then
	 * re-populated and the playlist track names are set to "artist - track".
	 */
	private void setPlaylists() {
		setPlaylistItemAction();
		playlists.setItems(MusicPlaylist.populatePlaylistMenu());
		MusicPlaylist.setContextMenu(playlistView);

		playlists.setOnAction(new EventHandler<ActionEvent>() {


			@Override
			public void handle(ActionEvent e) {
				MusicPlaylist.setCurrentPlaylist(playlists.getSelectionModel().getSelectedItem());
				playlistView.getItems().clear();
				playlistView.setItems(MusicPlaylist.populatePlaylistView());
				MusicLibrary.setFileNames(playlistView, true);
			}
		});
	}


	/*
	 * When an item is clicked in the artist list view the selected directory is
	 * used to populate the track list view and the tracklist's files are set to
	 * display track titles. Album art is also set.
	 */
	private void setArtistListItemAction() {
		artistList.setOnMouseClicked(new EventHandler<MouseEvent>() {


			@Override
			public void handle(MouseEvent e) {
				trackList.setItems(MusicLibrary.populateTrackList(artistList.getSelectionModel().getSelectedItem()));
				MusicLibrary.setFileNames(trackList, false);
				albumArt.setImage(MusicLibrary.setAlbumArt(artistList.getSelectionModel().getSelectedItem()));
			}
		});
	}


	/*
	 * Sets up the context menu in the tracklist view that pops up when a user
	 * right clicks on a track.
	 * 
	 * The only option in the context menu is to add the selected track to the
	 * current playlist.
	 */
	public void setTrackListView() {
		setTrackListItemAction();
		MusicLibrary.setContextMenu(trackList, playlistView);
	}


	/*
	 * Sets the action for when an track is selected in the track list view.
	 * 
	 * If a track is left clicked, the selected track is set as
	 * MusicPlayer.currentMedia which is then used to control play, pause and
	 * other things related to that single track. Each time a new track is
	 * selected it gets set to this.currentMedia.
	 */
	private void setTrackListItemAction() {
		trackList.setOnMouseClicked(new EventHandler<MouseEvent>() {


			@Override
			public void handle(MouseEvent e) {

				if (e.getButton().equals(MouseButton.PRIMARY)) {

					if (trackList.getSelectionModel().getSelectedItem() != null) {

						if (trackList.getSelectionModel().getSelectedItem().isDirectory()) {
							handleTrackListDirectorySelection();
						} else {
							handleTrackListFileSelection();
						}
					}
				}
			}
		});
	}


	private void handleTrackListFileSelection() {

		if (trackList.getSelectionModel().getSelectedItem() != null) {

			if (currentMedia != null)
				currentMedia.stop();

			File file = trackList.getSelectionModel().getSelectedItem();

			currentMedia = new MusicPlayer(file);
			currentMedia.setVolume(getCurrentVoume());
			setInfoLabels(file);
			setProgressBar();
			updateTimeLabel();
		}
	}


	private void handleTrackListDirectorySelection() {

		if (trackList.getSelectionModel().getSelectedItem() != null) {

			File file = trackList.getSelectionModel().getSelectedItem();

			trackList.setItems(null);
			trackList.setItems(MusicLibrary.populateTrackList(file));
			albumArt.setImage(MusicLibrary.setAlbumArt(file));
		}
	}


	/*
	 * Sets the action for when a track is selected in the playlist view.
	 * 
	 * When a playlist track has been left clicked it is set to
	 * MusicPlayer.currentMedia which is used to control play, pause, volume,
	 * time remaining and other things related to this track. This current
	 * tracks album art is also set to be displayed
	 */
	private void setPlaylistItemAction() {
		playlistView.setOnMouseClicked(new EventHandler<MouseEvent>() {


			@Override
			public void handle(MouseEvent e) {

				if (e.getButton().equals(MouseButton.PRIMARY)) {

					if (playlistView.getSelectionModel().getSelectedItem() != null) {

						if (currentMedia != null)
							currentMedia.stop();

						File file = playlistView.getSelectionModel().getSelectedItem();

						currentMedia = new MusicPlayer(file);
						currentMedia.setVolume(getCurrentVoume());
						albumArt.setImage(MusicLibrary.setAlbumArt(file.getParentFile()));

						setInfoLabels(file);
						setProgressBar();
						updateTimeLabel();
					}
				}
			}
		});
	}

}
