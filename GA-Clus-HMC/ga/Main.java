package ga;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class Main {

	public static FileWriter fwTest, fwAll, fwNew, fFirstGen, fLastGen, fEvolution;
	public static PrintWriter pwTest, pwAll, pwNew, pFirstGen, pLastGen, pEvolution;
	public static String path, targets,stratifiedMethod;
	public static double measuresSingle[][][][]; // [train,validation or test] [measure] [execution] [fold]
	public static double measuresMultiple[][][][][]; // [metaTraining,metaTest] -- [train or test] -- [measure] -- [execution] ... obs: nao tem resultado por folds
	public static int fitnessType, fitnessAggregationScheme, metaTrainingEvaluationType, multiObjectiveType, numJobs, evaluationType;
	public static boolean weightedMeasures, randomForest;
	public static int startFold = 0, initialPopBaselines, mlTask;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/* Parameters List:
		 * args[0] = dataset name (example: water-quality)
		 * args[1] = path of the datasets
		 * args[2] = path of the results
		 * args[3] = number of jobs (executions)
		 * args[4] = fitness type: 
		 * 0 = MAE (single)
		 * 1 = MSE (single)
		 * 2 = RMSE (single)
		 * 3 = WRMSE (single)
		 * args[5] = name of the parameters file
		 * args[6] = number of folds
		 * args[7] = index of the first target attribute
		 * args[8] = index of the last target attribute
		 * args[9] = single tree (0) or randomForest (1)
		 * args[10] =
		 * 				0 = totally randomly initial population
		 * 				1 = with baselines (all and independent) added in the first population
		 * 
		 * args[11] = ML task
		 * 				0 = regression
		 * 				1 = classification    
		 * 
		 * args[12] = if ML task = classification, stratified type
		 * 				0 = labelset
		 * 				1 = iterative
		 */
		
		
		randomForest = Integer.valueOf(args[9]) == 0 ? false : true;
		initialPopBaselines = Integer.valueOf(args[10]);
		
		targets = args[7] + "-" + args[8];
		numJobs = Integer.valueOf(args[3]);
		
		mlTask = Integer.valueOf(args[11]); // 0 = regression, 1 = classification
		if (mlTask == 1) { // classification
			stratifiedMethod = Integer.valueOf(args[12]) == 0 ? "labelset" : "iterative";
			new Dataset(args[1]+stratifiedMethod+"/",args[0],Integer.valueOf(args[6]));
			//new Dataset(args[1]+args[0]+"/"+stratifiedMethod+"/",args[0],Integer.valueOf(args[6]));
		}
		else {
			new Dataset(args[1]+args[0]+"/",args[0],Integer.valueOf(args[6]));
		}
			
		fitnessType = Integer.valueOf(args[4]);

		path = args[2] + args[0] + "/";
		new File(path).mkdirs();

		final File pFile = new File("ga/ga.params");
		final ParameterDatabase parameters = new ParameterDatabase(pFile);

		Parameter params[] = new Parameter[4];
		params[0] = new Parameter("pop.subpop.0.species.genome-size");
		params[1] = new Parameter("jobs");
		params[2] = new Parameter("pop.subpop.0.species.min-gene");
		params[3] = new Parameter("pop.subpop.0.species.max-gene");

		int nTargets = 1 + Integer.valueOf(args[8]) - Integer.valueOf(args[7]);
		String[] values = {String.valueOf(nTargets), args[3], "1", String.valueOf(nTargets)}; 
		for (int i = 0; i < params.length; i++) {
			parameters.set(params[i],values[i]);
		}
		
		myEvolve.mainSingleDataset(parameters);
		
	}
}


