package psl.memento.pervasive.recommendation;

/**
 * Container for a KeywordFinder. This contains all the information needed to start a KeywordFinder implementation:
 * o the class of the KeywordFinder (implementation of KeywordFinder interface)
 * o the configuration class (extension of the KeywordFinderConfiguration class)
 * o path to the xml config for the configuration
 */
public class KeywordFinderContainer {

	private Class _class;
	private Class _configclass;
	private String _xmlconfig;

	/**
	 * @see psl.memento.pervasive.recommendation.KeywordFinderContainer especially the general class description for details on the parameters
	 */
	public KeywordFinderContainer(
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
	 * @return the extension of the KeywordFinderConfiguration Class for this KeywordFinder
	 */
	public Class getConfigurationClass() {
		return _configclass;
	}

	/**
	 * @return the path to the xml document for the KeywordFinderConfiguration
	 */
	public String getXmlConfig() {
		return _xmlconfig;
	}

	/**
	 * @return the calss for the impelmentation of KeywordFinder
	 */
	public Class getKeywordFinderClass() {
		return _class;
	}
}
