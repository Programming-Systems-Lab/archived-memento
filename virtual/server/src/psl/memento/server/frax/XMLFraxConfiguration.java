package psl.memento.server.frax; 

// jdk imports
import java.io.InputStream;
import java.io.IOException;

// non-jdk imports
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

public class XMLFraxConfiguration extends FraxConfigurationBase
    implements FraxConfiguration {
	private static final String kErrorInputStreamNull =
		"InputStream is null.";

  private static final String kDefaultFileName = "etc/frax/frax-config.xml";  
  
  /** The digester object which interprets the configuration data. */
  private Digester mDigester;

  public XMLFraxConfiguration() throws IOException, SAXException {
    this(kDefaultFileName);
  }
  
  public XMLFraxConfiguration(String iFilename)
      throws IOException, SAXException {		
    this(ClassLoader.getSystemResourceAsStream(iFilename));
	}

	public XMLFraxConfiguration(InputStream iIn)
      throws IOException, SAXException {
    super();

		if (iIn == null) {
			throw new NullPointerException(kErrorInputStreamNull);
    }    

    // create and configure the digester object
    mDigester = new Digester();
    mDigester.push(this);

    // add extractor rules
    mDigester.addCallMethod("frax-config/extractors/extractor",
      "addExtractor", 3);
    mDigester.addCallParam("frax-config/extractors/extractor", 0, "class");
    mDigester.addCallParam("frax-config/extractors/extractor", 1, "schemes");
    mDigester.addCallParam("frax-config/extractors/extractor", 2,
      "dependencies");

    // add plug rules
    mDigester.addCallMethod("frax-config/plugs/plug", "addPlug", 3);
    mDigester.addCallParam("frax-config/plugs/plug", 0, "class");
    mDigester.addCallParam("frax-config/plugs/plug", 1, "types");
    mDigester.addCallParam("frax-config/plugs/plug", 2, "dependencies");

    // add extension map rules
    mDigester.addCallMethod("frax-config/extension-map/map-entry",
      "addExtensionMapping", 2);
    mDigester.addCallParam("frax-config/extension-map/map-entry", 0, "ext");
    mDigester.addCallParam("frax-config/extension-map/map-entry", 1, "mimetype");    

    // add oracle rules
    mDigester.addCallMethod("frax-config/oracle", "setOracleHostAndPort", 2,
      new String[] { "java.lang.String", "java.lang.Integer" });
    mDigester.addCallParam("frax-config/oracle", 0, "host");
    mDigester.addCallParam("frax-config/oracle", 1, "port");
    
    // add DBMS vendor rules
    mDigester.addCallMethod("frax-config/dbms-vendors/vendor",
      "addDBMSVendorMapping", 2);
    mDigester.addCallParam("frax-config/dbms-vendors/vendor", 0, "name");
    mDigester.addCallParam("frax-config/dbms-vendors/vendor", 1, "class");
    
    // add metadata cache rules
    mDigester.addCallMethod("frax-config/metadata-cache",
      "setMetadataCache", 3);
    mDigester.addCallParam("frax-config/metadata-cache", 0, "vendor");
    mDigester.addCallParam("frax-config/metadata-cache", 1, "jena-db-type");
    mDigester.addCallParam("frax-config/metadata-cache", 2, "location");
    
    // parse the XML configuration data
    mDigester.parse(iIn);
	}
}