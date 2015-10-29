package tools;

import headDt.Dataset;
import headDt.split.Measure;
import headDt.topDown.EvolvedAlgorithm;
import headDt.topDown.SplitParameters;
import headDt.topDown.TreeParameters;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import ec.util.MersenneTwister;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.instance.StratifiedRemoveFolds;

public class RandomSearch {

	static int evaluations;
	static int executions;
	static ArrayList<String> measures;
	static String dirCurrent, dirResults;
	static int param[] = new int[14];  
	static int bestParam[] = new int[14];
	static double bestFitness = 0;

	public static void main(String[] args) throws Exception {


		evaluations = 10000;
		executions = 5;

		for(int config = 0; config < 3; config++)  {

			int configuracao = 0;
			switch(config){
			case 0: configuracao = 5; break;
			case 1: configuracao = 7; break;
			case 2: configuracao = 9; break;
			}


			dirCurrent = "/Users/rodrigobarros/Desktop/Datasets/Arrumados/";
			dirResults = "/Users/rodrigobarros/Desktop/RandomSearch/Config"+configuracao+"/";
			new Dataset("basesUCI"+configuracao+".txt",dirCurrent);



			new File(dirResults).mkdir(); 


			double accuracy[][] = new double[Dataset.getMetaTest().size()][executions];
			double fmeasure[][] = new double[Dataset.getMetaTest().size()][executions];



			for(int exec = 0; exec < executions; exec++) {

				MersenneTwister generator = new MersenneTwister(exec);

				System.out.println();
				System.out.println("Execution "+exec);
				System.out.println();


				for (int evaluation = 0; evaluation < evaluations; evaluation++) {

					System.out.println("Evaluation "+(evaluation+1));
					param[0] = generator.nextInt(3);
					param[1] = generator.nextInt(101);
					param[2] = generator.nextInt(15);
					param[3] = generator.nextInt(2);
					param[4] = generator.nextInt(5);
					param[5] = generator.nextInt(101);
					param[6] = generator.nextInt(4);
					param[7] = generator.nextInt(7);
					param[8] = generator.nextInt(2);
					param[9] = generator.nextInt(2);
					param[10] = generator.nextInt(3);
					param[11] = generator.nextInt(6);
					param[12] = generator.nextInt(101);
					param[13] = generator.nextInt(9) + 2;


					TreeParameters treeParameters = new TreeParameters();
					SplitParameters splitParameters = new SplitParameters();
					setParameters(param,treeParameters, splitParameters);


					double fitness[] = new double[Dataset.getMetaTraining().size()];
					for (int base = 0; base < Dataset.getMetaTraining().size(); base++) {
						//	System.out.println(datasets.get(base));
						Instances data = Dataset.getMetaTraining().get(base);

						StratifiedRemoveFolds forTraining = new StratifiedRemoveFolds();
						StratifiedRemoveFolds forTest = new StratifiedRemoveFolds();

						forTraining.setInputFormat(data);
						forTraining.setSeed(1);
						forTraining.setNumFolds(4);
						forTraining.setFold(1);
						forTraining.setInvertSelection(true);
						Instances training = Filter.useFilter(data, forTraining);

						forTest.setInputFormat(data);
						forTest.setSeed(1);
						forTest.setNumFolds(4);
						forTest.setFold(1);
						forTest.setInvertSelection(false);
						Instances test = Filter.useFilter(data, forTest);

						EvolvedAlgorithm tree = new EvolvedAlgorithm(splitParameters,treeParameters);
						tree.buildClassifier(training);

						Evaluation evaluate = new Evaluation(data);
						evaluate.evaluateModel(tree, test);

						fitness[base] = evaluate.weightedFMeasure();

					}
					double averageFitness = Utils.mean(fitness);
					if(averageFitness > bestFitness){
						bestFitness = averageFitness;
						bestParam = param;
					}
				}

				TreeParameters treeParameters = new TreeParameters();
				SplitParameters splitParameters = new SplitParameters();
				setParameters(bestParam,treeParameters, splitParameters);


				for(int i=0; i< Dataset.getMetaTest().size();i++){
					Instances data = Dataset.getMetaTest().get(i);
					EvolvedAlgorithm tree = new EvolvedAlgorithm(splitParameters,treeParameters);
					Evaluation evaluate = new Evaluation(data);
					evaluate.crossValidateModel(tree, data, 10, new Random(1));

					accuracy[i][exec] = 1 - evaluate.errorRate();
					fmeasure[i][exec] = 1 - evaluate.weightedFMeasure();
				}

			}

			FileWriter fw = new FileWriter(dirResults+"RandomSearch.csv");
			PrintWriter pw = new PrintWriter(fw);
			pw.println("Dataset,Accuracy,,F-Measure,");

			for(int i=0; i< Dataset.getMetaTest().size();i++){
				Instances data = Dataset.getMetaTest().get(i);
				double acuraciaMedia = Utils.mean(accuracy[i]); 
				double acuraciaDesvio = Math.sqrt(Utils.variance(accuracy[i]));

				double fmeasureMedia = Utils.mean(fmeasure[i]); 
				double fmeasureDesvio = Math.sqrt(Utils.variance(fmeasure[i]));

				pw.println(data.relationName()+","+acuraciaMedia+","+acuraciaDesvio+","+fmeasureMedia+","+fmeasureDesvio);

			}

			fw.close();
			pw.close();

		}

	}


