package application.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import application.logic.MusicPlayer;

/**
 * MetaDataParser. This class can retrieve a track title and artist.
 * 
 * @author Rocky Robson - A00914509
 * @version Dec 14, 2016
 */
public class MetaDataParser {


	private static Metadata parseMetaData(File file) {
		Metadata data = new Metadata();

		try {
			InputStream input = new FileInputStream(file);
			ContentHandler handler = new DefaultHandler();
			Parser parser = new Mp3Parser();
			ParseContext parseCtx = new ParseContext();
			parser.parse(input, handler, data, parseCtx);
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}
		return data;
	}


	/**
	 * Get the title from the metadata of a specific file.
	 * 
	 * @param file The track whose title will be retrieved
	 * @return The track title as a string
	 */
	public static String getTitle(File file) {
		return parseMetaData(file).get("title");

	}


	/**
	 * Get the artist from the metadata of a specific file.
	 * 
	 * @param file The track whose artist will be retrieved
	 * @return The track's artist as a string
	 */
	public static String getArtist(File file) {
		return parseMetaData(file).get("creator");
	}
	
	/**
	 * Get the formatted duration of a track (mm:ss)
	 * 
	 * @param file The track to get the duration of
	 * @return The formatted duration of a track as a string
	 */
	public static String getDuration(File file){
		double durationMillis = Double.valueOf(parseMetaData(file).get("xmpDM:duration"));
		return MusicPlayer.getFormattedTime(durationMillis);
	}

}
