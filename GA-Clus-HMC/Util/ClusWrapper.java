package Util;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;

import clus.Clus;
import clus.algo.ClusInductionAlgorithmType;
import clus.algo.tdidt.ClusDecisionTree;
import clus.algo.tdidt.tune.CDTTuneFTest;
import clus.ext.ensembles.ClusEnsembleClassifier;
import clus.main.ClusOutput;
import clus.main.Settings;
import clus.util.ClusException;
import jeans.util.cmdline.CMDLineArgs;
import Util.Fichero;

/**
 * This class helps us to connect Clus with the Genetic algorithm
 * 
 * @author isaact
 *
 */
public class ClusWrapper {


	public static String currentdir = System.getProperty("user.dir")+"/";


	// These variables help me to ignore the multiple outputs from Clus.. that are not really informative.
	private static PrintStream realSystemOut = System.out;
	private PrintStream realSystemErr = System.err;


	private static class NullOutputStream extends OutputStream {
		@Override
		public void write(int b){
			return;
		}
		@Override
		public void write(byte[] b){
			return;
		}
		@Override
		public void write(byte[] b, int off, int len){
			return;
		}
		public NullOutputStream(){
		}
	}


	static public Clus clus = null;
	static ClusInductionAlgorithmType clss = null;

	private static String train;

	private static String test;

	public static String disable;

	private static int FirstOutputIndex=0;

	private static boolean forest=false;

	private static Hashtable<String, Double[][][]> PreviousSolutions;

	private static String outputFile="";
	/*
	private static void createBaseConfigFile(String target, boolean trainErrors) throws IOException{


		if(target.contains(",")) target = target.substring(0, target.length()-1); // I remove the last comma.

		BufferedWriter bf = new BufferedWriter(new FileWriter(currentdir+"config.s"));
		String cad = "";

		cad += "[Data]\n";
		cad += "File = "+train+"\n";
		cad += "TestSet = "+test+"\n\n";
		cad += "[Attributes]\nTarget = "+target+"\nDisable = "+disable+"\n"; // Disable = 17-30

		if(trainErrors)
			cad += "[Output]\nWritePredictions = {Test}\nTrainErrors = Yes\nWriteModelFile = No\nWriteOutFile = Yes\n\n";
		else
			cad += "[Output]\nWritePredictions = {Test}\nTrainErrors = No\nWriteModelFile = No\nWriteOutFile = Yes\n\n";

		// System.out.println(cad);
		bf.write(cad);
		bf.close();

	}
	 */

	private static InputStream createInputStreamBaseConfigFile(String target, boolean trainErrors) throws IOException{


		if(target.contains(",")) target = target.substring(0, target.length()-1); // I remove the last comma.

		String cad = "";

		cad += "[Data]\n";
		cad += "File = "+train+"\n";
		cad += "TestSet = "+test+"\n\n";
		//cad += "[Attributes]\nTarget = "+target+"\nDisable = "+disable+"\n"; // Disable = 17-30
		cad += "[Attributes]\nTarget = "+target+"\nDisableMultipleRunsWithoutRereadingData = "+disable+"\n"; // Disable = 17-30

		if(trainErrors)
			cad += "[Output]\nTrainErrors = Yes\nWriteModelFile = No\nWriteOutFile = Yes\n\n"; //WritePredictions = {Test}
		else
			cad += "[Output]\nTrainErrors = No\nWriteModelFile = No\nWriteOutFile = Yes\n\n"; //\nWritePredictions = {Test}


		if(forest){
			cad += "[Ensemble]\nIterations = 50 \nEnsembleMethod = RForest\n";
		}
		
		//System.out.println(cad);

		return new ByteArrayInputStream(cad.getBytes(StandardCharsets.UTF_8));

	}
	
	public static InputStream createInputStreamBaseConfigFileForClassification(String target, boolean trainErrors) throws IOException{

		if(target.contains(",")) target = target.substring(0, target.length()-1); // I remove the last comma.

		String cad = "";

		cad += "[Data]\n";
		cad += "File = "+train+"\n";
		cad += "TestSet = "+test+"\n\n";
		//cad += "[Attributes]\nTarget = "+target+"\nDisable = "+disable+"\n"; // Disable = 17-30
		cad += "[Attributes]\nTarget = "+target+"\nDisableMultipleRunsWithoutRereadingData = "+disable+"\n"; // Disable = 17-30
		cad += "Weights = 1\n";
		
		if(trainErrors)
			cad += "[Output]\nOutputMultiLabelErrors=Yes\n"; //WritePredictions = {Test}
		else
			cad += "[Output]\nOutputMultiLabelErrors=Yes\n"; //\nWritePredictions = {Test}


		if(forest){
			cad += "[Ensemble]\nIterations = 50 \nEnsembleMethod = RForest\n";
		}
		
		
		// System.err.println(cad);

		return new ByteArrayInputStream(cad.getBytes(StandardCharsets.UTF_8));

	}

	



	/**
	 * Standard call to Clus
	 * @param args
	 */


