package psl.memento.pervasive.recommendation.keywordfinder.centroid;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import psl.memento.pervasive.recommendation.Context;
import psl.memento.pervasive.recommendation.ConversationLogMessageStream;
import psl.memento.pervasive.recommendation.ConversationMessage;
import psl.memento.pervasive.recommendation.Keyword;
import psl.memento.pervasive.recommendation.KeywordContainer;
import psl.memento.pervasive.recommendation.KeywordFinder;
import psl.memento.pervasive.recommendation.KeywordFinderConfiguration;
import psl.memento.pervasive.recommendation.exception.GenericException;
import psl.memento.pervasive.recommendation.keywordfinder.centroid.trainer.Centroid;
import psl.memento.pervasive.recommendation.keywordfinder.centroid.trainer.VectorMath;

public class implKeywordFinder implements KeywordFinder {
	private final double DECAY_FACTOR = 1.0;

	//private String configfileName = "c:/Documents and Settings/jc424/My Documents/eclipse/workspace/keywords/psl/conversation/keywords2/KeywordFinderConfig.xml";
	private String configfileName =
		"psl/memento/pervasive/recommendation/keywordfinder/centroid/KeywordFinderConfig.xml";

	// Contains 
	// TfIDF = term freq * log(N / doc freq)
	// N and doc freq comes from training set

	private int[] termFrequency = null;

	/**
	 * Contains the tfidf score for the current conversation. Each conversation
	 * message will be considered to be a document.
	 */
	private double[] document = null;

	private ConversationLogMessageStream it;
	
	private static Pattern conversationTextValidChars =
		Pattern.compile("[^a-zA-Z]+", Pattern.DOTALL);

	private static final String KEYWORD_SEPARATOR = ",";

	/**
	 * Maps a word to a position in the document vector.
	 */
	private Hashtable htWordOrder = null;

	/**
	 * Lists all topics and the associated centroid vector. 
	 */
	private Hashtable centroids = null;

	/**
	 * Document Frequency for all the words that we know about. Use this to
	 * calculate TFIDF score of current conversation.
	 */
	private Hashtable htDocFreq = null;

	/**
	 * List of just the topics, so we can iterate through it again and again.
	 */
	private String[] allTopics = null;

	/**
	 * To save memory space, all keywords that have occurred will be placed into
	 * a hashtable. The history vector of keywords will contain references to
	 * the hashtable entry.
	 */
	private Vector topicHistory = new Vector();

	/**
	 * See description for topicHistory
	 */
	private Hashtable topics = new Hashtable();

	/**
	 * Constructor for implKeywordFinder.
	 */
	public implKeywordFinder() {
	}


	/* (non-Javadoc)
	 * @see psl.memento.pervasive.recommendation.KeywordFinder#init(psl.memento.pervasive.recommendation.KeywordFinderConfiguration)
	 */
	public void init(KeywordFinderConfiguration kfc) {
		try {
			implXMLContainer configData = (implXMLContainer) kfc;

			// Read wordslist object.
			FileInputStream fis =
				new FileInputStream(configData.getWordslistFileName());
			ObjectInputStream ois = new ObjectInputStream(fis);
			htWordOrder = (Hashtable) ois.readObject();
			fis.close();
			ois.close();
			debug("finished reading wordorder list");

			// Read topics and centroids objects from file.
			fis = new FileInputStream(configData.getCentroidFilename());
			ois = new ObjectInputStream(fis);
			centroids = (Hashtable) ois.readObject();
			fis.close();
			ois.close();
			allTopics = new String[centroids.size()];
			centroids.keySet().toArray(allTopics);
			debug("finished reading topics and centroid list");

			// Read document frequency in training documents
			fis = new FileInputStream(configData.getDocFreqFilename());
			ois = new ObjectInputStream(fis);
			htDocFreq = (Hashtable) ois.readObject();
			fis.close();
			ois.close();
			debug("finsihed doc freq");

			// create a new document to accept input words
			document = new double[htWordOrder.size()];
			termFrequency = new int[htWordOrder.size()];

		} catch (Exception e) {
			e.printStackTrace();
			(new KeywordException("Error on initialization.", e)).printStackTrace();
		}

	}

