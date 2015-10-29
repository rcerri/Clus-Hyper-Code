package tools;

import headDt.split.Measure;
import headDt.topDown.EvolvedAlgorithm;
import headDt.topDown.SplitParameters;
import headDt.topDown.TreeParameters;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.core.*;

public class teste2 {

	static ArrayList<String> measures;
	static ArrayList<String> DataSets;
	static String dirCurrent, dirResults;
	static int jobs;
	static double table[][][];

	public static void main(String[] args) throws Exception {
		initialization();
		new File(dirResults).mkdir();
		TreeParameters treeParam = new TreeParameters();
		SplitParameters splitParam = new SplitParameters();
		int param[][] = new int[5][14];  // num jobs = 5, num param = 14

		// ACC-AVERAGE
		/*int job0[] = {0,17,13,0,1,100,1,0,1,1,1,1,87,9};
		int job1[] = {2,93,4,0,1,60,1,4,1,0,2,1,94,4};
		int job2[] = {2,11,13,0,4,62,1,1,0,0,0,0,2,7};
		int job3[] = {0,15,4,0,4,8,0,3,0,1,1,0,46,8};
		int job4[] = {1,53,4,0,0,86,3,0,0,0,2,0,92,4};*/
		
		// ACC-MEDIAN
		/*int job0[] = {1,18,1,1,0,35,3,2,1,1,0,4,97,3};
		int job1[] = {1,88,2,1,0,93,2,6,1,0,0,2,43,5};
		int job2[] = {1,69,4,1,1,20,0,5,0,1,1,4,52,5};
		int job3[] = {0,46,12,1,1,40,3,3,1,0,1,0,54,6};
		int job4[] = {1,38,3,0,1,100,1,4,1,1,2,0,97,3};*/
		
		// ACC-Harmonic
		/*int job0[] = { 0,60,13,0 ,0 ,95 ,0 ,6 ,1 ,0 ,2 ,0 ,37 ,3};
		int job1[] = {2 ,34 ,4 ,0 ,4 ,26, 3, 1, 1, 0, 0, 0, 60, 5};
		int job2[] = {0, 8, 4, 0, 1, 40, 1, 5, 0, 1, 2, 1, 34, 3};
		int job3[] = {1, 13, 4, 0, 1, 20, 3, 3, 1, 1, 1, 3, 80, 8};
		int job4[] = {2, 58, 4, 0, 0, 68, 1, 4, 0, 1, 0, 0, 4, 5};*/
		
		// AUC-Average
	/*	int job0[] = { 2, 4, 8, 0, 1, 62, 0, 5, 0, 0, 1, 0, 42, 5};
		int job1[] = {0, 13, 8, 1, 2, 3, 1, 2, 1, 1, 2, 0, 32, 2};
		int job2[] = {0, 17, 8, 0, 1, 22, 3, 2, 0, 1, 0, 0, 20, 5};
		int job3[] = {2, 90, 13, 0, 1, 83, 1, 4, 0, 1, 0, 0, 66, 8};
		int job4[] = {2, 90, 4, 0, 4, 60, 3, 6, 1, 0, 0, 0, 88, 5};*/
		
		// AUC-Median
	/*	int job0[] = { 1, 28, 1, 1, 1, 62, 1, 0, 0, 1, 0, 0, 78, 8};
		int job1[] = {1, 88, 12, 1, 1, 62, 3, 3, 0, 0, 0, 0, 56, 10};
		int job2[] = {2, 28, 4, 1, 1, 23, 0, 5, 1, 1, 2, 0, 59, 5};
		int job3[] = {1, 98, 1, 1, 1, 2, 0, 6, 0, 0, 2, 0, 77, 5};
		int job4[] = {1, 70, 1, 1, 0, 11, 3, 3, 1, 1, 2, 2, 76, 9};
		*/
		
		// AUC-Harmonic
	/*	int job0[] = {1, 73, 8, 1, 2, 3, 1, 4, 0, 1, 0, 0, 15, 4};
		int job1[] = {1, 50, 13, 0, 1, 3, 3, 4, 1, 1, 0, 0, 55, 3};
		int job2[] = {1, 12, 8, 1, 2, 93, 1, 3, 1, 1, 2, 0, 31, 3};
		int job3[] = {1, 7, 13, 0, 1, 23, 3, 0, 0, 1, 2, 0, 40, 4};
		int job4[] = {2, 65, 13, 0, 4, 15, 1, 4, 1, 0, 1, 0, 8, 6};*/
		
		// FM-Average
		/*int job0[] = {2, 47, 4, 0, 0, 78, 1, 0, 1, 1, 0, 0, 38, 4};
		int job1[] = {0, 11, 13, 0, 1, 100, 2, 4, 1, 0, 1, 0, 15, 4};
		int job2[] = {0, 75, 4, 0, 0, 42, 3, 4, 0, 0, 0, 0, 20, 9};
		int job3[] = {0, 75, 4, 0, 4, 53, 0, 0, 0, 1, 1, 0, 41, 4};
		int job4[] = {0, 33, 4, 0, 1, 40, 1, 5, 0, 1, 0, 3, 28, 4};*/
		
		// FM-Median
		/*int job0[] = {0, 89, 0, 1, 1, 80, 1, 6, 1, 1, 1, 0, 55, 6};
		int job1[] = {2, 68, 8, 1, 1, 20, 3, 2, 0, 0, 0, 2, 36, 3};
		int job2[] = {0, 82, 10, 0, 1, 20, 3, 3, 1, 0, 0, 2, 68, 7};
		int job3[] = {0, 31, 2, 0, 0, 38, 2, 3, 0, 1, 2, 0, 84, 5};
		int job4[] = {2, 64, 1, 1, 1, 80, 2, 5, 0, 0, 0, 2, 100, 6};*/
		
		// FM-Harmonic
		/*int job0[] = {0, 47, 13, 0, 0, 93, 1, 5, 0, 0, 0, 0, 45, 8};
		int job1[] = {0, 86, 4, 0, 1, 0, 1, 5, 1, 1, 1, 3, 16, 3};
		int job2[] = {2, 93, 4, 0, 1, 0, 2, 5, 1, 1, 1, 1, 49, 9};
		int job3[] = {1, 100, 13, 0, 4, 89, 0, 5, 0, 1, 2, 0, 34, 6};
		int job4[] = {2, 89, 13, 0, 0, 51, 3, 6, 1, 0, 0, 0, 94, 6};*/
		
		// MAI-Average
		/*int job0[] = {2, 30, 13, 0, 0, 2, 1, 6, 0, 0, 1, 0, 41, 3};
		int job1[] = {1, 69, 4, 0, 1, 0, 1, 5, 0, 0, 2, 0, 40, 6};
		int job2[] = {0, 23, 13, 0, 1, 60, 1, 3, 1, 0, 1, 2, 93, 9};
		int job3[] = {1, 47, 13, 0, 0, 79, 3,5, 1, 1, 0, 0, 40, 3};
		int job4[] = {2, 84, 13, 0, 0, 43, 2, 3, 1, 0, 0, 0, 12, 4};*/
		
		// MAI-Median
		/*int job0[] = {0, 8, 0, 1, 1, 60, 2, 5, 1, 0, 0, 0, 40, 4};
		int job1[] = {1, 70, 4, 0, 0, 44, 3, 0, 1, 1, 1, 0, 95, 2};
		int job2[] = {2, 59, 4, 1, 1, 80, 1, 4, 0, 0, 1, 2, 48, 2};
		int job3[] = {1, 16, 8, 0, 1, 100, 2, 4, 0, 1, 0, 0, 80, 4};
		int job4[] = {1, 81, 12, 1, 1, 40, 1, 3, 1, 0, 2, 0, 8, 10};*/
		
		// MAI-Harmonic  
/*		int job0[] = {2, 92, 6, 0, 0, 27, 3, 3, 1, 0, 2, 3, 13, 4};
		int job1[] = {1, 53, 6, 0, 0, 37, 3, 3, 0, 0, 2, 1, 97, 3};
		int job2[] = {0, 34, 6, 0, 0, 30, 3, 5, 0, 0, 2, 1, 45, 6};
		int job3[] = {2, 97, 6, 0, 1, 62, 1, 5, 1, 1, 1, 1, 78, 4};
		int job4[] = {2, 81, 6, 0, 1, 62, 1, 4, 1, 0, 1, 1, 97, 3}; */
		
		// TPR-Average 
		/*int job0[] = {2, 44, 4, 0, 1, 100, 1, 5, 0, 0, 1, 0, 75, 8};
		int job1[] = {1, 86, 4, 0, 1, 80, 0, 2, 0, 0, 0, 1, 96, 9};
		int job2[] = {0, 62, 13, 0, 1, 60, 2, 1, 0, 1, 2, 1, 45, 10};
		int job3[] = {2, 25, 13, 0, 1, 80, 3, 6, 1, 0, 2, 3, 32, 9};
		int job4[] = {0, 83, 13, 0, 1, 80, 0, 4, 0, 0, 0, 0, 13, 10};*/
		
		// TPR-Median
		/*int job0[] = {2, 85, 0, 1, 1, 20, 0, 5, 1, 0, 2, 2, 50, 4};
		int job1[] = {0, 51, 1, 1, 1, 20, 1, 3, 1, 1, 0, 4, 38, 5};
		int job2[] = {2, 7, 1, 1, 1, 100, 1, 2, 1, 1, 2, 0, 8, 9};
		int job3[] = {1, 99, 8, 1, 1, 0, 2, 6, 0, 1, 1, 2, 29, 9};
		int job4[] = {1, 39, 12, 1, 1, 60, 1, 2, 1, 0, 0, 2, 56, 3};*/
		
		// TPR-Harmonic
		int job0[] = {0, 8, 13, 0, 4, 17, 2, 0, 1, 1, 1, 0, 64, 8};
		int job1[] = {1, 92, 13, 0, 4, 71, 1, 5, 1, 0, 2, 0, 23, 6};
		int job2[] = {0, 27, 4, 0, 1, 100, 0, 4, 1, 1, 0, 3, 8, 9};
		int job3[] = {2, 65, 13, 0, 1, 0, 2, 1, 0, 1, 1, 1, 97, 5};
		int job4[] = {2, 62, 13, 0, 1, 80, 1, 6, 0, 0, 0, 1, 44, 3}; 
		
		
		

		param[0] = job0;
		param[1] = job1;
		param[2] = job2;
		param[3] = job3;
		param[4] = job4; 

		for (int job = 0; job < jobs; job++) {
			System.out.println("job "+job);
			setParameters(param[job],treeParam,splitParam);
			System.out.println(writeIndividual(param[job]));

			for (int base = 0; base < DataSets.size(); base++) {
				System.out.println(DataSets.get(base));
				String TreesFile = dirResults + "Trees.txt";
				PrintWriter pTreesFile = new PrintWriter(TreesFile);
				FileReader readerData = new FileReader(dirCurrent+DataSets.get(base)+".arff");
				Instances data = new Instances(readerData);
				data.setClassIndex(data.numAttributes()-1);

				EvolvedAlgorithm tree = new EvolvedAlgorithm(splitParam,treeParam);
				Evaluation evaluate = new Evaluation(data);
				evaluate.crossValidateModel(tree, data, 10, new Random(1));
				table[base][0][job] = 1 - evaluate.errorRate();
				table[base][1][job] = evaluate.weightedFMeasure();
				/*				table[base][2][job] = evaluationTrain.weightedPrecision();
				table[base][3][job] = evaluationTrain.weightedRecall();
				table[base][4][job] = 1- evaluationTest.errorRate();
				table[base][5][job] = evaluationTest.weightedFMeasure();
				table[base][6][job] = evaluationTest.weightedPrecision();
				table[base][7][job] = evaluationTest.weightedRecall();
				table[base][8][job] = tree.getRoot().numNodes();*/

				pTreesFile.close();
			}
			generateResults();
		}
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
		jobs = 5;

		dirCurrent = "/Users/rodrigobarros/Desktop/Datasets/100/";
		dirResults = "/Users/rodrigobarros/Desktop/Fitness/";
		//dirCurrent = "/Users/basgalupp/datasets/GeneExpression/";
		//dirResults = "/Users/basgalupp/datasets/results/teste2/";

		new File(dirResults).mkdir(); 

		measures = new ArrayList<String>();
		measures.add("Test Accuracy");
		measures.add("Test F-Measure");
		//		measures.add("Precision Train");
		//		measures.add("Recall Train");
		//		measures.add("Test Accuracy");
		//		measures.add("F-Measure Test");
		//		measures.add("Precision Test");
		//		measures.add("Recall Test");
		//		measures.add("Tree Size");

		//measures.add("Real");
		//measures.add("Predicted");


		//measures.add("Tree Depth");
		//measures.add("Execution Time");

		DataSets = new ArrayList<String>();


		// Gene Expression

		/*	DataSets.add("alizadeh-2000-v1");
		DataSets.add("alizadeh-2000-v2");
		DataSets.add("alizadeh-2000-v3");
		DataSets.add("armstrong-2002-v1");
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
		DataSets.add("lapointe-2004-v2");
		DataSets.add("liang-2005");
		DataSets.add("nutt-2003-v1");
		DataSets.add("nutt-2003-v2");
		DataSets.add("nutt-2003-v3");
		DataSets.add("pomeroy-2002-v1");
		DataSets.add("pomeroy-2002-v2");
		DataSets.add("ramaswamy-2001");
		DataSets.add("risinger-2003");
		DataSets.add("shipp-2002-v1");
		DataSets.add("singh-2002");
		DataSets.add("su-2001");
		DataSets.add("tomlins-2006");
		DataSets.add("tomlins-2006-v2");
		DataSets.add("west-2001");
		DataSets.add("yeoh-2002-v1");
		DataSets.add("yeoh-2002-v2"); */

		DataSets.add("arrhythmia");
		DataSets.add("kdd_synthetic_control");
		DataSets.add("semeion");
		DataSets.add("sick");
		DataSets.add("winequality-white");



		table = new double [DataSets.size()][measures.size()][jobs];
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
		String summaryAllFile = dirResults + "Summary.csv";
		PrintWriter pSummaryAllFile = new PrintWriter(summaryAllFile);
		String row = new String("dataset,");
		for (int m = 0; m < measures.size(); m++) {
			row += measures.get(m);
			if ( m< (measures.size()-1))
				row += ",,";
		}
		pSummaryAllFile.println(row);

		for (int d = 0; d < DataSets.size(); d++) {
			String jobsFile = dirResults + DataSets.get(d) + "jobs.csv";
			PrintWriter pFoldsFile = new PrintWriter(jobsFile);
			for (int f = 0; f < jobs; f++) 
				pFoldsFile.print(",job"+f);
			pFoldsFile.println();

			for (int m=0; m < measures.size(); m++) {
				row = new String(measures.get(m));
				for (int f=0; f < jobs; f++) {
					row += ","+table[d][m][f];
				}
				pFoldsFile.println(row);
			}
			pFoldsFile.close();

			row = new String(DataSets.get(d));
			for (int m = 0; m < measures.size(); m++) {
				double media = Utils.mean(table[d][m]);
				double desvpad = Math.sqrt(Utils.variance(table[d][m]));
				row += ","+media+","+desvpad;
			}
			pSummaryAllFile.println(row);
		}
		pSummaryAllFile.close();
	}

}