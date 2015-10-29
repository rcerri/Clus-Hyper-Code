package tools;

import java.io.FileReader;
import java.io.PrintWriter;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.REPTree;
import weka.core.Instances;

public class GenerateREPTreeResults {

	public GenerateREPTreeResults(String source, String resultado) throws Exception{
		new DadosEntrada();

		int numFolds = 10;
		int numMeasures = 6;

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

				REPTree tree = new REPTree();
				tree.buildClassifier(dataset.trainCV(numFolds,f));
				Evaluation eval = new Evaluation(dataset);
				eval.evaluateModel(tree,dataset.testCV(numFolds,f));
				medidas[0] = (1 - eval.errorRate());
				medidas[1] = eval.weightedFMeasure();
				medidas[2] = eval.weightedPrecision();
				medidas[3] = eval.weightedRecall();
				medidas[4] = tree.numNodes();
				medidas[5] = 0;

				for (int medida = 0; medida < 6; medida++) {
					table[i][f][medida] = medidas[medida];
					mediasMedidas[i][medida] += table[i][f][medida]; 
				}
			}

			for (int medida = 0; medida < 6; medida++) {
				mediasMedidas[i][medida] /= numFolds;
			}	
		}

		double[] desvios = new double[6];

		String outFileName = new String(resultado + "REP.csv");
		PrintWriter pOutFile = new PrintWriter(outFileName);

		pOutFile.println(",Accuracy,,F-Measure,,Precision,,Recall,,Total Nodes,,Total Leaves,");
		for (int base = 0; base < DadosEntrada.datasets.size(); base++) {
			for (int medida = 0; medida < 6; medida++) {
				desvios[medida] = 0;
				for (int fold = 0; fold < numFolds; fold++) {
					desvios[medida] += Math.pow(table[base][fold][medida] - mediasMedidas[base][medida],2);
				}
				desvios[medida] = Math.sqrt(desvios[medida]/(numFolds-1));
			}	

			String line = new String(DadosEntrada.datasets.get(base));
			for (int medida = 0; medida < 6; medida++) {
				//		System.out.println(desvios[medida]);
				line += ","+mediasMedidas[base][medida] + "," + desvios[medida];
			}
			pOutFile.println(line);
		}
		pOutFile.close();
	}
}
