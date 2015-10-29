package headDt;

import weka.classifiers.Evaluation;
import weka.core.Instances;

public class myEvaluation extends Evaluation{

	public myEvaluation(Instances data) throws Exception {
		super(data);
	}

	public double fMeasure(int classIndex, double beta) {

		double precision = precision(classIndex);
		double recall = recall(classIndex);
		if ((precision + recall) == 0) {
			return 0;
		}
		return (1 + beta*beta) * precision * recall / ( beta*beta * (precision + recall) );
	}

}
