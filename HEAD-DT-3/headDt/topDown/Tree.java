package headDt.topDown;

import java.io.Serializable;

import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.classifiers.Classifier;

public class Tree implements Classifier,Serializable{

	private static final long serialVersionUID = 1L;

	/** The model selection method. */  
	protected AbstractSplitSelection m_toSelectModel;     

	/** Local model at node. */
	protected AbstractSplit m_localModel; 
	
	/** Tree parameters */
	protected TreeParameters m_treeParam;

	/** References to sons. */
	protected Tree [] m_sons;   
	
	/** Depth of the current tree */
	protected int m_depth;

	/** True if node is leaf. */
	protected boolean m_isLeaf;                   

	/** True if node is empty. */
	protected boolean m_isEmpty;                  

	/** The training instances. */
	protected Instances m_train;                  

	/** The pruning instances. */
	protected Distribution m_test;     

	/** The id for the node. */
	protected int m_id;

	protected boolean m_cleanup;

	protected boolean m_collapseTheTree;

	/** Constructor */
	public Tree(AbstractSplitSelection toSelectLocModel, TreeParameters treeParam, int depth) {
		m_toSelectModel = toSelectLocModel;
		m_treeParam = treeParam;
		m_depth = depth;
	}

	public void buildTree(Instances data) throws Exception {
		// remove instances with missing class
		data = new Instances(data);
		data.deleteWithMissingClass();
		buildTree(data, true);
	}

	/**
	 * Builds the tree structure.
	 *
	 * @param data the data for which the tree structure is to be
	 * generated.
	 * @param keepData is training data to be kept?
	 * @throws Exception if something goes wrong
	 */
	public void buildTree(Instances data, boolean keepData) throws Exception {
		Instances [] localInstances;
		if (keepData) {
			m_train = data;
		}
		m_test = null;
		m_isLeaf = false;
		m_isEmpty = false;
		m_sons = null;
		m_localModel = m_toSelectModel.selectModel(data,m_depth);
		if (m_localModel.numSubsets() > 1) {
			localInstances = m_localModel.split(data);
			data = null;
			m_sons = new Tree [m_localModel.numSubsets()];
			for (int i = 0; i < m_sons.length; i++) {
				m_sons[i] = getNewTree(localInstances[i]);
				localInstances[i] = null;
			}
		}else{
			m_isLeaf = true;
			if (Utils.eq(data.sumOfWeights(), 0))
				m_isEmpty = true;
			data = null;
		}
	}


	protected Tree getNewTree(Instances data) throws Exception {
		Tree newTree = new Tree(m_toSelectModel,m_treeParam,m_depth+1);
		newTree.buildTree(data, true);
		return newTree;
	}


	/**
	 * Builds the tree structure with hold out set
	 *
	 * @param train the data for which the tree structure is to be
	 * generated.
	 * @param test the test data for potential pruning
	 * @param keepData is training Data to be kept?
	 * @throws Exception if something goes wrong
	 */
	public void buildTree(Instances train, Instances test, boolean keepData)
	throws Exception {

		Instances [] localTrain, localTest;
		int i;

		if (keepData) {
			m_train = train;
		}
		m_isLeaf = false;
		m_isEmpty = false;
		m_sons = null;
		m_localModel = m_toSelectModel.selectModel(train, test, m_depth);
		m_test = new Distribution(test, m_localModel);
		if (m_localModel.numSubsets() > 1) {
			localTrain = m_localModel.split(train);
			localTest = m_localModel.split(test);
			train = test = null;
			m_sons = new Tree [m_localModel.numSubsets()];
			for (i=0;i<m_sons.length;i++) {
				m_sons[i] = getNewTree(localTrain[i], localTest[i]);
				localTrain[i] = null;
				localTest[i] = null;
			}
		}else{
			m_isLeaf = true;
			if (Utils.eq(train.sumOfWeights(), 0))
				m_isEmpty = true;
			train = test = null;
		}
	}

	protected Tree getNewTree(Instances train, Instances test) 
	throws Exception {

		Tree newTree = new Tree(m_toSelectModel,m_treeParam,m_depth+1);
		newTree.buildTree(train, test, true);

		return newTree;
	}


	/**
	 * Returns number of leaves in tree structure.
	 * 
	 * @return the number of leaves
	 */
	public int numLeaves() {

		int num = 0;
		int i;

		if (m_isLeaf)
			return 1;
		else
			for (i=0;i<m_sons.length;i++)
				num = num+m_sons[i].numLeaves();

		return num;
	}

