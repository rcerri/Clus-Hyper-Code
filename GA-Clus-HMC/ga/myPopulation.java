/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */


package ga;
import ec.Population;
import ec.vector.IntegerVectorIndividual;



public class myPopulation extends Population {
	
	public void restart() {
		
		int size = ((IntegerVectorIndividual)this.subpops[0].individuals[0]).genome.length;
		
		for(int x=0; x < this.subpops.length; x++) {
			for(int y=0;y< this.subpops[x].individuals.length;y++) {
				int numberOfGroups = (int) (1 + Math.random() * size);
				//System.out.println("groups = "+numberOfGroups);
				for (int i = 0; i < size; i++) {
					((IntegerVectorIndividual)this.subpops[x].individuals[y]).genome[i] = (int) (1 + Math.random() * numberOfGroups);
				}
			}
		}

		if (Main.initialPopBaselines == 1) { // inserting individuals [1...1] and [1 2 3 ... ntargets]
			for (int i = 0; i < size; i++) {
				((IntegerVectorIndividual)this.subpops[0].individuals[0]).genome[i] = 1;
				((IntegerVectorIndividual)this.subpops[0].individuals[1]).genome[i] = i+1;
			}
		}

	}



}
