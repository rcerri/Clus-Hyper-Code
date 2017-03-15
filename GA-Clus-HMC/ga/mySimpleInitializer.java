package ga;

import Util.ClusWrapperNonStatic;
import ec.EvolutionState;
import ec.Population;
import ec.simple.SimpleInitializer;
import ec.util.Parameter;
import ec.vector.IntegerVectorIndividual;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/* 
 * SimpleInitializer.java
 * 
 * Created: Tue Aug 10 21:07:42 1999
 * By: Sean Luke
 */

/**
 * SimpleInitializer is a default Initializer which initializes a Population
 * by calling the Population's populate(...) method.  For most applications,
 * this should suffice.
 *
 * @author Sean Luke
 * @version 1.0 
 */

public class mySimpleInitializer extends SimpleInitializer
{
	public void setup(final EvolutionState state, final Parameter base) { 
	}

	public long fatorial(long x) {
		long fat = x;
		for (long i = (x-1); i > 1; i--)
			fat *= i;
		return fat;
	}

	/*
	public long generateIntervals(int size, long fats[]) {
		long numberOfGroups = 0;
		long[] intervals = new long[size];
		// comeca no 
		intervals[0] = 0; intervals[1] = 0;
		long soma = 0;
		for (int i = 2; i < size; i++) {
			//int g = i+1;
			long comb = fats[size] / fats[i]*fats[size-i];
			soma += comb;
			intervals[i] = intervals[i-1] + comb;
			//c n,p = n! / p! (n-p)!;
		}

		numberOfGroups = (long) (1 + Math.random() * soma);
		return numberOfGroups; // not working yet, incompleted		
	} */

	/** Creates, populates, and returns a new population by making a new
        population, calling setup(...) on it, and calling populate(...)
        on it, assuming an unthreaded environment (thread 0).
        Obviously, this is an expensive method.  It should only
        be called once typically in a run. */

	public Population initialPopulation(final EvolutionState state, int thread, ClusWrapperNonStatic object) {
		Population p = setupPopulation(state, thread, object); 
		p.populate(state, thread);

		/*
		long fats[] = new long[size+1];
		fats[0] = 0;
		fats[1] = 1;
		for (int i = 2; i <= size; i++) {
			fats[i] = fatorial(i);
			//System.out.println("fats["+i+"] = "+fats[i]);
		}

		long[] intervals = new long[size];
		intervals[0] = 0; intervals[1] = 0;
		long soma = 0;
		for (int i = 2; i < size; i++) {
			System.out.println("i = "+i+"  -  Size = "+size);
			long comb = fats[size] / (fats[i]*fats[size-i]);
			soma += comb;
			intervals[i] = intervals[i-1] + comb;
			//c n,p = n! / p! (n-p)!;
		}
		 */

		int size = ((IntegerVectorIndividual)p.subpops[0].individuals[0]).genome.length;

		for(int x=0; x < p.subpops.length; x++) {
			for(int y=0;y< p.subpops[x].individuals.length;y++) {
				long numberOfGroups;

				/*
				// I will set = 1 (always)
				if (Main.initialPopBaselines == 1) {
					numberOfGroups = (int) (2 + Math.random() * (size - 2));
				}
				else {
					numberOfGroups = (int) (1 + Math.random() * size);	
				}
				*/
				
				// check later how it works
				//Random r = new Random();
				//double mySample = r.nextGaussian()*desiredStandardDeviation+desiredMean;
				
				numberOfGroups = (int) (Math.random() * (size/3) +  Math.random() * (size/3) +  Math.random() * (size/3)); 

				if (numberOfGroups < 2) {
					System.out.println("Number of groups < 2  ---> 2");
					numberOfGroups = 2;
				}
				else if (numberOfGroups > size){
					System.out.println("Number of groups > "+size+" ---> "+size);
					numberOfGroups = size;
				}

				/*
				numberOfGroups = (long) (1 + Math.random() * soma);
				int i = 2;
				while (numberOfGroups > intervals[i])
					i++;
				numberOfGroups = i;
				 */

				Set<Integer> genes = new HashSet<Integer>();
				for (int i = 0; i < size; i++)
					genes.add(i);

				//System.out.println("genes.size = "+genes.size());

				while (!genes.isEmpty()) {
					for (int i = 2; i <= numberOfGroups && !genes.isEmpty(); i++) {
						int gene = (int) (Math.random() * genes.size());

						int o = 0;
						for (Integer obj : genes) {
							if (o == gene)
								gene = obj;
							o++;
						}

						((IntegerVectorIndividual)p.subpops[x].individuals[y]).genome[gene] = i;
						genes.remove(gene);

						//System.out.println("genes = "+gene);
					}
				}


				//System.out.println("groups = "+numberOfGroups);

				// printing the initial population
				//System.out.println("groups = "+numberOfGroups);

				//System.out.print("Genome = ["+((IntegerVectorIndividual)p.subpops[x].individuals[y]).genome[0]);
				// for (i = 0; i < size; i++) {
				//	((IntegerVectorIndividual)p.subpops[x].individuals[y]).genome[i] = (int) (1 + Math.random() * numberOfGroups);
				//	 System.out.print(","+((IntegerVectorIndividual)p.subpops[x].individuals[y]).genome[i]);
				//}
				//System.out.println("]");

			}
		}

		if (Main.initialPopBaselines == 1) { // inserting individuals [1...1] and [1 2 3 ... ntargets]
			for (int i = 0; i < size; i++) {
				//System.out.println("TESTE");
				((IntegerVectorIndividual)p.subpops[0].individuals[0]).genome[i] = 1;
				((IntegerVectorIndividual)p.subpops[0].individuals[1]).genome[i] = i+1;
			}
		}

		for(int x=0; x < p.subpops.length; x++) {
			for(int y=0;y< p.subpops[x].individuals.length;y++) {
				System.out.print("Genome = ["+((IntegerVectorIndividual)p.subpops[x].individuals[y]).genome[0]);
				for (int i = 1; i < size; i++)
					System.out.print(","+((IntegerVectorIndividual)p.subpops[x].individuals[y]).genome[i]);
				System.out.println("]");
			}
		}

		return p;
	}



	public Population setupPopulation(final EvolutionState state, int thread, ClusWrapperNonStatic object)
	{
		Parameter base = new Parameter(P_POP);
		Population p = (Population) state.parameters.getInstanceForParameterEq(base,null,Population.class);  // Population.class is fine
		p.setup(state,base, object);
		return p;
	}
}
