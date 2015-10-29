package scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class replaceCSVdelimiter {


	public static void main(String[] args) throws IOException {

		String path = "/Volumes/Dados/Dropbox/Projetos/Fapesp/Alemanha/Dados/2015/DataMarcio/";

		//String[] data = new String[4]; 	data[0] = "CTNNB1"; data[1] = "Erk"; data[2] = "Ikk2"; data[3] = "LEF1";
		String[] data = new String[1]; 	data[0] = "LEF1";

		for (int i = 0; i < data.length; i++) {


			//String[] x = new String[1]; x[0] = "Partitions";
			//String[] x = new String[4]; x[0] = "Dc"; x[1] = "Di"; x[2] = "Raw"; x[3] = "Tt";
			
			String[] x = new String[4]; x[0] = "Di"; x[1] = "Raw"; x[2] = "Tt";
			

			for (int j = 0; j < x.length; j++) {
				String verify, putData;
				File file = new File(path+data[i] + "/" + data[i] + "." + x[j] + ".csv");

				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);

				//file.createNewFile();
				FileWriter fw = new FileWriter(file+"2.csv");
				BufferedWriter bw = new BufferedWriter(fw);

				while((verify=br.readLine()) != null ){ 
					putData = verify.replaceAll(";", ",");
					bw.write(putData);
					bw.newLine();
				}
				br.close();

				bw.close();
			}

		}
	}


}
