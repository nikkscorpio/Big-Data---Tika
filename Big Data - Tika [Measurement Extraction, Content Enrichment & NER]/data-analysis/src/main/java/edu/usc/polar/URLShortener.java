package edu.usc.polar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

/**
 * Class that contains methods to generate a unique short URL corresponding 
 * to a file by making HTTP requests to TinyURL. Once generated, this class
 * has methods to write this unique short URL to a JSON file
 * {inputFile path}/{inputFile name}_2.json. 
 * 
 * @author shriram
 *
 */
public abstract class URLShortener {
	private static HttpMethod method = new GetMethod("http://tinyurl.com/api-create.php");
	private static HttpClient httpclient = new HttpClient();

	/**
	 * Method that makes an HTTP client request to TinyURL and
	 * generates a unique URL
	 * 
	 * @param fullUrl
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String getTinyUrl(String fullUrl) throws HttpException, IOException {
		// Prepare a request object 
		method.setQueryString(new NameValuePair[]{new NameValuePair("url",fullUrl)});
		httpclient.executeMethod(method);
		InputStream stream = method.getResponseBodyAsStream();
		List<String> str = IOUtils.readLines(stream);
		method.releaseConnection();
		return str.get(0);
	}

	/**
	 * Method that invokes the short URL generation method and writes the
	 * response back to a JSON file {inputFile path}/{inputFile name}_2.json.
	 * 
	 * @param inputFile
	 */
	public static void parse(File inputFile) {
		// TODO Auto-generated method stub
		try {
			httpclient.getParams().setParameter("http.protocol.single-cookie-header", true);
			httpclient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			String url = getTinyUrl(inputFile.getAbsolutePath());
			url = url.replace("tinyurl.com", "polar.usc.edu");
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("ShortURL", url);
			String outputFilePath = inputFile.getAbsolutePath()+"_2.json";
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