	public static void InitializeClus(String[] args, InputStream configFile) {
		try {
			if(clus==null)
				clus = new Clus();
			/*else{
				clus = new Clus(clus.m_Data);
				clus.resetClus();
		    }*/
			
			Settings sett = clus.getSettings();
			CMDLineArgs cargs = new CMDLineArgs(clus);
			cargs.process(args);

			if (cargs.allOK()) {
				sett.setDate(new Date());
				sett.setAppName(cargs.getMainArg(0));

				// clus.initSettings(cargs);
				clus.initSettingsNOFILE(cargs, configFile);

				if (cargs.hasOption("forest")) {
					sett.setEnsembleMode(true);
					clss = new ClusEnsembleClassifier(clus);
					if (sett.getFTestArray().isVector())
						clss = new CDTTuneFTest(clss, sett.getFTestArray()
								.getDoubleVector());
				} else {
					clss = new ClusDecisionTree(clus);
					if (sett.getFTestArray().isVector())
						clss = new CDTTuneFTest(clss, sett.getFTestArray()
								.getDoubleVector());
				}

				clus.initializeMultipleRuns(cargs, clss);
				// clus.singleRun(clss);

			}else{
				System.out.println("What the heck is this?");
			}

		} catch (ClusException e) {
			System.err.println();
			System.err.println("Error: " + e);
		} catch (IllegalArgumentException e) {
			System.err.println();
			System.err.println("Error: " + e.getMessage());
		} catch (FileNotFoundException e) {
			System.err.println();
			System.err.println("File not found: " + e);
		} catch (IOException e) {
			System.err.println();
			System.err.println("IO Error: " + e);
		} 

	}


	public static void runClassifier(String[] args, InputStream ConfigFile) throws IOException, ClusException{

		 System.setOut(new PrintStream(new NullOutputStream()));  // To ignore outputs from Clus

		InitializeClus(args,ConfigFile);
		/*
		// clus.resetClus();
		// reinitialization:
		//Clus temp = new Clus();
		Settings sett = clus.getSettings();
		//Settings sett = temp.getSettings();
		
		CMDLineArgs cargs = new CMDLineArgs(clus);
		//CMDLineArgs cargs = new CMDLineArgs(temp);
		cargs.process(args);

		sett.setDate(new Date());
		sett.setAppName(cargs.getMainArg(0));

		
		//clus.initSettings(cargs);
		
		clus.initSettingsNOFILE(cargs, ConfigFile);
		//temp.initSettingsNOFILE(cargs, ConfigFile);
		
		//System.out.println("+++"+ConfigFile.toString());

		//System.exit(1);

		if (cargs.hasOption("forest")) {
			sett.setEnsembleMode(true);
			clss = new ClusEnsembleClassifier(clus);
			if (sett.getFTestArray().isVector())
				clss = new CDTTuneFTest(clss, sett.getFTestArray()
						.getDoubleVector());
		} else {
			clss = new ClusDecisionTree(clus);
			if (sett.getFTestArray().isVector())
				clss = new CDTTuneFTest(clss, sett.getFTestArray()
						.getDoubleVector());
		}


		// modify specific specific targets
		// clus.modifyOutputTargets(cargs, clss); 
	// 	clus.initializeMultipleRuns(cargs, clss);
		//temp.modifyTargetsV2(cargs, clss, clus.m_Data);


		// Run the classifier:
		//clus.singleRun(clss);
		//System.exit(1);
*/
		outputFile= clus.singleRunNOFILES(clss); // to avoid writing any file.
		//outputFile= temp.singleRunNOFILES(clss); // to avoid writing any file.

		 // System.out.println("Output file: "+outputFile);

		System.setOut(realSystemOut);
	}

	/**
	 * This function process the output from Clus, it return the measure you want: MAE, MSE, RMSE or Weighted RMSE
	 * for a single classifier. 

	public static double[] processOutput(String measure){


		String cadena = Fichero.leeFichero(currentdir+"config.out");
		StringTokenizer lineas = new StringTokenizer (cadena,"\n\r");

		String linea = "";
		while(!linea.equalsIgnoreCase("Testing error")){linea = lineas.nextToken();}  // go ahead until Testing error (to make sure no training error is collected)	  

		if(measure.equalsIgnoreCase("MAE")){
			while(!linea.equalsIgnoreCase("Mean absolute error (MAE)")){linea = lineas.nextToken();}
		}else if(measure.equalsIgnoreCase("MSE")){
			while(!linea.equalsIgnoreCase("Mean squared error (MSE)")){linea = lineas.nextToken();}
		}else if(measure.equalsIgnoreCase("RMSE")){
			while(!linea.equalsIgnoreCase("Root mean squared error (RMSE)")){linea = lineas.nextToken();}
		}else if(measure.equalsIgnoreCase("W-RMSE")){
			while(!linea.equalsIgnoreCase("Weighted root mean squared error (RMSE)")){linea = lineas.nextToken();}
		}else{
			System.out.println("The measure: "+measure+" was not computed by Clus");
			System.exit(-1);
		}

		// Take the next line (Original : ....)
		linea = lineas.nextToken(); // skip line default
		linea = lineas.nextToken();

		int start = linea.indexOf('[');
		int end = linea.indexOf(']');

		String list = linea.substring(start+1, end);

		// System.out.println(list);

		String values[] = list.split(",");

		double errors[] = new double[values.length];

		for(int i=0; i<errors.length; i++){
			errors[i] = Double.parseDouble(values[i]);
		// 	System.out.println(errors[i]);
		}

		return errors;
	}
	 */

