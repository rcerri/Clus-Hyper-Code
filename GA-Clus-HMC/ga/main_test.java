package ga;

import java.io.IOException;
import clus.util.ClusException;
import ec.EvolutionState;
import Util.ClusWrapper;     // you need to import the ClusWrapper
import Util.ClusWrapperNonStatic;
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
		
		//String dataFile = "corel5k";
		//int firstTarget = 500;
		//int lastTarget = 873;
		
		String dataFile = "water-quality";
		int firstTarget = 17;
		int lastTarget = 30;
		
		//String train = "/Users/basgalupp/Dropbox/Github/Clus-Hyper-Code/datasets/classification/"+dataFile+"/"+dataFile+"_fold1.trainvalid";
		//String test = "/Users/basgalupp/Dropbox/Github/Clus-Hyper-Code/datasets/classification/"+dataFile+"/"+dataFile+"_fold1.test";
		
		String train = "/Users/basgalupp/Dropbox/Github/Clus-Hyper-Code/datasets/regression/"+dataFile+"/"+dataFile+"_fold3.train";
		String valid = "/Users/basgalupp/Dropbox/Github/Clus-Hyper-Code/datasets/regression/"+dataFile+"/"+dataFile+"_fold3.valid";
		String trainValid = "/Users/basgalupp/Dropbox/Github/Clus-Hyper-Code/datasets/regression/"+dataFile+"/"+dataFile+"_fold3.trainvalid";
		String test = "/Users/basgalupp/Dropbox/Github/Clus-Hyper-Code/datasets/regression/"+dataFile+"/"+dataFile+"_fold3.valid";
		
		
		//System.out.println(train);
		
		int nTargets = 1 + lastTarget - firstTarget; 
		
		int all[] = new int[nTargets]; int independent[] = new int[nTargets];
		
		
		for (int i = 0; i < nTargets; i++) {
			all[i] = 1;
			independent[i] = i+1;
		}
		
		int solution[] = {1, 1, 2, 1, 3, 4, 4, 1, 5, 6, 6, 4, 1, 1};
		
		//int other[] = {1,2,3,4,5,6,7,8,7,9,10,11,10,12,13,14,15,16,10,17,18,17,19,20,21,22,23,24,25,21,26,22,27,27,28,29,30,27,31,29,32,33,29,34,28,35,33,36,34,29,37,28,38,37,39,36,26,40,41,41,41,32,42,42,43,44,43,13,45,46,47,46,48,49,49,50,51,52,53,54,49,45,55,56,57,58,51,59,58,60,61,60,61,9,2,62,36,63,64,14,8,46,20,58,54,53,36,65,66,67,64,59,68,4,34,67,69,70,71,72,73,74,75,72,73,76,77,56,78,79,80,13,78,69,81,82,83,84,85,8,76,82,86,80,84,57,87,79,83,88,89,85,85,90,45,51,91,67,92,49,73,86,82,93,90,94,95,96,97,67,98,99,91,100,91,101,102,90,26,92,103,104,105,28,6,106,85,107,99,95,65,49,108,59,101,109,110,111,101,112,113,114,115,116,117,75,107,97,79,118,56,49,116,119,67,111,67,117,108,68,120,100,115,100,110,121,122,123,124,125,117,126,127,115,110,24,111,124,128,104,96,129,77,130,65,131,132,15,133,32,134,135,136,15,137,138,139,140,50,131,123,141,102,123,142,72,139,111,143,144,81,145,115,123,146,147,125,79,148,18,126,130,26,145,131,54,149,150,93,82,135,119,119,151,152,27,67,140,153,69,119,73,154,110,78,63,155,156,157,13,52,158,159,160,24,161,124,97,162,160,81,101,24,112,163,164,148,154,159,64,65,64,153,2,65,165,166,167,168,65,169,170,50,160,171,172,162,159,164,33,150,22,168,173,174,173,39,175,176,172,171,99,118,177,71,178,179,74,180,118,3,180,181,182};

		
		
		// Run this BEFORE the main loop of the GA,
		// indicate: name of training and test datasets + range of output attributes (for Disable option)
		
		// regression
		//ClusWrapper.initialization(train, test, String.valueOf(firstTarget)+"-"+String.valueOf(lastTarget),false,false);	
		//myMeasures measures = ClusWrapper.evaluateIndividual(all,true);
		//myMeasures measures = ClusWrapper.evaluateIndividual(independent,true);
		
		long iTime = System.currentTimeMillis();
		//ClusWrapper.initialization(train,test, String.valueOf(firstTarget)+"-"+String.valueOf(lastTarget),true,true);
		//myMeasures measures = ClusWrapper.evaluateIndividualClassification(other,true);
		//ClusWrapperNonStatic object = new ClusWrapperNonStatic();		
		//object.initialization(train,valid, String.valueOf(firstTarget)+"-"+String.valueOf(lastTarget),true,true);
		//myMeasures measures = object.evaluateIndividualClassification(otherBirds,true);
		
		// regression multi thread
		ClusWrapperNonStatic objectClus = new ClusWrapperNonStatic();
		objectClus.clus = null;
	 	objectClus.initialization(train,valid,String.valueOf(firstTarget)+"-"+String.valueOf(lastTarget),false,false); // for the simulated annealing, this one has to be done.
		
	 	// regression single thread
	 	//ClusWrapper.clus = null;
		//ClusWrapper.initialization(train,valid, String.valueOf(firstTarget)+"-"+String.valueOf(lastTarget),false,false);
		
	 	myMeasures measures = objectClus.evaluateIndividual(solution,true);
		

		long fTime = System.currentTimeMillis();
		System.out.println("Tempo de execução = "+(fTime-iTime)/1000);
		
		double mae[] = new double[3];
		double mse[] = new double[3];
		double rmse[] = new double[3];
		double wrmse[] = new double[3];

		mae[0] = measures.getMAE()[0]; mse[0] = measures.getMSE()[0]; rmse[0] = measures.getRMSE()[0]; wrmse[0] = measures.getWRMSE()[0];
		mae[1] = measures.getMAE()[1]; mse[1] = measures.getMSE()[1]; rmse[1] = measures.getRMSE()[1]; wrmse[1] = measures.getWRMSE()[1];
		
		double auroc[] = new double[3];
		double auprc[] = new double[3];
		auroc[0] = measures.getAUROC()[0]; auprc[0] = measures.getAUPRC()[0]; 
		auroc[1] = measures.getAUROC()[1]; auprc[1] = measures.getAUPRC()[1];
		
		// Run this as your fitness function. You can choose between MAE, MSE, RMSE or WRMSE as performance measure
		
		System.out.println("\nValidation errors:: ");
		System.out.println("MAE,MSE,RMSE");		
		System.out.println(mae[1] +", "+ mse[1] +", "+ rmse[1]);
		
		System.out.println("\nTraining errors: ");
		System.out.println("MAE,MSE,RMSE");		
		System.out.println(mae[0] +", "+ mse[0] +", "+ rmse[0]);
		
		/*
		System.out.println("\nValidation errors:: ");
		System.out.println("AUROC,AUPRC,RMSE");		
		System.out.println(auroc[1] +", "+ auprc[1] +", "+ rmse[1]);
		
		System.out.println("\nTraining errors: ");
		System.out.println("AUROC,AUPRC,RMSE");		
		System.out.println(auroc[0] +", "+ auprc[0] +", "+ rmse[0]);
		*/
	}
}
