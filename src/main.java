import java.io.IOException;

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
		
		int all[] = {1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		int independent[] = {1,2,3,4,5,6,7,8,9,10,11,12,13,14};  // 14 different classifiers.
		
		int other[] = {1,1,1,4,5,2,2,2,9,10,11,3,3,3}; 
		
		population[0] = all;
		//population[1] = independent;
		//population[2] = other;
		
		String train = "./datasets/water-quality/water-quality-train.arff";
		String test = "./datasets/water-quality/water-quality-test.arff";
		
		
		// Run this BEFORE the main loop of the GA,
		// indicate: name of training and test datasets + range of output attributes (for Disable option)
		
		ClusWrapper.initialization(train, test, "17-30"); 	
		
		
		// Run this as your fitness function. You can choose between MAE, MSE, RMSE or WRMSE as performance measure
		
		
		/*
		double error[] = ClusWrapper.evaluatePopulation(population, "MAE");  
		
		System.out.println("\nIndividuals average RMSE errors: ");
		for(int i=0; i<1;i++){
			System.out.println("Error "+i+": "+error[i]);
		} */
		
		
		
		myMeasures measure = ClusWrapper.evaluateIndividual(all);
		
		System.out.println("MAE: "+ measure.getMAE()[0]);
		System.out.println("MSE: "+ measure.getMSE()[0]);
		System.out.println("RMSE: "+ measure.getRMSE()[0]);
		System.out.println("WRMSE: "+ measure.getWRMSE()[0]);
		
		
        measure = ClusWrapper.evaluateIndividual(independent);
		
		System.out.println("\nMAE: "+ measure.getMAE()[0]);
		System.out.println("MSE: "+ measure.getMSE()[0]);
		System.out.println("RMSE: "+ measure.getRMSE()[0]);
		System.out.println("WRMSE: "+ measure.getWRMSE()[0]);
		
	
	}

}
