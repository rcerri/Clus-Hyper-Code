package tools;

import headDt.Dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.StringTokenizer;



public class GenerateLatexTable {

	public static int measure;
	public static int config;
	public static String textoConfig;
	public static String folderConfig;
	public static String textoMeasure;
	public static String folder;
	public static String baselineFolder;
	public static String resultado = "/Users/rodrigobarros/Desktop/";
	public static String datasets = "/Users/rodrigobarros/Desktop/Datasets/Arrumados/";
	public static String fileBaseline[];
	public static String fileHead[];
	public static DecimalFormat df;
	public static DecimalFormatSymbols symbol;

//	public static String baselines[] = {"CART","C4.5", "REP"};
//	public static String baselines[] = {"RandomSearch"};
	public static String baselines[] = {"SVM"};
	public static String head[] = {"HEAD-DT"};

	public static void main(String args[]) throws IOException{

		config = 5;
		
		measure = 0;	
		initializeStrings();
		df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		symbol = new DecimalFormatSymbols();
		symbol.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(symbol);
//		String measures[] = {"Accuracy","F-Measure","Precision","Recall","TotalNodes","TotalLeaves"};
		
		
	//	folder = "/Users/rodrigobarros/Dropbox/Pesquisa/USP/Tese/Heterogeneo UCI/Resultados/"+folderConfig+"/";
	//	baselineFolder = "/Users/rodrigobarros/Dropbox/Pesquisa/USP/Tese/Heterogeneo UCI/Resultados/"+folderConfig+"/Baselines/";
		
//		folder = "/Users/rodrigobarros/Dropbox/Pesquisa/USP/Tese/Random Search/"+folderConfig+"/";
//		baselineFolder = "/Users/rodrigobarros/Dropbox/Pesquisa/USP/Tese/Random Search/"+folderConfig+"/";
		
		folder = "/Users/rodrigobarros/Dropbox/Artigos Marcio/IEEE TEC 2013/Resultados/"+folderConfig;
		baselineFolder = "/Users/rodrigobarros/Dropbox/Artigos Marcio/IEEE TEC 2013/Resultados/"+folderConfig; 

		fileBaseline = new String[baselines.length];
		for(int i=0;i<baselines.length;i++){
			fileBaseline[i] = new String(baselineFolder+baselines[i]+".csv");
		}

		fileHead = new String[head.length];
		for(int i=0;i<head.length;i++){
			fileHead[i] = new String(folder+head[i]+".csv");
		}

		new Dataset("bases"+config+".txt",datasets);

		String outFileName = new String(resultado +"tabelaLatex"+textoConfig+".txt");
		PrintWriter pw = new PrintWriter(outFileName);

		pw.println("\\begin{table}[!htbp]");
		pw.println(" \\centering");
		pw.println("\\caption{HEAD-DT: configuration "+textoConfig+"  vs SVM. Average $\\pm$ standard deviation.}");
		pw.println("\\label{tab:SVMcomparison}");
		pw.println("\\scriptsize");
		pw.println("\\vspace{-0.5cm}");
		
		generateHeader(pw);
		generateBody(pw);

		pw.println("\\qquad");
		pw.println("\\vspace{1cm}");
		
		
		// Troca de Medida - Agora F-Measure
		measure = 1;
		initializeStrings();
		
		generateHeader(pw);
		generateBody(pw);
		
		
		pw.println("\\end{table}");


		pw.close();
	}
	
	
	public static void initializeStrings(){
		switch(config){
		case 1:		textoConfig = "\\{1 x 20\\}";
					folderConfig = "meta1/";
					break;
		case 3: 	textoConfig = "\\{3 x 18\\}";
					folderConfig = "meta3/";
					break;
		case 5:		textoConfig = "\\{5 x 16\\}";
					folderConfig = "meta5/";
					break;
		case 7: 	textoConfig = "\\{7 x 14\\}";
					folderConfig = "meta7/";
					break;
		case 9:		textoConfig = "\\{9 x 12\\}";
					folderConfig = "meta9/";
					break;
		}
		
		switch(measure){
		case 0:	textoMeasure = "Accuracy"; break;
		case 1: textoMeasure = "F-Measure"; break;
		}
		
	}
	
	
	public static void generateHeader(PrintWriter pw){
		pw.println("\\subfloat["+textoMeasure+" results.]{");
		pw.print("\\begin{tabular}{l");
		for(int i=0;i<head.length;i++){
			pw.print("r");
		}
		for(int i=0;i<baselines.length;i++){
			if(i < baselines.length - 1)
				pw.print("r");
			else
				pw.println("r}");
		}

		pw.println("\\toprule");
		pw.print("\\multicolumn{1}{c}{Data Set} &");
		for(int i=0;i<head.length;i++){
			pw.print(head[i]+" & ");
		}
		for(int i=0;i<baselines.length;i++){
			if(i < baselines.length - 1)
				pw.print(baselines[i]+" & ");
			else
				pw.println(baselines[i]+ "\\\\");
		}             

		pw.println("\\midrule");
	}
	
	
	public static void generateBody(PrintWriter pw) throws IOException{
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

		for (int base = 0; base < Dataset.getMetaTest().size(); base++) {
			
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

			pw.print(Dataset.getMetaTestNames().get(base)+" & ");
			for(int i=0;i<head.length;i++) {
				pw.print(df.format(Double.valueOf(tokenHead[i].nextToken()))+"$\\pm$ "+df.format(Double.valueOf(tokenHead[i].nextToken()))+" & ");
			}
			for(int i=0;i<baselines.length;i++) {
				if(i<(baselines.length-1))
					pw.print(df.format(Double.valueOf(tokenBaseline[i].nextToken()))+"$\\pm$ "+df.format(Double.valueOf(tokenBaseline[i].nextToken()))+ " & ");
				else
					pw.println(df.format(Double.valueOf(tokenBaseline[i].nextToken()))+"$\\pm$ "+ df.format(Double.valueOf(tokenBaseline[i].nextToken()))+"\\\\");
			}

		}
		pw.println("\\midrule");
	//	pw.println("Average Rank & & & & \\\\");
		pw.println("Number of victories & & \\\\");
		pw.println("Wilcoxon $p$-value: &  \\multicolumn{2}{c}{}  \\\\");
		pw.println("\\bottomrule");
		pw.println("\\end{tabular}");
		pw.println("}");
	}

}
