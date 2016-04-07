package mimetype;

import java.io.File;
import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.tika.Tika;

import com.google.common.io.Files;

public class ClassifyFiles {


	void retrieveFiles(String path) {
		File folder = new File(path);
		if (folder.isFile()) {
			classifyIntoMimeType(folder);
		}
		if (null != folder && folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File file : files) {
				retrieveFiles(file.getAbsolutePath());
			}
		}
	}

	void classifyIntoMimeType(File folder) {
		if (!(folder.getName().equalsIgnoreCase(".DS_Store"))) {
			String fileName = folder.getName();
			//System.out.println(folder.getName());
			Tika tika = new Tika();
			try {
				File storageFolder = new File(Constants.PATH_TO_STORE_DATA);
				Boolean folderExists = ((storageFolder.exists() && storageFolder.isDirectory()) ? true
						: storageFolder.mkdir());
				String mediaType = tika.detect(folder);
				if (Arrays.asList(Constants.FIRST_15_MIMETYPES).contains(mediaType)) {
					mediaType = mediaType.replaceAll("/", "-");
					//System.out.println(mediaType);
					File mimetypeFolder = new File(Constants.PATH_TO_STORE_DATA + "/" + mediaType);
					File newFileCreated = new File(mimetypeFolder.getAbsolutePath() + "/" + fileName);
					newFileCreated.createNewFile();
					Files.copy(folder, newFileCreated);
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private void createFolders() {
		// TODO Auto-generated method stub
		for (String mediaType : Constants.FIRST_15_MIMETYPES) {
			mediaType = mediaType.replaceAll("/", "-");
			File mimetypeFolder = new File(Constants.PATH_TO_STORE_DATA + "/" + mediaType);
			Boolean mimeTypeFolderExists = (mimetypeFolder.exists()) ? true : mimetypeFolder.mkdir();
			Boolean seventyFiveFolder = (new File(
					mimetypeFolder.getAbsolutePath() + "/" + Constants.SEVENTY_FIVE_FOLDER)).exists() ? true
							: (new File(mimetypeFolder.getAbsolutePath() + "/" + Constants.SEVENTY_FIVE_FOLDER))
									.mkdir();
			Boolean twentyFiveFolder = (new File(
					mimetypeFolder.getAbsolutePath() + "/" + Constants.TWENTY_FIVE_FOLDER)).exists() ? true
							: (new File(mimetypeFolder.getAbsolutePath() + "/" + Constants.TWENTY_FIVE_FOLDER))
									.mkdir();
//			Boolean BFAFolder = (new File(mimetypeFolder.getAbsolutePath() + "/" + Constants.BFA_FOLDER)).exists()
//					? true : (new File(mimetypeFolder.getAbsolutePath() + "/" + Constants.BFA_FOLDER)).mkdir();
//			Boolean BFDCFolder = (new File(mimetypeFolder.getAbsolutePath() + "/" + Constants.BFDC_FOLDER)).exists()
//					? true : (new File(mimetypeFolder.getAbsolutePath() + "/" + Constants.BFDC_FOLDER)).mkdir();
//			Boolean BFCCFolder = (new File(mimetypeFolder.getAbsolutePath() + "/" + Constants.BFCC_FOLDER)).exists()
//					? true : (new File(mimetypeFolder.getAbsolutePath() + "/" + Constants.BFCC_FOLDER)).mkdir();
//			Boolean FHTFolder = (new File(mimetypeFolder.getAbsolutePath() + "/" + Constants.FHT_FOLDER)).exists()
//					? true : (new File(mimetypeFolder.getAbsolutePath() + "/" + Constants.FHT_FOLDER)).mkdir();

		}

	}

	public void moveFilesIntoRespectiveFolders(File parentFolder) {
		File[] files = parentFolder.listFiles();
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < files.length; i++) {
			list.add(i);
		}
		Collections.shuffle(list);
		for (int i = 0; i < (3 * list.size()) / 4; i++) {
			File fileToMove = files[list.get(i)];
			fileToMove.renameTo(new File(Constants.SEVENTY_FIVE_FOLDER + fileToMove.getName()));
		}
		for (int i = (3 * list.size()) / 4; i < list.size(); i++) {
			File fileToMove = files[list.get(i)];
			fileToMove.renameTo(new File(Constants.TWENTY_FIVE_FOLDER + fileToMove.getName()));
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClassifyFiles cs = new ClassifyFiles();
		cs.createFolders();
		cs.retrieveFiles(Constants.DATASET_PATH);
		for (String mediaType : Constants.FIRST_15_MIMETYPES) {
			mediaType = mediaType.replaceAll("/", "-");
			File mimetypeFolder = new File(Constants.PATH_TO_STORE_DATA + "/" + mediaType);
			cs.moveFilesIntoRespectiveFolders(mimetypeFolder);
		}
	}

}