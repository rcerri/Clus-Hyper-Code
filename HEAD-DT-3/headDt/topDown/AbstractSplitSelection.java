package headDt.topDown;

import weka.core.Instances;
import weka.core.Utils;
import headDt.split.GainRatio;


public abstract class AbstractSplitSelection {

	/** for serialization */
	private static final long serialVersionUID = 3372204862440821989L;

	/** Minimum number of objects in interval for pre-pruning. */
	protected int m_minNoObj;       

	/** Minimum percentage of objects in interval for pre-pruning. */
	protected double m_minPercObj;

	/** Accuracy threshold for pre-pruning */
	protected double m_minAccuracy;

	/** Depth threshold for pre-pruning */
	protected int m_maxDepth;

	/** Use MDL correction? */
	protected boolean m_useMDLcorrection = true;         

	/** All the training data */
	protected  Instances m_allData; 

	/** Dataset total size (for pre-pruning) */
	protected int m_datasetTotalSize;

	protected int m_betterSplit;

	protected int m_stoppingCriteria;

	/** Whether nominal splits are binary */
	protected boolean m_binarySplit;

	/** Missing Value Strategy for the split */
	protected int m_missingValueSplit;

	/** Missing Value Strategy for distributing instances */
	protected int m_missingValueDistribution;


	/**
	 * Sets reference to training data to null.
	 */
	public final void cleanup() {

		m_allData = null;
	}

	public abstract AbstractSplit selectModel(Instances data, int depth);

	public abstract AbstractSplit selectModel(Instances train, Instances test, int depth);

	public boolean isLeaf(Distribution d, int depth){
		switch(m_stoppingCriteria){

		// Homogeneous Training set
		case 0: if(Utils.eq(d.total(),d.perClass(d.maxClass())))
			return true;
		else
			return false;

		// Minimum number of instances reached (plus class homogeneity) - Estudar o pq da necessidade de ser 2*minNumber
		case 1: if(Utils.sm(d.total(),2*m_minNoObj) || Utils.eq(d.total(),d.perClass(d.maxClass())))
			return true;
		else
			return false;

		// Minimum percentage of instances reached (plus class homogeneity)
		case 2: if(Utils.smOrEq(d.total(),((double)m_datasetTotalSize)*m_minPercObj) || Utils.eq(d.total(),d.perClass(d.maxClass())))
			return true;
		else
			return false;

		// Minimum accuracy threshold (plus class homogeneity)
		case 3: if(Utils.grOrEq(d.perClass(d.maxClass()),(m_minAccuracy*d.total())) ||	Utils.eq(d.total(),d.perClass(d.maxClass())))
			return true;
		else
			return false;

		// Maximum depth (plus class homogeneity)
		case 4: if(Utils.grOrEq(depth, m_maxDepth) || Utils.eq(d.total(),d.perClass(d.maxClass())))
			return true;
		else
			return false;

		default:
			return true;

		}
	}

	public boolean isBetter(AbstractSplit split, double average, double best){
		boolean returnValue = false;
		switch(m_betterSplit){
		case(0): 
			// Use 1E-3 here to get a closer approximation to the original implementation. Tests whether measure should be min or maximized
			
			// scenario for the gain ratio measure in which the split must be better than average and also better than the best so far
			if(split.getMeasure().isMaximized() && split.getMeasure().getCriterionIndex() == 14){
				if ((split.measureValue() >= (average-1E-3)) && Utils.gr(((UnivariateSplit)split).getGainRatio(),best))
					returnValue = true;
				else
					returnValue = false;
			}
		//scenario for the remaining measures
			else if(split.getMeasure().isMaximized()){
				if ((split.measureValue() >= (average-1E-3)) && Utils.gr(split.measureValue(),best))
					returnValue = true;
				else
					returnValue = false;
			}
			else{
				if (split.measureValue() <= (average-1E-3) && Utils.sm(split.measureValue(),best))
					returnValue = true;
				else
					returnValue = false;
			}
		break;
		case(1): // in this scenario, the split must be better than the best so far
			if(split.getMeasure().isMaximized()){
				if (Utils.gr(split.measureValue(),best))
					returnValue = true;
				else
					returnValue = false;
			}
			else{
				if (Utils.sm(split.measureValue(),best))
					returnValue = true;
				else
					returnValue = false;
			}
		break;
		}
		return returnValue;
	}