	/**
	 * This function process the output from Clus, it return the measure you want: MAE, MSE, RMSE or Weighted RMSE
	 * for a single classifier. 
	 * 
	 * If train == true, training errors are provided.
	 */
	public static Double[][][] processOutputRegression(boolean train){


		//String cadena = Fichero.leeFichero(currentdir+"config.out");
		String cadena = outputFile;
		// System.out.println(cadena);
		//System.exit(1);
		StringTokenizer lineas = new StringTokenizer (cadena,"\n\r");
		String linea = "";
		Double errors[][][]=new Double[2][4][]; // dimension 0: tra/tst; dimension 1: MAE,MSE,RMSE,RMSE; dimension 2: Measures. 


		if(train){

			while(!linea.equalsIgnoreCase("Training error")){linea = lineas.nextToken();}  

			String values[][] = new String[4][]; 

			while(!linea.equalsIgnoreCase("Mean absolute error (MAE)")){linea = lineas.nextToken();}
			linea = lineas.nextToken(); // skip line default
			if(!forest) linea = lineas.nextToken(); // to skip also ORIGINAL, and TAKE PRUNED. 
			linea = lineas.nextToken();

			String list = linea.substring(linea.indexOf('[')+1, linea.indexOf(']'));
			values[0] = list.split(",");

			//System.out.println(values[0][0]);

			while(!linea.equalsIgnoreCase("Mean squared error (MSE)")){linea = lineas.nextToken();}

			// linea = lineas.nextToken(); // skip MSE line
			linea = lineas.nextToken(); // skip line default
			if(!forest) linea = lineas.nextToken(); // to skip also ORIGINAL, and TAKE PRUNED. 
			linea = lineas.nextToken();

			list = linea.substring(linea.indexOf('[')+1, linea.indexOf(']'));
			values[1] = list.split(",");

			//System.out.println(values[1][0]);
			while(!linea.equalsIgnoreCase("Root mean squared error (RMSE)")){linea = lineas.nextToken();}

			//linea = lineas.nextToken(); // skip RMSE line
			linea = lineas.nextToken(); // skip line default
			if(!forest) linea = lineas.nextToken(); // to skip also ORIGINAL, and TAKE PRUNED. 
			linea = lineas.nextToken();

			//System.out.println("linea: "+linea);

			list = linea.substring(linea.indexOf('[')+1, linea.indexOf(']'));
			values[2] = list.split(",");

			//System.out.println(values[2][0]);

			while(!linea.contains("Weighted root mean squared error (RMSE)")){linea = lineas.nextToken();}


			//linea = lineas.nextToken(); // skip WRMSE line
			linea = lineas.nextToken(); // skip line default
			if(!forest) linea = lineas.nextToken(); // to skip also ORIGINAL, and TAKE PRUNED. 
			linea = lineas.nextToken();


			list = linea.substring(linea.indexOf('[')+1, linea.indexOf(']'));
			values[3] = list.split(",");

			// System.out.println(values[3][0]);

			errors[0] = new Double[4][values[0].length]; // for the four measures


			for(int i=0; i<errors[0][0].length; i++){
				errors[0][0][i] = Double.parseDouble(values[0][i]);
				errors[0][1][i] = Double.parseDouble(values[1][i]);
				errors[0][2][i] = Double.parseDouble(values[2][i]);
				errors[0][3][i] = Double.parseDouble(values[3][i]);
				//System.out.println(errors[0][i]);
			}

		}


		while(!linea.equalsIgnoreCase("Testing error")){linea = lineas.nextToken();}  // go ahead until Testing error (to make sure no training error is collected)	  

		String values[][] = new String[4][]; 

		while(!linea.equalsIgnoreCase("Mean absolute error (MAE)")){linea = lineas.nextToken();}
		linea = lineas.nextToken(); // skip line default
		if(!forest) linea = lineas.nextToken(); // to skip also ORIGINAL, and TAKE PRUNED. 
		linea = lineas.nextToken();

		String list = linea.substring(linea.indexOf('[')+1, linea.indexOf(']'));
		values[0] = list.split(",");

		while(!linea.equalsIgnoreCase("Mean squared error (MSE)")){linea = lineas.nextToken();}

		// linea = lineas.nextToken(); // skip MSE line
		linea = lineas.nextToken(); // skip line default
		if(!forest) linea = lineas.nextToken(); // to skip also ORIGINAL, and TAKE PRUNED. 
		linea = lineas.nextToken();

		list = linea.substring(linea.indexOf('[')+1, linea.indexOf(']'));
		values[1] = list.split(",");

		while(!linea.equalsIgnoreCase("Root mean squared error (RMSE)")){linea = lineas.nextToken();}

		//		linea = lineas.nextToken(); // skip RMSE line
		linea = lineas.nextToken(); // skip line default
		if(!forest) linea = lineas.nextToken(); // to skip also ORIGINAL, and TAKE PRUNED. 
		linea = lineas.nextToken();

		list = linea.substring(linea.indexOf('[')+1, linea.indexOf(']'));
		values[2] = list.split(",");

		while(!linea.contains("Weighted root mean squared error (RMSE)")){linea = lineas.nextToken();}

		//		linea = lineas.nextToken(); // skip WRMSE line
		linea = lineas.nextToken(); // skip line default
		if(!forest) linea = lineas.nextToken(); // to skip also ORIGINAL, and TAKE PRUNED. 
		linea = lineas.nextToken();

		list = linea.substring(linea.indexOf('[')+1, linea.indexOf(']'));
		values[3] = list.split(",");


		errors[1] = new Double[4][values[0].length]; // for the four measures // for the four measures

		for(int i=0; i<errors[1][0].length; i++){
			errors[1][0][i] = Double.parseDouble(values[0][i]);
			errors[1][1][i] = Double.parseDouble(values[1][i]);
			errors[1][2][i] = Double.parseDouble(values[2][i]);
			errors[1][3][i] = Double.parseDouble(values[3][i]);
			//	System.out.println(errors[1][2][i]);
		}

		return errors;
	}

