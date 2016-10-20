package eda;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;

import weka.core.Instances;
import ec.Evolve;


public class EDAMain {

	public static String fromPath, toPath ;
	public static double[][] similarityMatrix;
	public static int k = 100; //number of clusters - only for the clusterEvolution class (integer genome)
	public static Instances dataset;
	public static double[] probabilities;
	public static String criterion; //options = DB (davies-bouldin index) ou SS (simplified silhouette)
	public static String fitnessType; //options are: WF (weighted formula between index and number of clusters), MONO (index only)

	public static double initialProb;
	public static String labels = null;

	public static FileWriter fOut;
	public static PrintWriter pOut;
	public static String dataName;
	public static int numJobs;
	
	public static double iTime, fTime;

	public static void main(String[] args) throws IOException {

		fromPath = args[0];
		dataName = args[1];
		toPath = args[2]+dataName+"/";
		new File(toPath).mkdir();
		
		numJobs = Integer.valueOf(args[4]);

		FileReader file = new FileReader(fromPath+dataName+".arff");
		dataset = new Instances(file);
		probabilities = new double[dataset.numInstances()];

		FileInputStream input = new FileInputStream(fromPath+dataName+".matrix");
		ObjectInputStream ois = new ObjectInputStream(input);
		try {
			similarityMatrix = (double[][])ois.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ois.close();

		if(args.length > 5)
			criterion =  args[5].trim();
		else
			criterion = "DB";  //default fitness is DB

		if(args.length > 6)
			fitnessType = args[6].trim();
		else
			fitnessType = "MONO"; //default fitness is simple and single-objective 
		
	
		if (args.length > 8) {
			labels = new String(fromPath+dataName+".label");
			System.out.println(labels);
		}

		initialProb = Double.valueOf(args[7]);
		
		System.out.println("Initial Prob = "+initialProb);

		// in case we're running an EDA, initialize probabilities of medoids with 0.05
		for(int i=0; i< probabilities.length;i++){
			probabilities[i] = initialProb;
		}
		

		fOut = new FileWriter(toPath+"results_"+dataName+".csv");
		pOut = new PrintWriter(fOut);
		pOut.println(",SS,DBI,# clusters,Fitness,ARI");

		String string[] = {"-file",args[3], "-p","jobs="+numJobs};

		iTime = System.currentTimeMillis();
		Evolve.main(string);
		
		
		
	}
}
