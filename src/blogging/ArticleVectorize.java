package blogging;

import java.util.HashSet;
import java.util.TreeMap;

public class ArticleVectorize {
	public static Object[] getArticleTF_DF(String[][] article) {
		Object[] obj = new Object[4];

		// TITLE
		TreeMap<String,Integer> tf_tt = new TreeMap<String,Integer>(); // TERM FREQUENCY with TERM and TITLE
		TreeMap<String,Integer> tf_t = new TreeMap<String,Integer>(); // TERM FREQUENCY on TITLES
		
		// POST(TEXT)
		TreeMap<String,Integer> tf_tp = new TreeMap<String,Integer>(); // TERM FREQUENCY with TERM and POST
		TreeMap<String,Integer> pf_t = new TreeMap<String,Integer>(); // TERM FREQUENCY on POSTS
		
		for (int i = 0; i < article.length; i++) {
			String title = article[i][0].trim();
			String text = article[i][1].trim();
			text = text.replace('\n', ' ');
			
			String[] titleArr = title.split(" ");
			String[] textArr = text.split(" ");
			
			HashSet<String> termTitle = new HashSet<String>(); // A SET of TERM for THIS TITLE
			HashSet<String> termPost = new HashSet<String>(); // A SET of TERM for THIS TEXT
			
			// TITLE
			for (String t : titleArr) {
				if (t.length() > 3) {
					for (int j = 0; j < t.length()-2; j++) {
						int count = 0;
						if (tf_tt.containsKey(t.substring(j,j+3)+";"+i))
							count = tf_tt.get(t.substring(j,j+3)+";"+i);
						count++;
						tf_tt.put(t.substring(j,j+3)+";"+i, count);
						
						termTitle.add(t.substring(j, j+3));
					}
				} else {
					int count = 0;
					if (tf_tt.containsKey(t+";"+i))
						count = tf_tt.get(t+";"+i);
					count++;
					tf_tt.put(t+";"+i, count);
					
					termTitle.add(t);
				}
			}
			
			// POSTS
			for (String t : textArr) {
				if (t.length() > 3) {
					for (int j = 0; j < t.length()-2; j++) {
						int count = 0;
						if (tf_tp.containsKey(t.substring(j,j+3)+";"+i))
							count = tf_tp.get(t.substring(j,j+3)+";"+i);
						count++;
						tf_tp.put(t.substring(j,j+3), count);

						termPost.add(t.substring(j,j+3));
					}
				} else {
					int count = 0;
					if (tf_tp.containsKey(t+";"+i))
						count = tf_tp.get(t+";"+i);
					count++;
					tf_tp.put(t+";"+i, count);
					
					termPost.add(t);
				}
			}
			
			// COUNT TERM FOR TITLES
			for (String t : termTitle) {
				int count = 0;
				if (tf_t.containsKey(t))
					count = tf_t.get(t);
				count++;
				tf_t.put(t, count);
			}
			
			// COUNT TERM FOR POSTS
			for (String t : termPost) {
				int count = 0;
				if (pf_t.containsKey(t))
					count = pf_t.get(t);
				count++;
				pf_t.put(t, count);
			}
		}
		
		obj[0] = tf_tt;
		obj[1] = tf_t;
		obj[2] = tf_tp;
		obj[3] = pf_t;
		
		return obj;
	}
	
	public static double[][] getW(int numIDs, TreeMap<String,Integer> tf, TreeMap<String,Integer> df) {
		double[][] w = new double[numIDs][df.keySet().size()];
		
		for (int i = 0; i < w.length; i++) {
			System.out.println(i);
			for (int j = 0; j < w[i].length; j++) {
				String term = (String) df.keySet().toArray()[j];
				
				if (tf.containsKey(term+";"+i))
					w[i][j] = (1.0 + Math.log((double)tf.get(term+";"+i))) * Math.log10((double)df.keySet().size() / (double) df.get(term));
				else
					w[i][j] = Math.log10((double)df.keySet().size() / (double) df.get(term));
			}
		}
		
		return w;
	}
	
	public static void main(String[] args) {
		double procTime = System.currentTimeMillis();
		
		Mysql.init_conn();
		String[][] article = Mysql.get_article();
		final int numIDs = Mysql.get_article_count();
		
		Object[] obj = getArticleTF_DF(article);
		// TITLE
		@SuppressWarnings("unchecked")
		TreeMap<String,Integer> tf_tt = (TreeMap<String,Integer>) obj[0];
		@SuppressWarnings("unchecked")
		TreeMap<String,Integer> tf_t = (TreeMap<String,Integer>) obj[1];
		// POST(TEXT)
		@SuppressWarnings("unchecked")
		TreeMap<String,Integer> tf_tp = (TreeMap<String,Integer>) obj[2];
		@SuppressWarnings("unchecked")
		TreeMap<String,Integer> pf_t = (TreeMap<String,Integer>) obj[3];
		
		double[][] w_title = getW(numIDs, tf_tt, tf_t);
		double[][] w_post = getW(numIDs, tf_tp, pf_t);
		
		Myfile.writeTF_IDF("w_title", w_title);
		Myfile.writeTF_IDF("w_post", w_post);
		
		System.out.println((System.currentTimeMillis() - procTime)/1000+" seconds.");
	}
}