	/**
	 * It will process a file that looks like:
	 * 
	 * 
	Training error
	--------------
	
	Number of examples: 129
	Mean absolute error (MAE)
	   Default        : [0.4997,0.4985,0.3882,0.3651,0.262]: 0.4027
	   Original       : [0.2474,0.1969,0.1658,0.1582,0.1132]: 0.1763
	   
	   ...
	   Multi-label error measures
	   Default        : 
	      Average AUROC:            0.5
	      Average AUPRC:            0.4232558139534883
	      Average AUPRC (weighted): 0.5242638498452452
	      Pooled AUPRC:             0.6439182902362767
	      0: blue:  AUROC: 0.5, AUPRC: 0.511628, Freq: 0.511628
	      1: yellow:  AUROC: 0.5, AUPRC: 0.472868, Freq: 0.472868
	      2: white:  AUROC: 0.5, AUPRC: 0.736434, Freq: 0.736434
	      3: black:  AUROC: 0.5, AUPRC: 0.24031, Freq: 0.24031
	      4: orange:  AUROC: 0.5, AUPRC: 0.155039, Freq: 0.155039
	   Original       : 
	      Average AUROC:            0.993548594927474
	      Average AUPRC:            0.984322405776331
	      Average AUPRC (weighted): 0.9912801770787876
	      Pooled AUPRC:             0.9911930392757778
	      0: blue:  AUROC: 0.987734, AUPRC: 0.989604, Freq: 0.511628
	      1: yellow:  AUROC: 0.997107, AUPRC: 0.996936, Freq: 0.472868
	      2: white:  AUROC: 0.996285, AUPRC: 0.998714, Freq: 0.736434
	      3: black:  AUROC: 0.996708, AUPRC: 0.989781, Freq: 0.24031
	      4: orange:  AUROC: 0.989908, AUPRC: 0.946578, Freq: 0.155039
	 * @param train
	 * @return
	 */

	public static Double[][][] processOutputClassification(boolean train){


		//String cadena = Fichero.leeFichero(currentdir+"config.out");
		String cadena = outputFile;
		// System.out.println(cadena);
		
		StringTokenizer lineas = new StringTokenizer (cadena,"\n\r");
		String linea = "";
		Double errors[][][]=new Double[2][2][]; // dimension 0: tra/tst; dimension 1: Classification Error,AUROC,AUPRC; dimension 2: Measures. 


		
		
		
		if(train){

			while(!linea.equalsIgnoreCase("Training error")){linea = lineas.nextToken();}  

			while(!linea.equalsIgnoreCase("Multi-label error measures")){linea = lineas.nextToken();}   // To skip regression measures
			 
			
			// To skip default result
			if(!forest){
				while(!linea.contains("Pruned")){linea = lineas.nextToken();}     // Pruned for single trees! 
			}else{ 
				
				while(!linea.contains("Original")){linea = lineas.nextToken();}     
			}
			
			String values[] = new String[2]; 

			values[0]="";values[1]="";
			
			while(!linea.contains("Pooled AUPRC")){linea = lineas.nextToken();}
			
			boolean stop = false;
			while(!linea.equalsIgnoreCase("Testing error") && !stop  ){
				
				linea = lineas.nextToken();
				
			
				if(linea.contains("AUROC")){
					String results[] = linea.split(",");
					
					String clean [] =results[0].split(":");
					
					values[0]+= clean[clean.length-1]+",";
					clean =results[1].split(":");
					values[1]+= clean[clean.length-1]+",";
				}else
					stop = true;
			}
			
			//System.out.println(values[0]);
			//System.out.println(values[1]);

			// System.exit(1);
		
			// size of the previous computed list.
		
			String AUCROC[] = values[0].split(",");
			String AUPRC[] = values[1].split(",");
			
			errors[0] = new Double[2][AUCROC.length];
			
			for(int i=0; i<errors[0][0].length; i++){
				errors[0][0][i] = Double.parseDouble(AUCROC[i]);
				errors[0][1][i] = Double.parseDouble(AUPRC[i]);
				//System.out.print(errors[0][1][i]+", ");
			}

			//System.exit(1);
		}

		while(!linea.equalsIgnoreCase("Multi-label error measures")){linea = lineas.nextToken();}  
		// To skip default result
		if(!forest){
			while(!linea.contains("Pruned")){linea = lineas.nextToken();}     // Pruned for single trees! 
		}else{ 
			
			while(!linea.contains("Original")){linea = lineas.nextToken();}     
		}
		
		while(!linea.contains("Pooled AUPRC")){linea = lineas.nextToken();}  // go ahead until Testing error (to make sure no training error is collected)	  

		String values[] = new String[2]; 

		values[0]="";values[1]="";
		
		boolean stop = false;
		while(lineas.hasMoreTokens() && !stop){
			
			linea = lineas.nextToken();
			
			if(linea.contains("AUROC")){
				String results[] = linea.split(",");
				
				String clean [] =results[0].split(":");
				
				values[0]+= clean[clean.length-1]+",";
				clean =results[1].split(":");
				values[1]+= clean[clean.length-1]+",";
			}else{
				stop = false; 
			}
			

		}
		
	
		//System.out.println(values[0]);
		//System.out.println(values[1]);
		// size of the previous computed list.
	
		String AUCROC[] = values[0].split(",");
		String AUPRC[] = values[1].split(",");
		
		errors[1] = new Double[2][AUCROC.length];
		
		for(int i=0; i<errors[1][0].length; i++){
			errors[1][0][i] = Double.parseDouble(AUCROC[i]);
			errors[1][1][i] = Double.parseDouble(AUPRC[i]);
		//	System.out.print(errors[1][1][i]+", ");
		}
		
		
		return errors;
	}
	
