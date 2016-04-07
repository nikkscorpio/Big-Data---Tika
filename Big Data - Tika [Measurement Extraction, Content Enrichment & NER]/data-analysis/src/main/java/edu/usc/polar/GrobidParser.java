package edu.usc.polar;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.journal.GrobidRESTParser;
import org.apache.tika.sax.BodyContentHandler;
import org.json.JSONObject;
import org.xml.sax.ContentHandler;

/**
 * Class that has methods which send the content text to the GROBID REST 
 * Parser of Tika in the form of an HTTP PUT request where the 
 * request payload is the text extracted from the file and also writes
 * the response of this GROBID REST Parser to a JSON file. This class also
 * has methods that sends the title of the current file to ScholarAPI.java
 * helper class which in turn invokes the Google Scholar API(A Python script)
 * whose response contains publications/authors that are related to the 
 * input file
 * 
 * @author shriram
 */
public class GrobidParser {
	private static GrobidRESTParser parser = new GrobidRESTParser();
	/**
	 * Method that sends file as request payload of a PUT request
	 * to GROBID's REST parser that is part of Apache Tika and obtains
	 * the repsonse of the server in the form of the metadata object.
	 * It then traverses over all the keys of this metadata object, sets
	 * the key-value pairs to a JSONObject and returns this JSON object
	 * 
	 * @param file
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static JSONObject getGrobidJournalParserResponse(File file) throws HttpException, IOException {
		JSONObject jsonObj = new JSONObject();
		ContentHandler contenthandler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		parser.parse(file.getAbsolutePath(), contenthandler, metadata, new ParseContext());
		for (String key : metadata.names()) {
			jsonObj.put(key,metadata.get(key));
		}
		jsonObj.put("RelatedPublications",ScholarAPI.parse((String) jsonObj.get("grobid:header_Title")));
		return jsonObj;
	}

	/**
	 * Method that takes in an inputFile, performs the GROBID Journal
	 * Parsing of this inputFile and writes the resulting JSON which 
	 * contains related publications and authors onto 
	 * {inputFile path}/{inputFile name}_3.json
	 * 
	 * @param inputFile
	 */
	public static void parse(File inputFile) {
		try {
			JSONObject jsonObj = getGrobidJournalParserResponse(inputFile);
			String outputFilePath = inputFile.getAbsolutePath()+"_3.json";
			ContentAnalysis.writeJSONToFile(jsonObj, outputFilePath);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
