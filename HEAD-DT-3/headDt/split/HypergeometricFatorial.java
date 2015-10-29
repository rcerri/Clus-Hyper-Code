package headDt.split;

import java.math.BigInteger;

import headDt.topDown.Distribution;

public class HypergeometricFatorial extends Criterion {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public HypergeometricFatorial(){
		this.maximized = false;
		this.binary = false;
	}
	
	@Override
	public double getValue(Distribution d, double total, int strategy) {
		BigInteger termA = BigInteger.ONE, termB = BigInteger.ZERO, termC=BigInteger.ONE, termD = BigInteger.ONE;
		double totalReal = 0;
		
		if(strategy == 0)
			totalReal = d.total();
		else
			totalReal = total;
		
		
		for(int i=0;i<d.numClasses();i++){
			termA = termA.multiply(factorial(d.perClass(i)));
		}
		System.out.println("TermA = "+termA);
	//	termA = termA.divide(factorial(sum_weights));
		
		for(int j=0;j<d.numBags();j++){
			termB = factorial(d.perBag(j));
			termC = BigInteger.ONE;
			for(int k=0;k<d.numClasses();k++){
				termC = termC.multiply(factorial(d.perClassPerBag(j,k)));
			}
			termD = termD.multiply(termB.divide(termC));
		}
		System.out.println("TermD = "+termD);
		termA = termA.multiply(termD);
		System.out.println("Fatorial do total = "+factorial(totalReal));
		System.out.println("Final = "+termA.divide(factorial(totalReal)));
		return (termA.divide(factorial(totalReal))).doubleValue();
		
	}
	
	
	public  BigInteger factorial(double num){
		BigInteger sum = BigInteger.ONE;
		if(num == 0.0)
			return BigInteger.ONE;
		
		for(int i=1;i<=num;i++)
			sum=sum.multiply(BigInteger.valueOf(i));
		return sum;
	}
	
 

}
