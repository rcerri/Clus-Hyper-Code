package ga;

import Util.ClusWrapperNonStatic;
import ec.EvolutionState;
import ec.Population;
import ec.simple.SimpleInitializer;
import ec.util.Parameter;
import ec.vector.IntegerVectorIndividual;

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

	/** Creates, populates, and returns a new population by making a new
        population, calling setup(...) on it, and calling populate(...)
        on it, assuming an unthreaded environment (thread 0).
        Obviously, this is an expensive method.  It should only
        be called once typically in a run. */

	public Population initialPopulation(final EvolutionState state, int thread, ClusWrapperNonStatic object) {
		Population p = setupPopulation(state, thread, object); 
		p.populate(state, thread);
		
		int size = ((IntegerVectorIndividual)p.subpops[0].individuals[0]).genome.length;
		
		for(int x=0; x < p.subpops.length; x++) {
			for(int y=0;y< p.subpops[x].individuals.length;y++) {
				int numberOfGroups = (int) (1 + Math.random() * size);
				//System.out.println("groups = "+numberOfGroups);
				for (int i = 0; i < size; i++) {
					((IntegerVectorIndividual)p.subpops[x].individuals[y]).genome[i] = (int) (1 + Math.random() * numberOfGroups);
				}
			}
		}

		if (Main.initialPopBaselines == 1) { // inserting individuals [1...1] and [1 2 3 ... ntargets]
			for (int i = 0; i < size; i++) {
				((IntegerVectorIndividual)p.subpops[0].individuals[0]).genome[i] = 1;
				((IntegerVectorIndividual)p.subpops[0].individuals[1]).genome[i] = i+1;
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
