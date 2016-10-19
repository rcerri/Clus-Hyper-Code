package eda;

import java.util.ArrayList;

import weka.core.DenseInstance;
import weka.core.Instance;

public class Cluster {
	
	private ArrayList<Integer> members;
	private Instance centroid;
	
	public Cluster(){
		setMembers(new ArrayList<Integer>());
	}

	public ArrayList<Integer> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<Integer> members) {
		this.members = members;
	}

	public Instance getCentroid() {
		return centroid;
	}

	public void setCentroid(Instance centroid) {
		this.centroid = new DenseInstance(centroid);
	}

}
