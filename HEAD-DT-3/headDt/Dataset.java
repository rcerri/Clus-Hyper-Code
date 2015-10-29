package headDt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.StringTokenizer;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.supervised.instance.StratifiedRemoveFolds;

public class Dataset {

	private static Instances data;
	private static Instances training;
	private static Instances validation;
	private static Instances test;
	private static int fold;
	private static int numFolds = 10;
	public static int numSetsPerGeneration = 5;

	private static boolean splitedFiles = false;
	private static boolean multipleData = false;
	private static ArrayList<Instances> metaTraining;
	public static ArrayList<Integer> dataIndices;
	public static ArrayList<Instances> trainingData;
	public static ArrayList<Instances> testData;
	private static ArrayList<String> metaTraningNames,metaTestNames;
	private static ArrayList<Instances> metaTest;


	private static String path, dataset;

	public Dataset(String dataset, String path) throws IOException{
		this.path = path;
		this.dataset = dataset;

		// if there are multiple datasets in the meta-sets, add them to the "metaTraining" and "metaTest" array lists.
		if(dataset.contains("txt")){
			multipleData = true;
			metaTraining = new ArrayList<Instances>();
			metaTest = new ArrayList<Instances>();
			metaTraningNames = new ArrayList<String>();
			metaTestNames = new ArrayList<String>();
			dataIndices = new ArrayList<Integer>();
			trainingData = new ArrayList<Instances>();
			testData = new ArrayList<Instances>();

			Instances newSet;
			FileReader set;
			String s = new String(path+dataset);
			BufferedReader reader = new BufferedReader(new FileReader(new File(s)));
			String training = reader.readLine();
			StringTokenizer token = new StringTokenizer(training, ",");
			while(token.hasMoreTokens()){
				String name = token.nextToken();
				set = new FileReader(path+name+".arff");
				newSet = new Instances(set);
				newSet.setClassIndex(newSet.numAttributes()-1);
				newSet.stratify(numFolds);
				newSet.setRelationName(name);
				metaTraining.add(newSet);
				metaTraningNames.add(newSet.relationName());
			}
			String test = reader.readLine();
			token = new StringTokenizer(test,",");
			while(token.hasMoreTokens()){
				String nameTest = token.nextToken();
				set = new FileReader(path+nameTest+".arff");
				newSet = new Instances(set);
				newSet.setClassIndex(newSet.numAttributes()-1);
				newSet.stratify(numFolds);
				metaTest.add(newSet);
				metaTestNames.add(nameTest);
			}

		} //end if multiple

		else{ //if there is a single dataset in the meta-set

			/* Regensburg -> para converter os .csv para .arff (método splitData)
			CSVLoader loader = new CSVLoader(); 
		    loader.setSource(new File(path+dataset+".csv"));
		    loader.setNoHeaderRowPresent(true);
		    data = loader.getDataSet();
		    data.setClassIndex(data.numAttributes()-1);
			data.stratify(numFolds);
			 */

			if (!splitedFiles) {
				// versão original do HEAD-DT
				FileReader reader = new FileReader(path+dataset+".arff");
				data = new Instances(reader);
				data.setClassIndex(data.numAttributes()-1);
				data.stratify(numFolds);
			}
			else {
				//*funcionando Regensburg -> para executar!
				
				FileReader reader = new FileReader(path+"/"+dataset+"/"+dataset+"-cv-"+fold+".arff");
				data = new Instances(reader);
				data.setClassIndex(data.numAttributes()-1);
				data.stratify(numFolds);
				//Regensburg */
			}

		}
		//	currentSeed = -1;
	}

	public static boolean isMultiple(){
		return multipleData;
	}

	public static Instances getFullData(){
		return data;
	}

	public static ArrayList<Instances> getMetaTraining(){
		return metaTraining;
	}

	public static ArrayList<String> getMetaTrainingNames(){
		return metaTraningNames;
	}

	public static ArrayList<String> getMetaTestNames(){
		return metaTestNames;
	}

	public static ArrayList<Instances> getMetaTest(){
		return metaTest;
	}

	public static void setFold(int f) {
		fold = f;
	}

	public static int getFold() {
		return fold;
	}

	public static void setNumFolds(int numFolds) {
		Dataset.numFolds = numFolds;
	}

	public static int getNumFolds() {
		return numFolds;
	}

	public static Instances getTrainingData(){
		return training;
	}

	public static Instances getValidationData(){
		return validation;
	}

	public static Instances getTestData(){
		return test;
	}




