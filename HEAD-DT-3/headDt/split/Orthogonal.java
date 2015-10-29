package headDt.split;

import headDt.topDown.Distribution;

public class Orthogonal extends Criterion {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public Orthogonal(){
		this.maximized = true;
		this.binary = true;
	}
	
	
	@Override
	public double getValue(Distribution d, double total, int strategy) {
		double equation = 0, numerator = 0, denominator = 0, norm1 = 0, norm2 = 0;
		
		for(int j=0;j<d.numClasses();j++){
			numerator = numerator + (d.perClassPerBag(0,j)*d.perClassPerBag(1,j));
			norm1 = norm1 + Math.pow(d.perClassPerBag(0,j),2);
			norm2 = norm2 + Math.pow(d.perClassPerBag(1,j),2);
		}
		denominator = norm1*norm2;
		equation = numerator/denominator;
		return (1 - equation);
	}

}
