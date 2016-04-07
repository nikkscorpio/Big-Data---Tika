package mimetype;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.tika.Tika;

public class FileHeaderTraileranalyzer {

	// Map containing fingerprints of all the files of a particular file type
		static HashMap<String, double[][]> h1fingerprintMap = new HashMap<String, double[][]>();
		static HashMap<String, double[][]> t1fingerprintMap = new HashMap<String, double[][]>();
		static HashMap<String, double[][]> h2fingerprintMap = new HashMap<String, double[][]>();
		static HashMap<String, double[][]> t2fingerprintMap = new HashMap<String, double[][]>();
		static HashMap<String, double[][]> h3fingerprintMap = new HashMap<String, double[][]>();
		static HashMap<String, double[][]> t3fingerprintMap = new HashMap<String, double[][]>();
		
		double[][] headerFingerprint1 = new double[4][256];
		double[][] trailerFingerprint1 = new double[4][256];

		double[][] headerFingerprint2 = new double[8][256];
		double[][] trailerFingerprint2 = new double[8][256];

		double[][] headerFingerprint3 = new double[16][256];
		double[][] trailerFingerprint3 = new double[16][256];
		
		
		
		
		
		

		
		/*
		 * public static double[] generateBFDFingerprint(String ofFilesinPath){
		 * double[] outputFingerpint = new double[256];  }
		 */

		
		public double[][] getHeaderFingerprint1() {
			return headerFingerprint1;
		}

		public void setHeaderFingerprint1(double[][] headerFingerprint1) {
			this.headerFingerprint1 = headerFingerprint1;
		}

		public double[][] getTrailerFingerprint1() {
			return trailerFingerprint1;
		}

		public void setTrailerFingerprint1(double[][] trailerFingerprint1) {
			this.trailerFingerprint1 = trailerFingerprint1;
		}

		public double[][] getHeaderFingerprint2() {
			return headerFingerprint2;
		}

		public void setHeaderFingerprint2(double[][] headerFingerprint2) {
			this.headerFingerprint2 = headerFingerprint2;
		}

		public double[][] getTrailerFingerprint2() {
			return trailerFingerprint2;
		}

		public void setTrailerFingerprint2(double[][] trailerFingerprint2) {
			this.trailerFingerprint2 = trailerFingerprint2;
		}

		public double[][] getHeaderFingerprint3() {
			return headerFingerprint3;
		}

		public void setHeaderFingerprint3(double[][] headerFingerprint3) {
			this.headerFingerprint3 = headerFingerprint3;
		}

		public double[][] getTrailerFingerprint3() {
			return trailerFingerprint3;
		}

		public void setTrailerFingerprint3(double[][] trailerFingerprint3) {
			this.trailerFingerprint3 = trailerFingerprint3;
		}

				/*public void normalizeFingerprint() {
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
*/
		//---------------T1
				public static double[][] generateheaderFingerprintOfFileh1(String filePath) throws IOException {
					double[][] headerFingerprint1 = new double[4][256];
					File file = new File(filePath);
					long length = file.length();
					FileInputStream fis = new FileInputStream(filePath);
					for (int i = 0; i < 4; i++) {
						
						byte b = (byte) fis.read();
						headerFingerprint1[i][b + 128] += 1;
						}
					
					return headerFingerprint1;
				}
				
				public static double[][] generateTrailerFingerprintOfFilet1(String filePath) throws IOException {
					double[][] trailerFingerprint1 = new double[4][256];
					File file = new File(filePath);
					long length = file.length();
			        //long i = length - 4;
			        int j=0 ;
					FileInputStream fis = new FileInputStream(filePath);
					byte[] b = new byte[(int)length];
					for (long i = 0; i < length; i++)
					{
						b[j] = (byte) fis.read();
						j++;
					}
					int blen = b.length;
					int k=0;
					for (j = (blen)-4; j < blen; j++) {
						
						byte b1 = b[j];
						trailerFingerprint1[k][b1+128] += 1;
						k++;
					}
					
					/*System.out.println("*******************************************");
					for(int i=0;i<4;i++)
					{
						for( j=0;j<256;j++)
						{
							System.out.print(trailerFingerprint1[i][j]);
						}
						System.out.println();
					}
					System.out.println("*******************************************");
			*/
					
					return trailerFingerprint1;
				}
				
				//---------------T2
				public static double[][] generateheaderFingerprintOfFileh2(String filePath) throws IOException {
					double[][] headerFingerprint2 = new double[8][256];
					File file = new File(filePath);
					long length = file.length();
					FileInputStream fis = new FileInputStream(filePath);
					for (int i = 0; i < 8; i++) {
					
						byte b = (byte) fis.read();
						headerFingerprint2[i][b + 128] += 1;
						
					}
					
					
					return headerFingerprint2;
				}
				
