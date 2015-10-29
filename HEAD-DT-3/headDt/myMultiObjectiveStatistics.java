/*
  Copyright 2010 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package headDt;

import headDt.myMultiObjectiveFitness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ec.EvolutionState;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleProblemForm;
import ec.simple.SimpleStatistics;
import ec.util.*;

import java.io.*;

/* 
 * MultiObjectiveStatistics.java
 * 
 * Created: Thu Feb 04 2010
 * By: Faisal Abidi and Sean Luke
 *
 */

/*
 * MultiObjectiveStatistics are a SimpleStatistics subclass which overrides the finalStatistics
 * method to output the current Pareto Front in various ways:
 *
 * <ul>
 * <li><p>Every individual in the Pareto Front is written to the end of the statistics log.
 * <li><p>A summary of the objective values of the Pareto Front is written to stdout.
 * <li><p>The objective values of the Pareto Front are written in tabular form to a special
 * Pareto Front file specified with the parameters below.  This file can be easily read by
 * gnuplot or Excel etc. to display the Front (if it's 2D or perhaps 3D).
 * 
 * <p>
 * <b>Parameters</b><br>
 * <table>
 * <tr>
 * <td valign=top><i>base</i>.<tt>front</tt><br>
 * <font size=-1>String (a filename)</font></td>
 * <td valign=top>(The Pareto Front file, if any)</td>
 * </tr>
 * </table>
 */

public class myMultiObjectiveStatistics extends SimpleStatistics
{   
	/** front file parameter */
	public static final String P_PARETO_FRONT_FILE = "front";
	public static final String P_SILENT_FRONT_FILE = "silent.front";

	public boolean silentFront;

	/** The pareto front log */
	public int frontLog = 0;  // stdout by default

	public void setup(final EvolutionState state, final Parameter base)
	{
		super.setup(state,base);

		silentFront = state.parameters.getBoolean(base.push(P_SILENT), null, false);
		// yes, we're stating it a second time.  It's correct logic.
		silentFront = state.parameters.getBoolean(base.push(P_SILENT_FRONT_FILE), null, silentFront);

		File frontFile = state.parameters.getFile(base.push(P_PARETO_FRONT_FILE),null);

		if (silentFront)
		{
			frontLog = Output.NO_LOGS;
		}
		else if (frontFile!=null)
		{
			try
			{
				frontLog = state.output.addLog(frontFile, !compress, compress);
			}
			catch (IOException i)
			{
				state.output.fatal("An IOException occurred while trying to create the log " + frontFile + ":\n" + i);
			}
		}
		else state.output.warning("No Pareto Front statistics file specified, printing to stdout at end.", base.push(P_PARETO_FRONT_FILE));
	}



	/** Logs the best individual of the run. */
	public void finalStatistics(final EvolutionState state, final int result)
	{
		bypassFinalStatistics(state, result);  // just call super.super.finalStatistics(...)

		ArrayList frontAll = new ArrayList();
		if (doFinal) state.output.println("\n\n\n PARETO FRONTS", statisticslog);
		for (int s = 0; s < state.population.subpops.length; s++)
		{
			MultiObjectiveFitness typicalFitness = (MultiObjectiveFitness)(state.population.subpops[s].individuals[0].fitness);
			if (doFinal) state.output.println("\n\nPareto Front of Subpopulation " + s, statisticslog);

			// build front
			ArrayList front = typicalFitness.partitionIntoParetoFront(state.population.subpops[s].individuals, null, null);
			frontAll.addAll(front);

			// sort by objective[0]
			Object[] sortedFront = front.toArray();
			QuickSort.qsort(sortedFront, new SortComparator()
			{
				public boolean lt(Object a, Object b)
				{
					return (((MultiObjectiveFitness) (((Individual) a).fitness)).getObjective(0) < 
							(((MultiObjectiveFitness) ((Individual) b).fitness)).getObjective(0));
				}

				public boolean gt(Object a, Object b)
				{
					return (((MultiObjectiveFitness) (((Individual) a).fitness)).getObjective(0) > 
					((MultiObjectiveFitness) (((Individual) b).fitness)).getObjective(0));
				}
			});

			// print out front to statistics log
			if (doFinal)
				for (int i = 0; i < sortedFront.length; i++)
					((Individual)(sortedFront[i])).printIndividualForHumans(state, statisticslog);

			// write short version of front out to disk
			if (!silentFront)
			{
				if (state.population.subpops.length > 1)
					state.output.println("Subpopulation " + s, frontLog);
				for (int i = 0; i < sortedFront.length; i++)
				{
					Individual ind = (Individual)(sortedFront[i]);
					MultiObjectiveFitness mof = (MultiObjectiveFitness) (ind.fitness);
					double[] objectives = mof.getObjectives();

					String line = "";
					for (int f = 0; f < objectives.length; f++)
						line += (objectives[f] + " ");
					state.output.println(line, frontLog);
				}
			}
		}





		if (state.evaluator.p_problem instanceof SimpleProblemForm) {
			/*
			for (int i = 0; i < frontAll.size(); i++) {
				Individual inda = (Individual) frontAll.get(i);
				for (int j = 0; j < frontAll.size(); j++) {
					Individual indb = (Individual) frontAll.get(j);
					int comp = compare(inda,indb);
					if (comp < 0)
						inda.measures.wins++;
					else if (comp > 0)
						indb.measures.wins++;
				}
			}
			 */


			if (Main.multiObjectiveType == 2) { // Pareto

				((myMultiObjectiveFitness)((Individual) frontAll.get(0)).fitness).measures.getFtb();

				int maxFtb = ((myMultiObjectiveFitness)((Individual) frontAll.get(0)).fitness).measures.getFtb();
				int iMaxFtb = 0;
				for (int i = 1; i < frontAll.size(); i++) {				
					int ftb = ((myMultiObjectiveFitness)((Individual) frontAll.get(i)).fitness).measures.getFtb();
					if (ftb > maxFtb) {
						maxFtb = ftb;
						iMaxFtb = i;
					}
				}

				((SimpleProblemForm)(state.evaluator.p_problem.clone())).describe(state, (Individual)frontAll.get(iMaxFtb), 0, 0, statisticslog);				
			}
			else if (Main.multiObjectiveType == 4) { // GridBased

				//((myMultiGridBasedFitness)((Individual) frontAll.get(0)).fitness)  .measures.getFtb();
				
				sortObjective1(frontAll);
				
				//System.out.println("Front Size = "+frontAll.size());
				double first = ((MultiObjectiveFitness) (((Individual) frontAll.get(0)).fitness)).getObjective(0);
				double last = ((MultiObjectiveFitness) (((Individual) frontAll.get(frontAll.size()-1)).fitness)).getObjective(0);
				
				//if (first < last)
					//	System.out.println("Ordenou errado - myMultiObjectiveStatistics");
				// 	System.out.println("ENTROU NO DESCRIBE - myMultiObjectiveStatistics");
				((SimpleProblemForm)(state.evaluator.p_problem.clone())).describe(state, (Individual)frontAll.get(0), 0, 0, statisticslog);				
			}

		}
	}
	
