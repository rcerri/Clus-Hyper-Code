package ga;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import Util.ClusWrapper;
import Util.ClusWrapperNonStatic;
import ec.EvolutionState;
import ec.Evolve;
import ec.util.Output;
import ec.util.Parameter;
import ec.util.ParameterDatabase;
import weka.core.Utils;

public class myEvolve extends Evolve{

	public static Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(ParameterDatabase parameters, ClusWrapperNonStatic objectClus) {
		EvolutionState state;
		//ParameterDatabase parameters;

		ParameterDatabase original = (ParameterDatabase) deepClone(parameters);

		int currentJob = 0;                             // the next job number (0 by default)
		//parameters = loadParameterDatabase(args);

		if (currentJob == 0)  // no current job number yet
			currentJob = parameters.getIntWithDefault(new Parameter("current-job"), null, 0);
		if (currentJob < 0)
			Output.initialError("The 'current-job' parameter must be >= 0 (or not exist, which defaults to 0)");

		int numJobs = parameters.getIntWithDefault(new Parameter("jobs"), null, 1);
		if (numJobs < 1)
			Output.initialError("The 'jobs' parameter must be >= 1 (or not exist, which defaults to 1)");


		// Now we know how many jobs remain.  Let's loop for that many jobs.  Each time we'll
		// load the parameter database scratch (except the first time where we reuse the one we
		// just loaded a second ago).  The reason we reload from scratch each time is that the
		// experimenter is free to scribble all over the parameter database and it'd be nice to
		// have everything fresh and clean.  It doesn't take long to load the database anyway,
		// it's usually small.
		for(int job = currentJob ; job < numJobs; job++)
		{
			// We used to have a try/catch here to catch errors thrown by this job and continue to the next.
			// But the most common error is an OutOfMemoryException, and printing its stack trace would
			// just create another OutOfMemoryException!  Which dies anyway and has a worthless stack
			// trace as a result.

			// try
			{
				// load the parameter database (reusing the very first if it exists)
				if (parameters == null)
					parameters = (ParameterDatabase) deepClone(original);
				//	   parameters = loadParameterDatabase(args);

				// Initialize the EvolutionState, then set its job variables
				state = initialize(parameters, job, objectClus);                // pass in job# as the seed increment
				state.output.systemMessage("Job: " + job);
				state.job = new Object[1];                                  // make the job argument storage
				state.job[0] = Integer.valueOf(job);                    // stick the current job in our job storage
				//state.runtimeArguments = args;                              // stick the runtime arguments in our storage
				if (numJobs > 1)                                                    // only if iterating (so we can be backwards-compatible),
				{
					String jobFilePrefix = "job." + job + ".";
					state.output.setFilePrefix(jobFilePrefix);     // add a prefix for checkpoint/output files 
					state.checkpointPrefix = jobFilePrefix + state.checkpointPrefix;  // also set up checkpoint prefix
				}

				// Here you can set up the EvolutionState's parameters further before it's setup(...).
				// This includes replacing the random number generators, changing values in state.parameters,
				// changing instance variables (except for job and runtimeArguments, please), etc.


				// now we let it go
				state.run(EvolutionState.C_STARTED_FRESH);
			
				//Main.measuresSingle = new double[3][nMeasures][Dataset.getNumFolds()][numJobs]; //  3 types  = Train, Validation and Test
						
				cleanup(state);  // flush and close various streams, print out parameters if necessary
				parameters = null;  // so we load a fresh database next time around
			}

		}

		System.exit(0);
	}

