package tools;

import headDt.Dataset;
import headDt.Main;
import headDt.split.Measure;
import headDt.topDown.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.*;

public class teste3 {

	static ArrayList<String> measures;
	static String dirCurrent, dirResults;
	static int folds;
	static double CRparam;
	public static double table[][];
	public static PrintWriter pTreesFile;

	public static void main(String[] args) throws Exception {
		
		//initialization();
		
		new Dataset(args[0],args[1]);
		
		
		new File(dirResults).mkdir();
		TreeParameters treeParam = new TreeParameters();
		SplitParameters splitParam = new SplitParameters();
		
		int param[] = {0,89,14,0,1,1,3,0,1,0,0,0,24,1}; // Gain-Ratio
		//int param[] = {0,89,0,0,1,1,3,0,1,0,0,0,24,1}; // Info-Gain
		//int param[] = {0,89,1,0,1,1,3,0,1,0,0,0,24,1}; // Gini-index
		
		
		setParameters(param,treeParam,splitParam);
		
		EvolvedAlgorithm tree = new EvolvedAlgorithm(splitParam,treeParam);
		
		
		String TreesFile = dirResults + "Trees.txt";
		pTreesFile = new PrintWriter(TreesFile);		
		

		for (CRparam = 0.05; CRparam < 0.95; CRparam+=0.05) {

			J48 j48 = new J48();
			String [] options = new String[4];
			options[0] = "-C";
			options[1] = String.valueOf(CRparam);
			options[2] = "-M";
			options[3] = "2";
			j48.forName("weka.classifiers.trees.J48",options);
			
			double fitness[][] = new double[3][Dataset.getMetaTraining().size()];
			// 0 = accuracy, 1 = fmeasure, 2 = AUC

			for (int base = 0; base < Dataset.getMetaTraining().size(); base++) {
				Instances dataTrain = Dataset.trainingData.get(base);
				Instances dataTest = Dataset.testData.get(base);
				j48.buildClassifier(dataTrain);
				pTreesFile.println(dataTrain.relationName());
				Evaluation eval = new Evaluation(dataTest);
				//(String function, int jobNumber, int dataSetID, int metaData, int fold, boolean describe)
				fitness[0][base] = 1 - eval.errorRate();
				fitness[1][base] = eval.weightedFMeasure();
				fitness[2][base] = eval.weightedAreaUnderROC();
			}
			
			//escolher o melhor CRparam
			
			// CRparam =  best
			
			double avgFitness[] = new double[3];
			double medianFitness[] = new double[3];
			double harmonicFitness[] = new double[3];
			
			
			int measure = fitness.length;
			for (int i = 0; i < 3; i++) {
				avgFitness[i] = Utils.mean(fitness[i]);
				Arrays.sort(fitness[i]);
				medianFitness[i] = fitness[i][(int)fitness[i].length/2];
				double sum = 0;
				boolean isNegative = false;
				for(int base = 0;base < fitness[i].length; base++){
					if(fitness[i][base] <= 0){
						isNegative = true;
						break;
					}
					sum += (1/fitness[i][base]);
				}
				if(isNegative)
					harmonicFitness[i] = 0.0;
				else
					harmonicFitness[i] = (fitness[i].length/sum);
			}
			
			j48 = new J48();
			options[0] = "-C";
			options[1] = String.valueOf(CRparam);
			options[2] = "-M";
			options[3] = "2";
			j48.forName("weka.classifiers.trees.J48",options);
			
			for (int base = 0; base < Dataset.getMetaTest().size(); base++) {
				Instances data = Dataset.getMetaTest().get(base);
				Evaluation eval = new Evaluation(data);
				eval.crossValidateModel(j48, data,10,new Random(1));
				
				table[base][8] = 0;
				eval.crossValidateModel(tree, data, 10, new Random(1));
				table[base][8] /= 10;

				table[base][0] = (1-eval.errorRate());
				table[base][1] = eval.weightedFMeasure();
				table[base][2] = eval.weightedPrecision();
				table[base][3] = eval.weightedRecall();             
				table[base][4] = 1- eval.errorRate();
				table[base][5] = eval.weightedFMeasure();
				table[base][6] = eval.weightedPrecision();
				table[base][7] = eval.weightedRecall();
				
				
			}
			
			

			
			
			

		}
		
		
		
		
		
		pTreesFile.close();
		generateResults();
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
		System.out.println("Entrou aqui");
		System.out.println("Valor do num folds = "+treeParam.getNumFoldPruning());
		break;

		// Reduced-error pruning
		case 5: treeParam.setNumFoldPruning(genome[13]);
		break;

		default:
			break; 
		}

	}


	public static void initialization() {
		folds = 10;

		//dirCurrent = "/Users/rodrigobarros/Desktop/Datasets/GeneExpression/";
		//dirResults = "/Users/rodrigobarros/Dropbox/eclipse/Workspace/Tese/Resultados/TesteFixo/";
		dirCurrent = "/Volumes/Dados/datasets/GeneExpression/";
		dirResults = "/Volumes/Dados/datasets/results/GPEM/"+CRparam+"/";

		new File(dirResults).mkdir(); 

		measures = new ArrayList<String>();
		measures.add("Train Accuracy");
		measures.add("F-Measure Train");
		measures.add("Precision Train");
		measures.add("Recall Train");
		measures.add("Test Accuracy");
		measures.add("F-Measure Test");
		measures.add("Precision Test");
		measures.add("Recall Test");
		measures.add("Tree Size");

		//measures.add("Real");
		//measures.add("Predicted");


		//measures.add("Tree Depth");
		//measures.add("Execution Time");

		DataSets = new ArrayList<String>();


		// Gene Expression


		DataSets.add("alizadeh-2000-v3");
		DataSets.add("armstrong-2002-v1");
		DataSets.add("lapointe-2004-v2");
		DataSets.add("nutt-2003-v3");
		DataSets.add("tomlins-2006");
		
		DataSets.add("alizadeh-2000-v1");
		DataSets.add("alizadeh-2000-v2");
		DataSets.add("armstrong-2002-v2");
		DataSets.add("bhattacharjee-2001");
		DataSets.add("bittner-2000");
		DataSets.add("bredel-2005");
		DataSets.add("chen-2002");
		DataSets.add("chowdary-2006");
		DataSets.add("dyrskjot-2003");
		DataSets.add("garber-2001");
		DataSets.add("golub-1999-v1");
		DataSets.add("golub-1999-v2");
		DataSets.add("gordon-2002");
		DataSets.add("khan-2001");
		DataSets.add("laiho-2007");
		DataSets.add("lapointe-2004-v1");
		DataSets.add("liang-2005");
		DataSets.add("nutt-2003-v1");
		DataSets.add("nutt-2003-v2");
		DataSets.add("pomeroy-2002-v1");
		DataSets.add("pomeroy-2002-v2");
		DataSets.add("ramaswamy-2001");
		DataSets.add("risinger-2003");
		DataSets.add("shipp-2002-v1");
		DataSets.add("singh-2002");
		DataSets.add("su-2001");
		DataSets.add("tomlins-2006-v2");
		DataSets.add("west-2001");
		DataSets.add("yeoh-2002-v1");
		DataSets.add("yeoh-2002-v2");

		table = new double [DataSets.size()][measures.size()];
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

	public static void generateResults() throws FileNotFoundException {
		//		String classification = dirResults + "Classification"+classifier+".csv";
		String summaryAllFile = dirResults + "Summary"+splitMeasure+".csv";
		PrintWriter pSummaryAllFile = new PrintWriter(summaryAllFile);
		String row = new String("dataset");
		for (int m = 0; m < measures.size(); m++) {
			row += ","+measures.get(m);
		}
		pSummaryAllFile.println(row);

		for (int d = 0; d < DataSets.size(); d++) {
			row = new String(DataSets.get(d));
			for (int m = 0; m < measures.size(); m++) {
				double media = table[d][m];
				//double desvpad = Math.sqrt(Utils.variance(table[d][m]));
				row += ","+media;
			}
			pSummaryAllFile.println(row);
		}
		pSummaryAllFile.close();
	}

}