package headDt.split;

import java.io.Serializable;

import headDt.topDown.Distribution;


public class Measure implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Class for selecting the split measure to be used for bulding decision trees */

	private int criterionIndex;
	private Criterion criterion;
	private boolean maximized;
	private boolean binary;


	public Measure(int crit){
		this.criterionIndex = crit;
		switch(this.criterionIndex){
		case 0: criterion = new InfoGain(); break;
		case 1: criterion = new GiniIndex(); break;
		case 2: criterion = new GMI(); break;
		case 3: criterion = new G(); break;
		case 4: criterion = new Mantaras(); break;
		case 5: criterion = new Hypergeometric(); break;
		case 6: criterion = new ChandraVarghese(); break;
		case 7: criterion = new DCSM(); break;
		case 8: criterion = new ChiSquared(); break;
		case 9: criterion = new MPI(); break;
		case 10: criterion = new NormalizedGain(); break;
		case 11: criterion = new Orthogonal(); break;
		case 12: criterion = new Twoing(); break;
		case 13: criterion = new CAIR(); break;
		case 14: criterion = new GainRatio(); break;
		default: criterion = new InfoGain(); break;
		}
		this.maximized = criterion.isMaximized();
		this.binary = criterion.isBinary();
	}


	public void setCriterion(int crit){
		this.criterionIndex = crit;
	}

	public int getCriterionIndex(){
		return criterionIndex;
	}
	
	public Criterion getCriterion(){
		return criterion;
	}


	public double getValue(Distribution d, double sum_weights, int strategy) {
		return this.criterion.getValue(d,sum_weights,strategy);
	}


	public boolean isMaximized() {
		return maximized;
	}
	
	public boolean isBinary(){
		return binary;
	}


	public String getName(){
		switch(this.criterionIndex){
		case 0: return "Information Gain";
		case 1: return "Gini Index";
		case 2: return "Global Mutual Information";
		case 3: return "CAIR";
		case 4: return "G statistic";
		case 5: return "De Mant‡ras Criterion";
		case 6: return "HyperGeometric Distribution";
		case 7: return "Chandra-Varghese Criterion";
		case 8: return "DCSM";
		case 9: return "Chi-Squared Statistic";
		case 10: return "Mean Posterior Improvement";
		case 11: return "Normalized Gain";
		case 12: return "Orthogonal";
		case 13: return "Twoing";
		case 14: return "Gain Ratio";
		case 15: return "Evolved Measure";
		case 99: return "Evolved Measure FIXED";
		default: return "No Criterion Selected. Evolved Measure is the default";
		}
	}
	
	public static String getName(int crit){
		switch(crit){
		case 0: return "InformationGain";
		case 1: return "GiniIndex";
		case 2: return "GlobalMutualInformation";
		case 3: return "CAIR";
		case 4: return "Gstatistic";
		case 5: return "DeMant‡ras";
		case 6: return "HyperGeometric";
		case 7: return "Chandra-Varghese";
		case 8: return "DCSM";
		case 9: return "Chi-SquaredStatistic";
		case 10: return "MeanPosteriorImprovement";
		case 11: return "NormalizedGain";
		case 12: return "Orthogonal";
		case 13: return "Twoing";
		case 14: return "GainRatio";
		case 15: return "Evolved Measure";
		case 99: return "Evolved Measure FIXED";
		default: return "No Criterion Selected. Evolved Measure is the default";
		}
	}
	
	public double initialize(){
		if(this.isMaximized())
			return Double.NEGATIVE_INFINITY;
		else
			return Double.POSITIVE_INFINITY;
	}



}
