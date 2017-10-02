package blogging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Myfile {
	public static void writeTF_IDF(String fileName, double[][] w) {
		BufferedWriter outputStream = null;
		
		try {
			outputStream = new BufferedWriter(new FileWriter(fileName+".tsv"));
			
			for (int i = 0; i < w.length; i++) {
				for (int j = 0; j < w[i].length; j++) {
					if(j == 0)
						outputStream.write(w[i][j]+"");
					else
						outputStream.write("\t"+w[i][j]);
					outputStream.flush();
				}
				outputStream.write("\n");
				outputStream.flush();
			}
			
			outputStream.close();
		} catch (IOException e) {
			System.err.println("writeTF_IDF: "+e.getMessage());
			System.exit(1);
		}
		
		System.out.println("\""+fileName+".tsv\" writing done!");
	}
}
