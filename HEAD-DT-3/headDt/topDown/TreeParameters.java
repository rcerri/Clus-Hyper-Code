package headDt.topDown;

import java.io.Serializable;

public class TreeParameters implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** whether the tree will be pruned */
	private boolean m_prune = true;
	
	/** The confidence factor for error-based pruning. */
	float m_CF;
	
	/** The ad-hoc parameter for minimum-error pruning. */
	float m;
	
	/** The number of Standard Errors for Pessimistic Error Pruning/Cost-Complexity Pruning (0.5,1,1.5,2) */
	double m_numberOfSEs;

	/** whether the tree will be collapsed, i.e., pre-pruning with training error */
	private boolean m_collapse;
	
	/** preciso descobrir o que isso faz */
	private boolean m_cleanup = true;
	
	/** whether MDL correction will be performed */
	private boolean m_useMDLCorrection = false;
	
	/** whether Laplace correction will be performed */
	private boolean m_useLaplace;
	
	/** the pruning algorithm to be used */
	private int m_pruningType; 
	// 0 --> No pruning   1 --> Error-based pruning    
	// 2 --> Minimum-error pruning    3 --> Pessimistic-error pruning
	// 4 --> Cost-complexity pruning   5 --> Reduced-error pruning
	
	/** the number of folds to be selected by the pruning algorithms that need a validation set */
	private int m_numFoldPruning;
	
	/** a seed for dividing the training set in training/validation for pruning */
	private int m_seed = 1234;
	
	/** missing value strategy for classifying new instances */
	private int m_missingValueClassification;
	// 0 --> Explore all branches combining the results (C4.5 strategy)
	// 1 --> Halt the classification in the current node and assign most probable class
	// 2 --> Take the route to the most probable bag
	

	
	
	public TreeParameters(boolean prune, boolean collapse, boolean MDLCorrection,
			          boolean laplace, int pruningType, int numFoldPruning){
		
		this.m_prune = prune;
		this.m_collapse = collapse;
		this.m_useMDLCorrection = MDLCorrection;
		this.m_useLaplace = laplace;
		this.m_pruningType = pruningType;
		this.m_numFoldPruning = numFoldPruning;

	}
	
	public TreeParameters(){}

	
	public boolean prune() {
		return m_prune;
	}

	public void set_prune(boolean m_prune) {
		this.m_prune = m_prune;
	}

	public boolean collapse() {
		return m_collapse;
	}

	public void set_collapse(boolean m_collapse) {
		this.m_collapse = m_collapse;
	}

	public boolean useMDLCorrection() {
		return m_useMDLCorrection;
	}

	public void set_MDLCorrection(boolean m_useMDLCorrection) {
		this.m_useMDLCorrection = m_useMDLCorrection;
	}

	public boolean useLaplace() {
		return m_useLaplace;
	}

	public void setLaplace(boolean m_useLaplace) {
		this.m_useLaplace = m_useLaplace;
	}

	

	public int getpruningType() {
		return m_pruningType;
	}

	public void setPruningType(int m_pruningType) {
		this.m_pruningType = m_pruningType;
	}

	public int getNumFoldPruning() {
		return m_numFoldPruning;
	}

	public void setNumFoldPruning(int m_numFoldPruning) {
		this.m_numFoldPruning = m_numFoldPruning;
	}

	
	public float getM_CF() {
		return m_CF;
	}

	public void setM_CF(float m_CF) {
		this.m_CF = m_CF;
	}

	public void setCleanup(boolean m_cleanup) {
		this.m_cleanup = m_cleanup;
	}

	public boolean cleanup() {
		return m_cleanup;
	}

	public void setSeed(int m_seed) {
		this.m_seed = m_seed;
	}

	public int getSeed() {
		return m_seed;
	}
	
	public float getM() {
		return m;
	}

	public void setM(float m) {
		this.m = m;
	}
	
	public double getNumberOfSEs() {
		return m_numberOfSEs;
	}

	public void setNumberOfSEs(double m_numberOfSEs) {
		this.m_numberOfSEs = m_numberOfSEs;
	}

	public void setMissingValueClassification(int m_missingValueClassification) {
		this.m_missingValueClassification = m_missingValueClassification;
	}

	public int getMissingValueClassification() {
		return m_missingValueClassification;
	}

}
