package headDt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import headDt.split.Measure;
import tools.MetaLearning;
import headDt.topDown.EvolvedAlgorithm;
import headDt.topDown.SplitParameters;
import headDt.topDown.TreeParameters;
import weka.classifiers.rules.ZeroR;
import weka.core.Instances;
import weka.core.Utils;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.vector.IntegerVectorIndividual;

public class MOGenerateTrees extends Problem implements SimpleProblemForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* describe original do HEAD-DT
	public void describe(final EvolutionState state,final Individual ind,final int subpopulation,final int threadnum,final int log){
		IntegerVectorIndividual ind2 = (IntegerVectorIndividual)ind;
		try {
			System.out.println("DESCRIE!!");
			evaluateBestIndividual(ind2,state);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	 */

	public void describeParetoFront(final EvolutionState state, ArrayList<Individual> front, final int subpopulation, final int threadnum, final int log){
		try {
			//System.out.println("DESCRIBE PARETO!!");
			evaluateBestParetoFront(front,state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void describe(final EvolutionState state, final Individual ind, final int subpopulation, final int threadnum, final int log){
		IntegerVectorIndividual ind2 = (IntegerVectorIndividual)ind;
		try {
			evaluateBestIndividual(ind2,state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**Method for evaluating individuals **/
	@Override
	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {

		if (ind.evaluated) return;
		if (!(ind instanceof IntegerVectorIndividual))
			state.output.fatal("Whoa!  It's not an IntegerVectorIndividual!!!",null);


		IntegerVectorIndividual ind2 = (IntegerVectorIndividual)ind;
		//	ind2.printIndividualForHumans(state, 1);


		boolean isIdeal;
		EvolvedAlgorithm algorithm;
		try {

			algorithm = decodeIndividual(ind2);  //decode integer string into an executable decision-tree induction algorithm

			// if there is a single dataset in the meta-sets
			if(!Dataset.isMultiple()){
				algorithm.buildClassifier(Dataset.getTrainingData());
				myEvaluation evalValidation = new myEvaluation(Dataset.getFullData());
				evalValidation.evaluateModel(algorithm, Dataset.getValidationData());

				myEvaluation evalTrain = new myEvaluation(Dataset.getFullData());
				evalTrain.evaluateModel(algorithm, Dataset.getTrainingData());

				/*
				// calcular fmeasure considerando a classe menos frequente (pior caso) (aplicação nos datasets de eng. de software)
				int positiveIndex = Utils.minIndex(Dataset.getFullData().attributeStats(Dataset.getFullData().classIndex()).nominalCounts);				
				double balanceTrain = 1 - Math.sqrt((Math.pow(1 - evalTrain.truePositiveRate(positiveIndex),2) + Math.pow(0 - evalTrain.falsePositiveRate(positiveIndex),2))/Math.sqrt(2)); // only works for 2-class problems
				double balanceValidation = 1 - Math.sqrt((Math.pow(1 - evalValidation.truePositiveRate(positiveIndex),2) + Math.pow(0 - evalValidation.falsePositiveRate(positiveIndex),2))/Math.sqrt(2)); // only works for 2-class problems
				double gmeanTrain = Math.sqrt(evalTrain.truePositiveRate(positiveIndex) * evalTrain.trueNegativeRate(positiveIndex));
				double gmeanValidation = Math.sqrt(evalValidation.truePositiveRate(positiveIndex) * evalValidation.trueNegativeRate(positiveIndex));
				 */


				double balanceTrain = 1 - Math.sqrt((Math.pow(1 - evalTrain.averageTruePositiveRate(),2) + Math.pow(0 - evalTrain.averageFalsePositiveRate(),2))/Math.sqrt(2)); // only works for 2-class problems
				double balanceValidation = 1 - Math.sqrt((Math.pow(1 - evalValidation.averageTruePositiveRate(),2) + Math.pow(0 - evalValidation.averageFalsePositiveRate(),2))/Math.sqrt(2)); // only works for 2-class problems

				double gmeanTrain = Math.sqrt(evalTrain.averageTruePositiveRate() * evalTrain.averageTrueNegativeRate());
				double gmeanValidation = Math.sqrt(evalValidation.averageTruePositiveRate() * evalValidation.averageTrueNegativeRate());

				myMeasures measures = new myMeasures();

				measures.accuracy[0] = 1 - evalTrain.errorRate();
				measures.accuracy[1] = 1 - evalValidation.errorRate();

				/*
				measures.recall[0] = evalTrain.recall(positiveIndex);
				measures.recall[1] = evalValidation.recall(positiveIndex);
				measures.precision[0] = evalTrain.precision(positiveIndex);
				measures.precision[1] = evalValidation.precision(positiveIndex);
				measures.fMeasure[0] = evalTrain.fMeasure(positiveIndex,Main.beta);
				measures.fMeasure[1] = evalValidation.fMeasure(positiveIndex,Main.beta);
				 */

				measures.recall[0] = evalTrain.averageRecall();
				measures.recall[1] = evalValidation.averageRecall();
				measures.precision[0] = evalTrain.averagePrecision();
				measures.precision[1] = evalValidation.averagePrecision();
				measures.fMeasure[0] = evalTrain.averageFMeasure();
				measures.fMeasure[1] = evalValidation.averageFMeasure();


				measures.balance[0] = balanceTrain;
				measures.balance[1] = balanceValidation;
				measures.gmean[0] = gmeanTrain;
				measures.gmean[1] = gmeanValidation;

				/*
				measures.auc[0] = evalTrain.areaUnderROC(positiveIndex);
				measures.auc[1] = evalValidation.areaUnderROC(positiveIndex);
				measures.avgClassAcc[0] = (evalTrain.truePositiveRate(positiveIndex) + evalTrain.trueNegativeRate(positiveIndex))/2;
				measures.avgClassAcc[1] = (evalValidation.truePositiveRate(positiveIndex) + evalValidation.trueNegativeRate(positiveIndex))/2;
				 */

				measures.auc[0] = evalTrain.averageAreaUnderROC();
				measures.auc[1] = evalValidation.averageAreaUnderROC();

				measures.avgClassAcc[0] = 0;
				measures.avgClassAcc[1] = 0;
				for (int i = 0; i < Dataset.getFullData().numClasses(); i++) {
					measures.avgClassAcc[0] += (evalTrain.truePositiveRate(i) + evalTrain.trueNegativeRate(i));
					measures.avgClassAcc[1] = (evalValidation.truePositiveRate(i) + evalValidation.trueNegativeRate(i));
				}
				measures.avgClassAcc[0] /= Dataset.getFullData().numClasses();
				measures.avgClassAcc[1] /= Dataset.getFullData().numClasses();

				//System.out.println("MEasure.FMeasure = "+measures.fMeasure[0]);
				measures.setTreeSize(algorithm.getRoot().numNodes());
				measures.setWins(0);
				measures.setLoses(0);
				measures.setFtb(0);

				if (Main.multiObjectiveType == 2) // Pareto
					((myMultiObjectiveFitness)ind.fitness).measures = measures;
				else if (Main.multiObjectiveType == 3) { // Lexico 
					//ind.measures = measures;
					((myLexicoFitness)ind.fitness).measures = measures;
					//System.out.println("Lexico fitness");
				}

				ind.evaluated = true;

				/*

				if(Main.fitnessType == 0){ //accuracy fitness
					if(Utils.eq(accFitness,1)) isIdeal = true;		
					else	isIdeal = false;
					((SimpleFitness)ind2.fitness).setFitness(state, ((float)accFitness), isIdeal);
				}
				else if (Main.fitnessType == 1){ //f-measure fitness
					if(Utils.eq(fmeasureFitness,1)) isIdeal = true;		
					else	isIdeal = false;	
					((SimpleFitness)ind2.fitness).setFitness(state, ((float)fmeasureFitness), isIdeal);
				}

				else if (Main.fitnessType == 3) { //AUC fitness
					if(Utils.eq(aucFitness,1)) isIdeal = true;		
					else	isIdeal = false;	
					((SimpleFitness)ind2.fitness).setFitness(state, ((float)aucFitness), isIdeal);
				}

				else if (Main.fitnessType == 5) { //balance fitness
					if(Utils.eq(balanceFitness,1)) isIdeal = true;		
					else	isIdeal = false;	
					((SimpleFitness)ind2.fitness).setFitness(state, ((float)balanceFitness), isIdeal);
				}
				 */


			}//end if there is a single dataset


			// if there are multiple datasets in the meta-training set
			else{


				// USAR TREINO E VALIDA��O JUNTOS!

				double[] fitness;
				if(Main.metaTrainingEvaluationType == 0)
					fitness = evaluateAllSetsPerGeneration(algorithm,state,false,5); //use all meta-training in each generation
				else
					fitness = evaluateXSetsPerGeneration(algorithm,state,false,5); //use X sets per generation, in a circular way


				double aggregatedFitness = 0;
				int size = fitness.length;
				switch(Main.fitnessAggregationScheme){

				case 0: // simple average
					aggregatedFitness = Utils.mean(fitness);
					break;

				case 1: // median
					Arrays.sort(fitness);
					int indice = 0;
					indice = size/2;  // indices come�am com zero ent�o para �mpares ser� exatamente o elemento central e para pares ser� o segundo elemento central.
					aggregatedFitness = fitness[indice];
					break;


				case 2: // harmonic mean
					double sum = 0;
					boolean isNegative = false;
					for(int i=0;i<fitness.length;i++){
						if(fitness[i] <=0){
							isNegative = true;
							break;
						}
						sum += (1/fitness[i]);
					}
					if(isNegative)
						aggregatedFitness = 0.0;
					else
						aggregatedFitness = (size/sum);
					break;

				default: // simple average
					aggregatedFitness = Utils.mean(fitness);
					System.out.println("Entrou no default do fitness aggregation scheme e nao deveria!");
					break;

				}


				//		if(Utils.gr(aggregatedFitness, 1)) isIdeal = true;  //never gonna happen
				//		else isIdeal = false;
				isIdeal = false;
				((SimpleFitness)ind2.fitness).setFitness(state, ((float)aggregatedFitness), isIdeal);

				if (!(ind2.fitness instanceof SimpleFitness))
					state.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);

				ind2.evaluated = true;

			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}


	/** Method for evaluating the DT algorithm in all sets that belong to the meta-training set (per generation). Warning: costly!
	 * 
	 * @param algorithm
	 * @param state
	 * @param crossValidate
	 * @param folds
	 * @return
	 * @throws Exception
	 */
	public double[] evaluateAllSetsPerGeneration(EvolvedAlgorithm algorithm, EvolutionState state, boolean crossValidate, int folds) throws Exception{
		int size = Dataset.getMetaTraining().size();
		double fitness[] = new double[size];

		// generate tree for each data set and store fitness in a vector
		for(int i=0;i<size;i++){
			fitness[i] = evaluateData(i,algorithm,state,crossValidate,folds);
			//	storeMetaData(algorithm,fitness[i],i);	
		}
		return fitness;
	}

	/** Method for evaluating a maximum of X data sets per generation - sampling with no replacement 
	 * 
	 * @param algorithm
	 * @param state
	 * @param crossValidate
	 * @param folds
	 * @return
	 * @throws Exception
	 */
	public double[] evaluateXSetsPerGeneration(EvolvedAlgorithm algorithm, EvolutionState state, boolean crossValidate, int folds) throws Exception{
		//	System.out.println("entrou onde deveria!");
		double fitness[] = new double[Dataset.numSetsPerGeneration];  //for now 5 sets per Generation (this can be changed in class Dataset)

		for(int i=0; i< Dataset.numSetsPerGeneration; i++){

			//select data set in an offset vector (see class "Dataset", method "divideMetaTrainingPerGeneration" for more information)
			//	int index = Dataset.dataIndices.get((state.generation + i));
			int index = state.generation + i;

			//prepare to evaluate selected data set
			//		Instances data = Dataset.getMetaTraining().get(index);

			fitness[i] = evaluateData(index,algorithm,state,crossValidate,folds);
			if(index >= Dataset.getMetaTraining().size()){
				index = index - Dataset.getMetaTraining().size();
			}
			storeMetaData(algorithm,fitness[i],index);

		}
		return fitness;
	}

	/** Method for evaluating the DT generated by "algorithm" in data set "data". User may choose to cross-validate in "folds".
	 * 
	 * @param data
	 * @param algorithm
	 * @param state
	 * @param crossValidate
	 * @return
	 * @throws Exception
	 */
	public double evaluateData(int data, EvolvedAlgorithm algorithm, EvolutionState state, boolean crossValidate, int folds) throws Exception{
		double treeAccuracy;
		double majorityAccuracy;
		double fitness;

		int positiveIndex = Utils.minIndex(Dataset.getFullData().attributeStats(Dataset.getFullData().classIndex()).nominalCounts);

		//	Instances training=null,test=null;
		myEvaluation treeEvaluation = new myEvaluation(Dataset.trainingData.get(data));

		// evaluate with cross-validation (slower, but statistically safer) 
		if(crossValidate){
			Instances dataset = Dataset.getMetaTraining().get(Dataset.dataIndices.get(data));
			treeEvaluation.crossValidateModel(algorithm, dataset, folds, new Random(state.generation));
		}

		// evaluate without cross-validation (faster, but dangerous)
		else{
			algorithm.buildClassifier(Dataset.trainingData.get(data));
			treeEvaluation.evaluateModel(algorithm, Dataset.testData.get(data));
		}


		fitness = 0;
		switch(Main.fitnessType){

		case 0: // relative accuracy improvement
			myEvaluation majorityEvaluation = new myEvaluation(Dataset.getMetaTraining().get(data));
			//more costly to cross-validate the majority class
			if(crossValidate)
				majorityEvaluation.crossValidateModel(new ZeroR(), Dataset.getMetaTraining().get(data), folds, new Random(state.generation));
			//better perhaps to use the same training/test sets
			else{
				ZeroR majority = new ZeroR();
				majority.buildClassifier(Dataset.trainingData.get(data));
				majorityEvaluation.evaluateModel(majority, Dataset.testData.get(data));
			}

			// AQUI TEM ERRO!!
			treeAccuracy = (1 - treeEvaluation.errorRate());

			majorityAccuracy = (1 - majorityEvaluation.errorRate());
			if(treeAccuracy > majorityAccuracy)
				fitness = ((treeAccuracy - majorityAccuracy)/(1-majorityAccuracy));
			else
				fitness = ((treeAccuracy - majorityAccuracy)/majorityAccuracy);
			break;

		case 1: // accuracy
			fitness = (1 - treeEvaluation.errorRate());
			break;

		case 2: // F-Measure (ponderada por classe)
			fitness = treeEvaluation.fMeasure(positiveIndex,Main.beta);
			break;

		case 3: // AUC (ponderada por classe)
			fitness = treeEvaluation.areaUnderROC(positiveIndex);
			break;

		case 4: // mean true positive rate (recall)
			int numClasses = Dataset.trainingData.get(data).numClasses();
			fitness = treeEvaluation.truePositiveRate(positiveIndex);

			break;
		case 5: // BALANCE

			double tpr = treeEvaluation.truePositiveRate(positiveIndex);
			double fpr = treeEvaluation.falsePositiveRate(positiveIndex);
			double balance = 1 - Math.sqrt((Math.pow(1 - tpr,2) + Math.pow(0 - fpr,2))) / Math.sqrt(2); // only works for 2-class problems

			// A = TN (verdadeiro negativo), B = FP (falso negativo) , C = FP (falso positivo) , D = TP (verdadeiro positivo) 
			// pd = recall
			// pf = C/(A+C) = FP / (TN + FP)  = False Positive Rate
			// pd = recall = D / (B +D)    => True Positive Rate
			// balance = 1 - raiz (  ( (0-pf)^2  + (1-pd)^2) ) / raiz(2)     )

			// PF = FP/(FP+VN)
			// PD = VP/(VP+FN)


			// aparentemente o Balance est� correto!


			fitness = balance;
			break;

		default: // F-Measure (ponderada por classe)
			fitness = treeEvaluation.fMeasure(positiveIndex,Main.beta);
			System.out.println("Entrou no default do fitness por algum motivo estranho!");
			break;
		}

		return fitness;
	}



	public void evaluateBestIndividual(IntegerVectorIndividual ind, EvolutionState state) throws Exception{

		EvolvedAlgorithm algorithm = decodeIndividual(ind);
		myEvaluation evalTrain, evalTest, evalValidation;

		//System.out.println("TESTE BEST INDIVIDUAL");

		if(!Dataset.isMultiple()){
			double tpr = 0.0, fpr = 0.0, balanceTrain = 0.0, balanceValidation = 0.0, balanceTest = 0.0;

			//int positiveIndex = Utils.minIndex(Dataset.getFullData().attributeStats(Dataset.getFullData().classIndex()).nominalCounts);

			algorithm.buildClassifier(Dataset.getTrainingData());

			evalTrain = new myEvaluation(Dataset.getFullData());
			evalTrain.evaluateModel(algorithm, Dataset.getTrainingData());

			/*
			// really positive and negative classes
			Main.measuresSingle[0][0][(Integer)state.job[0]][Dataset.getFold()] = (1-evalTrain.errorRate());
			Main.measuresSingle[0][1][(Integer)state.job[0]][Dataset.getFold()] = evalTrain.fMeasure(positiveIndex,Main.beta);
			Main.measuresSingle[0][2][(Integer)state.job[0]][Dataset.getFold()] = evalTrain.precision(positiveIndex);
			Main.measuresSingle[0][3][(Integer)state.job[0]][Dataset.getFold()] = evalTrain.recall(positiveIndex);
			Main.measuresSingle[0][4][(Integer)state.job[0]][Dataset.getFold()] = algorithm.getRoot().numNodes();
			Main.measuresSingle[0][5][(Integer)state.job[0]][Dataset.getFold()] = algorithm.getRoot().numLeaves();
			tpr = evalTrain.truePositiveRate(positiveIndex);
			fpr = evalTrain.falsePositiveRate(positiveIndex);
			balanceTrain = 1 - Math.sqrt((Math.pow(1-tpr,2) + Math.pow(0 - fpr,2))) / Math.sqrt(2); 
			Main.measuresSingle[0][6][(Integer)state.job[0]][Dataset.getFold()] = balanceTrain;
			Main.measuresSingle[0][7][(Integer)state.job[0]][Dataset.getFold()] = evalTrain.areaUnderROC(positiveIndex);

			double gmeanTrain = Math.sqrt(evalTrain.truePositiveRate(positiveIndex) * evalTrain.trueNegativeRate(positiveIndex));
			Main.measuresSingle[0][8][(Integer)state.job[0]][Dataset.getFold()] = gmeanTrain;

			double avgClassAccTrain = (evalTrain.truePositiveRate(positiveIndex) + evalTrain.trueNegativeRate(positiveIndex)) / 2;
			Main.measuresSingle[0][9][(Integer)state.job[0]][Dataset.getFold()] = avgClassAccTrain;
			 */

			// generic (weighted f-measure, etc.)
			Main.measuresSingle[0][0][(Integer)state.job[0]][Dataset.getFold()] = (1-evalTrain.errorRate());
			Main.measuresSingle[0][1][(Integer)state.job[0]][Dataset.getFold()] = evalTrain.averageFMeasure();
			Main.measuresSingle[0][2][(Integer)state.job[0]][Dataset.getFold()] = evalTrain.averagePrecision();
			Main.measuresSingle[0][3][(Integer)state.job[0]][Dataset.getFold()] = evalTrain.averageRecall();
			Main.measuresSingle[0][4][(Integer)state.job[0]][Dataset.getFold()] = algorithm.getRoot().numNodes();
			Main.measuresSingle[0][5][(Integer)state.job[0]][Dataset.getFold()] = algorithm.getRoot().numLeaves();
			tpr = evalTrain.averageTruePositiveRate();
			fpr = evalTrain.averageFalsePositiveRate();
			balanceTrain = 1 - Math.sqrt((Math.pow(1-tpr,2) + Math.pow(0 - fpr,2))) /Math.sqrt(2); 
			Main.measuresSingle[0][6][(Integer)state.job[0]][Dataset.getFold()] = balanceTrain;
			Main.measuresSingle[0][7][(Integer)state.job[0]][Dataset.getFold()] = evalTrain.averageAreaUnderROC();

			double gmeanTrain = Math.sqrt(evalTrain.averageTruePositiveRate() * evalTrain.averageTrueNegativeRate());
			Main.measuresSingle[0][8][(Integer)state.job[0]][Dataset.getFold()] = gmeanTrain;



			double avgClassAccTrain = 0;
			for (int i = 0; i < Dataset.getFullData().numClasses(); i++) {
				avgClassAccTrain += (evalTrain.truePositiveRate(i) + evalTrain.trueNegativeRate(i))/2;
			}
			avgClassAccTrain /= Dataset.getFullData().numClasses();
			Main.measuresSingle[0][9][(Integer)state.job[0]][Dataset.getFold()] = avgClassAccTrain;



			evalValidation = new myEvaluation(Dataset.getFullData());
			evalValidation.evaluateModel(algorithm, Dataset.getValidationData());

			/*
			// positiveIndex
			Main.measuresSingle[1][0][(Integer)state.job[0]][Dataset.getFold()] = (1-evalValidation.errorRate());
			Main.measuresSingle[1][1][(Integer)state.job[0]][Dataset.getFold()] = evalValidation.fMeasure(positiveIndex,Main.beta);
			Main.measuresSingle[1][2][(Integer)state.job[0]][Dataset.getFold()] = evalValidation.precision(positiveIndex);
			Main.measuresSingle[1][3][(Integer)state.job[0]][Dataset.getFold()] = evalValidation.recall(positiveIndex);
			Main.measuresSingle[1][4][(Integer)state.job[0]][Dataset.getFold()] = algorithm.getRoot().numNodes();
			Main.measuresSingle[1][5][(Integer)state.job[0]][Dataset.getFold()] = algorithm.getRoot().numLeaves();
			tpr = evalValidation.truePositiveRate(positiveIndex);
			fpr = evalValidation.falsePositiveRate(positiveIndex);
			balanceValidation = 1 - Math.sqrt((Math.pow(1-tpr,2) + Math.pow(0 - fpr,2)))/Math.sqrt(2); 
			Main.measuresSingle[1][6][(Integer)state.job[0]][Dataset.getFold()] = balanceValidation; 
			Main.measuresSingle[1][7][(Integer)state.job[0]][Dataset.getFold()] = evalValidation.areaUnderROC(positiveIndex);
			double gmeanVal = Math.sqrt(evalValidation.truePositiveRate(positiveIndex) * evalValidation.trueNegativeRate(positiveIndex));
			Main.measuresSingle[1][8][(Integer)state.job[0]][Dataset.getFold()] = gmeanVal;

			double avgClassAccValidation = (evalValidation.truePositiveRate(positiveIndex) + evalValidation.trueNegativeRate(positiveIndex)) / 2;
			Main.measuresSingle[1][9][(Integer)state.job[0]][Dataset.getFold()] = avgClassAccValidation;

			 */

			//

			Main.measuresSingle[1][0][(Integer)state.job[0]][Dataset.getFold()] = (1-evalValidation.errorRate());
			Main.measuresSingle[1][1][(Integer)state.job[0]][Dataset.getFold()] = evalValidation.averageFMeasure();
			Main.measuresSingle[1][2][(Integer)state.job[0]][Dataset.getFold()] = evalValidation.averagePrecision();
			Main.measuresSingle[1][3][(Integer)state.job[0]][Dataset.getFold()] = evalValidation.averageRecall();
			Main.measuresSingle[1][4][(Integer)state.job[0]][Dataset.getFold()] = algorithm.getRoot().numNodes();
			Main.measuresSingle[1][5][(Integer)state.job[0]][Dataset.getFold()] = algorithm.getRoot().numLeaves();
			tpr = evalValidation.averageTruePositiveRate();
			fpr = evalValidation.averageFalsePositiveRate();
			balanceValidation = 1 - Math.sqrt((Math.pow(1-tpr,2) + Math.pow(0 - fpr,2)))/Math.sqrt(2); 
			Main.measuresSingle[1][6][(Integer)state.job[0]][Dataset.getFold()] = balanceValidation; 
			Main.measuresSingle[1][7][(Integer)state.job[0]][Dataset.getFold()] = evalValidation.averageAreaUnderROC();

			double gmeanVal = Math.sqrt(evalValidation.averageTruePositiveRate() * evalValidation.averageTrueNegativeRate());
			Main.measuresSingle[1][8][(Integer)state.job[0]][Dataset.getFold()] = gmeanVal;

			double avgClassAccValidation = 0;
			for (int i = 0; i < Dataset.getFullData().numClasses(); i++) {
				avgClassAccValidation += (evalValidation.truePositiveRate(i) + evalValidation.trueNegativeRate(i))/2;
			}
			avgClassAccValidation /= Dataset.getFullData().numClasses();
			Main.measuresSingle[1][9][(Integer)state.job[0]][Dataset.getFold()] = avgClassAccValidation;



			evalTest = new myEvaluation(Dataset.getFullData());
			evalTest.evaluateModel(algorithm, Dataset.getTestData());

			/* really 2-class problems
			Main.measuresSingle[2][0][(Integer)state.job[0]][Dataset.getFold()] = (1-evalTest.errorRate());
			Main.measuresSingle[2][1][(Integer)state.job[0]][Dataset.getFold()] = evalTest.fMeasure(positiveIndex,Main.beta);
			Main.measuresSingle[2][2][(Integer)state.job[0]][Dataset.getFold()] = evalTest.precision(positiveIndex);
			Main.measuresSingle[2][3][(Integer)state.job[0]][Dataset.getFold()] = evalTest.recall(positiveIndex);
			Main.measuresSingle[2][4][(Integer)state.job[0]][Dataset.getFold()] = algorithm.getRoot().numNodes();
			Main.measuresSingle[2][5][(Integer)state.job[0]][Dataset.getFold()] = algorithm.getRoot().numLeaves();
			tpr = evalTest.truePositiveRate(positiveIndex);
			fpr = evalTest.falsePositiveRate(positiveIndex);
			balanceTest = 1 - Math.sqrt((Math.pow(1-tpr,2) + Math.pow(0 - fpr,2)))/Math.sqrt(2); 
			Main.measuresSingle[2][6][(Integer)state.job[0]][Dataset.getFold()] = balanceTest;
			Main.measuresSingle[2][7][(Integer)state.job[0]][Dataset.getFold()] = evalTest.areaUnderROC(positiveIndex);

			double gmeanTest = Math.sqrt(evalTest.truePositiveRate(positiveIndex) * evalTest.trueNegativeRate(positiveIndex));
			Main.measuresSingle[2][8][(Integer)state.job[0]][Dataset.getFold()] = gmeanTest;

			double avgClassAccTest = (evalTest.truePositiveRate(positiveIndex) + evalTest.trueNegativeRate(positiveIndex)) / 2;
			Main.measuresSingle[2][9][(Integer)state.job[0]][Dataset.getFold()] = avgClassAccTest;
			 */


			// generic (weighted)
			Main.measuresSingle[2][0][(Integer)state.job[0]][Dataset.getFold()] = (1-evalTest.errorRate());
			Main.measuresSingle[2][1][(Integer)state.job[0]][Dataset.getFold()] = evalTest.averageFMeasure();
			Main.measuresSingle[2][2][(Integer)state.job[0]][Dataset.getFold()] = evalTest.averagePrecision();
			Main.measuresSingle[2][3][(Integer)state.job[0]][Dataset.getFold()] = evalTest.averageRecall();
			Main.measuresSingle[2][4][(Integer)state.job[0]][Dataset.getFold()] = algorithm.getRoot().numNodes();
			Main.measuresSingle[2][5][(Integer)state.job[0]][Dataset.getFold()] = algorithm.getRoot().numLeaves();
			tpr = evalTest.averageTruePositiveRate();
			fpr = evalTest.averageFalsePositiveRate();
			balanceTest = 1 - Math.sqrt((Math.pow(1-tpr,2) + Math.pow(0 - fpr,2)))/Math.sqrt(2); 
			Main.measuresSingle[2][6][(Integer)state.job[0]][Dataset.getFold()] = balanceTest;
			Main.measuresSingle[2][7][(Integer)state.job[0]][Dataset.getFold()] = evalTest.averageAreaUnderROC();

			double gmeanTest = Math.sqrt(evalTest.averageTruePositiveRate() * evalTest.averageTrueNegativeRate());
			Main.measuresSingle[2][8][(Integer)state.job[0]][Dataset.getFold()] = gmeanTest;

			double avgClassAccTest = 0;
			for (int i = 0; i < Dataset.getFullData().numClasses(); i++) {
				avgClassAccTest += (evalTest.truePositiveRate(i) + evalTest.trueNegativeRate(i))/2;
			}
			avgClassAccTest /= Dataset.getFullData().numClasses();
			Main.measuresSingle[2][9][(Integer)state.job[0]][Dataset.getFold()] = avgClassAccTest;

			/*
			Main.pwTest.println((1-evalTest.errorRate())+","
					+evalTest.fMeasure(positiveIndex,Main.beta)+","
					+evalTest.precision(positiveIndex)+","
					+evalTest.recall(positiveIndex)+","
					+algorithm.getRoot().numNodes()+","
					+algorithm.getRoot().numLeaves()+","
					+balanceTest+","
					+evalTest.areaUnderROC(positiveIndex)+","
					+gmeanTest+","
					+avgClassAccTest+","
					+writeIndividual(ind)+","
					+writeIndividual2(ind));


			Main.pwAll.println((1-evalTrain.errorRate())+","
					+(1-evalValidation.errorRate())+","
					+(1-evalTest.errorRate())+","

					+evalTrain.fMeasure(positiveIndex,Main.beta)+","
					+evalValidation.fMeasure(positiveIndex,Main.beta)+","
					+evalTest.fMeasure(positiveIndex,Main.beta)+","

					+evalTrain.precision(positiveIndex)+","
					+evalValidation.precision(positiveIndex)+","
					+evalTest.precision(positiveIndex)+","

					+evalTrain.recall(positiveIndex)+","
					+evalValidation.recall(positiveIndex)+","
					+evalTest.recall(positiveIndex)+","

					+algorithm.getRoot().numNodes()+","
					+algorithm.getRoot().numLeaves()+","

					+balanceTrain+","
					+balanceValidation+","
					+balanceTest+","

					+evalTrain.areaUnderROC(positiveIndex)+","
					+evalValidation.areaUnderROC(positiveIndex)+","
					+evalTest.areaUnderROC(positiveIndex)+","

					+gmeanTrain+","
					+gmeanVal+","
					+gmeanTest+","

					+avgClassAccTrain+","
					+avgClassAccValidation+","
					+avgClassAccTest+","

					+writeIndividual(ind)+","
					+writeIndividual2(ind)

					);
			// end positiveIndex
			 */


			// weighted
			Main.pwTest.println((1-evalTest.errorRate())+","
					+evalTest.averageFMeasure()+","
					+evalTest.averagePrecision()+","
					+evalTest.averageRecall()+","
					+algorithm.getRoot().numNodes()+","
					+algorithm.getRoot().numLeaves()+","
					+balanceTest+","
					+evalTest.averageAreaUnderROC()+","

					+gmeanTest+","
					+avgClassAccTest+","

					+writeIndividual(ind)+","
					+writeIndividual2(ind));


			Main.pwAll.println((1-evalTrain.errorRate())+","
					+(1-evalValidation.errorRate())+","
					+(1-evalTest.errorRate())+","

					+evalTrain.averageFMeasure()+","
					+evalValidation.averageFMeasure()+","
					+evalTest.averageFMeasure()+","

					+evalTrain.averagePrecision()+","
					+evalValidation.averagePrecision()+","
					+evalTest.averagePrecision()+","

					+evalTrain.averageRecall()+","
					+evalValidation.averageRecall()+","
					+evalTest.averageRecall()+","

					+algorithm.getRoot().numNodes()+","
					+algorithm.getRoot().numLeaves()+","

					+balanceTrain+","
					+balanceValidation+","
					+balanceTest+","

					+evalTrain.averageAreaUnderROC()+","
					+evalValidation.averageAreaUnderROC()+","
					+evalTest.averageAreaUnderROC()+","

					+gmeanTrain+","
					+gmeanVal+","
					+gmeanTest+","

					+avgClassAccTrain+","
					+avgClassAccValidation+","
					+avgClassAccTest+","

					+writeIndividual(ind)+","
					+writeIndividual2(ind));


			Main.pConfusionMatrix.println("Train");
			printConfusionMatrix(Main.pConfusionMatrix, evalTrain.confusionMatrix());
			Main.pConfusionMatrix.println();
			Main.pConfusionMatrix.println("Validation");
			printConfusionMatrix(Main.pConfusionMatrix, evalValidation.confusionMatrix());
			Main.pConfusionMatrix.println();
			Main.pConfusionMatrix.println("Test");
			printConfusionMatrix(Main.pConfusionMatrix, evalTest.confusionMatrix());
			Main.pConfusionMatrix.println();

		}


		// if there are multiple datasets in the meta-test set
		else{

			int positiveIndex = Utils.minIndex(Dataset.getFullData().attributeStats(Dataset.getFullData().classIndex()).nominalCounts);

			double tpr = 0.0, fpr = 0.0, balance = 0.0;

			Main.pwTest.println("Execution "+(Integer)state.job[0]+","+writeIndividual(ind));
			Main.pwAll.println("Execution "+(Integer)state.job[0]+","+writeIndividual(ind));

			int sizeTraining = Dataset.getMetaTraining().size(); // datasets do conjunto metaTreino		
			for(int i=0;i<sizeTraining;i++){
				Instances fulldataMetaTraining = Dataset.getMetaTraining().get(i);
				myEvaluation eval = new myEvaluation(fulldataMetaTraining);

				//for(int f=0;f<10;f++){
				algorithm.buildClassifier(fulldataMetaTraining);
				eval.evaluateModel(algorithm, fulldataMetaTraining);

				tpr = eval.truePositiveRate(positiveIndex);
				fpr = eval.falsePositiveRate(positiveIndex);
				balance = 1 - Math.sqrt((Math.pow(1-tpr,2) + Math.pow(0 - fpr,2)))/Math.sqrt(2); // only works for 2-class problems

				Main.measuresMultiple[0][0][0][i][(Integer)state.job[0]] = (1-eval.errorRate());
				Main.measuresMultiple[0][0][1][i][(Integer)state.job[0]] = eval.fMeasure(positiveIndex,Main.beta);
				Main.measuresMultiple[0][0][2][i][(Integer)state.job[0]] = eval.precision(positiveIndex);
				Main.measuresMultiple[0][0][3][i][(Integer)state.job[0]] = eval.recall(positiveIndex);
				Main.measuresMultiple[0][0][4][i][(Integer)state.job[0]] = algorithm.getRoot().numNodes();
				Main.measuresMultiple[0][0][5][i][(Integer)state.job[0]] = algorithm.getRoot().numLeaves();
				Main.measuresMultiple[0][0][6][i][(Integer)state.job[0]] = balance;				
				Main.measuresMultiple[0][0][7][i][(Integer)state.job[0]] = eval.areaUnderROC(positiveIndex);


				eval.crossValidateModel(algorithm, fulldataMetaTraining,10, new Random(1));
				tpr = eval.truePositiveRate(positiveIndex);
				fpr = eval.falsePositiveRate(positiveIndex);
				balance = 1 - Math.sqrt((Math.pow(1-tpr,2) + Math.pow(0 - fpr,2)))/Math.sqrt(2);

				Main.measuresMultiple[0][1][0][i][(Integer)state.job[0]] = (1-eval.errorRate());
				Main.measuresMultiple[0][1][1][i][(Integer)state.job[0]] = eval.fMeasure(positiveIndex,Main.beta);
				Main.measuresMultiple[0][1][2][i][(Integer)state.job[0]] = eval.precision(positiveIndex);
				Main.measuresMultiple[0][1][3][i][(Integer)state.job[0]] = eval.recall(positiveIndex);
				Main.measuresMultiple[0][1][4][i][(Integer)state.job[0]] = algorithm.getRoot().numNodes();
				Main.measuresMultiple[0][1][5][i][(Integer)state.job[0]] = algorithm.getRoot().numLeaves();
				Main.measuresMultiple[0][1][6][i][(Integer)state.job[0]] = balance;
				Main.measuresMultiple[0][1][7][i][(Integer)state.job[0]] = eval.areaUnderROC(positiveIndex);

				//	}
			}


			int sizeTest = Dataset.getMetaTest().size(); // datasets do conjunto metaTest
			for(int i=0;i<sizeTest;i++){
				Instances fullDataMetaTest = Dataset.getMetaTest().get(i);
				myEvaluation eval = new myEvaluation(fullDataMetaTest); 

				//for(int f=0;f<10;f++){  
				algorithm.buildClassifier(fullDataMetaTest);
				eval.evaluateModel(algorithm, fullDataMetaTest);
				tpr = eval.truePositiveRate(positiveIndex);
				fpr = eval.falsePositiveRate(positiveIndex);
				balance = 1 - Math.sqrt((Math.pow(1-tpr,2) + Math.pow(0 - fpr,2)))/Math.sqrt(2);

				Main.measuresMultiple[1][0][0][i][(Integer)state.job[0]] = (1-eval.errorRate());
				Main.measuresMultiple[1][0][1][i][(Integer)state.job[0]] = eval.fMeasure(positiveIndex,Main.beta);
				Main.measuresMultiple[1][0][2][i][(Integer)state.job[0]] = eval.precision(positiveIndex);
				Main.measuresMultiple[1][0][3][i][(Integer)state.job[0]] = eval.recall(positiveIndex);
				Main.measuresMultiple[1][0][4][i][(Integer)state.job[0]] = algorithm.getRoot().numNodes();
				Main.measuresMultiple[1][0][5][i][(Integer)state.job[0]] = algorithm.getRoot().numLeaves();
				Main.measuresMultiple[1][0][6][i][(Integer)state.job[0]] = balance;
				Main.measuresMultiple[1][0][7][i][(Integer)state.job[0]] = eval.areaUnderROC(positiveIndex);

				eval.crossValidateModel(algorithm, fullDataMetaTest,10,new Random(1));
				tpr = eval.truePositiveRate(positiveIndex);
				fpr = eval.falsePositiveRate(positiveIndex);
				balance = 1 - Math.sqrt((Math.pow(1-tpr,2) + Math.pow(0 - fpr,2)))/Math.sqrt(2);

				Main.measuresMultiple[1][1][0][i][(Integer)state.job[0]] = (1-eval.errorRate());
				Main.measuresMultiple[1][1][1][i][(Integer)state.job[0]] = eval.fMeasure(positiveIndex,Main.beta);
				Main.measuresMultiple[1][1][2][i][(Integer)state.job[0]] = eval.precision(positiveIndex);
				Main.measuresMultiple[1][1][3][i][(Integer)state.job[0]] = eval.recall(positiveIndex);
				Main.measuresMultiple[1][1][4][i][(Integer)state.job[0]] = algorithm.getRoot().numNodes();
				Main.measuresMultiple[1][1][5][i][(Integer)state.job[0]] = algorithm.getRoot().numLeaves();
				Main.measuresMultiple[1][1][6][i][(Integer)state.job[0]] = balance;
				Main.measuresMultiple[1][1][7][i][(Integer)state.job[0]] = eval.areaUnderROC(positiveIndex);
				//	}
			}
			//	createMetaDataset();
		}
	}


	public void evaluateBestParetoFront(ArrayList<Individual> front, EvolutionState state) throws Exception{


		if(!Dataset.isMultiple()){

			myEvaluation evalTrain, evalTest, evalValidation;

			for (int x = 0; x < front.size(); x++) {

				IntegerVectorIndividual ind = (IntegerVectorIndividual) front.get(x);
				EvolvedAlgorithm algorithm = decodeIndividual(ind);

				double tpr = 0.0, fpr = 0.0, balanceTrain = 0.0, balanceValidation = 0.0, balanceTest = 0.0;
				algorithm.buildClassifier(Dataset.getTrainingData());
				evalTrain = new myEvaluation(Dataset.getFullData());
				evalTrain.evaluateModel(algorithm, Dataset.getTrainingData());
				tpr = evalTrain.averageTruePositiveRate();
				fpr = evalTrain.averageFalsePositiveRate();
				balanceTrain = 1 - Math.sqrt((Math.pow(1-tpr,2) + Math.pow(0 - fpr,2))) /Math.sqrt(2); 
				double gmeanTrain = Math.sqrt(evalTrain.averageTruePositiveRate() * evalTrain.averageTrueNegativeRate());
				double avgClassAccTrain = 0;
				for (int i = 0; i < Dataset.getFullData().numClasses(); i++) {
					avgClassAccTrain += (evalTrain.truePositiveRate(i) + evalTrain.trueNegativeRate(i))/2;
				}
				avgClassAccTrain /= Dataset.getFullData().numClasses();
				
				
				evalValidation = new myEvaluation(Dataset.getFullData());
				evalValidation.evaluateModel(algorithm, Dataset.getValidationData());
				tpr = evalValidation.averageTruePositiveRate();
				fpr = evalValidation.averageFalsePositiveRate();
				balanceValidation = 1 - Math.sqrt((Math.pow(1-tpr,2) + Math.pow(0 - fpr,2)))/Math.sqrt(2); 
				double gmeanVal = Math.sqrt(evalValidation.averageTruePositiveRate() * evalValidation.averageTrueNegativeRate());
				double avgClassAccValidation = 0;
				for (int i = 0; i < Dataset.getFullData().numClasses(); i++) {
					avgClassAccValidation += (evalValidation.truePositiveRate(i) + evalValidation.trueNegativeRate(i))/2;
				}
				avgClassAccValidation /= Dataset.getFullData().numClasses();

				
				evalTest = new myEvaluation(Dataset.getFullData());
				evalTest.evaluateModel(algorithm, Dataset.getTestData());
				tpr = evalTest.averageTruePositiveRate();
				fpr = evalTest.averageFalsePositiveRate();
				balanceTest = 1 - Math.sqrt((Math.pow(1-tpr,2) + Math.pow(0 - fpr,2)))/Math.sqrt(2); 
				double gmeanTest = Math.sqrt(evalTest.averageTruePositiveRate() * evalTest.averageTrueNegativeRate());
				double avgClassAccTest = 0;
				for (int i = 0; i < Dataset.getFullData().numClasses(); i++) {
					avgClassAccTest += (evalTest.truePositiveRate(i) + evalTest.trueNegativeRate(i))/2;
				}
				avgClassAccTest /= Dataset.getFullData().numClasses();

				// weighted
				Main.pwParetoTest.println((1-evalTest.errorRate())+","
						+evalTest.averageFMeasure()+","
						+evalTest.averagePrecision()+","
						+evalTest.averageRecall()+","
						+algorithm.getRoot().numNodes()+","
						+algorithm.getRoot().numLeaves()+","
						+balanceTest+","
						+evalTest.averageAreaUnderROC()+","

					+gmeanTest+","
					+avgClassAccTest+","

					+writeIndividual(ind)+","
					+writeIndividual2(ind));
				


				Main.pwParetoAll.println((1-evalTrain.errorRate())+","
						+(1-evalValidation.errorRate())+","
						+(1-evalTest.errorRate())+","

					+evalTrain.averageFMeasure()+","
					+evalValidation.averageFMeasure()+","
					+evalTest.averageFMeasure()+","

					+evalTrain.averagePrecision()+","
					+evalValidation.averagePrecision()+","
					+evalTest.averagePrecision()+","

					+evalTrain.averageRecall()+","
					+evalValidation.averageRecall()+","
					+evalTest.averageRecall()+","

					+algorithm.getRoot().numNodes()+","
					+algorithm.getRoot().numLeaves()+","

					+balanceTrain+","
					+balanceValidation+","
					+balanceTest+","

					+evalTrain.averageAreaUnderROC()+","
					+evalValidation.averageAreaUnderROC()+","
					+evalTest.averageAreaUnderROC()+","

					+gmeanTrain+","
					+gmeanVal+","
					+gmeanTest+","

					+avgClassAccTrain+","
					+avgClassAccValidation+","
					+avgClassAccTest+","

					+writeIndividual(ind)+","
					+writeIndividual2(ind));
				
				Main.pConfusionMatrixPareto.println("Train");
				printConfusionMatrix(Main.pConfusionMatrixPareto, evalTrain.confusionMatrix());
				Main.pConfusionMatrixPareto.println();
				Main.pConfusionMatrixPareto.println("Validation");
				printConfusionMatrix(Main.pConfusionMatrixPareto, evalValidation.confusionMatrix());
				Main.pConfusionMatrixPareto.println();
				Main.pConfusionMatrixPareto.println("Test");
				printConfusionMatrix(Main.pConfusionMatrixPareto, evalTest.confusionMatrix());
				Main.pConfusionMatrixPareto.println();

			}
			
			Main.pwParetoTest.println();
			Main.pwParetoAll.println();
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


	public StringBuffer writeIndividual2(IntegerVectorIndividual ind){
		StringBuffer algorithm = new StringBuffer();
		algorithm.append("{"+ind.genome[0]);
		for (int i = 1; i < ind.genome.length; i++) {
			algorithm.append(","+ind.genome[i]);
		}
		algorithm.append("}");
		return algorithm;

	}

	public StringBuffer writeIndividual(IntegerVectorIndividual ind){
		StringBuffer algorithm = new StringBuffer();

		switch(ind.genome[0]){
		case 0: algorithm.append("UnivariateSplit-");break;
		case 1: algorithm.append("MultivariateSplit-");break;
		case 2: algorithm.append("OmniSplit-");break;
		default: break;
		}

		switch(ind.genome[1]){
		// to do - oblique strategy
		default: break;
		}

		algorithm.append(Measure.getName(ind.genome[2])+"-");

		if(ind.genome[3] == 0)
			algorithm.append("MultiNominalSplit-");
		else
			algorithm.append("BinaryNominalSplit-");

		switch(ind.genome[4]){
		case 0: algorithm.append("HomogeneousStop-"); break;
		case 1: algorithm.append("MinNumberInstStop:"); break;
		case 2: algorithm.append("MinPercInstStop:"); break;
		case 3: algorithm.append("AccThresholdStop:"); break;
		case 4: algorithm.append("MaxDepthReached:"); break;
		}

		double param;
		switch(ind.genome[4]){
		case 0: break;
		case 1: param = (ind.genome[5] % 20) + 1; 
		algorithm.append(param+"-"); 
		break;
		case 2: param = ((ind.genome[5] % 10) + 1)/100; 
		algorithm.append(param+"-"); 
		break;
		case 3: param = (ind.genome[5] % 7) + 70 + (ind.genome[5]*5); 
		if(Utils.eq(param,100)) param = param - 1; 
		algorithm.append(param+"-"); 
		break;
		case 4: param = (ind.genome[5] % 9) + 2; 
		algorithm.append(param+"-"); 
		break;
		default: break;
		}

		switch(ind.genome[6]){
		case 0: algorithm.append("MVS:IgnoreMissingValues-"); break;
		case 1: algorithm.append("MVS:UnsupervisedImputation-"); break;
		case 2: algorithm.append("MVS:SupervisedImputation-"); break;
		case 3: algorithm.append("MVS:WeightSplitCriterionValue-"); break;
		default: break;
		}

		switch(ind.genome[7]){
		case 0: algorithm.append("MVD:WeightWithBagProbability-"); break;
		case 1: algorithm.append("MVD:IgnoreMissingValues-"); break;
		case 2: algorithm.append("MVD:UnsupervisedImputation-"); break;
		case 3: algorithm.append("MVD:SupervisedImputation-"); break;
		case 4: algorithm.append("MVD:AssignToAllBags-"); break;
		case 5: algorithm.append("MVD:AddToLargestBag-"); break;
		case 6: algorithm.append("MVD:AssignToMostProbableBagRegardingClass-"); break;
		default: break;
		}

		if(ind.genome[8]==0)
			algorithm.append("DontCollapseTree-");
		else
			algorithm.append("CollapseTree-");


		if(ind.genome[9]==0)
			algorithm.append("NoLaPlace-");
		else
			algorithm.append("UseLaPlaceCorrection-");

		switch(ind.genome[10]){
		case 0: algorithm.append("MVC:ExploreAllBranchesAndCombine-"); break;
		case 1: algorithm.append("MVC:HaltInTheCurrentNode-"); break;
		case 2: algorithm.append("MVC:GoToMostProbableBag-"); break;
		default: break;
		}

		switch(ind.genome[11]){
		case 0: algorithm.append("NoPruning-"); break;
		case 1: algorithm.append("ErrorBasedPruning:"); break;
		case 2: algorithm.append("MinimumErrorPruning:"); break;
		case 3: algorithm.append("PessimisticErrorPruning:"); break;
		case 4: algorithm.append("CostComplexityPruning:"); break;
		case 5: algorithm.append("ReducedErrorPruning:"); break;
		}

		Double pruningParameter;
		switch(ind.genome[11]){
		case 0: break;
		case 1: pruningParameter = ((double)(ind.genome[12] % 50) + 1)/100;; algorithm.append(pruningParameter); break;
		case 2: pruningParameter = (double)ind.genome[12] % 100; algorithm.append(pruningParameter); break;
		case 3: pruningParameter = 0.5* ((ind.genome[12] % 4)+1); algorithm.append(pruningParameter); break;
		case 4: pruningParameter = 0.5* ((ind.genome[12] % 4)+1); algorithm.append(pruningParameter+"-"+ind.genome[13]+"folds"); break;
		case 5: algorithm.append(ind.genome[13]+"folds"); break;
		default: break;
		}


		return algorithm;

	}

	public EvolvedAlgorithm decodeIndividual(IntegerVectorIndividual ind) throws Exception{
		SplitParameters split = new SplitParameters();
		TreeParameters treeParam = new TreeParameters();

		// Split Strategy - univariate, multivariate, omni
		split.setSplitType(ind.genome[0]);

		// Oblique Strategy (to do)
		// split.setObliqueStrategy(ind.genome[1]);

		// Split Measure
		split.setSplitMeasure(ind.genome[2]);

		// Binary Nominal Split?
		boolean binaryNominal;
		if(ind.genome[3] == 0) binaryNominal = false;
		else binaryNominal = true;
		split.BinarySplits(binaryNominal);

		// Stopping Criteria
		split.setStoppingCriteria(ind.genome[4]);

		// Stopping Criteria Parameters
		double stoppingParameter;
		switch(ind.genome[4]){

		// do nothing - homogeneity criterion
		case 0: break; // do nothing

		// min number of objects [1,20]
		case 1: stoppingParameter = (ind.genome[5] % 20) + 1;
		split.setMinNumObj((int)stoppingParameter);
		break;

		// min perc of objects [1%,10%]
		case 2: stoppingParameter = ((double)((ind.genome[5] % 10) + 1))/(double)100;
		split.setMinPercObj(stoppingParameter);
		break;

		// min accuracy reached {70%, 75%, 80%, 85%, 90%, 95%, 99%}
		case 3: stoppingParameter = ((double)((ind.genome[5] % 7) *5) + 70)/100;
		if(Utils.eq(stoppingParameter,1.0))
			stoppingParameter = stoppingParameter - 0.1;
		split.setMinAccuracy(stoppingParameter);
		break;

		// max depth reached {2,3,4,5,6,7,8,9,10}
		case 4: stoppingParameter = (ind.genome[5] % 9) + 2;
		split.setMaxDepth((int)stoppingParameter);
		break;

		default: break; //do nothing;
		}

		// Missing value during split
		split.setMissingValueSplit(ind.genome[6]);

		// Missing value distribution
		split.setMissingValueDistribution(ind.genome[7]);

		// Collapse tree - training error pruning
		boolean willCollapse;
		if(ind.genome[8] == 0) willCollapse = false;
		else willCollapse = true;
		treeParam.set_collapse(willCollapse);

		// Laplace correction at leaves?
		boolean useLaplace;
		if(ind.genome[9] == 0) useLaplace = false;
		else useLaplace = true;
		treeParam.setLaplace(useLaplace);

		// Missing value strategy for classification of new instances
		treeParam.setMissingValueClassification(ind.genome[10]);

		// Pruning strategy
		treeParam.setPruningType(ind.genome[11]);

		// Pruning parameter
		double pruningParameter = 0;
		switch(ind.genome[11]){

		// no pruning
		case 0: //do nothing;
			break; 

			// Error-based pruning - CF varies [1% - 50%]
		case 1: pruningParameter = ((double)(ind.genome[12] % 50) + 1)/100;
		treeParam.setM_CF((float)pruningParameter);
		break;

		// Minimum error pruning - M varies from [0,99]
		case 2: pruningParameter = ind.genome[12] % 100;
		treeParam.setM((float)pruningParameter);
		break;

		// Pessimistic-error pruning - number of SEs varies as {0.5, 1, 1.5, 2}
		case 3: pruningParameter =  0.5* ((ind.genome[12] % 4)+1);
		treeParam.setNumberOfSEs(pruningParameter);
		break;

		// Cost-complexity Pruning - stop param 1 = number of SEs (as above). stop param 2 = num folds (2-10).
		case 4: pruningParameter = 0.5* ((ind.genome[12] % 4)+1);
		treeParam.setNumberOfSEs(pruningParameter);
		treeParam.setNumFoldPruning(ind.genome[13] );
		break;

		// Reduced-error pruning
		case 5: treeParam.setNumFoldPruning(ind.genome[13]);
		break;

		default: //do nothing
			break; 
		}

		EvolvedAlgorithm algorithm = new EvolvedAlgorithm(split,treeParam);
		return algorithm;
	}


	public void storeMetaData(EvolvedAlgorithm algorithm, double fitness, int base){

		int binarySplit = algorithm.getSplitParam().isBinarySplits() ? 1 : 0;
		int mvClassification = algorithm.getTreeParam().getMissingValueClassification();
		int mvDistribution = algorithm.getSplitParam().getMissingValueDistribution();
		int mvSplit = algorithm.getSplitParam().getMissingValueSplit();
		int pruning = algorithm.getTreeParam().getpruningType();
		int criterion = algorithm.getSplitParam().getSplitMeasure();
		int stopCriterion = algorithm.getSplitParam().getStoppingCriteria();


		int stopParameter;
		switch(stopCriterion){
		//homogeneity, no parameter
		case 0: break;
		// min number of instances [1,20] --> convert to [0,19]
		case 1: {
			stopParameter = algorithm.getSplitParam().getMinNumObj();
			Main.metaData[base].setSTOPminNumber((stopParameter-1), fitness);
			break;
		}
		//min percentage of instances [1,10] --> convert to [0,9]
		case 2:{
			stopParameter = (int) (algorithm.getSplitParam().getMinPercObj() * 100);
			Main.metaData[base].setSTOPminPerc((stopParameter-1), fitness);
			break;
		}
		case 3:{
			int value = (int)(algorithm.getSplitParam().getMinAccuracy() * 100);
			switch(value){
			case(70):{
				Main.metaData[base].setSTOPminAcc(0, fitness);
				break;
			}
			case(75):{
				Main.metaData[base].setSTOPminAcc(1, fitness);
				break;
			}
			case(80):{
				Main.metaData[base].setSTOPminAcc(2, fitness);
				break;
			}
			case(85):{
				Main.metaData[base].setSTOPminAcc(3, fitness);
				break;
			}
			case(90):{
				Main.metaData[base].setSTOPminAcc(4, fitness);
				break;
			}
			case(95):{
				Main.metaData[base].setSTOPminAcc(5, fitness);
				break;
			}
			case(99):{
				Main.metaData[base].setSTOPminAcc(6, fitness);
				break;
			}
			default: {System.out.println("ERRO NESSA PARTE NOVA DO CODIGO - CLASSE GENERATETREES, METODO storeMetaData");
			break;
			}
			}
			break;
		}
		case 4:{
			stopParameter = algorithm.getSplitParam().getMaxDepth();
			Main.metaData[base].setSTOPmaxDepth((stopParameter-2), fitness);
			break;
		}
		default: break;
		}

		int pruningParameter;
		switch(pruning){
		// no pruning
		case 0: break;
		// Error-based Pruning [1,50] ---> [0,49]
		case 1: {
			pruningParameter = (int) (algorithm.getTreeParam().getM_CF() * 100);
			Main.metaData[base].setEBP((pruningParameter-1), fitness);
			break;
		}
		// Minimum Error Pruning [0,99]
		case 2: {
			pruningParameter = (int)algorithm.getTreeParam().getM();
			Main.metaData[base].setMEP(pruningParameter, fitness);
			break;
		}
		// PEP [0.5,1,1.5,2]
		case 3:{
			double value = algorithm.getTreeParam().getNumberOfSEs();
			if(value ==0.5){
				Main.metaData[base].setPEP(0, fitness);
			}
			else if(value == 1){
				Main.metaData[base].setPEP(1, fitness);
			}
			else if(value ==1.5){
				Main.metaData[base].setPEP(2, fitness);
			}
			else if(value == 2){
				Main.metaData[base].setPEP(3, fitness);
			}
			else {System.out.println("DEU ERRO NA PARTE NOVA - PRUNING, DENTRO DE STORE METADATA, DENTRO DE GENERATE TREES");}
			break;
		}
		// CCP - STANDARD ERRORS
		case 4:{
			double value = algorithm.getTreeParam().getNumberOfSEs();
			if(value ==0.5){
				Main.metaData[base].setCCP1(0, fitness);
			}
			else if(value == 1){
				Main.metaData[base].setCCP1(1, fitness);
			}
			else if(value ==1.5){
				Main.metaData[base].setCCP1(2, fitness);
			}
			else if(value == 2){
				Main.metaData[base].setCCP1(3, fitness);
			}
			else {System.out.println("DEU ERRO NA PARTE NOVA - PRUNING, DENTRO DE STORE METADATA, DENTRO DE GENERATE TREES");}
			break;
		} 
		// CCP2 - Number of Folds
		case 5:{
			pruningParameter = algorithm.getTreeParam().getNumFoldPruning();
			Main.metaData[base].setCCP2((pruningParameter-2), fitness);
			break;
		}
		// REP
		case 6:{
			pruningParameter = algorithm.getTreeParam().getNumFoldPruning();
			Main.metaData[base].setREP((pruningParameter-2), fitness);
			break;
		}
		}

		Main.metaData[base].setBinarySplit(binarySplit, fitness);
		Main.metaData[base].setMvClassification(mvClassification, fitness);
		Main.metaData[base].setMvDistribution(mvDistribution, fitness);
		Main.metaData[base].setMvSplit(mvSplit, fitness);
		Main.metaData[base].setPruning(pruning, fitness);
		Main.metaData[base].setSplitCriterion(criterion, fitness);
		Main.metaData[base].setStopCriterion(stopCriterion, fitness);
	}

	public void createMetaDataset() throws Exception{

		StringBuffer dataset = new StringBuffer();
		MetaLearning meta;
		dataset.append("Nome,#Classes,#Inst,#Att,#Nominal,%Nominal,#Numeric,%Numeric,#Bin,#Missing,%Missing," +
				"Dimensionality,ClassEntropy,MeanAttributeEntropy,MeanMutualInformation,EquivalentNumberOfAttributes, NSratio," +
				"SDratio,MeanAttributeCorrelation,MeanAttributeSkewness,MeanAttributeKurtosis," +
				"#Splits,SplitCrit,StopCrit,MvSplit,MvDist,MvClass,Pruning\n");
		for(int i=0;i<Dataset.getMetaTraining().size();i++){
			meta = new MetaLearning(Dataset.getMetaTraining().get(i));
			dataset.append(meta.getName()+","+meta.getNumClasses()+","+meta.getNumInstances()+","+meta.getNumAtt()+","+meta.getNomAtt()+","+
					meta.getPercentageNominal()+","+meta.getNumericAtt()+","+meta.getPercentageNumeric()+","+meta.getBinaryAtt()+","+
					meta.getMissingValues()+","+meta.getPercentageMissingValues()+","+meta.getDimensionality()+","+meta.getClassEntropy()+","+
					meta.getAttEntropy()+","+meta.getMutualInformation()+","+meta.getEquivalentNumberOfAttributes()+","+meta.getNSratio()+","+
					meta.getSDratio()+","+meta.getAverageAttributeCorrelation()+","+meta.getAverageAttributeSkewness()+","+
					meta.getAverageAttributeKurtosis()+","+Main.metaData[i].getBinarySplit()+","+Main.metaData[i].getSplitCriterion()+","+
					Main.metaData[i].getStopCriterion()+","+Main.metaData[i].getMvSplit()+","+ Main.metaData[i].getMvDistribution()+","+
					Main.metaData[i].getMvClassification()+","+Main.metaData[i].getPruning()+"\n");
		}
		Main.printMeta.print(dataset);
		try {
			Main.printMeta.close();
			Main.meta.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
