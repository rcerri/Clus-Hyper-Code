/*************************************************************************
 * Clus - Software for Predictive Clustering                             *
 * Copyright (C) 2007                                                    *
 *    Katholieke Universiteit Leuven, Leuven, Belgium                    *
 *    Jozef Stefan Institute, Ljubljana, Slovenia                        *
 *                                                                       *
 * This program is free software: you can redistribute it and/or modify  *
 * it under the terms of the GNU General Public License as published by  *
 * the Free Software Foundation, either version 3 of the License, or     *
 * (at your option) any later version.                                   *
 *                                                                       *
 * This program is distributed in the hope that it will be useful,       *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 * GNU General Public License for more details.                          *
 *                                                                       *
 * You should have received a copy of the GNU General Public License     *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 *                                                                       *
 * Contact information: <http://www.cs.kuleuven.be/~dtai/clus/>.         *
 *************************************************************************/

package clus.error;

import java.io.*;
import jeans.util.*;

import clus.data.rows.DataTuple;
import clus.data.type.*;
import clus.main.*;
import clus.statistic.ClusStatistic;
import clus.util.*;

public class ContingencyTable extends ClusNominalError {

	public final static long serialVersionUID = Settings.SERIAL_VERSION_ID;

	protected final static String REAL_PRED = "REAL\\PRED";

	protected int[][][] m_ContTable;

	public ContingencyTable(ClusErrorList par, NominalAttrType[] nom) {
		super(par, nom);
		m_ContTable = new int[m_Dim][][];
		for (int i = 0; i < m_Dim; i++) {
			// also add a column for "?" for the semi-supervised setting
			int size = m_Attrs[i].getNbValuesInclMissing();
			m_ContTable[i] = new int[size][size];
		}
	}

	public boolean isMultiLine() {
		return true;
	}
	
	public int calcNbTotal(int k) {
		int sum = 0;
		int size = m_Attrs[k].getNbValues();
		int[][] table = m_ContTable[k];
		for (int i = 0; i < size; i++) {		
			for (int j = 0; j < size; j++) {
				sum += table[i][j];
			}
		}
		return sum;
	}

	public int calcNbCorrect(int k) {
		int sum = 0;
		int size = m_Attrs[k].getNbValues();
		int[][] table = m_ContTable[k];
		for (int j = 0; j < size; j++) {
			sum += table[j][j];
		}
		return sum;
	}	

