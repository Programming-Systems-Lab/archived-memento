package psl.memento.pervasive.recommendation;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A basic configuration for a KeywordFinder, should be extended.
 * This straight forward Configuration is constructor from the path to an XML document with the configuration parameters. 
 * It then provides access to a Document object for this XML document.
 * 
 * Extensions should provide calls with detail immediatly relevant to a parcitular KeywordFinder
 */
public class KeywordFinderConfiguration {

	// Document Object pointing to the xml document for this configuration 
	protected Document _document;

	/**
	 * Construct a configuration from the path to an xml document for the configuration. get a builder, parse and store internally
	 * a Document object
	 */
	public KeywordFinderConfiguration(String xmlconfigpath) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			_document = builder.parse(new File(xmlconfigpath));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the Document object for the xml document for the configuration
	 */
	public Document getDocument() {
		return _document;
	}
}
