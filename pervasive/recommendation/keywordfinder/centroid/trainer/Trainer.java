package psl.memento.pervasive.recommendation.keywordfinder.centroid.trainer;



import java.io.FileOutputStream;

import java.io.ObjectOutputStream;

import java.text.DateFormat;

import java.util.ArrayList;

import java.util.Date;

import java.util.Enumeration;

import java.util.HashSet;

import java.util.Hashtable;

import java.util.Stack;

import java.util.regex.Pattern;



import org.xml.sax.Attributes;

import org.xml.sax.XMLReader;

import org.xml.sax.helpers.DefaultHandler;





/**

 * Takes input XML training documents, and output 2 datafiles that can be used

 * to detect topic of incoming text. 

 * <p>

 * INPUT: training documents in XML format. There is no DTD or schema

 * specification for the training doc, but the general format is that a document

 * is enclosed with a DOCUMENT_TAG. The hierarchy level is not being checked.

 * All tag names are assumed to be unique and do not appear in multiple levels

 * in the XML document. Any unexpected tags are ignored. An example is as

 * follows:

 * <p>

 *  <code>

  &lt;DOCUMENT_TAG&gt;<br>      

  &nbsp;&nbsp;&nbsp;&nbsp;&lt;DOCUMENT_TOPICS_TAG&gt;<br> 

  &nbsp;&nbsp;&nbsp;&nbsp &nbsp;&nbsp;&nbsp;&nbsp;&lt;DOCUMENT_TOPIC_TAG&gt;

  Topic 1&lt;/DOCUMENT_TOPIC_TAG&gt; <br> 

   &nbsp; &nbsp;&nbsp;&nbsp &nbsp;&nbsp;&nbsp;&nbsp;<b>....</b><br>     

   &nbsp;&nbsp;&nbsp;&nbsp &nbsp;&nbsp;&nbsp;&nbsp;&lt;DOCUMENT_TOPIC_TAG&gt; Topic N&lt; /DOCUMENT_TOPIC_TAG&gt;

 <br>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/DOCUMENT_TOPICS_TAG&gt; <br> &nbsp;&nbsp;&nbsp;&nbsp;&lt;DOCUMENT_TITLE_TAG&gt;document

  title&lt;/DOCUMENT_TITLE_TAG&gt;<br> &nbsp;&nbsp;&nbsp;&nbsp;&lt;DOCUMENT_CONTENT_TAG&gt;document

 body text&lt;/DOCUMENT_CONTENT_TAG&gt;<br> &lt;/DOCUMENT_TAG&gt;

</code>

 * <p>

 * OUTPUTS: 

 * <ul>

 * <li>A Hashtable where every key is a word in the training corpus, and the

 * correpsonding value is an ID number for the word.

 * <li>A Hashtable where every key is a conversation topic, and the

 * corresponding value is the centroid vector for the topic.

 * </ul>

 * Both of these Hashtables are serialized so they can be reused.

 * <p>

 * @author Julia Cheng (jc424@columbia.edu)

 */

public class Trainer extends DefaultHandler{

	/**

	 * Determines if debug messages should be printed. Defaults to true.

	 */

	private static final boolean VERBOSE = true;

	

	/**

	 * Stores each topic with its set of supporting documents. The key is a

	 * topic, value is a Hashset of TrainingDocuments that correspond to the

	 * topic.

	 */

	private Hashtable htDocsByTopic = new Hashtable();

		

	/**

	 * Tracks document frequency.  The key is the word, value is the number of

	 * documents that contain the word.

	 */

	private Hashtable htDocFreq = new Hashtable();

	

	/**

	 * Keeps track of each word in the training corpus, along with a unique

	 * number.  The Key of the hashtable is a word <i>w</i> in the training

	 * corpus, and the value is an ID number <i>i</i> such that <i>w</i> is the

	 * <i>i</i>th word in the training corpus.

	 */

	private Hashtable htWordOrder = new Hashtable();

	

	/**

	 * Tracks term frequency in the current training doc.

	 */

	private Hashtable htTermFreq = null;

		

	/**

	 * The current training document being processed.

	 */

	private TrainingDocument currDocument = null;

	

	/**

	 * Keeps track of the XML tags that we have seen so far.

	 */

	private Stack tagNames = new Stack();

	

	/**

	 * Keeps track of the topics assigned to the current training document.

	 */

	private ArrayList currDocTopics = null;

	

	/**

	 * Keeps track of character text while parsing an XML document.

	 */

	private StringBuffer characterBuffer = new StringBuffer(); 

	

	/**

	 * Counts how many unique words have been encountered in the training

	 * corups.

	 */

	private int iWordCount = 0;

	