				public static double[][] generateTrailerFingerprintOfFilet2(String filePath) throws IOException {
					double[][] trailerFingerprint2 = new double[8][256];
					File file = new File(filePath);
					long length = file.length();
			        //long i = length - 8;
			
			        int j=0 ;
					FileInputStream fis = new FileInputStream(filePath);
					byte[] b = new byte[(int)length];
					for (long i = 0; i < length; i++)
					{
						b[j] = (byte) fis.read();
						j++;
					}
					int blen = b.length;
					int k=0;
					for (j = (blen)-8; j < blen; j++) {
						
						byte b1 = b[j];
						trailerFingerprint2[k][b1+128] += 1;
						k++;
					}
					
					return trailerFingerprint2;
				}
				
				//---------------T3
				public static double[][] generateheaderFingerprintOfFileh3(String filePath) throws IOException {
							double[][] headerFingerprint3 = new double[16][256];
							File file = new File(filePath);
							long length = file.length();
							FileInputStream fis = new FileInputStream(filePath);
							for (int i = 0; i < 16; i++) {
								byte b = (byte) fis.read();
								headerFingerprint3[i][b+128] += 1;
								
							}
							return headerFingerprint3;
				}
					
				public static double[][] generateTrailerFingerprintOfFilet3(String filePath) throws IOException {
							double[][] trailerFingerprint3 = new double[16][256];
							File file = new File(filePath);
							long length = file.length();
					        //long i = length - 16;
					
					        int j=0 ;
							FileInputStream fis = new FileInputStream(filePath);
							byte[] b = new byte[(int)length];
							for (long i = 0; i < length; i++)
							{
								b[j] = (byte) fis.read();
								j++;
							}
							int blen = b.length;
							int k=0;
							for (j = (blen)-16; j < blen; j++) {
								
								byte b1 = b[j];
								trailerFingerprint3[k][b1+128] += 1;
								k++;
							}
							
					
							return trailerFingerprint3;
				}
		
		/*public void printFingerprint() {
			for (int i = 0; i < 256; i++) {
				System.out.print(fingerprint[i] + " ");
			}
		}*/

		private void addToFingerprintMaph1(String absolutePath) {
			// Adds the current File's fingerprint to the Common fingerprintmap with
			// key as the Filepath
			
			h1fingerprintMap.put(absolutePath, headerFingerprint1);

		}

		public static double[][] calculateAverageFingerprinth1() {
			// Calculates the average fingerprint of the files of a single filetype
			Iterator<String> it = h1fingerprintMap.keySet().iterator();
			//FileHeaderTraileranalyzer analyzer = new FileHeaderTraileranalyzer();
			double[][] avgFingerprint = new double[4][256];
			while (it.hasNext()) {
				String path = it.next().toString();
				double[][] fgrprt = h1fingerprintMap.get(path);
				for (int i = 0; i < 4; i++) {
					for(int j=0;j<256;j++)
					{
						//System.out.println(fgrprt[i][j]);
						avgFingerprint[i][j] += fgrprt[i][j];
				    }
			}
			for (int i = 0; i < 4; i++) {
				
				for(int j=0;j<256;j++)
				{
					
					avgFingerprint[i][j] = avgFingerprint[i][j] / h1fingerprintMap.size();
			    }
				
			}
			
		}
			
			
			return avgFingerprint;
		}

		private void addToFingerprintMaph2(String absolutePath) {
			// Adds the current File's fingerprint to the Common fingerprintmap with
			// key as the Filepath
			h2fingerprintMap.put(absolutePath, headerFingerprint2);
			
			
		}

		public static double[][] calculateAverageFingerprinth2() {
			// Calculates the average fingerprint of the files of a single filetype
			Iterator<String> it = h2fingerprintMap.keySet().iterator();
			//FileHeaderTraileranalyzer analyzer = new FileHeaderTraileranalyzer();
			double[][] avgFingerprint = new double[8][256];
			while (it.hasNext()) {
				String path = it.next().toString();
				double[][] fgrprt = h2fingerprintMap.get(path);
				for (int i = 0; i < 8; i++) {
					for(int j=0;j<256;j++)
					{
						//System.out.println(fgrprt[i][j]);
						avgFingerprint[i][j] += fgrprt[i][j];
				    }
			}
			for (int i = 0; i < 8; i++) {
				
				for(int j=0;j<256;j++)
				{
				//	System.out.println(fgrprt[i][j]);
					avgFingerprint[i][j] = avgFingerprint[i][j] / h2fingerprintMap.size();
			    }
				
			}
			
		}
			
			return avgFingerprint;
		}

