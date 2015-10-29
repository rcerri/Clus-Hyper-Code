package headDt.split;

import headDt.topDown.Distribution;

public class Hypergeometric extends Criterion {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** This criterion is detailed in the paper "An exact probability metric for decision tree splitting and stopping", by J. Martin, published at Machine Learning. */
	/** Firstly, we take the logarithm (base 2) of the entire criterion in order to get rid of the factorial terms and multiplicands. In the end, we return to the original
	 * value by setting 2 ^ result. */

	public Hypergeometric(){
		this.maximized = false;
		this.binary = false;
	}
	
	
	@Override
	public double getValue(Distribution d, double total, int strategy) {
		double termA = 0;
		double termB = 0;
		double termB1 = 0;
		double termB2 = 0;
		double totalReal = 0;
		
		if(strategy == 0)
			totalReal = d.total();
		else
			totalReal = total;
		
		for(int i=0;i<d.numClasses();i++){
			termA = termA + sumlog2(d.perClass(i));
		}
		termA = termA - sumlog2(totalReal);
		
		for(int i=0; i<d.numBags();i++){
			termB1 = sumlog2(d.perBag(i));
			termB2 = 0;
			for(int j=0;j<d.numClasses();j++){
				termB2 = termB2 + sumlog2(d.perClassPerBag(i,j));
			}
			termB = termB + (termB1 - termB2);
		}
	//	HypergeometricFatorial teste = new HypergeometricFatorial();
//		System.out.println("Normal = "+Math.exp(termA + termB));
	//	System.out.println("Fatorial = "+teste.getValue(d, sum_weights));
		return Math.exp(termA + termB);
	}
	
	public double sumlog2(double total){
		double result = 0;
		for(int i=1; i<=total;i++){
			result = result + Math.log(i);
		}
		return result;
	}
	
	

}
