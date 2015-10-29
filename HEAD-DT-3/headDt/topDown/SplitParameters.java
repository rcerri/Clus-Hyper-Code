package headDt.topDown;

import java.io.Serializable;

public class SplitParameters implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** the strategy to define whether a split will be univariate, multivariate or mixed */
	private int m_SplitType;
	// 0 --> univariate  	// 1 --> multivariate	// 2 --> mixed
	
	private int m_obliqueStrategy;
	// 0 -->  1 -->   2 --> etc
	
	/** the strategy to define whether a split is better than other */
	private int m_BetterSplit = 0; 
	// 0 -->   1 -->    2 -->    3 -->   4 -->
	
	/** the pre-pruning strategy */
	private int m_StoppingCriteria; 
	// 0 --> homogeneous  1 --> min number 2 --> min percentage 
	// 3 --> acc threshold  4 --> max depth
	
	/** whether nominal splits will be binary too */
	private boolean m_BinarySplits;
	
	/** the split measure to be used */
	private int m_SplitMeasure;
	// 15 split measures - see package "split"
	
	/** the minimum number of objects allowed in an internal node (i.e., pre-pruning) */
	private int m_minNumObj = 2;
	
	/** the minimum percentage of objects allowed in an internal node (i.e., pre-pruning) */
	private double m_minPercObj;
	
	/** minimum accuracy allowed for converting a node in leaf (i.e., pre-pruning) */
	private double m_minAccuracy;
	
	/** maximum depth allowed for building the tree (i.e., pre-pruning) */
	private int m_maxDepth;
	
	/** missing value strategy during split */
	private int m_missingValueSplit;
	// 0 --> ignore missing (FriedmanBreiman)  1 --> unsupervised imputation (ClarkNiblett)  
	//2 --> supervised imputation (LohShi)  3 --> weight the splitting criterion value (Quinlan)
	
	/** Missing value strategy for distributing training instances to child nodes */
	private int m_missingValueDistribution;
	// 0 --> weight with bag probability (C4.5/Kononenko1984)  1 --> ignore missing value (Quinlan1986)
	// 2 --> unsupervised imputation (Quinlan1989)  3 --> supervised imputation (Quinlan1989?) 
	// 4 --> assign instance to all partitions (Friedman, 1977)  5 --> add to bag with largest number of inst (Quinlan 1989)
	// 6 --> assign to bag with greatest probability regarding the respective inst class (Loh and Shih 1997) 
	
	
	
	public boolean isBinarySplits() {
		return m_BinarySplits;
	}

	public void BinarySplits(boolean m_BinarySplits) {
		this.m_BinarySplits = m_BinarySplits;
	}

	public int getSplitMeasure() {
		return m_SplitMeasure;
	}

	public void setSplitMeasure(int m_SplitMeasure) {
		this.m_SplitMeasure = m_SplitMeasure;
	}

	public int getMinNumObj() {
		return m_minNumObj;
	}

	public void setMinNumObj(int m_minNumObj) {
		this.m_minNumObj = m_minNumObj;
	}
	
	public int getBetterSplit() {
		return m_BetterSplit;
	}

	public void setBetterSplit(int m_BetterSplit) {
		this.m_BetterSplit = m_BetterSplit;
	}
	
	public void setMinAccuracy(double m_minAccuracy) {
		this.m_minAccuracy = m_minAccuracy;
	}

	public double getMinAccuracy() {
		return m_minAccuracy;
	}

	public void setMinPercObj(double m_minPercObj) {
		this.m_minPercObj = m_minPercObj;
	}

	public double getMinPercObj() {
		return m_minPercObj;
	}

	public void setStoppingCriteria(int m_StoppingCriteria) {
		this.m_StoppingCriteria = m_StoppingCriteria;
	}

	public int getStoppingCriteria() {
		return m_StoppingCriteria;
	}

	public void setMaxDepth(int m_maxDepth) {
		this.m_maxDepth = m_maxDepth;
	}

	public int getMaxDepth() {
		return m_maxDepth;
	}

	public void setMissingValueSplit(int m_missingValueSplit) {
		this.m_missingValueSplit = m_missingValueSplit;
	}

	public int getMissingValueSplit() {
		return m_missingValueSplit;
	}

	public void setMissingValueDistribution(int m_missingValueDistribution) {
		this.m_missingValueDistribution = m_missingValueDistribution;
	}

	public int getMissingValueDistribution() {
		return m_missingValueDistribution;
	}

	public void setSplitType(int m_SplitType) {
		this.m_SplitType = m_SplitType;
	}

	public int getSplitType() {
		return m_SplitType;
	}

	public void setObliqueStrategy(int m_obliqueStrategy) {
		this.m_obliqueStrategy = m_obliqueStrategy;
	}

	public int getObliqueStrategy() {
		return m_obliqueStrategy;
	}

}
