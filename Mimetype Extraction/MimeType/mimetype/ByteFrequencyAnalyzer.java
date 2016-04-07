package mimetype;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.Tika;

public class ByteFrequencyAnalyzer {
	double[] fingerprint = new double[256];
	/*public static double[] generateBFDFingerprint(String ofFilesinPath){
		double[] outputFingerpint = new double[256];

	}*/

	public double[] getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(double[] fingerprint) {
		this.fingerprint = fingerprint;
	}
	
	public double[] normalizeFingerprint(){
		double[] output
	}

	public static double[] generateFingerprintOfFile(String filePath) throws IOException{
		double[] outputFingerpint = new double[256];
		File file = new File(filePath);
		long length = file.length();
		FileInputStream fis = new FileInputStream(filePath);
		for(long i = 0 ; i < length; i++){
			byte b = (byte) fis.read();
			outputFingerpint[b+128] += 1;
		}
		return outputFingerpint;
	}
	
	public void printFingerprint(){
		for(int i = 0 ; i < 256; i++){
			System.out.print(fingerprint[i]+" ");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file = new File("C:/Users/Shriram/Desktop/Shriram Kalpathy Mohan Resume.pdf");

		//Instantiating tika facade class 
		Tika tika = new Tika();
		ByteFrequencyAnalyzer analyzer = new ByteFrequencyAnalyzer();

		//detecting the file type using detect method
		String filetype = "";
		try {
			filetype = tika.detect(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(filetype);
		try {
			analyzer.setFingerprint(ByteFrequencyAnalyzer.generateFingerprintOfFile(file.getAbsolutePath()));
			analyzer.printFingerprint();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
