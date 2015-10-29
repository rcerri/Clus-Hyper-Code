package headDt;

import ec.Fitness;
import ec.multiobjective.MultiObjectiveFitness;

public class myMultiObjectiveFitness extends MultiObjectiveFitness {

	public myMeasures measures = new myMeasures();

	/*	public myMultiObjectiveFitness() {
		//super.clone();
		measures = new myMeasures();
	}

	 */

public boolean betterThan(Fitness fitness) {
		if (this.paretoDominates((MultiObjectiveFitness)fitness)) {
			//System.out.print("Individuo A domina B, embora B domine mais indivíduos que A");
			return true;
		}
		else if (((myMultiObjectiveFitness)fitness).paretoDominates(this)) {
			//System.out.print("Individuo B domina A, embora A domine mais indivíduos que B");
			return false;
		}
		else if (this.measures.getFtb() > ((myMultiObjectiveFitness)fitness).measures.getFtb()) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean betterThanAnterior(Fitness fitness) {
		
		if (this.paretoDominates((MultiObjectiveFitness)fitness)  && (this.measures.getFtb() <  ((myMultiObjectiveFitness)fitness).measures.getFtb())) {
			System.out.print("Individuo A domina B, embora B domine mais indivíduos que A");	
		}
		
		if ( ((myMultiObjectiveFitness)fitness).paretoDominates(this) && (this.measures.getFtb() > ((myMultiObjectiveFitness)fitness).measures.getFtb())) {
			System.out.print("Individuo B domina A, embora A domine mais indivíduos que B");	
		}
		

		if (this.measures.getFtb() > ((myMultiObjectiveFitness)fitness).measures.getFtb()) {
			return true;
		}
		else {
			return false;
		}
	}
}

