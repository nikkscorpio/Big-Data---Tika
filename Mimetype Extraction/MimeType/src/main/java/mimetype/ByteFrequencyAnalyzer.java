package mimetype;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.tika.Tika;

public class ByteFrequencyAnalyzer {
	// Map containing fingerprints of all the files of a particular file type
	static HashMap<String, double[]> fingerprintMap = new HashMap<String, double[]>();
	double[] fingerprint = new double[256];
	/*
	 * public static double[] generateBFDFingerprint(String ofFilesinPath){
	 * double[] outputFingerpint = new double[256]; ​ }
	 */

	public double[] getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(double[] fingerprint) {
		this.fingerprint = fingerprint;
	}

	public void normalizeFingerprint() {
		double max = Arrays.stream(fingerprint).max().getAsDouble();
		// System.out.println(max);
		for (int i = 0; i < fingerprint.length; i++) {
			fingerprint[i] /= max;
		}
	}

	public void compandFingerprint() {
		for (int i = 0; i < fingerprint.length; i++) {
			fingerprint[i] = Math.sqrt(fingerprint[i]);
		}
	}

	public static double[] generateFingerprintOfFile(String filePath) throws IOException {
		double[] outputFingerpint = new double[256];
		File file = new File(filePath);
		long length = file.length();
		FileInputStream fis = new FileInputStream(filePath);
		for (long i = 0; i < length; i++) {
			byte b = (byte) fis.read();
			outputFingerpint[b + 128] += 1;
		}
		return outputFingerpint;
	}

	public void printFingerprint() {
		for (int i = 0; i < 256; i++) {
			System.out.print(fingerprint[i] + " ");
		}
	}

	private void addToFingerprintMap(String absolutePath) {
		// Adds the current File's fingerprint to the Common fingerprintmap with
		// key as the Filepath
		fingerprintMap.put(absolutePath, fingerprint);

	}

	public static double[] calculateAverageFingerprint() {
		// Calculates the average fingerprint of the files of a single filetype
		Iterator<String> it = fingerprintMap.keySet().iterator();
		double[] avgFingerprint = new double[256];
		while (it.hasNext()) {
			String path = it.next().toString();
			double[] fgrprt = fingerprintMap.get(path);
			for (int i = 0; i < 256; i++) {
				System.out.println(fgrprt[i]);
				avgFingerprint[i] += fgrprt[i];
			}
		}
		for (int i = 0; i < 256; i++) {
			avgFingerprint[i] = avgFingerprint[i] / fingerprintMap.size();
		}
		return avgFingerprint;
	}

	public static void createTSVFile(String fileName, double[] avgFingerprint) throws IOException {
		// Creates a TSV file containing the average fingerprint .
		// This file  will be fed into d3js framework to 
		// visualize the byte frequency.
		
		File tsvFile = new File(fileName + ".tsv");
	
			boolean fileExists = (tsvFile.exists()) ? true : tsvFile.createNewFile();
			PrintWriter writer;
			writer = new PrintWriter(tsvFile.getAbsolutePath(), "UTF-8");
			writer.println("Byte" + "\t" + "Frequency");
			for (int i = 0; i < 256; i++) {
				writer.println(i + "\t" + avgFingerprint[i]);
			}
			writer.close();

	
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// folderPath and fileType should be system arguments
		File datasetPath = new File(Constants.PATH_TO_STORE_DATA);
		for(File mimeTypeFolder:datasetPath.listFiles()){
			String folderPath = mimeTypeFolder.getAbsolutePath();
			String mimeType = folderPath.substring(folderPath.lastIndexOf("/")+1);
			
			File folder = (mimeType.equals("application-octet-stream")? new File(folderPath+"/"+Constants.TWENTY_FIVE_FOLDER): new File(folderPath+"/"+Constants.SEVENTY_FIVE_FOLDER));
			
			// Instantiating tika facade class
			
			// Calculating fingerprint for each file in the folder and adding it to common fingerprintMap
			for (File file : folder.listFiles()) {
				ByteFrequencyAnalyzer analyzer = new ByteFrequencyAnalyzer();
				try {
					analyzer.setFingerprint(ByteFrequencyAnalyzer.generateFingerprintOfFile(file.getAbsolutePath()));
					analyzer.normalizeFingerprint();
					analyzer.compandFingerprint();
					analyzer.printFingerprint();
					analyzer.addToFingerprintMap(file.getAbsolutePath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			double[] avgFingerprint = calculateAverageFingerprint();
			// Create a TSV file for feeding to d3js linechart program
			String mimeTypeFolderName = mimeType.replaceAll("/", "-");
			createTSVFile(folderPath+"/"+Constants.BFA_PREFIX+"_Results", avgFingerprint);
		}
	

	}
}