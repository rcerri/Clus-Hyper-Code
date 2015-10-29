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
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.supervised.instance.StratifiedRemoveFolds;

public class MetaOverfitting {

	static ArrayList<String> measures;
	static ArrayList<String> datasets;
	static String dirCurrent, dirResults;
	static int configurations;
	static double table[][][];
	static double c45[][][];

	public static void main(String[] args) throws Exception {
		initialization();
		new File(dirResults).mkdir();
		TreeParameters treeParam = new TreeParameters();
		SplitParameters splitParam = new SplitParameters();
		int param[][] = new int[5][14];  // num jobs = 5, num param = 14

			
		// Testando Meta-Overfitting
		int config1[] = {1, 99, 14, 0, 2, 98, 3, 0, 0, 1, 2, 0, 66, 9};
		int config3[] = {2, 73, 13, 0, 2, 58, 2, 1, 0, 0, 0, 0, 22, 9};
		int config5[] = {0, 42, 13, 0, 2, 9, 2, 6, 0, 1, 0, 0, 13, 8};
		int config7[] = {2, 17, 4, 1, 4, 77, 0, 2, 1, 0, 1, 2, 99, 7};
		int config9[] = {2, 50, 14, 1, 2, 16, 3, 5, 1, 0, 2, 1, 34, 9};
		

		param[0] = config1;
		param[1] = config3;
		param[2] = config5;
		param[3] = config7;
		param[4] = config9; 

		for (int config = 0; config < configurations; config++) {
			System.out.println("Configuracao "+config);
			setParameters(param[config],treeParam,splitParam);
			System.out.println(writeIndividual(param[config]));
			
			setMetaTrainingSet(config);

			for (int base = 0; base < datasets.size(); base++) {
				System.out.println(datasets.get(base));
				FileReader readerData = new FileReader(dirCurrent+datasets.get(base)+".arff");
				Instances data = new Instances(readerData);
				data.setClassIndex(data.numAttributes()-1);
				data.stratify(10);
				
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

				EvolvedAlgorithm tree = new EvolvedAlgorithm(splitParam,treeParam);
				tree.buildClassifier(training);
				
				J48 j48 = new J48();
				j48.buildClassifier(training);
				
				Evaluation evaluate = new Evaluation(data);
				Evaluation evaluateJ48 = new Evaluation(data);
				evaluate.evaluateModel(tree, test);
				evaluateJ48.evaluateModel(j48,test);
				
				
				
				table[base][0][config] = 1 - evaluate.errorRate();
				table[base][1][config] = evaluate.weightedFMeasure();
				
				c45[base][0][config] = 1 - evaluateJ48.errorRate();
				c45[base][1][config] = evaluateJ48.weightedFMeasure();
				
				
				/* table[base][2][job] = evaluationTrain.weightedPrecision();
				table[base][3][job] = evaluationTrain.weightedRecall();
				table[base][4][job] = 1- evaluationTest.errorRate();
				table[base][5][job] = evaluationTest.weightedFMeasure();
				table[base][6][job] = evaluationTest.weightedPrecision();
				table[base][7][job] = evaluationTest.weightedRecall();
				table[base][8][job] = tree.getRoot().numNodes();*/

			}
		}
		generateResults();
	}

	
	
	
	public static void setMetaTrainingSet(int config){
		
		switch(config){
		// 1 dataset no meta-training
		case 0: datasets.add("hayes-roth-full");
		        break;
		        
		// 3 datasets no meta-training        
		case 1: datasets.add("labor");
				datasets.add("tae");
				break;
				
		// 5 datasets no meta-training        
		case 2: datasets.add("iris");
				datasets.add("haberman");
				break;
				
		// 7 datasets no meta-training        
		case 3: datasets.add("postoperative-patient-data");
				datasets.add("bridges_version1");
				break;
				
		// 9 datasets no meta-training        
		case 4: datasets.add("tempdiag");
				datasets.add("lung-cancer");
				break;
		}
		
	}
	




	public static void initialization() {
		configurations = 5;

		dirCurrent = "/Users/rodrigobarros/Desktop/Datasets/Arrumados/";
		dirResults = "/Users/rodrigobarros/Desktop/MetaOverfitting/";
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

		datasets = new ArrayList<String>();
		table = new double[9][2][5];
		c45 = new double[9][2][5];


	}



	public static void generateResults() throws FileNotFoundException {
		//		String classification = dirResults + "Classification"+classifier+".csv";
		String config1 = dirResults + "Config1.csv";
		String config3 = dirResults + "Config3.csv";
		String config5 = dirResults + "Config5.csv";
		String config7 = dirResults + "Config7.csv";
		String config9 = dirResults + "Config9.csv";
		PrintWriter pw1 = new PrintWriter(config1);
		PrintWriter pw3 = new PrintWriter(config3);
		PrintWriter pw5 = new PrintWriter(config5);
		PrintWriter pw7 = new PrintWriter(config7);
		PrintWriter pw9 = new PrintWriter(config9);
		
		PrintWriter pw[] = new PrintWriter[5];
		pw[0] = pw1;
		pw[1] = pw3;
		pw[2] = pw5;
		pw[3] = pw7;
		pw[4] = pw9;
		
		datasets.clear();
		for(int config = 0;config<configurations;config++){
			pw[config].println("Dataset,Accuracy HEAD,Accuracy C4.5, F-Measure HEAD, F-Measure C4.5");
			setMetaTrainingSet(config);
			
			for(int i=0;i<datasets.size();i++){
				pw[config].println(datasets.get(i)+","+table[i][0][config]+","+c45[i][0][config]+","+table[i][1][config]+","+c45[i][1][config]);
			}
			pw[config].close();
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