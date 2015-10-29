package tools;

import java.io.FileReader;
import java.io.PrintWriter;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class GenerateSVMResults {

	public GenerateSVMResults(String source, String resultado) throws Exception{

		new DadosEntrada();

		int numFolds = 10;
		int numMeasures = 4;

		double[][][] table = new double[DadosEntrada.datasets.size()][numFolds][numMeasures];
		double[][] mediasMedidas = new double[DadosEntrada.datasets.size()][numMeasures];
		double[] medidas = new double[numMeasures];




		for(int i=0; i<DadosEntrada.datasets.size();i++){
			System.out.println(DadosEntrada.datasets.get(i));

			for(int f=0; f<numFolds;f++){

				FileReader readerData = new FileReader(source+DadosEntrada.datasets.get(i)+".arff");
				Instances dataset = new Instances(readerData);
				dataset.setClassIndex(dataset.numAttributes()-1);
				dataset.stratify(numFolds);

				SMO svm = new SMO();
				svm.buildClassifier(dataset.trainCV(numFolds,f));
				Evaluation eval = new Evaluation(dataset);
				eval.evaluateModel(svm,dataset.testCV(numFolds,f));
				medidas[0] = (1 - eval.errorRate());
				medidas[1] = eval.weightedFMeasure();
				medidas[2] = eval.weightedPrecision();
				medidas[3] = eval.weightedRecall();
			//	medidas[4] = svm.measureTreeSize();
			//	medidas[5] = svm.measureNumLeaves();

				for (int medida = 0; medida < numMeasures; medida++) {
					table[i][f][medida] = medidas[medida];
					mediasMedidas[i][medida] += table[i][f][medida]; 
				}
			}

			for (int medida = 0; medida < numMeasures; medida++) {
				mediasMedidas[i][medida] /= numFolds;
			}	
		}

		double[] desvios = new double[numMeasures];

		String outFileName = new String(resultado + "SVM.csv");
		PrintWriter pOutFile = new PrintWriter(outFileName);

		pOutFile.println(",Accuracy,,F-Measure,,Precision,,Recall,,Total Nodes,,Total Leaves,");
		for (int base = 0; base < DadosEntrada.datasets.size(); base++) {
			for (int medida = 0; medida < numMeasures; medida++) {
				desvios[medida] = 0;
				for (int fold = 0; fold < numFolds; fold++) {
					desvios[medida] += Math.pow(table[base][fold][medida] - mediasMedidas[base][medida],2);
				}
				desvios[medida] = Math.sqrt(desvios[medida]/(numFolds-1));
			}	

			String line = new String(DadosEntrada.datasets.get(base));
			for (int medida = 0; medida < numMeasures; medida++) {
		//		System.out.println(desvios[medida]);
				line += ","+mediasMedidas[base][medida] + "," + desvios[medida];
			}
			pOutFile.println(line);
		}
		pOutFile.close();
	}
}