	/**

	 * Counts how many documents have been processed during the training. This

	 * count does not include documents rejected for multiple or no topics.

	 */

	private int iDocumentCount = 0;

	

	/**

	 * Expected name of the XML tag that encloses a training document.

	 */

	private static final String DOCUMENT_TAG = "REUTERS";

	

	/**

	 * Expected name of the XML tag that encloses a document topic.

	 */

	private static final String DOCUMENT_TOPIC_TAG = "D";

	

	/**

	 * Expected name of the XML tag that will enclose zero or more document

	 * topic tags.

	 */

	private static final String DOCUMENT_TOPICS_TAG = "TOPICS"

	

	/**

	 * Expected name of the XML tag that encloses a document title. The title of

	 * the document is used since it should (in theory) be a summary of the

	 * article and contain all the important words.

	 */;

	private static final String DOCUMENT_TITLE_TAG = "TITLE";

	

	/**

	 * Expected name of the XML tag that encloses the document content.

	 */

	private static final String DOCUMENT_CONTENT_TAG = "BODY";

	

	/** 

	 * This is used to strip out non-alpha characters from input text.

	 */

	private Pattern delimiters = Pattern.compile("[^A-Za-z]+");



	/**

	 * Constructor.

	 */

	public Trainer() {

	}

	

	/**

	 * Program logic is all in here. 

	 * 

	 * @throws TrainingException

	 */

	public void train() throws TrainingException {

		Date now = new Date();

		DateFormat df = DateFormat.getDateTimeInstance();

		reportProgress("*** BEGIN TRAINING AT: " + df.format(now) + "****", false);

		

		/*

		 * 1. READ CONFIGURATION FILE FOR NAMES OF INPUT AND OUTPUT FILES.

		 */

		String centroidOutput = null;

		String wordsOutput = null;

		String docFreqOutput = null;

		String numFilesOutput = null;

		String[] inputDocs = null;

		try {

			TrainingIOFilenames ioInfo = new TrainingIOFilenames(TrainingProperties.SAXParserImpl);

			inputDocs = ioInfo.getInputTrainingSet();

			centroidOutput = ioInfo.getOutputCentroidFilename();

			wordsOutput = ioInfo.getOutputWordlistFilename();

			docFreqOutput = ioInfo.getOutputDocFreqFilename();

			ioInfo = null;

		} catch(Exception e) {

			throw new TrainingException(e);

		}

		

		/*

		 * 2. PARSE THE TRAINING DOCS. (parsing logic mostly in the endElement

		 * method)

		 */

		XMLReader reader = TrainingUtils.getSAXParser(TrainingProperties.SAXParserImpl);

		reader.setContentHandler(this);

		reportProgress("reading from directory: " + TrainingProperties.trainingDocsDirectory, false);

		for(int i=0;i<inputDocs.length;i++) {

			reportProgress("Parsing training document #" + i, true);

			try {

				reader.parse(TrainingProperties.trainingDocsDirectory + inputDocs[i]);

			} catch (Exception e) {

				reportProgress(e.toString(), false);

				continue;

			}

		} 

		reader = null;

		

		/*

		 * 2a. Turn Document frequency count into inverse document count 

		 * (idf = Math.log(iDocumentCount / docFreq) )

		 */

		Enumeration enumDocFreq = htDocFreq.keys();

		while(enumDocFreq.hasMoreElements()) {

			String word = (String)enumDocFreq.nextElement();

			double docFreq = ((Integer) htDocFreq.get(word)).doubleValue();

			docFreq = Math.log(iDocumentCount / docFreq);

			htDocFreq.put(word, new Double(docFreq));

		}



		/*

		 * 3. TURN THE RESULT OF PARSING INTO CENTROID VECTORS

		 */

		try {

			String wordsInTrainingCorpus[] = new String[htWordOrder.size()];

			htWordOrder.keySet().toArray(wordsInTrainingCorpus);

			

			//	M A K E   C E N T R O I D   V E C T O R S 

			Hashtable htCentroids = new Hashtable();

			Enumeration enumTopics = htDocsByTopic.keys();

			while(enumTopics.hasMoreElements()) {

				String currTopic = (String)enumTopics.nextElement();

				reportProgress("\nbegin creating centroid: " + currTopic, false);

				HashSet hsDocs = (HashSet)htDocsByTopic.get(currTopic);

				if(hsDocs.size() == 0) {

					continue;

				}

				TrainingDocument[] docs = new TrainingDocument[hsDocs.size()];

				hsDocs.toArray(docs);

				

				double centroidContent[] = new double[wordsInTrainingCorpus.length];

				for(int i=0;i<docs.length;i++){

					reportProgress("documents: " + i, true);

					//	T U R N   E A C H   D O C U M E N T   T O   V E C T O R  

					TrainingDocument currDoc = docs[i];

					double tfidf[] = new double[wordsInTrainingCorpus.length];

					for(int j=0;j<tfidf.length;j++) {

						String word = wordsInTrainingCorpus[j];

						int position = ((Integer)htWordOrder.get(word)).intValue();

						Integer termFreqInteger = (Integer)currDoc.getTermFreq().get(word);

						if(termFreqInteger == null) {

						tfidf[j] = 0;

						} else {

						double termFreq = termFreqInteger.doubleValue();

						double inverseDocFreq = ((Double)htDocFreq.get(word)).doubleValue();

						tfidf[j] = termFreq * inverseDocFreq;

						}

						if(Double.isNaN(tfidf[j])) {

						debug("INVALID NUM");

						}

					}



					// Normalize vector length

					double length = VectorMath.length(tfidf);

					// A VECTOR of length 0 means the document doesn't have any usuable words. Perhaps all words 

					// do not meet the minimum word length? In this case skip the document. 

					if(length == 0) {

						continue;

					}

					tfidf = VectorMath.scalarDivide(tfidf, length);

					currDoc.setTfidf(tfidf);

					currDoc.setTermFreq(null); // Allow it to be garbage collected.



					VectorMath.add(centroidContent, tfidf);

				}

				

				reportProgress("\nFinished converting supporting docs to supporting vectors for topic " + currTopic, false);

				

				centroidContent = VectorMath.scalarDivide(centroidContent, hsDocs.size());

				// Calculate length of centroid vector:

				/*

				double centroidLen = 0;

				for(int i=0;i<docs.length;i++) {

					for(int j=0;j<docs.length;j++) {

						centroidLen += VectorMath.dotProduct(docs[i].getTfidf(), docs[j].getTfidf());

					}

				}

				centroidLen /= (docs.length * docs.length);

				centroidLen = Math.sqrt(centroidLen);

				*/



				Centroid centroid = new Centroid();

				centroid.centroid = centroidContent;

				//centroid.length = centroidLen;

				centroid.length = VectorMath.length(centroidContent);

				htCentroids.put(currTopic, centroid);

				

				// ALLOW FOR GARBAGE COLLECTION

				for(int i=0;i<docs.length;i++) {

					docs[i] = null;

				}

				// Force garbage collection

				System.gc();

				

			} // end WHILE more topics

			

			/*

			 * 4. OUTPUT TRAINING RESULTS TO FILE

			 */

			serialize(htWordOrder, wordsOutput);

			serialize(htCentroids, centroidOutput);

			serialize(htDocFreq, docFreqOutput);

		} catch (TrainingException e) {

			reportProgress(e.toString(), false);

			reportProgress("\n\n**** ABORTED TRAINING AT " + df.format(new Date()) + "****", false);

			throw e;

		} catch (Exception e) {

			now = new Date();

			reportProgress(e.toString(), false);

			reportProgress("\n\n**** ABORTED TRAINING AT " + df.format(new Date()) + "****", false);

			throw new TrainingException(e);

		}

		reportProgress("\n*** FINISHED TRAINING AT: " + df.format(new Date()) + "****", false);

	}