	public static void initialization() {


	}



	private static void setParameters(int genome[], TreeParameters treeParam, SplitParameters split){

		// Split Strategy - univariate, multivariate, omni
		split.setSplitType(genome[0]);

		// Oblique Strategy
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
		case 2: stoppingParameter = ((double)((genome[5] % 10) + 1))/100;
		split.setMinPercObj(stoppingParameter);
		break;

		// min accuracy reached {70%, 75%, 80%, 85%, 90%, 95%, 99%}
		case 3: stoppingParameter = (genome[5] % 7) + 70 + (genome[5]*5);
		if(Utils.eq(stoppingParameter,100))
			stoppingParameter = stoppingParameter - 1.0;
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
		switch(genome[11]){

		// no pruning
		case 0: 
			break; 

			// Error-based pruning - CF varies [1% - 50%]
		case 1: stoppingParameter = ((double)(genome[12] % 50) + 1)/100;
		treeParam.setM_CF((float)stoppingParameter);
		break;

		// Minimum error pruning - M varies from [0,99]
		case 2: stoppingParameter = genome[12] % 100;
		treeParam.setM((float)stoppingParameter);
		break;

		// Pessimistic-error pruning - number of SEs varies as {0.5, 1, 1.5, 2}
		case 3: stoppingParameter =  0.5* ((genome[12] % 4)+1);
		treeParam.setNumberOfSEs(stoppingParameter);
		break;

		// Cost-complexity Pruning - stop param 1 = number of SEs (as above). stop param 2 = num folds (2-10).
		case 4: stoppingParameter = 0.5* ((genome[12] % 4)+1);
		treeParam.setNumberOfSEs(stoppingParameter);
		treeParam.setNumFoldPruning(genome[13]);
		break;

		// Reduced-error pruning
		case 5: treeParam.setNumFoldPruning(genome[13]);
		break;

		default:
			break; 
		}

	}

