package psl.memento.server.dataserver.sql;

import java.net.URI;

public class PostgreSQLVendorCoupler implements VendorCoupler {  
  private static final String kDriverClassName =
    "org.postgresql.Driver";
  
  private static final String kErrorBadDbURI = "Bad db: URI.";  
  
  public String getJdbcURL(URI iDbURI) {
    StringBuffer sb = new StringBuffer(100);    
    sb.append("jdbc:postgresql:");
    
    String host = iDbURI.getHost();
    int port = iDbURI.getPort();
    String path = iDbURI.getPath();
    
    if (path == null) {
      throw new IllegalArgumentException(kErrorBadDbURI);
    }
    
    if (path.startsWith("/")) {
      path = path.substring(1);
    }
    
    int index = path.indexOf('/');
    String db = (index == -1) ? path : path.substring(0, index);
    
    if (port == -1) {
      if (host == null) {
        return sb.append(db).toString();
      } else {
        return sb.append("//").append(host).append('/').append(db).toString();
      }
    } else {
      if (host == null) {
        host = "localhost";
      }
      
      return sb.append("//").append(host).append(':').append(port).append('/')
        .append(db).toString();
    }
  }

  /**
   * Returns the fully qualified class name for use with the JDBC driver
   * manager (for JDBC 1).
   *
   * @return the driver class name
   */
  public String getDriverClassName() {
    return kDriverClassName;
  }
}