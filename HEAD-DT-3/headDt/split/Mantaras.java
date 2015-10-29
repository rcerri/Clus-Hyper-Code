package headDt.split;

import headDt.topDown.Distribution;

public class Mantaras extends Criterion {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Refer to "A distance-based attribute selection measure for decision tree induction" by De M‡ntaras, published at Machine Learning. */

	public Mantaras(){
		this.maximized = true;
		this.binary = false;
	}
	
	
	@Override
	public double getValue(Distribution d, double total, int strategy) {
		double totalReal = 0;
		if(strategy == 0)
			totalReal = d.total();
		else
			totalReal = total;
		
		InfoGain info = new InfoGain();
		double numerator = info.getValue(d, total,strategy);
		double denominator = jointEntropy(d,totalReal);
		if(denominator == 0)
			return 0;
		else
			return(numerator/denominator);
		
	}

}
