package eda;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import Util.ClusWrapper;
import Util.ClusWrapperNonStatic;
import Util.myMeasures;
import au.com.bytecode.opencsv.CSVReader;
import weka.core.EuclideanDistance;
import weka.core.Utils;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleProblemForm;
import ec.vector.BitVectorIndividual;
import ec.vector.IntegerVectorIndividual;
import ga.Dataset;
import ga.Main;

public class MedoidEvolution extends Problem implements SimpleProblemForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum, ClusWrapperNonStatic object) {

		if (ind.evaluated) return;
		if (!(ind instanceof MyBitVectorIndividual))
			state.output.fatal("Whoa!  It's not a BitVectorIndividual!!!",null);

		MyBitVectorIndividual ind2 = (MyBitVectorIndividual)ind;
		//	ind2.printIndividualForHumans(state, 1);

		boolean isIdeal;
		
		try {
			int[] genome = ((IntegerVectorIndividual)ind).genome;
			/*
			System.out.print("Genome = ["+genome[0]);
			for (int i = 1; i < genome.length; i++)
				System.out.print(","+genome[i]);
			System.out.println("]");
			*/
			
			ClusWrapper.initialization(Dataset.getPath()+Dataset.getFileName() + "-train.arff", Dataset.getPath()+Dataset.getFileName() + "-train.arff", Main.targets,false);

			myMeasures measures = new myMeasures();
			measures = ClusWrapper.evaluateIndividual(genome,true);

			double mae = measures.getMAE()[1];
			double mse = measures.getMSE()[1];
			double rmse = measures.getRMSE()[1];
			//double wrmse = measures.getWRMSE()[1];
			
			if(Main.fitnessType == 0){ // MAE fitness
				if(Utils.eq(mae,0)) isIdeal = true;		
				else	isIdeal = false;
				//f.setStandardizedFitness(state, mae);
				((SimpleFitness)ind.fitness).setFitness(state, ((float) 10000 - mae), isIdeal);
			}
			else if (Main.fitnessType == 1){ //MSE fitness
				if(Utils.eq(mse,0)) isIdeal = true;		
				else	isIdeal = false;	
				((SimpleFitness)ind.fitness).setFitness(state, ((float) 10000 - mse), isIdeal);
			}

			else if (Main.fitnessType == 2) { //RMSE fitness
				if(Utils.eq(rmse,0)) isIdeal = true;		
				else	isIdeal = false;	
				((SimpleFitness)ind.fitness).setFitness(state, ((float) 10000 - rmse), isIdeal);
			}

			else {
				if (Utils.eq(mae,0)) isIdeal = true;		
				else	isIdeal = false;
				((SimpleFitness)ind.fitness).setFitness(state, ((float) 10000 - mae), isIdeal);
			}

			ind.evaluated = true;


		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ind2.evaluated = true;


	}



	public double simplifiedSilhouette(BitVectorIndividual ind2, int[] assignments, ArrayList<Cluster> partition){

		int numClusters = partition.size();

		if (numClusters <= 1) {
			return -1;

		}

		else {
			// Compute silhouette
			double objectSilhouette[] = new double[ind2.genome.length];

			// iterate among the objects to compute each object's silhouette
			for(int i=0; i<ind2.genome.length; i++){

				// beginning of a(i)'s computation - average distance between object i and its cluster members
				double ai = 0;
				int cluster = assignments[i]; // discover its cluster

				if(partition.get(cluster).getMembers().size() == 1)
					objectSilhouette[i] = 0;
				else{
					ai = EDAMain.similarityMatrix[i][partition.get(cluster).getMembers().get(0)]; //remember: medoid is the first element of the members list

					// end of a(i)'s computation

					// beginning of b(i)'s computation - average distance between object i and its nearest cluster
					double bi[] = new double[numClusters - 1]; // stores average distance between object i and all other clusters
					int otherClusters = 0;
					for(int k=0; k<numClusters;k++){
						if(k != assignments[i]){ // cannot compute b(i) for its own cluster!
							bi[otherClusters] = EDAMain.similarityMatrix[i][partition.get(k).getMembers().get(0)]; 
							otherClusters++;
						} 
					}


					double biFinal = bi[Utils.minIndex(bi)];

					objectSilhouette[i] = (biFinal - ai)/(Math.max(ai, biFinal));
				}
			}

			return Utils.mean(objectSilhouette);
		}
	}
	