	public static StringBuffer writeIndividual(int[] genome){
		StringBuffer algorithm = new StringBuffer();

		switch(genome[0]){
		case 0: algorithm.append("UnivariateSplit-");break;
		case 1: algorithm.append("MultivariateSplit-");break;
		case 2: algorithm.append("OmniSplit-");break;
		default: break;
		}

		switch(genome[1]){
		// to do - oblique strategy
		default: break;
		}

		algorithm.append(Measure.getName(genome[2])+"-");

		if(genome[3] == 0)
			algorithm.append("MultiNominalSplit-");
		else
			algorithm.append("BinaryNominalSplit-");

		switch(genome[4]){
		case 0: algorithm.append("HomogeneousStop-"); break;
		case 1: algorithm.append("MinNumberInstStop:"); break;
		case 2: algorithm.append("MinPercInstStop:"); break;
		case 3: algorithm.append("AccThresholdStop:"); break;
		case 4: algorithm.append("MaxDepthReached:"); break;
		}

		double param;
		switch(genome[4]){
		case 0: break;
		case 1: param = (genome[5] % 20) + 1; 
		algorithm.append(param+"-"); 
		break;
		case 2: param = ((genome[5] % 10) + 1)/100; 
		algorithm.append(param+"-"); 
		break;
		case 3: param = (genome[5] % 7) + 70 + (genome[5]*5); 
		if(Utils.eq(param,100)) param = param - 1; 
		algorithm.append(param+"-"); 
		break;
		case 4: param = (genome[5] % 9) + 2; 
		algorithm.append(param+"-"); 
		break;
		default: break;
		}

		switch(genome[6]){
		case 0: algorithm.append("MVS:IgnoreMissingValues-"); break;
		case 1: algorithm.append("MVS:UnsupervisedImputation-"); break;
		case 2: algorithm.append("MVS:SupervisedImputation-"); break;
		case 3: algorithm.append("MVS:WeightSplitCriterionValue-"); break;
		default: break;
		}

		switch(genome[7]){
		case 0: algorithm.append("MVD:WeightWithBagProbability-"); break;
		case 1: algorithm.append("MVD:IgnoreMissingValues-"); break;
		case 2: algorithm.append("MVD:UnsupervisedImputation-"); break;
		case 3: algorithm.append("MVD:SupervisedImputation-"); break;
		case 4: algorithm.append("MVD:AssignToAllBags-"); break;
		case 5: algorithm.append("MVD:AddToLargestBag-"); break;
		case 6: algorithm.append("MVD:AssignToMostProbableBagRegardingClass-"); break;
		default: break;
		}

		if(genome[8]==0)
			algorithm.append("DontCollapseTree-");
		else
			algorithm.append("CollapseTree-");


		if(genome[9]==0)
			algorithm.append("NoLaPlace-");
		else
			algorithm.append("UseLaPlaceCorrection-");

		switch(genome[10]){
		case 0: algorithm.append("MVC:ExploreAllBranchesAndCombine-"); break;
		case 1: algorithm.append("MVC:HaltInTheCurrentNode-"); break;
		case 2: algorithm.append("MVC:GoToMostProbableBag-"); break;
		default: break;
		}

		switch(genome[11]){
		case 0: algorithm.append("NoPruning-"); break;
		case 1: algorithm.append("ErrorBasedPruning:"); break;
		case 2: algorithm.append("MinimumErrorPruning:"); break;
		case 3: algorithm.append("PessimisticErrorPruning:"); break;
		case 4: algorithm.append("CostComplexityPruning:"); break;
		case 5: algorithm.append("ReducedErrorPruning:"); break;
		}

		Double pruningParameter;
		switch(genome[11]){
		case 0: break;
		case 1: pruningParameter = ((double)(genome[12] % 50) + 1)/100;; algorithm.append(pruningParameter); break;
		case 2: pruningParameter = (double)genome[12] % 100; algorithm.append(pruningParameter); break;
		case 3: pruningParameter = 0.5* ((genome[12] % 4)+1); algorithm.append(pruningParameter); break;
		case 4: pruningParameter = 0.5* ((genome[12] % 4)+1); algorithm.append(pruningParameter+"-"+genome[13]+"folds"); break;
		case 5: algorithm.append(genome[13]+"folds"); break;
		default: break;
		}


		return algorithm;

	}

}
