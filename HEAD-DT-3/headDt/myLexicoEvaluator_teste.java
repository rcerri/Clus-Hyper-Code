package headDt;

import ec.EvolutionState;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleEvaluator;


public class myLexicoEvaluator_teste extends SimpleEvaluator {

	public void evaluatePopulation(final EvolutionState state) {
		super.evaluatePopulation(state);
		
		for(int x = 0;x<state.population.subpops.length;x++) {
			for(int y = 0;y<state.population.subpops[x].individuals.length;y++) {
				Individual ind =  state.population.subpops[x].individuals[y];
				((myMultiObjectiveFitness)ind.fitness).measures.setFtb(0);
				((myMultiObjectiveFitness)ind.fitness).measures.setWins(0);
				double[] objectives = new double[2];
				//objectives[0] = ((myMultiObjectiveFitness)ind.fitness).measures.getfMeasure()[1];
				//objectives[1] = ((myMultiObjectiveFitness)ind.fitness).measures.getTreeSize();
				
				objectives[0] = ((myMultiObjectiveFitness)ind.fitness).measures.getBalance()[1];
				objectives[1] = ((myMultiObjectiveFitness)ind.fitness).measures.getTreeSize();
				
				if (objectives[1] == 1) {
					objectives[1] = 999; 
				}
				((MultiObjectiveFitness)ind.fitness).setObjectives(state, objectives);
			}
		}
		
		for(int x = 0;x<state.population.subpops.length;x++) {
			for(int y = 0;y<state.population.subpops[x].individuals.length;y++) {
				Individual inda =  state.population.subpops[x].individuals[y];
				for(int w = 0; w < state.population.subpops.length; w++) {
					for(int z = 0; z < state.population.subpops[w].individuals.length; z++) {
						// numero de WINS!!
						Individual indb =  state.population.subpops[w].individuals[z];
						if (((MultiObjectiveFitness)inda.fitness).paretoDominates((MultiObjectiveFitness)indb.fitness)) {
							((myMultiObjectiveFitness)inda.fitness).measures.incWins();
							((myMultiObjectiveFitness)inda.fitness).measures.incFtb();
							((myMultiObjectiveFitness)indb.fitness).measures.incLoses();
							((myMultiObjectiveFitness)indb.fitness).measures.decFtb();
						}
					}
				}
			}
		}
		
	}
}

