package psl.memento.pervasive.recommendation.keywordfinder.centroid;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import psl.conversation.Context;
import psl.conversation.ConversationLogIterator;
import psl.conversation.ConversationMessage;
import psl.conversation.Keyword;
import psl.conversation.KeywordContainer;
import psl.conversation.KeywordFinder;
import psl.conversation.XMLContainer;
import psl.memento.pervasive.recommendation.keywordfinder.centroid.trainer.VectorMath;
import psl.memento.pervasive.recommendation.keywordfinder.centroid.trainer.Centroid;

public class implKeywordFinder implements KeywordFinder {
	private final double DECAY_FACTOR = 1; // change decay to no decay
	
	private String configfileName = "psl/memento/pervasive/recommendation/keywordfinder/centroid/KeywordFinderConfig.xml";
	//private String configfileName = "/home/jc424/project/psl/conversation/keywords2/KeywordFinderConfig.xml";
	private double[] document = null;
	private ConversationLogIterator it;
	private static Pattern conversationTextValidChars;
	
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
		conversationTextValidChars = Pattern.compile("[^a-zA-Z]+", Pattern.DOTALL);
	}

	/**
	 * @see psl.conversation.KeywordFinder#start(psl.conversation.XMLContainer, psl.conversation.ConversationLogIterator)
	 */
	public void start(XMLContainer xmlc, ConversationLogIterator cli)
		throws Exception {
			try {
			it = cli;
			implXMLContainer configData = (implXMLContainer) xmlc;
			configData.loadData(configfileName);
			
			// Read wordslist object.
			FileInputStream fis = new FileInputStream(configData.getWordslistFileName());
			ObjectInputStream ois = new ObjectInputStream(fis);
			htWordOrder = (Hashtable) ois.readObject();
			fis.close();
			ois.close();

			// Read topics and centroids objects from file.
			String centroidFileName = configData.getCentroidFilename();
			fis = new FileInputStream(centroidFileName);
			ois = new ObjectInputStream(fis);
			centroids = (Hashtable) ois.readObject();
			fis.close();
			ois.close();
			allTopics = new String[centroids.size()];
			centroids.keySet().toArray(allTopics);

			// create a new document to accept input words
			document = new double[htWordOrder.size()];

		} catch (Exception e) {
			throw new KeywordException("Error on initialization.", e);
		}	
			
	}

	/**
	 * @see psl.conversation.KeywordFinder#reset()
	 */
	public void reset() {
	}

	/**
	 * @see psl.conversation.KeywordFinder#reset(psl.conversation.XMLContainer)
	 */
	public void reset(XMLContainer xmlc) throws Exception {
	}

	/**
	 * @see psl.conversation.KeywordFinder#signal(psl.conversation.KeywordContainer)
	 */
	public void signal(KeywordContainer kc) throws Exception {
		System.out.println("JAVA KF: SIGNAL IS CALLED");
		if(topicHistory.isEmpty()) {
			System.out.println("JAVA KF: NOTHING TO RETURN");
			return;
		}
		StringTokenizer stKeywords = new StringTokenizer((String)topicHistory.get(topicHistory.size() - 1), KEYWORD_SEPARATOR);
		while(stKeywords.hasMoreTokens()) {  
			System.out.println("JAVA KF: RETURN KEYWORDS NOW");
			kc.add(new Keyword(stKeywords.nextToken(), new Context()));
		}
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true) {
			while(it.hasNext()) {
				ConversationMessage msg = it.next2();
				String msgText = msg.getMessage();
				//debug("INCOMING CONVERSATION:" + msgText);
				// Remove non-alpha stuff
				Matcher matches = conversationTextValidChars.matcher(msgText);
				msgText = matches.replaceAll(" ");
				
				StringTokenizer st = new StringTokenizer(msgText);
				while(st.hasMoreTokens()) {
					// decay importance of previous words
					for(int i=0;i<document.length;i++) {
						document[i] *= DECAY_FACTOR;
					}
					
					// Add a weight of 1 for the current word		 
					String word = st.nextToken();
					Integer id = (Integer)htWordOrder.get(word);
					int wordID = -1;
					if(id != null) 
					{
						document[id.intValue()]++;
					} 
				}
			
				// After each conversation message, find the closest centroid	
				String closestTopic = null;
				double maxCos = -Double.MAX_VALUE;
				double diff;
				double docLength = VectorMath.length(document);
				for(int i=0;i<allTopics.length;i++)
				{
					String topic = allTopics[i];
					Centroid centroid = (Centroid)centroids.get(topic);
					diff = VectorMath.dotProduct(document, centroid.centroid);
					
					diff /= (centroid.length * docLength);

					if(diff > maxCos) {
						closestTopic = topic;
						maxCos = diff; 
					}
				}
				if(closestTopic != null) {
					topics.put(closestTopic,closestTopic);
					topicHistory.add(topics.get(closestTopic));
					debug("finished CONVERSATION: topic=" + closestTopic);
				} 
			}
		}
	}

	private void debug(String msg) {
		System.out.println(msg);
	}
}
