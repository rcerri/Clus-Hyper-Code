package tools;


import headDt.split.GMI;
import headDt.split.GainRatio;
import headDt.split.InfoGain;
import java.util.ArrayList;
import java.util.Enumeration;
import org.ejml.ops.CovarianceOps;
import org.ejml.simple.SimpleMatrix;
import headDt.topDown.Distribution;
import weka.core.*;


public class MetaLearning{

	private int numClasses = 0;
	private int numAtt = 0;
	private int numInstances = 0;
	private double dimensionality = 0;  // dimensionality = numAtt/numInstances
	private int missingValues = 0;
	private double percentageMissingValues = 0;

	private int numericAtt=0, nominalAtt=0;
	private double percentageNumeric = 0, percentageNominal = 0;
	private int numBinaryAtt = 0;

	private int maxIndex, minIndex, meanIndex, meanClass, minClass, maxClass;


	private int distribuicaoClasses[];
	private double averageMutualInformation=0, averageGainRatio=0, averageEntropy=0, averageInfoGain=0;
	private double classEntropy=0;
	private double equivalentNumberOfAttributes=0;
	private double NSratio=0;
	private double averageAttributeCorrelation=0;
	private double SDratio=0;
	private double averageAttributeSkewness = 0;
	private double averageAttributeKurtosis =0;
	private ArrayList<Integer> nominalIndices = new ArrayList<Integer>();
	private ArrayList<Integer> numericIndices = new ArrayList<Integer>();
	private String name;



	public MetaLearning(Instances dataset) throws Exception {
		name = dataset.relationName();
		numAtt = dataset.numAttributes() - 1;
		numInstances = dataset.numInstances();
		numClasses = dataset.numClasses();
		dimensionality = (double)numAtt/numInstances;

		for (int att = 0; att < numAtt; att++) {
			missingValues = missingValues + dataset.attributeStats(att).missingCount;
			if (dataset.attribute(att).isNumeric()){
				numericAtt++;
				numericIndices.add(att);
			}
			else{
				nominalAtt++;
				nominalIndices.add(att);
				if(dataset.attribute(att).numValues() == 2)
					numBinaryAtt++;
			}
		}

		if(nominalAtt>0){
			computeEntropy(dataset);
		}

		if(numericAtt>0){
			computeContinuousMeasures(dataset);
		}

		percentageNumeric = (double)numericAtt/numAtt;
		percentageNominal = (double)nominalAtt/numAtt;

		maxIndex = Utils.maxIndex(dataset.attributeStats(dataset.classIndex()).nominalCounts);
		minIndex = Utils.minIndex(dataset.attributeStats(dataset.classIndex()).nominalCounts);

		maxClass = dataset.attributeStats(dataset.classIndex()).nominalCounts[maxIndex];
		minClass = dataset.attributeStats(dataset.classIndex()).nominalCounts[minIndex];

		meanIndex = Utils.sort(dataset.attributeStats(dataset.classIndex()).nominalCounts)[(int) dataset.numClasses()/2];
		meanClass = dataset.attributeStats(dataset.classIndex()).nominalCounts[meanIndex];

		percentageMissingValues = ((double)missingValues)/(numAtt*numInstances);


		distribuicaoClasses = new int[numClasses];
		distribuicaoClasses = dataset.attributeStats(dataset.classIndex()).nominalCounts;

	}


