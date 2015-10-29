package headDt.split;

import headDt.topDown.Distribution;
import weka.core.Utils;

public class GainRatio extends Criterion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double gainRatio = 0;

	public GainRatio(){
		this.maximized = true;
		this.binary = false;
	}
	
	
	@Override
	public double getValue(Distribution d, double total, int strategy) {
		double numerator;
		double totalReal = 0;
		
	    if(strategy == 0)
	    	totalReal = d.total();
	    else
	    	totalReal = total;

		InfoGain info = new InfoGain();
		numerator = info.getValue(d,total,strategy);

		// Splits with no gain are useless.
		if (Utils.smOrEq(numerator,0))
			return 0;
		
		
		return numerator;
	}


	public double getValue(double infoGain,Distribution bags, double total){
		double denumerator;
		denumerator = selfEntropy(bags,total);
		
		// Test if split is trivial.
		if (Utils.smOrEq(denumerator,0))
			return 0;
		else return infoGain/denumerator;
		
	}
	
	
	
	public void setGainRatio(double gainRatio) {
		this.gainRatio = gainRatio;
	}


	public double getGainRatio() {
		return gainRatio;
	}

}
