package headDt;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Main {

	public static FileWriter fwTest, fwAll, fwNew, fwParetoTest, fwParetoAll;
	public static PrintWriter pwTest, pwAll, pwNew, pConfusionMatrix, pwParetoTest, pwParetoAll, pConfusionMatrixPareto;
	public static FileWriter pop; 
	public static PrintWriter printPop; 
	public static FileWriter meta;
	public static PrintWriter printMeta;
	public static String path;
	public static double measuresSingle[][][][]; // [train,validation or test] [measure] [execution] [fold]
	//	public static double measuresMultiple[][][][][][]; // [metaTraining,metaTest] -- [train or test] ...
	public static double measuresMultiple[][][][][]; // [metaTraining,metaTest] -- [train or test] -- [measure] -- [execution] ... obs: nao tem resultado por folds
	public static int fitnessType;
	public static int fitnessAggregationScheme;
	public static int metaTrainingEvaluationType;
	public static int multiObjectiveType;
	public static int numJobs;
	public static MetaData metaData[];
	public static double beta;
	
	public static FastScatterPlotDemo scatterPlot;
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		/*
		 * Parameters List:
		 * args[0] = dataset name or txt name with meta-training and test set names
		 * 
		 * args[1] = path of the datasets
		 * 
		 * args[2] = path of the results
		 * 
		 * args[3] = number of jobs (executions)
		 * 
		 * args[4] = fitness type:
		 * Single:
		 * 0 = accuracy (single)
		 * 1 = fmeasure (single)
		 * 2 = auc (single)
		 * 3 = balance (single)
		 * 4 = gmean (single)
		 * Multiplo:
		 * 0 = accuracy improvement Gisele
		 * 1 = accuracy
		 * 2 = f-measure
		 * 3 = AUC
		 * 4 = mean true positive rate
		 * 
		 * args[5] = name of the parameters file
		 * 
		 * args[6] = type of multiple meta-training evaluation
		 * 0 = all sets per generation
		 * 1 = 5 sets per generation in a circular vector
		 * 
		 * args[7] = fitness aggregation scheme:
		 * 0 = simple average
		 * 1 = median
		 * 2 = harmonic mean
		 * 
		 * args[8] = abordagem multi-objetiva
		 *
		 *		0 = singleObjective
		 *		1 = Weighted-Formula
		 *		2 = Pareto
		 *		3 = Lexicográfica
		 *		default = single Objective 
		 * 
		 * args[9] = if we have already split the folds ()
		 * 		0 = false
		 * 		1 = true
		 * 
		 */

		beta = 1;
		
		numJobs = Integer.valueOf(args[3]);

		if (Integer.valueOf(args[9]) == 1) {
			Dataset.setSplitedFiles(true);
		}
		
		new Dataset(args[0],args[1]);
		
		String string[] = {"-file",args[5], "-p","jobs="+args[3]};
		fitnessType = Integer.valueOf(args[4]);

		multiObjectiveType = Integer.valueOf(args[8]);

		metaTrainingEvaluationType = Integer.valueOf(args[6]);
		fitnessAggregationScheme = Integer.valueOf(args[7]);

		if(!Dataset.isMultiple()){
			new File(args[2]).mkdir();
			path = args[2]+Dataset.getFullData().relationName()+"/";
			//	pop = new FileWriter(Main.path+"/population.csv");
			//	printPop = new PrintWriter(pop);
			new File(path).mkdir();
			EvolveSingleObjective.mainSingleDataset(string);
		}

		else{
			int size = Dataset.getMetaTraining().size();
			// cria a classe que armazena os meta-dados
			metaData = new MetaData[size];
			for(int i=0; i<size;i++){
				metaData[i] = new MetaData();
			}
			path = args[2];
			pop = new FileWriter(Main.path+"/population.csv");
			printPop = new PrintWriter(pop);
			meta = new FileWriter(Main.path+"/meta-data.csv");
			printMeta = new PrintWriter(meta);
			EvolveSingleObjective.mainMultipleDatasets(string);
		}
	}
}


