package headDt.split;

import headDt.topDown.Distribution;
import weka.core.Utils;

public class DCSM extends Criterion {
	


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Criterion proposed by Chandra et al. in the paper "A new node splitting measure for decision tree construction",
	 * published at Pattern Recognition. */

	public DCSM(){
		this.maximized = false;
		this.binary = false;
	}
	
	@Override
	public double getValue(Distribution d, double total, int strategy) {
		double equation = 0, totalReal = 0;
		double termA = 0;
		double termB = 0;
		double exp = 0;
		
		if(strategy == 0)
			totalReal = d.total();
		else
			totalReal = total;
		
		if(Utils.eq(totalReal,0))
			return Double.MAX_VALUE;
		
		for(int i=0;i<d.numBags();i++){
			termA = (d.perBag(i)/totalReal) * (d.actualNumClasses(i)) * Math.exp(d.actualNumClasses(i));
			termB = 0;
			exp = 0;
			for(int j=0;j<d.numClasses();j++){
				exp = (d.actualNumClasses(i)/d.actualNumClasses()) * (1 - Math.pow(d.prob(j,i), 2));
				termB = termB + (d.prob(j,i) * Math.exp(exp));
			}
			equation = equation + (termA * termB);
		}
		return equation;
	}

}
