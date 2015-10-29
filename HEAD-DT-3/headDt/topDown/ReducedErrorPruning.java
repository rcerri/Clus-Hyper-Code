package headDt.topDown;

import java.util.Random;
import weka.core.Instances;
import weka.core.Utils;

public class ReducedErrorPruning extends Tree {

	private static final long serialVersionUID = 1L;

	/** How many subsets of equal size? One used for pruning, the rest for training. */
	private int numSets;

	/** The random number seed. */
	private int m_seed = 1;

	public ReducedErrorPruning(AbstractSplitSelection toSelectLocModel, TreeParameters treeParam, int depth)
	throws Exception {
		super(toSelectLocModel,treeParam,depth);
		numSets = treeParam.getNumFoldPruning();
		m_cleanup = treeParam.cleanup();
		m_seed = treeParam.getSeed();
		m_collapseTheTree = treeParam.collapse();
	}

	public void buildTree(Instances data) 
	throws Exception {

		// remove instances with missing class
		data = new Instances(data);
		data.deleteWithMissingClass();

		Random random = new Random(m_seed);
		data.stratify(numSets);
		buildTree(data.trainCV(numSets, numSets - 1, random),
				data.testCV(numSets, numSets - 1), true);
		if(m_collapseTheTree)
			collapse();
		prune();
		if (m_cleanup) {
			cleanup(new Instances(data, 0));
		}
	}
	
	/**
	 * Returns a newly created tree.
	 *
	 * @param train the training data
	 * @param test the test data
	 * @return the generated tree
	 * @throws Exception if something goes wrong
	 */
	protected ReducedErrorPruning getNewTree(Instances train, Instances test) 
	throws Exception {

		ReducedErrorPruning newTree = new ReducedErrorPruning(m_toSelectModel, m_treeParam,m_depth+1);
		newTree.buildTree(train, test, true);
		return newTree;
	}


	public void prune() throws Exception {
		
		if (!m_isLeaf) {

			// Prune all subtrees.
			for (int i = 0; i < m_sons.length; i++)
				((ReducedErrorPruning)son(i)).prune();

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
				if (Utils.eq(localModel().distribution().perBag(i), 0)) {
					errors += m_test.perBag(i)-	m_test.perClassPerBag(i,localModel().distribution().maxClass());
				} else
					errors += ((ReducedErrorPruning)son(i)).errorsForTree();

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

		return m_test.total() - m_test.perClass(localModel().distribution().maxClass());
	}



}
