package headDt;

import weka.core.Utils;

public class MetaData {

	private double splitCriterion[][];
	private double binarySplit[][];
	private double stopCriterion[][];
	private double mvSplit[][];
	private double mvDistribution[][];
	private double mvClassification[][];
	private double pruning[][];
	private double STOPminNumber[][];
	private double STOPminPerc[][];
	private double STOPminAcc[][];
	private double STOPmaxDepth[][];
	private double EBP[][];
	private double MEP[][];
	private double PEP[][];
	private double CCP1[][];
	private double CCP2[][];
	private double REP[][];


	public MetaData(){
		this.splitCriterion = new double[2][15];
		this.binarySplit = new double[2][2];
		this.stopCriterion = new double[2][5];
		this.mvSplit = new double[2][4];
		this.mvDistribution = new double[2][7];
		this.mvClassification = new double[2][3];
		this.pruning = new double[2][6];
		this.STOPmaxDepth = new double[2][9];
		this.STOPminAcc = new double[2][7];
		this.STOPminNumber = new double[2][20];     
		this.STOPminPerc = new double[2][10];
		this.EBP = new double[2][50];
		this.MEP = new double[2][100];
		this.PEP = new double[2][4];
		this.CCP1 = new double[2][4];
		this.CCP2 = new double[2][9];
		this.REP = new double[2][9];

	}

	public void setSplitCriterion(int criterion, double fitness) {
		this.splitCriterion[0][criterion] += fitness;
		this.splitCriterion[1][criterion] += 1;
	}
	public String getSplitCriterion() {
		for(int i=0; i<splitCriterion[0].length; i++){
			if(splitCriterion[1][i] > 0)
				splitCriterion[0][i] = splitCriterion[0][i] / splitCriterion[1][i];
			else
				splitCriterion[0][i] = 0;
		}

		int indice = Utils.maxIndex(splitCriterion[0]);

		switch(indice){
		case 0: return "InformationGain";
		case 1: return "GiniIndex";
		case 2: return "GlobalMutualInformation";
		case 3: return "CAIR";
		case 4: return "GStatistic";
		case 5: return "DeMant‡ras";
		case 6: return "HyperGeometricDistribution";
		case 7: return "Chandra-Varghese";
		case 8: return "DCSM";
		case 9: return "Chi-Squared";
		case 10: return "MPI";
		case 11: return "NormalizedGain";
		case 12: return "Orthogonal";
		case 13: return "Twoing";
		case 14: return "GainRatio";
		default: return "Error"; 
		}

	}

	public void setBinarySplit(int binarySplit, double fitness) {
		this.binarySplit[0][binarySplit] += fitness;
		this.binarySplit[1][binarySplit] += 1;
	}
	public String getBinarySplit() {

		for(int i=0; i<binarySplit[0].length; i++){
			if(binarySplit[1][i] > 0)
				binarySplit[0][i] = binarySplit[0][i] / binarySplit[1][i];
			else
				binarySplit[0][i] = 0;
		}

		int indice = Utils.maxIndex(binarySplit[0]);
		switch(indice){
		case 0: return "Multi";
		case 1: return "Binary";
		default: return "Error"; 
		}
	}


	public void setMvSplit(int mvSplit, double fitness) {
		this.mvSplit[0][mvSplit] += fitness;
		this.mvSplit[1][mvSplit] += 1;
	}
	public String getMvSplit() {

		for(int i=0; i<mvSplit[0].length; i++){
			if(mvSplit[1][i] > 0)
				mvSplit[0][i] = mvSplit[0][i] / mvSplit[1][i];
			else
				mvSplit[0][i] = 0;
		}

		int indice = Utils.maxIndex(mvSplit[0]);
		switch(indice){
		case 0: return "IgnoreMissing";
		case 1: return "UnsupervisedImputation";
		case 2: return "SupervisedImputation";
		case 3: return "WeightCriterion";
		default: return "Error"; 
		}
	}

	public void setMvDistribution(int mvDistribution, double fitness) {
		this.mvDistribution[0][mvDistribution] += fitness;
		this.mvDistribution[1][mvDistribution] += 1;
	}

	public String getMvDistribution() {

		for(int i=0; i<mvDistribution[0].length; i++){
			if(mvDistribution[1][i] > 0)
				mvDistribution[0][i] = mvDistribution[0][i] / mvDistribution[1][i];
			else
				mvDistribution[0][i] = 0;
		}

		int indice = Utils.maxIndex(mvDistribution[0]);
		switch(indice){
		case 0: return "WeightWithBagProbability";
		case 1: return "IgnoreMissingValues";
		case 2: return "UnsupervisedImputation";
		case 3: return "SupervisedImputation";
		case 4: return "AssignToAllBags";
		case 5: return "AddToLargestBag";
		case 6: return "AssignToMostProbableBagRegardingClass";
		default: return "Error"; 
		}

	}

	public void setMvClassification(int mvClassification, double fitness) {
		this.mvClassification[0][mvClassification] += fitness;
		this.mvClassification[1][mvClassification] += 1;
	}

