package psl.memento.pervasive.recommendation.keywordfinder.centroid.trainer;

/**
 * @author jc424
 */
public class VectorMath {

	private VectorMath() {
	}

	public static double length(double[] v) {
		double temp = 0;
		for(int i=0;i<v.length;i++) {
			temp += v[i]*v[i];
		}
		return Math.sqrt(temp);
	}
	
	public static double[] add(double[] v1, double[] v2) {
		for(int i=0;i<v1.length;i++) {
			v1[i] += v2[i];
		}
		return v1;
	}
	
	public static double dotProduct(double[] v1, double[] v2){
		double temp = 0;
		for(int i=0;i<v1.length;i++){
			temp += v1[i] * v2[i]; 
		}
		return temp;
	}
	
	public static double[] scalarDivide(double[] v, double d) {
		for(int i=0;i<v.length;i++){
			v[i] = v[i] / d;
		}
		return v;
	}
}
