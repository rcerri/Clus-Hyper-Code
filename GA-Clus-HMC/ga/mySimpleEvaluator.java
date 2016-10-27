package ga;

import java.util.Random;

import Util.ClusWrapperNonStatic;
import ec.*;
import ec.simple.SimpleEvaluator;
import ec.simple.SimpleProblemForm;
import ec.vector.IntegerVectorIndividual;


public class mySimpleEvaluator extends SimpleEvaluator {

	/** A simple evaluator that doesn't do any coevolutionary
        evaluation.  Basically it applies evaluation pipelines,
        one per thread, to various subchunks of a new population. */
	public void evaluatePopulation(final EvolutionState state, ClusWrapperNonStatic objectClus ) {
		super.evaluatePopulation(state, objectClus);

		if (((mySimpleEvolutionState)state).seed != state.generation / Main.SAgen) {

			System.out.println("starting Simulated Annealing: Generation "+state.generation);
			((mySimpleEvolutionState)state).seed++; 
			SimulatedAnnealing(state,objectClus);
			System.out.println("finishing Simulated Annealing: Seed = " + ((mySimpleEvolutionState)state).seed);
		}

	}

	public void SimulatedAnnealing(EvolutionState state, ClusWrapperNonStatic objectClus ) {

		// for now we just print the best fitness per subpopulation.
		Individual[] best_i = new Individual[state.population.subpops.length];  // quiets compiler complaints
		Individual[] worst_i = new Individual[state.population.subpops.length];  // quiets compiler complaints
		// it doesnt work for multiple populations
		int[] i_worst = {0,0};
		for(int x=0;x<state.population.subpops.length;x++) {
			best_i[x] = state.population.subpops[x].individuals[0];
			worst_i[x] = state.population.subpops[x].individuals[0];
			for(int y=1;y<state.population.subpops[x].individuals.length;y++) {
				if (state.population.subpops[x].individuals[y].fitness.betterThan(best_i[x].fitness))
					best_i[x] = state.population.subpops[x].individuals[y];
				if (!state.population.subpops[x].individuals[y].fitness.betterThan(worst_i[x].fitness)) {
					worst_i[x] = state.population.subpops[x].individuals[y];
					i_worst[0] = x; i_worst[1] = y;
				}
			}
		}
		
		Individual newIndividual;
		try {
			newIndividual = run(state,(SimpleProblemForm)(p_problem.clone()), (Individual) best_i[0].clone(),Main.SAmax,objectClus);
			// substituir na população!!
			state.population.subpops[i_worst[0]].individuals[i_worst[1]] = newIndividual;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public Individual run(EvolutionState state, SimpleProblemForm p, Individual x, int maxIter, ClusWrapperNonStatic objectClus) throws Exception { // talvez passe um individuo já com o genoma e score

		Random rand = new Random();

		//IntegerVectorIndividual mutatedX = (IntegerVectorIndividual) deepClone(x);
		IntegerVectorIndividual mutatedX = (IntegerVectorIndividual) x.clone();

		mutatedX.evaluated = false;

		double alpha = 0.95; // cálculo do alpha, percentual de diminuição da temperatura
		double t_final = 1; 
		double t0 = Math.pow(alpha, maxIter + Math.log(t_final)); // estimar a temperatura inicial
		double temp_atual = t0; // tf = temperatura da vez

		int iter = 0;
		maxIter *= 2;

		while (iter < maxIter) {
			
			int[] genome = mutatedX.genome;
			genome = mutate(genome, 1 - iter/maxIter); // mutar o vetor

			mutatedX.setGenome(genome);

			((ec.Problem)p).prepareToEvaluate(state,0);
			System.out.print("Mutated Genoma: "+mutatedX.genome[0]);
			for (int i = 0; i < mutatedX.genomeLength(); i++)
				System.out.print("," + mutatedX.genome[i]);
			System.out.println();

			p.evaluate(state,mutatedX, 0, 0,objectClus);
			((ec.Problem)p).finishEvaluating(state,0);

			double newF = mutatedX.fitness.fitness();
			// adequar para minimizar o fitness
			float delta = (float) (x.fitness.fitness() - newF);

			if (delta < 0 || rand.nextDouble() < Math.exp(- (delta) / temp_atual) ) {
				((IntegerVectorIndividual) x).setGenome(mutatedX.getGenome());
				x.evaluated = false;				
				((ec.Problem)p).prepareToEvaluate(state,0);
				p.evaluate(state,x, 0, 0,objectClus);
				((ec.Problem)p).finishEvaluating(state,0);
			}

			iter += 1;
			temp_atual = temp_atual * alpha;
		}
		return x;
	}

	public int[] mutate(int[] x, float scale) {
		Random rand = new Random();
		int randomQuantity = (int) ((int) rand.nextInt( (x.length) + 1 ) * scale);

		final int[] pos = new Random().ints(0, x.length).distinct().limit(randomQuantity).toArray(); // só funciona no Java 8

		int max = x[0];
		for (int i = 1; i < x.length; i++) {
			if (x[i] > max)
				max = x[i];
		}

		for (int i = 0; i < randomQuantity; i++) {
			int randomNum = (int) rand.nextInt(((int) (max * 0.25) == 0) ? 1 : (int) (max * 0.25) ) + 1; // int randomNum = rand.nextInt((max - min) + 1) + min;
			if (rand.nextInt(2) == 1) {
				randomNum = randomNum*(-1);
			}
			x[pos[i]] =  (x[pos[i]] + randomNum) % x.length;
			if (x[pos[i]] < 0)
				x[pos[i]] = x.length + x[pos[i]];  // tratamento para negativo
		}
		return x;
	}

}