	/**
	 * Returns number of nodes in tree structure.
	 * 
	 * @return the number of nodes
	 */
	public int numNodes() {

		int no = 1;
		int i;

		if (!m_isLeaf)
			for (i=0;i<m_sons.length;i++)
				no = no+m_sons[i].numNodes();

		return no;
	}

	/**
	 * Method to count the number of inner nodes in the tree.
	 * 
	 * @return 		the number of inner nodes
	 */
	public int numInnerNodes() {
		if (m_isLeaf)
			return 0;
		int numNodes = 1;
		for (int i = 0; i < m_sons.length; i++)
			numNodes += m_sons[i].numInnerNodes();
		return numNodes;
	}

	/**
	 * Prints tree structure.
	 * 
	 * @return the tree structure
	 */
	public String toString() {

		try {
			StringBuffer text = new StringBuffer();
			if (m_isLeaf) {
				text.append(": ");
				text.append(m_localModel.dumpLabel(0,m_train));
			}else
				dumpTree(0,text);
			text.append("\n\nNumber of Leaves  : \t"+numLeaves()+"\n");
			text.append("\nSize of the tree : \t"+numNodes()+"\n");

			return text.toString();
		} catch (Exception e) {
				e.printStackTrace();
			return "Can't print classification tree.";
		}
	}

	/**
	 * Help method for printing tree structure.
	 *
	 * @param depth the current depth
	 * @param text for outputting the structure
	 * @throws Exception if something goes wrong
	 */
	private void dumpTree(int depth, StringBuffer text) 
	throws Exception {

		int i,j;

		for (i=0;i<m_sons.length;i++) {
			text.append("\n");;
			for (j=0;j<depth;j++)
				text.append("|   ");
			text.append(m_localModel.leftSide(m_train));
			text.append(m_localModel.rightSide(i, m_train));
			if (m_sons[i].m_isLeaf) {
				text.append(": ");
				text.append(m_localModel.dumpLabel(i,m_train));
			}else
				m_sons[i].dumpTree(depth+1,text);
		}
	}

	public final void cleanup(Instances justHeaderInfo) {

		m_train = justHeaderInfo;
		m_test = null;
		if (!m_isLeaf)
			for (int i = 0; i < m_sons.length; i++)
				m_sons[i].cleanup(justHeaderInfo);
	}

	/**
	 * Method just exists to make program easier to read.
	 */
	protected AbstractSplit localModel() {

		return (AbstractSplit)m_localModel;
	}

	/**
	 * Method just exists to make program easier to read.
	 */
	protected Tree son(int index) {

		return (Tree)m_sons[index];
	}


	protected double getTrainingErrors(){

		double errors = 0;
		int i;

		if (m_isLeaf)
			return localModel().distribution().numIncorrect();
		else{
			for (i=0;i<m_sons.length;i++)
				errors = errors+son(i).getTrainingErrors();
			return errors;
		}
	}

	/**
	 * Collapses a tree to a node if training error doesn't increase.
	 */
	protected final void collapse(){

		double errorsOfSubtree;
		double errorsOfTree;
		int i;

		if (!m_isLeaf){
			errorsOfSubtree = getTrainingErrors();
			errorsOfTree = localModel().distribution().numIncorrect();
			if (errorsOfSubtree >= errorsOfTree-1E-3){

				// Free adjacent trees
				m_sons = null;
				m_isLeaf = true;

				// Get NoSplit Model for tree.
				m_localModel = new NoSplit(localModel().distribution());
			}else
				for (i=0;i<m_sons.length;i++)
					son(i).collapse();
		}
	}


	/**
	 * Computes new distributions of instances for nodes
	 * in tree.
	 *
	 * @param data the data to compute the distributions for
	 * @throws Exception if something goes wrong
	 */
	protected void newDistribution(Instances data) throws Exception {

		Instances [] localInstances;

		localModel().resetDistribution(data);
		m_train = data;
		if (!m_isLeaf){
			localInstances = (Instances [])localModel().split(data);
			for (int i = 0; i < m_sons.length; i++)
				son(i).newDistribution(localInstances[i]);
		} else {
			// Check whether there are some instances at the leaf now!
			if (!Utils.eq(data.sumOfWeights(), 0)) {
				m_isEmpty = false;
			}
		}
	}


