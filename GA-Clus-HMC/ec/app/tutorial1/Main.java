package ec.app.tutorial1;

import java.io.File;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class Main {

	public static void main(String[] args) throws Exception {
		//String string[] = {"-file","src/ec/app/tutorial1/tutorial1.params", "-p","jobs=1"};
		//File file = new File("src/ec/app/tutorial1/tutorial1.params");		

		final File pFile = new File("src/ec/app/tutorial1/tutorial1.params");
		// Do stuff to the parameters here 
		final ParameterDatabase parameters = new ParameterDatabase(pFile);
		
		Parameter params[] = new Parameter[4];
		params[0] = new Parameter("pop.subpop.0.species.genome-size");
		params[1] = new Parameter("jobs");
		params[2] = new Parameter("pop.subpop.0.species.min-gene");
		params[3] = new Parameter("pop.subpop.0.species.max-gene");
	
		String[] values = {"10", "2", "1", "37"}; 
		
		for (int i = 0; i < params.length; i++) {
			parameters.set(params[i],values[i]);
		}
		
		//final EvolutionState state = Evolve.initialize(parameters, 1);
		// Do stuff to the state variables here
		//state.run(EvolutionState.C_STARTED_FRESH);
		
		myEvolve.main(parameters);

	}
}


