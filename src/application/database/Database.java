package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import application.logic.MusicPlaylist;

/**
 * Database. An SQLite database used to store playlists and playlist tracks
 * 
 * @author Rocky Robson
 * @version Jan 2, 2017
 */
public class Database {


	private static final String databaseName = "playlists.db";
	private static final String fileNameCol = "FilePath";

	private static Connection conn;
	private static Statement statement;


	/*
	 * Opens a connection to the playlist database. This connection must be
	 * closed after use.
	 */
	private static void connectToDatabase() {

		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + MusicPlaylist.PLAYLIST_PATH + databaseName);
			statement = conn.createStatement();

//			System.out.println("Connected to Database");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/*
	 * Closes the connection to the playlist database.
	 */
	private static void disconnectFromDatabase() {

		try {
//			statement.close();
			conn.close();

//			System.out.println("Disconnected from the database");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Creates a new playlist.
	 * 
	 * @param playlistName The name of the playlist to be created
	 */
	public static void createPlaylist(String playlistName) {

		try {
			connectToDatabase();

			String createTable = "CREATE TABLE " + playlistName + " (" + fileNameCol + " TEXT)";
			statement.executeUpdate(createTable);

			statement.close();
			disconnectFromDatabase();

			System.out.println("Table created successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Adds a track's file path to the specified playlist.
	 * 
	 * @param playlistName The name of the playlist where the track is to be
	 *        added
	 * @param filePath The file path of the track to be added
	 */
	public static void addToPlaylist(String playlistName, String filePath) {

		try {
			connectToDatabase();

			String addition = "INSERT INTO " + playlistName + " (" + fileNameCol + ") VALUES ('" + filePath + "');";
			statement.executeUpdate(addition);

			statement.close();
			disconnectFromDatabase();

			System.out.println("Playlist Updated");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Retrieves an entire playlist from the specified playlist table
	 * 
	 * @param playlistName The name of the playlist to be retrieved
	 * @return A string array containing all the track's file paths
	 */
	public static String[] retrievePlaylist(String playlistName) {

		String[] results = null;

		try {
			connectToDatabase();

			String retrieve = "SELECT * FROM " + playlistName;
			ResultSet rs = statement.executeQuery(retrieve);

			results = new String[getRSSize(playlistName)];

			int index = 0;

			while (rs.next())
				results[index++] = rs.getString(fileNameCol);

			rs.close();
			disconnectFromDatabase();

			System.out.println(playlistName + " Playlist Retrieved");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return results;
	}


	/**
	 * Deletes a specific track from a specified playlist
	 * 
	 * @param playlistName The name of the playlist to remove a track from
	 * @param filePath The file path of the track to be removed
	 */
	public static void deleteFromPlaylist(String playlistName, String filePath) {

		try {
			connectToDatabase();

			String remove = "DELETE FROM " + playlistName + " where " + fileNameCol + "='" + filePath + "';";
			statement.executeUpdate(remove);

			statement.close();
			disconnectFromDatabase();

			System.out.println("Track deleted from playlist");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static String[] listAllPlaylists() {

		String[] results = null;

		try {
			connectToDatabase();

			String tables = "SELECT name FROM sqlite_master WHERE type='table'";
			ResultSet rs = statement.executeQuery(tables);

			results = new String[getRSSize("sqlite_master")];
			int index = 0;

			while (rs.next())
				results[index++] = rs.getString("name");

			rs.close();
			disconnectFromDatabase();
			
			System.out.println("Table list retrieved successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}


	private static int getRSSize(String playlistName) {

		int count = 0;

		try {
			connectToDatabase();
			
			String numOfRows = "SELECT COUNT(*) AS total FROM " + playlistName + ";";
			ResultSet rs = statement.executeQuery(numOfRows);
			
			while(rs.next())
				count = rs.getInt("total");
			
			rs.close();
			disconnectFromDatabase();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
}
