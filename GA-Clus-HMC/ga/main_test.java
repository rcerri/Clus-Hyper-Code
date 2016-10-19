package ga;

import java.io.IOException;
import clus.util.ClusException;
import ec.EvolutionState;
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
		/*
		oes10 -> [299-314]
		oes97 -> [264-279]
		rf1 -> [65-72]
		rf2 -> [577-584]
		scm1d -> [281-296]
		scm20d -> [62-77]
		water-quality -> [17-30]
		*/
		
		String dataFile = "corel5k";
		int firstTarget = 500;
		int lastTarget = 873;
		
		String train = "/Users/basgalupp/Dropbox/Github/Clus-Hyper-Code/datasets/classification/"+dataFile+"/"+dataFile+"_fold1.trainvalid";
		String test = "/Users/basgalupp/Dropbox/Github/Clus-Hyper-Code/datasets/classification/"+dataFile+"/"+dataFile+"_fold1.test";
		
		//System.out.println(train);
		
		int nTargets = 1 + lastTarget - firstTarget; 
		int all[] = new int[nTargets]; int independent[] = new int[nTargets];
		
		
		for (int i = 0; i < nTargets; i++) {
			all[i] = 1;
			independent[i] = i+1;
		}
		

		// Run this BEFORE the main loop of the GA,
		// indicate: name of training and test datasets + range of output attributes (for Disable option)
		
		// regression
		//ClusWrapper.initialization(train, test, String.valueOf(firstTarget)+"-"+String.valueOf(lastTarget),false,false);	
		//myMeasures measures = ClusWrapper.evaluateIndividual(all,true);
		//myMeasures measures = ClusWrapper.evaluateIndividual(independent,true);
		
		long iTime = System.currentTimeMillis();
		ClusWrapper.initialization(train,test, String.valueOf(firstTarget)+"-"+String.valueOf(lastTarget),false,true);
		myMeasures measures = ClusWrapper.evaluateIndividualClassification(independent,true);
		long fTime = System.currentTimeMillis();
		System.out.println("Tempo de execução = "+(fTime-iTime)/1000);
		
		double mae[] = new double[3];
		double mse[] = new double[3];
		double rmse[] = new double[3];
		double wrmse[] = new double[3];

		mae[0] = measures.getMAE()[0]; mse[0] = measures.getMSE()[0]; rmse[0] = measures.getRMSE()[0]; wrmse[0] = measures.getWRMSE()[0];
		mae[1] = measures.getMAE()[1]; mse[1] = measures.getMSE()[1]; rmse[1] = measures.getRMSE()[1]; wrmse[1] = measures.getWRMSE()[1];
		
		// Run this as your fitness function. You can choose between MAE, MSE, RMSE or WRMSE as performance measure
				  
		System.out.println("\nTesting errors:: ");
		System.out.println("MAE,MSE,RMSE");		
		System.out.println(mae[1] +", "+ mse[1] +", "+ rmse[1]);
		
		System.out.println("\nTraining errors: ");
		System.out.println("MAE,MSE,RMSE");		
		System.out.println(mae[0] +", "+ mse[0] +", "+ rmse[0]);
	}
}
