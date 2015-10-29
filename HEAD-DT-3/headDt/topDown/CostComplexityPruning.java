package headDt.topDown;

import java.util.ArrayList;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class CostComplexityPruning extends Tree {

	private static final long serialVersionUID = 1L;

	/** Number of folds for minimal cost-complexity pruning. */
	protected int m_numFoldsPruning = 5;

	/** Alpha-value (for pruning) at the node. */
	protected double m_Alpha;

	/** The number of SEs to be used, in case UseOneSE is set to true */
	protected double m_numSEs;

	/** Training data size. */
	protected double m_SizePer = 1;

	private int m_seed = 1;
	


	public CostComplexityPruning(AbstractSplitSelection toSelectLocModel,TreeParameters tree, int depth)
	throws Exception {
		super(toSelectLocModel,tree,depth);
		m_numFoldsPruning = tree.getNumFoldPruning();
		m_cleanup = tree.cleanup();
		m_collapseTheTree = tree.collapse();
		m_numSEs = tree.getNumberOfSEs();
		m_seed = tree.getSeed();
	}

	public void buildTree(Instances data) throws Exception{
		// remove instances with missing class
		data = new Instances(data);
		data.deleteWithMissingClass();

		Random random = new Random(m_seed);
		Instances cvData = new Instances(data);
		cvData.randomize(random);
		cvData = new Instances(cvData, 0, (int) (cvData.numInstances() * m_SizePer) - 1);
		cvData.stratify(m_numFoldsPruning);

		double[][] alphas = new double[m_numFoldsPruning][];
		double[][] errors = new double[m_numFoldsPruning][];

		// calculate errors and alphas for each fold
		for (int i = 0; i < m_numFoldsPruning; i++) {

			//for every fold, grow tree on training set and fix error on test set.
			Instances train = cvData.trainCV(m_numFoldsPruning, i);
			Instances test = cvData.testCV(m_numFoldsPruning, i);

			buildTree(train,true);

			int numNodes = this.numInnerNodes();

			alphas[i] = new double[numNodes + 2];
			errors[i] = new double[numNodes + 2];

			// prune back and log alpha-values and errors on test set
			prune(alphas[i], errors[i], test);
		}

		//build tree using all the data
		buildTree(data, true);
		if(m_collapseTheTree)
			this.collapse();

		int numNodes = numInnerNodes();

		double[] treeAlphas = new double[numNodes + 2];

		// prune back and log alpha-values
		int iterations = prune(treeAlphas, null, null);

		double[] treeErrors = new double[numNodes + 2];

		// for each pruned subtree, find the cross-validated error
		for (int i = 0; i <= iterations; i++) {
			//compute midpoint alphas
			double alpha = Math.sqrt(treeAlphas[i] * treeAlphas[i + 1]);
			double error = 0;
			for (int k = 0; k < m_numFoldsPruning; k++) {
				int l = 0;
				while (alphas[k][l] <= alpha)
					l++;
				error += errors[k][l - 1];
			}
			treeErrors[i] = error / m_numFoldsPruning;
		}

		// find best alpha
		int best = -1;
		double bestError = Double.MAX_VALUE;
		for (int i = iterations; i >= 0; i--) {
			if (treeErrors[i] < bestError) {
				bestError = treeErrors[i];
				best = i;
			}
		}

		// Standard Error rule to choose expansion
		double SE = m_numSEs*(Math.sqrt(bestError * (1 - bestError)/ (data.numInstances())));
		for (int i = iterations; i >= 0; i--) {
			if (treeErrors[i] <= bestError + SE) {
				best = i;
				break;
			}
		}

		double bestAlpha = Math.sqrt(treeAlphas[best]* treeAlphas[best + 1]);

		//"unprune" final tree (faster than regrowing it)
		unprune();
		prune(bestAlpha);
	}


	protected CostComplexityPruning getNewTree(Instances train) 
	throws Exception {

		CostComplexityPruning newTree = new CostComplexityPruning(m_toSelectModel, m_treeParam,m_depth+1);
		newTree.buildTree(train, true);
		return newTree;
	}



	public int prune(double[] alphas, double[] errors, Instances test) throws Exception {
		ArrayList<CostComplexityPruning> nodeList;

		// determine training error of subtrees (both with and without replacing a subtree), 
		// and calculate alpha-values from them
		//	modelErrors(); //acho que nao preciso disso
		//	treeErrors();  //ditto
		this.calculateAlphas();

		// get list of all inner nodes in the tree
		nodeList = getInnerNodes();

		boolean prune = (nodeList.size() > 0);

		//alpha_0 is always zero (unpruned tree)
		alphas[0] = 0;

		Evaluation eval;

		// error of unpruned tree
		if (errors != null) {
			eval = new Evaluation(test);
			eval.evaluateModel((Classifier)this, test);
			errors[0] = eval.errorRate();
		}

		int iteration = 0;
		double preAlpha = Double.MAX_VALUE;
		while (prune) {
			iteration++;

			// get node with minimum alpha
			CostComplexityPruning nodeToPrune = this.nodeToPrune(nodeList);

			// do not set m_sons null, want to unprune
			nodeToPrune.m_isLeaf = true;

			// normally would not happen
			if (nodeToPrune.m_Alpha == preAlpha) {
				iteration--;
				calculateAlphas();
				nodeList = getInnerNodes();
				prune = (nodeList.size() > 0);
				continue;
			}

			// add to the alphas' list
			alphas[iteration] = nodeToPrune.m_Alpha;

			// log error
			if (errors != null) {
				eval = new Evaluation(test);
				eval.evaluateModel((Classifier)this , test);
				errors[iteration] = eval.errorRate();
			}
			preAlpha = nodeToPrune.m_Alpha;

			// update alphas
			calculateAlphas();

			// update inner nodes list
			nodeList = getInnerNodes();
			prune = (nodeList.size() > 0);
		}

		// set last alpha 1 to indicate end
		alphas[iteration + 1] = 1.0;
		return iteration;
	}


	public void prune(double alpha) throws Exception {

		ArrayList<CostComplexityPruning> nodeList;
		calculateAlphas();

		// get list of all inner nodes in the tree
		nodeList = getInnerNodes();

		boolean prune = (nodeList.size() > 0);
		double preAlpha = Double.MAX_VALUE;
		while (prune) {

			// select node with minimum alpha
			CostComplexityPruning nodeToPrune = nodeToPrune(nodeList);

			// want to prune if its alpha is smaller than alpha
			if (nodeToPrune.m_Alpha > alpha) {
				break;
			}

			nodeToPrune.m_isLeaf = true;

			// normally would not happen
			if (nodeToPrune.m_Alpha == preAlpha) {
				nodeToPrune.m_isLeaf = true;
				calculateAlphas();
				nodeList = getInnerNodes();
				prune = (nodeList.size() > 0);
				continue;
			}

			preAlpha = nodeToPrune.m_Alpha;

			//update alphas
			calculateAlphas();

			nodeList = getInnerNodes();
			prune = (nodeList.size() > 0);
		}
	}



	/**
	 * Return a list of all inner nodes in the tree.
	 * 
	 * @return 		the list of all inner nodes
	 */
	protected ArrayList<CostComplexityPruning> getInnerNodes() {
		ArrayList<CostComplexityPruning> nodeList = new ArrayList<CostComplexityPruning>();
		fillInnerNodes(nodeList);
		return nodeList;
	}

	/**
	 * Fills a list with all inner nodes in the tree.
	 * 
	 * @param nodeList 	the list to be filled
	 */
	protected void fillInnerNodes(ArrayList<CostComplexityPruning> nodeList) {
		if (!m_isLeaf) {
			nodeList.add(this);
			for (int i = 0; i < m_sons.length; i++)
				((CostComplexityPruning)son(i)).fillInnerNodes(nodeList);
		}
	}


	/**
	 * Updates the alpha field for all nodes.
	 * 
	 * @throws Exception 	if something goes wrong
	 */
	public void calculateAlphas() throws Exception {

		if (!m_isLeaf) {
			double errorDiff = localModel().distribution().numIncorrect() - this.getTrainingErrors();
			if (errorDiff <= 0) {
				//split increases training error (should not normally happen). Prune it instantly.
				m_sons = null;
				m_isLeaf = true;
				m_Alpha = Double.MAX_VALUE;
			} else {
				//compute alpha
				errorDiff /= localModel().distribution().total();
				m_Alpha = errorDiff / (double) (this.numLeaves() - 1);
				long alphaLong = Math.round(m_Alpha * Math.pow(10, 10));
				m_Alpha = (double) alphaLong / Math.pow(10, 10);
				for (int i = 0; i < m_sons.length; i++) {
					((CostComplexityPruning)son(i)).calculateAlphas();
				}
			}
		} else {
			//alpha = infinite for leaves (do not want to prune)
			m_Alpha = Double.MAX_VALUE;
		}
	}

	/**
	 * Find the node with minimal alpha value. If two nodes have the same alpha, 
	 * choose the one with more leave nodes.
	 * 
	 * @param nodeList 	list of inner nodes
	 * @return 		the node to be pruned
	 */
	protected CostComplexityPruning nodeToPrune(ArrayList<CostComplexityPruning> nodeList) {
		if (nodeList.size() == 0)
			return null;
		if (nodeList.size() == 1)
			return (CostComplexityPruning) nodeList.get(0);
		CostComplexityPruning returnNode = (CostComplexityPruning) nodeList.get(0);
		double baseAlpha = returnNode.m_Alpha;
		for (int i = 1; i < nodeList.size(); i++) {
			CostComplexityPruning node = (CostComplexityPruning) nodeList.get(i);
			if (node.m_Alpha < baseAlpha) {
				baseAlpha = node.m_Alpha;
				returnNode = node;
			} else if (node.m_Alpha == baseAlpha) { // break tie
				if (node.numLeaves() > returnNode.numLeaves()) {
					returnNode = node;
				}
			}
		}
		return returnNode;
	}

	/**
	 * Method to "unprune" the CART tree. Sets all leaf-fields to false.
	 * Faster than re-growing the tree because CART do not have to be fit again.
	 */
	protected void unprune() {
		if (m_sons != null) {
			m_isLeaf = false;
			for (int i = 0; i < m_sons.length; i++)
				((CostComplexityPruning)m_sons[i]).unprune();
		}
	}


}
