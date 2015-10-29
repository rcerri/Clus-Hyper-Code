package headDt;

import ec.EvolutionState;
import ec.Individual;
import ec.select.TournamentSelection;

public class myParetoSelection extends TournamentSelection{
	
	public int produce(final int subpopulation,
			final EvolutionState state,
			final int thread) {
		// pick size random individuals, then pick the best.
		Individual[] oldinds = state.population.subpops[subpopulation].individuals;
		int best = getRandomIndividual(0, subpopulation, state, thread);

		int s = getTournamentSizeToUse(state.random[thread]);
		
		for (int x = 1; x < s; x++) {
			int j = getRandomIndividual(x, subpopulation, state, thread);
			if (betterThan(oldinds[j], oldinds[best], subpopulation, state, thread))  // j is better than best
				best = j;
		}

		return best;
	}

}

