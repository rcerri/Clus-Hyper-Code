package ga;

import ec.*;
import ec.simple.SimpleProblemForm;
import ec.simple.SimpleStatistics;
import ec.steadystate.*;
import java.io.IOException;
import ec.util.*;
import java.io.File;

/* 
 * SimpleStatistics.java
 * 
 * Created: Tue Aug 10 21:10:48 1999
 * By: Sean Luke
 */

/**
 * A basic Statistics class suitable for simple problem applications.
 *
 * SimpleStatistics prints out the best individual, per subpopulation,
 * each generation.  At the end of a run, it also prints out the best
 * individual of the run.  SimpleStatistics outputs this data to a log
 * which may either be a provided file or stdout.  Compressed files will
 * be overridden on restart from checkpoint; uncompressed files will be 
 * appended on restart.
 *
 * <p>SimpleStatistics implements a simple version of steady-state statistics:
 * if it quits before a generation boundary,
 * it will include the best individual discovered, even if the individual was discovered
 * after the last boundary.  This is done by using individualsEvaluatedStatistics(...)
 * to update best-individual-of-generation in addition to doing it in
 * postEvaluationStatistics(...).

 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><i>base.</i><tt>gzip</tt><br>
 <font size=-1>boolean</font></td>
 <td valign=top>(whether or not to compress the file (.gz suffix added)</td></tr>
 <tr><td valign=top><i>base.</i><tt>file</tt><br>
 <font size=-1>String (a filename), or nonexistant (signifies stdout)</font></td>
 <td valign=top>(the log for statistics)</td></tr>
 </table>

 *
 * @author Sean Luke
 * @version 1.0 
 */

public class myStatistics extends SimpleStatistics
{
	/** Logs the best individual of the generation. */
	boolean warned = false;

	@Override
	public void postEvaluationStatistics(final EvolutionState state) {
		//super.postEvaluationStatistics(state);
		// for now we just print the best fitness per subpopulation.
		Individual[] best_i = new Individual[state.population.subpops.length];  // quiets compiler complaints
		for(int x=0;x<state.population.subpops.length;x++) {
			best_i[x] = state.population.subpops[x].individuals[0];
			for(int y=1;y<state.population.subpops[x].individuals.length;y++) {
				if (state.population.subpops[x].individuals[y] == null) {
					if (!warned) {
						state.output.warnOnce("Null individuals found in subpopulation");
						warned = true;  // we do this rather than relying on warnOnce because it is much faster in a tight loop
					}
				}
				else if (best_i[x] == null || state.population.subpops[x].individuals[y].fitness.betterThan(best_i[x].fitness))
					best_i[x] = state.population.subpops[x].individuals[y];
				if (best_i[x] == null) {
					if (!warned)
					{
						state.output.warnOnce("Null individuals found in subpopulation");
						warned = true;  // we do this rather than relying on warnOnce because it is much faster in a tight loop
					}
				}
			}

			// now test to see if it's the new best_of_run
			if (best_of_run[x]==null || best_i[x].fitness.betterThan(best_of_run[x].fitness))
				best_of_run[x] = (Individual)(best_i[x].clone());
		
			((mySimpleEvolutionState)state).best_g[state.generation] = best_i[0]; // 0: only one pop
		}

		

		// print the best-of-generation individual
		if (doGeneration) state.output.println("\nGeneration: " + state.generation,statisticslog);
		if (doGeneration) state.output.println("Best Individual:",statisticslog);
		for(int x=0;x<state.population.subpops.length;x++) {
			if (doGeneration) state.output.println("Subpopulation " + x + ":",statisticslog);
			if (doGeneration) best_i[x].printIndividualForHumans(state,statisticslog);

			if (doGeneration) {
				Main.pEvolution.println(state.generation +","+ best_i[x].fitness.fitness());
			}

			if (doMessage && !silentPrint) state.output.message("Subpop " + x + " best fitness of generation" + 
					(best_i[x].evaluated ? " " : " (evaluated flag not set): ") +
					best_i[x].fitness.fitnessToStringForHumans());

			// describe the winner if there is a description
			if (doGeneration && doPerGenerationDescription) 
			{
				if (state.evaluator.p_problem instanceof SimpleProblemForm)
					((SimpleProblemForm)(state.evaluator.p_problem.clone())).describe(state, best_i[x], x, 0, statisticslog);   
			}   
		}
	}
}
