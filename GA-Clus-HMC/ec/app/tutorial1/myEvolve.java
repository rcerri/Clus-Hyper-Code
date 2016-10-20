package ec.app.tutorial1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ec.EvolutionState;
import ec.Evolve;
import ec.util.Output;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class myEvolve extends Evolve{


	public static Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public static void main(ParameterDatabase parameters) {
		EvolutionState state;
		//ParameterDatabase parameters;
		
		ParameterDatabase original = (ParameterDatabase) deepClone(parameters);

		int currentJob = 0;                             // the next job number (0 by default)
		//parameters = loadParameterDatabase(args);
		
		if (currentJob == 0)  // no current job number yet
			currentJob = parameters.getIntWithDefault(new Parameter("current-job"), null, 0);
		if (currentJob < 0)
			Output.initialError("The 'current-job' parameter must be >= 0 (or not exist, which defaults to 0)");

		int numJobs = parameters.getIntWithDefault(new Parameter("jobs"), null, 1);
		if (numJobs < 1)
			Output.initialError("The 'jobs' parameter must be >= 1 (or not exist, which defaults to 1)");


		// Now we know how many jobs remain.  Let's loop for that many jobs.  Each time we'll
		// load the parameter database scratch (except the first time where we reuse the one we
		// just loaded a second ago).  The reason we reload from scratch each time is that the
		// experimenter is free to scribble all over the parameter database and it'd be nice to
		// have everything fresh and clean.  It doesn't take long to load the database anyway,
		// it's usually small.
		for(int job = currentJob ; job < numJobs; job++)
		{
			// We used to have a try/catch here to catch errors thrown by this job and continue to the next.
			// But the most common error is an OutOfMemoryException, and printing its stack trace would
			// just create another OutOfMemoryException!  Which dies anyway and has a worthless stack
			// trace as a result.

			// try
			{
				// load the parameter database (reusing the very first if it exists)
				 if (parameters == null)
					 parameters = (ParameterDatabase) deepClone(original);
				//	   parameters = loadParameterDatabase(args);

				// Initialize the EvolutionState, then set its job variables
				state = initialize(parameters, job);                // pass in job# as the seed increment
				state.output.systemMessage("Job: " + job);
				state.job = new Object[1];                                  // make the job argument storage
				state.job[0] = Integer.valueOf(job);                    // stick the current job in our job storage
				//state.runtimeArguments = args;                              // stick the runtime arguments in our storage
				if (numJobs > 1)                                                    // only if iterating (so we can be backwards-compatible),
				{
					String jobFilePrefix = "job." + job + ".";
					state.output.setFilePrefix(jobFilePrefix);     // add a prefix for checkpoint/output files 
					state.checkpointPrefix = jobFilePrefix + state.checkpointPrefix;  // also set up checkpoint prefix
				}

				// Here you can set up the EvolutionState's parameters further before it's setup(...).
				// This includes replacing the random number generators, changing values in state.parameters,
				// changing instance variables (except for job and runtimeArguments, please), etc.


				// now we let it go
				state.run(EvolutionState.C_STARTED_FRESH);
				cleanup(state);  // flush and close various streams, print out parameters if necessary
				parameters = null;  // so we load a fresh database next time around
			}

		}

		System.exit(0);
	}
} 
