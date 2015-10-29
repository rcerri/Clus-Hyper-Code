package tools;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;

import headDt.topDown.EvolvedAlgorithm;
import headDt.topDown.SplitParameters;
import headDt.topDown.TreeParameters;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.Utils;

public class teste {

	public static EvolvedAlgorithm decodeIndividual(int genome[]) throws Exception{

		SplitParameters split = new SplitParameters();
		TreeParameters treeParam = new TreeParameters();

		// Split Strategy - univariate, multivariate, omni
		split.setSplitType(genome[0]);

		// Oblique Strategy (to do)
		// split.setObliqueStrategy(genome[1]);

		// Split Measure
		split.setSplitMeasure(genome[2]);

		// Binary Nominal Split?
		boolean binaryNominal;
		if(genome[3] == 0) binaryNominal = false;
		else binaryNominal = true;
		split.BinarySplits(binaryNominal);

		// Stopping Criteria
		split.setStoppingCriteria(genome[4]);

		// Stopping Criteria Parameters
		double stoppingParameter;
		switch(genome[4]){

		// do nothing - homogeneity criterion
		case 0: break; // do nothing

		// min number of objects [1,20]
		case 1: stoppingParameter = (genome[5] % 20) + 1;
		split.setMinNumObj((int)stoppingParameter);
		break;

		// min perc of objects [1%,10%]
		case 2: stoppingParameter = ((double)((genome[5] % 10) + 1))/(double)100;
		split.setMinPercObj(stoppingParameter);
		break;

		// min accuracy reached {70%, 75%, 80%, 85%, 90%, 95%, 99%}
		case 3: stoppingParameter = ((double)((genome[5] % 7) *5) + 70)/100;
		if(Utils.eq(stoppingParameter,1.0))
			stoppingParameter = stoppingParameter - 0.1;
		split.setMinAccuracy(stoppingParameter);
		break;

		// max depth reached {2,3,4,5,6,7,8,9,10}
		case 4: stoppingParameter = (genome[5] % 9) + 2;
		split.setMaxDepth((int)stoppingParameter);
		break;

		default: break; //do nothing;
		}

		// Missing value during split
		split.setMissingValueSplit(genome[6]);

		// Missing value distribution
		split.setMissingValueDistribution(genome[7]);

		// Collapse tree - training error pruning
		boolean willCollapse;
		if(genome[8] == 0) willCollapse = false;
		else willCollapse = true;
		treeParam.set_collapse(willCollapse);

		// Laplace correction at leaves?
		boolean useLaplace;
		if(genome[9] == 0) useLaplace = false;
		else useLaplace = true;
		treeParam.setLaplace(useLaplace);

		// Missing value strategy for classification of new instances
		treeParam.setMissingValueClassification(genome[10]);

		// Pruning strategy
		treeParam.setPruningType(genome[11]);

		// Pruning parameter
		double pruningParameter = 0;
		switch(genome[11]){

		// no pruning
		case 0: //do nothing;
			break; 

			// Error-based pruning - CF varies [1% - 50%]
		case 1: pruningParameter = ((double)(genome[12] % 50) + 1)/100;
		treeParam.setM_CF((float)pruningParameter);
		break;

		// Minimum error pruning - M varies from [0,99]
		case 2: pruningParameter = genome[12] % 100;
		treeParam.setM((float)pruningParameter);
		break;

		// Pessimistic-error pruning - number of SEs varies as {0.5, 1, 1.5, 2}
		case 3: pruningParameter =  0.5* ((genome[12] % 4)+1);
		treeParam.setNumberOfSEs(pruningParameter);
		break;

		// Cost-complexity Pruning - stop param 1 = number of SEs (as above). stop param 2 = num folds (2-10).
		case 4: pruningParameter = 0.5* ((genome[12] % 4)+1);
		treeParam.setNumberOfSEs(pruningParameter);
		treeParam.setNumFoldPruning(genome[13] );
		break;

		// Reduced-error pruning
		case 5: treeParam.setNumFoldPruning(genome[13]);
		break;

		default: //do nothing
			break; 
		}

		EvolvedAlgorithm algorithm = new EvolvedAlgorithm(split,treeParam);
		return algorithm;
	}



