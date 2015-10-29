
package headDt;


import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleEvaluator;
import ec.simple.SimpleFitness;


public class myLexicoEvaluator extends SimpleEvaluator{


	public void evaluatePopulation(final EvolutionState state) 
	{ 
		super.evaluatePopulation(state);
		int maxTreeSize = 0;

		//System.out.println("TESTE LEXICO myEvaluator!!!!");
		// IMPORTANTE: Tens que desabilitar o elitismo. Por que? Pense sobre isso!
		// Abordagem Lexicográfica!
		for(int x = 0;x<state.population.subpops.length;x++) {
			for(int y = 0;y<state.population.subpops[x].individuals.length;y++) {
				Individual inda =  state.population.subpops[x].individuals[y];
				for(int w = 0; w < state.population.subpops.length; w++) {
					for(int z = 0; z < state.population.subpops[w].individuals.length; z++) {
						// numero de WINS!!
						Individual indb =  state.population.subpops[w].individuals[z];
						int comp = compare(inda,indb);
						//System.out.println(count+",");
						if (comp < 0)
							((myLexicoFitness)inda.fitness).measures.wins++; 
							//inda.measures.wins++;
						else if (comp > 0)
							((myLexicoFitness)indb.fitness).measures.wins++;
							//indb.measures.wins++;
					}
				}
				//System.out.println("Ind Wins = "+inda.measures.wins);
				//((SimpleFitness)inda.fitness).setFitness(state, ((float)inda.measures.wins), false);
				((myLexicoFitness)inda.fitness).setFitness(state, ((float)((myLexicoFitness)inda.fitness).measures.wins), false); 
				//((SimpleFitness)inda.fitness).setFitness(state, ((float)((myLexicoFitness)inda.fitness).measures.wins), false);	
			}
		}

	}


	public int compare(Individual ind1, Individual ind2) {


		double ValF = ((myLexicoFitness)ind1.fitness).measures.getfMeasure()[1] - ((myLexicoFitness)ind2.fitness).measures.getfMeasure()[1];
		//double ValF = ind1.measures.getfMeasure()[1] - ind2.measures.getfMeasure()[1];
		
		//double TrainF = ind1.measures.getfMeasure()[0] - ind2.measures.getfMeasure()[0];
		double TrainF = ((myLexicoFitness)ind1.fitness).measures.getfMeasure()[0] - ((myLexicoFitness)ind2.fitness).measures.getfMeasure()[0];
		
		double ValBalance = ((myLexicoFitness)ind1.fitness).measures.getBalance()[1] - ((myLexicoFitness)ind2.fitness).measures.getBalance()[1];		
		double TrainBalance = ((myLexicoFitness)ind1.fitness).measures.getBalance()[0] - ((myLexicoFitness)ind2.fitness).measures.getBalance()[0];
		
		//double ValBalance = ind1.measures.getBalance()[1] - ind2.measures.getBalance()[1];
		//double TrainBalance = ind1.measures.getBalance()[0] - ind2.measures.getBalance()[0];

		double ValGmean = ((myLexicoFitness)ind1.fitness).measures.getGmean()[1] - ((myLexicoFitness)ind2.fitness).measures.getGmean()[1];
		double TrainGmean = ((myLexicoFitness)ind1.fitness).measures.getGmean()[0] - ((myLexicoFitness)ind2.fitness).measures.getGmean()[0];
		//double ValGmean = ind1.measures.getGmean()[1] - ind2.measures.getGmean()[1];
		
		double TreeSize = ((myLexicoFitness)ind1.fitness).measures.getTreeSize() - ((myLexicoFitness)ind2.fitness).measures.getTreeSize();
		//double TreeSize = ind1.measures.getTreeSize() - ind2.measures.getTreeSize();
		
		
		double avgClassAcc = ((myLexicoFitness)ind1.fitness).measures.getAvgClassAcc()[1] - ((myLexicoFitness)ind2.fitness).measures.getAvgClassAcc()[1];
		

		double obj1 = (ValF + TrainF)/2;
		double obj2 = (ValBalance + TrainBalance)/2;

		if (Math.abs(obj1) > 0.02) {
			if (obj1 > 0)
				return -1;
			else
				return 1;
		}
		else if (Math.abs(obj2) > 0.02) {
			if (obj2 > 0)
				return -1;
			else
				return 1;
		}
		else if (obj1 > 0)
			return -1;
		else if (obj1 < 0)
			return 1;

		else if (obj2 > 0)
			return -1;
		else if (obj2 < 0)
			return 1;

		else
			return 0;
	}
}


