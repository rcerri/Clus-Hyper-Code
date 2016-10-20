/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package ga;

import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleEvolutionState;
import ec.util.Checkpoint;
import ec.util.Parameter;


public class mySimpleEvolutionState extends SimpleEvolutionState {
	Individual[] best_g;
	int lastGeneration;
	int noChange = 0;
	double lastFitness = 0.0;
	
	int seed;
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		//System.out.println("numGenerations = "+super.numGenerations);
		best_g = new Individual[this.numGenerations];
		seed = 0;
		lastGeneration = -1;

	}


	public int evolve()
	{
		if (generation > 0) 
			output.message("Generation " + generation);
		//System.out.println("Seed = "+seed + "---- nGenerations = "+numGenerations);

		// EVALUATION
		statistics.preEvaluationStatistics(this);
		evaluator.evaluatePopulation(this);
		statistics.postEvaluationStatistics(this);

		
		if (lastFitness == best_g[generation].fitness.fitness()) {
			noChange++;
		}
		else {
			noChange = 0;
			lastFitness = best_g[generation].fitness.fitness();
		}
		
		
		//((mySimpleEvolutionState)state).best_g[state.generation] = best_i[0]; // 0: only one pop

		// SHOULD WE QUIT?
		if (evaluator.runComplete(this) && quitOnRunComplete) {
			output.message("Found Ideal Individual");
			lastGeneration = generation;
			return R_SUCCESS;
		}

		// SHOULD WE QUIT?
		if ( (generation == numGenerations-1) || (noChange > Main.maxGenNoChange)) {
			System.out.println("\nMore than " + Main.maxGenNoChange + " generations without changing the best individual.");
			return R_FAILURE;
		}

		// PRE-BREEDING EXCHANGING
		statistics.prePreBreedingExchangeStatistics(this);
		population = exchanger.preBreedingExchangePopulation(this);
		statistics.postPreBreedingExchangeStatistics(this);

		String exchangerWantsToShutdown = exchanger.runComplete(this);
		if (exchangerWantsToShutdown!=null)
		{ 
			output.message(exchangerWantsToShutdown);
			/*
			 * Don't really know what to return here.  The only place I could
			 * find where runComplete ever returns non-null is 
			 * IslandExchange.  However, that can return non-null whether or
			 * not the ideal individual was found (for example, if there was
			 * a communication error with the server).
			 * 
			 * Since the original version of this code didn't care, and the
			 * result was initialized to R_SUCCESS before the while loop, I'm 
			 * just going to return R_SUCCESS here. 
			 */

			return R_SUCCESS;
		}

		// BREEDING
		statistics.preBreedingStatistics(this);

		population = breeder.breedPopulation(this);

		// POST-BREEDING EXCHANGING
		statistics.postBreedingStatistics(this);

		// POST-BREEDING EXCHANGING
		statistics.prePostBreedingExchangeStatistics(this);
		population = exchanger.postBreedingExchangePopulation(this);
		statistics.postPostBreedingExchangeStatistics(this);

		// INCREMENT GENERATION AND CHECKPOINT
		generation++;
		if (checkpoint && generation%checkpointModulo == 0) 
		{
			output.message("Checkpointing");
			statistics.preCheckpointStatistics(this);
			Checkpoint.setCheckpoint(this);
			statistics.postCheckpointStatistics(this);
		}

		return R_NOTDONE;
	}

	/**
	 * @param result
	 */
	public void finish(int result) 
	{
		//Output.message("Finishing");
		/* finish up -- we completed. */
		statistics.finalStatistics(this,result);
		finisher.finishPopulation(this,result);
		exchanger.closeContacts(this,result);
		evaluator.closeContacts(this,result);
	}

}
