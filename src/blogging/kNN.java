package blogging;

import java.util.TreeMap;

public class kNN {
	public static int[] get_kNN(int K, int id) {
		double[] id_tvec = Mysql.get_title_vector(id);
		double[] id_pvec = Mysql.get_post_vector(id);

		double A = 0.65; // alpha

		int[] knn = new int[K];
		int articleCount = Mysql.get_article_count();

		// <Similarity, ID>
		TreeMap<Double,Integer> knnTemp = new TreeMap<Double,Integer>();

		for (int i = 0; i < articleCount; i++) {
			if((id-1) != i) {
				//				System.out.println(i);
				knnTemp.put(A*CosSim.cosSim(id_tvec,Mysql.get_title_vector(i+1))+(1-A)*CosSim.cosSim(id_pvec, Mysql.get_post_vector(i+1)), i+1);
			}
		}

		for (int i = 0; i < K; i++) {
			double sim = (double)knnTemp.keySet().toArray()[knnTemp.keySet().size()-i-1];
			int idd = knnTemp.get(sim);
			knn[i] = idd;
			//			System.out.println(idd+": "+sim);
		}

		return knn;
	}

	public static void print_kNN(int[] knn) {
		for (int i : knn) 
			printTitle(i);
	}

	public static void printTitle(int id) {
		System.out.println(Mysql.get_title_from_article(id)+" \t"+getAddress(Mysql.get_title_from_article(id)));
	}
	
	public static String getAddress(String title) {
		char[] rp = {'`','~','!','@','#','$','%','^','&','*','(',')','-','_','+','=','[',']','{','}',';',':','\'','\"','<','>',',','.','?','/','\\','|'};
		
		for (char r : rp)
			title = title.replace(r, ' ');
		
		StringBuffer address = new StringBuffer();
		
		address.append("http://woongheelee.com/entry/");
		
		for (String t : title.trim().split(" ")) {
			address.append(t.trim()+"-");
		}
		
		return address.toString().substring(0,address.length()-1);
		
	}

	public static void main(String[] args) {
		Mysql.init_conn();

		final int K = 5;

		int itr = 10;
		int i = 0;
		while(i < itr) {
			int id = (int)(Math.random()*Mysql.get_article_count()+1);

			System.out.println("[Your Query] "+id);
			printTitle(id);

			System.out.println("\n[Related Articles]");
			print_kNN(get_kNN(K, id));

			System.out.println();
			i++;
		}
	}
}
