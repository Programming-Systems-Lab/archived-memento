package psl.memento.server.frax;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author  Mark Ayzenshtat
 */
public interface Oracle extends Remote {
  /**
   * Registers this oracle server with the RMI registry on the local host.
   */
  public void rmiRegister() throws FraxException, RemoteException;
  
  /**
   * Retrieves the set of schemes, out of those known to a local instance of
   * Frax, that the oracle server does not know how to handle.
   *
   * @param iLocalSchemes the schemes the oracle server should check
   * @return the set of schemes the oracle does not know how to handle
   */
  public String[] getUnhandledSchemes(String[] iLocalSchemes)
    throws RemoteException;
  
  /**
   * Retrieves the set of content types, out of those known to a local instance
   * of Frax, that the oracle server does not know how to handle.
   *
   * @param iLocalTypes the content types the oracle server should check
   * @return the set of content types the oracle does not know how to handle
   */
  public String[] getUnhandledTypes(String[] iLocalTypes)
    throws RemoteException;

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
  public void registerExtractor(ClassBundle iBundle) throws RemoteException;

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
  public void registerPlug(ClassBundle iBundle) throws RemoteException;
  
  /**
   * Retrieves the fully qualified class name of an extractor that handles
   * the given scheme, if such an extractor is known to the oracle server.
   *
   * @param iScheme the scheme to test
   * @return the class name of an extractor that handles <code>iScheme</code>
   */
  public String getExtractorClassName(String iScheme) throws RemoteException;
  
  /**
   * Retrieves the fully qualified class name of a plug that handles the given
   * type, if such a plug is known to the oracle server.
   *
   * @param iType the type to test
   * @return the class name of a plug that handles <code>iType</code>
   */
  public String getPlugClassName(String iType) throws RemoteException;
  
  /**
   * Retrieves an extractor that handles the given scheme from the oracle
   * server.
   *
   * @param iScheme the scheme for which to obtain an extractor
   * @return the class bundle that contains the byte data for the extractor
   * class, along with any of its dependencies
   */
  public ClassBundle getExtractor(String iScheme) throws RemoteException;

  /**
   * Retrieves a plug that handles the given type from the oracle
   * server.
   *
   * @param iType the type for which to obtain an extractor
   * @return the class bundle that contains the byte data for the plug
   * class, along with any of its dependencies
   */
  public ClassBundle getPlug(String iType) throws RemoteException;
}