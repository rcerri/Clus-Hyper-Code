package headDt.topDown;

import weka.core.Instances;
import weka.core.Statistics;
import weka.core.Utils;


public class ErrorBasedPruning extends Tree {


	private static final long serialVersionUID = 1L;
	
	/** The confidence factor for pruning. */
	protected float m_CF;
	
	protected boolean m_subtreeRaising;

	


	public ErrorBasedPruning(AbstractSplitSelection toSelectLocModel, TreeParameters treeParam, int depth)
	throws Exception {
		super(toSelectLocModel,treeParam,depth);
		m_CF = treeParam.getM_CF();
		m_subtreeRaising = true;
		m_cleanup = treeParam.cleanup();
		m_collapseTheTree = treeParam.collapse();
	}
	
	public void buildTree(Instances data) throws Exception{
		super.buildTree(data);
		if(m_collapseTheTree)
			collapse();
		prune();
	}
	

	/**
	 * Returns a newly created tree.
	 *
	 * @param data the data to work with
	 * @return the new tree
	 * @throws Exception if something goes wrong
	 */
	protected ErrorBasedPruning getNewTree(Instances data) throws Exception {

		ErrorBasedPruning newTree = 
			new ErrorBasedPruning(m_toSelectModel, m_treeParam,m_depth+1);
		newTree.buildTree((Instances)data, m_subtreeRaising);

		return newTree;
	}


	public void prune() throws Exception {
		double errorsLargestBranch;
		double errorsLeaf;
		double errorsTree;
		int indexOfLargestBranch;
		ErrorBasedPruning largestBranch;
		int i;

		if (!m_isLeaf){

			// Prune all subtrees.
			for (i=0;i<m_sons.length;i++)
				((ErrorBasedPruning)son(i)).prune();

			// Compute error for largest branch
			indexOfLargestBranch = localModel().distribution().maxBag();
			if (m_subtreeRaising) {
				errorsLargestBranch = ((ErrorBasedPruning)son(indexOfLargestBranch)).getEstimatedErrorsForBranch((Instances)m_train);
			} else {
				errorsLargestBranch = Double.MAX_VALUE;
			}

			// Compute error if this Tree would be leaf
			errorsLeaf = getEstimatedErrorsForDistribution(localModel().distribution());

			// Compute error for the whole subtree
			errorsTree = getEstimatedErrors();

			// Decide if leaf is best choice.
			if (Utils.smOrEq(errorsLeaf,errorsTree+0.1) &&	Utils.smOrEq(errorsLeaf,errorsLargestBranch+0.1)){

				// Free son Trees
				m_sons = null;
				m_isLeaf = true;

				// Get NoSplit Model for node.
				m_localModel = new NoSplit(localModel().distribution());
				return;
			}

			// Decide if largest branch is better choice
			// than whole subtree.
			if (Utils.smOrEq(errorsLargestBranch,errorsTree+0.1)){
				largestBranch = ((ErrorBasedPruning)son(indexOfLargestBranch));
				m_sons = largestBranch.m_sons;
				m_localModel = largestBranch.localModel();
				m_isLeaf = largestBranch.m_isLeaf;
				newDistribution(m_train);
				prune();
			}
		}
	}


	/**
	 * Computes estimated errors for tree.
	 * 
	 * @return the estimated errors
	 */
	private double getEstimatedErrors(){

		double errors = 0;
		int i;

		if (m_isLeaf)
			return getEstimatedErrorsForDistribution(localModel().distribution());
		else{
			for (i=0;i<m_sons.length;i++)
				errors = errors+((ErrorBasedPruning)son(i)).getEstimatedErrors();
			return errors;
		}
	}

	/**
	 * Computes estimated errors for one branch.
	 *
	 * @param data the data to work with
	 * @return the estimated errors
	 * @throws Exception if something goes wrong
	 */
	private double getEstimatedErrorsForBranch(Instances data) 
	throws Exception {

		Instances [] localInstances;
		double errors = 0;
		int i;

		if (m_isLeaf)
			return getEstimatedErrorsForDistribution(new Distribution(data));
		else{
			Distribution savedDist = localModel().m_distribution;
			localModel().resetDistribution(data);
			localInstances = (Instances[])localModel().split(data);
			localModel().m_distribution = savedDist;
			for (i=0;i<m_sons.length;i++)
				errors = errors+ ((ErrorBasedPruning)son(i)).getEstimatedErrorsForBranch(localInstances[i]);
			return errors;
		}
	}

	/**
	 * Computes estimated errors for leaf.
	 * 
	 * @param theDistribution the distribution to use
	 * @return the estimated errors
	 */
	private double getEstimatedErrorsForDistribution(Distribution theDistribution){

		if (Utils.eq(theDistribution.total(),0))
			return 0;
		else
			return theDistribution.numIncorrect()+ addErrs(theDistribution.total(),theDistribution.numIncorrect(),m_CF);
	}


	public double addErrs(double N, double e, float CF){

		// Ignore stupid values for CF
		if (CF > 0.5) {
			System.err.println("WARNING: confidence value for pruning " +
			" too high. Error estimate not modified.");
			return 0;
		}

		// Check for extreme cases at the low end because the
		// normal approximation won't work
		if (e < 1) {

			// Base case (i.e. e == 0) from documenta Geigy Scientific
			// Tables, 6th edition, page 185
			double base = N * (1 - Math.pow(CF, 1 / N)); 
			if (e == 0) {
				return base; 
			}

			// Use linear interpolation between 0 and 1 like C4.5 does
			return base + e * (addErrs(N, 1, CF) - base);
		}

		// Use linear interpolation at the high end (i.e. between N - 0.5
		// and N) because of the continuity correction
		if (e + 0.5 >= N) {

			// Make sure that we never return anything smaller than zero
			return Math.max(N - e, 0);
		}

		// Get z-score corresponding to CF
		double z = Statistics.normalInverse(1 - CF);

		// Compute upper limit of confidence interval
		double  f = (e + 0.5) / N;
		double r = (f + (z * z) / (2 * N) +
				z * Math.sqrt((f / N) - 
						(f * f / N) + 
						(z * z / (4 * N * N)))) /
						(1 + (z * z) / N);

		return (r * N) - e;
	}




}
