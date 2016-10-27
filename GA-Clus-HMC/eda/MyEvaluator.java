package eda;

import Util.ClusWrapperNonStatic;
import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleEvaluator;
import ec.util.QuickSort;
import ec.util.SortComparatorL;

public class MyEvaluator extends SimpleEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void evaluatePopulation(final EvolutionState state, final ClusWrapperNonStatic object){

		// do what superclass does 
		super.evaluatePopulation(state, object);


		// find out the population's size
		int numInd = state.population.subpops[0].individuals.length;


		int[] orderedPop = new int[numInd];
		for(int x=0;x<numInd;x++) orderedPop[x] = x;

		// sort the individuals according to fitness
		QuickSort.qsort(orderedPop, new EliteComparator(state.population.subpops[0].individuals));

		// load the 50% best individuals into "bestIndividuals"
		Individual[] bestIndividuals = new Individual[numInd/2];

		int y = numInd-1;
		for(int x=0;x<bestIndividuals.length;x++){
			bestIndividuals[x] = (MyBitVectorIndividual)(state.population.subpops[0].individuals[orderedPop[y]].clone());
			y--;
		//	System.out.println("Fitness do individuo "+x + " = "+bestIndividuals[x].fitness.fitness());
		}


		// find out the genome's size
		int genomeSize = ((MyBitVectorIndividual)bestIndividuals[0]).genome.length;


		// update probabilities vector
		double genomeCounts[] = new double[genomeSize];
		for(int j=0; j<genomeSize; j++){
			for(int i=0; i<bestIndividuals.length;i++){
				if(((MyBitVectorIndividual)bestIndividuals[i]).genome[j])  //if it is a medoid (boolean position is true)
					genomeCounts[j]++;
			}
			genomeCounts[j] /= bestIndividuals.length;
			EDAMain.probabilities[j] = genomeCounts[j];
		}

	}

	static class EliteComparator implements SortComparatorL
	{
		Individual[] inds;
		public EliteComparator(Individual[] inds) {super(); this.inds = inds;}
		public boolean lt(long a, long b)
		{ return inds[(int)b].fitness.betterThan(inds[(int)a].fitness); }
		public boolean gt(long a, long b)
		{ return inds[(int)a].fitness.betterThan(inds[(int)b].fitness); }
	}

}


