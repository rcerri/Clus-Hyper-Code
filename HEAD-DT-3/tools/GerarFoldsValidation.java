package tools;

import java.io.File;
import java.io.FileReader;
import weka.core.*;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.supervised.instance.StratifiedRemoveFolds;
import java.util.ArrayList;

public class GerarFoldsValidation {

	public static void splitData(String path, String dataName, Instances data, int seed, int numFolds, int fold) throws Exception{
		Instances full = data.trainCV(numFolds,fold);
		StratifiedRemoveFolds train = new StratifiedRemoveFolds();
		StratifiedRemoveFolds valid = new StratifiedRemoveFolds();

		train.setInputFormat(full);
		train.setSeed(seed);
		train.setNumFolds(4);
		train.setFold(1);
		train.setInvertSelection(true);
		Instances training = Filter.useFilter(full,train);

		ArffSaver saver = new ArffSaver();
		saver.setInstances(training);
		saver.setFile(new File(path+dataName+"_fold"+fold+"_treino.arff"));
		//saver.setDestination(new File("./data/test.arff"));   // **not** necessary in 3.5.4 and later
		saver.writeBatch();

		valid.setInputFormat(full);
		valid.setSeed(seed);
		valid.setNumFolds(4);
		valid.setFold(1);
		valid.setInvertSelection(false);
		Instances validation = Filter.useFilter(full, valid);
		saver = new ArffSaver();
		saver.setInstances(validation);
		saver.setFile(new File(path+dataName+"_fold"+fold+"_validacao.arff"));
		//saver.setDestination(new File("./data/test.arff"));   // **not** necessary in 3.5.4 and later
		saver.writeBatch();

		Instances test = data.testCV(numFolds,fold);
		saver = new ArffSaver();
		saver.setInstances(test);
		saver.setFile(new File(path+dataName+"_fold"+fold+"_teste.arff"));
		//saver.setDestination(new File("./data/test.arff"));   // **not** necessary in 3.5.4 and later
		saver.writeBatch();

	}


	public static void main(String[] args) throws Exception {
		ArrayList<String> DataSets = new ArrayList<String>();
		int folds = 10; 
		int seed = 1;          // the seed for randomizing the data
		String dirCurrent = "/Volumes/Dados/datasets/GeneExpression/"; //diretorio que esta o arquivo fonte
		String dirResults = "/Volumes/Dados/datasets/GeneExpression/TVT/";
		new File(dirResults).mkdir();

		DataSets.add("alizadeh-2000-v1");
		DataSets.add("alizadeh-2000-v2");
		DataSets.add("alizadeh-2000-v3");
		DataSets.add("armstrong-2002-v1");
		DataSets.add("armstrong-2002-v2");
		DataSets.add("bhattacharjee-2001");
		DataSets.add("bittner-2000");
		DataSets.add("bredel-2005");
		DataSets.add("chen-2002");
		DataSets.add("chowdary-2006");
		DataSets.add("dyrskjot-2003");
		DataSets.add("garber-2001");
		DataSets.add("golub-1999-v1");
		DataSets.add("golub-1999-v2");
		DataSets.add("gordon-2002");
		DataSets.add("khan-2001");
		DataSets.add("laiho-2007");
		DataSets.add("lapointe-2004-v1");
		DataSets.add("lapointe-2004-v2");
		DataSets.add("liang-2005");
		DataSets.add("nutt-2003-v1");
		DataSets.add("nutt-2003-v2");
		DataSets.add("nutt-2003-v3");
		DataSets.add("pomeroy-2002-v1");
		DataSets.add("pomeroy-2002-v2");
		DataSets.add("ramaswamy-2001");
		DataSets.add("risinger-2003");
		DataSets.add("shipp-2002-v1");
		DataSets.add("singh-2002");
		DataSets.add("su-2001");
		DataSets.add("tomlins-2006-v2");
		DataSets.add("tomlins-2006");
		DataSets.add("west-2001");
		DataSets.add("yeoh-2002-v1");
		DataSets.add("yeoh-2002-v2");

		for (int base = 0; base < DataSets.size(); base++) {
			FileReader reader = new FileReader(dirCurrent+DataSets.get(base)+".arff");
			Instances data = new Instances(reader);
			data.setClassIndex(data.numAttributes()-1);
			data.stratify(folds);
			for (int fold = 0; fold < folds; fold++) {
				new File(dirResults+DataSets.get(base)).mkdir();
				splitData(dirResults+DataSets.get(base)+"/",DataSets.get(base),data,1,folds,fold);
			}
		}
	}
}