	public static void splitData(int seed) throws Exception{

		if (!splitedFiles) {
			Instances full = data.trainCV(numFolds,fold);

			StratifiedRemoveFolds train = new StratifiedRemoveFolds();
			StratifiedRemoveFolds valid = new StratifiedRemoveFolds();

			train.setInputFormat(full);
			train.setSeed(seed);
			train.setNumFolds(4);
			train.setFold(1);
			train.setInvertSelection(true);
			training = Filter.useFilter(full,train);

			valid.setInputFormat(full);
			valid.setSeed(seed);
			valid.setNumFolds(4);
			valid.setFold(1);
			valid.setInvertSelection(false);
			validation = Filter.useFilter(full, valid);

			test = data.testCV(numFolds,fold);
		}

		else {
			FileReader reader = new FileReader(path+"/"+dataset+"/"+dataset+"-cv-"+fold+".arff");
			Instances full = new Instances(reader);
			full.setClassIndex(full.numAttributes()-1);

			/*
			CSVLoader loader = new CSVLoader();
			loader.setNoHeaderRowPresent(true);
			String fileName = path+dataset+".1."+(fold+1)+".trn.csv";
		    loader.setSource(new File(fileName));
		    Instances full = loader.getDataSet();
			full.setClassIndex(full.numAttributes()-1);

			ArffSaver saver = new ArffSaver();
		    saver.setInstances(full);
		    saver.setFile(new File(path+dataset+".1."+(fold+1)+".trn.arff"));
		    saver.setDestination(new File(path+dataset+".1."+(fold+1)+".trn.arff"));
		    saver.writeBatch();
			 */

			StratifiedRemoveFolds train = new StratifiedRemoveFolds();
			StratifiedRemoveFolds valid = new StratifiedRemoveFolds();

			train.setInputFormat(full);
			train.setSeed(seed);
			train.setNumFolds(4);
			train.setFold(1);
			train.setInvertSelection(true);
			training = Filter.useFilter(full,train);

			valid.setInputFormat(full);
			valid.setSeed(seed);
			valid.setNumFolds(4);
			valid.setFold(1);
			valid.setInvertSelection(false);
			validation = Filter.useFilter(full, valid);


			/*
			loader = new CSVLoader();
			loader.setNoHeaderRowPresent(true);
			fileName = path+dataset+".1."+(fold+1)+".tst.csv";
		    loader.setSource(new File(fileName));
		    test = loader.getDataSet();
			 */

			reader = new FileReader(path+"/"+dataset+"/"+dataset+"-test-"+fold+".arff");
			test = new Instances(reader);
			test.setClassIndex(test.numAttributes()-1);

			/*
			saver = new ArffSaver();
		    saver.setInstances(test);
		    saver.setFile(new File(path+dataset+".1."+(fold+1)+".tst.arff"));
		    saver.setDestination(new File(path+dataset+".1."+(fold+1)+".tst.arff"));
		    saver.writeBatch();
			 */

		}
	}

	public static void splitTrainingTest() throws Exception{
		trainingData.clear();
		testData.clear();


		for(int i=0;i<metaTraining.size();i++){
			Instances data = metaTraining.get(i);
			StratifiedRemoveFolds forTraining = new StratifiedRemoveFolds();
			StratifiedRemoveFolds forTest = new StratifiedRemoveFolds();

			forTraining.setInputFormat(data);
			forTraining.setSeed(1);
			forTraining.setNumFolds(4);
			forTraining.setFold(1);
			forTraining.setInvertSelection(true);
			trainingData.add(Filter.useFilter(data,forTraining));

			forTest.setInputFormat(data);
			forTest.setSeed(1);
			forTest.setNumFolds(4);
			forTest.setFold(1);
			forTest.setInvertSelection(false);
			testData.add(Filter.useFilter(data, forTest));
		}

	}


	/** This method provides "numSetsPerGeneration" datasets per generation for fitness evaluation. At each generation, it offsets (size 1) the vector, i.e., a new dataset
	 *  is included and an old dataset is discarded. It guarantees that all datasets are equally used (same number of times) for evaluating individuals.  
	 * 
	 * @param numSetsPerGeneration
	 * @param numGenerations
	 * @throws Exception 
	 */
	public static void divideMetaTrainingPerGeneration(int numGenerations) throws Exception{
		double test = numGenerations % metaTraining.size();
		if(test != 0){
			System.out.println("Error! The number of sets in the training set must be a multiple of "+numGenerations);
			System.exit(1);
		}

		double numberOfVectors = numGenerations/metaTraining.size();


		ArrayList<Integer> vector = new ArrayList<Integer>();
		for(int i=0;i<metaTraining.size();i++){
			vector.add(i);
		}


		Collections.shuffle(vector,new Random(0));
		//	System.out.println("Vetor: "+vector);
		for(int i=0;i<numberOfVectors;i++){
			dataIndices.addAll(vector);
		}

		for(int i=0; i<numSetsPerGeneration - 1; i++){
			dataIndices.add(vector.get(i));
		}

		//		System.out.println("Vetor combinado: "+sampleTraining);

		if(dataIndices.size() < metaTraining.size() + (numSetsPerGeneration -1)){
			System.out.println("DEU MERDA AQUI!! CLASSE DATASET, METODO DE DIVISAO DE BASES POR GERACAO");
			System.exit(1);
		}

		divideTrainingAndTest();

		//	System.out.println("Tamanho do sampleTraining, s� para verificar = "+sampleTraining.size());
	}


	public static void divideTrainingAndTest() throws Exception{

		trainingData.clear();
		testData.clear();


		for(int i=0;i<dataIndices.size();i++){
			Instances data = metaTraining.get(dataIndices.get(i));
			StratifiedRemoveFolds forTraining = new StratifiedRemoveFolds();
			StratifiedRemoveFolds forTest = new StratifiedRemoveFolds();

			forTraining.setInputFormat(data);
			forTraining.setSeed(1);
			forTraining.setNumFolds(4);
			forTraining.setFold(1);
			forTraining.setInvertSelection(true);
			trainingData.add(Filter.useFilter(data,forTraining));

			forTest.setInputFormat(data);
			forTest.setSeed(1);
			forTest.setNumFolds(4);
			forTest.setFold(1);
			forTest.setInvertSelection(false);
			testData.add(Filter.useFilter(data, forTest));
		}


	}

	public static boolean isSplitedFiles() {
		return splitedFiles;
	}

	public static void setSplitedFiles(boolean splitedFiles) {
		Dataset.splitedFiles = splitedFiles;
	}



}