	/**
	 * @see psl.conversation.KeywordFinder#reset()
	 */
	public void reset() {
	}

	/**
	 * @see psl.conversation.KeywordFinder#signal(psl.conversation.KeywordContainer)
	 */
	public void signal(KeywordContainer kc) {
		// System.out.println("JAVA KF: SIGNAL IS CALLED");
		if (topicHistory.isEmpty()) {
			// System.out.println("JAVA KF: NOTHING TO RETURN");
			return;
		}
		StringTokenizer stKeywords =
			new StringTokenizer(
				(String) topicHistory.get(topicHistory.size() - 1),
				KEYWORD_SEPARATOR);
		while (stKeywords.hasMoreTokens()) {
			// System.out.println("JAVA KF: RETURN KEYWORDS NOW");
			try {
				kc.add(new Keyword(stKeywords.nextToken(), new Context(), Keyword.NO_DELAY));
				kc.close();
			} catch (GenericException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true) {
			if (it != null)
			while (it.hasNext()) {
				ConversationMessage msg = it.next2();
				String msgText = msg.getMessage();

				// Remove non-alpha stuff
				Matcher matches = conversationTextValidChars.matcher(msgText);
				msgText = matches.replaceAll(" ");
				//debug("INCOMING CONVERSATION:" + msgText);
				StringTokenizer st = new StringTokenizer(msgText);
				while (st.hasMoreTokens()) {
					// decay importance of previous words
					for (int i = 0; i < termFrequency.length; i++) {
						termFrequency[i] *= DECAY_FACTOR;
					}

					// Add a weight of 1 for the current word 
					String word = st.nextToken();
					Integer id = (Integer) htWordOrder.get(word);
					int wordID = -1;
					if (id != null) {
						int wordPosition = id.intValue();
						termFrequency[wordPosition]++;

						// CALCULATE TFIDF SCORE
						Double inverseDocFreq = (Double) htDocFreq.get(word);
						document[wordPosition] =
							termFrequency[wordPosition]
								* inverseDocFreq.doubleValue();
					}
				}

				// find the closest centroid
				String closestTopic = null;
				double maxCos = -Double.MAX_VALUE;
				double diff = 0.0;
				double docLength = VectorMath.length(document);
				for (int i = 0; i < allTopics.length; i++) {
					String topic = allTopics[i];
					Centroid centroid = (Centroid) centroids.get(topic);
					diff = VectorMath.dotProduct(document, centroid.centroid);
					diff /= (centroid.length * docLength);
					System.out.println(diff);
					if (diff > maxCos) {
						closestTopic = topic;
						maxCos = diff;
					}
				}

				if (closestTopic != null) {
					topics.put(closestTopic, closestTopic);
					topicHistory.add(topics.get(closestTopic));
					debug("finished CONVERSATION: topic=" + closestTopic);
				}
			}
		}
	}

	private void debug(String msg) {
		System.out.println(msg);
	}

	/* (non-Javadoc)
	 * @see psl.memento.pervasive.recommendation.KeywordFinder#registerLogIterator(psl.memento.pervasive.recommendation.ConversationLogMessageStream)
	 */
	public void registerLogIterator(ConversationLogMessageStream cli) throws GenericException {
		it = cli;
	}

	/* (non-Javadoc)
	 * @see psl.memento.pervasive.recommendation.KeywordFinder#reset(psl.memento.pervasive.recommendation.KeywordFinderConfiguration)
	 */
	public void reset(KeywordFinderConfiguration kfc) throws Exception {
		
		
	}

	/* (non-Javadoc)
	 * @see psl.memento.pervasive.recommendation.KeywordFinder#stop()
	 */
	public void stop() {
		
		
	}
}
