package tools;

import headDt.Dataset;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;



import weka.core.Instances;
import weka.core.Utils;

public class TesteResumeDatasets {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/*	FileReader fr = new FileReader("/Users/rodrigobarros/Desktop/Datasets/UCI/iris.arff");
		Instances dataset = new Instances(fr);

		dataset.setClassIndex(dataset.numAttributes()-1);

	//	MetaLearning resume = new MetaLearning(dataset);

		System.out.println("Class Entropy:"+resume.getClassEntropy());
		System.out.println("Average Attribute Entropy:"+resume.getAttEntropy());
		System.out.println("Average Attribute InfoGain:"+resume.getInfoGain());
		System.out.println("Average Attribute GainRatio:"+resume.getGainRatio());
		System.out.println("Average Attribute Mutual Information:"+resume.getMutualInformation());
		System.out.println("Equivalent Number of Attributes:"+resume.getEquivalentNumberOfAttributes());
		System.out.println("NSratio:"+resume.getNSratio());

		System.out.println();
		System.out.println();

		System.out.println(Arrays.toString(resume.attributesEntropy)); */

		FileWriter fw = new FileWriter("/Users/rodrigobarros/Desktop/meta-teste.csv");
		PrintWriter pw = new PrintWriter(fw);
		new Dataset("bases100.txt","/Users/rodrigobarros/Desktop/Datasets/100/");
	/*	pw.println("Base,Complexidade");
		for(int i=0;i<Dataset.getMetaTraining().size();i++){
			int numAtt = Dataset.getMetaTraining().get(i).numAttributes()-1;
			int numInst = Dataset.getMetaTraining().get(i).numInstances();
			double complexidade = numAtt * Utils.xlogx(numInst);
			pw.println(Dataset.getMetaTraining().get(i).relationName()+","+complexidade);} */

		StringBuffer dataset = new StringBuffer();
		MetaLearning meta;
		dataset.append("Nome,#Classes,#Inst,#Att,#Nominal,%Nominal,#Numeric,%Numeric,#Bin,#Missing,%Missing," +
				"Dimensionality,ClassEntropy,MeanAttributeEntropy,MeanMutualInformation,EquivalentNumberOfAttributes, NSratio," +
				"SDratio,MeanAttributeCorrelation,MeanAttributeSkewness,MeanAttributeKurtosis\n");
		for(int i=0;i<Dataset.getMetaTraining().size();i++){
			meta = new MetaLearning(Dataset.getMetaTraining().get(i));
			dataset.append(meta.getName()+","+meta.getNumClasses()+","+meta.getNumInstances()+","+meta.getNumAtt()+","+meta.getNomAtt()+","+
					meta.getPercentageNominal()+","+meta.getNumericAtt()+","+meta.getPercentageNumeric()+","+meta.getBinaryAtt()+","+
					meta.getMissingValues()+","+meta.getPercentageMissingValues()+","+meta.getDimensionality()+","+meta.getClassEntropy()+","+
					meta.getAttEntropy()+","+meta.getMutualInformation()+","+meta.getEquivalentNumberOfAttributes()+","+meta.getNSratio()+","+
					meta.getSDratio()+","+meta.getAverageAttributeCorrelation()+","+meta.getAverageAttributeSkewness()+","+
					meta.getAverageAttributeKurtosis()+"\n");
		}
		pw.print(dataset);
		pw.close();
		fw.close();
	}

}
