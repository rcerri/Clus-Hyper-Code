package headDt;

public class myMeasures{

	public double fMeasure[] = new double[3]; // 0 = treino, 1 = validacao, 2 = teste
	public double accuracy[] = new double[3];

	public double recall[] = new double[3];
	public double precision[] = new double[3];
	public double auc[] = new double[3];
	public double gmean[] = new double[3];
	public double balance[] = new double[3];
	public double avgClassAcc[] = new double[3];
	
	//public double fitness = 0.0;
	
	public double objectives[] = new double[2];
	public boolean maximize[] = new boolean[2];


	public int wins = 0;
	public int loses = 0;
	public int treeSize = 0;
	public int ftb = 0;

	public myMeasures() {
		for (int i = 0; i < 3; i++) {
			fMeasure[i] = 0;
			accuracy[i] = 0;
			recall[i] = 0;
			precision[i] = 0;
			auc[i] = 0;
			gmean[i] = 0;
			balance[i] = 0;
			avgClassAcc[i] = 0;
		}
		wins = 0;
		loses = 0;
		treeSize = 0;
		ftb = 0;

	}


	public double[] getAvgClassAcc() {
		return avgClassAcc;
	}


	public void setAvgClassAcc(double[] avgClassAcc) {
		this.avgClassAcc = avgClassAcc;
	}


	public double[] getRecall() {
		return recall;
	}


	public void setRecall(double[] recall) {
		this.recall = recall;
	}


	public double[] getPrecision() {
		return precision;
	}


	public void setPrecision(double[] precision) {
		this.precision = precision;
	}



	public double[] getAuc() {
		return auc;
	}



	public void setAuc(double[] auc) {
		this.auc = auc;
	}



	public double[] getGmean() {
		return gmean;
	}



	public void setGmean(double[] gmean) {
		this.gmean = gmean;
	}



	public double[] getBalance() {
		return balance;
	}



	public void setBalance(double[] balance) {
		this.balance = balance;
	}



	public double[] getfMeasure() {
		return fMeasure;
	}
	public void setfMeasure(double[] fMeasure) {
		this.fMeasure = fMeasure;
	}
	public double[] getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double[] accuracy) {
		this.accuracy = accuracy;
	}
	public int getWins() {
		return wins;
	}
	public void setWins(int wins) {
		this.wins = wins;
	}
	public int getTreeSize() {
		return treeSize;
	}
	public void setTreeSize(int treeSize) {
		this.treeSize = treeSize;
	}

	public int getLoses() {
		return loses;
	}

	public void setLoses(int loses) {
		this.loses = loses;
	}

	public int getFtb() {
		return ftb;
	}

	public void setFtb(int ftb) {
		this.ftb = ftb;
	}	

	public void incWins() {
		this.wins++;
	}

	public void incLoses() {
		this.loses++;
	}

	public void incFtb() {
		this.ftb++;
	}

	public void decFtb(){
		this.ftb--;
	}

}

