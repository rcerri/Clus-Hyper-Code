package headDt.split;

import headDt.topDown.Distribution;

public class Twoing extends Criterion {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Criterion proposed by Breiman et al. in their famous book "CART - Classification and Regression Trees */
	
	public Twoing(){
		this.maximized = true;
		this.binary = true;
	}
	
	
	@Override
	public double getValue(Distribution d, double total, int strategy) {
		double equation = 0, totalReal = 0;
		double termA = 0, termB = 0;
		
		if(strategy == 0)
			totalReal = d.total();
		else
			totalReal = total;
		
		termA = 0.25 * (d.perBag(0)/totalReal) * (d.perBag(1)/totalReal);
		for(int j = 0;j<d.numClasses();j++){
			termB = termB + Math.abs((d.perClassPerBag(0,j)/d.perBag(0)) - (d.perClassPerBag(1,j)/d.perBag(1)));
		}
		termB = Math.pow(termB,2);
		equation = termA * termB;
		return equation;
	}

}
