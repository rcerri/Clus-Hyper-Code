package headDt.split;

import headDt.topDown.Distribution;

public class G extends Criterion {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public G(){
		this.maximized = true;
		this.binary = false;
	}
	
	
	@Override
	public double getValue(Distribution d, double total, int strategy) {
		double equation = 0;
		InfoGain info = new InfoGain();
		equation = 2 * total * info.getValue(d, total,strategy) * Math.log(2);
		return equation;
	}

}
