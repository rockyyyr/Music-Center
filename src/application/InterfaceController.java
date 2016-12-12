package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;

/**
 * MusicPlayerInterface.
 * 
 * @author Rocky Robson - A00914509
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
	private ListView<File> artistList;
	@FXML
	private ListView<MediaPlayer> trackList;
	@FXML
	private ImageView albumArt;

	/**
	 * The currently selected media file. This file has functions for playing
	 * and pausing and is updated as the user clicks through the track list
	 * library.
	 */
	private MediaPlayer currentMedia;


	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL url, ResourceBundle rsrcs) {
		setupButtons();
		setupListViews();
	}


	private void setupButtons() {
		setPlayButton();
		setPauseButton();
		setAddLibraryButton();
	}


	private void setupListViews() {
		setArtistListItemAction();
		setTrackListItemAction();
	}


	private void setPlayButton() {
		Image playButtonImage = new Image(getClass().getResourceAsStream("Play.png"));
		playButton.setGraphic(new ImageView(playButtonImage));
		playButton.setStyle("-fx-background-color: transparent");

		playButton.setOnAction((ActionEvent e) -> {
			currentMedia.play();
		});
	}


	private void setPauseButton() {
		Image pauseButtonImage = new Image(getClass().getResourceAsStream("Pause.png"));
		pauseButton.setGraphic(new ImageView(pauseButtonImage));
		pauseButton.setStyle("-fx-background-color: transparent");

		pauseButton.setOnAction((ActionEvent e) -> {
			currentMedia.pause();
		});
	}


	private void setAddLibraryButton() {
		addLibraryButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				artistList.setItems(MusicLibrary.populateArtistList(playButton.getScene().getWindow()));
				MusicLibrary.setArtistNames(artistList);
			}
		});
	}


	private void setArtistListItemAction() {
		artistList.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				trackList.setItems(MusicLibrary.createTrackMedia(artistList.getSelectionModel().getSelectedItem()));
				MusicLibrary.setTrackNames(trackList);
				albumArt.setImage(MusicLibrary.setAlbumArt(artistList.getSelectionModel().getSelectedItem()));
			}
		});
	}


	/***************************
	 * Producing Null List Items
	 ***************************/
	private void setTrackListItemAction() {
		trackList.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				try {
					trackList.getSelectionModel().getSelectedItem().play();
					currentMedia = trackList.getSelectionModel().getSelectedItem();
				} catch (Exception ex) {
					System.out.println("ERROR: Null Item: ");
				}
			}
		});
	}

}
