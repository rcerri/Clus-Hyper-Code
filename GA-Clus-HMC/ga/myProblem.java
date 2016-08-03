package ga;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import Util.ClusWrapper;
import Util.myMeasures;
import weka.core.Utils;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.vector.IntegerVectorIndividual;

public class myProblem extends Problem implements SimpleProblemForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void finalResults() throws IOException {

		int numJobs = Main.numJobs;

		Main.fwTest = new FileWriter(Main.path+"/resultadoFinal.csv");
		Main.fwAll = new FileWriter(Main.path+"/resultadoAllFinal.csv");

		Main.pwTest = new PrintWriter(Main.fwTest);
		Main.pwAll = new PrintWriter(Main.fwAll);

		Main.pwTest.println("MAE,MSE,RMSE");
		Main.pwAll.println("Train MAE,Validation MAE,Test MAE,Train MSE,Validation MSE,Test MSE,Train RMSE,Validation RMSE,Test RMSE");

		double accuracy[][] = new double[3][numJobs];  // [0] = train; [1] = validation; [2] = test set  
		double fmeasure[][] = new double[3][numJobs];
		double precision[][] = new double[3][numJobs];
		double recall[][] = new double[3][numJobs];
		double balance[][] = new double[3][numJobs];
		double auc[][] = new double[3][numJobs];

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
		}

		Main.pwTest.println(Utils.mean(accuracy[2])+","
				+Utils.mean(fmeasure[2])+","
				+Utils.mean(precision[2])+","
				+Utils.mean(recall[2])+","
				+Utils.mean(numNodes)+","
				+Utils.mean(numLeaves)+","
				+Utils.mean(balance[2])+
				+Utils.mean(auc[2]));

		Main.pwAll.println(Utils.mean(accuracy[0])+","+Utils.mean(accuracy[1])+","+Utils.mean(accuracy[2])+","
				+Utils.mean(fmeasure[0])+","+Utils.mean(fmeasure[1])+","+Utils.mean(fmeasure[2])+","
				+Utils.mean(precision[0])+","+Utils.mean(precision[1])+","+Utils.mean(precision[2])+","
				+Utils.mean(recall[0])+","+Utils.mean(recall[1])+","+Utils.mean(recall[2])+","
				+Utils.mean(numNodes)+","+Utils.mean(numLeaves)+","
				+Utils.mean(balance[0])+","+Utils.mean(balance[1])+","+Utils.mean(balance[2])+","
				+Utils.mean(auc[0])+","+Utils.mean(auc[1])+","+Utils.mean(auc[2])
				);

		Main.pwTest.close();
		Main.pwAll.close();
		Main.fwTest.close();
		Main.fwAll.close();

	}

	public void describe(final EvolutionState state,final Individual ind,final int subpopulation,final int threadnum,final int log){
		IntegerVectorIndividual ind2 = (IntegerVectorIndividual)ind;
		try {
			evaluateBestIndividual(ind2,state);
			//if((Integer) state.job[0]  == (Main.numJobs - 1))
			//	finalResults();	
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private int[] genomeAdjustment(int[] genome) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int newValue = 2;
		map.put(genome[0],1);
		genome[0] = 1;
		
		for (int i = 1; i < genome.length; i++) {
			if (map.containsKey(genome[i])) {
				genome[i] = map.get(genome[i]);
			}
			else {
				map.put(genome[i],newValue);
				genome[i] = newValue;
				newValue++;
			}			
		}
		return genome;
	}

	/**Method for evaluating individuals **/
	@Override
	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
		if (ind.evaluated) return;
		if (!(ind instanceof IntegerVectorIndividual))
			state.output.fatal("Whoa!  It's not an IntegerVectorIndividual!!!",null);
		boolean isIdeal;
		try {
			
			((IntegerVectorIndividual)ind).genome = genomeAdjustment(((IntegerVectorIndividual)ind).genome);			
			int[] genome = ((IntegerVectorIndividual)ind).genome;

			/*
			System.out.print("Genome = ["+genome[0]);
			for (int i = 1; i < genome.length; i++)
				System.out.print(","+genome[i]);
			System.out.println("]");
			 */

			//int currentFold = Dataset.getCurrentFold();
			//String trainSet = Dataset.getPath()+Dataset.getFileName() + "_train_fold_"+(currentFold+1)+".arff";
			//String testSet = Dataset.getPath()+Dataset.getFileName() + "_test_fold_"+(currentFold+1)+".arff";
			//ClusWrapper.initialization(trainSet,trainSet, Main.targets,Main.randomForest);

			//System.out.println("train set = "+trainSet);

			myMeasures measures = new myMeasures();

			if (Main.mlTask == 1) { // classification

				System.out.print("Genome = ["+genome[0]);
				for (int i = 1; i < genome.length; i++)
					System.out.print(","+genome[i]);
				System.out.println("]");
				
				measures = ClusWrapper.evaluateIndividualClassification(genome,true);

				double auroc = measures.getAUROC()[1];
				double auprc = measures.getAUPRC()[1];
				
				if(Main.fitnessType == 0){ // Accuracy fitness
					if(Utils.eq(auroc,1)) isIdeal = true;		
					else	isIdeal = false;
					((SimpleFitness)ind.fitness).setFitness(state, (float) auroc, isIdeal);
				}
				else if (Main.fitnessType == 1){ //F1 fitness
					if(Utils.eq(auprc,1)) isIdeal = true;		
					else	isIdeal = false;	
					((SimpleFitness)ind.fitness).setFitness(state, (float) auprc, isIdeal);
				}

				else {
					if (Utils.eq(auroc,1)) isIdeal = true;		
					else	isIdeal = false;
					((SimpleFitness)ind.fitness).setFitness(state, (float) auroc, isIdeal);
				}

				ind.evaluated = true;

				//int currentGen = (int)state.generation;
				//System.out.println("current generation = "+currentGen);
				//System.out.println("number of generations = "+state.numGenerations);
				/* disable for allowing multi-thread
				if( (currentGen == 0) || (currentGen == (state.numGenerations - 1)))  { // first job

					ClusWrapper.initialization(trainSet, testSet, Main.targets,Main.randomForest);
					measures = new myMeasures();
					measures = ClusWrapper.evaluateIndividualClassification(genome,true);

					if (currentGen == 0)
						Main.pFirstGen.println(measures.getAccuracy()[0] +","+ measures.getF1()[0] +"," +measures.getWMSEnominal()[0] +","+ measures.getAccuracy()[1] +","+ measures.getF1()[1] +"," +measures.getWMSEnominal()[1]);
					else {
						Main.pLastGen.println(measures.getAccuracy()[0] +","+ measures.getF1()[0] +"," +measures.getWMSEnominal()[0] +","+ measures.getAccuracy()[1] +","+ measures.getF1()[1] +"," +measures.getWMSEnominal()[1]);
					}
				}
				*/

			}

			else { // regression
				
				//System.out.println("Main.targes = " + Main.targets);
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

				int currentGen = (int)state.generation;
				//System.out.println("current generation = "+currentGen);
				//System.out.println("number of generations = "+state.numGenerations);
				// disable for allowing multi-thread
				if( (currentGen == 0) || (currentGen == (state.numGenerations - 1)))  { // first job
					//ClusWrapper.initialization(trainSet, testSet, Main.targets,Main.randomForest);
					//measures = new myMeasures();
					//measures = ClusWrapper.evaluateIndividual(genome,true);

					if (currentGen == 0)
						Main.pFirstGen.println(measures.getMAE()[0] +","+ measures.getMSE()[0] +"," +measures.getRMSE()[0] +","+ measures.getMAE()[1] +","+ measures.getMSE()[1] +"," +measures.getRMSE()[1]);
					else {
						Main.pLastGen.println(measures.getMAE()[0] +","+ measures.getMSE()[0] +"," +measures.getRMSE()[0] +","+ measures.getMAE()[1] +","+ measures.getMSE()[1] +"," +measures.getRMSE()[1]);
					}
				}	
				//
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}


	public void printConfusionMatrix(PrintWriter pt, double[][] confusionMatrix) {
		int nClasses = confusionMatrix[0].length;

		for (int i = 0; i < nClasses; i++) {
			pt.print(","+i);
		}
		pt.println();
		for (int i = 0; i < nClasses; i++) {
			pt.print(i);
			for (int j = 0; j <nClasses; j++) {
				pt.print(","+confusionMatrix[i][j]);
			}
			pt.println();
		}
	}


	public void evaluateBestIndividual(IntegerVectorIndividual ind, EvolutionState state) throws Exception{
		int[] genome = ((IntegerVectorIndividual)ind).genome;
		// ajustar com parametros "globais"
		//ClusWrapper.initialization(Dataset.getPath()+Dataset.getFileName() + "-train.arff",Dataset.getPath()+Dataset.getFileName() + "-train.arff", Main.targets,false);

		int currentFold = Dataset.getCurrentFold();
		
		String trainSet = Dataset.getPath()+Dataset.getFileName() + "_fold"+(currentFold)+".train";
		String trainValidSet = Dataset.getPath()+Dataset.getFileName() + "_fold"+(currentFold)+".trainvalid";
		String valSet = Dataset.getPath()+Dataset.getFileName() + "_fold"+(currentFold)+".valid";
		String testSet = Dataset.getPath()+Dataset.getFileName() + "_fold"+(currentFold)+".test";
		
		
		//String trainSet = Dataset.getPath()+Dataset.getFileName() + "_train_fold_"+(currentFold+1)+".arff";
		//String testSet = Dataset.getPath()+Dataset.getFileName() + "_test_fold_"+(currentFold+1)+".arff";

		
		//ClusWrapper.initialization(Dataset.getPath()+Dataset.getFileName() + "-train.arff", Dataset.getPath()+Dataset.getFileName() + "-test.arff", Main.targets,Main.randomForest);
		myMeasures measures = new myMeasures();

		if (Main.mlTask == 1) {
			
			double auroc[] = new double[3];
			double auprc[] = new double[3];
			
			// true = classification or false = regression
			ClusWrapper.initialization(trainSet,valSet, Main.targets,Main.randomForest,true);
			measures = ClusWrapper.evaluateIndividualClassification(genome,true);
			auroc[1] = measures.getAUROC()[1]; auprc[1] = measures.getAUPRC()[1];;
			
			ClusWrapper.initialization(trainValidSet,testSet, Main.targets,Main.randomForest,true);
			measures = ClusWrapper.evaluateIndividualClassification(genome,true);
			
			auroc[0] = measures.getAUROC()[0]; auprc[0] = measures.getAUPRC()[0];			
			auroc[2] = measures.getAUROC()[1]; auprc[2] = measures.getAUPRC()[1];;

			for (int i = 0; i < 3; i++) {
				Main.measuresSingle[i][0][Dataset.getCurrentFold()][(Integer)state.job[0]] = auroc[i];
				Main.measuresSingle[i][1][Dataset.getCurrentFold()][(Integer)state.job[0]] = auprc[i];	
			}
			
			// Printing results
			String test = new String();
			for (int i = 0; i < 2; i++)
				test = test + Main.measuresSingle[2][i][Dataset.getCurrentFold()][(Integer)state.job[0]]+",";
			Main.pwTest.println(test);

			String full = new String();
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 3; j++) {
					full = full + Main.measuresSingle[j][i][Dataset.getCurrentFold()][(Integer)state.job[0]]+",";
				}
			}
			Main.pwAll.println(full);
		}

		else {
			double mae[] = new double[3];
			double mse[] = new double[3];
			double rmse[] = new double[3];
			
			// true = classification or false = regression
			ClusWrapper.initialization(trainSet,valSet, Main.targets,Main.randomForest,false);
			
			measures = ClusWrapper.evaluateIndividual(genome,true);
			mae[1] = measures.getMAE()[1]; mse[1] = measures.getMSE()[1]; rmse[1] = measures.getRMSE()[1]; // wrmse[1] = measures.getWRMSE()[0];
			
			ClusWrapper.initialization(trainValidSet,testSet, Main.targets,Main.randomForest,false);
			
			measures = ClusWrapper.evaluateIndividual(genome,true);
			mae[0] = measures.getMAE()[0]; mse[0] = measures.getMSE()[0]; rmse[0] = measures.getRMSE()[0]; // wrmse[0] = measures.getWRMSE()[0];
			mae[2] = measures.getMAE()[1]; mse[2] = measures.getMSE()[1]; rmse[2] = measures.getRMSE()[1]; // wrmse[2] = measures.getWRMSE()[1];

			for (int i = 0; i < 3; i++) {
				Main.measuresSingle[i][0][Dataset.getCurrentFold()][(Integer)state.job[0]] = mae[i];
				Main.measuresSingle[i][1][Dataset.getCurrentFold()][(Integer)state.job[0]] = mse[i];
				Main.measuresSingle[i][2][Dataset.getCurrentFold()][(Integer)state.job[0]] = rmse[i];
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


		//Main.pEvolution.println(mae[0] +","+ mse[0] +","+ rmse[0] +","+ mae[1] +","+ mse[1] +","+ rmse[1]);

	}

}
