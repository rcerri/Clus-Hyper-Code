package headDt.topDown;

import java.util.ArrayList;
import java.util.Enumeration;

import headDt.split.Measure;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Matrix;
import weka.core.Utils;


@SuppressWarnings("deprecation")
public class UnivariateBinarySplit extends UnivariateSplit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Attributes in the left branch. */
	ArrayList<Integer> leftBranch = new ArrayList<Integer>();


	/**
	 * @param attIndex
	 * @param minNoObj
	 * @param sumOfWeights
	 * @param measure
	 */

	public UnivariateBinarySplit(int attIndex,int minNoObj, double sumOfWeights,boolean useMDLcorrection,Measure measure, int missingValueSplit, int missingValueDist){
		super(attIndex,minNoObj,sumOfWeights,useMDLcorrection,measure,missingValueSplit,missingValueDist);
	}


	@Override
	public String rightSide(int index, Instances data) {
		StringBuffer text;

		text = new StringBuffer();
		if (data.attribute(m_attIndex).isNominal()){
			StringBuffer left = new StringBuffer();
			for(int i=0;i<leftBranch.size();i++){
				if(i == 0)
					left.append(data.attribute(m_attIndex).value(leftBranch.get(i)));
				else
					left.append(" | " + data.attribute(m_attIndex).value(leftBranch.get(i)));
			}
			if(index == 0)
				text.append(" = "+	left);
			else
				text.append(" != "+ left);
		}
		else
			if (index == 0)
				text.append(" <= "+	Utils.doubleToString(m_splitPoint,6));
			else
				text.append(" > "+	Utils.doubleToString(m_splitPoint,6));
		return text.toString();
	}



	@Override
	public int whichSubset(Instance instance) throws Exception {
		if (instance.isMissing(m_attIndex))
			return -1;
		else{
			if (instance.attribute(m_attIndex).isNominal()){
				if(leftBranch.contains((int)instance.value(m_attIndex)))
					return 0;
				else
					return 1;
			}
			else
				if (Utils.smOrEq(instance.value(m_attIndex),m_splitPoint))
					return 0;
				else
					return 1;
		}
	}



	public void handleEnumeratedAttribute(Instances trainInstances)
			throws Exception {

		Instance instance;
		Distribution tempDistribution = new Distribution(m_complexityIndex,trainInstances.numClasses());
		double[] meanClass = new double[trainInstances.numClasses()];
		double[][] classProbability = new double[m_complexityIndex][trainInstances.numClasses()];
		m_numSubsets = 0; // for the time being, assume no split was generated

		// initialize the vector of mean class probability
		for (int j=0; j<meanClass.length; j++) {
			meanClass[j]=0;
		}

		//	trainInstances.sort(m_attIndex);


		Enumeration<?> enu = trainInstances.enumerateInstances();
		double value = 0;
		while (enu.hasMoreElements()) {
			instance = (Instance) enu.nextElement();
			if (!instance.isMissing(m_attIndex))
				tempDistribution.add((int)instance.value(m_attIndex),instance);
			// Missing Value Strategy
			else{
				// Unsupervised Imputation
				if(m_missingValueSplit == 1){
					//instance.setValue(m_attIndex,trainInstances.meanOrMode(m_attIndex));
					value = trainInstances.meanOrMode(m_attIndex);
					tempDistribution.add((int)value, instance);
				}

				// Supervised Imputation
				if(m_missingValueSplit == 2){
					int theClass = (int)instance.classValue();
					//instance.setValue(m_attIndex,classes[theClass].meanOrMode(m_attIndex));
					value = classes[theClass].meanOrMode(m_attIndex);
					tempDistribution.add((int)value,instance);
				}
			}
		}


		// calculate the class probability matrix
		for(int i=0; i<classProbability.length;i++){
			for(int j=0; j<classProbability[0].length;j++){
				if(tempDistribution.perBag(i) == 0)
					classProbability[i][j] = 0;
				else
					classProbability[i][j] = tempDistribution.prob(j,i);
			}
		}

		// calculate the vector of mean class probability
		for(int j=0; j<meanClass.length;j++){
			meanClass[j] = tempDistribution.prob(j);
		}

		// calculate the covariance matrix
		double[][] covariance = new double[trainInstances.numClasses()][trainInstances.numClasses()];
		for (int i1=0; i1<trainInstances.numClasses(); i1++) {
			for (int i2=0; i2<trainInstances.numClasses(); i2++) {
				double element = 0;
				for (int j=0; j<m_complexityIndex; j++) {
					element += (classProbability[j][i2]-meanClass[i2])*(classProbability[j][i1]-meanClass[i1])*tempDistribution.perBag(j);
				}
				covariance[i1][i2] = element;
			}
		}

		Matrix matrix = new Matrix(covariance);
		double[][] eigenvector = new double[trainInstances.numClasses()][trainInstances.numClasses()];
		double[] eigenValues = new double[trainInstances.numClasses()];
		matrix.eigenvalueDecomposition(eigenvector, eigenValues);

		// find index of the largest eigenvalue
		int index=0;
		double largest = eigenValues[0];
		for (int i=1; i<eigenValues.length; i++) {
			if (eigenValues[i]>largest) {
				index=i;
				largest = eigenValues[i];
			}
		}

		// calculate the first principle component
		double[] FPC = new double[trainInstances.numClasses()];
		for (int i=0; i<FPC.length; i++) {
			FPC[i] = eigenvector[i][index];
		}

		//calculate the first principle component scores
		//System.out.println("the first principle component scores: ");
		double[] Sa = new double[m_complexityIndex];
		for (int i=0; i<Sa.length; i++) {
			Sa[i]=0;
			for (int j=0; j<trainInstances.numClasses(); j++) {
				Sa[i] += FPC[j]*classProbability[i][j];
			}
		}

		// calculate all possible splits constrained by the order of Sa
		int[] indices = new int[m_complexityIndex];
		int[] tempIndices = new int[m_complexityIndex];
		indices = Utils.sort(Sa);
		System.arraycopy(indices,0,tempIndices,0,m_complexityIndex);


		// attOnLeft means the number of attributes on the left edge
		int attOnLeft = 1;

		// bestSplit will hold the optimal number of attributes on the left considering the order of Sa
		int bestSplit = 0;

		// measureValue is an auxiliary double to help calculating the best split
		double measureValue;

		// initialize measureValue
		if(m_measure.isMaximized())
			measureValue = Double.NEGATIVE_INFINITY;
		else
			measureValue = Double.POSITIVE_INFINITY;

		// create a copy of the training instances
		Instances data = new Instances(trainInstances);

		// subset (0 or 1) to which the instance belongs to
		double subset = 0;

		// try combinations of attributes based on the ordering of Sa
		while(attOnLeft < m_complexityIndex){

			// find out who is the attribute with lowest Sa
			int left = Utils.minIndex(tempIndices);

			// remove this attribute from the list
			tempIndices[left] = Integer.MAX_VALUE;

			// add this attribute to the left branch
			leftBranch.add(left);

			// initialize a two-bag distribution
			Distribution tryDist = new Distribution(2,trainInstances.numClasses());  //NOVO

			// now lets create the distribution that respects the new split created
			Enumeration<?> enumeration = data.enumerateInstances();
			while (enumeration.hasMoreElements()) {				
				instance = (Instance) enumeration.nextElement();
				subset = whichSubset(instance);
				if (subset != -1){
					if(subset == 0){
						tryDist.add(0,instance);
					}
					else
						tryDist.add(1,instance);
				}
			}

			// once the distribution is created, lets calculate the split measure value
			if (tryDist.check(m_minNoObj)) {
				measureValue = m_measure.getValue(tryDist,m_sumOfWeights,m_missingValueSplit);
				if(isBetter(measureValue,m_measure_value)){
					m_numSubsets = 2; //guarantees a valid split was found
					m_measure_value = measureValue;
					m_distribution = (Distribution)tryDist.clone();
					bestSplit = attOnLeft;
				}
			}
			attOnLeft++;
		}

		// set the attributes that will be located on the left branch
		leftBranch.clear();
		int numAttLeft = 1;
		while(numAttLeft <= bestSplit){
			int left = Utils.minIndex(indices);
			indices[left] = Integer.MAX_VALUE;
			leftBranch.add(left);
			numAttLeft++;
		}
	}






	public boolean isBetter(double a, double b){
		if(m_measure.isMaximized()){
			if(a>=b) 
				return true;
			else 
				return false;
		}
		else{
			if(a <= b)
				return true;
			else
				return false;
		}
	}


}
