package blogging;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class InsertVectors {
	public static void insertVectorsIntoDB(String fileName) {
		String mode = fileName.substring(2,fileName.length()-4);
		System.out.println(mode);

		BufferedReader inputStream = null;

		try {
			inputStream = new BufferedReader(new FileReader(fileName));

			String l = null;
			int idx = 0;
			while((l = inputStream.readLine()) != null) {
				if (l.length() > 1) {
					if(mode.equals("title")) 
						Mysql.set_title_vector(idx+1, l);
					else 
						Mysql.set_post_vector(idx+1, l);

					System.out.println(mode+":\t"+(idx+1));
					idx++;
				}
			}
		} catch (IOException e) {
			System.out.println("insertVectorsIntoDB: "+e.getMessage());
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		Mysql.init_conn();
		insertVectorsIntoDB("w_title.tsv");
		insertVectorsIntoDB("w_post.tsv");
	}
}