	public double calcXSquare(int k) {
		int size = m_Attrs[k].getNbValues();
		int[] ri = new int[size];
		int[] cj = new int[size];
		for (int j = 0; j < size; j++) {
			ri[j] = sumRow(k, j);
			cj[j] = sumColumn(k, j);
		}
		double xsquare = 0.0;
		int nb = getNbExamples();
		int[][] table = m_ContTable[k];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				double eij = (double)ri[i]*cj[j]/nb;
				double err = (double)table[i][j] - eij;
				if (err != 0.0)	xsquare += err*err/eij;
			}
		}
		return xsquare;
	}

	public double calcCramerV(int k) {
		int q = m_Attrs[k].getNbValues();
		int n = calcNbTotal(k);
		double div = (double)n*(q-1);
		return Math.sqrt(calcXSquare(k)/div);
	}

	public double calcAccuracy(int k) {
		return (double)calcNbCorrect(k)/calcNbTotal(k);
	}

	public double calcDefaultAccuracy(int i) {
		return 0.0;
	}

	public double calcF1(int k) {
		double prec = calcPrecision(k);
		double rec = calcRecall(k);
		return (double) (2.0*prec*rec) / (prec+rec);
	}
	
	// assuming that the contingency table has dimension 2!!
	public double calcPrecision(int k) {
		int[][] table = m_ContTable[k];
		return (double) table[0][0]/(table[0][0]+table[1][0]);
	}
	
	// assuming that the contingency table has dimension 2!!
	public double calcRecall(int k) {
		int[][] table = m_ContTable[k];
		return (double) table[0][0]/(table[0][0]+table[0][1]);
	}
	
	public double getModelErrorComponent(int i) {
		return calcAccuracy(i);
	}
	
	public double getModelComponent() {
		double sum = 0.0;
		for (int i = 0; i < m_Dim; i++) {
			sum += calcAccuracy(i);
		}
		return sum/m_Dim;
	}
	
	public void showAccuracy(PrintWriter out, int i) {
		int nbcorr = calcNbCorrect(i);
		int nbtot = calcNbTotal(i);
		double acc = (double)nbcorr/nbtot;
		out.print("Accuracy: "+ClusFormat.SIX_AFTER_DOT.format(acc));
		//+" = "+nbcorr+"/"+nbtot);
		out.println();
	}
	
	public String showAccuracy( int i) {
		String out="";
		int nbcorr = calcNbCorrect(i);
		int nbtot = calcNbTotal(i);
		double acc = (double)nbcorr/nbtot;
		out+="Accuracy: "+ClusFormat.SIX_AFTER_DOT.format(acc);
		//+" = "+nbcorr+"/"+nbtot);
		out+="\n";
		return out;
	}
	
	public void add(ClusError other) {
		ContingencyTable cont = (ContingencyTable)other;
		for (int i = 0; i < m_Dim; i++) {
			int t1[][] = m_ContTable[i];
			int t2[][] = cont.m_ContTable[i];
			int size = t1.length;
			for (int j = 0; j < size; j++) {
				for (int k = 0; k < size; k++) {
					t1[j][k] += t2[j][k];
				}
			}
		}
	}
	
	// original version
 	/*public void showModelError(PrintWriter out, int detail) {
	 if (detail == DETAIL_VERY_SMALL) {
	 out.print(getPrefix()+"[");
	 for (int i = 0; i < m_Dim; i++) {
	 if (i != 0) out.print(",");
	 double acc = calcAccuracy(i);
	 out.print(ClusFormat.SIX_AFTER_DOT.format(acc));
	 }
	 out.println("]");
	 } else {
	 for (int i = 0; i < m_Dim; i++) {
	 out.println();
	 out.println(getPrefix()+"Attribute: "+m_Attrs[i].getName());
	 }
	 }
	 }*/
	
	
	
	// Celine adapted to work with ClusWrapper
	public void showModelError(PrintWriter out, int detail) {
		//if (detail == DETAIL_VERY_SMALL) {
		if (true) {
			out.print(getPrefix()+"[");
			double avgacc = 0.0;
			for (int i = 0; i < m_Dim; i++) {
				if (i != 0) out.print(",");
				double acc = calcAccuracy(i);
				avgacc += acc;
				out.print(ClusFormat.SIX_AFTER_DOT.format(acc));
			}
			//out.println("]");
			out.print("]: ");
			avgacc = avgacc / m_Dim;
			out.println(ClusFormat.SIX_AFTER_DOT.format(avgacc));	
		} else {
			for (int i = 0; i < m_Dim; i++) {
				out.println();
				out.println(getPrefix()+"Attribute: "+m_Attrs[i].getName());
				showContTable(out, i);
			}
		}
	}

	public String showModelError(int detail) {
		String out="";
		//if (detail == DETAIL_VERY_SMALL) {
		if (true) {
			out+=getPrefix()+"[";
			double avgacc = 0.0;
			for (int i = 0; i < m_Dim; i++) {
				if (i != 0) out+=",";
				double acc = calcAccuracy(i);
				avgacc += acc;
				out+=ClusFormat.SIX_AFTER_DOT.format(acc);
			}
			//out.println("]");
			out+="]: ";
			avgacc = avgacc / m_Dim;
			out+=ClusFormat.SIX_AFTER_DOT.format(avgacc);	
		} else {
			for (int i = 0; i < m_Dim; i++) {
				out+="\n";
				out+=getPrefix()+"Attribute: "+m_Attrs[i].getName()+"\n";
				out+=showContTable( i);
			}
		}
		
		return out;
	}
	
	public void printF1(PrintWriter out) {
		// if all targets are binary, then also print F1
		boolean binary = true;
		for (int i = 0; i < m_Dim; i++) {
			int size = m_Attrs[i].getNbValuesInclMissing();
			if (size != 2) binary = false;
			// for binary data we can be sure that the '1' is in the first position and the '0' in the second
			if (m_Attrs[i].getValueOrMissing(0) != "1") binary = false;
			if (m_Attrs[i].getValueOrMissing(1) != "0") binary = false;
		}
		if (binary) {
			out.print(getPrefix()+"[");
			double avgF1 = 0.0;
			for (int i = 0; i < m_Dim; i++) {
				if (i != 0) out.print(",");
				double f1 = calcF1(i);
				avgF1 += f1;
				if (((Double)f1).isNaN()) out.print("NaN");
				else out.print(ClusFormat.SIX_AFTER_DOT.format(f1));
			}
			//out.println("]");
			out.print("]: ");
			avgF1 = avgF1 / m_Dim;
			if (((Double)avgF1).isNaN()) out.println("NaN");
			else out.println(ClusFormat.SIX_AFTER_DOT.format(avgF1));
		}
		else out.println();
	}
	
	
	
	public String printF1() {
		// if all targets are binary, then also print F1
		String out="";
		boolean binary = true;
		for (int i = 0; i < m_Dim; i++) {
			int size = m_Attrs[i].getNbValuesInclMissing();
			if (size != 2) binary = false;
			// for binary data we can be sure that the '1' is in the first position and the '0' in the second
			if (m_Attrs[i].getValueOrMissing(0) != "1") binary = false;
			if (m_Attrs[i].getValueOrMissing(1) != "0") binary = false;
		}
		if (binary) {
			out+=getPrefix()+"[";
			double avgF1 = 0.0;
			for (int i = 0; i < m_Dim; i++) {
				if (i != 0) out+=",";
				double f1 = calcF1(i);
				avgF1 += f1;
				if (((Double)f1).isNaN()) out+="NaN";
				else out+=ClusFormat.SIX_AFTER_DOT.format(f1);
			}
			//out.println("]");
			out+="]: ";
			avgF1 = avgF1 / m_Dim;
			if (((Double)avgF1).isNaN()) out+="NaN\n";
			else out+=ClusFormat.SIX_AFTER_DOT.format(avgF1)+"\n";
		}
		else out+="\n";
		
		return out;
	}
	
	
	public int sumColumn(int[][] table, int j) {
		int sum = 0;
		for (int i = 0; i < table.length; i++)
			sum += table[i][j];
		return sum;
	}

	public int sumRow(int[][] table, int i) {
		int sum = 0;
		for (int j = 0; j < table.length; j++)
			sum += table[i][j];
		return sum;
	}
	
	public int sumColumn(int k, int j) {
		int sum = 0;
		int size = m_Attrs[k].getNbValues();
		int[][] table = m_ContTable[k];				
		for (int i = 0; i < size; i++)
			sum += table[i][j];
		return sum;
	}

	public int sumRow(int k, int i) {
		int sum = 0;
		int size = m_Attrs[k].getNbValues();
		int[][] table = m_ContTable[k];				
		for (int j = 0; j < size; j++)
			sum += table[i][j];
		return sum;
	}

	public void showContTable(PrintWriter out, int i) {
		int[][] table = m_ContTable[i];
		int size = m_Attrs[i].getNbValues();
		if (m_Attrs[i].hasMissing()) {
			// also add a column for "?" for the semi-supervised setting
			size++;
		}
		// Calculate sizes
		int[] wds = new int[size+2];
		// First column
		wds[0] = REAL_PRED.length();
		for (int j = 0; j < size; j++) {
			wds[j+1] = m_Attrs[i].getValueOrMissing(j).length()+1;
		}
		// Middle columns
		for (int j = 0; j < size; j++) {
			wds[0] = Math.max(wds[0], m_Attrs[i].getValueOrMissing(j).length());
			for (int k = 0; k < size; k++) {
				String str = String.valueOf(table[j][k]);
				wds[k+1] = Math.max(wds[k+1], str.length()+1);
			}
			String str = String.valueOf(sumRow(table, j));
			wds[size+1] = Math.max(wds[size+1], str.length()+1);
		}
		// Bottom row
		for (int k = 0; k < size; k++) {
			String str = String.valueOf(sumColumn(table, k));
			wds[k+1] = Math.max(wds[k+1], str.length()+1);
		}
		// Total sum
		wds[size+1] = Math.max(wds[size+1], String.valueOf(getNbExamples()).length()+1);
		// Calculate line width
		int s = 0;
		for (int j = 0; j < size+2; j++) s += wds[j];
		String horiz = getPrefix()+"  "+StringUtils.makeString('-', s+(size+1)*2);
		// Header
		out.print(getPrefix()+"  ");
		printString(out, wds[0], REAL_PRED);
		out.print(" |");
		for (int j = 0; j < size; j++) {
			printString(out, wds[j+1], m_Attrs[i].getValueOrMissing(j));
			out.print(" |");
		}
		out.println();
		out.println(horiz);
		// Data rows
		for (int j = 0; j < size; j++) {
			out.print(getPrefix()+"  ");
			printString(out, wds[0], m_Attrs[i].getValueOrMissing(j));
			out.print(" |");
			for (int k = 0; k < size; k++) {
				printString(out, wds[k+1], String.valueOf(table[j][k]));
				out.print(" |");
			}
			printString(out, wds[size+1], String.valueOf(sumRow(table, j)));
			out.println();
		}
		out.println(horiz);
		out.print(getPrefix()+"  ");
		out.print(StringUtils.makeString(' ', wds[0]));
		out.print(" |");
		for (int k = 0; k < size; k++) {
			printString(out, wds[k+1], String.valueOf(sumColumn(table, k)));
			out.print(" |");
		}
		printString(out, wds[size+1], String.valueOf(getNbExamples()));
		out.println();
		out.print(getPrefix()+"  ");
		showAccuracy(out, i);
		out.print(getPrefix()+"  ");
		double cramer = calcCramerV(i);
		out.println("Cramer's coefficient: "+ClusFormat.SIX_AFTER_DOT.format(cramer));
		out.println();
	}
	
	public String showContTable(int i) {
		String out = "";
		int[][] table = m_ContTable[i];
		int size = m_Attrs[i].getNbValues();
		if (m_Attrs[i].hasMissing()) {
			// also add a column for "?" for the semi-supervised setting
			size++;
		}
		// Calculate sizes
		int[] wds = new int[size+2];
		// First column
		wds[0] = REAL_PRED.length();
		for (int j = 0; j < size; j++) {
			wds[j+1] = m_Attrs[i].getValueOrMissing(j).length()+1;
		}
		// Middle columns
		for (int j = 0; j < size; j++) {
			wds[0] = Math.max(wds[0], m_Attrs[i].getValueOrMissing(j).length());
			for (int k = 0; k < size; k++) {
				String str = String.valueOf(table[j][k]);
				wds[k+1] = Math.max(wds[k+1], str.length()+1);
			}
			String str = String.valueOf(sumRow(table, j));
			wds[size+1] = Math.max(wds[size+1], str.length()+1);
		}
		// Bottom row
		for (int k = 0; k < size; k++) {
			String str = String.valueOf(sumColumn(table, k));
			wds[k+1] = Math.max(wds[k+1], str.length()+1);
		}
		// Total sum
		wds[size+1] = Math.max(wds[size+1], String.valueOf(getNbExamples()).length()+1);
		// Calculate line width
		int s = 0;
		for (int j = 0; j < size+2; j++) s += wds[j];
		String horiz = getPrefix()+"  "+StringUtils.makeString('-', s+(size+1)*2);
		// Header
		out+=getPrefix()+"  ";
		out+=printString( wds[0], REAL_PRED);
		out+=" |";
		for (int j = 0; j < size; j++) {
			out+=printString( wds[j+1], m_Attrs[i].getValueOrMissing(j));
			out+=" |";
		}
		out+="\n";
		out+=horiz;
		// Data rows
		for (int j = 0; j < size; j++) {
			out+=getPrefix()+"  ";
			printString( wds[0], m_Attrs[i].getValueOrMissing(j));
			out+=" |";
			for (int k = 0; k < size; k++) {
				printString( wds[k+1], String.valueOf(table[j][k]));
				out+=" |";
			}
			out+=printString( wds[size+1], String.valueOf(sumRow(table, j)));
			out+="\n";
		}
		out+=horiz+"\n";
		out+=getPrefix()+"  ";
		out+=StringUtils.makeString(' ', wds[0]);
		out+=" |";
		for (int k = 0; k < size; k++) {
			out+=printString( wds[k+1], String.valueOf(sumColumn(table, k)));
			out+=" |";
		}
		out+=printString( wds[size+1], String.valueOf(getNbExamples()));
		out+="\n";
		out+=getPrefix()+"  ";
		showAccuracy(i);
		out+=getPrefix()+"  ";
		double cramer = calcCramerV(i);
		out+="Cramer's coefficient: "+ClusFormat.SIX_AFTER_DOT.format(cramer)+"\n";
		out+="\n";
		
		return out;
	}
	

	public void showSummaryError(PrintWriter out, boolean detail) {
		if (!detail) {
			for (int i = 0; i < m_Dim; i++) {
				out.print(getPrefix()+"Attribute: "+m_Attrs[i].getName()+" - ");
				showAccuracy(out, i);
			}
		}
	}
	
	public String showSummaryError( boolean detail) {
		String out="";
		if (!detail) {
			for (int i = 0; i < m_Dim; i++) {
				out+=getPrefix()+"Attribute: "+m_Attrs[i].getName()+" - ";
				out+=showAccuracy( i);
			}
		}
		return out;
	}

	public void printString(PrintWriter out, int wd, String str) {
		out.print(StringUtils.makeString(' ', wd-str.length()));
		out.print(str);
	}
	
	public String printString(int wd, String str) {
		String out = StringUtils.makeString(' ', wd-str.length());
		out+=str;
		return out;
	}


	public String getName() {
		//return "Classification Error";
		// Actually the accuracy is returned, not the error!
		return "Accuracy";
	}

	public ClusError getErrorClone(ClusErrorList par) {
		return new ContingencyTable(par, m_Attrs);
	}

	public void addExample(DataTuple tuple, ClusStatistic pred) {
		int[] predicted = pred.getNominalPred();
		for (int i = 0; i < m_Dim; i++) {
			m_ContTable[i][getAttr(i).getNominal(tuple)][predicted[i]]++;
		}
	}

	public void addInvalid(DataTuple tuple) {
	}

// FIXME: do we still need these (?):

		public double get_error_classif(){
			//System.out.println("nb of examples : "+getNbExamples());
			//System.out.println("nb of correctly classify examples : "+calcNbCorrect(m_ContTable[0]));
		return (1 - get_accuracy());
		}

		public double get_TP(){
			return calcNbCorrect(0);
		}

		public double get_accuracy() {
			return calcAccuracy(0);
		}

		public double get_precision() {
			return 0.0;
		}

		public double get_recall() {
			return 0.0;
		}

		public double get_auc() {
			return 0.0;
		}
}
