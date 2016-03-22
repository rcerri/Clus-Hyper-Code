package ga;

import java.io.IOException;

public class Dataset {


	private static int currentFold;
	private static int numFolds;
	private static String path, fileName;

	
	public Dataset(String path, String fileName, int nFolds) throws IOException{
		this.path = path;
		this.numFolds = nFolds;
		this.fileName = fileName;
	}

	

	public static String getFileName() {
		return fileName;
	}


	public static void setFileName(String fileName) {
		Dataset.fileName = fileName;
	}



	public static void setCurrentFold(int currentFold) {
		Dataset.currentFold = currentFold;
	}



	public static int getCurrentFold() {
		return currentFold;
	}


	public static int getNumFolds() {
		return numFolds;
	}


	public static void setNumFolds(int numFolds) {
		Dataset.numFolds = numFolds;
	}


	public static String getPath() {
		return path;
	}


	public static void setPath(String path) {
		Dataset.path = path;
	}

}
