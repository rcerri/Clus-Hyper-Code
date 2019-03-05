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
public class OracleRanking {


	public static boolean nextPartition(int []kappa, int []M){
		
		int n = kappa.length;
		
		for (int i = n-1; i > 0; --i)
		    if (kappa[i] <= M[i-1]) {
		      ++kappa[i];

		      int new_max = Integer.max(M[i], kappa[i]);
		      M[i] = new_max;
		      for (int j = i + 1; j < n; ++j) {
		        kappa[j] = kappa[0];
		        M[j] = new_max;
		      }

			    // integrityCheck();
		      return true;
		    }
		  
		return false;
	}
	
	public static String printPartition (int [] partition) {
		String part = "";
		
		for (int i=0; i< partition.length-1; i++) {
			part+= Integer.toString(partition[i]) + ",";
		}
		
		return part += Integer.toString(partition[partition.length-1]);
	}
	
	public static void main(String[] args) throws IOException, ClusException {
		
	       if (args.length != 5) {
	            System.err.println("The number of parameters is 5:  <training dataset> <test dataset> <attributes> <numOutputs> <classification|regression>");
	            System.exit(1);
	        }
	        boolean classification = false;
	        if (args[4].equalsIgnoreCase("classification")) {
	            classification = true;
	        }
	        long timeStart = System.nanoTime();
	        String train = args[0];
	        String test = args[1];
	        ClusWrapper example = new ClusWrapper();
	        ClusWrapper.initialization(train, test, args[2], true, classification); // define true or false to use RF or DecisionTree
	        int numOutputs = Integer.parseInt(args[3]);
	        int cont = 1;
	        
	        int[] kappa = new int[numOutputs];
	        int[] M = new int[numOutputs];
	        int i = 0;
	        while (i < numOutputs) {
	            kappa[i] = 1;
	            M[i] = 1;
	            ++i;
	        }
	        
	        myMeasures measure = classification ? ClusWrapper.evaluateIndividualClassification(kappa, true) : ClusWrapper.evaluateIndividual(kappa, true);
	        
	        
	        // global and local as baselines
			int global[] = new int[numOutputs]; 
			int local[] = new int[numOutputs];
			
	     	for (i=0; i<numOutputs; i++){
				global[i]=1;
				local[i]=i+1;			
			}

	     	
	        measure = classification ? ClusWrapper.evaluateIndividualClassification(global, true) : ClusWrapper.evaluateIndividual(global, true);
  
	        double global_trainAUCROC = 0, global_testAUCROC=0, global_trainAUPRC=0, global_testAUPRC=0;
	        double global_trainMAE=0, global_testMAE=0, global_trainMSE=0, global_testMSE=0;
	        
	        if(classification) {
	        	global_trainAUCROC = measure.getAUROC()[0];
	        	global_testAUCROC = measure.getAUROC()[1];
	        	
	        	global_trainAUPRC= measure.getAUPRC()[0];
	        	global_testAUPRC  = measure.getAUPRC()[1];
	        }else {
	        	global_trainMAE = measure.getMAE()[0];
	        	global_testMAE = measure.getMAE()[1];
	        	
	        	global_trainMSE= measure.getMSE()[0];
	        	global_testMSE =  measure.getMSE()[1];
	        	
	        }
	        
	        measure = classification ? ClusWrapper.evaluateIndividualClassification(local, true) : ClusWrapper.evaluateIndividual(local, true);

	        
	        double local_trainAUCROC=0, local_testAUCROC=0, local_trainAUPRC=0, local_testAUPRC=0;
	        double local_trainMAE=0, local_testMAE=0, local_trainMSE=0, local_testMSE=0;
	        
	        if(classification) {
	        	local_trainAUCROC = measure.getAUROC()[0];
	        	local_testAUCROC = measure.getAUROC()[1];
	        	
	        	local_trainAUPRC= measure.getAUPRC()[0];
	        	local_testAUPRC  = measure.getAUPRC()[1];
	        }else {
	        	local_trainMAE = measure.getMAE()[0];
	        	local_testMAE = measure.getMAE()[1];
	        	
	        	local_trainMSE= measure.getMSE()[0];
	        	local_testMSE =  measure.getMSE()[1];
	        	
	        }	        

	        String BetterThanGlobal_trainAUCROC = "",BetterThanGlobal_testAUCROC ="", BetterThanGlobal_trainAUPRC="", BetterThanGlobal_testAUPRC="";
	        String BetterThanLocal_trainAUCROC="",BetterThanLocal_testAUCROC="", BetterThanLocal_trainAUPRC="", BetterThanLocal_testAUPRC="";
	        String BetterThanGlobal_trainMAE="",BetterThanGlobal_testMAE="", BetterThanGlobal_trainMSE="", BetterThanGlobal_testMSE="";
	        String BetterThanLocal_trainMAE="",BetterThanLocal_testMAE="", BetterThanLocal_trainMSE="", BetterThanLocal_testMSE="";
	        

	        while (BruteForceAdhoc.nextPartition(kappa, M)) {
	            measure = classification ? ClusWrapper.evaluateIndividualClassification(kappa, true) : ClusWrapper.evaluateIndividual(kappa, true);
            	
	            String individual = printPartition(kappa);

            	
	            if (classification) {
	            	// Better than global?
	                if (measure.getAUROC()[0] > global_trainAUCROC) {
	                	BetterThanGlobal_trainAUCROC += individual + ": " +measure.getAUROC()[0] + "\n";
	                }
	                if (measure.getAUROC()[1] > global_testAUCROC) {
	                	BetterThanGlobal_testAUCROC += individual +": " + measure.getAUROC()[1]+ "\n";;
	                }
	                if (measure.getAUPRC()[0] > global_trainAUPRC) {
	                	BetterThanGlobal_trainAUPRC += individual + ": " +measure.getAUPRC()[0]+ "\n";;
	                }
	                if (measure.getAUPRC()[1] > global_testAUPRC) {
	                	BetterThanGlobal_testAUPRC += individual + ": " +measure.getAUPRC()[1]+ "\n";;
	                }
	                
	                // better than local?
	                if (measure.getAUROC()[0] > local_trainAUCROC) {
	                	BetterThanLocal_trainAUCROC += individual + ": " +measure.getAUROC()[0]+ "\n";;
	                }
	                if (measure.getAUROC()[1] > local_testAUCROC) {
	                	BetterThanLocal_testAUCROC += individual + ": " +measure.getAUROC()[1]+ "\n";;
	                }
	                if (measure.getAUPRC()[0] > local_trainAUPRC) {
	                	BetterThanLocal_trainAUPRC += individual + ": " +measure.getAUPRC()[0]+ "\n";;
	                }
	                if (measure.getAUPRC()[1] > local_testAUPRC) {
	                	BetterThanLocal_testAUPRC += individual + ": " +measure.getAUPRC()[1]+ "\n";;
	                }
	                
	                
	            } else {

	                if (measure.getMAE()[0] > global_trainMAE) {
	                	BetterThanGlobal_trainMAE += individual +": " + measure.getMAE()[0]+ "\n";;
	                }
	                if (measure.getMAE()[1] > global_testMAE) {
	                	BetterThanGlobal_testMAE += individual + ": " +measure.getMAE()[1]+ "\n";;
	                }
	                if (measure.getMSE()[0] > global_trainMSE) {
	                	BetterThanGlobal_trainMSE += individual + ": " +measure.getMSE()[0]+ "\n";;
	                }
	                if (measure.getMSE()[1] > global_testMSE) {
	                	BetterThanGlobal_testMSE += individual + ": " +measure.getMSE()[1]+ "\n";;
	                }
	                
	                // better than local?
	                if (measure.getMAE()[0] > local_trainMAE) {
	                	BetterThanLocal_trainMAE += individual + ": " +measure.getMAE()[0]+ "\n";;
	                }
	                if (measure.getMAE()[1] > local_testMAE) {
	                	BetterThanLocal_testMAE += individual + ": " +measure.getMAE()[1]+ "\n";;
	                }
	                if (measure.getMSE()[0] > local_trainMSE) {
	                	BetterThanLocal_trainMSE += individual + ": " +measure.getMSE()[0]+ "\n";;
	                }
	                if (measure.getMSE()[1] > local_testMSE) {
	                	BetterThanLocal_testMSE += individual + ": " +measure.getMSE()[1]+ "\n";;
	                }
		            
	            }
	            ++cont;
	        }
	        long timeEnd = System.nanoTime();
	        
	        
	        String dataNameVector[] = train.split("/");
	        String dataName = "RankingOutput//" + dataNameVector[dataNameVector.length-1];

	        System.out.println(dataName);
	        
	        if (classification) {

	        	Fichero.escribeFichero(dataName+"//BetterThanGlobal_trainAUCROC.out", BetterThanGlobal_trainAUCROC);
	        	Fichero.escribeFichero(dataName+"//BetterThanGlobal_testAUCROC.out", BetterThanGlobal_testAUCROC);
	        	Fichero.escribeFichero(dataName+"//BetterThanGlobal_trainAUPRC.out", BetterThanGlobal_trainAUPRC);
	        	Fichero.escribeFichero(dataName+"//BetterThanGlobal_testAUPRC.out", BetterThanGlobal_testAUPRC);
	        	Fichero.escribeFichero(dataName+"//BetterThanLocal_trainAUCROC.out", BetterThanLocal_trainAUCROC);
	        	Fichero.escribeFichero(dataName+"//BetterThanLocal_testAUCROC.out", BetterThanLocal_testAUCROC);
	        	Fichero.escribeFichero(dataName+"//BetterThanLocal_trainAUPRC.out", BetterThanLocal_trainAUPRC);
	        	Fichero.escribeFichero(dataName+"//BetterThanLocal_testAUPRC.out", BetterThanLocal_testAUPRC);

	        } else {
	        	Fichero.escribeFichero(dataName+"//BetterThanGlobal_trainMAE.out", BetterThanGlobal_trainMAE);
	        	Fichero.escribeFichero(dataName+"//BetterThanGlobal_testMAE.out", BetterThanGlobal_testMAE);
	        	Fichero.escribeFichero(dataName+"//BetterThanGlobal_trainMSE.out", BetterThanGlobal_trainMSE);
	        	Fichero.escribeFichero(dataName+"//BetterThanGlobal_testMSE.out", BetterThanGlobal_testMSE);
	        	Fichero.escribeFichero(dataName+"//BetterThanLocal_trainMAE.out", BetterThanLocal_trainMAE);
	        	Fichero.escribeFichero(dataName+"//BetterThanLocal_testMAE.out", BetterThanLocal_testMAE);
	        	Fichero.escribeFichero(dataName+"//BetterThanLocal_trainMSE.out", BetterThanLocal_trainMSE);
	        	Fichero.escribeFichero(dataName+"//BetterThanLocal_testMSE.out", BetterThanLocal_testMSE);
	           
	        }
	        
	      //  System.out.println("\nMy hash table has : " + ClusWrapper.PreviousSolutions.size());
	        System.out.println("\nnumber of individuals evaluated: " + cont);
	        System.out.println("RunTime: " + (double)(timeEnd - timeStart) / 1.0E9);
	    }
}



