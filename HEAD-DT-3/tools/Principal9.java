package tools;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;
import weka.classifiers.Evaluation;
import weka.core.*;
import weka.classifiers.trees.*;

public class Principal9 {

	static ArrayList<String> measures;
	static ArrayList<String> DataSets;
	static String dirCurrent, dirResults;
	static int folds;
	static double table[][][];
	static String classifier;
	


	public static void main(String[] args) throws Exception {
		inicialization();
		classifier = new String("SimpleCart");
		dirResults += classifier+"/";
		new File(dirResults).mkdir(); 

		for (int base = 0; base < DataSets.size(); base++) {
			
			double predictedClassification[];
			
			System.out.println(DataSets.get(base));
			String TreesFile = dirResults + "Trees"+classifier+".txt";
			PrintWriter pTreesFile = new PrintWriter(TreesFile);

			FileReader readerData = new FileReader(dirCurrent+DataSets.get(base)+".arff");
			Instances data = new Instances(readerData);
			data.setClassIndex(data.numAttributes()-1);
			data.stratify(folds);
			for (int fold = 0; fold < folds; fold++) {
				System.out.println("fold "+fold);
				Instances dataTrain = new Instances(data.trainCV(folds, fold));
				Instances dataTest = new Instances(data.testCV(folds,fold));
				dataTrain.setClassIndex(dataTrain.numAttributes() - 1);
				dataTest.setClassIndex(dataTest.numAttributes() - 1);
				
				Evaluation evaluationTrain = new Evaluation(dataTrain);
				Evaluation evaluationTest = new Evaluation(dataTest);

				if (classifier.equals("SimpleCart")) {
					
					//dataTrain.deleteWithMissingClass();
					SimpleCart sCart = new SimpleCart();
					sCart.buildClassifier(dataTrain);
					evaluationTrain.evaluateModel(sCart,dataTrain);
					evaluationTest.evaluateModel(sCart,dataTest);
					table[base][14][fold] = sCart.measureTreeSize();
				}

				
				if (classifier.equals("REPTree")) {
					REPTree repTree =  new REPTree();
					repTree.buildClassifier(dataTrain);
					evaluationTrain.evaluateModel(repTree,dataTrain);
					evaluationTest.evaluateModel(repTree,dataTest);
					table[base][14][fold] = repTree.numNodes();
				}
				if (classifier.equals("J48")) {
					J48 j48 = new J48();
					j48.buildClassifier(dataTrain);
					evaluationTrain.evaluateModel(j48,dataTrain);
					evaluationTest.evaluateModel(j48,dataTest);
					table[base][14][fold] = j48.measureTreeSize();					
					pTreesFile.write(j48.toString());
				}
				


				table[base][0][fold] = 1 - evaluationTrain.errorRate();
				table[base][1][fold] = evaluationTrain.weightedFMeasure();
				table[base][2][fold] = evaluationTrain.weightedPrecision();
				table[base][3][fold] = evaluationTrain.weightedRecall();
				table[base][4][fold] = evaluationTrain.truePositiveRate(0);
				table[base][5][fold] = evaluationTrain.trueNegativeRate(0);
				table[base][6][fold] = Math.sqrt(evaluationTrain.truePositiveRate(0)*evaluationTrain.trueNegativeRate(0));
				
				
				table[base][7][fold] = 1 - evaluationTest.errorRate();
				table[base][8][fold] = evaluationTest.weightedFMeasure();
				table[base][9][fold] = evaluationTest.weightedPrecision();
				table[base][10][fold] = evaluationTest.weightedRecall();
				
				
				table[base][11][fold] = evaluationTest.truePositiveRate(0);
				table[base][12][fold] = evaluationTest.trueNegativeRate(0);
				table[base][13][fold] = Math.sqrt(evaluationTest.truePositiveRate(0)*evaluationTest.trueNegativeRate(0));
				

				/*
				table[base][0][fold] = 1 - evaluationTrain.errorRate();
				table[base][1][fold] = evaluationTrain.weightedFMeasure();
				table[base][2][fold] = evaluationTrain.weightedPrecision();
				table[base][3][fold] = evaluationTrain.weightedRecall();
				table[base][4][fold] = 1 - evaluationTest.errorRate();
				table[base][5][fold] = evaluationTest.weightedFMeasure();
				table[base][6][fold] = evaluationTest.weightedPrecision();
				table[base][7][fold] = evaluationTest.weightedRecall();
				*/
			}
			pTreesFile.close();
			
			
			String classification = dirResults + "Classification"+classifier+".csv";
			
			
			
		}
		generateResults();
	}

