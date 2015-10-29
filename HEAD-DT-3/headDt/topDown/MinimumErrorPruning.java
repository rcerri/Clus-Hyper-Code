package headDt.topDown;

import weka.core.Instances;
import weka.core.Utils;

public class MinimumErrorPruning extends Tree {

	private static final long serialVersionUID = 1L;
	
	/** Ad-hoc parameter m. The higher the value of m, the more severe the pruning. Try m = number of classes. */
	float m;


	public MinimumErrorPruning(AbstractSplitSelection toSelectLocModel,TreeParameters treeParam, int depth)
	throws Exception {
		super(toSelectLocModel,treeParam,depth);
		m = treeParam.getM();
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
	protected MinimumErrorPruning getNewTree(Instances data) throws Exception {

		MinimumErrorPruning newTree = 
			new MinimumErrorPruning(m_toSelectModel, m_treeParam,m_depth+1);
		newTree.buildTree((Instances)data, true);
		return newTree;
	}
	
	
	public void prune() throws Exception {
		if (!m_isLeaf) {

			// Prune all subtrees.
			for (int i = 0; i < m_sons.length; i++)
				((MinimumErrorPruning)son(i)).prune();

			// Decide if leaf is best choice.
			if (Utils.smOrEq(errorsForLeaf(),errorsForTree())) {

				// Free son Trees
				m_sons = null;
				m_isLeaf = true;

				// Get NoSplit Model for node.
				m_localModel = new NoSplit(localModel().distribution());
			}
		}
	}
	
	
	/**
	 * Computes estimated errors for tree.
	 *
	 * @return the estimated errors
	 * @throws Exception if error estimate can't be computed
	 */
	private double errorsForTree() throws Exception {

		double errors = 0;

		if (m_isLeaf)
			return errorsForLeaf();
		else{
			for (int i = 0; i < m_sons.length; i++)
					errors += (localModel().distribution().perBag(i)/localModel().distribution().total())* ((MinimumErrorPruning)son(i)).errorsForTree();
			return errors;
		}
	}

	/**
	 * Computes estimated errors for leaf.
	 *
	 * @return the estimated errors
	 * @throws Exception if error estimate can't be computed
	 */
	private double errorsForLeaf() throws Exception {
		int k = localModel().distribution().numClasses();
		double total = localModel().distribution().total();
		double expectedError[] = new double[k];
		for(int l=0;l<k;l++){
			double nl = localModel().distribution().perClass(l);
			expectedError[l] = (total - nl  + (1 - (nl/total)) * m)/ (total + m);
		}
		int minIndex = Utils.minIndex(expectedError);
		return expectedError[minIndex];
	}

}
