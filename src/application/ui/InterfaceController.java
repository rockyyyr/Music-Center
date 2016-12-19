package application.ui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import application.logic.MetaDataParser;
import application.logic.MusicLibrary;
import application.logic.MusicPlayer;
import application.logic.MusicPlaylist;
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
	private ComboBox<File> playlists;
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

	/**
	 * The currently selected media file. This file has functions for playing
	 * and pausing and is updated as the user clicks through the track list
	 * library.
	 */
	private MusicPlayer currentMedia;


	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL url, ResourceBundle rsrcs) {
		setupComponents();
	}


	private void setupComponents() {
		initializeLibrary();
		setTimeLabel();
		setPlayButton();
		setPauseButton();
		setAddLibraryButton();
		setAddPlaylistButton();
		setPlaylists();
		setVolumeSlider();
		setArtistListItemAction();
		setTrackListView();
	}


	private void initializeLibrary() {
		File directory = new File(MusicLibrary.retrieveLibraryDirectory());

		if (directory != null) {
			artistList.setItems(MusicLibrary.populateArtistList(directory));
			MusicLibrary.setFileNames(artistList, false);
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


	private void setVolumeSlider() {
		volumeSlider.setMax(1);
		volumeSlider.setValue(0.75);

		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {


			@Override
			public void changed(ObservableValue<? extends Number> volumeSlider, Number oldVal, Number newVal) {

				if (currentMedia != null)
					currentMedia.setVolume(newVal.doubleValue());
			}
		});
	}


	private void setProgressBar() {

		ChangeListener<Duration> progressChangeListener = new ChangeListener<Duration>() {


			@Override
			public void changed(ObservableValue<? extends Duration> value, Duration oldVal, Duration newVal) {
				progressBar.setProgress(currentMedia.getProgressValue());
			}
		};
		currentMedia.getTimeProperty().addListener(progressChangeListener);
	}


	private void setTimeLabel() {
		timeLabel.setText("0:00 / 0:00");
	}


	private void setInfoLabels(File file) {
		setArtistLabel(file);
		setTrackLabel(file);
	}


	private void setArtistLabel(File file) {
		artistLabel.setText("[ " + MetaDataParser.getArtist(file) + " ]");
	}


	private void setTrackLabel(File file) {
		trackLabel.setText("- " + MetaDataParser.getTitle(file));
	}


	private void updateTimeLabel() {
		ChangeListener<Duration> timeListener = new ChangeListener<Duration>() {


			@Override
			public void changed(ObservableValue<? extends Duration> arg0, Duration arg1, Duration arg2) {
				timeLabel.setText(currentMedia.getTimeLabel());
			}
		};
		currentMedia.getTimeProperty().addListener(timeListener);
	}


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


	private void setAddPlaylistButton() {
		addPlaylistButton.setOnAction(new EventHandler<ActionEvent>() {


			@Override
			public void handle(ActionEvent e) {
				MusicPlaylist.createPlaylist();
				setPlaylists();
			}
		});
	}


	private void setPlaylists() {
		setPlaylistItemAction();
		playlists.setItems(MusicPlaylist.populatePlaylistMenu());
		MusicPlaylist.setFileNames(playlists);

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


	public void setTrackListView() {
		setTrackListItemAction();
		MusicLibrary.setContextMenu(trackList, playlistView);
	}


	private void setTrackListItemAction() {
		trackList.setOnMouseClicked(new EventHandler<MouseEvent>() {


			@Override
			public void handle(MouseEvent e) {

				if (e.getButton().equals(MouseButton.PRIMARY)) {
					
					if (currentMedia != null)
						currentMedia.stop();

					File file = trackList.getSelectionModel().getSelectedItem();

					currentMedia = new MusicPlayer(file);
					setInfoLabels(file);
					setProgressBar();
					updateTimeLabel();
				}
			}
		});
	}


	private void setPlaylistItemAction() {
		playlistView.setOnMouseClicked(new EventHandler<MouseEvent>() {


			@Override
			public void handle(MouseEvent e) {
				if (currentMedia != null)
					currentMedia.stop();

				File file = playlistView.getSelectionModel().getSelectedItem();

				currentMedia = new MusicPlayer(file);
				albumArt.setImage(MusicLibrary.setAlbumArt(file.getParentFile()));

				setInfoLabels(file);
				setProgressBar();
				updateTimeLabel();
			}

		});
	}

}