	public static void inicialization() {
		folds = 10;
		
		
		dirCurrent = "/Volumes/Dados/datasets/Nature/";
		dirResults = "/Volumes/Dados/datasets/results/Nature/";
		
		//dirCurrent = "/Users/basgalupp/datasets/GeneExpression/";
		//dirResults = "/Users/basgalupp/datasets/results/J48/";
		
		new File(dirResults).mkdir(); 
		
	
		measures = new ArrayList<String>();
		measures.add("Train Accuracy");
		measures.add("Train F-Measure");
		measures.add("Train Precision");
		measures.add("Train Recall");
		
		measures.add("Train TPR");
		measures.add("Train TNR");
		measures.add("Train Gmean");
		
		measures.add("Test Accuracy");
		measures.add("Test F-Measure");
		measures.add("Test Precision");
		measures.add("Test Recall");
		
		measures.add("Test TPR");
		measures.add("Test TNR");
		measures.add("Test Gmean");
		
		measures.add("Tree Size");
		
		measures.add("Real");
		measures.add("Predicted");
		
		
		//measures.add("Tree Depth");
		//measures.add("Execution Time");

		DataSets = new ArrayList<String>();
		
		DataSets.add("CE-worm-GO-threshold-3");
		DataSets.add("DM-fly-GO-threshold-3");
		DataSets.add("MM-mouse-GO-threshold-3");
		DataSets.add("SC-yeast-GO-threshold-3");
		
		

		table = new double [DataSets.size()][measures.size()][folds];
	}


	public static void generateResults() throws FileNotFoundException {
		
		String classification = dirResults + "Classification"+classifier+".csv";
		
		
		
		
		String summaryAllFile = dirResults + "Summary"+classifier+".csv";
		PrintWriter pSummaryAllFile = new PrintWriter(summaryAllFile);
		String row = new String("dataset,");
		for (int m = 0; m < measures.size(); m++) {
			row += measures.get(m);
			if ( m< (measures.size()-1))
				row += ",,";
		}
		pSummaryAllFile.println(row);

		for (int d = 0; d < DataSets.size(); d++) {
			String foldsFile = dirResults + DataSets.get(d) + "folds.csv";
			PrintWriter pFoldsFile = new PrintWriter(foldsFile);
			for (int f = 0; f < folds; f++) 
				pFoldsFile.print(",fold"+f);
			pFoldsFile.println();

			for (int m=0; m < measures.size(); m++) {
				row = new String(measures.get(m));
				for (int f=0; f < folds; f++) {
					row += ","+table[d][m][f];
				}
				pFoldsFile.println(row);
			}
			pFoldsFile.close();

			row = new String(DataSets.get(d));
			for (int m = 0; m < measures.size(); m++) {
				double media = Utils.mean(table[d][m]);
				double desvpad = Math.sqrt(Utils.variance(table[d][m]));
				// inicio teste
				//media = 0;
				//for (int fold =0; fold < 10; fold++) {
				//	media += table[d][m][fold];
				//}
				//media /= 10;
				//fim teste
				
				row += ","+media+","+desvpad;
			}
			pSummaryAllFile.println(row);
		}
		pSummaryAllFile.close();
	}

}