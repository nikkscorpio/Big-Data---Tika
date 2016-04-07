package edu.usc.polar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.tika.Tika;
import org.json.JSONObject;

/**
 * Class that performs the analysis of PolarTREC data. The analysis
 * is comprised of a sequence of the following 6 tasks in this order
 * for each file that is processed from the dataset:
 * 1. Tag Ratio Parsing to identify sections of text
 * 2. Generating a unique short URL for the file
 * 3. GROBID Jounal Parsing for PDF files to identify related publications
 * 4. Geotopic Parsing to identify references of geographic regions in the file
 * 5. SWEET Concenpts Extraction to identify references of SWEET concepts in the file
 * 6. Metadata Score Generation to evaluate metadata quality of the file
 * 
 * For each step, the class generates the results with the help of the
 * corresponding Parser class and stores these results as a {filename}_x.json
 * where { filename } stands for the name of the file and x denotes that the 
 * JSON file is the result of the corresponding task number (1-6).
 *   
 * @author shriram
 *
 */
public class ContentAnalysis {
	private static Tika tika;
	/**
	 * Method that exectues the different parsers sequentially.
	 * Firstly, the list of files to be processed in our dataset
	 * are identified using file filters and they are processed one
	 * by one and sent through the different parsers.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		tika = new Tika();
		
		//Directory that points to our dataset
		File dir = new File("/home/shriram/Documents/572-team1");
		//Do not consider the intermediate results as input
		IOFileFilter[] filters = {
		new NotFileFilter(new SuffixFileFilter("_1.json")),
		new NotFileFilter(new SuffixFileFilter("_2.json")),
		new NotFileFilter(new SuffixFileFilter("_3.json")),
		new NotFileFilter(new SuffixFileFilter("_4.json")),
		new NotFileFilter(new SuffixFileFilter("_5.json")),
		new NotFileFilter(new SuffixFileFilter("_6.json"))};
		IOFileFilter finalFilter = new RegexFileFilter("^(.*?)"); 
		for(IOFileFilter filter : filters){
			finalFilter = new AndFileFilter(finalFilter,filter);
		}
		Collection files = FileUtils.listFiles(
				dir, 
				finalFilter,
				DirectoryFileFilter.DIRECTORY
				);
		Iterator it = files.iterator();
		SWEETParser.createSweetList();	//Setup for SWEET parsing
		while(it.hasNext()){
			File file = (File)it.next();
			try{
				String mimetype = tika.detect(file);
				System.out.println(file.getPath());
				//Tag Ratio Parsing
				if(!(new File(file.getAbsolutePath()+"_1.json").exists())){
					try{
						TagRatioParser.parse(file);
						System.out.println("Tag Ratio Parsing done!");
					}
					catch(Exception e){
						System.out.println("Tag Ratio Parsing failed due to an exception!");
						e.printStackTrace(System.out);
					}
					catch(Error e){
						System.out.println("Tag Ratio Parsing failed due to an error!");
						e.printStackTrace(System.out);
					}
				}
				//URL Shortening
				if(!(new File(file.getAbsolutePath()+"_2.json").exists())){
					try{
						URLShortener.parse(file);
						System.out.println("URL Shortening done!");
					}
					catch(Exception e){
						System.out.println("URL Shortening failed!");
						e.printStackTrace(System.out);
					}
				}
				//GROBID Journal Parsing(called only for PDFs)
				if(!(new File(file.getAbsolutePath()+"_3.json").exists())){
					try{
						if(mimetype.equals("application/pdf")){
							GrobidParser.parse(file);
							System.out.println("GROBID Journal Parsing done!");
						}
					}
					catch(Exception e){
						System.out.println("GROBID Journal Parsing failed!");
						e.printStackTrace(System.out);
					}
				}
				//Geotopic Parsing
				if(!(new File(file.getAbsolutePath()+"_4.json").exists())){
					try{
						String content = tika.parseToString(file);
						GeotopicParser.parse(file,content,mimetype);
						System.out.println("Geotopic Parsing done!");
					}
					catch(Exception e){
						System.out.println("Geotopic Parsing failed! ");
						e.printStackTrace(System.out);
					}
				}
				//SWEET Concept Extraction
				if(!(new File(file.getAbsolutePath()+"_5.json").exists())){
					try{
						SWEETParser.parse(file);
					}
					catch(Exception e){
						System.out.println("SWEET Parsing failed!");
						e.printStackTrace(System.out);
					}
				}
				//Metadata Quality Evaluation
				if(!(new File(file.getAbsolutePath()+"_6.json").exists())){
					try{
						MetadataEvaluator.parse(file);
						System.out.println("Metadata quality score generated!");
					}
					catch(Exception e){
						System.out.println("Metadata quality score generation failed!");
						e.printStackTrace(System.out);
					}
				}
			}
			catch(Exception e){
				System.out.println("EXCEPTION!");
				e.printStackTrace(System.out);
			}
		}
	}

	/**
	 * This method writes the jsonObj JSON Object as a string
	 * to the file at the outputFilePath path.
	 * 
	 * @param jsonObj
	 * @param outputFilePath
	 * @throws IOException
	 */
	public static void writeJSONToFile(JSONObject jsonObj, String outputFilePath) throws IOException
	{
		FileWriter file = new FileWriter(outputFilePath);
		file.write(jsonObj.toString());
		file.close();
	}

}
