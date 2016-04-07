/**
 * 
 */
package mimetype;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 */
public class ByteFrequencyCrossCorrelation {

	/**
	 * @param args
	 */

	public static double[][] avgBFCCMatrix = new double[256][256];
	// public static double[][] individualCorrMatrix = new double[256][256];

	public void findCrossCorrelation(double[] fileFingerprint, long fileNumber) {
		// Finds the difference between the frequencies of every pair of bytes
		// in the file
		double[][] individualBFCCMatrix = new double[256][256];
		for (int i = 0; i < 256; i++) {
			for (int j = 0; j < 256; j++) {
				if (i > j) {
					individualBFCCMatrix[i][j] = fileFingerprint[i] - fileFingerprint[j];
					double correlationStrength = 1 - Math.abs(avgBFCCMatrix[i][j] - individualBFCCMatrix[i][j]);
					double avgCorrelationStrength = ((avgBFCCMatrix[j][i] * fileNumber) + correlationStrength)
							/ (fileNumber + 1);
					avgBFCCMatrix[i][j] = ((avgBFCCMatrix[i][j] * fileNumber) + individualBFCCMatrix[i][j])
							/ (fileNumber + 1);
					avgBFCCMatrix[j][i] = avgCorrelationStrength;

				}
				// Store the Number-of-files-seen at position array[0][0]
				else if (i == 0 && j == 0) {
					avgBFCCMatrix[i][j] = fileNumber + 1;
				}
			}
		}
	}

	private static void createTSVFile(String fileName) throws IOException {
		// TODO Auto-generated method stub
		// Creates a TSV file named "CrossCorrelation <mimetype>.tsv" containing
		// the average CrossCorrelation Strength .
		// This file will be fed into d3js framework to
		// visualize the Cross Correlation matrix.

		File tsvFile = new File(fileName + ".tsv");

		boolean fileExists = (tsvFile.exists()) ? true : tsvFile.createNewFile();
		PrintWriter writer;
		writer = new PrintWriter(tsvFile.getAbsolutePath(), "UTF-8");
		writer.println("Byte" + "\t" + "CorrelationStrength");
		for (int i = 0; i < 256; i++) {
			writer.print("\t" + i);

		}

		for (int i = 0; i < 256; i++) {
			writer.println();
			writer.print(i + "\t");
			for (int j = 0; j < 256; j++) {
				writer.print(avgBFCCMatrix[i][j] + "\t");
			}

		}
		writer.close();
	}

	public static void main(String[] args) {

		ByteFrequencyCrossCorrelation bfcc = new ByteFrequencyCrossCorrelation();
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


			long fileNumber = 0;
			try {
				for (File file : folder.listFiles()) {
					ByteFrequencyAnalyzer analyzer = new ByteFrequencyAnalyzer();

					analyzer.setFingerprint(ByteFrequencyAnalyzer.generateFingerprintOfFile(file.getAbsolutePath()));
					analyzer.normalizeFingerprint();
					analyzer.compandFingerprint();
					double[] fileFingerprint = analyzer.getFingerprint();
					bfcc.findCrossCorrelation(fileFingerprint, fileNumber);
					fileNumber++;
				}		
				createTSVFile(folderPath+"/"+Constants.BFCC_PREFIX+"_Results" + mimeType);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