	public static void main(String[] args) throws Exception {

		int folds = 10;
		new DadosEntrada();
		String folder = "/Volumes/Dados/Dropbox/datasets/Nature/";
		String folder2 = "/Volumes/Dados/Dropbox/datasets/results/Nature/All/";

		FileReader reader;
		FileWriter writer, writerCV, writerTrain;
		PrintWriter pw,pwCV,pwTrain;
		Instances instances;

		//int param[] = { 0, 89, 14, 0, 1, 1, 3, 0, 1, 0, 0, 1, 24, 5};
		// int param[] = {0,89,14,0,1,14,0,0,0,0,2,1,4,5}; BMC
		//int param[] = {1,89,12,0,1,16,0,1,1,1,0,5,0,2}; // para o REP, o indice 12 (coloquei 0) nao tem influencia! Algoritmo SAC 2013!!

		//int param[] = {0,89,99,0,1,1,3,0,1,0,0,0,24,1}; // EVOLVE MEASURE (sem poda)

		int param1[][] = {
				// Nature 1
				{0,91,13,0,1,23,3,3,1,1,0,2,85,6},
				{0,21,13,0,1,63,1,5,0,0,0,2,85,10},
				{1,77,4,1,1,63,1,3,1,0,0,2,37,6},
				{0,13,4,0,1,83,2,6,1,0,1,2,45,2},
				{0,84,13,0,1,83,3,1,0,1,0,2,49,4},
				{1,25,13,1,1,3,2,0,1,0,2,2,52,4},
				{0,95,13,1,1,83,0,1,1,1,2,2,99,7},
				{2,83,4,1,1,43,0,3,1,1,1,2,64,2},
				{2,71,4,0,1,83,2,2,0,1,2,2,82,9},
				{1,66,4,0,1,23,2,0,1,0,1,2,98,6}
		};

		// Nature 2
		int param2[][] = {

				{0,41,4,0,1,44,3,3,0,0,0,2,70,6},
				{1,97,13,1,1,44,1,2,1,0,0,2,21,10},
				{1,8,13,1,1,64,0,4,1,1,1,2,98,6},
				{2,43,4,1,1,24,3,4,1,0,1,2,78,9},
				{2,75,4,1,1,4,2,4,0,0,2,2,90,9},
				{1,37,4,1,1,24,2,2,1,0,2,2,26,9},
				{0,21,4,0,1,44,3,6,1,0,2,2,26,5},
				{1,31,4,1,1,4,3,2,1,1,1,2,16,5},
				{1,31,4,1,1,44,3,1,1,0,0,2,39,2},
				{1,96,4,0,1,84,2,3,0,1,0,2,4,3}
		};


		//Nature 3

		int param3[][] = {

				{2,45,0,1,0,93,2,2,1,0,0,1,79,10},
				{2,19,3,1,0,18,2,6,0,0,0,1,28,6},
				{0,43,10,1,1,81,1,3,1,1,0,1,82,9},
				{0,96,7,0,2,14,2,6,1,0,1,2,94,8},
				{0,64,7,0,2,34,0,3,1,1,0,2,5,10},
				{0,68,2,1,0,88,2,0,0,1,0,1,86,4},
				{0,80,0,0,0,81,3,6,0,0,1,1,79,10},
				{1,46,7,1,2,74,0,0,0,1,2,2,78,10},
				{0,78,7,0,2,74,1,4,0,1,0,2,88,10},
				{0,23,3,1,1,20,2,5,0,1,0,3,68,7}
		};


		//Nature 4

		int param[][] = {
				{0,38,0,0,1,44,1,2,0,0,2,0,21,2},
				{1,70,3,1,1,4,1,0,0,1,0,2,4,3},
				{0,38,0,1,1,64,0,1,0,1,1,2,45,3},
				{2,31,10,1,1,84,2,1,1,0,2,2,6,6},
				{1,69,2,0,1,84,2,3,1,0,0,2,99,5},
				{2,22,2,1,1,84,0,5,0,1,0,0,12,2},
				{0,32,2,0,1,24,0,4,1,1,1,0,15,9},
				{0,39,10,1,1,84,3,4,1,0,0,0,80,7},
				{1,64,3,0,1,44,1,2,1,0,2,2,70,2},
				{1,91,10,0,1,44,3,1,1,0,0,2,93,5}
		};

		for(int base = 0; base < DadosEntrada.datasets.size(); base++){
			reader = new FileReader(folder+DadosEntrada.datasets.get(base)+".arff");
			instances = new Instances(reader);
			instances.setClassIndex(instances.numAttributes()-1);
			instances.stratify(folds);

			
			writerTrain = new FileWriter(folder2+DadosEntrada.datasets.get(base)+"Train.csv");
			pwTrain = new PrintWriter(writerTrain);
			pwTrain.println("Run,Accuracy,F-Measure,Precision,Recall,Total Nodes,Total Leaves,AUC,True Positive Rate,True Negative Rate,Gmean");
			
			
			writerCV = new FileWriter(folder2+DadosEntrada.datasets.get(base)+"cross-validation.csv");
			pwCV = new PrintWriter(writerCV);
			pwCV.println("Run,Accuracy,F-Measure,Precision,Recall,Total Nodes,Total Leaves,AUC,True Positive Rate,True Negative Rate,Gmean");

			for (int exec = 0; exec < 10; exec++) {

				System.out.print("algorithm = ["+param[exec][0]);
				for (int i = 1; i < param[exec].length; i++) {
					System.out.print(","+param[exec][i]);
				}
				System.out.println("]");

				//System.out.println(writeIndividual(param[exec]));

				// cross-validation				
				Evaluation eval = new Evaluation(instances);
				EvolvedAlgorithm tree = decodeIndividual(param[exec]);

				tree.buildClassifier(instances);
				eval.evaluateModel(tree, instances);
				
				pwTrain.println(exec+","
						+(1-eval.errorRate())+","+
						eval.weightedFMeasure()+","+
						eval.weightedPrecision()+","+
						eval.weightedRecall()+","+
						tree.getRoot().numNodes()+","+
						tree.getRoot().numLeaves()+","+
						eval.weightedAreaUnderROC()+","+
						eval.truePositiveRate(0)+","+
						eval.trueNegativeRate(0)+","+
						Math.sqrt(eval.truePositiveRate(0) * eval.trueNegativeRate(0))
						);
				
				eval = new Evaluation(instances);
				eval.crossValidateModel(tree, instances,10,new Random(1));
				System.out.println(eval.toSummaryString());
				tree.getRoot().toString();
				pwCV.println(exec+","
						+(1-eval.errorRate())+","+
						eval.weightedFMeasure()+","+
						eval.weightedPrecision()+","+
						eval.weightedRecall()+","+

						","+
						","+

						//tree.getRoot().numNodes()+","+
						//tree.getRoot().numLeaves()+","+
						eval.weightedAreaUnderROC()+","+
						eval.truePositiveRate(0)+","+
						eval.trueNegativeRate(0)+","+
						Math.sqrt(eval.truePositiveRate(0) * eval.trueNegativeRate(0))
						);
				// cross-validation


				// fold a fold
				new File(folder2+DadosEntrada.datasets.get(base)+"/").mkdir();
				writer = new FileWriter(folder2+DadosEntrada.datasets.get(base)+"/resultado-folds-exec"+exec+".csv");
				pw = new PrintWriter(writer);
				pw.println("Fold,Accuracy,F-Measure,Precision,Recall,Total Nodes,Total Leaves,AUC,True Positive Rate,True Negative Rate,Gmean");
				for(int fold = 0; fold < folds; fold++){
					tree.buildClassifier(instances.trainCV(folds,fold));
					eval.evaluateModel(tree, instances.testCV(folds,fold));	
					pw.println(fold+","
							+(1-eval.errorRate())+","+
							eval.weightedFMeasure()+","+
							eval.weightedPrecision()+","+
							eval.weightedRecall()+","+
							tree.getRoot().numNodes()+","+
							tree.getRoot().numLeaves()+","+
							eval.weightedAreaUnderROC()+","+
							eval.truePositiveRate(0)+","+
							eval.trueNegativeRate(0)+","+
							Math.sqrt(eval.truePositiveRate(0) * eval.trueNegativeRate(0))
							);
				}
				writer.close();
				pw.close();

				// cross-validation

			}
			writerTrain.close();
			pwTrain.close();
			writerCV.close();
			pwCV.close();
		}

	}


}
