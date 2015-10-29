package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class BuildStatisticalTestThiago {
	public static void main(String args[]) throws IOException{

		int numMeasures = 2;
		String measures[] = {"Accuracy","F-Measure","Precision","Recall","TotalNodes","TotalLeaves"};
		
		String folderBaselines = "/Users/rodrigobarros/Desktop/CART/";
		String folder = "/Users/rodrigobarros/Dropbox/Artigos Marcio/IEEE TEC 2013/Resultados/meta9/";
		String resultado = "/Users/rodrigobarros/Desktop/CART/";
		
	//	String baselines[] = {"CART","C4.5", "REP"};
	//	String baselines[] = {};
	//	String head[] = {"HEAD10","HEAD20","HEAD30","HEAD40","HEAD50","HEAD60","HEAD70","HEAD80","HEAD90"};
	//	String head[] = {"HEAD60"};
	//	String head[] = {"HEAD"};
		String head[] = {};
//		String baselines[] = {"C4.5-0.1","C4.5-0.2","C4.5-0.3","C4.5-0.4","C4.5-0.5","C4.5-0.25"};
//		String baselines[] = {"REP-2","REP-3","REP-4","REP-5","REP-6","REP-7","REP-8","REP-9","REP-10"};
		String baselines[] = {"CART-2","CART-3","CART-4","CART-5","CART-6","CART-7","CART-8","CART-9","CART-10"};
		
		int size = 14; //meta-test size
		
		String fileBaseline[] = new String[baselines.length];
		for(int i=0;i<baselines.length;i++){
			fileBaseline[i] = new String(folderBaselines+baselines[i]+".csv");
		}
		
		String fileHead[] = new String[head.length];
		for(int i=0;i<head.length;i++){
			fileHead[i] = new String(folder+head[i]+".csv");
		}

	//	new DadosEntrada();

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
			
			
			String order;
			// define whether the measure should be maximized or minimized
			if(measure <= 3)
				order = "ASC";
			else
				order = "DESC";

		//	pw.println(DadosEntrada.datasets.size()+"\t"+(head.length + baselines.length)+"\t"+order);
			pw.println(size+"\t"+(head.length + baselines.length)+"\t"+order);
			for (int base = 0; base < size; base++) {
				
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
				for(int i=0;i<head.length;i++) {
					if(baselines.length == 0){
						if(i<(head.length-1))
							pw.print(Double.valueOf(tokenHead[i].nextToken())+"\t");
						else
							pw.println(Double.valueOf(tokenHead[i].nextToken()));
					}
					else
						pw.print(Double.valueOf(tokenHead[i].nextToken())+"\t");
				}
				for(int i=0;i<baselines.length;i++) {
					if(i<(baselines.length-1))
						pw.print(Double.valueOf(tokenBaseline[i].nextToken())+"\t");
					else
						pw.println(Double.valueOf(tokenBaseline[i].nextToken()));
				}
				
			//	System.out.println("Passou do dataset "+DadosEntrada.datasets.get(base));	
			}
			
			pw.close();
		}
	}

}
