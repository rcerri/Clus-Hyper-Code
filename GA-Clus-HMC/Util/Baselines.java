package Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import clus.util.ClusException;
import Util.ClusWrapper;     // you need to import the ClusWrapper
import Util.myMeasures;

/** 
 * Example of use of ClusWrapper - version for regression and classification
 * @author isaact
 *
 */
public class Baselines {



	public static void main(String[] args) throws IOException, ClusException {
		
		if(args.length!=6){
			System.err.println("The number of parameters is 6:  <training dataset> <test dataset> <attributes> <numOutputs> <classification|regression> <DecisionTree|RandomForest>");
			System.exit(1);
		}
		
        boolean classification = false;
        if (args[4].equalsIgnoreCase("classification")) {
            classification = true;
        }
        
        boolean forest = false;
        if (args[5].equalsIgnoreCase("RandomForest")) {
        	forest = true;
        }
        
		long timeStart=System.nanoTime();
		String train = args[0]; //
		String test = args[1]; // 
		
		
   	    
		
		int numOutputs= Integer.parseInt(args[3]);
		
		// Generate potential solutions:

		int global[] = new int[numOutputs]; // k is usable as an array of indexes into a set.
		int local[] = new int[numOutputs];
		
     	for (int i=0; i<numOutputs; i++){
			global[i]=1;
			local[i]=i+1;			
		}


     	
     	ClusWrapper.initialization(train, test, args[2],forest,classification); 
     	
     	/*
     	// try a single classifier in Clus.
     	//ClusWrapper.initialization(train, test,"74",false,true); 	//
     	InputStream configFile = ClusWrapper.createInputStreamBaseConfigFileForClassification("74",true);
	
		args= new String[1]; args[0] = "config.s"; 
		
		// Run the classifier
		ClusWrapper.runClassifier(args, configFile);
		
		
     	
     	// ClusWrapper.initialization(train, test,"78",false,true); 	//
		// ClusWrapper.disable="73-77";
        configFile = ClusWrapper.createInputStreamBaseConfigFileForClassification("78",true);
	
		args= new String[1]; args[0] = "config.s"; 
		
		// Run the classifier
		ClusWrapper.runClassifier(args, configFile);
		
     	System.exit(1);
		*/
     	
     	if(classification){
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
			
		
     	}else{
     		
    		myMeasures measure = ClusWrapper.evaluateIndividual(global,true);
    		
    		
    		System.out.println("\n***********GLOBAL************ ");
    		
    		System.out.println("MAE-tra: "+ measure.getMAE()[0]); 
            System.out.println("MAE-tst: "+ measure.getMAE()[1]); 
            
    		System.out.println("MSE-tra: "+ measure.getMSE()[0]); 
    		System.out.println("MSE-tst "+ measure.getMSE()[1]); 
    		

    		System.out.println("\nRunTimeGlobal: "+(System.nanoTime()-timeStart)/1e9);

    		timeStart=System.nanoTime();
    		
    		myMeasures measure2 = ClusWrapper.evaluateIndividual(local,true);
    		
    		System.out.println("\n***********LOCAL************ ");
    		
    		System.out.println("MAE-tra: "+ measure2.getMAE()[0]); 
            System.out.println("MAE-tst: "+ measure2.getMAE()[1]); 
            
    		System.out.println("MSE-tra: "+ measure2.getMSE()[0]); 
    		System.out.println("MSE-tst "+ measure2.getMSE()[1]); 
    		
    		
    		long timeEnd=System.nanoTime();
    		
    		System.out.println("\nRunTimeLocal: "+(timeEnd-timeStart)/1e9);
    		
     	}
	}
}
