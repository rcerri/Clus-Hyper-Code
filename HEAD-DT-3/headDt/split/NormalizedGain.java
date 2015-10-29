package headDt.split;

import headDt.topDown.Distribution;

public class NormalizedGain extends Criterion {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Criterion proposed by Jun et al. in "A New Criterion in Selection and Discretization of Attributes
	 * for the Generation of Decision Trees", published at IEEE Transactions on Pattern Analysis and Machine Intelligence */

	public NormalizedGain(){
		this.maximized = true;
		this.binary = false;
	}
	
	
	@Override
	public double getValue(Distribution d, double total, int strategy) {
		InfoGain info = new InfoGain();
		double equation = info.getValue(d,total,strategy);
		equation = equation/log2(d.numBags());
		return equation;
	}

}