	/**

	 * Utility function to write an object to file. 

	 * @param obj - the object to be written to file

	 * @param filename - the fullpath name of the file. A FileOutputStream will

	 * be created from this name.

	 * @throws Exception - this may be a file I/O exception.

	 */

	private void serialize(Object obj, String filename) throws Exception {

		FileOutputStream fout = new FileOutputStream(filename);

		ObjectOutputStream oos = new ObjectOutputStream(fout);

		oos.writeObject(obj);

		oos.close();	

	}

	

	/**

	 * Utility function to output a debug messages. Can optionally change

	 * output source to something besides System.out. 

	 * @param msg - text to send to output.

	 * @param inline - if true, don't write a linefeed instead of newline.

	 */

	private void reportProgress(String msg, boolean inline) {

		if(VERBOSE) {

			if(inline) {

				System.out.print("\r" + msg);

			} else {

				System.out.println(msg);

			}

		}

	}

	

	private void debug(String msg) {

		reportProgress(msg, false);

	}

	

	//	*** CONTENT HANDLER SECTION 

	/**

	 * When encounter the start of a new training document, create new objects

	 * to store data about the document.

	 */

	 public void startElement(String namespaceURI, String localName, String qName, Attributes atts)

	 {

		 if(localName.equals(DOCUMENT_TAG)) {

			 currDocTopics = new ArrayList();

			 htTermFreq = new Hashtable();

		 }

		 tagNames.push(localName);

	 }

	

	/**

	 * 

	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)

	 */

