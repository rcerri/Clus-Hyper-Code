package headDt.split;

import headDt.topDown.Distribution;

public class MPI extends Criterion {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Split criterion proposed by Taylor and Silverman in "Block diagrams and splitting criteria for classification trees" */

	public MPI(){
		this.maximized = true;
		this.binary = true;
	}
	
	
	@Override
	public double getValue(Distribution d, double total, int strategy) {
		double equation = 0, termA = 0, termB = 0, totalReal = 0;
		
		if(strategy == 0)
			totalReal = d.total();
		else
			totalReal = total;
		
		termA = (d.perBag(0)/totalReal) * (d.perBag(1)/totalReal);
		for(int j=0;j<d.numClasses();j++){
			termB = termB + ((d.perClass(j)/totalReal)* (d.perClassPerBag(0,j)/totalReal) * (d.perClassPerBag(1,j)/totalReal));
		}
		equation = termA - termB;
		return equation;
	}

}
