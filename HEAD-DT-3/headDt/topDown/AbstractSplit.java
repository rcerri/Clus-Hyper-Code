/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    ClassifierSplitModel.java
 *    Copyright (C) 1999 University of Waikato, Hamilton, New Zealand
 *
 */

package headDt.topDown;

import headDt.split.Measure;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionHandler;
import weka.core.Utils;
import java.io.Serializable;
import java.util.Enumeration;

/** 
 * Abstract class for classification models that can be used 
 * recursively to split the data.
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @version $Revision: 1.11 $
 */
public abstract class AbstractSplit
implements Cloneable, Serializable, RevisionHandler {

	/** for serialization */
	private static final long serialVersionUID = 4280730118393457457L;
	
	private final double log2 = Math.log(2);

	/** Distribution of class values. */  
	protected Distribution m_distribution;  

	/** Number of created subsets. */
	protected int m_numSubsets;   

	/** Attribute to split on. */
	protected int m_attIndex;  

	/** Value of split point. */
	protected double m_splitPoint;

	/** Instances from each class - to use in missing value strategy */
	protected Instances[] classes;

	/** Split Missing Value strategy */
	protected int m_missingValueDistribution;
	
	/** Measure used for splitting */
	protected Measure m_measure;


	/**
	 * Allows to clone a model (shallow copy).
	 */
	public Object clone() {

		Object clone = null;

		try {
			clone = super.clone();
		} catch (CloneNotSupportedException e) {
		} 
		return clone;
	}



	/**
	 * Checks if generated model is valid.
	 */
	public boolean checkModel() {

		if (m_numSubsets > 0)
			return true;
		else
			return false;
	}

	/**
	 * Classifies a given instance.
	 *
	 * @exception Exception if something goes wrong
	 */
	public final double classifyInstance(Instance instance)
	throws Exception {

		int theSubset;

		theSubset = whichSubset(instance);
		if (theSubset > -1)
			return (double)m_distribution.maxClass(theSubset);
		else
			return (double)m_distribution.maxClass();
	}

	/**
	 * Gets class probability for instance.
	 *
	 * @exception Exception if something goes wrong
	 */
	public double classProb(int classIndex, Instance instance, int theSubset) 
	throws Exception {

		if (theSubset > -1) {
			return m_distribution.prob(classIndex,theSubset);
		} else {
			double [] weights = weights(instance);
			if (weights == null) {
				return m_distribution.prob(classIndex);
			} else {
				double prob = 0;
				for (int i = 0; i < weights.length; i++) {
					prob += weights[i] * m_distribution.prob(classIndex, i);
				}
				return prob;
			}
		}
	}

	/**
	 * Gets class probability for instance.
	 *
	 * @exception Exception if something goes wrong
	 */
	public double classProbLaplace(int classIndex, Instance instance,
			int theSubset) throws Exception {

		if (theSubset > -1) {
			return m_distribution.laplaceProb(classIndex, theSubset);
		} else {
			double [] weights = weights(instance);
			if (weights == null) {
				return m_distribution.laplaceProb(classIndex);
			} else {
				double prob = 0;
				for (int i = 0; i < weights.length; i++) {
					prob += weights[i] * m_distribution.laplaceProb(classIndex, i);
				}
				return prob;
			}
		}
	}

	/**
	 * Returns coding costs of model. Returns 0 if not overwritten.
	 */
	public double codingCost() {

		return 0;
	}

	/**
	 * Returns the distribution of class values induced by the model.
	 */
	public final Distribution distribution() {

		return m_distribution;
	}

	/**
	 * Prints left side of condition satisfied by instances.
	 *
	 * @param data the data.
	 */
	public abstract String leftSide(Instances data);

	/**
	 * Prints left side of condition satisfied by instances in subset index.
	 */
	public abstract String rightSide(int index,Instances data);

	/**
	 * Prints label for subset index of instances (eg class).
	 *
	 * @exception Exception if something goes wrong
	 */
	public final String dumpLabel(int index,Instances data) throws Exception {

		StringBuffer text;

		text = new StringBuffer();
		text.append(((Instances)data).classAttribute().value(m_distribution.maxClass(index)));
		text.append(" ("+Utils.roundDouble(m_distribution.perBag(index),2));
		if (Utils.gr(m_distribution.numIncorrect(index),0))
			text.append("/"+Utils.roundDouble(m_distribution.numIncorrect(index),2));
		text.append(")");

		return text.toString();
	}

	public final String sourceClass(int index, Instances data) throws Exception {

		System.err.println("sourceClass");
		return (new StringBuffer(m_distribution.maxClass(index))).toString();
	}

	public abstract String sourceExpression(int index, Instances data);

	/**
	 * Prints the split model.
	 *
	 * @exception Exception if something goes wrong
	 */
	public final String dumpModel(Instances data) throws Exception {

		StringBuffer text;
		int i;

		text = new StringBuffer();
		for (i=0;i<m_numSubsets;i++) {
			text.append(leftSide(data)+rightSide(i,data)+": ");
			text.append(dumpLabel(i,data)+"\n");
		}
		return text.toString();
	}

	/**
	 * Returns the number of created subsets for the split.
	 */
	public final int numSubsets() {

		return m_numSubsets;
	}

	/**
	 * Sets distribution associated with model.
	 */
	//	public void resetDistribution(Instances data) throws Exception {

	//	m_distribution = new Distribution(data, this);
	//	}

	/**
	 * Splits the given set of instances into subsets.
	 *
	 * @exception Exception if something goes wrong
	 */
	public final Instances [] split(Instances data) 
	throws Exception { 

		Instances [] instances = new Instances [m_numSubsets];
		double [] weights;
		double newWeight;
		Instance instance;
		int subset, i, j;
		int missingSet;
		 double[][] probs = new double[m_distribution.numClasses()][m_distribution.numBags()]; 

		//initialize
		for (j=0;j<m_numSubsets;j++)
			instances[j] = new Instances((Instances)data, data.numInstances());
		
		// compute probabilities --> needed for missing value strategy number 6
		if(m_missingValueDistribution == 6){
			for(i = 0; i < m_distribution.numClasses();i++)
				for (j = 0; j < m_numSubsets; j++)
					probs[i][j] = m_distribution.prob(i,j);
		}

		for (i = 0; i < data.numInstances(); i++) {
			instance = ((Instances) data).instance(i);
			weights = weights(instance);
			subset = whichSubset(instance);
			if (subset > -1)
				instances[subset].add(instance);

			// missing value strategy for distributing instances
			else{
				switch(m_missingValueDistribution){

				// Add all instances with unknown values for the corresponding attribute to the distribution for the model, 
				// according to the Kononenko et al. 1984 strategy (same strategy of C4.5)
				case(0): for (j = 0; j < m_numSubsets; j++){
							if (Utils.gr(weights[j],0)) {
								newWeight = weights[j]*instance.weight();
								instances[j].add(instance);
								instances[j].lastInstance().setWeight(newWeight);
							}
						}
				break;

				// Ignore missing values (do not distribute them)
				case(1): break;

				// Replace by mean/mode unsupervised
				case(2): missingSet = fillInstance(instance,data,false);
						 instances[missingSet].add(instance);
				break;
						 
				// Replace by mean/mode supervised
				case(3): missingSet = fillInstance(instance,data,true);
						 instances[missingSet].add(instance);
				break;
						 
				// Add to all bags
				case(4): for (j = 0; j < m_numSubsets; j++)
							instances[j].add(instance);
				break;
				
				// Add to most probable bag
				case(5): missingSet = m_distribution.maxBag();
				         instances[missingSet].add(instance);
				break;
				
				// Add to most probable bag considering the class of the current instance (see method prob(j,i) in Distribution)
				case(6): int classIndex = (int)instance.classValue();
						 missingSet = Utils.maxIndex(probs[classIndex]);
						 instances[missingSet].add(instance);
				break;
				}
			}
		}
		for (j = 0; j < m_numSubsets; j++)
			instances[j].compactify();

		return instances;
	}
	
	
	public int fillInstance(Instance inst, Instances data, boolean supervised) throws Exception{
		DenseInstance filledInstance = new DenseInstance(inst);
		Double value;
		if(supervised){
			int theClass = (int)inst.classValue();
			value = classes[theClass].meanOrMode(m_attIndex);
			filledInstance.setValue(m_attIndex, value);
		}
		else{
			value = data.meanOrMode(m_attIndex);
			filledInstance.setValue(m_attIndex, value);
		}
		int returnValue = whichSubset(filledInstance);
		return returnValue;
		
	}

	/**
	 * Returns weights if instance is assigned to more than one subset.
	 * Returns null if instance is only assigned to one subset.
	 */
	public abstract double [] weights(Instance instance);

	/**
	 * Returns index of subset instance is assigned to.
	 * Returns -1 if instance is assigned to more than one subset.
	 *
	 * @exception Exception if something goes wrong
	 */
	public abstract int whichSubset(Instance instance) throws Exception;

	public abstract void buildSplit(Instances data) throws Exception;

	public abstract double measureValue();

	public int attIndex(){
		return this.m_attIndex;
	}

	/**
	 * Returns the split point (numeric attribute only).
	 * 
	 * @return the split point used for a test on a numeric attribute
	 */
	public double splitPoint() {
		return m_splitPoint;
	}

	/**
	 * Sets split point to greatest value in given data smaller or equal to
	 * old split point.
	 * (C4.5 does this for some strange reason).
	 */
	public final void setSplitPoint(Instances allInstances) {

		double newSplitPoint = -Double.MAX_VALUE;
		double tempValue;
		Instance instance;

		if ((allInstances.attribute(m_attIndex).isNumeric()) &&
				(m_numSubsets > 1)) {
			Enumeration<?> enu = allInstances.enumerateInstances();
			while (enu.hasMoreElements()) {
				instance = (Instance) enu.nextElement();
				if (!instance.isMissing(m_attIndex)) {
					tempValue = instance.value(m_attIndex);
					if (Utils.gr(tempValue,newSplitPoint) && 
							Utils.smOrEq(tempValue,m_splitPoint))
						newSplitPoint = tempValue;
				}
			}
			m_splitPoint = newSplitPoint;
		}
	}


	/**
	 * Sets distribution associated with model.
	 * @throws Exception 
	 */
	public void resetDistribution(Instances data) throws Exception {

		m_distribution = new Distribution(data, this);
	}


	public void createListOfClasses(Instances train){
		classes = new Instances[train.numClasses()];

		for(int j=0;j<train.numClasses();j++){
			classes[j] = new Instances(train,train.numInstances());

		}
		for(int i=0;i<train.numInstances();i++) {
			int theClass = (int)train.instance(i).classValue();
			classes[theClass].add(train.instance(i));
		}
	}



	@Override
	public String getRevision() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Measure getMeasure(){
		return m_measure;
	}
	

}





