package tools;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import weka.core.Instances;

public class TabelaDatasets {

	static String folder = "/Users/rodrigobarros/Desktop/data-sets/";
	static String folder2 = "/Users/rodrigobarros/Desktop/data-sets/";
	static FileWriter fw; 
	static PrintWriter writer;
	static FileReader leitor;
	static double log2 = Math.log(2);


	public static void main(String[] args) throws Exception{
		DadosEntrada dados = new DadosEntrada();
		Instances instances;
		ArrayList<String> datasets = dados.getData();
		fw = new FileWriter(folder2+"datasetsGene.csv");
		writer = new PrintWriter(fw,true);
		writer.println("Dataset,#instances,#numAtt,#instxatt,#numericAtt,#nomAtt,%miss,minClass,maxClass,#classes");
		for(int i=0; i<datasets.size();i++){
			System.out.println(datasets.get(i));
			leitor = new FileReader(folder+datasets.get(i)+".arff");
			instances = new Instances(leitor);
			instances.setClassIndex(instances.numAttributes()-1);
			MetaLearning resumo = new MetaLearning(instances);
			writer.println(resumo.getName()+","+resumo.getNumInstances()+","+resumo.getNumAtt()+","+complexity(resumo.getNumInstances(),resumo.getNumAtt())+","+resumo.getNumericAtt()+","+resumo.getNomAtt()+
					","+resumo.getMissingValues()+","+resumo.getMinClass()+
					","+resumo.getMaxClass()+","+resumo.getNumClasses());
		}
		fw.close();
	}

	public static double complexity(int inst, int att){
		double complexity = att* (inst*log2((double)inst));
		return complexity;
	}

	public static double log2(double num){
		// Constant hard coded for efficiency reasons
		if (num < 1e-6)
			return 0;
		else
			return Math.log(num)/log2;

	}
}
