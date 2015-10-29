package tools;

import java.util.ArrayList;

public class DadosEntrada {
	
	public static ArrayList<String> datasets;
	
	public DadosEntrada(){
		datasets = new ArrayList<String>();
		
		//datasets.add("SC-yeast-GO-threshold-3");
		//datasets.add("MM-mouse-GO-threshold-3");
		//datasets.add("DM-fly-GO-threshold-3");
		datasets.add("CE-worm-GO-threshold-3");
		
		
//		datasets.add("baseUFRGS");

/*		datasets.add("abalone.data");
		datasets.add("anneal");
		datasets.add("arrhythmia");
		datasets.add("audiology");
		datasets.add("autos");
		datasets.add("breast-cancer");
		datasets.add("bridges_version1");
		datasets.add("bridges_version2");
		datasets.add("car");
		datasets.add("cmc");
		datasets.add("colic.ORIG");
		datasets.add("colic");
		datasets.add("colicGALE");
		datasets.add("column_2C_weka");
		datasets.add("column_3C_weka");
		datasets.add("credit-a");
		datasets.add("credit-g");
		datasets.add("cylinder-bands");
		datasets.add("dermatology");
		datasets.add("diabetes");
		datasets.add("ecoli");
		datasets.add("flags");
		datasets.add("glass");
		datasets.add("haberman");
		datasets.add("heart-c");
		datasets.add("heart-h");
		datasets.add("heart-statlog");
		datasets.add("hepatitis");
		datasets.add("HillValleyWithNoise-full");
		datasets.add("hypothyroid");
		datasets.add("ionosphere");
		datasets.add("iris");
		datasets.add("kdd_synthetic_control");
		datasets.add("kr-vs-kp");
		datasets.add("liver-disorders");
		datasets.add("lymph");
		datasets.add("meta.data");
		datasets.add("mfeat-morphological");
		datasets.add("molecular-biology_promoters");
		datasets.add("mushroom");
		datasets.add("postoperative-patient-data");
		datasets.add("primary-tumor");
		datasets.add("segment");
		datasets.add("semeion");
		datasets.add("sensor_readings_2");
		datasets.add("sensor_readings_4");
		datasets.add("shuttle-landing-control");
		datasets.add("sick");
		datasets.add("solar-flare_1");
		datasets.add("sonar");
		datasets.add("soybean");
		datasets.add("spambase");
		datasets.add("sponge");
		datasets.add("tae");
		datasets.add("tempdiag");
		datasets.add("tep.fea");
		datasets.add("tic-tac-toe");
		datasets.add("trains");
		datasets.add("transfusion");
		datasets.add("vehicle");
		datasets.add("vote");
		datasets.add("vowel");
		datasets.add("winequality-red");
		datasets.add("winequality-white"); 
		datasets.add("breast-w");
		datasets.add("zoo"); */
		

/*		datasets.add("lung-cancer"); 
		datasets.add("labor"); 
		datasets.add("hayes-roth-full");
		datasets.add("balance-scale");
	    datasets.add("wine");
		datasets.add("solar-flare_2");
		datasets.add("page-blocks");
		datasets.add("splice");
		datasets.add("K8");
		datasets.add("letter");
		datasets.add("pendigits");
		datasets.add("sensor_readings_24");
		datasets.add("eighthr");
		datasets.add("onehr");
		datasets.add("optdigits"); */

		//Datasets do paper GECCO
	/*	datasets.add("abalone.data");
		datasets.add("anneal");
		datasets.add("arrhythmia");
		datasets.add("audiology");
		datasets.add("bridges_version1");
		datasets.add("car");
		datasets.add("cylinder-bands");
		datasets.add("glass");
		datasets.add("hepatitis");
		datasets.add("iris");
		datasets.add("kdd_synthetic_control");
		datasets.add("segment");
		datasets.add("semeion");
		datasets.add("shuttle-landing-control");
		datasets.add("sick");
		datasets.add("tempdiag");
		datasets.add("tep.fea.csv");
		datasets.add("vowel");
		datasets.add("winequality-red");
		datasets.add("winequality-white"); */
		
		// Datasets molecular docking
	//	datasets.add("ETH");
	//	datasets.add("PIF");
	//	datasets.add("TCL");
	//	datasets.add("NADH");
	//	datasets.add("INH");
	//	datasets.add("TCLDerivado");
		
		//Parte 1 Otimizacao
/*		datasets.add("molecular-biology_promoters");
		datasets.add("solar-flare_2");
		datasets.add("soybean");
		datasets.add("tic-tac-toe");
		datasets.add("vote"); */
		
		//Imbalanced
/*		datasets.add("automobile");
		datasets.add("balance");
		datasets.add("cleveland");
		datasets.add("contraceptive");
		datasets.add("dermatology");
		datasets.add("ecoli");
		datasets.add("flare");
		datasets.add("glass");
	//	datasets.add("hayes-roth");
		datasets.add("led7digit");
		datasets.add("lymphography");
		datasets.add("new-thyroid");
		datasets.add("nursery");
		datasets.add("page-blocks");
//		datasets.add("post-operative");
		datasets.add("satimage");
		datasets.add("shuttle");
		datasets.add("splice");
		datasets.add("thyroid");
		datasets.add("wine");
		datasets.add("winequality-red");
		datasets.add("winequality-white");
		datasets.add("yeast");
		datasets.add("zoo");  */
		

	
		
		//Gene Expression (full)
/*		datasets.add("alizadeh-2000-v1");
		datasets.add("alizadeh-2000-v2");
		datasets.add("alizadeh-2000-v3");
		datasets.add("armstrong-2002-v1");
		datasets.add("armstrong-2002-v2");
		datasets.add("bhattacharjee-2001");
		datasets.add("bittner-2000");
		datasets.add("bredel-2005");
		datasets.add("chen-2002");
		datasets.add("chowdary-2006");
		datasets.add("dyrskjot-2003");
		datasets.add("garber-2001");
		datasets.add("golub-1999-v1");
		datasets.add("golub-1999-v2");
		datasets.add("gordon-2002");
		datasets.add("khan-2001");
		datasets.add("laiho-2007");
		datasets.add("lapointe-2004-v1");
		datasets.add("lapointe-2004-v2");
		datasets.add("liang-2005");
		datasets.add("nutt-2003-v1");
		datasets.add("nutt-2003-v2");
		datasets.add("nutt-2003-v3");
		datasets.add("pomeroy-2002-v1");
		datasets.add("pomeroy-2002-v2"); 
		datasets.add("ramaswamy-2001");
		datasets.add("risinger-2003");
		datasets.add("shipp-2002-v1");
		datasets.add("singh-2002");
		datasets.add("su-2001");
		datasets.add("tomlins-2006");
		datasets.add("tomlins-2006-v2");
		datasets.add("west-2001");
		datasets.add("yeoh-2002-v1");
		datasets.add("yeoh-2002-v2");  */
		
		
		/*
	    //Gene Expression (21 selecionadas para experimentos)
		datasets.add("alizadeh-2000-v1");
		datasets.add("alizadeh-2000-v2");

//		datasets.add("alizadeh-2000-v3");
//		datasets.add("armstrong-2002-v1");

		datasets.add("bhattacharjee-2001");
		datasets.add("bittner-2000");
		datasets.add("chen-2002");
		datasets.add("chowdary-2006");
		datasets.add("golub-1999-v1"); 
		datasets.add("lapointe-2004-v1"); 
//		datasets.add("lapointe-2004-v2");
		datasets.add("liang-2005");
		datasets.add("nutt-2003-v1");
		datasets.add("nutt-2003-v2");
//		datasets.add("nutt-2003-v3");
		datasets.add("pomeroy-2002-v1");
		datasets.add("risinger-2003");
		datasets.add("shipp-2002-v1");
		datasets.add("singh-2002");
//		datasets.add("tomlins-2006");
		datasets.add("west-2001");  

		 */
		
		//Gene Expression (14 selecionados para otimizacao de parametros)
/*		datasets.add("armstrong-2002-v2");
		datasets.add("bredel-2005");
		datasets.add("dyrskjot-2003");
		datasets.add("garber-2001");
		datasets.add("golub-1999-v2");
		datasets.add("gordon-2002");
		datasets.add("khan-2001");
		datasets.add("laiho-2007");
		datasets.add("pomeroy-2002-v2");
		datasets.add("ramaswamy-2001");
		datasets.add("su-2001");
		datasets.add("tomlins-2006-v2");
		datasets.add("yeoh-2002-v1");
		datasets.add("yeoh-2002-v2"); */
		
		
		
		//Datasets paper ICDM 2012
	/*	datasets.add("9Gauss");
		datasets.add("australian");
		datasets.add("balance-scale");
		datasets.add("BCI");
		datasets.add("column3c");
		datasets.add("digit1");
		datasets.add("heart");
		datasets.add("ionosphere");
		datasets.add("iris");
		datasets.add("letters");
		datasets.add("liver");
		datasets.add("mfeat");
		datasets.add("pendigits");
		datasets.add("segment");
		datasets.add("sensor2");
		datasets.add("sensor4");
		datasets.add("sonar");
		datasets.add("tep.fea");
		datasets.add("USPS");
		datasets.add("vehicle");
		datasets.add("wine");
		datasets.add("wisconsin"); */
		
	//	datasets.add("Dados9");
		
		
		// Todos datasets UCI formatados para tese
		/*datasets.add("abalone.data");
		datasets.add("anneal");
		datasets.add("arrhythmia");
		datasets.add("audiology");
		datasets.add("autos");
		datasets.add("balance-scale");
		datasets.add("breast-cancer");
		datasets.add("breast-w");
		datasets.add("bridges_version1");
		datasets.add("bridges_version2");
		datasets.add("car");
		datasets.add("cmc");
		datasets.add("colic");
		datasets.add("column_2C_weka");
		datasets.add("column_3C_weka");
		datasets.add("credit-a");
		datasets.add("credit-g");
		datasets.add("cylinder-bands");
		datasets.add("dermatology");
		datasets.add("diabetes");
		datasets.add("ecoli");
		datasets.add("flags");
		datasets.add("glass");
		datasets.add("haberman");
		datasets.add("hayes-roth-full");
		datasets.add("heart-c");
		datasets.add("heart-h");
		datasets.add("heart-statlog");
		datasets.add("hepatitis");
		datasets.add("ionosphere");
		datasets.add("iris");
		datasets.add("kdd_synthetic_control");
		datasets.add("kr-vs-kp");
		datasets.add("labor");
		datasets.add("liver-disorders");
		datasets.add("lung-cancer");
		datasets.add("lymph");
		datasets.add("meta.data");
		datasets.add("mfeat-morphological");
		datasets.add("molecular-biology_promoters");
		datasets.add("mushroom");
		datasets.add("postoperative-patient-data");
		datasets.add("primary-tumor");
		datasets.add("segment");
		datasets.add("semeion");
		datasets.add("sensor_readings_2");
		datasets.add("sensor_readings_4");
		datasets.add("shuttle-landing-control");
		datasets.add("sick");
		datasets.add("solar-flare_1");
		datasets.add("solar-flare_2");
		datasets.add("sonar");
		datasets.add("soybean");
		datasets.add("sponge");
		datasets.add("tae");
		datasets.add("tempdiag");
		datasets.add("tep.fea");
		datasets.add("tic-tac-toe");
		datasets.add("trains");
		datasets.add("transfusion");
		datasets.add("vehicle");
		datasets.add("vote");
		datasets.add("vowel");
		datasets.add("wine");
		datasets.add("winequality-red");
		datasets.add("winequality-white");
		datasets.add("zoo");
		datasets.add("glass");*/
		
		
		// UCI 27 OTIMIZACAO
/*		datasets.add("balance-scale");
		datasets.add("cmc");
		datasets.add("column_2C_weka");
		datasets.add("column_3C_weka");
		datasets.add("credit-a");
		datasets.add("cylinder-bands");
		datasets.add("dermatology");
		datasets.add("diabetes");
		datasets.add("ecoli");
		datasets.add("glass");
		datasets.add("heart-statlog");
		datasets.add("hepatitis");
		datasets.add("lymph");
		datasets.add("mushroom");
		datasets.add("primary-tumor");
		datasets.add("segment");
		datasets.add("semeion");
		datasets.add("sensor_readings_2");
		datasets.add("sensor_readings_4");
		datasets.add("sick");
		datasets.add("solar-flare_1");
		datasets.add("solar-flare_2");
		datasets.add("sonar");
		datasets.add("sponge");
		datasets.add("trains");
		datasets.add("wine");
		datasets.add("zoo"); */
		
		
		// UCI 40 bases grupo "experimentos"
/*		datasets.add("abalone.data");
		datasets.add("anneal");
		datasets.add("arrhythmia");
		datasets.add("audiology");
		datasets.add("autos");
		datasets.add("breast-cancer");
//		datasets.add("bridges_version1");
		datasets.add("bridges_version2");
		datasets.add("car");
		datasets.add("heart-c");
		datasets.add("flags");
		datasets.add("credit-g");
		datasets.add("colic");
//		datasets.add("haberman");
		datasets.add("heart-h");
		datasets.add("ionosphere");
//		datasets.add("iris");
		datasets.add("kr-vs-kp");
//		datasets.add("labor");
		datasets.add("liver-disorders");
//		datasets.add("lung-cancer");
		datasets.add("meta.data");
		datasets.add("mfeat-morphological");
		datasets.add("molecular-biology_promoters");
//		datasets.add("postoperative-patient-data");
		datasets.add("shuttle-landing-control");
		datasets.add("soybean");
		datasets.add("kdd_synthetic_control");
//		datasets.add("tae");
//		datasets.add("tempdiag");
		datasets.add("tep.fea");
		datasets.add("tic-tac-toe");
		datasets.add("transfusion");
		datasets.add("vehicle");
		datasets.add("vote");
		datasets.add("vowel");
		datasets.add("winequality-red");
		datasets.add("winequality-white");
		datasets.add("breast-w"); */
		
		
		// baselines
	/*	datasets.add("kdd_synthetic_control");
		datasets.add("trains");
		datasets.add("sonar");
		datasets.add("credit-a");
		datasets.add("sick");
		datasets.add("sponge");
		datasets.add("lymph");
		datasets.add("ecoli"); */
		
		
		
	}
	
	public ArrayList<String> getData(){
		return datasets;
	}

}
