package tools;

import java.io.FileReader;
import java.io.PrintWriter;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.REPTree;
import weka.core.Instances;

public class OtimizacaoParametrosBaselines {

	private static String source = "/Users/rodrigobarros/Desktop/Datasets/Arrumados/";
	private static String results = "/Users/rodrigobarros/Desktop/";
	private static int numFolds = 10;

	public static void main(String args[]) throws Exception{


		new DadosEntrada();
		int numFolds = 10;
		int numMeasures = 4;

		

		/*	Classifier classificadores[] = new Classifier[3];

		classificadores[0] = new J48();
		classificadores[1] = new SimpleCart();
		classificadores[2] = new REPTree(); */



	//	float parametros[] = {0.1f,0.2f,0.3f,0.4f,0.5f,0.6f,0.7f,0.8f,0.9f,0.25f};
	//	float parametros[] = {0.25f};
		
		int parametros[] = {2,3,4,5,6,7,8,9,10};
		

		for(int j=0; j<parametros.length;j++){
			
			double[][][] table = new double[DadosEntrada.datasets.size()][numFolds][numMeasures];
			double[][] mediasMedidas = new double[DadosEntrada.datasets.size()][numMeasures];
			double[] medidas = new double[numMeasures];
		

			for(int i=0; i<DadosEntrada.datasets.size();i++){
				System.out.println(DadosEntrada.datasets.get(i));
				
				FileReader readerData = new FileReader(source+DadosEntrada.datasets.get(i)+".arff");
				Instances dataset = new Instances(readerData);
				dataset.setClassIndex(dataset.numAttributes()-1);
				dataset.stratify(numFolds);

				for(int f=0; f<numFolds;f++){

					SimpleCart tree = new SimpleCart();
					tree.setNumFoldsPruning(parametros[j]);
					tree.buildClassifier(dataset.trainCV(numFolds,f));
					Evaluation eval = new Evaluation(dataset);
					eval.evaluateModel(tree,dataset.testCV(numFolds,f));
					medidas[0] = (1 - eval.errorRate());
					medidas[1] = eval.weightedFMeasure();
					medidas[2] = eval.weightedPrecision();
					medidas[3] = eval.weightedRecall();
		//			medidas[4] = tree.measureTreeSize();
		//			medidas[5] = tree.measureNumLeaves();

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

			String outFileName = new String(results + "CART-"+parametros[j]+".csv");
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


}
