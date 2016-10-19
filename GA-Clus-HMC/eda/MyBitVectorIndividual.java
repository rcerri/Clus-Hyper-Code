package eda;

import ec.EvolutionState;
import ec.vector.BitVectorIndividual;

public class MyBitVectorIndividual extends BitVectorIndividual {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double index;
	private int numClusters;


	/** Initialize individual according to a probability vector (fixed in 0.05 for a GA and with variable probabilities for an EDA) */
    public void reset(EvolutionState state, int thread)
        {
        for(int x=0;x<genome.length;x++)
            genome[x] = state.random[thread].nextBoolean(EDAMain.probabilities[x]);
        }

	public int getNumClusters() {
		return numClusters;
	}

	public void setNumClusters(int numClusters) {
		this.numClusters = numClusters;
	}

	public double getIndex() {
		return index;
	}

	public void setIndex(double index) {
		this.index = index;
	}
	

}
