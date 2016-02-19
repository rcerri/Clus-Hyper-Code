package Util;

public class myMeasures{

	// added by Isaac for multi-target problems
	public double mae[] = new double[2];
	public double mse[] = new double[2];
	public double rmse[] = new double[2];
	public double wrmse[] = new double[2];

	
	
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

	
}

