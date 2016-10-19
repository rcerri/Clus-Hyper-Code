package Util;

import java.io.IOException;
import java.util.StringTokenizer;

import clus.util.ClusException;
import Util.ClusWrapper;     // you need to import the ClusWrapper
import Util.myMeasures;

/** 
 * Example of use of ClusWrapper
 * @author isaact
 *
 */
public class BruteForce {

	public static void main(String[] args) throws IOException, ClusException {
		// TODO Auto-generated method stub

		int [][] population = new int [1][14];  // Example of population 2 individuals, 14 components.
		
		int all[] = {1,1,1,1,1,1,1,1};
		int independent[] = {1,2,3,4,5,6,7,8};  // 14 different classifiers.
		
		int other[] = {1,1,1,4,5,2,2,2,9,10,11,3,3,3}; 
		
		population[0] = all;
		//population[1] = independent;
		//population[2] = other;
		
		if(args.length!=5){
			System.err.println("The number of parameters is 4:  <training dataset> <test dataset> <attributes> <fileWithPotentialPartitions><numOutputs>");
			System.exit(1);
		}
		
		String train = args[0]; // "../datasets/regression/rf1/rf1-train.arff";
		String test = args[1]; // "../datasets/regression/rf1/rf1-test.arff";
		
		//rf1 -> [65-72]
		// Run this BEFORE the main loop of the GA,
		// indicate: name of training and test datasets + range of output attributes (for Disable option)
		
		ClusWrapper.initialization(train, test, args[2],false,false); 	
		

		// brute force
		// read file with all possible partitions for this particular problem
		
		String cadena = Fichero.leeFichero(args[3]); //"../potentialPartitions-RF1.txt"
		//String cadena = Fichero.leeFichero("../prueba.txt");
		StringTokenizer lineas = new StringTokenizer (cadena,"\n\r");
		
		String linea = "";
		
		// lineas.nextToken(); // skip first line
		
		int cont=0;
		
		double minimumTRAError = Double.MAX_VALUE;
		String betterTRAIndividual="";
		
		double minimumTSTError = Double.MAX_VALUE;
		String betterTSTIndividual="";
		
		int numOutputs= Integer.parseInt(args[4]);
		
		while (lineas.hasMoreElements()){
			
			
			linea = lineas.nextToken();
			
			String clusters[] = linea.split(" ");
			
			int individual [] = new int[numOutputs];
			
			// System.out.println(linea);
			
			
			for(int i=0; i<clusters.length; i++){
				
				if(clusters[i].contains(",")){
					
					String targets[] = clusters[i].split(",");
					
					for(int j=0; j< targets.length; j++){
						// System.out.println(targets[j]);
						individual[Integer.parseInt(targets[j])-1] = i+1;
					}
				}else{
					
					
					individual[Integer.parseInt(clusters[i])-1] = i+1; 
					
				}
			}
			
			System.out.print("\n Individual: ");
			for(int i=0; i< individual.length; i++){
				System.out.print(individual[i]+", ");
			}
			
			System.out.println("");
			// System.out.println("\n"+clusters.length);
			
			myMeasures measure = ClusWrapper.evaluateIndividual(individual,true);
			
			System.out.println("Training: " +measure.getMAE()[0]+ ","+measure.getMSE()[0]+","+measure.getRMSE()[0]+","+measure.getWRMSE()[0]);
			System.out.println("Test: " +measure.getMAE()[1]+ ","+measure.getMSE()[1]+","+measure.getRMSE()[1]+","+measure.getWRMSE()[1]);
			
			
			if(measure.getMAE()[0]<minimumTRAError){
				minimumTRAError = measure.getMAE()[0];
				betterTRAIndividual = linea;
			}
			
			if(measure.getMAE()[1]<minimumTSTError){
				minimumTSTError = measure.getMAE()[1];
				betterTSTIndividual = linea;
			}
			
			cont++;
		}
		
		
		System.out.println("\n\nThe best training individual is: "+betterTRAIndividual +  ";  error: "+minimumTRAError);
		System.out.println("The best test individual is: "+betterTSTIndividual+  ";  error: "+minimumTSTError);
		
		// Run this as your fitness function. You can choose between MAE, MSE, RMSE or WRMSE as performance measure

		
		/*
		// Isaac: why Clus fails with Trees without training error??
		myMeasures measure = ClusWrapper.evaluateIndividual(all,true);
		
		
		System.out.println("\nTraining error: ");
		System.out.println("MAE: "+ measure.getMAE()[0]);  // Give 0's, if false.
		System.out.println("MSE: "+ measure.getMSE()[0]);
		System.out.println("RMSE: "+ measure.getRMSE()[0]);
		System.out.println("WRMSE: "+ measure.getWRMSE()[0]);
		
        System.out.println("\nTest error: ");
		System.out.println("MAE: "+ measure.getMAE()[1]);
		System.out.println("MSE: "+ measure.getMSE()[1]);
		System.out.println("RMSE: "+ measure.getRMSE()[1]);
		System.out.println("WRMSE: "+ measure.getWRMSE()[1]);
		
		
				
        measure = ClusWrapper.evaluateIndividual(independent,true);
		
        System.out.println("\nTraining error: ");
		System.out.println("\nMAE: "+ measure.getMAE()[0]);
		System.out.println("MSE: "+ measure.getMSE()[0]);
		System.out.println("RMSE: "+ measure.getRMSE()[0]);
		System.out.println("WRMSE: "+ measure.getWRMSE()[0]);
		
        System.out.println("\nTest error: ");

		System.out.println("\nMAE: "+ measure.getMAE()[1]);
		System.out.println("MSE: "+ measure.getMSE()[1]);
		System.out.println("RMSE: "+ measure.getRMSE()[1]);
		System.out.println("WRMSE: "+ measure.getWRMSE()[1]);
		
		*/
		
	}

}