	public double silhouette(BitVectorIndividual ind2, int[] assignments, ArrayList<Cluster> partition){
		int numClusters = partition.size();
		if (numClusters <= 1) {
			return -1;
		}
		else {
			// Compute silhouette
			double objectSilhouette[] = new double[ind2.genome.length];

			// iterate among the objects to compute each object's silhouette
			for(int i=0; i<ind2.genome.length; i++){

				// beginning of a(i)'s computation - average distance between object i and its cluster members
				double ai = 0;
				int cluster = assignments[i]; // discover its cluster

				if(partition.get(cluster).getMembers().size() == 1)
					objectSilhouette[i] = 0;
				else{
					ai = EDAMain.similarityMatrix[i][partition.get(cluster).getMembers().get(0)]; //remember: medoid is the first element of the members list
					// end of a(i)'s computation
					// beginning of b(i)'s computation - average distance between object i and its nearest cluster
					double bi[] = new double[numClusters - 1]; // stores average distance between object i and all other clusters
					int otherClusters = 0;
					for(int k=0; k<numClusters;k++){
						if(k != assignments[i]){ // cannot compute b(i) for its own cluster!
							bi[otherClusters] = EDAMain.similarityMatrix[i][partition.get(k).getMembers().get(0)]; 
							otherClusters++;
						} 
					}


					double biFinal = bi[Utils.minIndex(bi)];

					objectSilhouette[i] = (biFinal - ai)/(Math.max(ai, biFinal));
				}
			}

			return Utils.mean(objectSilhouette);
		}
	}



	public double DBI(BitVectorIndividual ind2, int[] assignments, ArrayList<Cluster> partition){

		double DBI = 0;

		int k = partition.size();
		double alpha[] = new double[k];
		double distances[][] = new double[k][k];



		//compute alphas
		for(int i=0; i<k; i++){
			double sum = 0;
			int sizeCluster = partition.get(i).getMembers().size();
			for(int j=0; j<sizeCluster; j++){
				if(j != 0){
					sum += EDAMain.similarityMatrix[partition.get(i).getMembers().get(0)][partition.get(i).getMembers().get(j)];
				}
			}
			if(sizeCluster > 1)
				alpha[i] = sum/(sizeCluster-1);
			else
				alpha[i] = 0;
		}

		//compute DB
		double maxValues[] = new double[k];
		for(int i=0;i<k;i++){
			double max[] = new double[k];
			for(int j=0;j<k;j++){
				distances[i][j] = EDAMain.similarityMatrix[partition.get(i).getMembers().get(0)][partition.get(j).getMembers().get(0)];
				if(distances[i][j] != 0)
					max[j] = (alpha[i] + alpha[j]) / distances[i][j];
				else
					max[j] = 0;
			}
			maxValues[i] = max[Utils.maxIndex(max)];
		}
		DBI = Utils.mean(maxValues);

		return DBI;

	}


