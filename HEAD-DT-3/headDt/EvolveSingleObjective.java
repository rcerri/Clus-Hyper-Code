package headDt;


import ec.EvolutionState;
import ec.Evolve;
import ec.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import weka.core.Utils;


public class EvolveSingleObjective extends Evolve
{
	/** Rodrigo criou isso */
	public static EvolutionState state2 = null;

	public static void mainSingleDataset(String[] args) throws Exception
	{
		EvolutionState state;
		ParameterDatabase parameters;


		// if we're loading from checkpoint, let's finish out the most recent job
		state = possiblyRestoreFromCheckpoint(args);
		int currentJob = 0;                             // the next job number (0 by default)

		// this simple job iterator just uses the 'jobs' parameter, iterating from 0 to 'jobs' - 1
		// inclusive.  The current job number is stored in state.jobs[0], so we'll begin there if
		// we had loaded from checkpoint.

		if (state != null)  // loaded from checkpoint
		{
			// extract the next job number from state.job[0] (where in this example we'll stash it)
			try
			{
				if (state.runtimeArguments == null)
					Output.initialError("Checkpoint completed from job started by foreign program (probably GUI).  Exiting...");
				args = state.runtimeArguments;                          // restore runtime arguments from checkpoint
				currentJob = ((Integer)(state.job[0])).intValue() + 1;  // extract next job number
			}
			catch (Exception e)
			{
				Output.initialError("EvolutionState's jobs variable is not set up properly.  Exiting...");
			}

			state.run(EvolutionState.C_STARTED_FROM_CHECKPOINT);
			cleanup(state);
		}

		// A this point we've finished out any previously-checkpointed job.  If there was
		// one such job, we've updated the current job number (currentJob) to the next number.
		// Otherwise currentJob is 0.

		// Now we're going to load the parameter database to see if there are any more jobs.
		// We could have done this using the previous parameter database, but it's no big deal.
		parameters = loadParameterDatabase(args);
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

		Main.measuresSingle = new double[3][10][numJobs][Dataset.getNumFolds()]; //  3 types  = Train, Validation and Test 
		//  7 measures = accuracy, fmeasure, precision, recall, numNodes, numLeaves, balance, AUC, gmean and avgClassAccuracy

		
		
		
		for(int i=0; i<Dataset.getNumFolds();i++){
			Dataset.setFold(i);
			Dataset.splitData(1);
			new File(Main.path+"Fold"+i+"/").mkdir();
			Main.fwTest = new FileWriter(Main.path+"Fold"+i+"/resultado-fold"+i+".csv");
			Main.fwAll = new FileWriter(Main.path+"Fold"+i+"/resultado-All-fold"+i+".csv");
			
			Main.fwParetoTest = new FileWriter(Main.path+"Fold"+i+"/resultado-Pareto-fold"+i+".csv");
			Main.fwParetoAll = new FileWriter(Main.path+"Fold"+i+"/resultado-Pareto-All-fold"+i+".csv");
			
			
			String outFileCM = Main.path+"Fold"+i+"/confusionMatrix-fold"+i+".csv";
			Main.pConfusionMatrix = new PrintWriter(outFileCM);
			
			Main.pConfusionMatrixPareto = new PrintWriter(Main.path+"Fold"+i+"/confusionMatrix-Pareto-fold"+i+".csv");
			

			Main.pwTest = new PrintWriter(Main.fwTest);
			Main.pwAll = new PrintWriter(Main.fwAll);
			
			Main.pwParetoTest = new PrintWriter(Main.fwParetoTest);
			Main.pwParetoAll = new PrintWriter(Main.fwParetoAll);
			

			Main.pwTest.println("Accuracy,F-Measure,Precision,Recall,Total Nodes,Total Leaves,Balance,AUC,Gmean,AvgClassAccuracy");
			Main.pwAll.println("Train Accuracy,Validation Accuracy,Test Accuracy,Train F-Measure,Validation F-Measure,Test F-Measure,Train Precision,Validation Precision,Test Precision,Train Recall,Validation Recall, Test Recall,Total Nodes,Total Leaves,Train Balance,Validation Balance,Test Balance, Train AUC, Validation AUC, Test AUC, Train Gmean, Validation Gmean, Test Gmean, Train AvgClassAccuracy, Validation AvgClassAccuracy, Test AvgClassAccuracy");
			
			Main.pwParetoTest.println("Accuracy,F-Measure,Precision,Recall,Total Nodes,Total Leaves,Balance,AUC,Gmean,AvgClassAccuracy");
			Main.pwParetoAll.println("Train Accuracy,Validation Accuracy,Test Accuracy,Train F-Measure,Validation F-Measure,Test F-Measure,Train Precision,Validation Precision,Test Precision,Train Recall,Validation Recall, Test Recall,Total Nodes,Total Leaves,Train Balance,Validation Balance,Test Balance, Train AUC, Validation AUC, Test AUC, Train Gmean, Validation Gmean, Test Gmean, Train AvgClassAccuracy, Validation AvgClassAccuracy, Test AvgClassAccuracy");
			


			for(int job = currentJob ; job < numJobs; job++){
				try
				{
					// load the parameter database (reusing the very first if it exists)
					if (parameters == null)
						parameters = loadParameterDatabase(args);

					// Initialize the EvolutionState, then set its job variables
					state = initialize(parameters, job);                // pass in job# as the seed increment
					state.output.systemMessage("Job: " + job);
					state.job = new Object[1];                                  // make the job argument storage
					state.job[0] = new Integer(job);                    // stick the current job in our job storage
					state.runtimeArguments = args;                              // stick the runtime arguments in our storage
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
					state.run(EvolutionState.C_STARTED_FRESH);
					state2 = state;
					cleanup(state);  // flush and close various streams, print out parameters if necessary
					parameters = null;  // so we load a fresh database next time around
				}
				catch (Throwable e)  // such as an out of memory error caused by this job
				{
					e.printStackTrace();
					state = null;
					System.gc();  // take a shot!
				}
			}
			Main.pwTest.println("=average(A2:A"+(numJobs+1)+"),=average(B2:B"+(numJobs+1)+"),=average(C2:C"+(numJobs+1)+"),=average(D2:D"+(numJobs+1)+"),=average(E2:E"+(numJobs+1)+"),=average(F2:F"+(numJobs+1)+"),=average(G2:G"+(numJobs+1)+"),=average(H2:H"+(numJobs+1)+"),=average(I2:I"+(numJobs+1)+"),=average(J2:J"+(numJobs+1)+")");
			Main.pwAll.println("=average(A2:A"+(numJobs+1)+"),=average(B2:B"+(numJobs+1)+"),=average(C2:C"+(numJobs+1)+"),=average(D2:D"+(numJobs+1)+"),=average(E2:E"+(numJobs+1)+"),=average(F2:F"+(numJobs+1)+"),=average(G2:G"+(numJobs+1)+"),=average(H2:H"+(numJobs+1)+"),=average(I2:I"+(numJobs+1)+"),=average(J2:J"+(numJobs+1)+"),=average(K2:K"+(numJobs+1)+"),=average(L2:L"+(numJobs+1)+"),=average(M2:M"+(numJobs+1)+"),=average(N2:N"+(numJobs+1)+"),=average(O2:O"+(numJobs+1)+"),=average(P2:P"+(numJobs+1)+"),=average(Q2:Q"+(numJobs+1)+"),=average(R2:R"+(numJobs+1)+"),=average(S2:S"+(numJobs+1)+"),=average(T2:T"+(numJobs+1)+"),=average(U2:U"+(numJobs+1)+"),=average(V2:V"+(numJobs+1)+"),=average(W2:W"+(numJobs+1)+"),=average(X2:X"+(numJobs+1)+"),=average(Y2:Y"+(numJobs+1)+"),=average(Z2:Z"+(numJobs+1)+")");

			Main.pwTest.close();
			Main.pwAll.close();
			Main.fwTest.close();
			Main.fwAll.close();
			Main.pConfusionMatrix.close();
			
			Main.pwParetoTest.close();
			Main.pwParetoAll.close();
			Main.fwParetoTest.close();
			Main.fwParetoAll.close();
			Main.pConfusionMatrixPareto.close();
			
		}

		Main.fwTest = new FileWriter(Main.path+"/resultadoFinal.csv");
		Main.fwAll = new FileWriter(Main.path+"/resultadoAllFinal.csv");

		Main.pwTest = new PrintWriter(Main.fwTest);
		Main.pwAll = new PrintWriter(Main.fwAll);


		Main.pwTest.println("Accuracy,F-Measure,Precision,Recall,Total Nodes,Total Leaves,Balance, Gmean, AvgClassAccuracy");
		Main.pwAll.println("Train Accuracy,Validation Accuracy,Test Accuracy,Train F-Measure,Validation F-Measure,Test F-Measure,Train Precision,Validation Precision,Test Precision,Train Recall,Validation Recall, Test Recall,Total Nodes,Total Leaves,Train Balance,Validation Balance,Test Balance,Train AUC,Validation AUC,Test AUC,Train Gmean,Validation Gmean,Test Gmean,Train AvgClassAccuracy, Validation AvgClassAccuracy, Test AvgClassAccuracy");

		double accuracy[][] = new double[3][numJobs];  // [0] = treino; [1] = validacao; [2] = teste  
		double fmeasure[][] = new double[3][numJobs];
		double precision[][] = new double[3][numJobs];
		double recall[][] = new double[3][numJobs];
		double balance[][] = new double[3][numJobs];
		double auc[][] = new double[3][numJobs];
		double gmean[][] = new double[3][numJobs];
		double avgClassAcc[][] = new double[3][numJobs];

		double numNodes[] = new double[numJobs];
		double numLeaves[] = new double[numJobs];

		for(int j=0; j<numJobs;j++){
			accuracy[0][j] = Utils.mean(Main.measuresSingle[0][0][j]);
			accuracy[1][j] = Utils.mean(Main.measuresSingle[1][0][j]);
			accuracy[2][j] = Utils.mean(Main.measuresSingle[2][0][j]);
			fmeasure[0][j] = Utils.mean(Main.measuresSingle[0][1][j]);
			fmeasure[1][j] = Utils.mean(Main.measuresSingle[1][1][j]);
			fmeasure[2][j] = Utils.mean(Main.measuresSingle[2][1][j]);
			precision[0][j] = Utils.mean(Main.measuresSingle[0][2][j]);
			precision[1][j] = Utils.mean(Main.measuresSingle[1][2][j]);
			precision[2][j] = Utils.mean(Main.measuresSingle[2][2][j]);
			recall[0][j] = Utils.mean(Main.measuresSingle[0][3][j]);
			recall[1][j] = Utils.mean(Main.measuresSingle[1][3][j]);
			recall[2][j] = Utils.mean(Main.measuresSingle[2][3][j]);
			numNodes[j] = Utils.mean(Main.measuresSingle[0][4][j]);
			numLeaves[j] = Utils.mean(Main.measuresSingle[0][5][j]);
			balance[0][j] = Utils.mean(Main.measuresSingle[0][6][j]);
			balance[1][j] = Utils.mean(Main.measuresSingle[1][6][j]);
			balance[2][j] = Utils.mean(Main.measuresSingle[2][6][j]);
			auc[0][j] = Utils.mean(Main.measuresSingle[0][7][j]);
			auc[1][j] = Utils.mean(Main.measuresSingle[1][7][j]);
			auc[2][j] = Utils.mean(Main.measuresSingle[2][7][j]);
			gmean[0][j] = Utils.mean(Main.measuresSingle[0][8][j]);
			gmean[1][j] = Utils.mean(Main.measuresSingle[1][8][j]);
			gmean[2][j] = Utils.mean(Main.measuresSingle[2][8][j]);
			avgClassAcc[0][j] = Utils.mean(Main.measuresSingle[0][9][j]);
			avgClassAcc[1][j] = Utils.mean(Main.measuresSingle[1][9][j]);
			avgClassAcc[2][j] = Utils.mean(Main.measuresSingle[2][9][j]);
		}

		Main.pwTest.println(Utils.mean(accuracy[2])+","
				+Utils.mean(fmeasure[2])+","
				+Utils.mean(precision[2])+","
				+Utils.mean(recall[2])+","
				+Utils.mean(numNodes)+","
				+Utils.mean(numLeaves)+","
				+Utils.mean(balance[2])+","
				+Utils.mean(auc[2])+","
				+Utils.mean(gmean[2])+","
				+Utils.mean(avgClassAcc[2])
				);

		Main.pwAll.println(Utils.mean(accuracy[0])+","+Utils.mean(accuracy[1])+","+Utils.mean(accuracy[2])+","
				+Utils.mean(fmeasure[0])+","+Utils.mean(fmeasure[1])+","+Utils.mean(fmeasure[2])+","
				+Utils.mean(precision[0])+","+Utils.mean(precision[1])+","+Utils.mean(precision[2])+","
				+Utils.mean(recall[0])+","+Utils.mean(recall[1])+","+Utils.mean(recall[2])+","
				+Utils.mean(numNodes)+","+Utils.mean(numLeaves)+","
				+Utils.mean(balance[0])+","+Utils.mean(balance[1])+","+Utils.mean(balance[2])+","
				+Utils.mean(auc[0])+","+Utils.mean(auc[1])+","+Utils.mean(auc[2])+","
				+Utils.mean(gmean[0])+","+Utils.mean(gmean[1])+","+Utils.mean(gmean[2])+","
				+Utils.mean(avgClassAcc[0])+","+Utils.mean(avgClassAcc[1])+","+Utils.mean(avgClassAcc[2])
				
				);

		Main.pwTest.close();
		Main.pwAll.close();
		Main.fwTest.close();
		Main.fwAll.close();
		System.exit(0);
	}



