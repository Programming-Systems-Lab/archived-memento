package psl.memento.server.dataserver.sql;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class ConnectionSourceBase implements ConnectionSource {
  private static final String kErrorNoVC =
    "VendorCoupler reference necessary for handling DB URIs.";
  
  protected VendorCoupler mVendorCoupler;
  
  protected ConnectionSourceBase(VendorCoupler iVC) {
    mVendorCoupler = iVC;
  }	
  
  public Connection obtainConnection(URI iDbURI) throws SQLException {
    if (mVendorCoupler == null) {
      throw new IllegalStateException(kErrorNoVC);
    }
    
    return obtainConnection(mVendorCoupler.getJdbcURL(iDbURI));
  }
}