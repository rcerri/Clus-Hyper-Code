package headDt.split;

import headDt.topDown.Distribution;

public class ChandraVarghese extends Criterion {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Criterion proposed by Chandra and Varghese (2009) in the paper "Moving towards efficient decision tree construction" */
	
	public ChandraVarghese(){
		this.maximized = false;
		this.binary = false;
	}
	
	@Override
	public double getValue(Distribution d, double total, int strategy) {
		double equation = 0;
		double termA = 0, termB = 0;
		double totalReal = 0;
		if(strategy == 0)
			totalReal = d.total();
		else
			totalReal = total;
		
		if(totalReal == 0)
			return Double.MAX_VALUE;
		
		for(int i=0; i<d.numBags();i++){
			termA = (d.perBag(i)/totalReal) * (d.actualNumClasses(i)/d.actualNumClasses());
			termB = 0;
			int j = d.numClasses() - 1;
			while(j>= 0){
				if(d.perClass(j) > 0){
					termB = termB + (d.perClassPerBag(i,j)/d.perClass(j));
				}
				j--;
			}
			equation = equation + (termA * termB);	
		}
		return equation;
	}

}