	public static void mainMultipleDatasets(String[] args) throws Exception
	{
		EvolutionState state;
		ParameterDatabase parameters;


		// if we're loading from checkpoint, let's finish out the most recent job
		state = possiblyRestoreFromCheckpoint(args);
		int currentJob = 0;                             // the next job number (0 by default)

		// this simple job iterator just uses the 'jobs' parameter, iterating from 0 to 'jobs' - 1
		// inclusive.  The current job number is stored in state.jobs[0], so we'll begin there if
		// we had loaded from checkpoint.

		if (state != null)  // loaded from checkpoint
		{
			// extract the next job number from state.job[0] (where in this example we'll stash it)
			try
			{
				if (state.runtimeArguments == null)
					Output.initialError("Checkpoint completed from job started by foreign program (probably GUI).  Exiting...");
				args = state.runtimeArguments;                          // restore runtime arguments from checkpoint
				currentJob = ((Integer)(state.job[0])).intValue() + 1;  // extract next job number
			}
			catch (Exception e)
			{
				Output.initialError("EvolutionState's jobs variable is not set up properly.  Exiting...");
			}

			state.run(EvolutionState.C_STARTED_FROM_CHECKPOINT);
			cleanup(state);
		}

		// A this point we've finished out any previously-checkpointed job.  If there was
		// one such job, we've updated the current job number (currentJob) to the next number.
		// Otherwise currentJob is 0.

		// Now we're going to load the parameter database to see if there are any more jobs.
		// We could have done this using the previous parameter database, but it's no big deal.
		parameters = loadParameterDatabase(args);
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

		int greaterSize = 0;
		if(Dataset.getMetaTest().size() > Dataset.getMetaTraining().size())
			greaterSize = Dataset.getMetaTest().size();
		else
			greaterSize = Dataset.getMetaTraining().size();


		//Main.measuresMultiple = new double[2][2][7][greaterSize][numJobs][Dataset.getNumFolds()];
		Main.measuresMultiple = new double[2][2][11][greaterSize][numJobs];
		// [2][2] = Meta-Traning=0 (train=0  and test=1), Meta-Test=1 (Train and test)
		// 11 measures = accuracy, fmeasure, precision, recall, numNodes, numLeaves, balance, AUC, TRP, TNP, and Gmean
		Main.fwTest = new FileWriter(Main.path+"/algorithms.txt");
		Main.pwTest = new PrintWriter(Main.fwTest);
		Main.fwNew = new FileWriter(Main.path+"/resultados-runs.csv");

		Main.fwAll = new FileWriter(Main.path+"/algorithms-all.txt");
		Main.pwAll = new PrintWriter(Main.fwAll);
		Main.pwNew = new PrintWriter(Main.fwNew);


		Dataset.splitTrainingTest();

		Main.pwNew.println("Dataset,Run,Accuracy,F-Measure,Precision,Recall,Total Nodes,Total Leaves,AUC,True Positive Rate,True Negative Rate,Gmean");
		for(int job = currentJob ; job < numJobs; job++){
			try
			{
				// load the parameter database (reusing the very first if it exists)
				if (parameters == null)
					parameters = loadParameterDatabase(args);

				// Initialize the EvolutionState, then set its job variables
				state = initialize(parameters, job);                // pass in job# as the seed increment
				state.output.systemMessage("Job: " + job);
				state.job = new Object[1];                                  // make the job argument storage
				state.job[0] = new Integer(job);                    // stick the current job in our job storage
				state.runtimeArguments = args;                              // stick the runtime arguments in our storage
				if (numJobs > 1)                                                    // only if iterating (so we can be backwards-compatible),
				{
					String jobFilePrefix = "job." + job + ".";
					state.output.setFilePrefix(jobFilePrefix);     // add a prefix for checkpoint/output files 
					state.checkpointPrefix = jobFilePrefix + state.checkpointPrefix;  // also set up checkpoint prefix
				}

				// Here you can set up the EvolutionState's parameters further before it's setup(...).
				// This includes replacing the random number generators, changing values in state.parameters,
				// changing instance variables (except for job and runtimeArguments, please), etc.

				state.parameters.set(new ec.util.Parameter("stat.file"), ""+Main.path+"out.stat");
				state.parameters.set(new ec.util.Parameter("stat.child.0.file"), ""+Main.path+"out2.stat");

				// now we let it go
				state.run(EvolutionState.C_STARTED_FRESH);
				//	state2 = state;
				cleanup(state);  // flush and close various streams, print out parameters if necessary
				parameters = null;  // so we load a fresh database next time around
			}
			catch (Throwable e)  // such as an out of memory error caused by this job
			{
				e.printStackTrace();
				state = null;
				System.gc();  // take a shot!
			}
		}

		Main.fwTest.close();
		Main.fwAll.close();
		Main.pwTest.close();
		Main.pwAll.close();

		Main.fwNew.close();
		Main.pwNew.close();

		Main.fwTest = new FileWriter(Main.path+"/resultadoFinal.csv");
		Main.pwTest = new PrintWriter(Main.fwTest);

		Main.fwAll = new FileWriter(Main.path+"/resultadoAllFinal.csv");
		Main.pwAll = new PrintWriter(Main.fwAll);


		Main.pwTest.println("Dataset,Accuracy,,F-Measure,,Precision,,Recall,,Total Nodes,,Total Leaves,,Balance,,AUC,,True Positive Rate,,True Negative Rate,, Gmean");

		Main.pwAll.println("Dataset,Train Accuracy,,Test Accuracy,,Train F-Measure,,Test F-Measure,,Train Precision,,Test Precision,,Train Recall,,Test Recall,,Total Nodes,,Total Leaves,,Train Balance,,Test Balance,, Train AUC,, Test AUC,, Train TPR,, Test TPR,, Train TNR,, Test TRN,, Train Gmean,, Test Gmean");

		int[] size = new int[2];
		size[0] = Dataset.getMetaTraining().size();
		size[1] = Dataset.getMetaTest().size();

		for (int s = 0; s < 2; s++) {
			double accuracy[][] = new double[2][size[s]];
			double fmeasure[][] = new double[2][size[s]];
			double precision[][] = new double[2][size[s]];
			double recall[][] =  new double[2][size[s]];
			double numNodes[] = new double[size[s]];
			double numLeaves[] = new double[size[s]];
			double balance[][] =  new double[2][size[s]];
			double auc[][] =  new double[2][size[s]];
			double tpr[][] =  new double[2][size[s]];
			double tnr[][] =  new double[2][size[s]];
			double gmean[][] =  new double[2][size[s]];

			for(int d=0;d<size[s];d++){
				//	for(int j=0; j<numJobs;j++){
				accuracy[0][d] = Utils.mean(Main.measuresMultiple[s][0][0][d]);
				accuracy[1][d] = Utils.mean(Main.measuresMultiple[s][1][0][d]);
				fmeasure[0][d] = Utils.mean(Main.measuresMultiple[s][0][1][d]);
				fmeasure[1][d] = Utils.mean(Main.measuresMultiple[s][1][1][d]);
				precision[0][d] = Utils.mean(Main.measuresMultiple[s][0][2][d]);
				precision[1][d] = Utils.mean(Main.measuresMultiple[s][1][2][d]);
				recall[0][d] = Utils.mean(Main.measuresMultiple[s][0][3][d]);
				recall[1][d] = Utils.mean(Main.measuresMultiple[s][1][3][d]);
				numNodes[d] = Utils.mean(Main.measuresMultiple[s][0][4][d]);
				numLeaves[d] = Utils.mean(Main.measuresMultiple[s][0][5][d]);
				balance[0][d] = Utils.mean(Main.measuresMultiple[s][0][6][d]);
				balance[1][d] = Utils.mean(Main.measuresMultiple[s][1][6][d]);
				auc[0][d] = Utils.mean(Main.measuresMultiple[s][0][7][d]);
				auc[1][d] = Utils.mean(Main.measuresMultiple[s][1][7][d]);
				tpr[0][d] = Utils.mean(Main.measuresMultiple[s][0][8][d]);
				tpr[1][d] = Utils.mean(Main.measuresMultiple[s][1][8][d]);
				tnr[0][d] = Utils.mean(Main.measuresMultiple[s][0][9][d]);
				tnr[1][d] = Utils.mean(Main.measuresMultiple[s][1][9][d]);
				gmean[0][d] = Utils.mean(Main.measuresMultiple[s][0][10][d]);
				gmean[1][d] = Utils.mean(Main.measuresMultiple[s][1][10][d]);

				//	}
			}

			int nMedidas = 11;
			double[][] desvios;
			double quadrados[][] = new double[2][nMedidas];
			double soma[][] = new double[2][nMedidas];

			for(int d = 0;d < size[s]; d++){
				desvios = new double[2][nMedidas];
				for (int medida = 0; medida < nMedidas; medida++) {
					quadrados[0][medida] = 0;
					quadrados[1][medida] = 0;
					soma[0][medida] = 0;
					soma[1][medida] = 0;
					for (int j = 0; j < numJobs; j++) {
						//		for (int fold = 0; fold < numFolds; fold++) {
						quadrados[0][medida] += Math.pow(Main.measuresMultiple[s][0][medida][d][j],2);
						quadrados[1][medida] += Math.pow(Main.measuresMultiple[s][1][medida][d][j],2);
						soma[0][medida] += Main.measuresMultiple[s][0][medida][d][j];
						soma[1][medida] += Main.measuresMultiple[s][1][medida][d][j];
						//	}
					}
					desvios[0][medida] = Math.sqrt((quadrados[0][medida]/(numJobs)) - Math.pow((soma[0][medida]/(numJobs)), 2));
					desvios[1][medida] = Math.sqrt((quadrados[1][medida]/(numJobs)) - Math.pow((soma[1][medida]/(numJobs)), 2));
				}

				String dataSetName = new String();
				if (s==0) { // Meta-treino
					dataSetName = Dataset.getMetaTraining().get(d).relationName();
				}
				else { // Meta-test 
					dataSetName = Dataset.getMetaTest().get(d).relationName();
					Main.pwTest.println(dataSetName+","
							+accuracy[1][d]+","+desvios[1][0]+","
							+fmeasure[1][d]+","+desvios[1][1]+","
							+precision[1][d]+","+desvios[1][2]+","
							+recall[1][d]+","+desvios[1][3]+","
							+numNodes[d]+","+desvios[1][4]+","
							+numLeaves[d]+","+desvios[1][5]+","
							+balance[1][d]+","+desvios[1][6]+","
							+auc[1][d]+","+desvios[1][7]+","
							+tpr[1][d]+","+desvios[1][8]+","
							+tnr[1][d]+","+desvios[1][9]+","
							+gmean[1][d]+","+desvios[1][10]							
							);
				}

				Main.pwAll.println(dataSetName+","
						+accuracy[0][d]+","+desvios[0][0]+","
						+accuracy[1][d]+","+desvios[1][0]+","
						+fmeasure[0][d]+","+desvios[0][1]+","
						+fmeasure[1][d]+","+desvios[1][1]+","
						+precision[0][d]+","+desvios[0][2]+","
						+precision[1][d]+","+desvios[1][2]+","
						+recall[0][d]+","+desvios[0][3]+","
						+recall[1][d]+","+desvios[1][3]+","
						+numNodes[d]+","+desvios[0][4]+","
						+numLeaves[d]+","+desvios[0][5]+","
						+balance[0][d]+","+desvios[0][6]+","
						+balance[1][d]+","+desvios[1][6]+","
						+auc[0][d]+","+desvios[0][7]+","
						+auc[1][d]+","+desvios[1][7]+","
						+tpr[0][d]+","+desvios[0][8]+","
						+tpr[1][d]+","+desvios[1][8]+","
						+tnr[0][d]+","+desvios[0][9]+","
						+tnr[1][d]+","+desvios[1][9]+","
						+gmean[0][d]+","+desvios[0][10]+","
						+gmean[1][d]+","+desvios[1][10]);

			}
		}

		Main.pwTest.close();
		Main.pwAll.close();
		Main.fwTest.close();
		Main.fwAll.close();

		System.exit(0);
	}

}