	/** This method updates the node distribution with the missing values
	 *  according to a predefined missing value strategy, i.e., m_missingValueDistribution */
	public void distributeMissingValues(AbstractSplit splitModel, Instances data) throws Exception{

		switch(m_missingValueDistribution){

		// Add instances according to the Kononenko et al. 1984 strategy (same strategy of C4.5)
		// i.e., weighted by the proportion of cases with known values (bag probability)
		case(0): splitModel.distribution().addInstWithUnknown(data,splitModel.attIndex());
		break;

		//Ignore missing values - do nothing
		case(1): break;

		// Unsupervised Imputation
		case(2): splitModel.distribution().addInstWithUnknownImputation(splitModel, data,false);
		break;

		// Supervised Imputation
		case(3): splitModel.distribution().addInstWithUnknownImputation(splitModel, data,true);
		break;

		// Assign to all bags
		case(4): splitModel.distribution().addInstWithUnknownToAll(data, splitModel.attIndex());
		break;
		
		// Assign to most probable bag, i.e., max bag probability
		case(5): splitModel.distribution().addInstWithUnknownToMaxBag(data, splitModel.attIndex());
		break;
		
		// Assign to most probable bag regarding the same class of the current instance
		case(6): splitModel.distribution().addInstWithUnknownToMaxBagClass(data, splitModel.attIndex());
		break;
		
	/*	// Create new bag for missing values - to do
		case(7): if(splitModel.numSubsets() > 1){
			     	splitModel.distribution().addInstWithUnknownToMissingBag(data, splitModel.attIndex());
			     	}
		break;*/
		
		default: break;
		}
	}

	public int getM_minNoObj() {
		return m_minNoObj;
	}

	public void setM_minNoObj(int m_minNoObj) {
		this.m_minNoObj = m_minNoObj;
	}

	public double getM_minPercObj() {
		return m_minPercObj;
	}

	public void setM_minPercObj(double m_minPercObj) {
		this.m_minPercObj = m_minPercObj;
	}

	public double getM_minAccuracy() {
		return m_minAccuracy;
	}

	public void setM_minAccuracy(double m_minAccuracy) {
		this.m_minAccuracy = m_minAccuracy;
	}

	public int getM_maxDepth() {
		return m_maxDepth;
	}

	public void setM_maxDepth(int m_maxDepth) {
		this.m_maxDepth = m_maxDepth;
	}

	public boolean isM_useMDLcorrection() {
		return m_useMDLcorrection;
	}

	public void setM_useMDLcorrection(boolean m_useMDLcorrection) {
		this.m_useMDLcorrection = m_useMDLcorrection;
	}

	public Instances getM_allData() {
		return m_allData;
	}

	public void setM_allData(Instances m_allData) {
		this.m_allData = m_allData;
	}

	public int getM_datasetTotalSize() {
		return m_datasetTotalSize;
	}

	public void setM_datasetTotalSize(int m_datasetTotalSize) {
		this.m_datasetTotalSize = m_datasetTotalSize;
	}

	public int getM_betterSplit() {
		return m_betterSplit;
	}

	public void setM_betterSplit(int m_betterSplit) {
		this.m_betterSplit = m_betterSplit;
	}

	public int getM_stoppingCriteria() {
		return m_stoppingCriteria;
	}

	public void setM_stoppingCriteria(int m_stoppingCriteria) {
		this.m_stoppingCriteria = m_stoppingCriteria;
	}

	public boolean isM_binarySplit() {
		return m_binarySplit;
	}

	public void setM_binarySplit(boolean m_binarySplit) {
		this.m_binarySplit = m_binarySplit;
	}

	public int getM_missingValueSplit() {
		return m_missingValueSplit;
	}

	public void setM_missingValueSplit(int m_missingValueSplit) {
		this.m_missingValueSplit = m_missingValueSplit;
	}

	public int getM_missingValueDistribution() {
		return m_missingValueDistribution;
	}

	public void setM_missingValueDistribution(int m_missingValueDistribution) {
		this.m_missingValueDistribution = m_missingValueDistribution;
	}

}

