package psl.memento.server.dataserver.sql;

import java.net.URI;

public class PostgreSQLVendorCoupler implements VendorCoupler {  
  private static final String kDriverClassName =
    "org.postgresql.Driver";
  
  private static final String kErrorBadDbURI = "Bad db: URI.";  
  
  public String getJdbcURL(URI iDbURI) {
    
    String host = iDbURI.getHost();
    int port = iDbURI.getPort();
    String db = iDbURI.getPath();
    String userInfo = iDbURI.getUserInfo();
    
    if (host == null || db == null) {
      throw new IllegalArgumentException(kErrorBadDbURI);
    }
    
    StringBuffer jdbcURL = new StringBuffer(100);
    jdbcURL.append("jdbc:postgresql://").append(host);
    if (port != -1) {
      jdbcURL.append(':').append(port);      
    }
    jdbcURL.append(db);
    
    if (userInfo != null) {     
      int index = userInfo.indexOf(':');
      
      String user = (index == -1) ? userInfo : userInfo.substring(0, index);
      
      jdbcURL.append("?user=").append(user);
      
      if (index != -1) {
        // append the password, if one exists
        jdbcURL.append("&password=").append((index + 1 >= userInfo.length()) ?
          "" : userInfo.substring(index + 1));
      }
    }
        
    return jdbcURL.toString();
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