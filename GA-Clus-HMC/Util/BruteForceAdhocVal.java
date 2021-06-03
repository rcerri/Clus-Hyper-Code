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
public class BruteForceAdhocVal {


	
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
	
	
	public static int count (int [] partition) {
		int max = Integer.MIN_VALUE;
		
		for (int i=0; i< partition.length; i++) {
			if(partition[i]>max)
				max = partition[i];
		}
		
		return max;
	}
	
	
	public static void main(String[] args) throws IOException, ClusException {
		
	       if (args.length != 8) {
	            System.err.println("The number of parameters is 8:  <training dataset> <validation dataset> <trainval data> <test dataset> <attributes> <numOutputs> <classification|regression> <dt|rf>");
	            System.exit(1);
	        }
	        boolean classification = false;
	        if (args[6].equalsIgnoreCase("classification")) {
	            classification = true;
	        }
	        
	        
	        String train = args[0];
	        String validation = args[1];
	        String trainval = args[2];
	        String test = args[3];
	        
	        ClusWrapper example = new ClusWrapper();
	        
	        long timeStart=System.nanoTime();
	        
	        boolean CreateForest = false;
	        
	        if (args[7].equalsIgnoreCase("rf")) {
	        	CreateForest = true;
	        }
	        
	        
	        ClusWrapper.initialization(train, validation, args[4], CreateForest, classification);
	        
	
	        
	        int numOutputs = Integer.parseInt(args[5]);
	        int cont = 1;
	        double minimumTRAMAE = Double.MAX_VALUE;
	        double TestMAEBestTrainingPartition = -1.0;
	        int[] betterMAETRAIndividual = new int[numOutputs];
	        double minimumTSTMAE = Double.MAX_VALUE;
	        double TrainingMAEBestTrainingPartition = -1.0;
	        int[] betterMAETSTIndividual = new int[numOutputs];
	        
	        double minimumTRAMSE = Double.MAX_VALUE;
	        double TestMSEBestTrainingPartition = -1.0;
	        int[] betterMSETRAIndividual = new int[numOutputs];
	        double minimumTSTMSE = Double.MAX_VALUE;
	        double TrainingMSEBestTrainingPartition = -1.0;
	        int[] betterMSETSTIndividual = new int[numOutputs];
	        
	        double minimumTRARMSE = Double.MAX_VALUE;
	        double TestRMSEBestTrainingPartition = -1.0;
	        int[] betterRMSETRAIndividual = new int[numOutputs];
	        double minimumTSTRMSE = Double.MAX_VALUE;
	        double TrainingRMSEBestTrainingPartition = -1.0;
	        int[] betterRMSETSTIndividual = new int[numOutputs];
	        
	        double maxTRA_AUCROC = Double.MIN_VALUE;
	        double TestAUCROCBestTrainingPartition = -1.0;
	        int[] betterTRA_AUCROCIndividual = new int[numOutputs];
	        double maxTST_AUCROC = Double.MIN_VALUE;
	        double TrainingAUCROCBestTrainingPartition = -1.0;
	        int[] betterTST_AUCROCIndividual = new int[numOutputs];
	        double maxTRA_AUPRC = Double.MIN_VALUE;
	        double TestAUPRCBestTrainingPartition = -1.0;
	        int[] betterTRA_AUPRCIndividual = new int[numOutputs];
	        double maxTST_AUPRC = Double.MIN_VALUE;
	        double TrainingAUPRCBestTrainingPartition = -1.0;
	        int[] betterTST_AUPRCIndividual = new int[numOutputs];
	        int[] kappa = new int[numOutputs];
	        int[] M = new int[numOutputs];
	        
	        
	        for (int i=0; i<numOutputs; i++){
	        	kappa[i]=1;
	        	M[i]=1;			
	        }
	        
	        // I am printing the header for Exhaustive search... training and validation metrics.
	        
	        if(classification)
	        	System.out.println("Partitions, AUCROC train, AUCROC val, AUPRC train, AUPRC val");
	        else
	        	System.out.println("Partitions, MAE train, MAE val, MSE train, MSE val");
	        
	        	        
	        myMeasures measure = classification ? ClusWrapper.evaluateIndividualClassification(kappa, true) : ClusWrapper.evaluateIndividual(kappa, true);
	        
	        String individual = printPartition(kappa);
	        
	        System.out.print("'["+individual+"]',");

	        if (classification) {
	        	System.out.println(measure.getAUROC()[0]+","+measure.getAUROC()[1]+","+measure.getAUPRC()[0]+","+measure.getAUPRC()[1]);
	        	
	            if (measure.getAUROC()[0] > maxTRA_AUCROC) {
	                maxTRA_AUCROC = measure.getAUROC()[0];
	                TestAUCROCBestTrainingPartition = measure.getAUROC()[1];
	                betterTRA_AUCROCIndividual = (int[])kappa.clone();
	            }
	            if (measure.getAUROC()[1] > maxTST_AUCROC) {
	                maxTST_AUCROC = measure.getAUROC()[1];
	                TrainingAUCROCBestTrainingPartition = measure.getAUROC()[0];
	                betterTST_AUCROCIndividual = (int[])kappa.clone();
	            }
	            if (measure.getAUPRC()[0] > maxTRA_AUPRC) {
	                maxTRA_AUPRC = measure.getAUPRC()[0];
	                TestAUPRCBestTrainingPartition = measure.getAUPRC()[1];
	                betterTRA_AUPRCIndividual = (int[])kappa.clone();
	            }
	            if (measure.getAUPRC()[1] > maxTST_AUPRC) {
	                maxTST_AUPRC = measure.getAUPRC()[1];
	                TrainingAUPRCBestTrainingPartition = measure.getAUPRC()[0];
	                betterTST_AUPRCIndividual = (int[])kappa.clone();
	            }
	        } else {
	        	
	        	System.out.println(measure.getMAE()[0]+","+measure.getMAE()[1]+","+measure.getMSE()[0]+","+measure.getMSE()[1]);
	        	
	            if (measure.getMAE()[0] < minimumTRAMAE) {
	                minimumTRAMAE = measure.getMAE()[0];
	                TestMAEBestTrainingPartition = measure.getMAE()[1];
	                betterMAETRAIndividual = (int[])kappa.clone();
	            }
	            if (measure.getMAE()[1] < minimumTSTMAE) {
	                minimumTSTMAE = measure.getMAE()[1];
	                TrainingMAEBestTrainingPartition = measure.getMAE()[0];
	                betterMAETSTIndividual = (int[])kappa.clone();
	            }
	        }
	        

	        
	        
	        while (nextPartition(kappa, M)) {
	        	
	        	/*
	        	 			for(int j=0; j<numOutputs;j++){
	        	 				System.out.print(kappa[j]+" ");
	        	 			}
	        	 			System.out.println("-  "+cont);
	        	*/
	        	
	        	
	            measure = classification ? ClusWrapper.evaluateIndividualClassification(kappa, true) : ClusWrapper.evaluateIndividual(kappa, true);
	            
	            individual = printPartition(kappa);

            	System.out.print("'["+individual+"]',");

            	
	            if (classification) {
	            	System.out.println(measure.getAUROC()[0]+","+measure.getAUROC()[1]+","+measure.getAUPRC()[0]+","+measure.getAUPRC()[1]);
	            	
	                if (measure.getAUROC()[0] > maxTRA_AUCROC) {
	                    maxTRA_AUCROC = measure.getAUROC()[0];
	                    TestAUCROCBestTrainingPartition = measure.getAUROC()[1];
	                    betterTRA_AUCROCIndividual = (int[])kappa.clone();
	                }
	                if (measure.getAUROC()[1] > maxTST_AUCROC) {
	                    maxTST_AUCROC = measure.getAUROC()[1];
	                    TrainingAUCROCBestTrainingPartition = measure.getAUROC()[0];
	                    betterTST_AUCROCIndividual = (int[])kappa.clone();
	                }
	                if (measure.getAUPRC()[0] > maxTRA_AUPRC) {
	                    maxTRA_AUPRC = measure.getAUPRC()[0];
	                    TestAUPRCBestTrainingPartition = measure.getAUPRC()[1];
	                    betterTRA_AUPRCIndividual = (int[])kappa.clone();
	                }
	                if (measure.getAUPRC()[1] > maxTST_AUPRC) {
	                    maxTST_AUPRC = measure.getAUPRC()[1];
	                    TrainingAUPRCBestTrainingPartition = measure.getAUPRC()[0];
	                    betterTST_AUPRCIndividual = (int[])kappa.clone();
	                }
	            } else {
	            	
	            	System.out.println(measure.getMAE()[0]+","+measure.getMAE()[1]+","+measure.getMSE()[0]+","+measure.getMSE()[1]);
	            	
		            if (measure.getMAE()[0] < minimumTRAMAE) {
		                minimumTRAMAE = measure.getMAE()[0];
		                TestMAEBestTrainingPartition = measure.getMAE()[1];
		                betterMAETRAIndividual = (int[])kappa.clone();
		            }
		            if (measure.getMAE()[1] < minimumTSTMAE) {
		                minimumTSTMAE = measure.getMAE()[1];
		                TrainingMAEBestTrainingPartition = measure.getMAE()[0];
		                betterMAETSTIndividual = (int[])kappa.clone();
		            }
		            
		            if (measure.getMSE()[0] < minimumTRAMSE) {
		                minimumTRAMSE = measure.getMSE()[0];
		                TestMSEBestTrainingPartition = measure.getMSE()[1];
		                betterMSETRAIndividual = (int[])kappa.clone();
		            }
		            if (measure.getMSE()[1] < minimumTSTMSE) {
		                minimumTSTMSE = measure.getMSE()[1];
		                TrainingMSEBestTrainingPartition = measure.getMSE()[0];
		                betterMSETSTIndividual = (int[])kappa.clone();
		            }
		            
		            if (measure.getRMSE()[0] < minimumTRARMSE) {
		                minimumTRARMSE = measure.getRMSE()[0];
		                TestRMSEBestTrainingPartition = measure.getRMSE()[1];
		                betterRMSETRAIndividual = (int[])kappa.clone();
		            }
		            if (measure.getRMSE()[1] < minimumTSTRMSE) {
		                minimumTSTRMSE = measure.getRMSE()[1];
		                TrainingRMSEBestTrainingPartition = measure.getRMSE()[0];
		                betterRMSETSTIndividual = (int[])kappa.clone();
		            }
		            
	            }
	            cont++;
	        }
	        
	        
	        System.out.println("\nEND EXHAUSTIVE SEARCH \n");
	   
	        /*
	        if (classification) {
	            System.out.println("\n****************AUCROC*************\n");
	            System.out.println("The best training  AUCROC individual is: ");
	            int i2 = 0;
	            while (i2 < numOutputs) {
	                System.out.print(String.valueOf(betterTRA_AUCROCIndividual[i2]) + " ");
	                ++i2;
	            }
	            System.out.print(";  AUCROC: " + maxTRA_AUCROC + "; test AUCROC: " + TestAUCROCBestTrainingPartition);
	            System.out.println("\n\nThe best test AUCROC individual is: ");
	            i2 = 0;
	            while (i2 < numOutputs) {
	                System.out.print(String.valueOf(betterTST_AUCROCIndividual[i2]) + " ");
	                ++i2;
	            }
	            System.out.print(";  AUCROC: " + maxTST_AUCROC + "; training AUCROC: " + TrainingAUCROCBestTrainingPartition);
	            System.out.println("\n\n\n****************AUPRC*************\n");
	            System.out.println("The best training  AUPRC individual is: ");
	            i2 = 0;
	            while (i2 < numOutputs) {
	                System.out.print(String.valueOf(betterTRA_AUPRCIndividual[i2]) + " ");
	                ++i2;
	            }
	            System.out.print(";  AUPRC: " + maxTRA_AUPRC + "; test AUPRC: " + TestAUPRCBestTrainingPartition);
	            System.out.println("\n\nThe best test AUPRC individual is: ");
	            i2 = 0;
	            while (i2 < numOutputs) {
	                System.out.print(String.valueOf(betterTST_AUPRCIndividual[i2]) + " ");
	                ++i2;
	            }
	            System.out.print(";  AUPRC: " + maxTST_AUPRC + "; training AUPRC: " + TrainingAUPRCBestTrainingPartition);
	        } else {
	        	
	        	System.out.println("\n\n\n****************MAE*************\n");
	        	
	            System.out.println("\n\nThe best training individual is: ");
	            int i3 = 0;
	            while (i3 < numOutputs) {
	                System.out.print(String.valueOf(betterMAETRAIndividual[i3]) + " ");
	                ++i3;
	            }
	            System.out.print(";  MAE: " + minimumTRAMAE + "; test MAE: " + TestMAEBestTrainingPartition);
	            System.out.println("\n\nThe best test individual is: ");
	            i3 = 0;
	            while (i3 < numOutputs) {
	                System.out.print(String.valueOf(betterMAETSTIndividual[i3]) + " ");
	                ++i3;
	            }
	            System.out.print(";  MAE: " + minimumTSTMAE + "; training MAE: " + TrainingMAEBestTrainingPartition);
	            
	        	System.out.println("\n\n\n****************MSE*************\n");
	        	
	            System.out.println("\n\nThe best training individual is: ");
	           
	            i3 = 0;
	            while (i3 < numOutputs) {
	                System.out.print(String.valueOf(betterMSETRAIndividual[i3]) + " ");
	                ++i3;
	            }
	            System.out.print(";  MSE: " + minimumTRAMSE + "; test MSE: " + TestMSEBestTrainingPartition);
	            System.out.println("\n\nThe best test individual is: ");
	            i3 = 0;
	            while (i3 < numOutputs) {
	                System.out.print(String.valueOf(betterMSETSTIndividual[i3]) + " ");
	                ++i3;
	            }
	            System.out.print(";  MSE: " + minimumTSTMSE + "; training MSE: " + TrainingMSEBestTrainingPartition);
	            
	        	System.out.println("\n\n\n****************RMSE*************\n");
	        	
	            System.out.println("\n\nThe best training individual is: ");
	            
	            i3 = 0;
	            while (i3 < numOutputs) {
	                System.out.print(String.valueOf(betterRMSETRAIndividual[i3]) + " ");
	                ++i3;
	            }
	            System.out.print(";  RMSE: " + minimumTRARMSE + "; test RMSE: " + TestRMSEBestTrainingPartition);
	            System.out.println("\n\nThe best test individual is: ");
	            i3 = 0;
	            while (i3 < numOutputs) {
	                System.out.print(String.valueOf(betterRMSETSTIndividual[i3]) + " ");
	                ++i3;
	            }
	            System.out.print(";  RMSE: " + minimumTSTRMSE + "; training RMSE: " + TrainingRMSEBestTrainingPartition);
	            
	        }
	      //  System.out.println("\nMy hash table has : " + ClusWrapper.PreviousSolutions.size());
	        
	        */
	        
	        long timeEnd = System.nanoTime();
	        //System.out.println("\n\nnumber of individuals evaluated: " + cont);
	        //System.out.println("\nRunTime BruteForce (TRA vs. VAL): " + (double)(timeEnd - timeStart) / 1.0E9);
	        
	        
	        // Final classification phase:
	        
	        timeStart=System.nanoTime();
			ClusWrapper.clus=null;

	        ClusWrapper.initialization(trainval, test, args[4], CreateForest, classification);

	        // global and local as baselines
			int global[] = new int[numOutputs]; 
			int local[] = new int[numOutputs];
			
	     	for (int i=0; i<numOutputs; i++){
				global[i]=1;
				local[i]=i+1;			
			}
	     	
	        myMeasures measureD = classification ? ClusWrapper.evaluateIndividualClassification(global, true) : ClusWrapper.evaluateIndividual(global, true);

	        
	        if(classification)
	        	System.out.println("Best Partitions AUCROC, NumPartitions, AUCROC test, Best Partitions AUPRC, NumPartitions, AUPRC test");
	        else
	        	System.out.println("Best Partitions MAE, NumPartitions, MAE test, Best Partitions MSE, NumPartitions, MSE test");
	        
	        
	        
	        String individual1, individual2;
	        myMeasures measure1, measure2;
	        
	        int count1, count2;
	        
	        if (classification) {
	             individual1 = printPartition(betterTST_AUCROCIndividual);
            	 individual2 = printPartition(betterTST_AUPRCIndividual);

	        	 measure1 = ClusWrapper.evaluateIndividualClassification(betterTST_AUCROCIndividual, true);
	        	 measure2 = ClusWrapper.evaluateIndividualClassification(betterTST_AUPRCIndividual, true);
	        
	        	 count1 = count(betterTST_AUCROCIndividual);
	        	 count2 = count(betterTST_AUPRCIndividual);
	        	 
	        	 System.out.println("'["+individual1+"]'"+","+count1+","+measure1.getAUROC()[1]+","+"'["+individual2+"]'"+","+count2+","+measure2.getAUPRC()[1]);
	        	 
	        	 //System.out.println("\nAUROC Test results with best AUPRC individual is: "+measure.getAUROC()[1]);
	        	 // System.out.println("\nAUPRC Test results with best AUPRC individual is: "+measure.getAUPRC()[1]);
	        	        	 
	        	 // System.out.println("\nAUROC Test results with best AUROC individual is: "+measure.getAUROC()[1]);
	        	 // System.out.println("\nAUPRC Test results with best AUROC individual is: "+measure.getAUPRC()[1]);
	        	
	        }else{
	             individual1 = printPartition(betterMAETSTIndividual);
           	     individual2 = printPartition(betterMSETSTIndividual);

	        	 measure1 = ClusWrapper.evaluateIndividual(betterMAETSTIndividual, true);
	        	 measure2 = ClusWrapper.evaluateIndividual(betterMSETSTIndividual, true);
	        
	        	 count1 = count(betterMAETSTIndividual);
	        	 count2 = count(betterMSETSTIndividual);
	        	 
	        	 
	        	 System.out.println("'["+individual1+"]'"+","+count1+","+measure1.getMAE()[1]+","+"'["+individual2+"]'"+","+count2+","+measure2.getMSE()[1]);
	        	
	        	
	        	/*
	        	 measure =ClusWrapper.evaluateIndividual(betterMAETSTIndividual, true);
	        	 
	        	 System.out.println("\nMAE Test results with best MAE individual is: "+measure.getMAE()[1]);
	        	 System.out.println("\nMSE Test results with best MAE individual is: "+measure.getMSE()[1]);
	        	 System.out.println("\nRMSE Test results with best MAE individual is: "+measure.getRMSE()[1]);
	        	 
	        	 measure =ClusWrapper.evaluateIndividual(betterMSETSTIndividual, true);
	        	 
	        	 System.out.println("\nMAE Test results with best MSE individual is: "+measure.getMAE()[1]);
	        	 System.out.println("\nMSE Test results with best MSE individual is: "+measure.getMSE()[1]);
	        	 System.out.println("\nRMSE Test results with best MSE individual is: "+measure.getRMSE()[1]);
	        	
	        
	        	 measure =ClusWrapper.evaluateIndividual(betterRMSETSTIndividual, true);
	        	 
	        	 System.out.println("\nMAE Test results with best RMSE individual is: "+measure.getMAE()[1]);
	        	 System.out.println("\nMSE Test results with best RMSE individual is: "+measure.getMSE()[1]);
	        	 System.out.println("\nRMSE Test results with best RMSE individual is: "+measure.getRMSE()[1]);
	        */	 
	        	 
	        }
	        
	        timeEnd = System.nanoTime();

	        
	        //System.out.println("\nRunTime (TRAVAL vs. TEST): " + (double)(timeEnd - timeStart) / 1.0E9);

	        
	        
	    }
}



