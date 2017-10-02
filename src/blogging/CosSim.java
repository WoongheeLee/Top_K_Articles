package blogging;

public class CosSim {
	public static double dotProduct(double[] X, double[] Y) {
		double XY = 0d;
		
		for (int i = 0; i < X.length; i++) {
			XY += X[i]*Y[i];
		}
		
		return XY;
	}
	
	public static double cosSim(double[] X, double[] Y) {
		double XY = dotProduct(X,Y);
		double XX = dotProduct(X,X);
		double YY = dotProduct(Y,Y);
		
		if (XX == 0d || YY == 0d)
			return 0d;
		
		XX = Math.sqrt(XX);
		YY = Math.sqrt(YY);
		
		return XY/(XX*YY);
	}
	
	public static void main(String[] args) {
		Mysql.init_conn();
		final int N = Mysql.get_article_count();
		final double alpha = 0.65;
		for (int i = 0; i < N; i++) 
			for (int j = i+1; j < N; j++) 
				Mysql.set_id1_id2_sim(i+1, j+1, alpha*cosSim(Mysql.get_title_vector(i+1),Mysql.get_title_vector(j+1)) + (1-alpha)*cosSim(Mysql.get_post_vector(i+1), Mysql.get_post_vector(j+1)));
	}
}
