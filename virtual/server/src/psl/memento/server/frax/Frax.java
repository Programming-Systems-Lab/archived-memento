package psl.memento.server.frax;

// jdk imports
import java.io.InputStream;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.*;
import org.apache.commons.logging.*;
import psl.memento.server.frax.FraxException;
import psl.memento.server.frax.util.MiscUtils;
import psl.memento.server.frax.vocabulary.ResourceVocab;

/**
 * This class serves as the core of the Frax public API, and it is the class
 * that should generally be used to extract metadata from a resource, given
 * its URI.
 *
 * @author Mark Ayzenshtat
 */
public final class Frax {
  private static final String kErrorNullConfiguration =
    "Configuration is null.";
  private static final String kNoConfigurationSet =
    "Configuration must be set with a call to setConfiguration prior to " +
    "calling this method.";
  private static final String kErrorNullURI = "URI is null";
  private static final String kErrorNoSchemeNameGiven =
    "URI must be absolute (it must specify a scheme).";
  private static final String kErrorNoExtractorObjectFound =
    "No appropriate extractor object could be found to match the scheme: ";
  private static final String kErrorCreatingResource =
    "Error creating RDF Resource object.";
  private static final String kWarningCouldNotSetDefaultConfig =
    "Could not set the default configuration: ";
  
  private static Log sLog = LogFactory.getLog(Frax.class);
  private static Frax sInstance;  
  
  private FraxConfiguration mConfiguration;
  private Oracle mOracle;
  
  public static Frax getInstance() {    
    if (sInstance == null) {
      sInstance = new Frax();
    }
    
    return sInstance;
  }
  
  private Frax() {    
    try {
      setConfiguration(new XMLFraxConfiguration());      
    } catch (Exception ex) {
      ex.printStackTrace();
      sLog.warn(kWarningCouldNotSetDefaultConfig + ex);
    }
  }

  /**
   * <p>
   * Extracts metadata from the resource at the supplied URI and returns it as
   * an RDF resource object, not consulting the oracle server if it encounters
   * unknown URI schemes or MIME content types.
   *
   * @param iURI a URI object that specifies the location of the resource --
   * this URI must be absolute (must contain a scheme component)
   * @return the RDF resource object containing the extracted metadata
   * @exception FraxException if there was an error extracting the metadata
   * @exception NullPointerException if the supplied URI is null
   * @exception IllegalArgumentException if the supplied URI is not absolute
   * @exception IllegalStateException if the FraxConfiguration has not been set
   */
  public Resource extractMetadata(URI iURI) throws FraxException {
    return extractMetadata(iURI, false, false);
  }  

  /**
   * <p>
   * Extracts metadata from the resource at the supplied URI and returns it as
   * an RDF resource object.
   *
   * @param iURI a URI object that specifies the location of the resource --
   * this URI must be absolute (must contain a scheme component)
   * @param iUseOracleForExtractors if <code>true</code>, Frax will consult
   * the oracle server whenever it encounters a URI scheme that it cannot
   * procure an extractor object for locally
   * @param iUseOracleForPlugs if <code>true</code>, Frax will consult
   * the oracle server whenever it encounters either an unknown MIME content
   * type or cannot procure a plug object for a known MIME content type
   * @return the RDF resource object containing the extracted metadata
   * @exception FraxException if there was an error extracting the metadata
   * @exception NullPointerException if the supplied URI is null
   * @exception IllegalArgumentException if the supplied URI is not absolute
   * @exception IllegalStateException if the FraxConfiguration has not been set
   */
  public Resource extractMetadata(URI iURI, boolean iUseOracleForExtractors,
      boolean iUseOracleForPlugs)
      throws FraxException {
    if (iURI == null) {
      throw new NullPointerException(kErrorNullURI);
    }
    
    String scheme = iURI.getScheme();
    if (scheme == null) {
      throw new IllegalArgumentException(kErrorNoSchemeNameGiven);
    }
    
    if (mConfiguration == null) {
      throw new IllegalStateException(kNoConfigurationSet);
    }
    
    Extractor extractor = Extractor.getInstance(scheme);
    
    if (extractor == null) {
      if (iUseOracleForExtractors) {
        // TODO: look for extractor on oracle server -- if found,
        //       use it, otherwise, throw an exception
        throw new FraxException(kErrorNoExtractorObjectFound + scheme);
      } else {      
        throw new FraxException(kErrorNoExtractorObjectFound + scheme);
      }
    }
    
    // create a Resource object
    Resource r = null;
    try {
      Model rdfModel = new ModelMem();
      r = rdfModel.createResource(iURI.toString());
    } catch (RDFException ex) {
      throw new FraxException(kErrorCreatingResource, ex);
    }
    
    // extract scheme metadata
    InputStream contentStream = extractor.extractSchemeMetadata(iURI, r);
    
    // extract content metadata, if necessary
    if (contentStream != null) {
      String mimeType = MiscUtils.getMIMEType(r);
      if (mimeType != null) {
        Plug plug = Plug.getInstance(mimeType);
        
        if (plug == null) {
          if (iUseOracleForPlugs) {
            // TODO: look for plug on oracle server -- if found, use it,
            //       otherwise do not extract any content metadata
          }
        } else {
          plug.extractContentMetadata(contentStream, r);
        }
      }
    }

    return r;
  }
  
  /**
   * Retrieves the configuration object associated with this instance of Frax.
   * The configuration object must be set (either explicitly or implicitly
   * through the <code>getInstance</code> method) before this method is called.
   *
   * @return the configuration object
   * @exception IllegalStateException if the configuration object is
   * <code>null</code>
   */
  public FraxConfiguration getConfiguration() {
    if (mConfiguration == null) {
      throw new IllegalStateException(kNoConfigurationSet);
    }
    
    return mConfiguration;
  }
  
  public void setConfiguration(FraxConfiguration iConfiguration) {
    if (iConfiguration == null) {
      throw new NullPointerException(kErrorNullConfiguration);
    }
    
    mConfiguration = iConfiguration;
  }
  
  public void synchWithOracle() throws FraxException {
    FraxConfiguration config = getConfiguration();
    mOracle = OracleImpl.resolveOracle();
    
    try {
      String[] schemes = config.getLocalSchemes();
      String[] types = config.getLocalTypes();    
      String[] unhandledSchemes = mOracle.getUnhandledSchemes(schemes);
      String[] unhandledTypes = mOracle.getUnhandledTypes(types);

      for (int i = 0; i < unhandledSchemes.length; i++) {
        mOracle.registerExtractor(ClassBundle.getInstance(
          config.getExtractorClass(unhandledSchemes[i])));
      }

      for (int i = 0; i < unhandledTypes.length; i++) {
        mOracle.registerPlug(ClassBundle.getInstance(
          config.getPlugClass(unhandledTypes[i])));
      }
      
      mOracle.registerExtractor(ClassBundle.getInstance(config.getExtractorClass("db")));
    } catch (RemoteException ex) {
      throw new FraxException("RMI error.", ex);
    }
  }
}