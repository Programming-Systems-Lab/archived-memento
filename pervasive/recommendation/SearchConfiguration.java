/*
 * Created on Apr 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package psl.memento.pervasive.recommendation;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This is the configuration for a Search algorithm, to be extended for more specifics.
 * Currently gives access to a Document which may be manually parsed.
 */
public class SearchConfiguration {

	// Document Object pointing to the xml document for this configuration
	private Document _document;

	/**
	 * Constructo a configuration from the path to an xml document for the configuration. get a builder, parse and store a Document object
	 * @param xmlconfig path
	 */
	public SearchConfiguration(String xmlconfigpath) {
		Document document = null;
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
	 * @return the Document object for the xml document of this configuration
	 */
	public Document getDocument() {
		return _document;
	}
}
