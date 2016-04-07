package edu.usc.polar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Utility class that aids in combining and flattening all the
 * individual JSON files corresponding to results of each parser's
 * work on an input file from the dataset. This combining and flattening
 * process ensures that data is in a format eligible for consumption
 * by Apache Solr for index creation. The class takes in 
 * {inputFile path}/{inputFile name}_1.json,
 * {inputFile path}/{inputFile name}_2.json,
 * {inputFile path}/{inputFile name}_3.json,
 * {inputFile path}/{inputFile name}_4.json,
 * {inputFile path}/{inputFile name}_5.json and
 * {inputFile path}/{inputFile name}_6.json and combines and flattens these
 * to form {inputFile path}/{inputFile name}_solr.json
 * 
 * @author shriram
 *
 */
public class JoinJSONFiles {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Input dataset path
		File dir = new File("/home/shriram/Documents/572-team1");
		//Avoid considering intermediate results for processing
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
		while(it.hasNext()){
			File file = (File)it.next();
			System.out.println(file.getAbsolutePath());
			String outputFilePath = file.getAbsolutePath()+"_solr.json";
			if(!(new File(outputFilePath).exists())){
				Random ran = new Random();
				StringBuilder strBuilder = new StringBuilder("{");
				String uniqueID = "http://polar.usc.edu/"+(100000 + ran.nextInt(900000));
				for(int i = 1 ; i <= 6; i++ ){
					if(new File(file.getAbsolutePath()+"_2.json").exists()){
						try{
							if(new File(file.getAbsolutePath()+"_"+i+".json").exists()){
								InputStream is = new FileInputStream(new File(file.getAbsolutePath()+"_"+i+".json"));
								StringWriter writer = new StringWriter();
								IOUtils.copy(is, writer);
								StringBuilder currentBuilder = new StringBuilder(writer.toString());
								if(i==1){
									JSONObject jsonObj = new JSONObject(currentBuilder.toString());
									JSONArray arr = jsonObj.getJSONArray("Measurements");
									currentBuilder = new StringBuilder("\"Measurements\":[");
									for(int j = 0; j < arr.length(); j++){
										JSONObject obj = arr.getJSONObject(j);
										Iterator keys = obj.keys();
										while(keys.hasNext()){
											String key = (String)keys.next();
											currentBuilder.append("\""+key+" "+obj.get(key)+"\",");
										}
									}
									if((",").equals(currentBuilder.toString().charAt(currentBuilder.toString().length()-1))){
										currentBuilder.deleteCharAt(currentBuilder.toString().length()-1);
									}
									currentBuilder.append("],");
									strBuilder.append(currentBuilder);
								}
								else if(i == 2){
									JSONObject jsonObj = new JSONObject(currentBuilder.toString());
									uniqueID = jsonObj.getString("ShortURL");
									strBuilder.append("\"id\":\""+uniqueID+"\",");
								}
								else if(i==3){
									JSONObject jsonObj = new JSONObject(currentBuilder.toString());
									JSONObject relPubObj = (JSONObject) jsonObj.get("RelatedPublications");
									Iterator keys = relPubObj.keys();
									while(keys.hasNext()){
										String key = (String) keys.next();
										JSONObject particularRelPubObj = relPubObj.getJSONObject(key);
										Iterator innerKeys = particularRelPubObj.keys();
										while(innerKeys.hasNext()){
											String innerKey = (String) innerKeys.next();
											jsonObj.put(key+"-"+innerKey, particularRelPubObj.get(innerKey));
										}
									}
									jsonObj.remove("RelatedPublications");
									StringBuilder jsonStr = new StringBuilder(jsonObj.toString());
									if(jsonStr.toString().contains("{")){
										jsonStr.deleteCharAt(jsonStr.toString().indexOf("{"));
									}
									if(jsonStr.toString().contains("}")){
										jsonStr.deleteCharAt(jsonStr.toString().lastIndexOf("}"));
									}
									strBuilder.append(jsonStr.toString()+",");
								}
								else if(i==4){
									currentBuilder.deleteCharAt(currentBuilder.toString().indexOf("{"));
									currentBuilder.deleteCharAt(currentBuilder.toString().lastIndexOf("}"));
									currentBuilder = new StringBuilder(currentBuilder.toString().replace("\"[", ""));
									currentBuilder = new StringBuilder(currentBuilder.toString().replace("]\"", ""));
									currentBuilder = new StringBuilder(currentBuilder.toString().replace("\n",""));
									currentBuilder = new StringBuilder(currentBuilder.toString().replace("\\", ""));
									currentBuilder = new StringBuilder(currentBuilder.toString().replace("\"Geotopic\":{", ""));
									if(currentBuilder.toString().contains("}")){
										currentBuilder.deleteCharAt(currentBuilder.toString().lastIndexOf("}"));
									}
									//buildecurrentBuilderr.deleteCharAt(currentBuilder.toString().length()-1);
									strBuilder.append(currentBuilder.toString()+",");
								}
								else{
									if(currentBuilder.toString().contains("{")){
										currentBuilder.deleteCharAt(currentBuilder.toString().indexOf("{"));
									}
									if(currentBuilder.toString().contains("}")){
										currentBuilder.deleteCharAt(currentBuilder.toString().lastIndexOf("}"));
									}
									strBuilder.append(currentBuilder.toString()+",");
								}
							}
						}
						catch(IOException e){
							System.out.println("IOEXCEPTION Caught!");
							e.printStackTrace(System.out);
						}
					}
				}
				try {
					strBuilder.deleteCharAt(strBuilder.lastIndexOf(","));
					strBuilder.append("}");
					System.out.println(strBuilder.toString());

					JSONObject finalJSON = new JSONObject(strBuilder.toString());
					ContentAnalysis.writeJSONToFile(finalJSON, outputFilePath);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("IOEXCEPTION while writing final JSON!");
					e.printStackTrace();
				}
				System.out.println("Solr JSON created!");
			}
		}
	}
}