	@Override
	public double classifyInstance(Instance instance) throws Exception {
		double maxProb = -1;
		double currentProb;
		int maxIndex = 0;
		int j;

		for (j = 0; j < instance.numClasses(); j++) {
			currentProb = getProbs(j, instance, 1);
			if (Utils.gr(currentProb,maxProb)) {
				maxIndex = j;
				maxProb = currentProb;
			}
		}

		return (double)maxIndex;
	}

	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		return this.distributionForInstance(instance, false);
	}

	/** 
	 * Returns class probabilities for a weighted instance.
	 *
	 * @param instance the instance to get the distribution for
	 * @param useLaplace whether to use laplace or not
	 * @return the distribution
	 * @throws Exception if something goes wrong
	 */
	public final double [] distributionForInstance(Instance instance,
			boolean useLaplace) 
	throws Exception {

		double [] doubles = new double[instance.numClasses()];

		for (int i = 0; i < doubles.length; i++) {
			if (!useLaplace) {
				doubles[i] = getProbs(i, instance, 1);
			} else {
				doubles[i] = getProbsLaplace(i, instance, 1);
			}
		}

		return doubles;
	}

	/**
	 * Help method for computing class probabilities of 
	 * a given instance.
	 *
	 * @param classIndex the class index
	 * @param instance the instance to compute the probabilities for
	 * @param weight the weight to use
	 * @return the laplace probs
	 * @throws Exception if something goes wrong
	 */
	private double getProbsLaplace(int classIndex, Instance instance, double weight) 
	throws Exception {

		if (m_isLeaf) {
			return weight * localModel().classProbLaplace(classIndex, instance, -1);
		} else {
			int treeIndex = localModel().whichSubset(instance);
			if (treeIndex == -1) {
				return missingValuesInClassificationLaplace(instance,classIndex,weight);	
			} else {
				if (son(treeIndex).m_isEmpty) {
					return weight * localModel().classProbLaplace(classIndex, instance, treeIndex);
				} else {
					return son(treeIndex).getProbsLaplace(classIndex, instance, weight);
				}
			}
		}
	}

	/**
	 * Help method for computing class probabilities of 
	 * a given instance.
	 * 
	 * @param classIndex the class index
	 * @param instance the instance to compute the probabilities for
	 * @param weight the weight to use
	 * @return the probs
	 * @throws Exception if something goes wrong
	 */
	private double getProbs(int classIndex, Instance instance, double weight) throws Exception { 

		if (m_isLeaf) {
			return weight * localModel().classProb(classIndex, instance, -1);
		} else {
			int treeIndex = localModel().whichSubset(instance);
			if (treeIndex == -1) {
				return missingValuesInClassification(instance,classIndex,weight);
			} else {
				if (son(treeIndex).m_isEmpty) {
					return weight * localModel().classProb(classIndex, instance, treeIndex);
				} else {
					return son(treeIndex).getProbs(classIndex, instance, weight);
				}
			}
		}
	}
	
	public double missingValuesInClassificationLaplace(Instance instance, int classIndex, double weight) throws Exception{
		
		
		switch(m_treeParam.getMissingValueClassification()){
		
		// Explore all branches
		case 0: double[] weights = localModel().weights(instance);
				double prob = 0;
				for (int i = 0; i < m_sons.length; i++) {
					if (!son(i).m_isEmpty) {
						prob += son(i).getProbsLaplace(classIndex, instance, weights[i] * weight);
					}
				}
				return prob;
		
		// Halt the classification in the current node		
		case 1: return weight * localModel().distribution().laplaceProb(classIndex);
		
		// Take the path to the max bag
		case 2: int treeIndex = localModel().distribution().maxBag();
				if (son(treeIndex).m_isEmpty) {
					return weight * localModel().classProbLaplace(classIndex, instance, treeIndex);
				} else {
					return son(treeIndex).getProbsLaplace(classIndex, instance, weight);
				}		
		default: return -1;
		}
	}
	
	public double missingValuesInClassification(Instance instance, int classIndex, double weight) throws Exception{
		switch(m_treeParam.getMissingValueClassification()){
		// Explore all branches
		case 0: double[] weights = localModel().weights(instance);
				double prob = 0;
				for (int i = 0; i < m_sons.length; i++) {
					if (!son(i).m_isEmpty) {
						prob += son(i).getProbs(classIndex, instance, weights[i] * weight);
					}
				}
				return prob;
		
		// Halt the classification in the current node		
		case 1: return weight * localModel().distribution().prob(classIndex);
		
		// Take the path to the max bag
		case 2: int treeIndex = localModel().distribution().maxBag();
				if (son(treeIndex).m_isEmpty) {
					return weight * localModel().classProb(classIndex, instance, treeIndex);
				} else {
					return son(treeIndex).getProbs(classIndex, instance, weight);
				}		
		default: return -1;
		}
	}

	@Override
	public Capabilities getCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void buildClassifier(Instances data) throws Exception {
	    // remove instances with missing class
	    data = new Instances(data);
	    data.deleteWithMissingClass();
		buildTree(data,true);

	}

}
