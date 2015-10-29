package headDt.split;


import headDt.topDown.Distribution;
import weka.core.Utils;

public class InfoGain extends Criterion {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The Information Gain criterion. Please see Quinlan - C4.5 programs for machine learning or ID3 related papers. */

	double log2 = Math.log(2);

	public InfoGain(){
		this.maximized = true;
		this.binary = false;
		//System.out.println("REICH!!");
	}


	

	public double getValue(Distribution bags, double total, int strategy){

		double numerator;
		double noUnknown;
		double unknownRate;

		noUnknown = total-bags.total();
		unknownRate = noUnknown/total;
		numerator = (oldEnt(bags)-newEnt(bags));
		numerator = (1-unknownRate)*numerator;

		// Splits with no gain are useless.
		if (Utils.eq(numerator,0))
			return 0;

		return numerator/bags.total();
	}

	public boolean isMaximized() {
		return maximized; 
	}


	/**
	 * Computes entropy of distribution before splitting.
	 */
	public final double oldEnt(Distribution bags) {
		double returnValue = 0;
		int j;
		for (j=0;j<bags.numClasses();j++)
			returnValue = returnValue+logFunc(bags.perClass(j));
		return logFunc(bags.total())-returnValue; 
	}




	/**
	 * Computes entropy of distribution after splitting.
	 */
	public final double newEnt(Distribution bags) {

		double returnValue = 0;
		int i,j;
		for (i=0;i<bags.numBags();i++){
			for (j=0;j<bags.numClasses();j++)
				returnValue = returnValue+logFunc(bags.perClassPerBag(i,j));
			returnValue = returnValue-logFunc(bags.perBag(i));
		}
		return -returnValue;
	}



	/**
	 * Help method for computing entropy.
	 */
	public final double logFunc(double num) {

		// Constant hard coded for efficiency reasons
		if (num < 1e-6)
			return 0;
		else
			return num*Math.log(num)/log2;
	}



}
