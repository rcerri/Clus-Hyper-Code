package Util;

public class myMeasures{

	// added by Isaac for multi-target problems
	public double mae[] = new double[2];
	public double mse[] = new double[2];
	public double rmse[] = new double[2];
	public double wrmse[] = new double[2];

	
	public double accuracy[] = new double[2];
	public double f1[] = new double[2];
	public double wmseNominal[] = new double[2];
	
	public double AUROC[] = new double [2];
	
	public double AUPRC[] = new double [2];

	// add performance measures for regression problems
	
	public boolean problem = true; // true = classification  or   false = regression
	
	
	public double averageTreeSize = 0;

	
	public myMeasures() {
		for (int i = 0; i < 2; i++) {
			mae[i] = 0;
			mse[i] = 0;
			rmse[i] = 0;
			wrmse[i] = 0;
		}
	}


	public double[] getMAE() {
		return mae;
	}


	public void setMAE(double[] mae) {
		this.mae = mae;
	}


	public double[] getMSE() {
		return mse;
	}


	public void setMSE(double[] mse) {
		this.mse = mse;
	}


	public double[] getRMSE() {
		return rmse;
	}


	public void setRMSE(double[] rmse) {
		this.rmse = rmse;
	}

	
	public double[] getWRMSE() {
		return wrmse;
	}


	public void setWRMSE(double[] wrmse) {
		this.wrmse = wrmse;
	}

	
	public double[] getAccuracy() {
		return accuracy;
	}


	public void setAccuracy(double[] accuracy) {
		this.accuracy = accuracy;
	}

	public double[] getF1() {
		return f1;
	}


	public void setF1(double[] f1) {
		this.f1 = f1;
	}
	
	
	public double[] getWMSEnominal() {
		return wmseNominal;
	}


	public void setWMSEnominal(double[] wmseNominal) {
		this.wmseNominal = wmseNominal;
	}

	
	public double[] getAUROC() {
		return AUROC;
	}


	public void setAUROC(double[] roc) {
		this.AUROC = roc;
	}
	
	public double[] getAUPRC() {
		return AUPRC;
	}


	public void setAUPRC(double[] pr) {
		this.AUPRC = pr;
	}
	
}

