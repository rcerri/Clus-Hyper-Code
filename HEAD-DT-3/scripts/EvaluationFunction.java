package scripts;

import java.math.BigDecimal;
import weka.core.Utils;

public class EvaluationFunction {

	public EvaluationFunction() {

	}

	public static double arredonda(double x, int casas) {
		BigDecimal bd = new BigDecimal(x);
		bd = bd.setScale(casas,BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}


	/**
	 * Compute and return gini gain for given distributions of a node and its 
	 * successor nodes.
	 * 
	 * @param parentDist 	class distributions of parent node
	 * @param childDist 	class distributions of successor nodes
	 * @return 		Gini gain computed
	 */
	public static double computeGiniGain(int[] parentDist, int[][] childDist) {

		double totalWeight = Utils.sum(parentDist);
		// I dont know if this is correct, because the totalWeight can be different from the parent depending on the missing values handling.
		if (totalWeight==0)
			return 0;
		double leftWeight = Utils.sum(childDist[0]);
		double rightWeight = Utils.sum(childDist[1]);
		double parentGini = computeGini(parentDist, totalWeight);
		double leftGini = computeGini(childDist[0],leftWeight);
		double rightGini = computeGini(childDist[1], rightWeight);

		return parentGini - leftWeight/totalWeight*leftGini - rightWeight/totalWeight*rightGini;
	}



	public static double computeGini(int[] dist, double total) {
		if (total == 0)
			return 0;
		double val = 0;
		for (int i = 0; i < dist.length; i++) {
			val += ((double)dist[i]/total)*((double)dist[i]/total);
		}
		return 1 - val;
	}

	/**
	 * Compute and return information gain for given distributions of a node 
	 * and its successor nodes.
	 * 
	 * @param parentDist 	class distributions of parent node
	 * @param childDist 	class distributions of successor nodes
	 * @return 		information gain computed
	 */
	public static double computeInfoGain(int[] parentDist, int[][] childDist) {
		double totalWeight = Utils.sum(parentDist);
		if (totalWeight == 0)
			return 0;
		double leftWeight = Utils.sum(childDist[0]);
		double rightWeight = Utils.sum(childDist[1]);
		double parentInfo = computeEntropy(parentDist, totalWeight);
		double leftInfo = computeEntropy(childDist[0],leftWeight);
		double rightInfo = computeEntropy(childDist[1], rightWeight);
		return parentInfo - (leftWeight/totalWeight)*leftInfo - (rightWeight/totalWeight)*rightInfo;
	}

	/**
	 * Compute and return entropy for a given distribution of a node.
	 * 
	 * @param dist 	class distributions
	 * @param total 	class distributions
	 * @return 		entropy of the class distributions
	 */

	public static double computeEntropy(int[] dist, double total) {
		if (total == 0) return 0;
		double entropy = 0;
		for (int i = 0; i < dist.length; i++) {
			if (dist[i] != 0)
				entropy -= ((double)dist[i]/total) * Utils.log2((double)dist[i]/total);
		}
		return entropy;
	}
}
