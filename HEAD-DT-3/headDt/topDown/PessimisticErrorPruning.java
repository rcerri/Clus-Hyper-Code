package headDt.topDown;

import weka.core.Instances;

public class PessimisticErrorPruning extends Tree{

	private static final long serialVersionUID = 1L;
	
	private double m_numberOfSEs;

	public PessimisticErrorPruning(AbstractSplitSelection toSelectLocModel, TreeParameters treeParam, int depth)
	throws Exception {
		super(toSelectLocModel,treeParam,depth);
		m_cleanup = treeParam.cleanup();
		m_collapseTheTree = treeParam.collapse();
		m_numberOfSEs = treeParam.getNumberOfSEs();
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
	protected PessimisticErrorPruning getNewTree(Instances data) throws Exception {

		PessimisticErrorPruning newTree = 
			new PessimisticErrorPruning(m_toSelectModel, m_treeParam,m_depth+1);
		newTree.buildTree((Instances)data, true);

		return newTree;
	}


	public void prune() throws Exception {
		double errorsOfSubtree;
		double errorsOfTree;
		int i;

		if (!m_isLeaf){
			errorsOfSubtree = getCorrectedTrainingErrors();
			errorsOfTree = localModel().distribution().numIncorrect() + 0.5;
			if ((errorsOfSubtree + (m_numberOfSEs*SE(errorsOfSubtree))) >= errorsOfTree){

				// Free adjacent trees
				m_sons = null;
				m_isLeaf = true;

				// Get NoSplit Model for tree.
				m_localModel = new NoSplit(localModel().distribution());
			}else
				for (i=0;i<m_sons.length;i++)
					((PessimisticErrorPruning)son(i)).prune();
		}

	}

	public double getCorrectedTrainingErrors(){
		double errors = 0;
		int i;

		if (m_isLeaf)
			return localModel().distribution().numIncorrect() + 0.5;
		else{
			for (i=0;i<m_sons.length;i++)
				errors = errors+((PessimisticErrorPruning)son(i)).getCorrectedTrainingErrors();
			return errors;
		}
	}

	public double SE(double subtreeError){
		double standardError = 0;
		standardError = Math.sqrt((subtreeError * (localModel().distribution().total() - subtreeError))/localModel().distribution().total());
		return standardError;
	}

}
