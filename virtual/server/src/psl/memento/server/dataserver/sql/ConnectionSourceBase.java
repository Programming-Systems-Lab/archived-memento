package psl.memento.server.dataserver.sql;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class ConnectionSourceBase implements ConnectionSource {
  protected VendorCoupler mVendorCoupler;
  
  protected ConnectionSourceBase(VendorCoupler iVC) {
    mVendorCoupler = iVC;
  }	
  
  public Connection obtainConnection(URI iDbURI) throws SQLException {
    return obtainConnection(mVendorCoupler.getJdbcURL(iDbURI));
  }
}