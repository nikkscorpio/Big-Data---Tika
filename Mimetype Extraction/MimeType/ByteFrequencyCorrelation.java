/**
 * 
 */
package mimetype;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * @author
 *
 */
public class ByteFrequencyCorrelation {

	public static double[] avgBFAArray = new double[256];
	public static double[] avgCorrelationStrengthArray = new double[256];

	public static double[] getAvgCorrelationStrengthArray() {
		return avgCorrelationStrengthArray;
	}

	public static void setAvgCorrelationStrengthArray(double[] avgCorrelationStrengthArray) {
		ByteFrequencyCorrelation.avgCorrelationStrengthArray = avgCorrelationStrengthArray;
	}

	/**
	 * @param mimetype
	 *            : Mimetype for the input files
	 * @param folderPath
	 *            : Folder containing the input files
	 * @throws IOException
	 * 
	 */

	private static void retrieveAverageBFA(String mimeType) throws IOException {
		// TODO Auto-generated method stub
		FileInputStream fstream = new FileInputStream(mimeType + ".tsv");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine = br.readLine();
		// Read File Line By Line
		int i = 0;
		while ((strLine = br.readLine()) != null) {
			String[] arr = strLine.split("\t");
			avgBFAArray[i++] = Double.parseDouble(arr[1]);
		}
		// Close the input stream
		br.close();
	}

	private static void findCorrelationStrength(double[] fileFingerprint, long fileNumber) {
		// Finds difference between the Byte values of the avg BFA array and the
		// input file's BFA array
		// The difference is then used to determine byte's of high correlation
		// and low correlation i.e. Correlation Strength

		for (int i = 0; i < fileFingerprint.length; i++) {
			double difference = fileFingerprint[i] - avgBFAArray[i];
			double correlationStrength = 1 - Math.abs(difference);
			findAvgCorrStrength(correlationStrength, fileNumber, i);
		}
	}

	private static void findAvgCorrStrength(double newCorrelationStrength, long fileNumber, int byteNumber) {
		// TODO Auto-generated method stub
		avgCorrelationStrengthArray[byteNumber] = ((avgCorrelationStrengthArray[byteNumber] * fileNumber)
				+ newCorrelationStrength) / (fileNumber + 1);
	}

	public static void createTSVFile(String fileName, double[] avgCorrelationStrength) throws IOException {
		// Creates a TSV file named "Correlation <mimetype>.tsv" containing the
		// average Correlation Strength .
		// This file will be fed into d3js framework to
		// visualize the byte frequency.

		File tsvFile = new File(fileName + ".tsv");

		boolean fileExists = (tsvFile.exists()) ? true : tsvFile.createNewFile();
		PrintWriter writer;
		writer = new PrintWriter(tsvFile.getAbsolutePath(), "UTF-8");
		writer.println("Byte" + "\t" + "CorrelationStrength");
		for (int i = 0; i < 256; i++) {
			writer.println(i + "\t" + avgCorrelationStrength[i]);
		}
		writer.close();

	}

	public static void main(String[] args) throws IOException {

		File datasetPath = new File(Constants.PATH_TO_STORE_DATA);
		for (File mimeTypeFolder : datasetPath.listFiles()) {
			String folderPath = mimeTypeFolder.getAbsolutePath();
			String mimeType = folderPath.substring(folderPath.lastIndexOf("/") + 1);

			File folder = new File(folderPath + "/" + Constants.TWENTY_FIVE_FOLDER);

			// TODO Auto-generated method stub
			// folderPath and fileType should be system arguments

			mimeType = mimeType.replaceAll("/", "-");

			// Retrieving the .TSV file for the Average BFA fingerprint of the
			// corresponding fileType

			if (new File(folderPath + "/" + Constants.BFDC_PREFIX + "_Results.tsv").exists()) {
				retrieveAverageBFA(folderPath + "/" + Constants.BFDC_PREFIX + "_Results");
			}
			long fileNumber = 0;
			for (File file : folder.listFiles()) {
				ByteFrequencyAnalyzer analyzer = new ByteFrequencyAnalyzer();
				try {
					analyzer.setFingerprint(ByteFrequencyAnalyzer.generateFingerprintOfFile(file.getAbsolutePath()));
					analyzer.normalizeFingerprint();
					analyzer.compandFingerprint();
					double[] fileFingerprint = analyzer.getFingerprint();
					findCorrelationStrength(fileFingerprint, fileNumber);
					fileNumber++;
					createTSVFile(folderPath + "/" + Constants.BFDC_PREFIX + "_Results", avgCorrelationStrengthArray);

				} catch (Exception e) {

				}
			}
		}

	}

}