	/**
	 * Calcula as medidas de Teoria da Informacao para Meta-Learning, ex:
	 * 1) Entropia media dos atributos 
	 * 2) Entropia das classes
	 * 3) Ganho de Informacao Medio dos Atributos
	 * 4) Mutual Information Media dos Atributos
	 * 5) Numero Equivalente de Atributos
	 * 6) Noise-signal Ratio
	 */
	public void computeEntropy(Instances dataset) throws Exception{				

		
		double attributesEntropy[] = new double[nominalAtt];
		double attributesInfoGain[] = new double[nominalAtt];
		double attributesGainRatio[] = new double[nominalAtt];
		double attributesMutualInformation[] = new double[nominalAtt];
		
		// Calcula a entropia de cada uma das classes
		Distribution dist = new Distribution(dataset);
		classEntropy = info(dist.getPerClass())/dataset.numInstances();


		// Calcula a entropia de cada um dos atributos
		for(int i=0;i<nominalIndices.size();i++){
			AttributeStats stats = dataset.attributeStats(nominalIndices.get(i));
			int[] counts = stats.nominalCounts;
			attributesEntropy[i] = Utils.info(counts);
			attributesEntropy[i] /= (stats.totalCount-stats.missingCount);	

			Distribution full = new Distribution(dataset.attribute(nominalIndices.get(i)).numValues(),dataset.numClasses());
			Enumeration<?> enu = dataset.enumerateInstances();
			Instance instance;
			while (enu.hasMoreElements()) {
				instance = (Instance) enu.nextElement();

				if (!instance.isMissing(nominalIndices.get(i)))
					full.add((int)instance.value(nominalIndices.get(i)),instance);
			}

			InfoGain infoGain = new InfoGain();
			GMI mutual = new GMI();
			GainRatio gainRatio = new GainRatio();
			double total = dataset.numInstances();
			attributesInfoGain[i] = infoGain.getValue(full, total, 0);
			attributesMutualInformation[i] = mutual.getValue(full, total, 0);
			attributesGainRatio[i] = gainRatio.getValue(attributesInfoGain[i], full, total);
		}
		
		averageInfoGain = Utils.mean(attributesInfoGain);
		averageMutualInformation = Utils.mean(attributesMutualInformation);
		averageGainRatio = Utils.mean(attributesGainRatio);
		averageEntropy = Utils.mean(attributesEntropy);
		
		if(averageMutualInformation > 0){
			equivalentNumberOfAttributes = (classEntropy/averageMutualInformation);
			NSratio = (averageEntropy - averageMutualInformation)/averageMutualInformation;	
		}
		else{
			equivalentNumberOfAttributes = 0;
			NSratio = 0;
		}
		

		

	}


