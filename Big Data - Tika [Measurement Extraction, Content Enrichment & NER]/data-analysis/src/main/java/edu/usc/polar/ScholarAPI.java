package edu.usc.polar;

import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONObject;

import com.opencsv.CSVReader;

/**
 * Helper class containing a helper method which is used by
 * GrobidParser.java to run the Google Scholar API through a Python
 * file(scholar.py) installed in the system. The helper method after 
 * running the scholar.py also picks up its JSON response and wraps
 * it around a JSON Object and returns it to GrobidParser.java. This
 * JSON object contains related publications/authors of a publication.
 * 
 * @author shriram
 *
 */
public class ScholarAPI
{
	public static JSONObject parse(String title) {
		JSONObject jsonObj = new JSONObject();
		try
		{
			ProcessBuilder builder = new ProcessBuilder("python", "/home/shriram/scholar.py","-c20", "-s",title,"--csv","bt");
			builder.redirectOutput(new File("/home/shriram/output"));
			builder.redirectError(new File("/home/shriram/error"));
			Process p = builder.start();
			p.waitFor();
			CSVReader reader = new CSVReader(new FileReader("/home/shriram/output"), '|');
			String[] fields = {"Title","URL","Year","Citations","Versions","Cluster ID","PDF link","Citations list","Versions list","None","Excerpt"};
			String [] nextLine;
			int count = 0;
			while ((nextLine = reader.readNext()) != null) {
				JSONObject innerJsonObj = new JSONObject();
				for(int i = 0 ; i < nextLine.length; i++){
					//System.out.print(nextLine[i]+ " | ");
					if(!("None".equals(nextLine[i]))){
						innerJsonObj.put(fields[i], nextLine[i]);
					}
				}
				System.out.println();
				jsonObj.put("RelatedPublication"+(++count),innerJsonObj);
			}

		}
		catch (Exception e)
		{
			String cause = e.getMessage();
			System.out.println(cause);
		}
		return jsonObj;
	}	
}