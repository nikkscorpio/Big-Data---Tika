package tagratio;

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

public class SWEETParser {
	public static ArrayList<String> sweetList;

	public static void createSweetList() {
		sweetList = new ArrayList<String>();
		File SWEETSet = new File("SWEET OWL Set.txt");

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

	public static void main(String args[]) throws SAXException, TikaException, IOException {
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
		createSweetList();

		String s;
		RandomAccessFile f;
		try {
			
			f = new RandomAccessFile("9997030953132000764E44A6C870DDA72FE21C92DDE57D6A6225AD54D9C6D331", "r");

			JSONObject fileJsonObj = new JSONObject();
			fileJsonObj.put("FileName", "9997030953132000764E44A6C870DDA72FE21C92DDE57D6A6225AD54D9C6D331");
			JSONArray sweetWordsInFile = new JSONArray();
			s = f.readLine();
			while (s != null) {
				for (String sweetWord : sweetList) {
					if (s.indexOf(sweetWord) != -1) {
						// Write this to Json
						System.out.println(sweetWord);
						sweetWordsInFile.put(sweetWord);
					}
				}
				s = f.readLine();
			}
			fileJsonObj.put("Sweet Concepts", sweetWordsInFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}