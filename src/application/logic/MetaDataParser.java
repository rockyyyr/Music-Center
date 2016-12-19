package application.logic;
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

/**
 * MetaDataParser.
 * 
 * @author Rocky Robson - A00914509
 * @version Dec 14, 2016
 */
public class MetaDataParser {


	public static Metadata parseMetaData(File file) {
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
	
	public static String getTitle(File file){
		return parseMetaData(file).get("title");
		
	}
	
	public static String getArtist(File file){
		return parseMetaData(file).get("creator");
	}


}
