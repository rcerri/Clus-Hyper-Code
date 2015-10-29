package scripts;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

import weka.core.Utils;

public class foldsTesteT {

	public static void main(String args[]) throws FileNotFoundException {

		ArrayList<String> DataSets = new ArrayList<String>();

		DataSets.add("alizadeh-2000-v1");
		DataSets.add("alizadeh-2000-v2");
		DataSets.add("alizadeh-2000-v3");
		DataSets.add("armstrong-2002-v1");
		DataSets.add("armstrong-2002-v2");
		DataSets.add("bittner-2000");
		DataSets.add("bredel-2005");
		DataSets.add("chen-2002");
		DataSets.add("chowdary-2006");
		DataSets.add("dyrskjot-2003");
		DataSets.add("golub-1999-v1");
		DataSets.add("golub-1999-v2");
		DataSets.add("laiho-2007");
		DataSets.add("lapointe-2004-v1");
		DataSets.add("liang-2005");
		DataSets.add("nutt-2003-v1");
		DataSets.add("nutt-2003-v2");
		DataSets.add("nutt-2003-v3");
		DataSets.add("pomeroy-2002-v1");
		DataSets.add("pomeroy-2002-v2");
		DataSets.add("risinger-2003");
		DataSets.add("shipp-2002-v1");
		DataSets.add("singh-2002");
		DataSets.add("tomlins-2006-v2");
		DataSets.add("west-2001");
		DataSets.add("yeoh-2002-v1");

		

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
		
		String abordagem = "HEAD-DT";
		String fileToWriteName = "/Volumes/Dados/Dropbox/papers/2. em andamento/IJCAI 2015/resultados/"+abordagem+"/allFolds.csv";
		PrintWriter fileToWrite = new PrintWriter(fileToWriteName);
		fileToWrite.println(",Train Accuracy,,Validation Accuracy,,Test Accuracy,,Train F-Measure,,Validation F-Measure,,Test F-Measure,,Train Precision,,Validation Precision,,Test Precision,,Train Recall,,Validation Recall,,Test Recall,,Total Nodes,,Total Leaves,,Train Balance,,Validation Balance,,Test Balance,,Train AUC,,Validation AUC,,Test AUC");
		
		for (int d = 0; d < DataSets.size(); d++ ) {
			String dir = "/Volumes/Dados/Dropbox/papers/2. em andamento/IJCAI 2015/resultados/"+abordagem+"/"+DataSets.get(d)+"/";
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

