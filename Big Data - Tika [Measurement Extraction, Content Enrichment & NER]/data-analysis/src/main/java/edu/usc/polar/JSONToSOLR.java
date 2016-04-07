package edu.usc.polar;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Class that takes in all the {inputFile path}/{inputFile name}_solr.json
 * for the input files in the dataset and uploads them onto Apache Solr 
 * in the form of HTTP Client Requests to the Apache Solr Collection.
 * These requests update the Solr Collection with the file's features 
 * present in the JSON.
 * 
 * @author shriram
 *
 */
public class JSONToSOLR {

	/**
	 * Method that picks each file of the format 
	 * {inputFile path}/{inputFile name}_solr.json corresponding to
	 * each and every inputFile in the dataset and creates an HTTP
	 * POST request to Solr's collection update JSON service with the 
	 * file's data as the payload.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File dir = new File("/home/shriram/Documents/572-team1");
		IOFileFilter[] filters = {new SuffixFileFilter("_solr.json")};
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
			try{
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost postRequest = new HttpPost("http://localhost:8983/solr/sample/update?wt=json");
				StringWriter writer = new StringWriter();
				IOUtils.copy(new FileInputStream(file), writer);
				StringEntity input = new StringEntity("{add: {doc: "+writer.toString()+", boost: 1, overwrite: true, commitWithin: 1000}}");
				input.setContentType("application/json");
				postRequest.setEntity(input);
				ResponseHandler<String> responseHandler=new BasicResponseHandler();
				String response = httpClient.execute(postRequest,responseHandler);
				System.out.println(response);
			}
			catch(Exception e){
				System.out.println("EXCEPTION CAUGHT!");
				e.printStackTrace(System.out);
			}
		}
	}
}