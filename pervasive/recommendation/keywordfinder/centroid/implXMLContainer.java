package psl.memento.pervasive.recommendation.keywordfinder.centroid;
import java.io.IOException;

import org.apache.crimson.parser.XMLReaderImpl;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import psl.conversation.XMLContainer;

/**
 * Contains start-up args for Jebara sweeperM c++ code.
 */
public class implXMLContainer extends DefaultHandler 
							implements XMLContainer 
{
	private static final String DATA_FILE_NAMES_TAG = "datafiles";
	
	private String centroidFilename;
	private String wordslistFileName;
	
	/**
	 * Constructor for impXMLContainer.
	 */
	public implXMLContainer() {
		
	}
	
	public void loadData(String configFileName) throws Exception
	{
		try {
			XMLReader reader = new XMLReaderImpl();
			try {
				reader.setContentHandler(this);
				reader.parse(configFileName);
			} catch (IOException e) {
				throw e;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Call back function for sax.
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
	public void startElement(String uri, String name,
								String qName, Attributes atts)
	{
		// No need to push element names onto a stack because we're not expecting
		// multiple depth in the file, and all names are unique.
		if(name.equals(DATA_FILE_NAMES_TAG)) {
			wordslistFileName = atts.getValue("wordslist");
			centroidFilename = atts.getValue("centroidfile");
		}
	}
	
	/**
	 * Returns filename that stores the list of content words.
	 * 
	 * @return String
	 */
	public String getWordslistFileName()
	{
		return wordslistFileName;
	}
	
	/**
	 * Returns filename templates for datafiles of centroid vectors. Each
	 * centroid vector data file is stored in the same naming format, with the
	 * topic word concatenated at the end.
	 */
	public String getCentroidFilename()
	{
		return centroidFilename;
	}
	
	/**
	 * @see XMLContainer#getRootNode()
	 */
	public Node getRootNode() {
		return null;
	}

}
