package headDt.topDown;

import java.util.Enumeration;

import ec.gp.GPIndividual;
import headDt.split.GainRatio;
import headDt.split.Measure;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class UnivariateSplit extends AbstractSplit {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Desired number of branches. */
	protected int m_complexityIndex;        

	/** Minimum number of objects in a split.   */
	protected int m_minNoObj;         

	/** Use MDL correction? */
	private boolean m_useMDLcorrection;           

	/** Measure value */ 
	protected double m_measure_value;

	/** Gain Ratio - special case */
	protected double m_gainRatio;

	/** Split Missing Value strategy */
	protected int m_missingValueSplit;


	/** The sum of the weights of the instances. */
	protected double m_sumOfWeights;  

	/** Number of split points. */
	protected int m_index;            


	/**
	 * Initializes the split model.
	 */
	public UnivariateSplit(int attIndex,int minNoObj, double sumOfWeights,boolean useMDLcorrection, Measure measure, int missingValueSplit, int missingValueDist) {
		// Get index of attribute to split on.
		m_attIndex = attIndex;
		// Set minimum number of objects.
		m_minNoObj = minNoObj;
		// Set the sum of the weights
		m_sumOfWeights = sumOfWeights;
		// Whether to use the MDL correction for numeric attributes
		m_useMDLcorrection = useMDLcorrection;
		//The measure to be calculated
		m_measure = measure;
		m_missingValueSplit = missingValueSplit;
		m_missingValueDistribution = missingValueDist;
	}

	/**
	 * Creates a univariate split on the given data. Assumes that none of
	 * the class values is missing.
	 *
	 * @exception Exception if something goes wrong
	 */
	@Override
	public void buildSplit(Instances trainInstances) throws Exception {

		// Initialize the remaining instance variables.
		m_gainRatio = 0;
		m_numSubsets = 0;
		m_splitPoint = Double.POSITIVE_INFINITY;
		m_measure_value = m_measure.initialize();


		// If missing value strategy is supervised imputation, create a list of instances, one position per class
		if(m_missingValueSplit == 2 || m_missingValueDistribution == 3){
			createListOfClasses(trainInstances);
		}

		// Different treatment for enumerated and numeric attributes.
		if (trainInstances.attribute(m_attIndex).isNominal()) {
			m_complexityIndex = trainInstances.attribute(m_attIndex).numValues();
			m_index = m_complexityIndex;
			handleEnumeratedAttribute(trainInstances);
		}else{
			m_complexityIndex = 2;
			m_index = 0;
			handleNumericAttribute(trainInstances);
		}
	}    


	public void handleEnumeratedAttribute(Instances trainInstances)
			throws Exception {

		Instance instance;
		m_distribution = new Distribution(m_complexityIndex,trainInstances.numClasses());

		Enumeration<?> enu = trainInstances.enumerateInstances();
		double value = 0;
		while (enu.hasMoreElements()) {
			instance = (Instance) enu.nextElement();

			if (!instance.isMissing(m_attIndex))
				m_distribution.add((int)instance.value(m_attIndex),instance);

			// Missing Value Strategy
			else{

				// Unsupervised Imputation
				if(m_missingValueSplit == 1){
					//	instance.setValue(m_attIndex,trainInstances.meanOrMode(m_attIndex));
					value = trainInstances.meanOrMode(m_attIndex);
					m_distribution.add((int)value, instance);
				}

				// Supervised Imputation
				if(m_missingValueSplit == 2){
					int theClass = (int)instance.classValue();
					//	instance.setValue(m_attIndex,classes[theClass].meanOrMode(m_attIndex));
					value = classes[theClass].meanOrMode(m_attIndex);
					m_distribution.add((int)value,instance);
				}
			}
		}

		// Check if minimum number of Instances in at least two subsets.
		if (m_distribution.check(m_minNoObj)) {
			m_numSubsets = m_complexityIndex;
			//System.out.println("NominalSplit");
			m_measure_value = m_measure.getValue(m_distribution,m_sumOfWeights,m_missingValueSplit);

/*			TESTE.setNumClass(m_distribution.numClasses());
			
			double[] perClass = new double[m_distribution.numClasses()];
			for (int i = 0; i < perClass.length; i++) {
				perClass[i] = m_distribution.perClass(i);
				System.out.print(perClass[i]+",");
				// 		numEx += bags.perClass(i);
			}
			System.out.print("  -> ");
			TESTE.setPerClass(perClass);
			//TESTE.setNumEx(numEx);

			((GPIndividual)TESTE.getNewInd()).trees[0].child.eval(
					TESTE.getStateNew(),
					TESTE.getThreadnumNew(),
					TESTE.getInput(),
					TESTE.getStackNew(),
					((GPIndividual)TESTE.getNewInd()),
					EvolvedAlgorithm.indGP);

			System.out.println("PIMBA = "+TESTE.input.x);

			m_measure_value = TESTE.input.x; 

*/
		}
	}

	/**
	 * Creates split on numeric attribute.
	 *
	 * @exception Exception if something goes wrong
	 */
	public void handleNumericAttribute(Instances trainInstancesOriginal)
			throws Exception {

		int firstMiss, firstMissOriginal;
		int next = 1;
		int last = 0;
		int splitIndex = -1;
		double currentMeasureValue;
		double minSplit;
		Instance instance;
		int i;
		boolean hasMissing = false;

		// Current attribute is a numeric attribute.
		m_distribution = new Distribution(2,trainInstancesOriginal.numClasses());

		// For replacing missing values
		Instances trainInstances = new Instances(trainInstancesOriginal);

		// sort attribute (necessary to be efficient)
		trainInstances.sort(trainInstances.attribute(m_attIndex));
		trainInstancesOriginal.sort(trainInstancesOriginal.attribute(m_attIndex));

		// Only Instances with known values are relevant.
		Enumeration<?> enu = trainInstancesOriginal.enumerateInstances();
		i = 0;
		while (enu.hasMoreElements()) {
			instance = (Instance) enu.nextElement();
			if (instance.isMissing(m_attIndex))
				break;
			m_distribution.add(1,instance);
			i++;
		}

		firstMissOriginal = firstMiss = i;


		if(m_missingValueSplit == 1 || m_missingValueSplit == 2){
			// if there are missing values in attribute m_attIndex
			if(trainInstances.attributeStats(m_attIndex).missingCount > 0){
				hasMissing = true;
				for(int j=0; j< trainInstances.numInstances();j++){
					if(trainInstances.instance(j).isMissing(m_attIndex)){
						// Unsupervised Imputation
						if(m_missingValueSplit == 1){
							trainInstances.instance(j).setValue(m_attIndex, trainInstances.meanOrMode(m_attIndex));
						}
						// Supervised Imputation
						else{
							int theClass = (int)trainInstances.instance(j).classValue();
							trainInstances.instance(j).setValue(m_attIndex, classes[theClass].meanOrMode(m_attIndex));
						}
					}
				}
			}
			firstMiss = trainInstances.numInstances();
		}


		// Compute minimum number of Instances required in each subset.
		minSplit =  0.1*(m_distribution.total())/ ((double)trainInstances.numClasses());
		if (Utils.smOrEq(minSplit,m_minNoObj)) 
			minSplit = m_minNoObj;
		else
			if (Utils.gr(minSplit,25)) 
				minSplit = 25;

		// Enough Instances with known values?
		if (Utils.sm((double)firstMiss,2*minSplit))
			return;


		while (next < firstMiss) {
			if (trainInstances.instance(next-1).value(m_attIndex)+1e-5 < trainInstances.instance(next).value(m_attIndex)) { 
				
				// Move class values for all Instances up to next possible split point.
				m_distribution.shiftRange(1,0,trainInstances,last,next);
				
				// Check if enough Instances in each subset and compute values for criteria.
				if (Utils.grOrEq(m_distribution.perBag(0),minSplit) && Utils.grOrEq(m_distribution.perBag(1),minSplit)) {
					currentMeasureValue = m_measure.getValue(m_distribution,m_sumOfWeights,m_missingValueSplit);
					if(m_measure.isMaximized()){
						if (Utils.gr(currentMeasureValue,m_measure_value)) {
							m_measure_value = currentMeasureValue;
							splitIndex = next-1;
						}
					}
					else{
						if (Utils.sm(currentMeasureValue,m_measure_value)) {
							m_measure_value = currentMeasureValue;
							splitIndex = next-1;
						}
					}
					m_index++;
				}
				last = next;
			}
			next++;
		}

		// Was there any useful split?
		if (m_index == 0)
			return;

		// Compute modified information gain (ratio) for best split.
		if(m_measure.getCriterionIndex() == 0 || m_measure.getCriterionIndex() == 14 || m_measure.getCriterionIndex() == 99){
			if (m_useMDLcorrection) {
				m_measure_value = m_measure_value-(Utils.log2(m_index)/m_sumOfWeights);
			}
			if (Utils.smOrEq(m_measure_value,0))
				return;
		}
		
		
		
		//	m_measure_value = m_measure.initialize();

		// Set instance variables' values to values for best split.
		m_numSubsets = 2;
		m_splitPoint =	(trainInstances.instance(splitIndex+1).value(m_attIndex)+ trainInstances.instance(splitIndex).value(m_attIndex))/2;

		// In case we have a numerical precision problem we need to choose the
		// smaller value
		if (m_splitPoint == trainInstances.instance(splitIndex + 1).value(m_attIndex)) {
			m_splitPoint = trainInstances.instance(splitIndex).value(m_attIndex);
		}



		i = splitIndex + 1;
		if(hasMissing){
			i = 0;
			while (trainInstancesOriginal.get(i).value(m_attIndex) <= m_splitPoint) {
				i++;
			}
		}

		// Restore distributioN for best split.
		m_distribution = new Distribution(2,trainInstancesOriginal.numClasses());
		m_distribution.addRange(0,trainInstancesOriginal,0,i);
		m_distribution.addRange(1,trainInstancesOriginal,i,firstMissOriginal);

		if(m_measure.getCriterionIndex() == 14)
			m_gainRatio = ((GainRatio)m_measure.getCriterion()).getValue(m_measure_value, m_distribution, m_sumOfWeights); 
	}


	public double measureValue() {

		return m_measure_value;
	}

	/**
	 * Prints left side of condition..
	 *
	 * @param data training set.
	 */
	public String leftSide(Instances data) {

		return data.attribute(m_attIndex).name();
	}

	/**
	 * Prints the condition satisfied by instances in a subset.
	 *
	 * @param index of subset 
	 * @param data training set.
	 */
	public String rightSide(int index,Instances data) {

		StringBuffer text;

		text = new StringBuffer();
		if (data.attribute(m_attIndex).isNominal())
			text.append(" = "+
					data.attribute(m_attIndex).value(index));
		else
			if (index == 0)
				text.append(" <= "+
						Utils.doubleToString(m_splitPoint,6));
			else
				text.append(" > "+
						Utils.doubleToString(m_splitPoint,6));
		return text.toString();
	}

	/**
	 * Returns a string containing java source code equivalent to the test
	 * made at this node. The instance being tested is called "i".
	 *
	 * @param index index of the nominal value tested
	 * @param data the data containing instance structure info
	 * @return a value of type 'String'
	 */
	public String sourceExpression(int index, Instances data) {

		StringBuffer expr = null;
		if (index < 0) {
			return "i[" + m_attIndex + "] == null";
		}
		if (data.attribute(m_attIndex).isNominal()) {
			expr = new StringBuffer("i[");
			expr.append(m_attIndex).append("]");
			expr.append(".equals(\"").append(data.attribute(m_attIndex)
					.value(index)).append("\")");
		} else {
			expr = new StringBuffer("((Double) i[");
			expr.append(m_attIndex).append("])");
			if (index == 0) {
				expr.append(".doubleValue() <= ").append(m_splitPoint);
			} else {
				expr.append(".doubleValue() > ").append(m_splitPoint);
			}
		}
		return expr.toString();
	}  


	/**
	 * Returns the minsAndMaxs of the index.th subset.
	 */
	public double [][] minsAndMaxs(Instances data, double [][] minsAndMaxs,
			int index) {

		double [][] newMinsAndMaxs = new double[data.numAttributes()][2];

		for (int i = 0; i < data.numAttributes(); i++) {
			newMinsAndMaxs[i][0] = minsAndMaxs[i][0];
			newMinsAndMaxs[i][1] = minsAndMaxs[i][1];
			if (i == m_attIndex)
				if (data.attribute(m_attIndex).isNominal())
					newMinsAndMaxs[m_attIndex][1] = 1;
				else
					newMinsAndMaxs[m_attIndex][1-index] = m_splitPoint;
		}

		return newMinsAndMaxs;
	}


	/** Returns index of subset instance is assigned to.
	 * Returns -1 if instance is assigned to more than one subset.
	 *
	 * @exception Exception if something goes wrong
	 */
	public int whichSubset(Instance instance) 
			throws Exception {

		if (instance.isMissing(m_attIndex))
			return -1;
		else{
			if (instance.attribute(m_attIndex).isNominal())
				return (int)instance.value(m_attIndex);
			else
				if (Utils.smOrEq(instance.value(m_attIndex),m_splitPoint))
					return 0;
				else
					return 1;
		}
	}

	/**
	 * Checks if generated model is valid.
	 */
	public boolean checkModel() {
		if (m_numSubsets > 0)
			return true;
		else
			return false;
	}


	@Override
	public String getRevision() {
		// TODO Auto-generated method stub
		return null;
	}

	public double [] weights(Instance instance) {

		double [] weights;
		int i;

		if (instance.isMissing(m_attIndex)) {
			weights = new double [m_numSubsets];
			for (i=0;i<m_numSubsets;i++)
				weights [i] = m_distribution.perBag(i)/m_distribution.total();
			return weights;
		}else{
			return null;
		}
	}

	public double getGainRatio(){
		return m_gainRatio;
	}




}
