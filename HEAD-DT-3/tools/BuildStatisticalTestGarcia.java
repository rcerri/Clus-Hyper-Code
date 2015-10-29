package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class BuildStatisticalTestGarcia {

	public static void main(String args[]) throws IOException{

		int numMeasures = 6;
		String measures[] = {"Accuracy","F-Measure","Precision","Recall","TotalNodes","TotalLeaves"};
		String folder = "/Users/rodrigobarros/Desktop/Resultados/IEEETEC/Individual/";
		String folder2 = "/Users/rodrigobarros/Desktop/Resultados/IEEETEC/Baselines/";
		String resultado = "/Users/rodrigobarros/Desktop/Resultados/IEEETEC/Individual/TesteEstatistico/";
		
		String baselines[] = {"CART","J48", "REP"};
		String head[] = {"HEAD10", "HEAD80"};
		
		String fileBaseline[] = new String[baselines.length];
		for(int i=0;i<baselines.length;i++){
			fileBaseline[i] = new String(folder2+baselines[i]+".csv");
		}
		
		String fileHead[] = new String[head.length];
		for(int i=0;i<head.length;i++){
			fileHead[i] = new String(folder+head[i]+".csv");
		}

		new DadosEntrada();

		//for each measure, generate a file with data formatted for the statistical test
		for(int measure = 0; measure<numMeasures;measure++){

			String outFileName = new String(resultado + measures[measure]+".txt");
			PrintWriter pw = new PrintWriter(outFileName);

			BufferedReader bufferBaseline[] = new BufferedReader[baselines.length];
			for(int i=0;i<baselines.length;i++){
				bufferBaseline[i] = new BufferedReader(new FileReader(new File(fileBaseline[i])));
			}
			
			BufferedReader bufferHead[] = new BufferedReader[head.length];
			for(int i=0;i<head.length;i++){
				bufferHead[i] = new BufferedReader(new FileReader(new File(fileHead[i])));
			}
			

			// skip first line - labels
			String lineBaseline[] = new String[baselines.length];
			for(int i=0; i<baselines.length;i++){
				lineBaseline[i] = bufferBaseline[i].readLine();
			}
			
			String lineHead[] = new String[head.length];
			for(int i=0; i<head.length;i++){
				lineHead[i] = bufferHead[i].readLine();
			}
			
			

			//Imprime a primeira linha
			pw.print("Dataset,");
			for(int i=0; i<head.length;i++){
				pw.print(head[i]+",");
			}
			for(int i=0; i<baselines.length;i++){
				if(i<(baselines.length-1))
					pw.print(baselines[i]+",");
				else
					pw.println(baselines[i]);
			}
			
			for (int base = 0; base < DadosEntrada.datasets.size(); base++) {
//				
				for(int i=0; i<baselines.length;i++){
					lineBaseline[i] = bufferBaseline[i].readLine();
				}
				
				for(int i=0; i<head.length;i++){
					lineHead[i] = bufferHead[i].readLine();
				}
				
				StringTokenizer tokenBaseline[] = new StringTokenizer[baselines.length];
				for(int i=0;i<baselines.length;i++){
					tokenBaseline[i] = new StringTokenizer(lineBaseline[i], ",");
				}
				
				StringTokenizer tokenHead[] = new StringTokenizer[head.length];;
				for(int i=0;i<head.length;i++){
					tokenHead[i] = new StringTokenizer(lineHead[i], ",");
				}
				
				
				//ignora o nome da base
				for(int i=0; i<baselines.length;i++) {
					tokenBaseline[i].nextToken();
				}
				
				for(int i=0; i<head.length;i++) {
					tokenHead[i].nextToken();
				}

				//vai pulando os tokens atŽ chegar na medida desejada
				for(int j=0; j<measure*2;j++){
					for(int i=0; i<baselines.length;i++) {
						tokenBaseline[i].nextToken();
					}
					for(int i=0; i<head.length;i++) {
						tokenHead[i].nextToken();
					}
				}

				
				
				//imprime no arquivo
				pw.print(DadosEntrada.datasets.get(base)+",");
				for(int i=0;i<head.length;i++) {
					pw.print(Double.valueOf(tokenHead[i].nextToken())+",");
				}
				for(int i=0;i<baselines.length;i++) {
					if(i<(baselines.length-1))
						pw.print(Double.valueOf(tokenBaseline[i].nextToken())+",");
					else
						pw.println(Double.valueOf(tokenBaseline[i].nextToken()));
				}
				
				
			}
			pw.close();
		}
	}

}
