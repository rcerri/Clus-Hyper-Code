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
public class BruteForceAdhoc {


	
	
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
		
		ClusWrapper example = new ClusWrapper();
		
		example.initialization(train, test, args[2],false); 	
		
		int numOutputs= Integer.parseInt(args[3]);
		
		int cont=1;
		
		double minimumTRAError = Double.MAX_VALUE;
		double TestErrorBestTrainingPartition=-1;
		int betterTRAIndividual[] = new int [numOutputs];
		
		double minimumTSTError = Double.MAX_VALUE;
		double TrainingErrorBestTrainingPartition=-1;
		int betterTSTIndividual[]= new int [numOutputs];
		
		// Generate potential solutions:

		int kappa[] = new int[numOutputs]; // k is usable as an array of indexes into a set.
		int M[] = new int[numOutputs];
		
		// First: initialize
		
		for (int i=0; i<numOutputs; i++){
			kappa[i]=1;
			M[i]=1;			
		}

		/*
		for(int j=0; j<numOutputs;j++){
			System.out.print(kappa[j]+" ");
		}
		System.out.println("");
		*/
		myMeasures measure = example.evaluateIndividual(kappa,true);
		
		if(measure.getMAE()[0]<minimumTRAError){
			minimumTRAError = measure.getMAE()[0];
			TestErrorBestTrainingPartition = measure.getMAE()[1];
			betterTRAIndividual = kappa.clone();
		}
		
		if(measure.getMAE()[1]<minimumTSTError){
			minimumTSTError = measure.getMAE()[1];
			TrainingErrorBestTrainingPartition = measure.getMAE()[0];
			betterTSTIndividual = kappa.clone();
		}
		
		while(nextPartition(kappa,M)){
			/*
			for(int j=0; j<numOutputs;j++){
				System.out.print(kappa[j]+" ");
			}
			System.out.println("");
			*/
			measure = example.evaluateIndividual(kappa,true);
			
			if(measure.getMAE()[0]<minimumTRAError){
				minimumTRAError = measure.getMAE()[0];
				TestErrorBestTrainingPartition = measure.getMAE()[1];
				betterTRAIndividual = kappa.clone();
			}
			
			if(measure.getMAE()[1]<minimumTSTError){
				minimumTSTError = measure.getMAE()[1];
				TrainingErrorBestTrainingPartition = measure.getMAE()[0];
				betterTSTIndividual = kappa.clone();
			}
			
			//System.out.println("\nMy hash table has : "+ClusWrapper.PreviousSolutions.size());
		//	System.out.print(cont+", ");
			cont++;
		}


		
		
		
		long timeEnd=System.nanoTime();
		
		System.out.println("\n\nThe best training individual is: ");
		
		for(int i=0; i<numOutputs;i++) System.out.print(betterTRAIndividual[i]+" ");
		
		System.out.print(";  error: "+minimumTRAError +"; test error: "+TestErrorBestTrainingPartition);
		
		System.out.println("\n\nThe best test individual is: ");
		
		for(int i=0; i<numOutputs;i++) System.out.print(betterTSTIndividual[i]+" ");
		
		System.out.print(";  error: "+minimumTSTError +"; training error: "+TrainingErrorBestTrainingPartition);
		
		System.out.println("\nMy hash table has : "+example.PreviousSolutions.size());
		
		System.out.println("number of individuals evaluated: " + cont);
		System.out.println("RunTime: "+(timeEnd-timeStart)/1e9);
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