	/**
	 * This function process the output from Clus as a classifier, it return the measure you want: Classification accuracy.
	 * for a single classifier. 
	 * 
	 * If train == true, training errors are provided.
	 
	public static Double[][][] processOutputClassification(boolean train){


		//String cadena = Fichero.leeFichero(currentdir+"config.out");
		String cadena = outputFile;
	//	System.out.println(cadena);
	//	System.exit(1);
		StringTokenizer lineas = new StringTokenizer (cadena,"\n\r");
		String linea = "";
		Double errors[][][]=new Double[2][3][]; // dimension 0: tra/tst; dimension 1: Classification Error,F1,Weighted MSE; dimension 2: Measures. 



		if(train){

			while(!linea.equalsIgnoreCase("Training error")){linea = lineas.nextToken();}  

			String values[][] = new String[4][]; 

			while(!linea.equalsIgnoreCase("Accuracy")){linea = lineas.nextToken();}
			linea = lineas.nextToken(); // skip line default
			if(!forest) linea = lineas.nextToken(); // to skip also ORIGINAL, and TAKE PRUNED. 
			linea = lineas.nextToken();

			String list = linea.substring(linea.indexOf('[')+1, linea.indexOf(']'));
			values[0] = list.split(",");

			//System.out.println(values[0][0]);

			while(!linea.equalsIgnoreCase("F1")){linea = lineas.nextToken();}

			// linea = lineas.nextToken(); // skip MSE line
			linea = lineas.nextToken(); // skip line default
			if(!forest) linea = lineas.nextToken(); // to skip also ORIGINAL, and TAKE PRUNED. 
			linea = lineas.nextToken();

			list = linea.substring(linea.indexOf('[')+1, linea.indexOf(']'));
			values[1] = list.split(",");

			//System.out.println(values[1][0]);
			while(!linea.contains("Weighted mean squared error")){linea = lineas.nextToken();}

			//linea = lineas.nextToken(); // skip RMSE line
			linea = lineas.nextToken(); // skip line default
			if(!forest) linea = lineas.nextToken(); // to skip also ORIGINAL, and TAKE PRUNED. 
			linea = lineas.nextToken();

			//System.out.println("linea: "+linea);

			list = linea.substring(linea.indexOf('[')+1, linea.indexOf(']'));
			values[2] = list.split(",");

			//System.out.println(values[2][0]);

			// System.out.println(values[3][0]);

			errors[0] = new Double[4][values[0].length]; // for the four measures


			for(int i=0; i<errors[0][0].length; i++){
				errors[0][0][i] = Double.parseDouble(values[0][i]);
				errors[0][1][i] = Double.parseDouble(values[1][i]);
				errors[0][2][i] = Double.parseDouble(values[2][i]);
				//System.out.println(errors[0][i]);
			}

		}


		while(!linea.equalsIgnoreCase("Testing error")){linea = lineas.nextToken();}  // go ahead until Testing error (to make sure no training error is collected)	  

		String values[][] = new String[4][]; 

		while(!linea.equalsIgnoreCase("Accuracy")){linea = lineas.nextToken();}
		linea = lineas.nextToken(); // skip line default
		if(!forest) linea = lineas.nextToken(); // to skip also ORIGINAL, and TAKE PRUNED. 
		linea = lineas.nextToken();

		String list = linea.substring(linea.indexOf('[')+1, linea.indexOf(']'));
		values[0] = list.split(",");

		while(!linea.equalsIgnoreCase("F1")){linea = lineas.nextToken();}

		// linea = lineas.nextToken(); // skip MSE line
		linea = lineas.nextToken(); // skip line default
		if(!forest) linea = lineas.nextToken(); // to skip also ORIGINAL, and TAKE PRUNED. 
		linea = lineas.nextToken();

		list = linea.substring(linea.indexOf('[')+1, linea.indexOf(']'));
		values[1] = list.split(",");

		while(!linea.contains("Weighted mean squared error")){linea = lineas.nextToken();}

		//	linea = lineas.nextToken(); // skip RMSE line
		linea = lineas.nextToken(); // skip line default
		if(!forest) linea = lineas.nextToken(); // to skip also ORIGINAL, and TAKE PRUNED. 
		linea = lineas.nextToken();

		list = linea.substring(linea.indexOf('[')+1, linea.indexOf(']'));
		values[2] = list.split(",");

		errors[1] = new Double[3][values[0].length]; // for the four measures // for the four measures

		for(int i=0; i<errors[1][0].length; i++){
			errors[1][0][i] = Double.parseDouble(values[0][i]);
			errors[1][1][i] = Double.parseDouble(values[1][i]);
			errors[1][2][i] = Double.parseDouble(values[2][i]);
			//	System.out.println(errors[1][2][i]);
		}

		return errors;
	}
	
	*/

