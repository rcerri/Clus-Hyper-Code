package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class BuildStatisticalTestWilcoxonSignedRanks {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	public static int config;
	public static String textoConfig;
	public static String folderConfig;
	public static int size;
	
	public static void main(String[] args) throws IOException {


		config = 9;
		initializeStrings();
		int numMeasures = 2;
		String measures[] = {"Accuracy","F-Measure","Precision","Recall","TotalNodes","TotalLeaves"};

		String folderBaseline = "/Users/rodrigobarros/Dropbox/Pesquisa/USP/Tese/Resultados/HEAD-DT/Comparison GE - SingleXMultiple/"+folderConfig;
		String folder = "/Users/rodrigobarros/Dropbox/Pesquisa/USP/Tese/Resultados/HEAD-DT/Comparison GE - SingleXMultiple/"+folderConfig;
		String resultado = "/Users/rodrigobarros/Dropbox/Pesquisa/USP/Tese/Resultados/HEAD-DT/Comparison GE - SingleXMultiple/"+folderConfig;


		String fileBaseline = new String(folderBaseline+"HEADSingle.csv");


		String fileHead = new String(folder+"HEAD60.csv");



		//for each measure, generate a file with data formatted for the statistical test
		for(int measure = 0; measure<numMeasures;measure++){

			String outFileName = new String(resultado + measures[measure]+".txt");
			PrintWriter pw = new PrintWriter(outFileName);

			BufferedReader bufferBaseline = new BufferedReader(new FileReader(new File(fileBaseline)));
			BufferedReader bufferHead = new BufferedReader(new FileReader(new File(fileHead)));
			
			StringBuffer headString =  new StringBuffer("");
			StringBuffer baselineString = new StringBuffer("");


			//skip labels
			bufferBaseline.readLine();
			bufferHead.readLine();

			//imprime no arquivo
			headString.append("General<- c(");
			baselineString.append("Specific<- c(");
			
			//varre os data sets
			for (int base = 0; base < size; base++) {

				String lineBaseline = bufferBaseline.readLine();
				String lineHead = bufferHead.readLine();

				StringTokenizer tokenBaseline = new StringTokenizer(lineBaseline,",");
				StringTokenizer tokenHead = new StringTokenizer(lineHead,",");

				//ignora o nome da base
				tokenBaseline.nextToken();
				tokenHead.nextToken();

				//vai pulando os tokens atŽ chegar na medida desejada
				for(int j=0; j<measure*2;j++){
					tokenBaseline.nextToken();
					tokenHead.nextToken();
				}

				if(base<(size-1)){
					headString.append(tokenHead.nextToken()+",");
					baselineString.append(tokenBaseline.nextToken()+",");
				}
				else{
					headString.append(tokenHead.nextToken()+")");
					baselineString.append(tokenBaseline.nextToken()+")");
				}

			}
			pw.println(headString);
			pw.println(baselineString);
			pw.close();
			
		}
	}
	
	public static void initializeStrings(){
		switch(config){
		case 1:		textoConfig = "\\{1 x 20\\}";
					folderConfig = "meta1/";
					size = 20;
					break;
		case 3: 	textoConfig = "\\{3 x 18\\}";
					folderConfig = "meta3/";
					size = 18;
					break;
		case 5:		textoConfig = "\\{5 x 16\\}";
					folderConfig = "meta5/";
					size = 16;
					break;
		case 7: 	textoConfig = "\\{7 x 14\\}";
					folderConfig = "meta7/";
					size = 14;
					break;
		case 9:		textoConfig = "\\{9 x 12\\}";
					folderConfig = "meta9/";
					size = 12;
					break;
		}
	}
}

