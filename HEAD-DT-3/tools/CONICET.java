package tools;

import headDt.Dataset;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;


public class CONICET {


	public static void main(String args[]) throws Exception{

		new Dataset(args[0],"./datasets/");
		String resultado = "./resultados/";
	//	new Dataset(args[0],"/Users/rodrigobarros/Desktop/Datasets/CONICET/");
	//	String resultado = "/Users/rodrigobarros/Desktop/Resultados/CONICET/";
		int numMeasures = 6;
		int numAlgorithms = 4;
		String medidas[] = {"Weighted FMeasure", "Unweighted FMeasure", "Weighted Precision", "Unweighted Precision", "Weighted Recall", "Unweighted Recall"};
		double measures[][] = new double[numAlgorithms][numMeasures];
		FileWriter fw = new FileWriter(resultado+args[0]+".csv");
		PrintWriter pw = new PrintWriter(fw);

		for(int i=0;i<numAlgorithms;i++){
			for(int j=0;j<numMeasures;j++){
				measures[i][j] = 0;
			}
		}

		pw.println("Measure,J48,Random Forests,SVM,MLP"); 


		J48 j48 = new J48();
		RandomForest rf = new RandomForest();
		rf.setNumTrees(1000);
		SMO svm = new SMO();
		MultilayerPerceptron mlp = new MultilayerPerceptron();


		Evaluation evalJ48 = new Evaluation(Dataset.getFullData());
		Evaluation evalRF = new Evaluation(Dataset.getFullData());
		Evaluation evalSVM = new Evaluation(Dataset.getFullData());
		Evaluation evalMLP = new Evaluation(Dataset.getFullData());

		evalJ48.crossValidateModel(j48, Dataset.getFullData(), 10, new Random(1));
		evalRF.crossValidateModel(rf,Dataset.getFullData(), 10, new Random(1));
		evalSVM.crossValidateModel(svm,Dataset.getFullData(), 10, new Random(1));
		evalMLP.crossValidateModel(mlp,Dataset.getFullData(), 10, new Random(1));

		for(int j=0; j< Dataset.getFullData().numClasses();j++){
			measures[0][3]+= evalJ48.precision(j);
			measures[0][5]+= evalJ48.recall(j);

			measures[1][3]+= evalRF.precision(j);
			measures[1][5]+= evalRF.recall(j);

			measures[2][3]+= evalSVM.precision(j);
			measures[2][5]+= evalSVM.recall(j);

			measures[3][3]+= evalMLP.precision(j);
			measures[3][5]+= evalMLP.recall(j);

		}

		measures[0][3]/= Dataset.getFullData().numClasses();
		measures[0][5]/= Dataset.getFullData().numClasses();

		measures[1][3]/= Dataset.getFullData().numClasses();
		measures[1][5]/= Dataset.getFullData().numClasses();

		measures[2][3]/= Dataset.getFullData().numClasses();
		measures[2][5]/= Dataset.getFullData().numClasses();

		measures[3][3]/= Dataset.getFullData().numClasses();
		measures[3][5]/= Dataset.getFullData().numClasses();



		measures[0][0] = evalJ48.weightedFMeasure();
		measures[0][1] = evalJ48.unweightedMicroFmeasure();
		measures[0][2] = evalJ48.weightedPrecision();
		measures[0][4] = evalJ48.weightedRecall();

		measures[1][0] = evalRF.weightedFMeasure();
		measures[1][1] = evalRF.unweightedMicroFmeasure();
		measures[1][2] = evalRF.weightedPrecision();
		measures[1][4] = evalRF.weightedRecall();

		measures[2][0] = evalSVM.weightedFMeasure();
		measures[2][1] = evalSVM.unweightedMicroFmeasure();
		measures[2][2] = evalSVM.weightedPrecision();
		measures[2][4] = evalSVM.weightedRecall();

		measures[3][0] = evalMLP.weightedFMeasure();
		measures[3][1] = evalMLP.unweightedMicroFmeasure();
		measures[3][2] = evalMLP.weightedPrecision();
		measures[3][4] = evalMLP.weightedRecall();


		for(int j=0; j< numMeasures; j++){
			pw.print(medidas[j]);
			for(int i=0; i< numAlgorithms; i++){
				pw.print(","+measures[i][j]);
			}
			pw.println();
		}

		//		}
	pw.close();
	fw.close();

	}

}