	public void evaluateBestIndividual(MyBitVectorIndividual ind, EvolutionState state) throws Exception{
		int[] genome = ((MyBitVectorIndividual)ind).genome;
		// ajustar com parametros "globais"
		//ClusWrapper.initialization(Dataset.getPath()+Dataset.getFileName() + "-train.arff",Dataset.getPath()+Dataset.getFileName() + "-train.arff", Main.targets,false);
		ClusWrapper.initialization(Dataset.getPath()+Dataset.getFileName() + "-train.arff", Dataset.getPath()+Dataset.getFileName() + "-test.arff", Main.targets,false);
		myMeasures measures = ClusWrapper.evaluateIndividual(genome,true);
		double mae[] = new double[3];
		double mse[] = new double[3];
		double rmse[] = new double[3];
		//double wrmse[] = new double[3];

		mae[0] = measures.getMAE()[0]; mse[0] = measures.getMSE()[0]; rmse[0] = measures.getRMSE()[0]; // wrmse[0] = measures.getWRMSE()[0];
		mae[1] = measures.getMAE()[0]; mse[1] = measures.getMSE()[0]; rmse[1] = measures.getRMSE()[0]; // wrmse[1] = measures.getWRMSE()[0];
		mae[2] = measures.getMAE()[1]; mse[2] = measures.getMSE()[1]; rmse[2] = measures.getRMSE()[1]; // wrmse[2] = measures.getWRMSE()[1];
		
		// mae[1] = measures.getMAE()[1]; mse[1] = measures.getMSE()[1]; rmse[1] = measures.getRMSE()[1]; wrmse[1] = measures.getWRMSE()[1];
		// ClusWrapper.initialization(Dataset.getPath()+Dataset.getFileName() + "-train.arff", Dataset.getPath()+Dataset.getFileName() + "-test.arff", Main.targets,false);
		// measures = ClusWrapper.evaluateIndividual(genome,true);
		// mae[2] = measures.getMAE()[1]; mse[2] = measures.getMSE()[1]; rmse[2] = measures.getRMSE()[1]; wrmse[2] = measures.getWRMSE()[1];

		for (int i = 0; i < 3; i++) {
			Main.measuresSingle[i][0][Dataset.getCurrentFold()][(Integer)state.job[0]] = mae[i];
			Main.measuresSingle[i][1][Dataset.getCurrentFold()][(Integer)state.job[0]] = mse[i];
			Main.measuresSingle[i][2][Dataset.getCurrentFold()][(Integer)state.job[0]] = rmse[i];
			//Main.measuresSingle[i][3][Dataset.getCurrentFold()][(Integer)state.job[0]] = wrmse[i];	
		}

		// Printing results

		String test = new String();
		for (int i = 0; i < 3; i++)
			test = test + Main.measuresSingle[2][i][Dataset.getCurrentFold()][(Integer)state.job[0]]+",";
		Main.pwTest.println(test);

		String full = new String();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				full = full + Main.measuresSingle[j][i][Dataset.getCurrentFold()][(Integer)state.job[0]]+",";
			}
		}
		Main.pwAll.println(full);
	}


	public void describe(final EvolutionState state,final Individual ind,final int subpopulation,final int threadnum,final int log){
		MyBitVectorIndividual ind2 = (MyBitVectorIndividual)ind;
		try {
			evaluateBestIndividual(ind2,state);
			//if((Integer) state.job[0]  == (Main.numJobs - 1))
			//	finalResults();	
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Write down result in a txt file for further biological validation
	public void describe(final EvolutionState state,final Individual ind,final int subpopulation,final int threadnum,final int log){
		MyBitVectorIndividual ind2 = (MyBitVectorIndividual)ind;
		
		
		try {


			int assignments[] = new int[ind2.genome.length];
			int count = 0;

			// Create and initialize data structure for holding data partition (list of integer lists)
			ArrayList<Cluster> partition = new ArrayList<Cluster>();
			for(int i=0; i<ind2.genome.length; i++){
				if(ind2.genome[i]){
					Cluster cluster = new Cluster();
					cluster.setCentroid(EDAMain.dataset.instance(i));
					cluster.getMembers().add(i);
					partition.add(cluster);
					assignments[i] = count;
					count++;
				}

			}

			// Assign each object to each cluster

			for(int i=0; i< EDAMain.probabilities.length;i++){
				EDAMain.probabilities[i] = EDAMain.initialProb;
			}


			if((int)state.job[0] == (EDAMain.numJobs - 1)){ // last job
				EDAMain.fTime = System.currentTimeMillis();

				System.out.println("Execution time = "+String.valueOf((EDAMain.fTime - EDAMain.iTime)/1000));
				EDAMain.pOut.println("average,=average(B2:B"+(EDAMain.numJobs+1)+"),=average(C2:C"+(EDAMain.numJobs+1)+"),=average(D2:D"+(EDAMain.numJobs+1)+"),=average(E2:E"+(EDAMain.numJobs+1)+"),=average(F2:F"+(EDAMain.numJobs+1)+")");
				EDAMain.pOut.println("stdev,=stdev(B2:B"+(EDAMain.numJobs+1)+"),=stdev(C2:C"+(EDAMain.numJobs+1)+"),=stdev(D2:D"+(EDAMain.numJobs+1)+"),=stdev(E2:E"+(EDAMain.numJobs+1)+"),=stdev(F2:F"+(EDAMain.numJobs+1)+")");
				EDAMain.pOut.close();
				EDAMain.fOut.close();
			}
			
			String fileName = EDAMain.toPath+EDAMain.dataName+"exec"+(int)state.job[0]+".csv";
			FileWriter fExec = new FileWriter(fileName);
			PrintWriter pExec = new PrintWriter(fExec);
			
			for (int i = 0; i < assignments.length; i++) {
				pExec.println(assignments[i]+1);
			}
			fExec.close();
			pExec.close();
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}



}