/*
 * 	/*
	 *
	 *This version merely uses Strings for everything, its comptuation becomes pretty slow.
	public static String generatePotentialSolutions(int numPartitions){
		
		String[] partitions = new String[numPartitions+1];
		
		partitions[0] ="1";
		partitions[1] ="1";		
				
		for (int i=2; i<=numPartitions; i++){
			partitions[i] = "";
			
			//take all elements from partition with i-1 elements, and add new cluster with single element
			String parts[] = partitions[i-1].split(";");
			for(int j=0; j< parts.length; j++){
				partitions[i] += parts[j]+" "+i +";";
			}
			
			//take all elements from partition with i-1 elements, and add new element to each cluster
			for(int j=0; j< parts.length; j++){
				String clusters[] = parts[j].split(" ");
				
				for(int cl=0; cl<clusters.length; cl++){
					
					String old = clusters[cl];
					clusters[cl]= clusters[cl] + "," + i;
					String newPart="";
					for(int m=0; m<clusters.length-1; m++){
						newPart+=clusters[m]+" ";
					}
					newPart+=clusters[clusters.length-1];
					
					clusters[cl]= old;
					 partitions[i] += newPart+";";

				}
			}
		}		
		
		return partitions[numPartitions]; //.split(";");
	}
	
	
	 * max partitions  = 15
	 * @param numPartitions
	 * @return
	 
	public static String[] generatePotentialSolutionsVector(int numPartitions){
		
		String[][] partitions = new String[2][]; // only the last two is enough.
		
		partitions[0] = new String[1]; // previous
		
		partitions[0][0] = "1"; 

		int size[] ={1, 1, 2, 5, 15, 52, 203, 877, 4140, 21147, 115975, 678570, 4213597, 27644437, 190899322, 1382958545};
		
		for (int i=2; i<=numPartitions; i++){
			partitions[1] = new String[size[i]];  // current one.
			
			int cont=0;
			//take all elements from partition with i-1 elements, and add new cluster with single element
			for(int j=0; j< partitions[0].length; j++){
				partitions[1][cont] = partitions[0][j]+" "+i;
				cont++;

			}
			
			//take all elements from partition with i-1 elements, and add new element to each cluster
			for(int j=0; j< partitions[0].length; j++){
				String clusters[] = partitions[0][j].split(" ");
				
				for(int cl=0; cl<clusters.length; cl++){
					
					String old = clusters[cl];
					clusters[cl]= clusters[cl] + "," + i;
					String newPart="";
					for(int m=0; m<clusters.length-1; m++){
						newPart+=clusters[m]+" ";
					}
					newPart+=clusters[clusters.length-1];
					
					clusters[cl]= old;
					partitions[1][cont] = newPart;
					cont++;
				}
			}
			
		//	System.out.println("");

			
			// set previous
			if(i!=numPartitions){
				partitions[0] = partitions[1].clone();
			}
				
			
		}		
		
		return partitions[1]; //.split(";");
	}
	
	 /*
	String [] soluciones = generatePotentialSolutionsVector(14);
	
	for(int i=0; i<soluciones.length; i++){
		System.out.println(soluciones[i]);
	}
	
	 System.exit(1);
	 */
	
	/*
	
	public static myMeasures assessIndividual(String linea, int numOutputs) throws IOException, ClusException{
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
		
//		System.out.print("\n Individual: "); System.out.println(linea);

		// System.out.println("\n"+clusters.length);
		
		myMeasures measure = ClusWrapper.evaluateIndividual(individual,true);
		
	//	System.out.println("Training: " +measure.getMAE()[0]+ ","+measure.getMSE()[0]+","+measure.getRMSE()[0]+","+measure.getWRMSE()[0]);
	//	System.out.println("Test: " +measure.getMAE()[1]+ ","+measure.getMSE()[1]+","+measure.getRMSE()[1]+","+measure.getWRMSE()[1]);
		
		return measure;
		
	}
	*/
	/**
	 * It will support maximum numOutputs = 16. We need to save till the last iteration, the potential solutions. The maximum allow
	 * to reserve memory from the bell number is: 1382958545;
	 * @param args
	 * @throws IOException
	 * @throws ClusException
	 
	
	public static void main(String[] args) throws IOException, ClusException {
		
		if(args.length!=4){
			System.err.println("The number of parameters is 4:  <training dataset> <test dataset> <attributes> <numOutputs>");
			System.exit(1);
		}
		
		
		long timeStart=System.nanoTime();
		String train = args[0]; // "../datasets/regression/rf1/rf1-train.arff";
		String test = args[1]; // "../datasets/regression/rf1/rf1-test.arff";
		
		//rf1 -> [65-72]
		// Run this BEFORE the main loop of the GA,
		// indicate: name of training and test datasets + range of output attributes (for Disable option)
		
		ClusWrapper.initialization(train, test, args[2],false); 	
		
		int numOutputs= Integer.parseInt(args[3]);
		
		int cont=0;
		
		double minimumTRAError = Double.MAX_VALUE;
		String betterTRAIndividual="";
		
		double minimumTSTError = Double.MAX_VALUE;
		String betterTSTIndividual="";
		
		// Generate potential solutions:
	
		String[][] partitions = new String[2][]; // only the last two is enough.
		
		partitions[0] = new String[1]; // previous
		
		partitions[0][0] = "1"; 

		int sizeBell[] ={1, 1, 2, 5, 15, 52, 203, 877, 4140, 21147, 115975, 678570, 4213597, 27644437, 190899322, 1382958545};
		
		// this loop will generate everything till the numOutput-1, in order to save memory ,we don't store the outputs of the last one..
		
		for (int i=2; i<numOutputs; i++){
			partitions[1] = new String[sizeBell[i]];  // current one.
			
			int cont2=0;
			//take all elements from partition with i-1 elements, and add new cluster with single element
			for(int j=0; j< partitions[0].length; j++){
				partitions[1][cont2] = partitions[0][j]+" "+i;
				cont2++;
			}

			//take all elements from partition with i-1 elements, and add new element to each cluster
			for(int j=0; j< partitions[0].length; j++){
				String clusters[] = partitions[0][j].split(" ");
				
				for(int cl=0; cl<clusters.length; cl++){
					
					String old = clusters[cl];
					clusters[cl]= clusters[cl] + "," + i;
					String newPart="";
					for(int m=0; m<clusters.length-1; m++){
						newPart+=clusters[m]+" ";
					}
					newPart+=clusters[clusters.length-1];
					clusters[cl]= old;
					partitions[1][cont2] = newPart;
					cont2++;
				}
			}
			
		//	System.out.println("");

			
			// set previous
			if(i!=numOutputs-1)
				partitions[0] = partitions[1].clone();
		}		
		

		//Generating actual individuals!
		
		// First group:
		
		for(int j=0; j< partitions[1].length; j++){
			String individual=partitions[1][j]+" "+numOutputs;
			myMeasures measure= assessIndividual(individual,numOutputs);;

			
			if(measure.getMAE()[0]<minimumTRAError){
				minimumTRAError = measure.getMAE()[0];
				betterTRAIndividual = individual;
			}
			
			if(measure.getMAE()[1]<minimumTSTError){
				minimumTSTError = measure.getMAE()[1];
				betterTSTIndividual = individual;
			}
			
			cont++;
				
		}
	
		
		// Second group:
		
		for(int j=0; j< partitions[1].length; j++){
			String clusters[] = partitions[1][j].split(" ");
			
			for(int cl=0; cl<clusters.length; cl++){
				
				String old = clusters[cl];
				clusters[cl]= clusters[cl] + "," + numOutputs;
				String newPart="";
				for(int m=0; m<clusters.length-1; m++){
					newPart+=clusters[m]+" ";
				}
				newPart+=clusters[clusters.length-1];
				clusters[cl]= old;
				
				String individual= newPart;
				
				myMeasures measure= assessIndividual(individual,numOutputs);

				
				if(measure.getMAE()[0]<minimumTRAError){
					minimumTRAError = measure.getMAE()[0];
					betterTRAIndividual = individual;
				}
				
				if(measure.getMAE()[1]<minimumTSTError){
					minimumTSTError = measure.getMAE()[1];
					betterTSTIndividual = individual;
				}
				
				cont++;
					
			
		
			}
		}
		
		long timeEnd=System.nanoTime();
		
		System.out.println("\n\nThe best training individual is: "+betterTRAIndividual +  ";  error: "+minimumTRAError);
		System.out.println("The best test individual is: "+betterTSTIndividual+  ";  error: "+minimumTSTError);
		System.out.println("My hash table has : "+ClusWrapper.PreviousSolutions.size());
		
		System.out.println("number of individuals evaluated: " + cont);
		System.out.println("RunTime: "+(timeEnd-timeStart)/1e9);
	}
	*/
