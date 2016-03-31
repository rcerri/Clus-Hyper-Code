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
public class main {

	public static void main(String[] args) throws IOException, ClusException {
		// TODO Auto-generated method stub

		int [][] population = new int [1][14];  // Example of population 2 individuals, 14 components.
		
		int all[] = {1,1,1,1,1,1,1};
		int independent[] = {1,2,3,4,5,6,7};  // 14 different classifiers.
		
		int other[] = {1,1,1,4,4,2,2,2}; 
		int other2[] = {1,1,1,2,2,4,3,3}; 

		
		population[0] = all;
		//population[1] = independent;
		//population[2] = other;
		
		
		/*
		 * ***********************REGRESSION EXAMPLE************************
		 *
		
		String train = "../datasets/regression/water-quality/water-quality-train.arff";
		String test =  "../datasets/regression/water-quality/water-quality-test.arff";
		
		//rf1 -> [65-72]
		// Run this BEFORE the main loop of the GA,
		// indicate: name of training and test datasets + range of output attributes (for Disable option)
		
		ClusWrapper.initialization(train, test, "17-30",true); 	
		

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
		
		System.exit(1);
				
        measure = ClusWrapper.evaluateIndividual(other2,true);
		
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
		/*
		 * ***********************CLASSIFICATION************************
		 */
		
		
		String train = "../datasets/classification/flags/flags-train.arff";
		String test =  "../datasets/classification/flags/flags-test.arff";
		
		ClusWrapper.initialization(train, test, "20-26",false); 	
		

		// Isaac: why Clus fails with Trees without training error??
		myMeasures measure = ClusWrapper.evaluateIndividualClassification(all,true);
		
		
		System.out.println("\nTraining error: ");
		System.out.println("Classification Error: "+ measure.getAccuracy()[0]);  // Give 0's, if false.
		System.out.println("F1: "+ measure.getF1()[0]);
		System.out.println("WMSE nominal: "+ measure.getWMSEnominal()[0]);
		
        System.out.println("\nTest error: ");
		System.out.println("Classification Error: "+ measure.getAccuracy()[1]);  // Give 0's, if false.
		System.out.println("F1: "+ measure.getF1()[1]);
		System.out.println("WMSE nominal: "+ measure.getWMSEnominal()[1]);
		
		
		
        measure = ClusWrapper.evaluateIndividualClassification(independent,true);
		
		System.out.println("\nTraining error: ");
		System.out.println("Classification Error: "+ measure.getAccuracy()[0]);  // Give 0's, if false.
		System.out.println("F1: "+ measure.getF1()[0]);
		System.out.println("WMSE nominal: "+ measure.getWMSEnominal()[0]);
		
        System.out.println("\nTest error: ");
		System.out.println("Classification Error: "+ measure.getAccuracy()[1]);  // Give 0's, if false.
		System.out.println("F1: "+ measure.getF1()[1]);
		System.out.println("WMSE nominal: "+ measure.getWMSEnominal()[1]);
		
		
		
	}

}
