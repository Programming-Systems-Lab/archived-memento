package psl.memento.pervasive.recommendation.keywordfinder.centroid.trainer;

import org.xml.sax.XMLReader;

public class TrainingUtils {

	private TrainingUtils() {
	}

	public static XMLReader getSAXParser(String parserName) throws TrainingException {
		try {
			Class c = Class.forName(parserName);
			XMLReader reader = (XMLReader) c.newInstance();
			return reader;
		} catch (Exception e) {
			throw new TrainingException("Could not instantiate the SAXParser", e);
		} 
	}
}
