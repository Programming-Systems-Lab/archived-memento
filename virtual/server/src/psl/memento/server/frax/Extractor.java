package psl.memento.server.frax;

// jdk imports
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.logging.Logger;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.model.Resource;
import psl.memento.server.frax.FraxException;
import psl.memento.server.frax.util.MiscUtils;

/**
 * An extractor knows how to extract metadata from a resource that is
 * accessible using a particular scheme.
 *
 * @author Mark Ayzenshtat
 */
public abstract class Extractor implements Serializable {
  static {
    MiscUtils.configureLogging();
  }
  
  protected static final String kErrorAddingProperty =
    "Error adding RDF Property object.";
  private static final String kWarningCouldNotInstantiateExtractor = 
    "Could not instantiate extractor class: ";
  
  private static Logger sLog = Logger.getLogger("psl.memento.server.frax");
  
 	protected Extractor() {
    // make direct instantiation by non-subclasses impossible    
  }
  
  /**
   * <p>
   * Obtains an <code>Extractor</code> object that can extract metadata
   * from resources using the given scheme.
   *
   * <p>
   * A list of known schemes is available <a
   * href="http://www.w3.org/Addressing/schemes">here</a>.
   *
   * @param iScheme the name of the scheme for which to obtain an extractor
   * @return an <code>Extractor</code> object or <code>null</code> if no
   * extractor that handles the given scheme can be found
   */
  public static Extractor getInstance(String iScheme) {
    Class extractorClass;    

    extractorClass = Frax.getInstance().getConfiguration()
      .getExtractorClass(iScheme);
    
    if (extractorClass == null) {
      return null;
    }    

    Extractor extractor = null;
    
    try {
      extractor = (Extractor) extractorClass.newInstance();
    } catch (Exception ex) {      
      sLog.warning(kWarningCouldNotInstantiateExtractor + extractorClass);      
    }
    
    return extractor;
  }
  
	/**
   * <p>
   * Extracts scheme metadata from the resource denoted by the given URI.
   * Scheme metadata is considered to be information that can be learned
   * without examining the contents, or actual byte data, of the resource
   * itself.  For example, for the "file" scheme, we can extract file size,
   * date last modified, MIME type, and other properties without knowing
   * anything about the actual file data.
   *
   * <p>
   * This method returns an <code>InputStream</code> object with which we can
   * extract the target resource's byte data with a content plug, or <code>
   * null</code> if either the notion of byte data is meaningless for this
   * type of resource or we cannot procure the <code>InputStream</code>.
   *
   * @param iURI the URI that denotes the resource from which to extract
   * metadata
   * @param iTarget the RDF resource object to which to add the extracted
   * metadata
   * @return an <code>InputStream</code> with which we can extract the
   * resource's byte data, or <code>null</code> if we cannot extract
   * byte data for resources with this scheme
   * @exception FraxException if an error occurred extracting metadata
   */
  public abstract InputStream extractSchemeMetadata(URI iURI, Resource iTarget)
      throws FraxException;
}