	public static void initialization(String trainName, String testName, String disableAtt, boolean Createforest, boolean classification) throws IOException{

		// Load the dataset only ONCE. 

		PreviousSolutions = new Hashtable<String, Double[][][]>();

		// copy into global variables
		train = trainName;
		test = testName;
		disable = disableAtt;
		forest = Createforest;

		FirstOutputIndex = Integer.parseInt(disable.split("-")[0]); // Compute the first index

		//createBaseConfigFile(disable,false); // create initial config.s file
		
		/*
		 * InputStream configFile= null;
		
		if(classification)
			configFile = createInputStreamBaseConfigFileForClassification(targetAtt,false); // create initial config.s file
		else
			configFile = createInputStreamBaseConfigFile(targetAtt,false); // create initial config.s file

		
		String [] args;

		if(forest){
			args= new String[2];
			args[0] = "-forest";
			args[1] = "config.s";
		}else{
			args= new String[1];
			args[0] = "config.s";
		}

		System.setOut(new PrintStream(new NullOutputStream()));  // To ignore outputs from Clus
		InitializeClus(args, configFile); // load data into memory.
		System.setOut(realSystemOut);*/
	}

	/**
	 * Requires that the previous 'initalization function' is already run.
	 * 
	 * Assumption: In this implementation I assumed that THERE IS NO OVERLAP between classifiers!
	 * @param pop
	 * @return
	 * @throws IOException
	 * @throws ClusException


	public static double[] evaluatePopulation(int pop [][], String measure) throws IOException, ClusException{

		double errorIndividuals [] = new double [pop.length];

		// Check parameters:
		if(pop==null) return null;
		else if (pop.length<1) return null;

		String [] args;	args= new String[1]; args[0] = "config.s";    //options to run Clus.	

		// Start evaluation		

		int MaxClassifiers = pop[0].length; // The maximum number of classifiers would be equal to the size of the individual.

		String Classifier[] = new String [MaxClassifiers];//list of targets per classifier.

		// evaluate an individual

		for(int i=0; i<pop.length; i++){


			System.out.println("\n\n********************** Evaluating individual "+i+" **************************");

			Arrays.fill(Classifier, ""); // empty initialization 

			//1st. Check which targets belong to each classifier.

			for (int j=0; j<MaxClassifiers;j++){
				Classifier[pop[i][j]-1]+= Integer.toString(FirstOutputIndex+j)+",";  // From FirstOutputIndex
			}

			// 2nd, run every classifier.

			double ErrorTarget[] = new double [pop[0].length];  // one error per target.


			for (int j=0; j<MaxClassifiers;j++){

				if(!Classifier[j].equals("")){
					// Create the proper config.s file:
					createBaseConfigFile(Classifier[j],false); // create initial config.s file  (I put False directly)

					// Run the classifier
					runClassifier(args);

					// Process outputs, for each target.
					String targets[]=Classifier[j].split(",");
					double errors[] = new double [targets.length];
					errors= processOutput(measure);

					// map these errors into ErrorTarget
					for(int t=0; t<targets.length; t++){						
						ErrorTarget[Integer.parseInt(targets[t])-FirstOutputIndex] = errors[t]; // Assume targets start from FirstOutputIndex
					}
				}
			}


			// 3rd. aggregate outputs:

			System.out.print("ERRORs: ");

			double averageError = 0;

			for(int e=0; e<ErrorTarget.length; e++){
				averageError+= ErrorTarget[e];
				System.out.print(ErrorTarget[e]+", ");
			}

			System.out.println("");

			errorIndividuals[i] = averageError/ErrorTarget.length;

			// System.exit(1);


		}





		return errorIndividuals;
	}
	 */

	/**
	 * Requires that the previous 'initalization function' is already run.
	 * Evaluate a single individual
	 * 
	 * Assumption: In this implementation I assumed that THERE IS NO OVERLAP between classifiers!
	 * @param pop
	 * @param train - if true the performance measures are given for both datasets (training and 'test'). If false, it just provides
	 * the performance measures for the 'test' set.
	 * @return
	 * @throws IOException
	 * @throws ClusException
	 */

