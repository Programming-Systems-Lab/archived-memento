package psl.memento.server.dataserver.sql;

import java.net.URI;

/**
 * <p>
 * Allows the data server to call DBMS vendor-specific functionality in a
 * vendor-independent way.  Any such functionality should reside in a concrete
 * implementation of <code>VendorCoupler</code> that is particular to that
 * vendor.
 *
 * @author Mark Ayzenshtat
 */
public interface VendorCoupler {
  /**
   * Converts a vendor-neutral <code>db</code> URI into a vendor-specific JDBC
   * URL.
   *
   * @param iDbURI a URI with the scheme <code>db</code>
   * @return a vendor-specific JDBC URL
   */
  public String getJdbcURL(URI iDbURI);  
  
  /**
   * Returns the fully qualified class name for use with the JDBC driver
   * manager (for JDBC 1).
   *
   * @return the driver class name
   */
  public String getDriverClassName();
}