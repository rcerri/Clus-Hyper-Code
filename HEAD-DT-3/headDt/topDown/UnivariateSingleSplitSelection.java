package headDt.topDown;

import java.io.Serializable;
import java.util.Enumeration;

import headDt.split.Measure;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;


public class UnivariateSingleSplitSelection extends AbstractSplitSelection implements Serializable {


	private static final long serialVersionUID = -8460850982571366664L;
	private Measure m_measure;


	/**
	 * Initializes the split selection method with the given parameters.
	 *
	 * @param minNoObj minimum number of instances that have to occur in at least two
	 * subsets induced by split
	 * @param allData FULL training dataset (necessary for
	 * selection of split points).
	 * @param useMDLcorrection whether to use MDL adjustement when
	 * finding splits on numeric attributes
	 */
	public UnivariateSingleSplitSelection(SplitParameters splitParam, Instances allData, int size /*boolean useMDLcorrection,*/) {
		m_minNoObj = splitParam.getMinNumObj();
		m_minPercObj = splitParam.getMinPercObj();
		m_minAccuracy = splitParam.getMinAccuracy();
		m_maxDepth = splitParam.getMaxDepth();
		m_allData = allData;
		//	m_useMDLcorrection = useMDLcorrection;
		m_measure = new Measure(splitParam.getSplitMeasure());
		m_binarySplit = splitParam.isBinarySplits();
		m_betterSplit = splitParam.getBetterSplit();
		m_datasetTotalSize = size;
		m_stoppingCriteria = splitParam.getStoppingCriteria();
		m_missingValueSplit = splitParam.getMissingValueSplit();
	}


	/**
	 * Selects single split for the given dataset.
	 */
	public final AbstractSplit selectModel(Instances data, int depth){

		double best;
		AbstractSplit [] currentModel;
		Attribute attribute;
		AbstractSplit bestModel = null;
		NoSplit noSplitModel = null;
		double averageMeasure = 0;
		int validModels = 0;
		boolean multiVal = true;
		Distribution checkDistribution;
		double sumOfWeights;
		int i;
		try{

			//check the stopping criterion (criteria) --> either to generate an internal node or a leaf!
			checkDistribution = new Distribution(data);
			noSplitModel = new NoSplit(checkDistribution);
			if (isLeaf(checkDistribution,depth))
				return noSplitModel;

			if (m_allData != null) {
				Enumeration<?> enu = data.enumerateAttributes();
				while (enu.hasMoreElements()) {
					attribute = (Attribute) enu.nextElement();
					if ((attribute.isNumeric()) ||	(Utils.sm((double)attribute.numValues(),(0.3*(double)m_allData.numInstances())))){
						multiVal = false;
						break;
					}
				}
			} 


			// initialize split
			if(m_binarySplit || m_measure.isBinary())
				currentModel = new UnivariateBinarySplit[data.numAttributes()];
			else
				currentModel = new UnivariateSplit[data.numAttributes()];

			sumOfWeights = data.sumOfWeights();

			// generate best split for each attribute
			for (i = 0; i < data.numAttributes(); i++){
				//System.out.println("att"+i);
				// except for class attribute.
				if (i != (data).classIndex()){

					currentModel[i] = createNewSplit(i,sumOfWeights,m_useMDLcorrection);
					currentModel[i].buildSplit(data);
					
					//System.out.println("CurrentModel att"+i+" -> splitValue = "+currentModel[i].measureValue());

					// check if useful split for current attribute exists
					if (currentModel[i].checkModel()){
						if (m_allData != null) {
							if ((data.attribute(i).isNumeric()) || (multiVal || Utils.sm((double)data.attribute(i).numValues(),
									(0.3*(double)m_allData.numInstances())))){
								averageMeasure = averageMeasure+currentModel[i].measureValue();
								validModels++;
							} 
						} 
						else {
							averageMeasure = averageMeasure+currentModel[i].measureValue();
							validModels++;
						}
					}
				}
				else
					currentModel[i] = null;
			}

			// Check if any useful split was found.
			if (validModels == 0) {
				//System.out.println("NENHUM SPLIT ÚTIL FUI ENCONTRADO");
				return noSplitModel;
			}

			averageMeasure = averageMeasure/(double)validModels;

			// find "best" attribute to split on.
			best = m_measure.initialize();
			//System.out.println("BEST = "+m_measure.getCriterionIndex());
			

			for (i=0;i<data.numAttributes();i++){
				if ((i != (data).classIndex()) && (currentModel[i].checkModel())){
					if(isBetter(currentModel[i], averageMeasure,best)){
						bestModel = currentModel[i];
						//System.out.println("measureValue = "+currentModel[i].measureValue());
						
						if(m_measure.getCriterionIndex() == 14) //if it is the gain ratio
							best = ((UnivariateSplit)currentModel[i]).getGainRatio();
						else 
							best = currentModel[i].measureValue();
					}
					/*				System.out.println("Attribute  = "+currentModel[i].attIndex());
					System.out.println("Info Gain = "+currentModel[i].measureValue());
					System.out.println("Average Info Gain = "+averageMeasure);
					System.out.println("Gain ratio = "+((UnivariateSplit)currentModel[i]).getGainRatio()); */
				}
			}



			// Check if useful split was found.
			if (best == Double.NEGATIVE_INFINITY || best == Double.POSITIVE_INFINITY || bestModel.distribution().actualNumBags()<=1)
				return noSplitModel;


			// Distribute missing values according to the missing value strategy for distributing instances
			distributeMissingValues(bestModel,data);


			/*			System.out.println();
			System.out.println();
			System.out.println("Selected Attribute  = "+bestModel.attIndex());
			System.out.println("Info Gain = "+bestModel.measureValue());
			System.out.println("Gain ratio = "+((UnivariateSplit)bestModel).getGainRatio());
			System.out.println();
			System.out.println(); */

			// Set the split point analogue to C45 if attribute numeric.
			if(bestModel.m_measure.getCriterionIndex() == 0 || bestModel.m_measure.getCriterionIndex() == 14 ){
				if (m_allData != null)
					bestModel.setSplitPoint(m_allData);
			}

			return bestModel;

		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}


	public final AbstractSplit selectModel(Instances train, Instances test, int depth) {

		return selectModel(train,depth);
	}



	public AbstractSplit createNewSplit(int i, double sumOfWeights, boolean m_useMDLcorrection){
		if(m_binarySplit || m_measure.isBinary()){
			return new UnivariateBinarySplit(i,m_minNoObj,sumOfWeights,m_useMDLcorrection,m_measure,m_missingValueSplit,m_missingValueDistribution);
		}
		else
			return new UnivariateSplit(i,m_minNoObj,sumOfWeights,m_useMDLcorrection,m_measure,m_missingValueSplit,m_missingValueDistribution);
	}

}


