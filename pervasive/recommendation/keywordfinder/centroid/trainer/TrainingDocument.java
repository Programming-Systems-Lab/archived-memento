package psl.memento.pervasive.recommendation.keywordfinder.centroid.trainer;

import java.util.Hashtable;

/**
 * @author jc424
 */
public class TrainingDocument {
	private Hashtable htTermFreq = null;
	private double[] tfidf = null;

	/**
	 * Constructor for TrainingDocument.
	 */
	public TrainingDocument() {
		super();
	}

	/**
	 * Returns the htTermFreq.
	 * @return Hashtable
	 */
	public Hashtable getTermFreq() {
		return htTermFreq;
	}

	/**
	 * Sets the htTermFreq.
	 * @param htTermFreq The htTermFreq to set
	 */
	public void setTermFreq(Hashtable htTermFreq) {
		this.htTermFreq = htTermFreq;
	}
	/**
	 * Returns the tfidf.
	 * @return double[]
	 */
	public double[] getTfidf() {
		return tfidf;
	}

	/**
	 * Sets the tfidf.
	 * @param tfidf The tfidf to set
	 */
	public void setTfidf(double[] tfidf) {
		this.tfidf = tfidf;
	}

}
