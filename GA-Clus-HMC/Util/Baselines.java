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
public class Baselines {



	public static void main(String[] args) throws IOException, ClusException {
		
		if(args.length!=4){
			System.err.println("The number of parameters is 4:  <training dataset> <test dataset> <attributes> <numOutputs>");
			System.exit(1);
		}
		
		
		long timeStart=System.nanoTime();
		String train = args[0]; //
		String test = args[1]; // 
		
		
   	    ClusWrapper.initialization(train, test, args[2],false,true); 	
		
		int numOutputs= Integer.parseInt(args[3]);
		
		// Generate potential solutions:

		int global[] = new int[numOutputs]; // k is usable as an array of indexes into a set.
		int local[] = new int[numOutputs];
		
     	for (int i=0; i<numOutputs; i++){
			global[i]=1;
			local[i]=i+1;			
		}


		myMeasures measure = ClusWrapper.evaluateIndividualClassification(global,true);
		
		
		System.out.println("\n***********GLOBAL************ ");
		
		System.out.println("AUROC-tra: "+ measure.getAUROC()[0]); 
        System.out.println("AUROC-tst: "+ measure.getAUROC()[1]); 
        
		System.out.println("AUPRC-tra: "+ measure.getAUPRC()[0]); 
		System.out.println("AUPRC-tst "+ measure.getAUPRC()[1]); 
		

		System.out.println("\nRunTimeGlobal: "+(System.nanoTime()-timeStart)/1e9);

		timeStart=System.nanoTime();
		
		myMeasures measure2 = ClusWrapper.evaluateIndividualClassification(local,true);
		
		System.out.println("\n***********LOCAL************ ");
		
		System.out.println("AUROC-tra: "+ measure2.getAUROC()[0]); 
        System.out.println("AUROC-tst: "+ measure2.getAUROC()[1]); 

		System.out.println("AUPRC-tra: "+ measure2.getAUPRC()[0]); 
		System.out.println("AUPRC-tst "+ measure2.getAUPRC()[1]); 
		
		
		long timeEnd=System.nanoTime();
		
		System.out.println("\nRunTimeLocal: "+(timeEnd-timeStart)/1e9);
	}
}
