package headDt.split;

import java.io.Serializable;

import headDt.topDown.Distribution;



public abstract class Criterion implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean maximized;
	protected boolean binary;
	private final double log2 = Math.log(2);

	public abstract double getValue(Distribution d, double total, int strategy);

	public double log2(double num){
		// Constant hard coded for efficiency reasons
		if (num < 1e-6)
			return 0;
		else
			return Math.log(num)/log2;
	}
	
	public double logFunc(double num){
		if (num < 1e-6)
			return 0;
		else
			return num*Math.log(num)/log2;
		
	}
	
	public double selfEntropy(Distribution bags, double total){
		//System.out.println("SelfEntropy");
		double returnValue = 0;
		for (int j=0;j<bags.numBags();j++)
			returnValue = returnValue+((bags.perBag(j)/total)*log2(bags.perBag(j)/total));
		
		double unknownRate = (total-bags.total())/total;
		returnValue = returnValue + (unknownRate*log2(unknownRate));
		return -returnValue;
	}
	

	public double entropyBefore(Distribution bags, double total){
		double returnValue = 0;
	//	System.out.println("EntropyBefore");
		for (int j=0;j<bags.numClasses();j++)
			returnValue = returnValue+((bags.perClass(j)/total)*log2(bags.perClass(j)/total));
		return -returnValue;
	}

	public double entropyAfter(Distribution bags, double total){
		double sum = 0;
		double returnValue = 0;
	//	System.out.println("EntropyAfter");
		for(int i=0;i<bags.numBags();i++){
			sum = 0;
			if(bags.perBag(i) > 0){
				for(int j=0;j<bags.numClasses();j++){
					sum = sum + ((bags.perClassPerBag(i,j)/bags.perBag(i))*log2(bags.perClassPerBag(i,j)/bags.perBag(i)));
				}
			}
			returnValue = returnValue + ((bags.perBag(i)/total) * (-sum));
		}
		return returnValue;
	} 
	

	
/*	public final double entropyBefore(Distribution bags, double total) {

		double returnValue = 0;
		int j;

		for (j=0;j<bags.numClasses();j++)
			returnValue = returnValue+logFunc(bags.perClass(j));
		return logFunc(total)-returnValue; 
	}


	public final double entropyAfter(Distribution bags, double total) {

		double returnValue = 0;
		int i,j;

		for (i=0;i<bags.numBags();i++){
			for (j=0;j<bags.numClasses();j++)
				returnValue = returnValue+logFunc(bags.perClassPerBag(i,j));
			returnValue = returnValue-logFunc(bags.perBag(i));
		}
		return -returnValue;
	}
	*/

	public double jointEntropy(Distribution bags, double total){
		double result = 0;
//		System.out.println("jointEntropy");
		for(int i = 0; i<bags.numBags();i++){
			for(int j = 0; j<bags.numClasses();j++){
				result = result + ((bags.perClassPerBag(i, j)/total)*log2((bags.perClassPerBag(i, j)/total)));
			}
		}
		return (-result);

	}

	public boolean isMaximized(){
		return this.maximized;
	}
	
	public boolean isBinary(){
		return this.binary;
	}

}