		private void addToFingerprintMaph3(String absolutePath) {
			// Adds the current File's fingerprint to the Common fingerprintmap with
			// key as the Filepath
			h3fingerprintMap.put(absolutePath, headerFingerprint3);

		}

		public static double[][] calculateAverageFingerprinth3() {
			// Calculates the average fingerprint of the files of a single filetype
			Iterator<String> it = h3fingerprintMap.keySet().iterator();
			//FileHeaderTraileranalyzer analyzer = new FileHeaderTraileranalyzer();
			double[][] avgFingerprint = new double[16][256];
			while (it.hasNext()) {
				String path = it.next().toString();
				double[][] fgrprt = h3fingerprintMap.get(path);
				for (int i = 0; i < 16; i++) {
					for(int j=0;j<256;j++)
					{
						//System.out.println(fgrprt[i][j]);
						avgFingerprint[i][j] += fgrprt[i][j];
				    }
			}
			for (int i = 0; i < 16; i++) {
				
				for(int j=0;j<256;j++)
				{
				//	System.out.println(fgrprt[i][j]);
					avgFingerprint[i][j] = avgFingerprint[i][j] / h3fingerprintMap.size();
			    }
				
			}
			
		}
			System.out.println("HPPP*******************************************");
			for(int i=0;i<16;i++)
			{
				for( int j=0;j<256;j++)
				{
					System.out.print("  "+String.format("%.8f", avgFingerprint[i][j]));
				}
				System.out.println();
			}
			System.out.println("*******************************************");
			return avgFingerprint;
		}
	
		
		
		
		private void addToFingerprintMapt1(String absolutePath) {
			// Adds the current File's fingerprint to the Common fingerprintmap with
			// key as the Filepath
			t1fingerprintMap.put(absolutePath, trailerFingerprint1);

		}

		public static double[][] calculateAverageFingerprintt1() {
			// Calculates the average fingerprint of the files of a single filetype
			Iterator<String> it = t1fingerprintMap.keySet().iterator();
			//FileHeaderTraileranalyzer analyzer = new FileHeaderTraileranalyzer();
			double[][] avgFingerprint = new double[4][256];
			while (it.hasNext()) {
				String path = it.next().toString();
				double[][] fgrprt = t1fingerprintMap.get(path);
				for (int i = 0; i < 4; i++) {
					for(int j=0;j<256;j++)
					{
						//System.out.println(fgrprt[i][j]);
						avgFingerprint[i][j] += fgrprt[i][j];
				    }
			}
			for (int i = 0; i < 4; i++) {
				
				for(int j=0;j<256;j++)
				{
					//System.out.println(fgrprt[i][j]);
					avgFingerprint[i][j] = avgFingerprint[i][j] / t1fingerprintMap.size();
			    }
				
			}
			
		}
			return avgFingerprint;
		}

		private void addToFingerprintMapt2(String absolutePath) {
			// Adds the current File's fingerprint to the Common fingerprintmap with
			// key as the Filepath
			t2fingerprintMap.put(absolutePath, trailerFingerprint2);

		}

		public static double[][] calculateAverageFingerprintt2() {
			// Calculates the average fingerprint of the files of a single filetype
			Iterator<String> it = t2fingerprintMap.keySet().iterator();
			//FileHeaderTraileranalyzer analyzer = new FileHeaderTraileranalyzer();
			double[][] avgFingerprint = new double[8][256];
			while (it.hasNext()) {
				String path = it.next().toString();
				double[][] fgrprt = t2fingerprintMap.get(path);
				for (int i = 0; i < 8; i++) {
					for(int j=0;j<256;j++)
					{
						//System.out.println(fgrprt[i][j]);
						avgFingerprint[i][j] += fgrprt[i][j];
				    }
			}
			for (int i = 0; i < 8; i++) {
				
				for(int j=0;j<256;j++)
				{
					//System.out.println(fgrprt[i][j]);
					avgFingerprint[i][j] = avgFingerprint[i][j] / t2fingerprintMap.size();
			    }
				
			}
			
		}
			return avgFingerprint;
		}

		private void addToFingerprintMapt3(String absolutePath) {
			// Adds the current File's fingerprint to the Common fingerprintmap with
			// key as the Filepath
			t3fingerprintMap.put(absolutePath, trailerFingerprint3);

		}

