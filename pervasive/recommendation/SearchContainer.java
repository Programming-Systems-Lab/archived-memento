/*
 * Created on Apr 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package psl.memento.pervasive.recommendation;

/**
 * Container for a Search. This contains all of the information needed to start a Search implementation:
 * o the class of the Search (implementation of Search interface)
 * o the configuration class (extension of the SearchConfiguration class)
 * o path to the xm lconfig for the configuration
 */
public class SearchContainer {

	private Class _class;
	private Class _configclass;
	private String _xmlconfig;

	/**
	 * @param classname
	 * @param xmlconfig
	 */
	public SearchContainer(
		String classname,
		String configclass,
		String xmlconfig) {
		try {
			_class = Class.forName(classname);
			_configclass = Class.forName(configclass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		_xmlconfig = xmlconfig;
	}

	/**
	 * @return the extention of the SearchConfiguration Class for this Search
	 */
	public Class getConfigurationClass() {
		return _configclass;
	}

	/**
	 * @return the path to the xml document for the SearchConfiguration
	 */
	public String getXmlConfig() {
		return _xmlconfig;
	}

	/**
	 * @return the class for the implementation of Search
	 */
	public Class getSearchClass() {
		return _class;
	}

}
