package tools;

import headDt.Dataset;

import java.io.PrintWriter;
import java.io.File;
import java.util.ArrayList;

import weka.classifiers.Evaluation;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.supervised.instance.StratifiedRemoveFolds;

import weka.core.*;

public class GPEM_J48 {

	static ArrayList<String> measures;
	static String dirCurrent, dirResults;
	static double CRparam;

	public static void main(String[] args) throws Exception {

		initialization();

		Instances training = null, validation = null, test = null;

		new Dataset(args[0],args[1]);

		new File(dirResults).mkdir();

		double fitness[][] = new double[5][Dataset.getMetaTest().size()];
		double fitnessTest[][] = new double[5][Dataset.getMetaTest().size()];
		double bestFitness[][] = new double[5][Dataset.getMetaTest().size()];
		
		double desviosTest[][] = new double[5][Dataset.getMetaTest().size()];
		
		double bestFitnessTest[][] = new double[5][Dataset.getMetaTest().size()];
		double bestCR[][] = new double[5][Dataset.getMetaTest().size()];
		
		double bestDesviosTest[][] = new double[5][Dataset.getMetaTest().size()];
		
		
		for (int i = 0; i < 5; i++) {
			for (int base = 0; base < Dataset.getMetaTest().size(); base++) {
				bestFitness[i][base] = 0.0;
				bestFitnessTest[i][base] = 0.0;
				bestCR[i][base] = 0.05;
				fitness[i][base] = 0;
				bestDesviosTest[i][base] = 0.0;
			}
		}

		for (int base = 0; base < Dataset.getMetaTest().size(); base++) {
			for (CRparam = 0.05; CRparam < 0.5; CRparam+=0.05) {
				J48 j48 = new J48();
				j48.setConfidenceFactor((float)CRparam);
						/*
						String [] options = new String[4];
						options[0] = "-C";
						options[1] = String.valueOf(CRparam);
						options[2] = "-M";
						options[3] = "2";
						j48.forName("weka.classifiers.trees.J48",options);
						*/

				double folds[][] = new double[5][10];
				for (int i = 0; i < 5; i++)
					for (int j = 0; j < 10; j++)
					folds[i][j] = 0;
				
				for (int fold = 0; fold < 10; fold++) {
					Instances full = Dataset.getMetaTest().get(base).trainCV(10,fold);
					StratifiedRemoveFolds train = new StratifiedRemoveFolds();
					StratifiedRemoveFolds valid = new StratifiedRemoveFolds();
					train.setInputFormat(full);
					train.setSeed(1);
					train.setNumFolds(4);
					train.setFold(1);
					train.setInvertSelection(true);
					training = Filter.useFilter(full,train);
					valid.setInputFormat(full);
					valid.setSeed(1);
					valid.setNumFolds(4);
					valid.setFold(1);
					valid.setInvertSelection(false);
					validation = Filter.useFilter(full, valid);
					test = Dataset.getMetaTest().get(base).testCV(10,fold);

					/*if (base == 0) {
						System.out.println("Treino = "+training.numInstances());
						System.out.println("Validacao = "+validation.numInstances());
						System.out.println("Teste = "+test.numInstances());
					}*/
					
					j48.buildClassifier(training);
					Evaluation evalValidation = new Evaluation(validation);
					evalValidation.evaluateModel(j48, validation);
					fitness[0][base] += (1 - evalValidation.errorRate());
					fitness[1][base] += evalValidation.weightedFMeasure();
					fitness[2][base] += evalValidation.weightedAreaUnderROC();
					fitness[3][base] += evalValidation.weightedRecall();
					
					// relative accuracy improvement
					Evaluation majorityEvaluation = new Evaluation(validation);
					//more costly to cross-validate the majority class
					ZeroR majority = new ZeroR();
					majority.buildClassifier(training);
					majorityEvaluation.evaluateModel(majority, validation);

					double treeAccuracy = (1 - evalValidation.errorRate());
					double majorityAccuracy = (1 - majorityEvaluation.errorRate());
					if(treeAccuracy > majorityAccuracy) {
						fitness[4][base] += ((treeAccuracy - majorityAccuracy)/(1-majorityAccuracy));
						
					}
					else {
						fitness[4][base] += ((treeAccuracy - majorityAccuracy)/majorityAccuracy);
					}

					
					Evaluation evalTest = new Evaluation(test);
					evalTest.evaluateModel(j48, test);
					
					folds[0][fold] = (1 - evalTest.errorRate());
					folds[1][fold] = evalTest.weightedFMeasure();
					folds[2][fold] = evalTest.weightedAreaUnderROC();
					folds[3][fold] = evalTest.weightedRecall();
					
					//fitnessTest[0][base] += (1 - evalTest.errorRate()); 
					//fitnessTest[1][base] += evalTest.weightedFMeasure();
					//fitnessTest[2][base] += evalTest.weightedAreaUnderROC();
					//fitnessTest[3][base] += evalTest.weightedRecall();
					
					//relative accuracy improvement
					majorityEvaluation.evaluateModel(majority, test);
					treeAccuracy = (1 - evalTest.errorRate());
					majorityAccuracy = (1 - majorityEvaluation.errorRate());
					if(treeAccuracy > majorityAccuracy) {
						//fitnessTest[4][base] += ((treeAccuracy - majorityAccuracy)/(1-majorityAccuracy));
						folds[4][fold] = ((treeAccuracy - majorityAccuracy)/(1-majorityAccuracy));
						
					}
					else {
						//fitnessTest[4][base] += ((treeAccuracy - majorityAccuracy)/majorityAccuracy);
						folds[4][fold] = ((treeAccuracy - majorityAccuracy)/majorityAccuracy);
					}
				}
			
				// calcular os devios
				
				for (int i = 0; i < 5; i++) {
					fitnessTest[i][base] = Utils.mean(folds[i]);
					desviosTest[i][base] = Math.sqrt(Utils.variance(folds[i]));
				}
				
				for (int i = 0; i < 5; i++) {
					fitness[i][base] /= 10;
					if (fitness[i][base] >= bestFitness[i][base]) {
						bestFitness[i][base] = fitness[i][base];
						bestCR[i][base] = CRparam;
						bestDesviosTest[i][base] = desviosTest[i][base];
						bestFitnessTest[i][base] = fitnessTest[i][base];
					}
				}
			}
		}

		String summaryAllFile = dirResults + "Summary-J48.csv";
		PrintWriter pSummaryAllFile = new PrintWriter(summaryAllFile);
		String row = new String("dataset,");
		for (int m = 0; m < (measures.size() - 1); m++) {
			row += measures.get(m) + ", , ,";
		}
		row += measures.get(measures.size() - 1);
		pSummaryAllFile.println(row);

		for (int base = 0; base < Dataset.getMetaTest().size(); base++) {
			row = new String(Dataset.getMetaTest().get(base).relationName()+",");
			for (int m = 0; m < (measures.size()-1); m++) {
				row += bestFitnessTest[m][base] + "," + bestDesviosTest[m][base] + "," + bestCR[m][base]+",";
			}
			row += bestFitnessTest[measures.size()-1][base] + "," + bestDesviosTest[measures.size()-1][base] + "," + bestCR[measures.size()-1][base];
			pSummaryAllFile.println(row);
		}
		pSummaryAllFile.close();
	}

	public static void initialization() {
		dirCurrent = "/Volumes/Dados/datasets/UCI/GEPM/";
		dirResults = "/Volumes/Dados/datasets/results/GPEM/";

		new File(dirResults).mkdir(); 

		measures = new ArrayList<String>();
		measures.add("Accuracy");
		measures.add("F-Measure");
		measures.add("AUC");
		measures.add("medida1");
		measures.add("medida2");
	}

}