package ga;

import java.io.IOException;
import clus.util.ClusException;
import Util.ClusWrapper;     // you need to import the ClusWrapper
import Util.myMeasures;

/** 
 * Example of use of ClusWrapper
 * @author isaact
 *
 */
public class main_test {

	public static void main(String[] args) throws IOException, ClusException {
		// TODO Auto-generated method stub
		
		int all[] = {1,1,1,1,1,1,1,1};
		int independent[] = {1,2,3,4,5,6,7,8};  // 14 different classifiers.
		
		String dataFile = "water-quality";
		String train = "/Users/basgalupp/Dropbox/datasets/Clus/regression/"+dataFile+"/"+dataFile+"-train.arff";
		String test = "/Users/basgalupp/Dropbox/datasets/Clus/regression/"+dataFile+"/"+dataFile+"-test.arff";
		
		// Run this BEFORE the main loop of the GA,
		// indicate: name of training and test datasets + range of output attributes (for Disable option)
		
		ClusWrapper.initialization(train, test, "17-30",true);	
		myMeasures measures = ClusWrapper.evaluateIndividual(all,true);
		//myMeasures measures = ClusWrapper.evaluateIndividual(independent,true);
		
		double mae[] = new double[3];
		double mse[] = new double[3];
		double rmse[] = new double[3];
		double wrmse[] = new double[3];

		mae[0] = measures.getMAE()[0]; mse[0] = measures.getMSE()[0]; rmse[0] = measures.getRMSE()[0]; wrmse[0] = measures.getWRMSE()[0];
		mae[1] = measures.getMAE()[1]; mse[1] = measures.getMSE()[1]; rmse[1] = measures.getRMSE()[1]; wrmse[1] = measures.getWRMSE()[1];
		
		// Run this as your fitness function. You can choose between MAE, MSE, RMSE or WRMSE as performance measure
				  
		System.out.println("\nTesting errors:: ");
		System.out.println("MAE: "+mae[1]);
		System.out.println("MSE: "+mse[1]);
		System.out.println("RMSE: "+rmse[1]);
		
		System.out.println("\nTraining errors: ");
		System.out.println("MAE: "+mae[0]);
		System.out.println("MSE: "+mse[0]);
		System.out.println("RMSE: "+rmse[0]);
	}
}
