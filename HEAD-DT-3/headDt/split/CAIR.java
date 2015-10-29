package headDt.split;

import headDt.topDown.Distribution;



public class CAIR extends Criterion {
	


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Refer to "Class-dependent discretization for inductive learning from continuous and mixed-mode data" by Ching et al. (1995). */

	
	public CAIR(){
		this.maximized = true;
		this.binary = false;
	}
	
	
	@Override
	public double getValue(Distribution d, double total, int strategy) {
		double numerator = 0, denominator = 0, totalReal = 0;
		GMI gmi = new GMI();
		numerator = gmi.getValue(d, total,strategy);
		if(strategy == 0)
			totalReal = d.total();
		else
			totalReal = total;
		
		denominator = jointEntropy(d,totalReal);

		if(denominator == 0)
			return 0;
		else 
			return (numerator/denominator);
	}

}
