package psl.memento.pervasive.recommendation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import psl.memento.pervasive.recommendation.exception.GenericException;

/**
 * Configuration for a SuggestionEngine. XML based. 
 * Contains the KeywordFinders and Searches that a SuggestionEngine should load.
 */
public class SuggestionEngineConfiguration {

	// keyword finders
	private ArrayList _keywordFinders = null;

	// searches
	private ArrayList _searches = null;

	/**
	 * Load up a Configuration from the path to an XML document
	 */
	public SuggestionEngineConfiguration(String xmlpath) throws GenericException {

		_keywordFinders = new ArrayList();
		_searches = new ArrayList();

		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// load up builder
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new File(xmlpath));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// make sure the xml is of the correct type
		// TODO use validation perhaps?
		Element e = document.getDocumentElement();
		if (!"SuggestionEngineConfiguration".equals(e.getTagName())) {
			throw new GenericException("SuggestionEngineCOnfiguration xml document is incorrect.");
		}

		NodeList children = e.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element c = (Element) n;
				// keywordfinders
				if ("KeywordFinder".equals(c.getTagName())) {
					String classname = c.getAttribute("classname");
					String configclassname = c.getAttribute("configclassname");
					String xmlconfig = c.getAttribute("xmlconfig");
					KeywordFinderContainer kfc =
						new KeywordFinderContainer(
							classname,
							configclassname,
							xmlconfig);
					_keywordFinders.add(kfc);
					// searches
				} else if ("Search".equals(c.getTagName())) {
					String classname = c.getAttribute("classname");
					String configclassname = c.getAttribute("configclassname");
					String xmlconfig = c.getAttribute("xmlconfig");
					SearchContainer sc =
						new SearchContainer(classname, configclassname, xmlconfig);
					_searches.add(sc);
				}
			}
		}
	}

	/**
	 * @return an Iterator of KeywordFinderContainers for this Configuration
	 */
	public Iterator getKeywordFinders() {
		return _keywordFinders.iterator();
	}

	/**
	 * @return an Iterator of SearchContainers for this Configuration 
	 */
	public Iterator getSearches() {
		return _searches.iterator();
	}
}
