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
		XX = Math.sqrt(XX);
		YY = Math.sqrt(YY);
		
		return XY/(XX*YY);
	}
	
	public static void main(String[] args) {
		
	}
}
