package headDt.split;

import headDt.topDown.Distribution;

public class GMI extends Criterion {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The Global Mutual Information criterion. Please refer to "Towards automated medical decisions" by 
	 * Gleser and Collen, or alternatively "Hierarchical Classifier Design Using Mutual Information" by Sethi and Sarvarayudu. */

	
	public GMI(){
		this.maximized = true;
		this.binary = false;
	}
	
	@Override
	public double getValue(Distribution d, double total, int strategy) {
		
		double equation = 0, totalReal = 0;
		
		if(strategy == 0)
			totalReal = d.total();
		else
			totalReal = total;
		
		for(int i=0; i<d.numClasses();i++){
			for(int j=0;j<d.numBags();j++){
				equation = equation + d.perClassPerBag(j,i) * loge(d.perClassPerBag(j,i)*totalReal,d.perBag(j)*d.perClass(i));
			}
		}
		equation = equation * (1/totalReal);
		return equation;
	}
	
	public double loge(double numerator, double denominator){
		if((denominator <= 0) || numerator <= 0)
			return 0;
		else
			return Math.log(numerator/denominator);
		
		
	}
	
	

}
