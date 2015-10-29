package tools;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import weka.core.Instances;

public class GenerateLatexTableDatasets {

	public static void main(String args[]) throws Exception{


		String folder = "/Users/rodrigobarros/Desktop/";
		String folder2 = "/Users/rodrigobarros/Desktop/Datasets/Arrumados/";
		FileWriter fw = new FileWriter(folder+"datasetsLatex.txt");
		PrintWriter pw = new PrintWriter(fw,true);
		FileReader leitor;

		pw.println("\\begin{table*}[ht]");
		pw.println(" \\centering");
		pw.println("\\caption{Summary of the 67 UCI data sets.}");
		pw.println("\\label{tab:datasetsUCI67}");
		pw.println("\\scriptsize");
		pw.println("\\begin{tabular*}{\\textwidth}");
		pw.print("{@{\\extracolsep{\\fill}}");
		pw.print("lcccccccc}");
		pw.println("\\toprule");
		pw.println("Data set & Number of & Number of & Numeric & Nominal & % of & Min & Max & Number of \\\\");
		pw.println(" & Instances & Attributes & Attributes & Attributes & Missing Values & Class & Class & Classes \\\\");
		pw.println("\\midrule");
		
		DadosEntrada dados = new DadosEntrada();
		Instances instances;
		ArrayList<String> datasets = dados.getData();

		for(int i=0; i<datasets.size();i++){
			System.out.println(datasets.get(i));
			leitor = new FileReader(folder2+datasets.get(i)+".arff");
			instances = new Instances(leitor);
			instances.setClassIndex(instances.numAttributes()-1);
			MetaLearning resumo = new MetaLearning(instances);
			pw.println(dados.getData().get(i)+" & "+resumo.getNumInstances()+" & "+(resumo.getNumAtt()-1)+" & "+resumo.getNumericAtt()+" & "+resumo.getNomAtt()+
					" & "+resumo.getPercentageMissingValues()+" & "+resumo.getMinClass()+
					" & "+resumo.getMaxClass()+" & "+resumo.getNumClasses()+" \\\\");
		}
		pw.println("\\bottomrule");
		pw.println("\\end{tabular*}");
		pw.println("\\end{table*}");
		fw.close();
	}

}
