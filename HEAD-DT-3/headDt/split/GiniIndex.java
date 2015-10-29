package headDt.split;

import headDt.topDown.Distribution;
import weka.core.Utils;

public class GiniIndex extends Criterion {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** The Gini Index criterion. Please refer to the book of Breiman et al. "CART - Classification and Regression Trees" (1984). */


	public GiniIndex(){
		this.maximized = false;
		this.binary = false;
	}
	
	@Override
	public double getValue(Distribution bags, double total, int strategy) {
	    double totalReal = 0;
	    if(strategy == 0)
	    	totalReal = bags.total();
	    else
	    	totalReal = total;
	    
		double equation = gini(bags,totalReal);
	    
	    // Splits with no gain are useless.
	    if (Utils.eq(equation,0))
	      return 0;
		return equation;  
	}
	
	
	public double gini(Distribution bags, double total){
		double sum = 0;
		double returnValue = 0;
		for(int i=0;i<bags.numBags();i++){
			sum = 0;
			for(int j=0;j<bags.numClasses();j++){
				sum = sum + ((bags.perClassPerBag(i,j)/bags.perBag(i))*(bags.perClassPerBag(i,j)/bags.perBag(i)));
			}
			sum = 1-sum;
			returnValue = returnValue + ((bags.perBag(i)/total) * sum);
		}
		
		return returnValue;
		
	}

}
