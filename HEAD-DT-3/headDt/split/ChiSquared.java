package headDt.split;

import headDt.topDown.Distribution;

public class ChiSquared extends Criterion {
	



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	/** The chi-squared statistic. Please refer to Mingers' "An Empirical Comparison of Selection Measures for Decision-Tree Induction",
	 * published at Machine Learning. */

	
	public ChiSquared(){
		this.maximized = true;
		this.binary = false;
	}
	
	
	
	@Override
	public double getValue(Distribution d, double total, int strategy) {
		double equation = 0, totalReal = 0;
		double termA = 0, termB = 0;
		
		if(strategy == 0)
			totalReal = d.total();
		else
			totalReal = total;
		
		for(int i=0;i<d.numBags();i++){
			for(int j=0; j<d.numClasses();j++){
				termA = Math.pow((d.perClassPerBag(i,j) - ((d.perBag(i) * d.perClass(j))/totalReal)),2);
				termB = (d.perBag(i) * d.perClass(j))/totalReal;
				if(termB != 0)
					equation = equation + (termA/termB);
			}
		}
		return equation;
	}

}
