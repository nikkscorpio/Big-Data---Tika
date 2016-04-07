package edu.usc.polar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import org.apache.tika.exception.TikaException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

/**
 * Class that contains methods to extract concepts of the SWEET Ontology
 * that are relevant to each file from the input dataset. Once extracted,
 * these concepts are written to {inputFile path}/{inputFile name}_5.json.
 * 
 * @author shriram
 *
 */
public class SWEETParser {
	public static ArrayList<String> sweetList;

	/**
	 * Method that initializes the sweetList with the list of all
	 * the SWEET concepts that are extracted through the one time
	 * execution of the commented block of code in the parse() method.
	 */
	public static void createSweetList() {
		sweetList = new ArrayList<String>();
		File SWEETSet = new File("src/main/resources/SWEET OWL set.txt");
		
		try {	
			BufferedReader br = new BufferedReader(new FileReader(SWEETSet));
			for (String line; (line = br.readLine()) != null;) {
				sweetList.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method checks for the presence of any of the SWEET concepts
	 * in the file and adds them to a list which is then added onto a
	 * JSON object. This JSON object is then stored onto 
	 * {inputFile path}/{inputFile name}_5.json.
	 * 
	 * @param file
	 * @throws SAXException
	 * @throws TikaException
	 * @throws IOException
	 */
	public static void parse(File file) throws SAXException, TikaException, IOException {
		/*
		 * Run this code only once to generate the SWEET word set
		 * 
		 * String OWLFolderPath = "/Users/nikitharathnakar/Downloads/2.3"; File
		 * OWLFolder = new File(OWLFolderPath); String OutputFilePath =
		 * "output_OWL_Classes.txt"; File outFile = new File(OutputFilePath);
		 * 
		 * 
		 * 
		 * for(File owlFile : OWLFolder.listFiles()){ if
		 * (!(owlFile.getName().equalsIgnoreCase(".DS_Store"))) {
		 * ArrayList<String> arr; try { PrintWriter writer = new PrintWriter(new
		 * BufferedWriter(new FileWriter(outFile.getName(), true)));
		 * 
		 * // PrintWriter writer; // writer = new
		 * PrintWriter(outFile.getAbsolutePath()); arr =
		 * owlAPI(owlFile,OutputFilePath);
		 * 
		 * if(arr!=null){ for(int i=0;i<arr.size();i++){
		 * System.out.println(arr.get(i)); writer.println(arr.get(i)); } }
		 * writer.close(); } catch (FileNotFoundException e) { // TODO
		 * Auto-generated catch block
		 * 
		 * e.printStackTrace(); } catch (UnsupportedEncodingException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 * 
		 * }
		 * 
		 */

		// Creating the sweetList should happen only once for all the input
		// files

		// End of creating sweetList
		String s;
		try {
			RandomAccessFile f = new RandomAccessFile(file,"r");

			JSONObject fileJsonObj = new JSONObject();
			JSONArray sweetWordsInFile = new JSONArray();
			s = f.readLine();
			while (s != null) {
				for (String sweetWord : sweetList) {
					if (s.indexOf(sweetWord) != -1) {
						// Write this to Json
						//System.out.println(sweetWord);
						sweetWordsInFile.put(sweetWord);
					}
				}
				s = f.readLine();
			}
			fileJsonObj.put("Sweet Concepts", sweetWordsInFile);
			String outputFilePath = file.getAbsolutePath()+"_5.json";
			ContentAnalysis.writeJSONToFile(fileJsonObj, outputFilePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}