package psl.memento.pervasive.recommendation.keywordfinder.jebara;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import psl.memento.pervasive.recommendation.KeywordFinderConfiguration;

/**
 * Contains start-up args for Jebara sweeperM c++ code. It extends the general Configuration for KeywordFinders
 */
public class impXMLContainer extends KeywordFinderConfiguration {

	private static final String CONFIG_TAG_NAME = "args";
	private static final String TEST_RESET_FLAG_NAME = "testReset";
	
	private Hashtable _args = new Hashtable();
	private boolean _testReset = false;
	
	/**
	 * Constructor for impXMLContainer.
	 */
	public impXMLContainer(String xmlconfigpath) {
		super(xmlconfigpath); // important call super to set up the XML parser
		
		Element e = _document.getDocumentElement();
		if (!"configuration".equals(e.getTagName())) {
			// This means that the xml file is not for a SuggestionEngineConfiguration, bad
			System.exit(-1); // TODO this may be a bit harsh, thrown an exception?
		}
		
		NodeList children = e.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element c = (Element) n;
				if (CONFIG_TAG_NAME.equals(c.getTagName())) {
					NamedNodeMap nnm = c.getAttributes();
					for (int j = 0; j < nnm.getLength(); j++) {
						Attr att = (Attr) nnm.item(j);
						_args.put(att.getName(), att.getValue());
					}
				} else if(TEST_RESET_FLAG_NAME.equals(c.getTagName())) {
					NamedNodeMap nnm = c.getAttributes();
					for (int j = 0; j < nnm.getLength(); j++) {
						Attr att = (Attr) nnm.item(j);
						if (att.getName().equals("value") && att.getValue().equals("true")) {
							_testReset = true;				
						}
					}
				}
			}
		}
	}
	
	/**
	 * Returns configuration arguments for the C++ implementation of the algorithm
	 * 
	 * @return String
	 */
	public String getInitString()
	{
		StringBuffer sb = new StringBuffer();
		
		ArrayList argslist = Collections.list(_args.keys());
		for(int i=0;i<argslist.size();i++)
		{
			String argName = (String)argslist.get(i);
			sb.append("-");
			sb.append(argName);
			sb.append(" ");
			sb.append(_args.get(argName));
			sb.append(" ");
		}
		
		return sb.toString();
	}
	
	/**
	 * Returns whether or not reset should be supported
	 */
	public boolean getTestReset()
	{
		return _testReset;
	}
}
