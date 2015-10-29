package tools;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import weka.core.Instances;


public class GenerateHEADResults {
	static String source = "/Users/rodrigobarros/Desktop/Datasets/Arrumados/";
	static String dirResults =  "/Users/rodrigobarros/Dropbox/Pesquisa/USP/Tese/Resultados/HEAD-DT/GE-SingleDataset/";
	static String dirCurrent = "/Users/rodrigobarros/Dropbox/Pesquisa/USP/Tese/Resultados/HEAD-DT/GE-SingleDataset/";
	static int folds = 10; 
	static int execucoes = 5;

	public static void main(String[] args) throws Exception {
		new DadosEntrada();
	//	new File(dirResults).mkdir();

		double[][][][] table = new double[DadosEntrada.datasets.size()][folds][execucoes][6];
		double[][] mediasMedidas = new double[DadosEntrada.datasets.size()][6];

		for (int base = 0; base < DadosEntrada.datasets.size(); base++) {
			System.out.println("Dataset = "+DadosEntrada.datasets.get(base));
			for (int fold = 0; fold < folds; fold++) {
				System.out.println("Fold = "+fold);
				
				FileReader readerData = new FileReader(source+DadosEntrada.datasets.get(base)+".arff");
				Instances dataset = new Instances(readerData);
				
				String inFileName = new String(dirCurrent+DadosEntrada.datasets.get(base)+"/Fold"+fold+"/resultado-fold"+fold+".csv");
				BufferedReader pInFile = new BufferedReader(new FileReader(new File(inFileName)));
				
				String line = pInFile.readLine(); // primeira linha contŽm apenas os nomes da medidas
				
				
				for (int exec = 0; exec < execucoes; exec ++) {
					line = pInFile.readLine();
					StringTokenizer st = new StringTokenizer(line, ",");
					for (int medida = 0; medida < 6; medida++) {
						table[base][fold][exec][medida] = Double.valueOf(st.nextToken());
						mediasMedidas[base][medida] += table[base][fold][exec][medida]; 
					}
				}
			}
			for (int medida = 0; medida < 6; medida++) {
				mediasMedidas[base][medida] /= (folds*execucoes);
			}
		}

		double[] desvios = new double[6];

		String outFileName = new String(dirResults + "HEAD.csv");
		PrintWriter pOutFile = new PrintWriter(outFileName);

		pOutFile.println(",Accuracy,,F-Measure,,Precision,,Recall,,Total Nodes,,Total Leaves,");
		for (int base = 0; base < DadosEntrada.datasets.size(); base++) {
			for (int medida = 0; medida < 6; medida++) {
				desvios[medida] = 0;
				for (int exec = 0; exec < execucoes; exec++) {
					for (int fold = 0; fold < folds; fold++) {
						desvios[medida] += Math.pow(table[base][fold][exec][medida] - mediasMedidas[base][medida],2);
					}
				}
				desvios[medida] = Math.sqrt(desvios[medida]/((execucoes*folds)-1));
			}	

			String line = new String(DadosEntrada.datasets.get(base));
			for (int medida = 0; medida < 6; medida++) {
				System.out.println(desvios[medida]);
				line += ","+mediasMedidas[base][medida] + "," + desvios[medida];
			}
			pOutFile.println(line);
		}
		pOutFile.close();
	}

}
