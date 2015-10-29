package headDt.topDown;

import java.io.Serializable;
import weka.classifiers.Classifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;



public class EvolvedAlgorithm implements Classifier, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5201434555343483408L;
	private Tree root;
	private SplitParameters splitParam;
	private TreeParameters treeParam;
	private AbstractSplitSelection model;
	
	
	public EvolvedAlgorithm(SplitParameters split, TreeParameters tree) throws Exception{
		this.treeParam = tree;
		this.splitParam = split;
	}

	@Override
	public void buildClassifier(Instances data) throws Exception {
		this.model = new UnivariateSingleSplitSelection(splitParam,data, data.numInstances());
		
		switch(treeParam.getpruningType()){
		case 0: root = new Tree(model,treeParam,0);
		root.buildTree(data);
		break;
		
		case 1: root =  new ErrorBasedPruning(model,treeParam,0);
		((ErrorBasedPruning)root).buildTree(data);
		break;
		
		case 2: root =  new MinimumErrorPruning(model, treeParam,0); 
		((MinimumErrorPruning)root).buildTree(data);
		break;
		
		case 3: root = new PessimisticErrorPruning(model,treeParam,0);
		((PessimisticErrorPruning)root).buildTree(data);
		break;
		
		case 4: root = new CostComplexityPruning(model,treeParam,0);
		((CostComplexityPruning)root).buildTree(data);
		break;
		
		case 5: root = new ReducedErrorPruning(model,treeParam,0);
		((ReducedErrorPruning)root).buildTree(data);
		break;
		
		default: root = null;
		}
		
		
		model.cleanup();
		
	}

	@Override
	public double classifyInstance(Instance instance) throws Exception {
		return root.classifyInstance(instance);
	}

	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		return root.distributionForInstance(instance);
	}

	@Override
	public Capabilities getCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Tree getRoot() {
		return root;
	}

	public void setRoot(Tree root) {
		this.root = root;
	}
	
	public SplitParameters getSplitParam() {
		return splitParam;
	}

	public void setSplitParam(SplitParameters splitParam) {
		this.splitParam = splitParam;
	}

	public TreeParameters getTreeParam() {
		return treeParam;
	}

	public void setTreeParam(TreeParameters treeParam) {
		this.treeParam = treeParam;
	}

}
