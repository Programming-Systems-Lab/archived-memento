package psl.memento.server.frax;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.apache.commons.logging.*;

/**
 *
 * @author  Mark Ayzenshtat
 */
public class OracleImpl extends UnicastRemoteObject implements Oracle {
  private static final String kObjectName = "psl-frax-oracle";
  
  private static final String kErrorCouldntResolveOracle =
    "Could not resolve oracle from RMI registry.";
  private static final String kErrorCouldntRegisterOracle = 
    "Could not put oracle in RMI registry.";
  private static final String kWarningCouldNotSetDefaultConfig =
    "Could not set the default configuration: ";
  
  private static Log sLog = LogFactory.getLog(OracleImpl.class);
  
  private static FraxConfiguration sConfig;
  private static String sRMIString;
  
  static {    
    try {
      Frax.getInstance().setConfiguration(new XMLFraxConfiguration());
    } catch (Exception ex) {      
      sLog.warn(kWarningCouldNotSetDefaultConfig + ex);
    }
    
    sConfig = Frax.getInstance().getConfiguration();
    
    StringBuffer sb = new StringBuffer(50);
    
    sb.append("//").append(sConfig.getOracleHostName()).append(':')
      .append(sConfig.getOraclePort()).append('/').append(kObjectName);
    
    sRMIString = sb.toString();
  }
  