	 public void endElement(String namespaceURI, String localName, String qName) 

	 {

		String currElem = (String)tagNames.pop();

		String characterData = characterBuffer.toString().trim();

		

		if(currElem.equals(DOCUMENT_TAG)) {

			// END OF Document. 

			// Reject this document if it doesn't have one and only one topic 

			if(currDocTopics.size() != 1) {

				htTermFreq = null;

				currDocTopics = null;

				currDocument = null;

				return;

			}

			

			// Gather up data into a new Document object.

			String topic = (String)currDocTopics.get(0);

			try {

				currDocument = new TrainingDocument();

			} catch (RuntimeException e) {

				reportProgress("RAN OUT OF MEMORY CREATING NEW TRAINING DOC: " + e.toString(), false);

			}

			currDocument.setTermFreq(htTermFreq);

			iDocumentCount++;

			

			// Get the supporting set of its topic, so we can add the current document into the set.

			HashSet documentSet = (HashSet)htDocsByTopic.get(topic);

			if(documentSet == null) {

				try {

					documentSet = new HashSet();

				} catch (RuntimeException e) {

					reportProgress("RAN OUT OF MEMORY CREATING NEW SET FOR SUPPORTING DOCS: " + e.toString(), false);

				}

			}

			documentSet.add(currDocument);

			htDocsByTopic.put(topic, documentSet);

		 } else if (currElem.equals(DOCUMENT_TOPIC_TAG)) {

			// Just finished reading a document topic. Add the topic to the current document.

			 String parentTag = (String)tagNames.peek();

			 if(DOCUMENT_TOPICS_TAG.equals(parentTag)){

				 currDocTopics.add(characterData);

			 }

		 } else if(currElem.equals(DOCUMENT_TITLE_TAG) || currElem.equals(DOCUMENT_CONTENT_TAG)) {

			 // Reading in data from either the document title or body, assuming that all document topics have been read.

			 // So, at this point we know if this document has more than 1 topic or not.

			 if(currDocTopics.size() != 1) {

			 return;

			 }



			 // Strip out non-alpha characters

			 String words[] = delimiters.split(characterData);

			

			 for(int i=0;i<words.length;i++)

			 {

				 String token = words[i].toLowerCase().trim();

				 if(token.length() < TrainingProperties.MINIMUM_WORD_LENGTH) {

					 continue;

				 }

				 //	***** T E R M    F R E Q U E N C Y ******

				 // Increment the frequency of the word in the current document.

				 Integer termFrequency = (Integer)htTermFreq.get(token);

				 if(termFrequency == null) {

					 /* THIS IS THE FIRST TIME WE SAW THIS WORD IN THE DOCUMENT */

					 termFrequency = new Integer(1);

				     

					 // **** D O C U M E N T   F R E Q U E N C Y ****

					 /* If we have never seen this word before, add it to the list of

					  *  known words, with document frequency = 1. Else increment document 

					  * frequency... BUT ONLY INCREMENT ONCE FOR EACH OCCURANCE IN THE DOCUMENT */

					 /*

					  * This guarantees only 1 increment per document since we

					  * only add to the document frequency count the first time

					  * we see the word in the document.

					  */

					 Integer documentFrequency = (Integer)htDocFreq.get(token);

					 if(documentFrequency == null) {

					 documentFrequency = new Integer(1);

					 /* This is the first time we have seen the word in the entire training corpus. 

					  * Save the word and assign it an ID number.

					  */

					 htWordOrder.put(token, new Integer(iWordCount++));

					 } else {

					 documentFrequency = new Integer(documentFrequency.intValue() + 1);

					 }

					 htDocFreq.put(token, documentFrequency);

				 } else {

					 /* WE HAVE ALREADY SEEN THIS WORD IN THE DOCUMENT. */

					 termFrequency = new Integer(termFrequency.intValue() + 1);

				 }

				 htTermFreq.put(token, termFrequency);

			 }

		 }

		 characterBuffer = new StringBuffer();

	 }

	

	/** 

	 * Store character content. Depending on the SAXParser implementation, the

	 * text may be returned via multiple calls to this method, so just keep the

	 * text for now, and process in the <code>endElement</code> method.

	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)

	 */

	 public void characters(char[] ch, int start, int length) 

	 {

		 //JG253 

		 characterBuffer.append(new String(ch, start, length));

		 // ~JG253

	 }

	 // ///// END CONTENT HANDLER

	

	 public static void main(String args[]) {

		 Trainer trainer = new Trainer();

		 try {

			 trainer.train();

		 } catch (TrainingException e) {

			 System.out.println("TRAINING ERROR: " + e.toString());

			 e.printStackTrace();

		 }

	 }

}

