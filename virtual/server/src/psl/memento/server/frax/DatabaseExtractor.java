package psl.memento.server.frax;

// jdk imports
import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.sql.*;
import java.util.*;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.common.*;
import psl.memento.server.dataserver.sql.*;
import psl.memento.server.frax.FraxException;
import psl.memento.server.frax.vocabulary.DatabaseVocab;

class DatabaseExtractor extends Extractor {
  private static final String kErrorBadJdbcURL =
    "Badly formatted JDBC URL: ";
  private static final String kNoSuchCoupler =
    "No vendor coupler registered for JDBC subprotocol: ";
  private static final String kErrorDatabase =
    "Error interacting with database.";  
  private static final String kErrorNoAppropriateVendor =
    "No appropriate DBMS vendor found to handle: ";
  
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
    
    VendorCoupler vc = null;
    ConnectionSource cs = null;
    Connection conn = null;
    
    try {
      Map vendorMap = Frax.getInstance().getConfiguration().getDBMSVendorMap();
      
      if ("db".equals(scheme)) {
        // try all of the registered DBMS vendor couplers
        // until we find one that works        
        for (Iterator i = vendorMap.values().iterator(); i.hasNext(); ) {
          try {
            vc = (VendorCoupler) Class.forName((String) i.next()).newInstance();
            cs = new JdbcOneConnectionSource(vc);
            conn = cs.obtainConnection(iURI);

            // if we made it this far, the connection was obtained
            // successfully, so break out of the loop
            break;          
          } catch (Exception ex) {
            // couldn't resolve the vendor coupler class, instantiate it,
            // or use it to handle the URI -- move on to the next one
            continue;
          }
        }

        if (conn == null) {
          // we iterated through all registered vendors but couldn't find
          // one to handle the URI
          throw new FraxException(kErrorNoAppropriateVendor + iURI);
        }
      } else {  // the scheme is assumed to be "jdbc"        
        // get the JDBC subprotocol
        String uriString = iURI.toString();
        int index = uriString.indexOf(':');
        if (index == -1 || (index + 1) >= uriString.length()) {          
          throw new FraxException(kErrorBadJdbcURL + iURI);
        }
        int index2 = uriString.indexOf(':', index + 1);
        if (index == -1) {          
          throw new FraxException(kErrorBadJdbcURL + iURI);
        }
        String subProtocol = uriString.substring(index + 1, index2);
        String driverClassName = (String) vendorMap.get(subProtocol);
        
        if (driverClassName == null) {          
          throw new FraxException(kNoSuchCoupler + subProtocol);
        }
        
        cs = new JdbcOneConnectionSource(driverClassName);        
        conn = cs.obtainConnection(iURI.toString());
      }
      
      // extract the metadata
      DatabaseMetaData md = conn.getMetaData();

      if ("db".equals(scheme)) {
        StringTokenizer st = new StringTokenizer(iURI.getPath(), "/");
        if (st.countTokens() >= 2) {
          // a table, not just a database, is specified in the URI
          String catalogName = st.nextToken();
          String tableName = st.nextToken();

          ResultSet rs = md.getColumns(catalogName, null, tableName, "%");
          Seq columnsSeq = iTarget.getModel().createSeq();

          while (rs.next()) {
            columnsSeq.add(rs.getString(4));
          }            
          iTarget.addProperty(DatabaseVocab.kColumns.getProperty(), columnsSeq);

          return null;
        }
      }

      iTarget.addProperty(DatabaseVocab.kVendorName.getProperty(),
        md.getDatabaseProductName());
      iTarget.addProperty(DatabaseVocab.kVendorVersion.getProperty(),
        md.getDatabaseProductVersion());
      iTarget.addProperty(DatabaseVocab.kDriverName.getProperty(),
        md.getDriverName());
      iTarget.addProperty(DatabaseVocab.kDriverName.getProperty(),
        md.getDriverVersion());

      ResultSet rs = md.getCatalogs();
      Seq catalogsSeq = iTarget.getModel().createSeq();
      while (rs.next()) {
        String catalogName = rs.getString(1);
        Resource catalog = iTarget.getModel().createResource();
        catalog.addProperty(DatabaseVocab.kCatalogName.getProperty(),
          catalogName);
        ResultSet rs2 = md.getTables(catalogName, null, "%",
          new String[] {"TABLE"});
        Seq tablesSeq = iTarget.getModel().createSeq();
        while (rs2.next()) {
          tablesSeq.add(rs2.getString(3));
        }
        catalog.addProperty(DatabaseVocab.kTables.getProperty(),
          tablesSeq);

        catalogsSeq.addProperty(DatabaseVocab.kCatalog.getProperty(),
          catalog);
      }
      iTarget.addProperty(DatabaseVocab.kCatalogs.getProperty(),
        catalogsSeq);
    } catch (SQLException ex) {
      throw new FraxException(kErrorDatabase, ex);
    } catch (RDFException ex) {
      throw new FraxException(kErrorAddingProperty, ex);
    } finally {
      if (cs != null && conn != null) {
        try {          
          cs.releaseConnection(conn);
        } catch (SQLException ex) {
          // do nothing
        }
      }
    }
    
    return null;
  }
}