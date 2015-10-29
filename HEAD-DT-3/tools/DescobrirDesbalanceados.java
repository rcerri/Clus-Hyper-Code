package tools;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import weka.core.Instances;
import weka.core.Utils;

public class DescobrirDesbalanceados {


	public static void main(String args[]) throws IOException{

		String dirCurrent = "/Users/rodrigobarros/Desktop/Datasets/100/";
		String dirResults = "/Users/rodrigobarros/Desktop/";
		new DadosEntrada();
		FileWriter writer = new FileWriter(dirResults+"datasetsOrdenados.csv");
		PrintWriter printer = new PrintWriter(writer);
		
		printer.println("Dataset,#Attributes,#Examples,IR");
		for (int base = 0; base < DadosEntrada.datasets.size(); base++) {			
			FileReader readerData = new FileReader(dirCurrent+DadosEntrada.datasets.get(base)+".arff");
			Instances data = new Instances(readerData);
			data.setClassIndex(data.numAttributes()-1);
			
			int numAtt = data.numAttributes() - 1;
			for(int i=0;i<numAtt;i++){
				if(data.attribute(i).isNumeric()){
					if(data.variance(data.attribute(i))==0)
						System.out.println("Base "+data.relationName()+", numeric attribute "+(i+1)+" is constant!");
				}
				else{
					if(data.attribute(i).numValues() <=1)
						System.out.println("Base "+data.relationName()+", nominal attribute "+(i+1)+" is constant!");
				}
					
			}
			
			
			int maxIndex = Utils.maxIndex(data.attributeStats(data.classIndex()).nominalCounts);
			int minIndex = Utils.minIndex(data.attributeStats(data.classIndex()).nominalCounts);
			
			int maxClass = data.attributeStats(data.classIndex()).nominalCounts[maxIndex];
			int minClass = data.attributeStats(data.classIndex()).nominalCounts[minIndex];
			
			if(minClass == 0){
				System.out.println("Dataset com classe zerada = "+data.relationName());
				minClass = 1;
			}
			double IR = (double)maxClass/minClass;
			//System.out.println(IR);
			
			int numExamples = data.numInstances();
			
			printer.println(data.relationName()+","+numAtt+","+numExamples+","+IR);
		//	System.out.println(data.relationName()+","+IR);

		}
		printer.close();
		writer.close();

	}
}
