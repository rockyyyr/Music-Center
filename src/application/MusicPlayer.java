package application;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * MusicPlayer. This is a wrapper class for javafx.scene.media.MediaPlayer
 * objects.
 * <p>
 * This plays media files using MediaPlayer. The media files are wrapped in a
 * javafx.scene.media.Media object.
 * 
 * @author Rocky Robson - A00914509
 * @version Dec 8, 2016
 */
public class MusicPlayer {

	/**
	 * The currently selected media file. This file has functions for playing
	 * and pausing and is updated as the user clicks through a track list of
	 * files.
	 */
	private MediaPlayer currentMedia;


	/**
	 * Default constructor
	 */
	public MusicPlayer() {
	}


	/**
	 * Constructor. Accepts a file to be used for playback.
	 * 
	 * @param file To be played. Must be of an acceptable file type.
	 */
	public MusicPlayer(File file) {
		playSelectedFile(file);
	}


	/**
	 * Plays the media file selected from a track list of media files.
	 * 
	 * @param file The media file to be played if and only if the file is not
	 *        null.
	 */
	public void playSelectedFile(File file) {

		if (isAcceptableFileType(file)) {

			try {
				currentMedia = new MediaPlayer(new Media(file.toURI().toString()));
				play();
			} catch (MediaException e) {
				System.out.println("ERROR: " + file.getName() + " is an unsupported File Type");
			}

		}
	}


	/**
	 * Play the currently selected media file
	 */
	public void play() {
		currentMedia.play();
	}


	/**
	 * Pause the currently selected media file
	 */
	public void pause() {
		currentMedia.pause();
	}


	/**
	 * Stops the currently selected media file
	 */
	public void stop() {
		currentMedia.stop();
	}


	/**
	 * Sets the volume for the currently selected media.
	 * 
	 * @param volume The volume for the currently selected media as a double.
	 *        Must be between 0 and 1
	 */
	public void setVolume(double volume) {
		if (volume >= 0 && volume <= 1)
			currentMedia.setVolume(volume);
	}


	/**
	 * Returns the progress value of the currently playing media file
	 * 
	 * @return The progress value as a double
	 */
	public double getProgressValue() {
		return currentMedia.getCurrentTime().toMillis() / currentMedia.getTotalDuration().toMillis();
	}


	/**
	 * Returns the Time property of the currently playing media file
	 * 
	 * @return The Time property as a ReadOnlyObjectProperty<Duration>
	 */
	public ReadOnlyObjectProperty<Duration> getTimeProperty() {
		return currentMedia.currentTimeProperty();
	}


	/**
	 * Checks the parameter File Type. Accepted file types are mp3, wav, aif,
	 * aiff, fxm, flv and m4a.
	 * 
	 * @param file The file to be checked
	 * @return True if the parameter File is of an acceptable file type. Returns false
	 *         if the file type is null or not accepted
	 */
	public static boolean isAcceptableFileType(File file) {

		if (file == null)
			return false;

		boolean acceptable = false;
		String ext = FilenameUtils.getExtension(file.getPath());

		switch (ext) {
		case "mp3":
			acceptable = true;
			break;
		case "wav":
			acceptable = true;
			break;
		case "aif":
			acceptable = true;
			break;
		case "aiff":
			acceptable = true;
			break;
		case "fxm":
			acceptable = true;
			break;
		case "flv":
			acceptable = true;
			break;
		case "m4a":
			acceptable = true;
			break;
		}
		return acceptable;
	}

}