	public String getMvClassification() {

		for(int i=0; i<mvClassification[0].length; i++){
			if(mvClassification[1][i] > 0)
				mvClassification[0][i] = mvClassification[0][i] / mvClassification[1][i];
			else
				mvClassification[0][i] = 0;
		}

		int indice = Utils.maxIndex(mvClassification[0]);
		switch(indice){
		case 0: return "ExploreAllBranchesAndCombine";
		case 1: return "HaltInTheCurrentNode";
		case 2: return "GoToMostProbableBag";
		default: return "Error"; 
		}
	}


	public void setStopCriterion(int stopCriterion, double fitness) {
		this.stopCriterion[0][stopCriterion] += fitness;
		this.stopCriterion[1][stopCriterion] += 1;
	}
	public String getStopCriterion() {

		for(int i=0; i<stopCriterion[0].length; i++){
			if(stopCriterion[1][i] > 0)
				stopCriterion[0][i] = stopCriterion[0][i] / stopCriterion[1][i];
			else
				stopCriterion[0][i] = 0;
		}

		int indice = Utils.maxIndex(stopCriterion[0]);
		switch(indice){
		case 0: return "Homogeneity";
		case 1: return "MinNumber-"+this.getSTOPminNumber();
		case 2: return "MinPercentage-"+this.getSTOPminPerc();
		case 3: return "MinAccuracy-"+this.getSTOPminAcc();
		case 4: return "MaxDepth-"+this.getSTOPmaxDepth();
		default: return "Error"; 
		}
	}

	public void setPruning(int pruning, double fitness) {
		this.pruning[0][pruning] += fitness;
		this.pruning[1][pruning] += 1;
	}

	public String getPruning() {

		for(int i=0; i<pruning[0].length; i++){
			if(pruning[1][i] > 0)
				pruning[0][i] = pruning[0][i] / pruning[1][i];
			else
				pruning[0][i] = 0;
		}

		int indice = Utils.maxIndex(pruning[0]);
		switch(indice){
		case 0: return "NoPruning";
		case 1: return "ErrorBasedPruning-"+this.getEBP();
		case 2: return "MinimumErrorPruning-"+this.getMEP();
		case 3: return "PessimisticErrorPruning-"+this.getPEP();
		case 4: return "CostComplexityPruning-"+this.getCCP1()+"-"+this.getCCP2();
		case 5: return "ReducedErrorPruning-"+this.getREP();
		default: return "Error"; 
		}
	}

	public void setSTOPminNumber(int parameter, double fitness){
		this.STOPminNumber[0][parameter] += fitness;
		this.STOPminNumber[1][parameter] += 1;
	}

	public String getSTOPminNumber(){

		for(int i=0; i<STOPminNumber[0].length; i++){
			if(STOPminNumber[1][i] > 0)
				STOPminNumber[0][i] = STOPminNumber[0][i] / STOPminNumber[1][i];
			else
				STOPminNumber[0][i] = 0;
		}

		int indice = Utils.maxIndex(STOPminNumber[0]) + 1;
		
		int binSize = STOPminNumber[0].length / 5;
		int result = indice / binSize;
		int mod = indice % binSize;
		if(mod > 0)
			result = result + 1;
		
		switch(result-1){
		case 0: return "VeryLow";
		case 1: return "Low";
		case 2: return "Medium";
		case 3: return "High";
		case 4: return "VeryHigh";
		default: return "Error"; 
		}
		
	}

	public void setSTOPminPerc(int parameter, double fitness){
		this.STOPminPerc[0][parameter] += fitness;
		this.STOPminPerc[1][parameter] += 1;
	}

	public String getSTOPminPerc(){

		for(int i=0; i<STOPminPerc[0].length; i++){
			if(STOPminPerc[1][i] > 0)
				STOPminPerc[0][i] = STOPminPerc[0][i] / STOPminPerc[1][i];
			else
				STOPminPerc[0][i] = 0;
		}

		int indice = Utils.maxIndex(STOPminPerc[0]) + 1;
		
		int binSize = STOPminPerc[0].length / 5;
		int result = indice / binSize;
		int mod = indice % binSize;
		if(mod > 0)
			result = result + 1;
		
		switch(result-1){
		case 0: return "VeryLow";
		case 1: return "Low";
		case 2: return "Medium";
		case 3: return "High";
		case 4: return "VeryHigh";
		default: return "Error"; 
		}
	}

	public void setSTOPminAcc(int parameter, double fitness){
		this.STOPminAcc[0][parameter] += fitness;
		this.STOPminAcc[1][parameter] += 1;
	}

	public String getSTOPminAcc(){

		for(int i=0; i<STOPminAcc[0].length; i++){
			if(STOPminAcc[1][i] > 0)
				STOPminAcc[0][i] = STOPminAcc[0][i] / STOPminAcc[1][i];
			else
				STOPminAcc[0][i] = 0;
		}

		int indice = Utils.maxIndex(STOPminAcc[0]);
		switch(indice){
		case 0: return "70%";
		case 1: return "75%";
		case 2: return "80%";
		case 3: return "85%";
		case 4: return "90%";
		case 5: return "95%";
		case 6: return "99%";
		default: return "Error"; 
		}
	}