	/**
	 * Calcula as medidas de meta-learning para atributos continuos:
	 * 1) SD.ratio
	 * 2) Average attribute correlation
	 * 3) Average attribute skewness
	 * 4) Average attribute kurtosis
	 */
	public void computeContinuousMeasures(Instances dataset){

		// Variable to store the number of instances per class
		int[] instancesPerClass = new int[dataset.numClasses()];


		// Find out how many instances per class
		int classe = 0;
		for(int i=0;i<dataset.numInstances();i++){
			classe = (int)dataset.instance(i).classValue();
			instancesPerClass[classe]++;
		}

		// Create a list of k matrices in which matrix i stores the instances that belong to the ith of the k classes
		ArrayList<SimpleMatrix> attributeValuesPerClass = new ArrayList<SimpleMatrix>();
		for(int i=0;i<dataset.numClasses();i++){
			attributeValuesPerClass.add(new SimpleMatrix(instancesPerClass[i],numericIndices.size()));
		}



		// Matrix "dados" stores the dataset with only numeric attributes
		// The list of matrices "attributeValuesPerClass" holds the k matrices of instances divided according to their corresponding class
		SimpleMatrix dados = new SimpleMatrix(dataset.numInstances(),numericIndices.size());
		classe = 0;
		int currentRow[] = new int[dataset.numClasses()];
		for(int i=0;i<dataset.numInstances();i++){
			classe = (int)dataset.instance(i).classValue();
			for(int j=0;j<numericIndices.size();j++){
				double value = dataset.instance(i).isMissing(numericIndices.get(j)) ?
						dataset.meanOrMode(numericIndices.get(j)) : dataset.instance(i).value(numericIndices.get(j));
						dados.set(i,j,value);
						attributeValuesPerClass.get(classe).set(currentRow[classe], j, value);
			}
			currentRow[classe]++;
		}


		// Compute the average instance per class (like a centroid per class)
		SimpleMatrix meanInstancePerClass[] = new SimpleMatrix[dataset.numClasses()];
		for(int k=0;k<dataset.numClasses();k++){
			SimpleMatrix a = attributeValuesPerClass.get(k);
			meanInstancePerClass[k] = new SimpleMatrix(1,a.numCols());
			for(int j=0;j<a.numCols();j++){
				double value = 0;
				for(int i=0;i<a.numRows();i++){
					value += a.get(i, j);
				}
				value /= a.numRows();
				meanInstancePerClass[k].set(0, j, value);
			}
		}


		// Compute the covariance matrix per class
		SimpleMatrix covarianceMatrixPerClass[] = new SimpleMatrix[dataset.numClasses()];
		SimpleMatrix dispersionMatrixPerClass[] = new SimpleMatrix[dataset.numClasses()];
		for(int k=0; k<dataset.numClasses();k++){
			SimpleMatrix a = attributeValuesPerClass.get(k);
			SimpleMatrix acumulador = new SimpleMatrix(a.numCols(),a.numCols());
			for(int i=0;i<a.numRows();i++){
				SimpleMatrix x = a.extractVector(true, i); //extrai a i-esima linha da matriz (true indica linha, false indica coluna nesse metodo)
				SimpleMatrix sub1 = x.minus(meanInstancePerClass[k]);
				SimpleMatrix sub2 = sub1.transpose();
				SimpleMatrix mult = sub2.mult(sub1);
				acumulador = acumulador.plus(mult);
			}
			dispersionMatrixPerClass[k] = acumulador;
			double value = 1.0/(instancesPerClass[k] - 1);
			covarianceMatrixPerClass[k] = acumulador.scale(value);
		}


		// Compute the pooled covariance matrix and a term for the lambda calculation
		SimpleMatrix pooledCovarianceMatrix = new SimpleMatrix(numericAtt,numericAtt);
		double sum = 0;
		for(int k=0;k<dataset.numClasses();k++){
			pooledCovarianceMatrix = pooledCovarianceMatrix.plus(dispersionMatrixPerClass[k]);
			//gambiarra do Rodrigo para evitar casos de sum = infinity
			if(instancesPerClass[k] <=1)
				sum+=0;
			else
				sum += (1.0/(instancesPerClass[k]-1)) - (1.0/(dataset.numInstances() - dataset.numClasses()));
		}
		double denominator = 1.0/(dataset.numInstances() - dataset.numClasses());
		pooledCovarianceMatrix = pooledCovarianceMatrix.scale(denominator);



		// If the covariance matrix has a positive diagonal, is symetric, and positive definite, then continue computing SDratio
		if(CovarianceOps.isValid(pooledCovarianceMatrix.getMatrix()) == 0){

			// Compute the lambda value for Box's M-statistic
			double term =  ((2*Math.pow(numericAtt,2)) + (3*numericAtt) - 1) / (6 * (numericAtt+1) * (dataset.numClasses()-1));
			double lambda = 1 - (term * sum);

			// Compute the Box's M-statistic
			double division = 0;
			double insideM = 0;
			for(int k=0;k<dataset.numClasses();k++){				
				// gambiarra de Rodrigo para evitar divisao por zero:
				if(covarianceMatrixPerClass[k].determinant() == 0 || Double.isNaN(covarianceMatrixPerClass[k].determinant()))
					division = 0;
				else
					division = (pooledCovarianceMatrix.determinant()/covarianceMatrixPerClass[k].determinant());
				
				// mais uma gambiarra
				if(Double.isNaN(division))
					division = 0;
			
				
				// outra gambiarra do Rodrigo para evitar log de zero
				if(division <= 0)
					insideM = 0;
				else
					insideM += (instancesPerClass[k] - 1) * Math.log(division);
			}
			double Mstatistic = lambda * insideM;
			

			// Finally, compute the SD ratio based on the Mstatistic value
			double anotherDenominator = 0;
			for(int k=0;k<dataset.numClasses();k++){
				anotherDenominator += (instancesPerClass[k] - 1);
			}
			anotherDenominator = anotherDenominator * numericAtt;
			
			double exp = (Mstatistic/anotherDenominator);
			
			SDratio = Math.exp(exp);
			
		}

		// If the covariance matrix has any problem (which means determinant is zero and SDratio is not defined)
		else{
			SDratio = -100;
		}



		// Compute correlation between all pairs of variables (per class)
		int size = numericIndices.size();
		int numberOfPairs = (size*(size-1))/2;
		double correlationPerClassPerPair[][] = new double[dataset.numClasses()][numberOfPairs];
		int pair;
		double covariance;
		double varianceI,varianceJ;
		for(int k=0; k<dataset.numClasses();k++){
			pair = 0;
			// se certa classe nao abriga mais do que uma instancia, entao nao ha como calcular correlacao de pares 
			if(instancesPerClass[k] <=1){
				for(int z = 0;z<numberOfPairs;z++){
					correlationPerClassPerPair[k][z] = 0;}
				continue;
			}
			for(int i=0;i<(size-1);i++){

				for(int j=i+1;j<size;j++){
					covariance = covarianceMatrixPerClass[k].get(i,j);
					varianceI = covarianceMatrixPerClass[k].get(i,i);
					varianceJ = covarianceMatrixPerClass[k].get(j,j);
					if(varianceI == 0 || varianceJ == 0)
						correlationPerClassPerPair[k][pair] = 0;
					else
						correlationPerClassPerPair[k][pair] = Math.abs(covariance/(Math.sqrt(varianceI*varianceJ)));
					pair++;
				}
			}
		}
		// Average the correlation per pair and then per class
		averageAttributeCorrelation = 0;
		for(int k=0;k<dataset.numClasses();k++){
			averageAttributeCorrelation += Utils.mean(correlationPerClassPerPair[k]);
		}
		averageAttributeCorrelation /= dataset.numClasses();



		// compute skewness and kurtosis in a per attribute per class fashion
		int numberOfAttributes = numericIndices.size();
		double skewness[][] = new double[dataset.numClasses()][numberOfAttributes];
		double kurtosis[][] = new double[dataset.numClasses()][numberOfAttributes];
		double sumSkewness = 0;
		double sumKurtosis = 0;
		double subtracao = 0;
		double desvio = 0;
		for(int k=0;k<dataset.numClasses();k++){
			for(int j=0;j<numberOfAttributes;j++){
				sumSkewness = 0;
				sumKurtosis = 0;
				desvio = Math.sqrt(covarianceMatrixPerClass[k].get(j,j));
				if(desvio != 0 && instancesPerClass[k] > 1){
					for(int i=0;i<instancesPerClass[k];i++){
						subtracao = attributeValuesPerClass.get(k).get(i, j) - meanInstancePerClass[k].get(0,j);
						sumSkewness += Math.pow(subtracao, 3);
						sumKurtosis += Math.pow(subtracao, 4);
					}
					skewness[k][j] = sumSkewness/((instancesPerClass[k] - 1) * Math.pow(desvio,3));
					kurtosis[k][j] = sumKurtosis/((instancesPerClass[k] - 1) * Math.pow(desvio,4));
				}
				else{
					skewness[k][j] = 0;
					kurtosis[k][j] = 0;
				}	
			}
		}

		// Average the skewness and kurtosis values per attribute and per class
		for(int k=0;k<dataset.numClasses();k++){
			averageAttributeSkewness += Utils.mean(skewness[k]);
			averageAttributeKurtosis += Utils.mean(kurtosis[k]);
		}
		averageAttributeSkewness /= dataset.numClasses();
		averageAttributeKurtosis /= dataset.numClasses();


	}




