package psl.memento.pervasive.recommendation.keywordfinder.centroid;

import java.io.IOException;
import org.apache.crimson.parser.XMLReaderImpl;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import psl.memento.pervasive.recommendation.KeywordFinderConfiguration;

/**

 * Contains start-up args for Jebara sweeperM c++ code.

 */

public class implXMLContainer extends KeywordFinderConfiguration {

	private Handler h;

	/**
	
	 * Constructor for impXMLContainer.
	
	 */

	public implXMLContainer(String xmlconfigpath) {
		super(xmlconfigpath);
		h = new Handler(xmlconfigpath);
	}


	public class Handler extends DefaultHandler {
		private static final String DATA_FILE_NAMES_TAG = "datafiles";

		private String centroidFilename;

		private String wordslistFileName;

		private String docFreqFilename;

		private String numFilesFilename;

		public Handler(String xmlconfigpath) {

			try {

				XMLReader reader = new XMLReaderImpl();

				try {

					reader.setContentHandler(this);

					reader.parse(xmlconfigpath);

				} catch (IOException e) {

					throw e;

				}

			} catch (Exception e) {

				e.printStackTrace();
				System.exit(1);

			}

		}

		/**
		
		 * Call back function for sax.
		
		 * 
		
		 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
		
		 */

		public void startElement(
			String uri,
			String name,
			String qName,
			Attributes atts) {

			// No need to push element names onto a stack because we're not expecting

			// multiple depth in the file, and all names are unique.

			if (name.equals(DATA_FILE_NAMES_TAG)) {

				wordslistFileName = atts.getValue("wordslist");

				centroidFilename = atts.getValue("centroidfile");

				docFreqFilename = atts.getValue("documentFrequency");

				numFilesFilename = atts.getValue("numDocs");

			}

		}
	}
	/**
	
	 * Returns filename that stores the list of content words.
	
	 * 
	
	 * @return String
	
	 */

	public String getWordslistFileName() {

		return h.wordslistFileName;

	}

	/**
	
	 * Returns filename templates for datafiles of centroid vectors. Each
	
	 * centroid vector data file is stored in the same naming format, with the
	
	 * topic word concatenated at the end.
	
	 */

	public String getCentroidFilename() {

		return h.centroidFilename;

	}

	/**
	
	 * @see XMLContainer#getRootNode()
	
	 */

	public Node getRootNode() {

		return null;

	}

	/**
	
	 * Returns the docFreqFilename.
	
	 * @return String
	
	 */

	public String getDocFreqFilename() {

		return h.docFreqFilename;

	}

	/**
	
	 * Returns the numFilesFilename.
	
	 * @return String
	
	 */

	public String getNumFilesFilename() {

		return h.numFilesFilename;

	}

}
