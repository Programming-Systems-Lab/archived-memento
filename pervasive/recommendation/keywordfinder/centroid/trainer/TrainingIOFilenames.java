package psl.memento.pervasive.recommendation.keywordfinder.centroid.trainer;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class reads an XML file containing filenames used for input and output
 * of the training module.  This class must be given the name of a SAXParser
 * implementation.  When the constructor is invoked, the object will instantiate
 * the SAXParser specified in the argument, and use it to read the data file. 
 * <p>
 * After the constructor runs succesfully, the getter methods can be invoked to
 * get the filenames.
 */
public class TrainingIOFilenames extends DefaultHandler {
	private Vector trainingDocs = new Vector();
	private String docs[] = null;
	private String wordslistFilename = null;
	private String centroidFilename = null;
	
	public TrainingIOFilenames(String parserName) throws TrainingException {
		XMLReader reader = TrainingUtils.getSAXParser(parserName);
		reader.setContentHandler(this);
		try {
			reader.parse(TrainingProperties.datafile);
		} catch (Exception e) {
			throw new TrainingException(e);
		}
		
		if(!trainingDocs.isEmpty()) {
			docs = new String[trainingDocs.size()];
			trainingDocs.toArray(docs);	
		}
	}
	
	public String[] getInputTrainingSet()  {
		return docs;
	}
	
	public String getOutputCentroidFilename() {
		return centroidFilename;
	}
	
	public String getOutputWordlistFilename() {
		return wordslistFilename;
	}
	
	/** 
	 * This is used for parsing the XML file containing names of input and
	 * output files for the training module.
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
	public void startElement(String uri, String name, String qName, Attributes atts)
	{
		if(qName.equals("document")) {
			trainingDocs.add(atts.getValue("location"));
		} else if(qName.equals("outputFilenames")) {
			wordslistFilename = atts.getValue("wordlist");
			centroidFilename = atts.getValue("centroidTemplate");
		}
	}
}