	public static void mainSingleDataset(ParameterDatabase parameters) throws Exception
	{


		EvolutionState state;
		//ParameterDatabase parameters;

		ParameterDatabase original = (ParameterDatabase) deepClone(parameters);

		int currentJob = 0;                             // the next job number (0 by default)
		//parameters = loadParameterDatabase(args);

		if (currentJob == 0)  // no current job number yet
			currentJob = parameters.getIntWithDefault(new Parameter("current-job"), null, 0);
		if (currentJob < 0)
			Output.initialError("The 'current-job' parameter must be >= 0 (or not exist, which defaults to 0)");

		int numJobs = parameters.getIntWithDefault(new Parameter("jobs"), null, 1);
		if (numJobs < 1)
			Output.initialError("The 'jobs' parameter must be >= 1 (or not exist, which defaults to 1)");


		// Now we know how many jobs remain.  Let's loop for that many jobs.  Each time we'll
		// load the parameter database scratch (except the first time where we reuse the one we
		// just loaded a second ago).  The reason we reload from scratch each time is that the
		// experimenter is free to scribble all over the parameter database and it'd be nice to
		// have everything fresh and clean.  It doesn't take long to load the database anyway,
		// it's usually small.


		//String[] measuresNames = {"MAE", "MSE", "RMSE", "WRMSE", "Total Nodes", "Total Leaves", "Balance", "AUC", "Gmean", "AvgClassAccuracy"};
		String[] measuresNames;
		if (Main.mlTask == 0) {
			measuresNames = new String[4];
			measuresNames[0] = "MAE"; measuresNames[1] = "MSE"; measuresNames[2] = "RMSE"; measuresNames[3] = "Execution Time";
		}
		else {
			measuresNames = new String[3];
			measuresNames[0] = "AUROC"; measuresNames[1] = "AUPRC"; measuresNames[2] = "Execution Time";
		}
		
		int nMeasures = measuresNames.length;
		Main.measuresSingle = new double[3][nMeasures][Dataset.getNumFolds()][numJobs]; //  3 types  = Train, Validation and Test

		//  7 measures = accuracy, fmeasure, precision, recall, numNodes, numLeaves, balance, AUC, gmean and avgClassAccuracy

		int startFold = Main.startFold;
		for(int i = startFold; i < Dataset.getNumFolds(); i++){
			Dataset.setCurrentFold(i);
			
			String trainSet = Dataset.getPath()+Dataset.getFileName() + "_fold"+(i)+".train";
			String valSet = Dataset.getPath()+Dataset.getFileName() + "_fold"+(i)+".valid";
			
			ClusWrapperNonStatic objectClus = new ClusWrapperNonStatic();

			
			if (Main.mlTask == 0 && Main.parallel == 0){
				ClusWrapper.clus = null;
				ClusWrapper.initialization(trainSet,valSet, Main.targets,Main.randomForest,false);
			}else if (Main.mlTask == 1 && Main.parallel == 0) {
				ClusWrapper.clus = null;
				ClusWrapper.initialization(trainSet,valSet, Main.targets,Main.randomForest,true);
			//	System.out.println("HEre I am"); System.exit(1);
			}
			else if (Main.mlTask == 1 && Main.parallel == 1) { // Classification in parallel
				// nothing. It is done in myProblem.evaluate()
				objectClus.clus = null;
			 	objectClus.initialization(trainSet,valSet, Main.targets,Main.randomForest,true); // for the simulated annealing, this one has to be done.
			}else if (Main.mlTask == 0 && Main.parallel == 1) {  // Regression in parallel
				// nothing. It is done in myProblem.evaluate()
				objectClus.clus = null;
			 	objectClus.initialization(trainSet,valSet, Main.targets,Main.randomForest,false); // for the simulated annealing, this one has to be done.
			}
					
			System.out.println("fold = "+i);
			new File(Main.path+"Fold"+i+"/").mkdir();
			Main.fwTest = new FileWriter(Main.path+"Fold"+i+"/resultado-fold"+i+".csv");
			Main.fwAll = new FileWriter(Main.path+"Fold"+i+"/resultado-All-fold"+i+".csv");

			Main.pwTest = new PrintWriter(Main.fwTest);
			Main.pwAll = new PrintWriter(Main.fwAll);

			if (Main.mlTask == 1) {
				Main.pwTest.println("AUROC,AUPRC,Executio Time");
				Main.pwAll.println("TrainVal AUROC,Validation AUROC,Test AUROC,TrainVal AUPRC,Validation AUPRC,Test AUPRC, Execution Time");
			}
			else {
				Main.pwTest.println("MAE,MSE,RMSE,Execution Time");
				Main.pwAll.println("TrainVal MAE,Validation MAE,Test MAE,TrainVal MSE,Validation MSE,Test MSE,TrainVal RMSE,Validation RMSE,Test RMSE,Execution Time");
			}

			for(int job = currentJob ; job < numJobs; job++){
				try
				{

					Main.fFirstGen = new FileWriter(Main.path+"Fold"+i+"/firstGeneration_job"+job+".csv");
					Main.pFirstGen = new PrintWriter(Main.fFirstGen);

					Main.fLastGen = new FileWriter(Main.path+"Fold"+i+"/lastGeneration_job"+job+".csv");
					Main.pLastGen = new PrintWriter(Main.fLastGen);

					Main.fEvolution = new FileWriter(Main.path+"Fold"+i+"/Evolution_job"+job+".csv");
					Main.pEvolution = new PrintWriter(Main.fEvolution);


					if (Main.mlTask == 1) { // classification
						Main.pFirstGen.println("TrainVal AUROC, TrainVal AUPRC, Test AUROC, Test AUPRC, Execution Time");
						Main.pLastGen.println("TrainVal AUROC, TrainVal AUPRC, Test AUROC, Test AUPRC, Execution Time");
					}
					else { // regression
						Main.pFirstGen.println("TrainVal MAE, TrainVal MSE, TrainVal RMSE, Test MAE, Test MSE, Test RMSE, Execution Time");
						Main.pLastGen.println("TrainVal MAE, TrainVal MSE, TrainVal RMSE, Test MAE, Test MSE, Test RMSE, Execution Time");
					}

					//Main.pEvolution.println("Train MAE, Train MSE, Train RMSE, Test MAE, Test MSE, Test RMSE");
					Main.pEvolution.println("Generation, Fitness");

					// load the parameter database (reusing the very first if it exists)
					if (parameters == null)
						parameters = (ParameterDatabase) deepClone(original);
					//parameters = loadParameterDatabase(args);

					// Initialize the EvolutionState, then set its job variables
					state = initialize(parameters, job, objectClus);                // pass in job# as the seed increment
					state.output.systemMessage("Job: " + job);
					state.job = new Object[1];                                  // make the job argument storage
					state.job[0] = new Integer(job);                    // stick the current job in our job storage
					//state.runtimeArguments = args;                              // stick the runtime arguments in our storage
					if (numJobs > 1)                                                    // only if iterating (so we can be backwards-compatible),
					{
						String jobFilePrefix = "job." + job + ".";
						state.output.setFilePrefix(jobFilePrefix);     // add a prefix for checkpoint/output files 
						state.checkpointPrefix = jobFilePrefix + state.checkpointPrefix;  // also set up checkpoint prefix
					}

					// Here you can set up the EvolutionState's parameters further before it's setup(...).
					// This includes replacing the random number generators, changing values in state.parameters,
					// changing instance variables (except for job and runtimeArguments, please), etc.

					state.parameters.set(new ec.util.Parameter("stat.file"), ""+Main.path+"Fold"+i+"/out.stat");
					state.parameters.set(new ec.util.Parameter("stat.child.0.file"), ""+Main.path+"Fold"+i+"/out2.stat");

					// now we let it go
					
					
					long iTime = System.currentTimeMillis();
					state.run(EvolutionState.C_STARTED_FRESH);
					long fTime = System.currentTimeMillis();
					Main.measuresSingle[0][nMeasures-1][i][job] = Main.measuresSingle[1][nMeasures-1][i][job] = Main.measuresSingle[2][nMeasures-1][i][job] = (fTime-iTime)/1000;
					
					cleanup(state);  // flush and close various streams, print out parameters if necessary
					parameters = null;  // so we load a fresh database next time around

					Main.fFirstGen.close();
					Main.fLastGen.close();
					Main.fEvolution.close();
				}
				catch (Throwable e)  // such as an out of memory error caused by this job
				{
					e.printStackTrace();
					state = null;
					System.gc();  // take a shot!
				}

			}

			//Main.pwTest.println("=average(A2:A"+(numJobs+1)+"),=average(B2:B"+(numJobs+1)+"),=average(C2:C"+(numJobs+1)+"),=average(D2:D"+(numJobs+1)+"),=average(E2:E"+(numJobs+1)+"),=average(F2:F"+(numJobs+1)+"),=average(G2:G"+(numJobs+1)+"),=average(H2:H"+(numJobs+1)+"),=average(I2:I"+(numJobs+1)+"),=average(J2:J"+(numJobs+1)+")");
			//Main.pwAll.println("=average(A2:A"+(numJobs+1)+"),=average(B2:B"+(numJobs+1)+"),=average(C2:C"+(numJobs+1)+"),=average(D2:D"+(numJobs+1)+"),=average(E2:E"+(numJobs+1)+"),=average(F2:F"+(numJobs+1)+"),=average(G2:G"+(numJobs+1)+"),=average(H2:H"+(numJobs+1)+"),=average(I2:I"+(numJobs+1)+"),=average(J2:J"+(numJobs+1)+"),=average(K2:K"+(numJobs+1)+"),=average(L2:L"+(numJobs+1)+"),=average(M2:M"+(numJobs+1)+"),=average(N2:N"+(numJobs+1)+"),=average(O2:O"+(numJobs+1)+"),=average(P2:P"+(numJobs+1)+"),=average(Q2:Q"+(numJobs+1)+"),=average(R2:R"+(numJobs+1)+"),=average(S2:S"+(numJobs+1)+"),=average(T2:T"+(numJobs+1)+"),=average(U2:U"+(numJobs+1)+"),=average(V2:V"+(numJobs+1)+"),=average(W2:W"+(numJobs+1)+"),=average(X2:X"+(numJobs+1)+"),=average(Y2:Y"+(numJobs+1)+"),=average(Z2:Z"+(numJobs+1)+")");
			Main.pwTest.println("=average(A2:A"+(numJobs+1)+"),=average(B2:B"+(numJobs+1)+"),=average(C2:C"+(numJobs+1)+"),=average(D2:D"+(numJobs+1)+")");
			Main.pwAll.println("=average(A2:A"+(numJobs+1)+"),=average(B2:B"+(numJobs+1)+"),=average(C2:C"+(numJobs+1)+"),=average(D2:D"+(numJobs+1)+"),=average(E2:E"+(numJobs+1)+"),=average(F2:F"+(numJobs+1)+"),=average(G2:G"+(numJobs+1)+")");


			Main.pwTest.close();
			Main.pwAll.close();
			Main.fwTest.close();
			Main.fwAll.close();

		}

		Main.fwTest = new FileWriter(Main.path+"/resultadoFinal.csv");
		Main.fwAll = new FileWriter(Main.path+"/resultadoAllFinal.csv");

		Main.pwTest = new PrintWriter(Main.fwTest);
		Main.pwAll = new PrintWriter(Main.fwAll);

		String headerTest = measuresNames[0];
		String headerAll = "Train " + measuresNames[0] + ", Validation " + measuresNames[0] + ", Test " + measuresNames[0];
		for (int m = 1; m < measuresNames.length; m++) {
			headerTest += "," + measuresNames[m];
			headerAll += ", Train " + measuresNames[m] + ", Validation " + measuresNames[m] + ", Test " + measuresNames[m];
		}

		Main.pwTest.println(headerTest);
		Main.pwAll.println(headerAll);
		//		Main.pwTest.println("Accuracy,F-Measure,Precision,Recall,Total Nodes,Total Leaves,Balance, AUC, Gmean, AvgClassAccuracy");
		//		Main.pwAll.println("Train Accuracy,Validation Accuracy,Test Accuracy,Train F-Measure,Validation F-Measure,Test F-Measure,Train Precision,Validation Precision,Test Precision,Train Recall,Validation Recall, Test Recall,Total Nodes,Total Leaves,Train Balance,Validation Balance,Test Balance,Train AUC,Validation AUC,Test AUC,Train Gmean,Validation Gmean,Test Gmean,Train AvgClassAccuracy, Validation AvgClassAccuracy, Test AvgClassAccuracy");


		double measures[][][] = new double[nMeasures][3][Dataset.getNumFolds()]; // numero de medidas

		for (int m = 0; m < nMeasures ; m++) {
			for(int f = 0; f <  Dataset.getNumFolds(); f++){
				measures[m][0][f] = Utils.mean(Main.measuresSingle[0][m][f]);
				measures[m][1][f] = Utils.mean(Main.measuresSingle[1][m][f]);
				measures[m][2][f] = Utils.mean(Main.measuresSingle[2][m][f]);
			}
		}

		// ajustar para calcular a media das N execucoes pra cada FOLD, e depois a media dos folds.

		String measuresTest = String.valueOf(Utils.mean(measures[0][2]));
		String measuresAll = Utils.mean(measures[0][0]) + "," +Utils.mean(measures[0][1]) + "," + Utils.mean(measures[0][2]);

		for (int m = 1; m < measuresNames.length; m++) {
			measuresTest += "," + Utils.mean(measures[m][2]);
			measuresAll += "," + Utils.mean(measures[m][0]) + "," +Utils.mean(measures[m][1]) + "," + Utils.mean(measures[m][2]);
		}

		Main.pwTest.println(measuresTest);
		Main.pwAll.println(measuresAll);

		Main.pwTest.close();
		Main.pwAll.close();
		Main.fwTest.close();
		Main.fwAll.close();
		System.exit(0);
	}
} 
