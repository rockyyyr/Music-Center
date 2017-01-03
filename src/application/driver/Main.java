package application.driver;

import java.util.logging.Level;
import java.util.logging.Logger;

import application.logic.MusicPlaylist;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Main. Driver Class
 * @author Rocky Robson
 * @version Dec 13, 2016
 */
public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			AnchorPane headAnchor = (AnchorPane) FXMLLoader.load(getClass().getResource("/application/ui/UserInterface.fxml"));
			Scene scene = new Scene(headAnchor);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Music Center");
			primaryStage.show();
		} catch (Exception e) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
		}
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){

			@Override
			public void handle(WindowEvent event) {
				MusicPlaylist.saveCurrentPlaylistSelection();
			}
			
		});
	}


	public static void main(String[] args) {
		launch(args);
	}
}
