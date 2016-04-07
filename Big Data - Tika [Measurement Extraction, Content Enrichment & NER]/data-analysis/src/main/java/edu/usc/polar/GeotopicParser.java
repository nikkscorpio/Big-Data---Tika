package edu.usc.polar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.json.JSONObject;

public class GeotopicParser {
	private static PutMethod method = new PutMethod("http://localhost:9998/rmeta");
	private static HttpClient httpclient = new HttpClient();
	private static Tika tika = new Tika();
	
	/**
	 * Class that has a method which send the content text to the Geotopic 
	 * Parser of Tika in the form of an HTTP request to the Tika Server as 
	 * a file with Content-Type set to application/geotopic and the 
	 * request payload being the text extracted from the file. It also
	 * has a method that would write the HTTP response back to a JSON file
	 * 
	 * @param file
	 * @param content
	 * @param mimetype
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static JSONObject getGeotopicParserResponse(File file, String content, String mimetype) throws HttpException, IOException {
		// Prepare a request object
		InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(content.getBytes()));
		RequestEntity requestEntity = new StringRequestEntity(content, mimetype, reader.getEncoding());
		method.setRequestEntity(requestEntity);
		method.addRequestHeader("Content-Disposition", "attachment");
		method.addRequestHeader("Content-Type", "application/geotopic");
		HttpClientParams params = new HttpClientParams(); 
		params.setParameter("filename", file.getName());
		params.setParameter(HttpMethodParams.RETRY_HANDLER, 
		           new DefaultHttpMethodRetryHandler(0,false));
		httpclient.setParams(params);
		httpclient.executeMethod(method);
		InputStream stream = method.getResponseBodyAsStream();
		List<String> str = IOUtils.readLines(stream);
		method.releaseConnection();
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("Geotopic",JSONObject.stringToValue(str.get(0)));
		return jsonObj;
	}

	/**
	 * This method uses the Tika's Geotopic server to obtain matching
	 * responses for 'content' which is text extracted from the file
	 * and write the response that contains matching regions of the world
	 * on to {inputFile path}/{inputFile name}_4.json
	 * 
	 * @param inputFile
	 * @param content
	 * @param mimetype
	 */
	public static void parse(File inputFile, String content, String mimetype) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObj = getGeotopicParserResponse(inputFile,content,mimetype);
			//System.out.println(jsonObj);
			String outputFilePath = inputFile.getAbsolutePath()+"_4.json";
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
