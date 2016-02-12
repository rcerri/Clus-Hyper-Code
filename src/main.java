import java.io.IOException;

import clus.util.ClusException;
import Util.ClusWrapper;     // you need to import the ClusWrapper

/** 
 * Example of use of ClusWrapper
 * @author isaact
 *
 */
public class main {

	public static void main(String[] args) throws IOException, ClusException {
		// TODO Auto-generated method stub

		int [][] population = new int [2][14];  // Example of population 2 individuals, 14 components.
		
		int all[] = {1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		int independent[] = {1,2,3,4,5,6,7,8,9,10,11,12,13,14};  // 14 different classifiers.
		
		population[0] = all;
		population[1] = independent;
		
		
		String train = "./datasets/water-quality/water-quality-train.arff";
		String test = "./datasets/water-quality/water-quality-test.arff";
		
		
		// Run this BEFORE the main loop of the GA,
		// indicate: name of training and test datasets + range of output attributes (for Disable option)
		
		ClusWrapper.initialization(train, test, "17-30"); 	
		
		
		// Run this as your fitness function. You can choose between MAE, MSE, RMSE or WRMSE as performance measure
		
		
		double error[] = ClusWrapper.evaluatePopulation(population, "RMSE");  
		
		System.out.println("\nIndividuals average RMSE errors: ");
		for(int i=0; i<2;i++){
			System.out.println("Error "+i+": "+error[i]);
		}
	
	}

}
