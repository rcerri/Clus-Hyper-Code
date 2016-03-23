package eda;

import ec.Fitness;
import ec.simple.SimpleFitness;

public class MyFitness extends SimpleFitness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	   public boolean betterThan(final Fitness _fitness)
       {
	   if(EDAMain.criterion.equals("SS")) //maximize fitness
		   return ((SimpleFitness)_fitness).fitness() < fitness();
	   else //minimize fitness
		   return ((SimpleFitness)_fitness).fitness() > fitness();
       }

}