		public static double[][] calculateAverageFingerprintt3() {
			// Calculates the average fingerprint of the files of a single filetype
			Iterator<String> it = t3fingerprintMap.keySet().iterator();
			//FileHeaderTraileranalyzer analyzer = new FileHeaderTraileranalyzer();
			double[][] avgFingerprint = new double[16][256];
			while (it.hasNext()) {
				String path = it.next().toString();
				double[][] fgrprt = t3fingerprintMap.get(path);
				for (int i = 0; i < 16; i++) {
					for(int j=0;j<256;j++)
					{
						//System.out.println(fgrprt[i][j]);
						avgFingerprint[i][j] += fgrprt[i][j];
				    }
			}
			for (int i = 0; i < 16; i++) {
				
				for(int j=0;j<256;j++)
				{
					//System.out.println(fgrprt[i][j]);
					avgFingerprint[i][j] = avgFingerprint[i][j] / t3fingerprintMap.size();
			    }
				
			}
			
		}
			return avgFingerprint;
		}
		
		
		public static void createTSVFile(String fileName, double[][] avgFingerprint) {
			// Creates a TSV file containing the average fingerprint .
			// This file  will be fed into d3js framework to 
			// visualize the byte frequency.
			
			File jsonFile = new File(fileName + ".json");
			try {
				boolean fileExists = (jsonFile.exists()) ? true : jsonFile.createNewFile();
				PrintWriter writer;
				writer = new PrintWriter(jsonFile.getAbsolutePath(), "UTF-8");
				writer.println("File" + "\t" + "Header/Trailer");
				for (int i = 0; i < avgFingerprint.length; i++) {
					for(int j=0;j<256;j++){
					writer.print("\t" + avgFingerprint[i][j]);
				}
					writer.println();
				}
				writer.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		public static void main(String[] args) throws IOException {
			// TODO Auto-generated method stub
			
			File datasetPath = new File(Constants.PATH_TO_STORE_DATA+"/application-xml");
			
				
		
			// Calculating fingerprint for each file in the folder and adding it to common fingerprintMap
			for (File file : datasetPath.listFiles()) {
				if(file.isFile()){
				FileHeaderTraileranalyzer analyzer = new FileHeaderTraileranalyzer();
				try {
					analyzer.setHeaderFingerprint1(FileHeaderTraileranalyzer.generateheaderFingerprintOfFileh1(file.getAbsolutePath()));
					analyzer.setTrailerFingerprint1(FileHeaderTraileranalyzer.generateTrailerFingerprintOfFilet1(file.getAbsolutePath()));
					analyzer.setHeaderFingerprint2(FileHeaderTraileranalyzer.generateheaderFingerprintOfFileh2(file.getAbsolutePath()));
					analyzer.setTrailerFingerprint2(FileHeaderTraileranalyzer.generateTrailerFingerprintOfFilet2(file.getAbsolutePath()));
					analyzer.setHeaderFingerprint3(FileHeaderTraileranalyzer.generateheaderFingerprintOfFileh3(file.getAbsolutePath()));
					analyzer.setTrailerFingerprint3(FileHeaderTraileranalyzer.generateTrailerFingerprintOfFilet3(file.getAbsolutePath()));
					//analyzer.normalizeFingerprint();
					//analyzer.compandFingerprint();
					//analyzer.printFingerprint();
					analyzer.addToFingerprintMaph1(file.getAbsolutePath());
					analyzer.addToFingerprintMapt1(file.getAbsolutePath());
					analyzer.addToFingerprintMaph2(file.getAbsolutePath());
					analyzer.addToFingerprintMapt2(file.getAbsolutePath());
					analyzer.addToFingerprintMaph3(file.getAbsolutePath());
					analyzer.addToFingerprintMapt3(file.getAbsolutePath());
					System.out.println("11");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		}
			double[][] avgFingerprinth1 = calculateAverageFingerprinth1();
			
			double[][] avgFingerprintt1 = calculateAverageFingerprintt1();
			
			double[][] avgFingerprinth2 = calculateAverageFingerprinth2();
			
			
			
			double[][] avgFingerprintt2 = calculateAverageFingerprintt2();
			
			
			double[][] avgFingerprinth3 = calculateAverageFingerprinth3();
			double[][] avgFingerprintt3 = calculateAverageFingerprintt3();
			// Create a TSV file for feeding to d3js linechart program
			String mimeTypeFolderName = datasetPath + "/" + Constants.FHT_PREFIX + "_Results".replaceAll("/", "-");
			createTSVFile(mimeTypeFolderName+"_H1", avgFingerprinth1);
			createTSVFile(mimeTypeFolderName+"_T1", avgFingerprintt1);
			createTSVFile(mimeTypeFolderName+"_H2", avgFingerprinth2);
			createTSVFile(mimeTypeFolderName+"_T2", avgFingerprintt2);
			createTSVFile(mimeTypeFolderName+"_H3", avgFingerprinth3);
			createTSVFile(mimeTypeFolderName+"_T3", avgFingerprintt3);
		
	
	
	
		}
}