	/**
	 * Retorna a entropia media de todos atributos nominais do dataset
	 */
	public double getAttEntropy(){
		if(nominalAtt <=0)
			return -100;
		else
			return averageEntropy;
	}


	public double getClassEntropy(){
		if(nominalAtt <=0)
			return -100;
		else
			return classEntropy;
	}


	public double getInfoGain(){
		if(nominalAtt <=0)
			return -100;
		else
			return averageInfoGain;
	}


	public double getMutualInformation(){
		if(nominalAtt <=0)
			return -100;
		else
			return averageMutualInformation;
	}


	public double getEquivalentNumberOfAttributes(){
		if(nominalAtt <=0)
			return -100;
		else
			return equivalentNumberOfAttributes;
	}


	public double getNSratio(){
		if(nominalAtt <=0)
			return -100;
		else
			return NSratio;
	}


	public double getGainRatio(){
		if(nominalAtt <=0)
			return -100;
		else
			return averageGainRatio;
	}


	public double getSDratio(){
		if(numericAtt <= 1)
			return -100;
		else
			return SDratio;
	}


	public double getAverageAttributeCorrelation() {
		if(numericAtt <=1)
			return -100;
		else
			return averageAttributeCorrelation;
	}


	public double getAverageAttributeSkewness(){
		if(numericAtt <=0)
			return -100;
		else
			return averageAttributeSkewness;
	}


	public double getAverageAttributeKurtosis(){
		if(numericAtt <=0)
			return -100;
		else
			return averageAttributeKurtosis;
	}


	public int getNumAtt(){
		return this.numAtt;
	}


	public double getDimensionality() {
		return dimensionality;
	}


	public double getMissingValues(){
		return this.missingValues;
	}


	public double getPercentageMissingValues() {
		return percentageMissingValues;
	}


	public int getNumInstances(){
		return this.numInstances;
	}

	public int getNumClasses(){
		return this.numClasses;
	}

	public int getMeanClass() {
		return meanClass;
	}

	public int getMinClass() {
		return minClass;
	}

	public int getMaxClass() {
		return maxClass;
	}

	public int getNumericAtt() {
		return numericAtt;
	}

	public int getNomAtt() {
		return nominalAtt;
	}

	public int getDistribuicaoClasses(int i) {
		return distribuicaoClasses[i];
	}

	public int[] getDistribuicaoClasses() {
		return distribuicaoClasses;
	}


	public String getName(){
		return this.name; 
	}


	public double getPercentageNominal() {
		return percentageNominal;
	}


	public double getPercentageNumeric() {
		return percentageNumeric;
	}


	public int getBinaryAtt(){
		return numBinaryAtt;
	}


	public double info(double counts[]) {

		int total = 0;
		double x = 0;
		for (int j = 0; j < counts.length; j++) {
			x -= xlogx(counts[j]);
			total += counts[j];
		}
		return x + xlogx(total);
	}

	public double xlogx(double c) {

		if (c == 0) {
			return 0.0;
		}
		return c * Utils.log2(c);
	}
}

