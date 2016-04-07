package edu.usc.polar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.json.JSONObject;
import org.xml.sax.SAXException;

/**
 * Class that evaluates the quality of metadata of each of the input
 * files in the dataset and writes the score to a JSON file
 * {inputFile path}/{inputFile name}_6.json,
 * 
 * @author shriram
 *
 */
public class MetadataEvaluator {
	/**
	 * This method uses Tika's AutoDetectParser parse() method to
	 * parse the input file and retrieve its metadata and traverses
	 * over each of the metadata names and values and adds the following
	 * to a score variable 
	 * i)	Adds 1 each for the presence of a title, description and version
	 *     	metadata
	 * ii)	Adds 1 for the presence of an alias/knownas metadata
	 * iii)	Adds 1 for an existing unique URL/UID/permalink/pURL
	 * iv)	Adds the (count of metadata fields)/(number of metadata models)
	 * This score is then written onto 
	 * {inputFile path}/{inputFile name}_6.json,
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public static void parse(File file) throws FileNotFoundException {
		// TODO Auto-generated method stub
		try{
			//Parser method parameters
			Parser parser = new AutoDetectParser();
			BodyContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			double score = 0.0;
			FileInputStream inputstream = new FileInputStream(file);
			ParseContext context = new ParseContext();

			parser.parse(inputstream, handler, metadata, context);

			//getting the list of all meta data elements 
			String[] metadataNames = metadata.names();
			Set<String> metadataModels = new HashSet<String>();
			boolean containsTitle = false, containsDesc = false, containsVersion = false,
					containsAlias = false, containsLicense = false, containsUID = false;
			for(String name : metadataNames) {		        
				if(!containsTitle && (name.contains("title") || name.contains("Title"))){
					score += 1;
					containsTitle = true;
				}
				if(!containsDesc && (name.contains("description") || name.contains("Description"))){
					score += 1;
					containsDesc = true;
				}
				if(!containsVersion && (name.contains("version") || name.contains("Version"))){
					score += 1;
					containsVersion = true;
				}
				if(!containsAlias && (name.contains("alias") || name.contains("Alias") || name.equalsIgnoreCase("knownas"))){
					score += 1;
					containsAlias = true;
				}
				if(name.contains(":")){
					String model = name.substring(0,name.indexOf(":"));
					metadataModels.add(model);
				}
				if(!containsLicense && (name.contains("license") || name.contains("License"))){
					score += 1;
					containsLicense = true;
				}
				if(!containsUID && (name.contains("purl") || name.contains("pURL") || name.contains("permalink") || name.contains("uid") || name.contains("UID") || name.contains("doi"))){
					score += 1;
					containsUID = true;
				}
			}
			score += ((double)metadataNames.length/metadataModels.size()+1);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("MetadataScore", score);
			String outputFilePath = file.getAbsolutePath()+"_6.json";
			ContentAnalysis.writeJSONToFile(jsonObj, outputFilePath);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