	private void sortObjective1(ArrayList<Individual> individualList) {
		Collections.sort(individualList, new Comparator<Individual>() {
			
			public int compare(Individual d1, Individual d2) {
				if (((MultiObjectiveFitness) (((Individual) d1).fitness)).getObjective(0) < ((MultiObjectiveFitness) (((Individual) d1).fitness)).getObjective(0)) {
					return 1;
				}
				else if (((MultiObjectiveFitness) (((Individual) d1).fitness)).getObjective(0) > ((MultiObjectiveFitness) (((Individual) d1).fitness)).getObjective(0)) {
					return -1;
				}
				else
					return 0;
			}
		});
	}



	public int comparePareto(Individual ind1, Individual ind2) {
		int deltaFtb =  ((myMultiObjectiveFitness)ind1.fitness).measures.getFtb() - ((myMultiObjectiveFitness)ind2.fitness).measures.getFtb();
		double ValF = ((myMultiObjectiveFitness)ind1.fitness).measures.fMeasure[1] - ((myMultiObjectiveFitness)ind2.fitness).measures.fMeasure[1];
		double TreeSize = ((myMultiObjectiveFitness)ind1.fitness).measures.getTreeSize() - ((myMultiObjectiveFitness)ind2.fitness).measures.getTreeSize();
		//double TreeSize = ind1.treeSize - ind2.treeSize;

		if (Math.abs(deltaFtb) > 0) {
			if (deltaFtb > 0)
				return -1;
			else
				return 1;
		}
		else if (Math.abs(ValF) > 0) {
			if (ValF > 0)
				return -1;
			else
				return 1;
		}
		else if (Math.abs(TreeSize) > 0) {
			if (TreeSize > 0)
				return 1;
			else
				return -1;
		}
		else
			return 1;
	}


	public int compareLexico(Individual ind1, Individual ind2) {
		double ValF = ((myMultiObjectiveFitness)ind1.fitness).measures.fMeasure[1] - ((myMultiObjectiveFitness)ind2.fitness).measures.fMeasure[1];
		double TreeSize = ((myMultiObjectiveFitness)ind1.fitness).measures.getTreeSize() - ((myMultiObjectiveFitness)ind2.fitness).measures.getTreeSize();
		//double TreeSize = ind1.treeSize - ind2.treeSize;
		if (Math.abs(ValF) > 0.02) {
			if (ValF > 0)
				return -1;
			else
				return 1;
		}
		else if (Math.abs(TreeSize) > 2) {
			if (TreeSize > 0)
				return 1;
			else
				return -1;
		}
		else if (ValF > 0)
			return -1;
		else if (ValF < 0)
			return 1;
		else if (TreeSize < 0)
			return -1;
		else if (TreeSize > 0)
			return 1;
		else
			return 0;
	}
}
