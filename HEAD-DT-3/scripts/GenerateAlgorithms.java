package scripts;

import headDt.split.Measure;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

import ec.vector.IntegerVectorIndividual;
import weka.core.Utils;

public class GenerateAlgorithms {
	
	public StringBuffer readIndividual(StringBuffer ind){
		StringBuffer algorithm = new StringBuffer();

		switch(ind.genome[0]){
		case 0: algorithm.append("UnivariateSplit-");break;
		case 1: algorithm.append("MultivariateSplit-");break;
		case 2: algorithm.append("OmniSplit-");break;
		default: break;
		}

		switch(ind.genome[1]){
		// to do - oblique strategy
		default: break;
		}

		algorithm.append(Measure.getName(ind.genome[2])+"-");

		if(ind.genome[3] == 0)
			algorithm.append("MultiNominalSplit-");
		else
			algorithm.append("BinaryNominalSplit-"); //88

		switch(ind.genome[4]){
		case 0: algorithm.append("HomogeneousStop-"); break;
		case 1: algorithm.append("MinNumberInstStop:"); break;
		case 2: algorithm.append("MinPercInstStop:"); break;
		case 3: algorithm.append("AccThresholdStop:"); break;
		case 4: algorithm.append("MaxDepthReached:"); break;
		}

		double param;
		switch(ind.genome[4]){
		case 0: break;
		case 1: param = (ind.genome[5] % 20) + 1; 
		algorithm.append(param+"-"); 
		break;
		case 2: param = ((ind.genome[5] % 10) + 1)/100; 
		algorithm.append(param+"-"); 
		break;
		case 3: param = (ind.genome[5] % 7) + 70 + (ind.genome[5]*5); 
		if(Utils.eq(param,100)) param = param - 1; 
		algorithm.append(param+"-"); 
		break;
		case 4: param = (ind.genome[5] % 9) + 2; 
		algorithm.append(param+"-"); 
		break;
		default: break;
		}

		switch(ind.genome[6]){
		case 0: algorithm.append("MVS:IgnoreMissingValues-"); break;
		case 1: algorithm.append("MVS:UnsupervisedImputation-"); break;
		case 2: algorithm.append("MVS:SupervisedImputation-"); break;
		case 3: algorithm.append("MVS:WeightSplitCriterionValue-"); break;
		default: break;
		}

		switch(ind.genome[7]){
		case 0: algorithm.append("MVD:WeightWithBagProbability-"); break;
		case 1: algorithm.append("MVD:IgnoreMissingValues-"); break;
		case 2: algorithm.append("MVD:UnsupervisedImputation-"); break;
		case 3: algorithm.append("MVD:SupervisedImputation-"); break;
		case 4: algorithm.append("MVD:AssignToAllBags-"); break;
		case 5: algorithm.append("MVD:AddToLargestBag-"); break;
		case 6: algorithm.append("MVD:AssignToMostProbableBagRegardingClass-"); break;
		default: break;
		}

		if(ind.genome[8]==0)
			algorithm.append("DontCollapseTree-");
		else
			algorithm.append("CollapseTree-");//77


		if(ind.genome[9]==0)
			algorithm.append("NoLaPlace-");
		else
			algorithm.append("UseLaPlaceCorrection-"); //66

		switch(ind.genome[10]){
		case 0: algorithm.append("MVC:ExploreAllBranchesAndCombine-"); break;
		case 1: algorithm.append("MVC:HaltInTheCurrentNode-"); break;
		case 2: algorithm.append("MVC:GoToMostProbableBag-"); break;
		default: break;
		}

		switch(ind.genome[11]){
		case 0: algorithm.append("NoPruning-"); break;
		case 1: algorithm.append("ErrorBasedPruning:"); break;
		case 2: algorithm.append("MinimumErrorPruning:"); break;
		case 3: algorithm.append("PessimisticErrorPruning:"); break;
		case 4: algorithm.append("CostComplexityPruning:"); break;
		case 5: algorithm.append("ReducedErrorPruning:"); break;
		}

		Double pruningParameter;
		switch(ind.genome[11]){
		case 0: break;
		case 1: pruningParameter = ((double)(ind.genome[12] % 50) + 1)/100;; algorithm.append(pruningParameter); break;
		case 2: pruningParameter = (double)ind.genome[12] % 100; algorithm.append(pruningParameter); break;
		case 3: pruningParameter = 0.5* ((ind.genome[12] % 4)+1); algorithm.append(pruningParameter); break;
		case 4: pruningParameter = 0.5* ((ind.genome[12] % 4)+1); algorithm.append(pruningParameter+"-"+ind.genome[13]+"folds"); break;
		case 5: algorithm.append(ind.genome[13]+"folds"); break;
		default: break;
		}


		return algorithm;


	public static void main(String args[]) throws FileNotFoundException {

		ArrayList<String> DataSets = new ArrayList<String>();

		int nExec = 10;
		int nMedidas = 20;
		int nFolds = 10;

		double[][] medidas = new double[nMedidas][nExec]; // pra cada fold. Vou usar para calcular as médias das execuções
		double[][] folds = new double[nMedidas][nFolds]; //  uso para calcular as medias de cada fold/

		//dirCurrent = "/Users/basgalup/Desktop/datasets-UCI/UCI/nominal/COMPLETOS/TVT90-10-10/"; //diretorio que est�o os datasets
		//String outFinalTableFileName = dirResults + "Summary.csv";

		//pFinalTable = new PrintWriter(outFinalTableFileName);
		//pFinalTable.println(",Train Accuracy,Validation Accuracy,Test Accuracy,Tree Size,Tree Depth,Execution Time,Recall Train,Recall Validation,Recall Test,Precision Train,Precision Validation,Precision Test,F-Measure Train,F-Measure Validation,F-Measure Test,n Generations");

		//String fileToWriteName = "/Volumes/Dados/Dropbox/papers/2. em andamento/SAC2015/resultados/UCI/Pareto/"+DataSets.get(d)+"folds.csv";
		
		String abordagem = "Pareto Novo";
		String fileToWriteName = "/Volumes/Dados/Dropbox/papers/2. em andamento/SAC2015/resultados/UCI/"+abordagem+"/allFolds.csv";
		PrintWriter fileToWrite = new PrintWriter(fileToWriteName);
		fileToWrite.println(",Train Accuracy,,Validation Accuracy,,Test Accuracy,,Train F-Measure,,Validation F-Measure,,Test F-Measure,,Train Precision,,Validation Precision,,Test Precision,,Train Recall,,Validation Recall,,Test Recall,,Total Nodes,,Total Leaves,,Train Balance,,Validation Balance,,Test Balance,,Train AUC,,Validation AUC,,Test AUC");
		
		for (int d = 0; d < DataSets.size(); d++ ) {
			String dir = "/Volumes/Dados/Dropbox/papers/2. em andamento/SAC2015/resultados/UCI/"+abordagem+"/"+DataSets.get(d)+"/";
			for (int fold = 0; fold < nFolds; fold++ ) {
				String fileToReadName = dir+"Fold"+fold+"/resultado-All-fold"+fold+".csv";
				BufferedReader br = new BufferedReader(new FileReader(new File(fileToReadName)));
				try {
					int exec = 0;
					String line = br.readLine();
					while (exec < nExec) {
						line = br.readLine(); // elimar sempre a primeira linha (rotulo das classes) 	
						System.out.println(line);
						StringTokenizer st = new StringTokenizer(line, ",");
						int m = 0;
						while (st.hasMoreTokens() && (m < 20)) {
							medidas[m][exec] = Double.valueOf(st.nextToken());
							m++;
						}
						exec++;
					}
					// medidas[][] está pronto!
					for (int m = 0; m < nMedidas; m++) {
						folds[m][fold] = Utils.mean(medidas[m]);	
					}
					
//					fileToWrite.println("fold"+fold+","+br.readLine());
					//Close the input stream
					br.close();

				}
				catch (Exception e){//Catch exception if any
					System.err.println("Error: " + e.getMessage());
				}
			}
			String mediaDesvio = new String(DataSets.get(d));
			for (int m = 0; m < nMedidas; m++) {
				double media = 0.0;
				double desvpad = 0.0;
				media = Utils.mean(folds[m]);
				desvpad = Math.sqrt(Utils.variance(folds[m]));
				mediaDesvio += "," +media+ "," +desvpad;
			}

			fileToWrite.println(mediaDesvio);
			//fileToWrite.println(DataSets.get(d)+",=average(B2:B"+(nFolds+1)+"),=average(C2:C"+(nFolds+1)+"),=average(D2:D"+(nFolds+1)+"),=average(E2:E"+(nFolds+1)+"),=average(F2:F"+(nFolds+1)+"),=average(G2:G"+(nFolds+1)+"),=average(H2:H"+(nFolds+1)+"),=average(I2:I"+(nFolds+1)+"),=average(J2:J"+(nFolds+1)+"),=average(K2:K"+(nFolds+1)+"),=average(L2:L"+(nFolds+1)+"),=average(M2:M"+(nFolds+1)+"),=average(N2:N"+(nFolds+1)+"),=average(O2:O"+(nFolds+1)+"),=average(P2:P"+(nFolds+1)+"),=average(Q2:Q"+(nFolds+1)+"),=average(R2:R"+(nFolds+1)+"),=average(S2:S"+(nFolds+1)+"),=average(T2:T"+(nFolds+1)+"),=average(U2:U"+(nFolds+1)+")");
			//fileToWrite.println(DataSets.get(d)+",=stdev(B2:B"+(nFolds+1)+"),=stdev(C2:C"+(nFolds+1)+"),=stdev(D2:D"+(nFolds+1)+"),=stdev(E2:E"+(nFolds+1)+"),=stdev(F2:F"+(nFolds+1)+"),=stdev(G2:G"+(nFolds+1)+"),=stdev(H2:H"+(nFolds+1)+"),=stdev(I2:I"+(nFolds+1)+"),=stdev(J2:J"+(nFolds+1)+"),=stdev(K2:K"+(nFolds+1)+"),=stdev(L2:L"+(nFolds+1)+"),=stdev(M2:M"+(nFolds+1)+"),=stdev(N2:N"+(nFolds+1)+"),=stdev(O2:O"+(nFolds+1)+"),=stdev(P2:P"+(nFolds+1)+"),=stdev(Q2:Q"+(nFolds+1)+"),=stdev(R2:R"+(nFolds+1)+"),=stdev(S2:S"+(nFolds+1)+"),=stdev(T2:T"+(nFolds+1)+"),=stdev(U2:U"+(nFolds+1)+")");
		}
		fileToWrite.close();
	}
}