	public static myMeasures evaluateIndividual(int individual [], boolean train) throws IOException, ClusException{

		myMeasures errorIndividuals = new myMeasures();

		// Check parameters:
		if(individual==null) return null;
		else if (individual.length<1) return null;

		String [] args;

		if(forest){
			args= new String[2]; 	args[0] = "-forest"; args[1] = "config.s";    //options to run Clus with RandomForest
		}else{
			args= new String[1]; args[0] = "config.s";    //options to run Clus as a single tree
		}

		// Start evaluation		

		int MaxClassifiers = individual.length; // The maximum number of classifiers would be equal to the size of the individual.


		String Classifier[] = new String [MaxClassifiers];//list of targets per classifier.

		// evaluate an individual

		Arrays.fill(Classifier, ""); // empty initialization 

		//1st. Check which targets belong to each classifier.

		for (int j=0; j<MaxClassifiers;j++){
			Classifier[individual[j]-1]+= Integer.toString(FirstOutputIndex+j)+",";  // From FirstOutputIndex
		}

		// 2nd, run every classifier.

		double ErrorTarget[][][] = new double [2][4][individual.length];  // one error per target.


		for (int j=0; j<MaxClassifiers;j++){

			if(!Classifier[j].equals("")){


				if(PreviousSolutions.containsKey(Classifier[j])){ // if this is already in the HashTable:

					//System.out.println("Yes, I already did this individual: "+Classifier[j]);

					// we already did this!

					// Process outputs, for each target.
					String targets[]=Classifier[j].split(",");
					Double errors[][][] = (Double [][][]) PreviousSolutions.get(Classifier[j]);

					//System.out.println(individual.length+ ", "+FirstOutputIndex + "; "+Integer.parseInt(targets[0]));
					// map these errors into ErrorTarget
					for(int t=0; t<targets.length; t++){			
						if(train){
							ErrorTarget[0][0][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[0][0][t]; // Assume targets start from FirstOutputIndex
							ErrorTarget[0][1][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[0][1][t];
							ErrorTarget[0][2][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[0][2][t];
							ErrorTarget[0][3][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[0][3][t];
						}
						ErrorTarget[1][0][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[1][0][t]; // Assume targets start from FirstOutputIndex
						ErrorTarget[1][1][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[1][1][t];
						ErrorTarget[1][2][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[1][2][t];
						ErrorTarget[1][3][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[1][3][t];
					}


				}else{

					//System.out.println("Classifier[j]: "+Classifier[j]);
					//System.exit(1);

					// Create the proper config.s file:

					// TODO: create a STring, instead of a file.
					InputStream configFile = createInputStreamBaseConfigFile(Classifier[j],train);// createBaseConfigFile(Classifier[j],train); // create initial config.s file

					// Run the classifier
					runClassifier(args, configFile);

					// Process outputs, for each target.
					String targets[]=Classifier[j].split(",");
					Double errors[][][] = new Double [2][4][targets.length]; // one error per performance measure, training and test
					errors= processOutputRegression(train);

					PreviousSolutions.put(Classifier[j], errors);


					//System.out.println(individual.length+ ", "+FirstOutputIndex + "; "+Integer.parseInt(targets[0]));
					// map these errors into ErrorTarget
					for(int t=0; t<targets.length; t++){			
						if(train){
							ErrorTarget[0][0][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[0][0][t]; // Assume targets start from FirstOutputIndex
							ErrorTarget[0][1][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[0][1][t];
							ErrorTarget[0][2][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[0][2][t];
							ErrorTarget[0][3][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[0][3][t];
						}
						ErrorTarget[1][0][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[1][0][t]; // Assume targets start from FirstOutputIndex
						ErrorTarget[1][1][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[1][1][t];
						ErrorTarget[1][2][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[1][2][t];
						ErrorTarget[1][3][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[1][3][t];
					}
				}
			}
		}


		// 3rd. aggregate outputs:

		//	System.out.print("ERRORs: ");

		double averageTrainingError[]  = new double [4];
		Arrays.fill(averageTrainingError, 0);

		double averageTestError[]  = new double [4];
		Arrays.fill(averageTestError, 0);


		for(int m=0; m<ErrorTarget[0].length; m++){
			for(int e=0; e<ErrorTarget[0][0].length; e++){

				averageTrainingError[m]+= ErrorTarget[0][m][e];
				averageTestError[m]+= ErrorTarget[1][m][e];

				//System.out.print(ErrorTarget[0][m][e]+", ");
			}
			//System.out.println("----");

		}

		double [] measure= new double[2];
		measure[0]= averageTrainingError[0]/ErrorTarget[0][0].length;
		measure[1] = averageTestError[0]/ErrorTarget[1][0].length;
		errorIndividuals.setMAE(measure);

		measure= new double[2];
		measure[0]= averageTrainingError[1]/ErrorTarget[0][0].length;
		measure[1] = averageTestError[1]/ErrorTarget[1][0].length;
		errorIndividuals.setMSE(measure);

		measure= new double[2];
		measure[0]= averageTrainingError[2]/ErrorTarget[0][0].length;
		measure[1] = averageTestError[2]/ErrorTarget[1][0].length;
		errorIndividuals.setRMSE(measure);

		measure= new double[2];
		measure[0]= averageTrainingError[3]/ErrorTarget[0][0].length;
		measure[1] = averageTestError[3]/ErrorTarget[1][0].length;
		errorIndividuals.setWRMSE(measure);

		// System.exit(1);


		return errorIndividuals;
	}



	/**
	 * Requires that the previous 'initalization function' is already run.
	 * Evaluate a single individual for classification purposes.
	 * 
	 * Assumption: In this implementation I assumed that THERE IS NO OVERLAP between classifiers!
	 * @param pop
	 * @param train - if true the performance measures are given for both datasets (training and 'test'). If false, it just provides
	 * the performance measures for the 'test' set.
	 * @return
	 * @throws IOException
	 * @throws ClusException
	 */

	public static myMeasures evaluateIndividualClassification(int individual [], boolean train) throws IOException, ClusException{

		myMeasures errorIndividuals = new myMeasures();

		// Check parameters:
		if(individual==null) return null;
		else if (individual.length<1) return null;

		String [] args;

		if(forest){
			args= new String[2]; 	args[0] = "-forest"; args[1] = "config.s";    //options to run Clus with RandomForest
		}else{
			args= new String[1]; args[0] = "config.s";    //options to run Clus as a single tree
		}

		// Start evaluation		

		int MaxClassifiers = individual.length; // The maximum number of classifiers would be equal to the size of the individual.


		String Classifier[] = new String [MaxClassifiers];//list of targets per classifier.

		// evaluate an individual

		Arrays.fill(Classifier, ""); // empty initialization 

		//1st. Check which targets belong to each classifier.

		for (int j=0; j<MaxClassifiers;j++){
			Classifier[individual[j]-1]+= Integer.toString(FirstOutputIndex+j)+",";  // From FirstOutputIndex
			
		}

		// 2nd, run every classifier.

		double ErrorTarget[][][] = new double [2][2][individual.length];  // one error per target.


		for (int j=0; j<MaxClassifiers;j++){

			if(!Classifier[j].equals("")){


				if(PreviousSolutions.containsKey(Classifier[j])){ // if this is already in the HashTable:

					//System.out.println("Yes, I already did this individual: "+Classifier[j]);

					// we already did this!

					// Process outputs, for each target.
					String targets[]=Classifier[j].split(",");
					Double errors[][][] = (Double [][][]) PreviousSolutions.get(Classifier[j]);

					//System.out.println(individual.length+ ", "+FirstOutputIndex + "; "+Integer.parseInt(targets[0]));
					// map these errors into ErrorTarget
					for(int t=0; t<targets.length; t++){			
						if(train){
							ErrorTarget[0][0][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[0][0][t]; // Assume targets start from FirstOutputIndex
							ErrorTarget[0][1][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[0][1][t];
						//	ErrorTarget[0][2][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[0][2][t];
						}
						ErrorTarget[1][0][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[1][0][t]; // Assume targets start from FirstOutputIndex
						ErrorTarget[1][1][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[1][1][t];
						//ErrorTarget[1][2][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[1][2][t];
					}


				}else{

					//System.out.println("Classifier[j]: "+Classifier[j]);

					// AFter new modification of Celine: 21/6/2016: we now have to determine the targets opposite to the Classifier target itself..
					
					/*String targetInverted = "";
					
					for(int m=0; m<MaxClassifiers; m++){
						if(!Classifier[j].contains(Integer.toString(m+1)))
							targetInverted+= (m+1)+",";
					}*/
				
					//System.out.println(targetInverted);
					// System.out.println(Classifier[j]);
			
					//System.exit(1);

					InputStream configFile = createInputStreamBaseConfigFileForClassification(Classifier[j],train);
					//InputStream configFile = createInputStreamBaseConfigFileForClassification(targetInverted,train);// createBaseConfigFile(Classifier[j],train); // create initial config.s file

					// Run the classifier
					runClassifier(args, configFile);

					// Process outputs, for each target.
					String targets[]=Classifier[j].split(",");
					Double errors[][][] = new Double [2][2][targets.length]; // one error per performance measure, training and test
					errors= processOutputClassification(train);

					PreviousSolutions.put(Classifier[j], errors);


					//System.out.println(individual.length+ ", "+FirstOutputIndex + "; "+Integer.parseInt(targets[0]));
					// map these errors into ErrorTarget
					for(int t=0; t<targets.length; t++){			
						if(train){
							ErrorTarget[0][0][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[0][0][t]; // Assume targets start from FirstOutputIndex
							ErrorTarget[0][1][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[0][1][t];
							// ErrorTarget[0][2][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[0][2][t];
						}
						ErrorTarget[1][0][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[1][0][t]; // Assume targets start from FirstOutputIndex
						ErrorTarget[1][1][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[1][1][t];
						// ErrorTarget[1][2][Integer.parseInt(targets[t])-FirstOutputIndex] = errors[1][2][t];
					}
				}
			}
		}

		// 3rd. aggregate outputs:

		//	System.out.print("ERRORs: ");

		double averageTrainingError[]  = new double [2];
		Arrays.fill(averageTrainingError, 0);

		double averageTestError[]  = new double [2];
		Arrays.fill(averageTestError, 0);


		for(int m=0; m<ErrorTarget[0].length; m++){
			for(int e=0; e<ErrorTarget[0][0].length; e++){

				averageTrainingError[m]+= ErrorTarget[0][m][e];
				averageTestError[m]+= ErrorTarget[1][m][e];

			//	System.out.print(ErrorTarget[0][m][e]+", ");
			}
			//System.out.println("----");

		}

		double [] measure= new double[2];
		measure[0]= averageTrainingError[0]/ErrorTarget[0][0].length;
		measure[1] = averageTestError[0]/ErrorTarget[1][0].length;
		//errorIndividuals.setAccuracy(measure);
		errorIndividuals.setAUROC(measure);
		
		measure= new double[2];
		measure[0]= averageTrainingError[1]/ErrorTarget[0][0].length;
		measure[1] = averageTestError[1]/ErrorTarget[1][0].length;
		//errorIndividuals.setF1(measure);
		errorIndividuals.setAUPRC(measure);

		/*
		measure= new double[2];
		measure[0]= averageTrainingError[2]/ErrorTarget[0][0].length;
		measure[1] = averageTestError[2]/ErrorTarget[1][0].length;
		errorIndividuals.setWMSEnominal(measure);

*/
		// System.exit(1);


		return errorIndividuals;
	}

}