	public void setSTOPmaxDepth(int parameter, double fitness){
		this.STOPmaxDepth[0][parameter] += fitness;
		this.STOPmaxDepth[1][parameter] += 1;
	}

	public String getSTOPmaxDepth(){

		for(int i=0; i<STOPmaxDepth[0].length; i++){
			if(STOPmaxDepth[1][i] > 0)
				STOPmaxDepth[0][i] = STOPmaxDepth[0][i] / STOPmaxDepth[1][i];
			else
				STOPmaxDepth[0][i] = 0;
		}

		int indice = Utils.maxIndex(STOPmaxDepth[0]);
		
		if((indice+2) <=3)
			return "VeryLow";
		else if((indice+2) <=5)
			return "Low";
		else if((indice+2) <=7)
			return "Medium";
		else if((indice+2) <=9)
			return "High";
		else
			return "VeryHigh";
	}

	public void setEBP(int parameter, double fitness){
		this.EBP[0][parameter] += fitness;
		this.EBP[1][parameter] += 1;
	}

	public String getEBP(){

		for(int i=0; i<EBP[0].length; i++){
			if(EBP[1][i] > 0)
				EBP[0][i] = EBP[0][i] / EBP[1][i];
			else
				EBP[0][i] = 0;
		}

		int indice = Utils.maxIndex(EBP[0]);
		
		if((indice+1) <=10)
			return "VeryLow";
		else if((indice+1) <=20)
			return "Low";
		else if((indice+1) <=30)
			return "Medium";
		else if((indice+1) <=40)
			return "High";
		else
			return "VeryHigh";
	}

	public void setMEP(int parameter, double fitness){
		this.MEP[0][parameter] += fitness;
		this.MEP[1][parameter] += 1;
	}

	public String getMEP(){

		for(int i=0; i<MEP[0].length; i++){
			if(MEP[1][i] > 0)
				MEP[0][i] = MEP[0][i] / MEP[1][i];
			else
				MEP[0][i] = 0;
		}

		int indice = Utils.maxIndex(MEP[0]);
		
		if((indice) <=19)
			return "VeryLow";
		else if((indice) <=39)
			return "Low";
		else if((indice) <=59)
			return "Medium";
		else if((indice) <=79)
			return "High";
		else
			return "VeryHigh";
	}

	public void setPEP(int parameter, double fitness){
		this.PEP[0][parameter] += fitness;
		this.PEP[1][parameter] += 1;
	}

	public String getPEP(){

		for(int i=0; i<PEP[0].length; i++){
			if(PEP[1][i] > 0)
				PEP[0][i] = PEP[0][i] / PEP[1][i];
			else
				PEP[0][i] = 0;
		}

		int indice = Utils.maxIndex(PEP[0]);
		switch(indice){
		case 0: return "0.5-SE";
		case 1: return "1-SE";
		case 2: return "1.5-SE";
		case 3: return "2-SE";
		default: return "Error"; 
		}
	}

	public void setCCP1(int parameter, double fitness){
		this.CCP1[0][parameter] += fitness;
		this.CCP1[1][parameter] += 1;
	}

	public String getCCP1(){

		for(int i=0; i<CCP1[0].length; i++){
			if(CCP1[1][i] > 0)
				CCP1[0][i] = CCP1[0][i] / CCP1[1][i];
			else
				CCP1[0][i] = 0;
		}

		int indice = Utils.maxIndex(CCP1[0]);
		switch(indice){
		case 0: return "0.5-SE";
		case 1: return "1-SE";
		case 2: return "1.5-SE";
		case 3: return "2-SE";
		default: return "Error"; 
		}
	}

	public void setCCP2(int parameter, double fitness){
		this.CCP2[0][parameter] += fitness;
		this.CCP2[1][parameter] += 1;
	}

	public String getCCP2(){

		for(int i=0; i<CCP2[0].length; i++){
			if(CCP2[1][i] > 0)
				CCP2[0][i] = CCP2[0][i] / CCP2[1][i];
			else
				CCP2[0][i] = 0;
		}

		int indice = Utils.maxIndex(CCP2[0]);
	
		
		if((indice+2) <=3)
			return "VeryLow";
		else if((indice+2) <=7)
			return "Low";
		else if((indice+2) <=7)
			return "Medium";
		else if((indice+2) <=9)
			return "High";
		else
			return "VeryHigh";
	}

	public void setREP(int parameter, double fitness){
		this.REP[0][parameter] += fitness;
		this.REP[1][parameter] += 1;
	}

	public String getREP(){

		for(int i=0; i<REP[0].length; i++){
			if(REP[1][i] > 0)
				REP[0][i] = REP[0][i] / REP[1][i];
			else
				REP[0][i] = 0;
		}

		int indice = Utils.maxIndex(REP[0]);
		
		if((indice+2) <=3)
			return "VeryLow";
		else if((indice+2) <=7)
			return "Low";
		else if((indice+2) <=7)
			return "Medium";
		else if((indice+2) <=9)
			return "High";
		else
			return "VeryHigh";
	}









}
