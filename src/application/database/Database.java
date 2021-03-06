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


	private static final String PLAYLIST_DATABASE = "playlists.db";
	private static final String DIRECTORY_DATABASE = "directories.db";
	private static final String PLAYLIST_FILE_COL = "FilePath";
	private static final String DIRECTORY_TABLE_NAME = "directories";

	private static Connection conn;
	private static Statement statement;


	/*
	 * Opens a connection to the playlist database. This connection must be
	 * closed after use.
	 */
	private static void connectToPlaylistDatabase() {

		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + MusicPlaylist.PLAYLIST_PATH + PLAYLIST_DATABASE);
			statement = conn.createStatement();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/*
	 * Opens a connection to the directory database. This connection must be
	 * closed after use.
	 */
	private static void connectToDirectoryDatabase() {

		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + MusicPlaylist.PLAYLIST_PATH + DIRECTORY_DATABASE);
			statement = conn.createStatement();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/*
	 * Closes the connection to the playlist database.
	 */
	private static void disconnectFromDatabase() {

		try {

			conn.close();

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
			connectToPlaylistDatabase();

			String createTable = "CREATE TABLE " + playlistName + " (" + PLAYLIST_FILE_COL + " TEXT);";
			statement.executeUpdate(createTable);

			statement.close();
			disconnectFromDatabase();

			System.out.println("Table created successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Creates a new table for storing playlist and library directories if one
	 * does not already exist.
	 * 
	 * Also initializes the table for storage of both directories if not yet
	 * initialized
	 */
	public static void createDirectoryTable() {

		try {
			connectToDirectoryDatabase();

			String createTable = "CREATE TABLE IF NOT EXISTS " + DIRECTORY_TABLE_NAME
					+ " (row INT, playlist TEXT, library TEXT);";
			statement.executeUpdate(createTable);

			String initialize = "INSERT INTO  " + DIRECTORY_TABLE_NAME + " (row, playlist, library) " +
					"SELECT 1, ' ', ' ' " +
					"WHERE NOT EXISTS (SELECT * FROM " + DIRECTORY_TABLE_NAME + " WHERE row=1);";
			statement.executeUpdate(initialize);

			statement.close();
			disconnectFromDatabase();

			System.out.println("Directory table created successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 
	 * @param directoryPath
	 */
	public static void savePlaylistDirectory(String directoryPath) {
		saveDirectory("playlist", directoryPath);
	}


	public static void saveLibraryDirectory(String directoryPath) {
		saveDirectory("library", directoryPath);
	}


	private static void saveDirectory(String directory, String directoryPath) {

		try {
			connectToDirectoryDatabase();

			String dirPath = "UPDATE " + DIRECTORY_TABLE_NAME + " SET " + directory + "='" + directoryPath
					+ "' WHERE row=1;";
			statement.executeUpdate(dirPath);

			statement.close();
			disconnectFromDatabase();

			System.out.println(directory + " directory saved");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static String retrievePlaylistDirectory() {
		return retrieveDirectory("playlist");
	}


	public static String retrieveLibraryDirectory() {
		return retrieveDirectory("library");
	}


	private static String retrieveDirectory(String directory) {

		String result = "";

		try {
			connectToDirectoryDatabase();

			String sql = "SELECT " + directory + " FROM " + DIRECTORY_TABLE_NAME + ";";
			ResultSet rs = statement.executeQuery(sql);

			while (rs.next()) {
				result = rs.getString(directory);
			}

			rs.close();
			disconnectFromDatabase();

			System.out.println(directory + " retrieved successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}


	public static void displayDirectories() {
		try {
			connectToDirectoryDatabase();

			String sql = "SELECT * FROM directories";
			ResultSet rs = statement.executeQuery(sql);

			while (rs.next()) {
				System.out.println("row: " + rs.getInt("row"));
				System.out.println("playlist: " + rs.getString("playlist"));
				System.out.println("library: " + rs.getString("library"));
				System.out.println();
			}

			rs.close();
			disconnectFromDatabase();

		} catch (SQLException e) {
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
			connectToPlaylistDatabase();

			String addition = "INSERT INTO " + playlistName + " (" + PLAYLIST_FILE_COL + ") VALUES ('" + filePath
					+ "');";
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
			connectToPlaylistDatabase();

			String retrieve = "SELECT * FROM " + playlistName;
			ResultSet rs = statement.executeQuery(retrieve);

			results = new String[getRSSize(playlistName)];

			int index = 0;

			while (rs.next())
				results[index++] = rs.getString(PLAYLIST_FILE_COL);

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
			connectToPlaylistDatabase();

			String remove = "DELETE FROM " + playlistName + " where " + PLAYLIST_FILE_COL + "='" + filePath + "';";
			statement.executeUpdate(remove);

			statement.close();
			disconnectFromDatabase();

			System.out.println("Track deleted from playlist");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Lists all the currently created playlists in the database. This list is
	 * used to populate the playlist combo box
	 * 
	 * @return A string array containing all created playlists
	 */
	public static String[] listAllPlaylists() {

		String[] results = null;

		try {
			connectToPlaylistDatabase();

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


	/**
	 * Returns the number of tracks in a playlist.
	 * 
	 * @param playlistName The name of the playlist.
	 * @return The number of tracks in the specified playlist
	 */
	public static int getRSSize(String playlistName) {

		int count = 0;

		try {
			connectToPlaylistDatabase();

			String numOfRows = "SELECT COUNT(*) AS total FROM " + playlistName + ";";
			ResultSet rs = statement.executeQuery(numOfRows);

			while (rs.next())
				count = rs.getInt("total");

			rs.close();
			disconnectFromDatabase();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
}