  public static void main(String[] args) {
    try {
      Oracle o = new OracleImpl();
      o.rmiRegister();      
      
      System.out.println("Oracle server registered at: " + sRMIString);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  
  public OracleImpl() throws RemoteException {
    super();
  }
  
  /**
   * Acquires a reference to the oracle server on the shared RMI registry.
   * This method should mainly be called by Frax instances that wish to share
   * a common oracle server.
   *
   * @return a reference to the shared oracle 
   */
  public static Oracle resolveOracle() throws FraxException {
    try {
      return (Oracle) Naming.lookup(sRMIString);
    } catch (Exception ex) {
      throw new FraxException (kErrorCouldntResolveOracle, ex);
    }
  }
  
  /**
   * Registers this oracle server with the RMI registry on the local host.
   * This method should only be called on the virtual machine that the
   * oracle server is running on.
   */
  public void rmiRegister() throws FraxException, RemoteException {    
    try {
      Naming.rebind(sRMIString, this);
    } catch (Exception ex) {
      throw new FraxException (kErrorCouldntRegisterOracle, ex);
    }
  }
  
  /**
   * Retrieves the set of schemes, out of those known to a local instance of
   * Frax, that the oracle server does not know how to handle.
   *
   * @param iLocalSchemes the schemes the oracle server should check
   * @return the set of schemes the oracle does not know how to handle
   */
  public String[] getUnhandledSchemes(String[] iLocalSchemes)
      throws RemoteException {
    FraxConfiguration config = Frax.getInstance().getConfiguration();
    
    String[] temp = new String[iLocalSchemes.length];
    int n = 0;
    for (int i = 0; i < iLocalSchemes.length; i++) {
      if (config.getExtractorClass(iLocalSchemes[i]) == null) {
        temp[n] = iLocalSchemes[i];
        n++;
      }
    }
    
    String[] uSch = new String[n];
    System.arraycopy(temp, 0, uSch, 0, n);
    
    return uSch;
  }
  
  /**
   * Retrieves the set of content types, out of those known to a local instance
   * of Frax, that the oracle server does not know how to handle.
   *
   * @param iLocalTypes the content types the oracle server should check
   * @return the set of content types the oracle does not know how to handle
   */
  public String[] getUnhandledTypes(String[] iLocalTypes)
      throws RemoteException {
    FraxConfiguration config = Frax.getInstance().getConfiguration();
    
    String[] temp = new String[iLocalTypes.length];
    int n = 0;
    for (int i = 0; i < iLocalTypes.length; i++) {
      if (config.getPlugClass(iLocalTypes[i]) == null) {
        temp[n] = iLocalTypes[i];
        n++;
      }
    }
    
    String[] uTyp = new String[n];
    System.arraycopy(temp, 0, uTyp, 0, n);
    
    return uTyp;
  }

  /**
   * Registers a local extractor with the oracle server.  Calling this method
   * will push the byte data for the extractor class, along with any of its
   * dependencies, to the oracle server.  Since this can potentially add up
   * to a lot of data, call <code>getUnhandledSchemes</code> first to narrow
   * down the number of class bundles you need to push.
   *
   * @param iBundle the class bundle that contains the byte data for the
   * extractor class, along with any of its dependencies
   */
  public void registerExtractor(ClassBundle iBundle) throws RemoteException {
    try {
      String className = iBundle.getClassName();

      String classFileName = className.replace('.', '/') + ".class";
      URL u = ClassLoader.getSystemResource("etc/frax/acquired/");
      File acquiredDir = new File(new URI(u.toString()));
      File classFile = new File(acquiredDir,  classFileName);      
      classFile.getParentFile().mkdirs();
      classFile.createNewFile();
      FileOutputStream fos = new FileOutputStream(classFile);
      fos.write(iBundle.getClassByteData());
      fos.close();

      String[] depNames = iBundle.getDependenciesNames();
      byte[][] depData = iBundle.getDependencyByteData();
      for (int i = 0; i < depNames.length; i++) {
        File f = new File(acquiredDir, depNames[i]);
        f.getParentFile().mkdirs();
        f.createNewFile();
        fos = new FileOutputStream(f);
        fos.write(depData[i]);
        fos.close();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Registers a local plug with the oracle server.  Calling this method
   * will push the byte data for the plug class, along with any of its
   * dependencies, to the oracle server.  Since this can potentially add up
   * to a lot of data, call <code>getUnhandledTypes</code> first to narrow
   * down the number of class bundles you need to push.
   *
   * @param iBundle the class bundle that contains the byte data for the
   * plug class, along with any of its dependencies
   */
  public void registerPlug(ClassBundle iBundle) throws RemoteException {
  }
  
  /**
   * Retrieves the fully qualified class name of an extractor that handles
   * the given scheme, if such an extractor is known to the oracle server.
   *
   * @param iScheme the scheme to test
   * @return the class name of an extractor that handles <code>iScheme</code>
   */
  public String getExtractorClassName(String iScheme) throws RemoteException {
    Class c = Frax.getInstance().getConfiguration().getExtractorClass(iScheme);    
    return (c == null) ? null : c.getName();
  }
  
  /**
   * Retrieves the fully qualified class name of a plug that handles the given
   * type, if such a plug is known to the oracle server.
   *
   * @param iType the type to test
   * @return the class name of a plug that handles <code>iType</code>
   */
  public String getPlugClassName(String iType) throws RemoteException {
    Class c = Frax.getInstance().getConfiguration().getPlugClass(iType);    
    return (c == null) ? null : c.getName();
  }
  
  /**
   * Retrieves an extractor that handles the given scheme from the oracle
   * server.
   *
   * @param iScheme the scheme for which to obtain an extractor
   * @return the class bundle that contains the byte data for the extractor
   * class, along with any of its dependencies
   */
  public ClassBundle getExtractor(String iScheme) throws RemoteException {
    try {
      return ClassBundle.getInstance(Frax.getInstance().getConfiguration()
        .getExtractorClass(iScheme));
    } catch (Exception ex) {
      return null;
    }
  }

  /**
   * Retrieves a plug that handles the given type from the oracle
   * server.
   *
   * @param iType the type for which to obtain an extractor
   * @return the class bundle that contains the byte data for the plug
   * class, along with any of its dependencies
   */
  public ClassBundle getPlug(String iType) throws RemoteException {
    try {
      return ClassBundle.getInstance(Frax.getInstance().getConfiguration()
        .getPlugClass(iType));
    } catch (Exception ex) {
      return null;
    }
  }
}