package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class PairwiseResults {
	
	public static String folder = "/Users/rodrigobarros/Desktop/Resultados/Tese/FitnessAcc/Bases/90-5-5/";
	public static String folder2 = "/Users/rodrigobarros/Desktop/Resultados/Tese/Baselines/";
	
	public static void main(String args[]) throws IOException{
		
		new DadosEntrada();
		int vitorias[] = new int[6];
		int empates[] = new int[6]; 
		int derrotas[] = new int[6];
		
		String headS = new String(folder+"resultadoFinal.csv");
		String baselineS = new String(folder2+"J48.csv");
		BufferedReader head = new BufferedReader(new FileReader(new File(headS)));
		BufferedReader baseline = new BufferedReader(new FileReader(new File(baselineS)));
		
		
		String line = head.readLine(); // primeira linha contém apenas os nomes da medidas
		String line2 = baseline.readLine();

		
		for (int base = 0; base < DadosEntrada.datasets.size(); base++) {
			line = head.readLine();
			line2 = baseline.readLine();
			StringTokenizer stHead = new StringTokenizer(line, ",");
			StringTokenizer stBaseline= new StringTokenizer(line2, ",");
			stHead.nextToken(); //ignora nome da base
			stBaseline.nextToken(); // ignora nome da base
			
			for (int medida = 0; medida < 4; medida++) {
				String tokenHead = stHead.nextToken();
				String tokenJ48 = stBaseline.nextToken();
				if(Double.valueOf(tokenHead) > Double.valueOf(tokenJ48))
					vitorias[medida]++;
				else if(Double.valueOf(tokenHead) == Double.valueOf(tokenJ48))
					empates[medida]++;
				else
					derrotas[medida]++;
				
				stHead.nextToken(); //ignora desvio padrao
				stBaseline.nextToken(); // ignora desvio padrao
				}
			for (int medida = 4; medida < 6; medida++) {
				String tokenHead = stHead.nextToken();
				String tokenJ48 = stBaseline.nextToken();
				if(Double.valueOf(tokenHead) < Double.valueOf(tokenJ48))
					vitorias[medida]++;
				else if(Double.valueOf(tokenHead) == Double.valueOf(tokenJ48))
					empates[medida]++;
				else
					derrotas[medida]++;
				
				stHead.nextToken(); //ignora desvio padrao
				stBaseline.nextToken(); // ignora desvio padrao
			}
		}
		
		System.out.println("Resultado Acurácia: "+vitorias[0]+" vitorias, "+empates[0]+" empates e "+derrotas[0]+" derrotas.");
		System.out.println("Resultado F-Measure: "+vitorias[1]+" vitorias, "+empates[1]+" empates e "+derrotas[1]+" derrotas.");
		System.out.println("Resultado Precision: "+vitorias[2]+" vitorias, "+empates[2]+" empates e "+derrotas[2]+" derrotas.");
		System.out.println("Resultado Recall: "+vitorias[3]+" vitorias, "+empates[3]+" empates e "+derrotas[3]+" derrotas.");
		System.out.println("Resultado Tamanho: "+vitorias[4]+" vitorias, "+empates[4]+" empates e "+derrotas[4]+" derrotas.");
		System.out.println("Resultado Folhas: "+vitorias[5]+" vitorias, "+empates[5]+" empates e "+derrotas[5]+" derrotas.");
	}

}
