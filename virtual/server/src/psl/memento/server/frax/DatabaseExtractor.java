package psl.memento.server.frax;

// jdk imports
import java.io.InputStream;
import java.io.IOException;
import java.net.URI;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.common.SeqImpl;
//import psl.memento.server.dataserver.sql.*;
import psl.memento.server.frax.FraxException;

class DatabaseExtractor extends Extractor {
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
  public InputStream extractSchemeMetadata(URI iURI, Resource iTarget)
      throws FraxException {
    String scheme = iURI.getScheme();
    
    if (scheme.equals("db")) {
      // we need to convert it to a JDBC URL, which is vendor-specific
    }
    
    if (false) throw new FraxException(null);
    
    return null;
  }
}