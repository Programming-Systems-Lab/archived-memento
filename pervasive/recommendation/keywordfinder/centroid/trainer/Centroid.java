package psl.memento.pervasive.recommendation.keywordfinder.centroid.trainer;

import java.io.Serializable;

public class Centroid implements Serializable {
	public double centroid[] = null;
	
	/**
	 * NOT the length of the array, but the length as calculated using the
	 * formula for "pairwise similarity btween the documents that support the
	 * centroid". See formula in section 4.1 of the article. 
	 */
	public double length;
	
	public Centroid() {
	}

}
