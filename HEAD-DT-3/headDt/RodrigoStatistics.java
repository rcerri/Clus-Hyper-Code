package headDt;

import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleShortStatistics;

public class RodrigoStatistics extends SimpleShortStatistics {

	/**
	 * Overwrites the postEvaluationStatistics method to include worst fitness
	 * and also to store the fitness of all population
	 */

	private static final long serialVersionUID = 1L;

	public void _postEvaluationStatistics(final EvolutionState state){
		super._postEvaluationStatistics(state);  //always call this

		Individual[] worst_i = new Individual[state.population.subpops.length];
		Main.printPop.print(state.generation);
		for(int y=0;y<state.population.subpops[0].individuals.length;y++){
			
			Main.printPop.print(","+state.population.subpops[0].individuals[y].fitness.fitness());
			
			// worst individual
			if (worst_i[0]==null ||
					worst_i[0].fitness.betterThan(state.population.subpops[0].individuals[y].fitness))
				worst_i[0] = state.population.subpops[0].individuals[y];
		}
		Main.printPop.println("");
		state.output.print("" + worst_i[0].fitness.fitness() + " ",
				statisticslog);
	}

}
