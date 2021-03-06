package psl.memento.server.frax;

// jdk imports
import java.io.*;
import java.net.*;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.common.SelectorImpl;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.rdb.*;
import psl.memento.server.dataserver.sql.*;
import psl.memento.server.frax.FraxException;
import psl.memento.server.frax.util.MiscUtils;
import psl.memento.server.frax.vocabulary.ResourceVocab;
import com.hp.hpl.mesa.rdf.jena.model.Statement;

/**
 * This class serves as the core of the Frax public API, and it is the class
 * that should generally be used to extract metadata from a resource, given
 * its URI.
 *
 * @author Mark Ayzenshtat
 */
public final class Frax {
  static {
    MiscUtils.configureLogging();
  }
  
  private static final String kErrorNullConfiguration =
    "Configuration is null.";
  private static final String kErrorNoConfigurationSet =
    "Configuration must be set with a call to setConfiguration prior to " +
    "calling this method.";
  private static final String kErrorNullURI = "URI is null";
  private static final String kErrorNoSchemeNameGiven =
    "URI must be absolute (it must specify a scheme).";
  private static final String kErrorNoExtractorObjectFound =
    "No appropriate extractor object could be found to match the scheme: ";
  private static final String kErrorCreatingResource =
    "Error creating RDF Resource object.";
  private static final String kErrorCouldNotInstantiateCoupler =
    "Could not instantiate vendor coupler class: ";
  private static final String kErrorWhileRegisteringExtractor =
    "Error occurred while trying to register extractor: ";
  private static final String kErrorWhileRegisteringPlug =
    "Error occurred while trying to register plug: ";
  private static final String kWarningCouldNotInitMetadataCache =
    "Could not initialize metadata cache: ";
  private static final String kInfoPersistentModelLoaded =
    "Persistent RDF model loaded for metadata cache.";
  
  public static final float kVersion = 2.0f;
  private static Logger sLog = Logger.getLogger("psl.memento.server.frax");
  private static Frax sInstance;
  
  private FraxConfiguration mConfiguration;
  private Oracle mOracle;
  private ConnectionSource mConnSource;
  private Connection mMasterConn;
  private Model mPersistentModel;
  private List mAcquiredDataRecord;
  private BitSet mAcquiredDataIdentities;
  
  public static Frax getInstance() {    
    if (sInstance == null) {
      sInstance = new Frax();
    }
    
    return sInstance;
  }
  
  private Frax() {
    Runtime.getRuntime().addShutdownHook(new FraxShutdownHook());    
  }
  
  private void shutDown() {    
    writeAcquiredDataRecord();
    
    if (mPersistentModel != null) {
      mPersistentModel.close();
    }
    
    if (mConnSource != null && mMasterConn != null) {
      try {
        mConnSource.releaseConnection(mMasterConn);
      } catch (SQLException ex) {
        // do nothing
      }
    }
  }
  
  public void loadPersistentModel() {
    try {
      FraxConfiguration config = getConfiguration();

      String cacheVendor = config.getMetadataCacheVendor();
      String cacheLoc = config.getMetadataCacheLocation();
      String cacheJenaDBType = config.getMetadataCacheJenaDBType();      
      
      if (cacheVendor == null || cacheLoc == null || cacheJenaDBType == null) {
        sLog.warning(kWarningCouldNotInitMetadataCache +
          "Missing configuration data.");
        return;
      }

      Map vendors = config.getDBMSVendorMap();
      String couplerClassName = (String) vendors.get(cacheVendor);
      VendorCoupler vc =
        (VendorCoupler) Class.forName(couplerClassName).newInstance();
      
      mConnSource = new JdbcOneConnectionSource(vc);
      mMasterConn = mConnSource.obtainConnection(vc.getJdbcURL(
        new URI(cacheLoc)));
      DBConnection dbConn = new DBConnection(mMasterConn);
            
      try {
        mPersistentModel = ModelRDB.open(dbConn);
      } catch (RDFException ex) {        
        // database not yet formatted for storing RDF tuples
        mPersistentModel = ModelRDB.create(dbConn, cacheJenaDBType);
      }
      
      sLog.info(kInfoPersistentModelLoaded);
    } catch (SQLException ex) {
      sLog.warning(kWarningCouldNotInitMetadataCache + ex);
    } catch (URISyntaxException ex) {
      sLog.warning(kWarningCouldNotInitMetadataCache + ex);
    } catch (RDFException ex) {
      sLog.warning(kWarningCouldNotInitMetadataCache + ex);      
    } catch (InstantiationException ex) {
      sLog.warning(kWarningCouldNotInitMetadataCache +
        kErrorCouldNotInstantiateCoupler + ex);
    } catch (IllegalAccessException ex) {
      sLog.warning(kWarningCouldNotInitMetadataCache +
        kErrorCouldNotInstantiateCoupler + ex);
    } catch (ClassNotFoundException ex) {
      sLog.warning(kWarningCouldNotInitMetadataCache +
        kErrorCouldNotInstantiateCoupler + ex);
    }
  }

  /**
   * <p>
   * Extracts metadata from the resource at the supplied URI.  Frax extracts
   * scheme-specific metadata using the appropriate <code>Extractor</code>
   * implementation and content-specific metadata using the appropriate
   * <code>Plug</code> implementation.  Extracted metadata is returned as
   * an RDF model object.
   *
   * <p>
   * Frax will not consult the oracle server if it does not recognize the
   * URI scheme or MIME content type of this resource.  Additionally, Frax
   * will return cached metadata for the given resource, if it exists.
   *
   * @param iURI a URI object that specifies the location of the resource --
   * this URI must be absolute (must contain a scheme component)
   * @return the RDF model containing the extracted metadata
   * @exception FraxException if there was an error extracting the metadata
   * @exception NullPointerException if the supplied URI is null
   * @exception IllegalArgumentException if the supplied URI is not absolute
   * @exception IllegalStateException if the FraxConfiguration has not been set
   */
  public Model extractMetadata(URI iURI) throws FraxException {
    return extractMetadata(iURI, true, false, false, true);
  }  

  /**
   * <p>
   * Extracts metadata from the resource at the supplied URI.  Frax extracts
   * scheme-specific metadata using the appropriate <code>Extractor</code>
   * implementation and content-specific metadata using the appropriate
   * <code>Plug</code> implementation.  Extracted metadata is returned as
   * an RDF model object.
   *
   * <p>
   * The user may also specify whether to use the oracle server to resolve
   * unrecognized URI scheme or MIME content types, and whether to return
   * cached metadata for the given resource, if it exists.
   *
   * @param iURI a URI object that specifies the location of the resource --
   * this URI must be absolute (must contain a scheme component)
   * @param iUseOracleForExtractors if <code>true</code>, Frax will consult
   * the oracle server whenever it encounters a URI scheme that it cannot
   * procure an extractor object for locally
   * @param iUseOracleForPlugs if <code>true</code>, Frax will consult
   * the oracle server whenever it encounters either an unknown MIME content
   * type or cannot procure a plug object for a known MIME content type
   * @param iUseCacheIfExists if <code>true</code>, Frax will return metadata
   * cached in the database instead of (re-)retrieving it
   * @return the RDF model containing the extracted metadata
   * @exception FraxException if there was an error extracting the metadata
   * @exception NullPointerException if the supplied URI is null
   * @exception IllegalArgumentException if the supplied URI is not absolute
   * @exception IllegalStateException if the FraxConfiguration has not been set
   */
  public Model extractMetadata(URI iURI, boolean iExtractContentMetadata,
      boolean iUseOracleForExtractors, boolean iUseOracleForPlugs,
      boolean iUseCacheIfExists) throws FraxException {    
    if (iURI == null) {
      throw new NullPointerException(kErrorNullURI);
    }
    
    String scheme = iURI.getScheme();
    if (scheme == null) {
      throw new IllegalArgumentException(kErrorNoSchemeNameGiven);
    }
    
    if (mConfiguration == null) {
      throw new IllegalStateException(kErrorNoConfigurationSet);
    }
    
    Extractor extractor = Extractor.getInstance(scheme);
    
    if (extractor == null) {
      if (iUseOracleForExtractors) {
        // look for extractor on oracle server -- if found,
        // use it, otherwise, throw an exception        
        ClassBundle cb = null;
        
        try {
          cb = mOracle.getExtractor(scheme);
        } catch (RemoteException ex) {
          sLog.warning("Could not access oracle: " + ex);          
          throw new FraxException(kErrorNoExtractorObjectFound + scheme);
        }
        
        if (cb == null) {
          // appropriate extractor not found on oracle server          
          throw new FraxException(kErrorNoExtractorObjectFound + scheme);
        }

        // save the extractor we got from the oracle
        // with the local Frax instance
        boolean success = registerExtractor(cb);
        if (!success) {          
          throw new FraxException(kErrorNoExtractorObjectFound + scheme);
        }

        // now that the extractor has been saved, try retrieving it again
        extractor = Extractor.getInstance(scheme);

        if (extractor == null) {
          // just in case
          sLog.fine("Extractor from oracle was not saved properly.");          
          throw new FraxException(kErrorNoExtractorObjectFound + scheme);
        }        
      } else {        
        throw new FraxException(kErrorNoExtractorObjectFound + scheme);
      }
    }
    
    // create a Resource object
    Resource r = null;
    try {
      if (iUseCacheIfExists && mPersistentModel != null) {
        r = MiscUtils.getResourceIfExists(mPersistentModel, iURI.toString());
        if (r != null) {
          // the resource already exists in the persistent model         
          
          StmtIterator si =
            r.listProperties(ResourceVocab.kStatements.getProperty());
          
          List subjects = new ArrayList(50);
          while (si.hasNext()) {
            subjects.add(((Statement) si.next()).getObject());
          }          
          
          Model m = mPersistentModel.query(
            new MiscUtils.SubjectSelector(subjects));

          return m;
        }
      }
      
      Model rdfModel = new ModelMem();       
      r = rdfModel.getResource(iURI.toString());      
      r.addProperty(ResourceVocab.kExists.getProperty(), true);
    } catch (RDFException ex) {
      throw new FraxException(kErrorCreatingResource, ex);
    }
    
    // extract scheme metadata
    InputStream contentStream = extractor.extractSchemeMetadata(iURI, r);
    
    // extract content metadata, if necessary    
    if (iExtractContentMetadata && contentStream != null) {
      String mimeType = MiscUtils.getMIMEType(r);      
      if (mimeType != null) {
        Plug plug = Plug.getInstance(mimeType);
        
        if (plug == null) {
          if (iUseOracleForPlugs) {
            // look for plug on oracle server -- if found, use it,
            // otherwise do not extract any content metadata
            ClassBundle cb2 = null;

            try {
              cb2 = mOracle.getPlug(mimeType);
              
              if (cb2 != null) {
                // appropriate plug found on oracle server --
                // save it with the local Frax instance
                boolean success = registerPlug(cb2);
                if (success) {                  
                  // now that the plug has been saved, try retrieving it again
                  plug = Plug.getInstance(mimeType);

                  if (plug != null) {
                    plug.extractContentMetadata(contentStream, r);
                  } else {
                    sLog.fine("Plug from oracle was not saved properly: " +
                      mimeType);
                  }
                }
              }
            } catch (RemoteException ex) {
              sLog.warning("Could not access oracle: " + ex);              
            }
          }
        } else {
          plug.extractContentMetadata(contentStream, r);
        }
      }
    }
    
    try {
      Model mCopy = new ModelMem();
      mCopy.add(r.getModel());
      

      for (ResIterator i = mCopy.listSubjects(); i.hasNext(); ) {
        r.addProperty(ResourceVocab.kStatements.getProperty(), i.next());
      }
    } catch (RDFException ex) {
      // do nothing
    }
    
    if (mPersistentModel != null) {
      try {
        // remove relevant properties from the persistent model
        Resource persR =
          MiscUtils.getResourceIfExists(mPersistentModel, iURI.toString());
        if (persR != null) {
          persR.removeProperties();
        }
        
        // copy metadata into the persistent model
        mPersistentModel.add(r.getModel());        
      } catch (RDFException ex) {
        // could not copy metadata into the persistent model
      }
    }

    return r.getModel();
  }
  
  private void addStatementsRecursively(Model iM, Model iDest) {
    try {
      for (StmtIterator i = iM.listStatements(); i.hasNext(); ) {
        try {
          Statement s = i.next();          
          Resource r = s.getResource();
          
          Model m = mPersistentModel.query(new SelectorImpl(r, null,
            (RDFNode) null));
          if (m.size() > 0) {
            iDest.add(m);
            addStatementsRecursively(m, iDest);
          }
        } catch (RDFException ex) {
          // do nothing
        }
      }
    } catch (RDFException ex) {
      // do nothing
    }
  }
  
  /**
   * Retrieves the configuration object associated with this instance of Frax.
   * The configuration object must be set (either explicitly or implicitly
   * through the <code>getInstance</code> method) before this method is called.
   *
   * @return the configuration object
   * @exception IllegalStateException if the configuration object is
   * <code>null</code>
   */
  public FraxConfiguration getConfiguration() {
    if (mConfiguration == null) {
      throw new IllegalStateException(kErrorNoConfigurationSet);
    }
    
    return mConfiguration;
  }
  
  public void setConfiguration(FraxConfiguration iConfiguration) {
    if (iConfiguration == null) {
      throw new NullPointerException(kErrorNullConfiguration);
    }
    
    mConfiguration = iConfiguration;
    
    readAcquiredDataRecord();    
  }
  
  public void synchWithOracle() throws FraxException {
    FraxConfiguration config = getConfiguration();
    mOracle = OracleImpl.resolveOracle();
    
    try {
      String[] schemes = config.getLocalSchemes();
      String[] types = config.getLocalTypes();    
      String[] unhandledSchemes = mOracle.getUnhandledSchemes(schemes);
      String[] unhandledTypes = mOracle.getUnhandledTypes(types);
      
      Set extractors = new HashSet();
      for (int i = 0; i < unhandledSchemes.length; i++) {
        extractors.add(config.getExtractorClass(unhandledSchemes[i]));
      }
      for (Iterator i = extractors.iterator(); i.hasNext(); ) {
        Class c = (Class) i.next();
        mOracle.registerExtractor(ClassBundle.getInstance(
          c, config.getSchemeString(c.getName())));
      }

      Set plugs = new HashSet();
      for (int i = 0; i < unhandledTypes.length; i++) {
        plugs.add(config.getPlugClass(unhandledTypes[i]));
      }
      for (Iterator i = plugs.iterator(); i.hasNext(); ) {
        Class c = (Class) i.next();
        mOracle.registerPlug(ClassBundle.getInstance(
          c, config.getTypeString(c.getName())));
      }
    } catch (RemoteException ex) {
      throw new FraxException("RMI error.", ex);
    }
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
  public boolean registerExtractor(ClassBundle iBundle) {
    try {
      registerCommon(iBundle);
      int index = mAcquiredDataRecord.size() - 1;
      mAcquiredDataIdentities.set(index);
      
      Frax.getInstance().getConfiguration().addExtractor(
        iBundle.getClassName(), iBundle.getLabel(),
        MiscUtils.concatenateDeps(iBundle.getDependenciesNames()));
    } catch (Exception ex) {
      ex.printStackTrace();
      sLog.fine(kErrorWhileRegisteringExtractor + ex);
      return false;
    }
    
    return true;
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
  public boolean registerPlug(ClassBundle iBundle) {
    try {
      registerCommon(iBundle);
      int index = mAcquiredDataRecord.size() - 1;
      mAcquiredDataIdentities.clear(index);
      
      Frax.getInstance().getConfiguration().addPlug(
        iBundle.getClassName(), iBundle.getLabel(),
        MiscUtils.concatenateDeps(iBundle.getDependenciesNames()));
    } catch (Exception ex) {
      sLog.fine(kErrorWhileRegisteringPlug + ex);
      return false;
    }
    
    return true;
  }
  
  private void registerCommon(ClassBundle iBundle) throws Exception {
    String className = iBundle.getClassName();

    String classFileName = className.replace('.', '/') + ".class";
    File acquiredDir = new File("etc/frax/acquired/");
    File classFile = new File(acquiredDir, classFileName);      

    classFile.getParentFile().mkdirs();
    classFile.createNewFile();
    FileOutputStream fos = new FileOutputStream(classFile);
    fos.write(iBundle.getClassByteData());
    fos.close();

    String[] depNames = iBundle.getDependenciesNames();
    byte[][] depData = iBundle.getDependencyByteData();
    for (int i = 0; i < depNames.length; i++) {
      File f = new File(acquiredDir, "deps/" + depNames[i]);
      f.getParentFile().mkdirs();
      f.createNewFile();
      fos = new FileOutputStream(f);
      fos.write(depData[i]);
      fos.close();
    }

    MiscUtils.rebuildClassLoaderPaths();
    mAcquiredDataRecord.add(iBundle);
  }
  
  private void readAcquiredDataRecord() {
    mAcquiredDataRecord = new ArrayList();
    mAcquiredDataIdentities = new BitSet();
    
    ObjectInputStream ois = null;
    int dataRecSize = -1;
    try {
      ois = new ObjectInputStream(
        new FileInputStream("etc/frax/acquired.dat"));
      
      dataRecSize = ois.readInt();
      mAcquiredDataIdentities = (BitSet) ois.readObject();
      
      for (int i = 0; i < dataRecSize; i++) {
        mAcquiredDataRecord.add(ois.readObject());
      }
    } catch (Exception ex) {
      // do nothing
    } finally {
      if (ois != null) {
        try {
          ois.close();
        } catch (IOException ex2) {
          // do nothing
        }
      }
    }
    
    if (dataRecSize < 1) {
      return;
    }
    
    for (int i = 0; i < dataRecSize; i++) {
      ClassBundle c = (ClassBundle) mAcquiredDataRecord.get(i);
      
      if (mAcquiredDataIdentities.get(i)) {
        Frax.getInstance().getConfiguration().addExtractor(
          c.getClassName(), c.getLabel(),
          MiscUtils.concatenateDeps(c.getDependenciesNames()));
      } else {
        Frax.getInstance().getConfiguration().addPlug(
          c.getClassName(), c.getLabel(),
          MiscUtils.concatenateDeps(c.getDependenciesNames()));
      }
    }
  }
  
  private void writeAcquiredDataRecord() {
    if (mAcquiredDataRecord.isEmpty()) {
      return;
    }
    
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(
        new FileOutputStream("etc/frax/acquired.dat"));
      
      oos.writeInt(mAcquiredDataRecord.size());
      oos.writeObject(mAcquiredDataIdentities);
      
      for (int i = 0, n = mAcquiredDataRecord.size(); i < n; i++) {        
        ClassBundle c = (ClassBundle) mAcquiredDataRecord.get(i);
        c.setClassByteData(null);
        c.setDependencyByteData(null);
        oos.writeObject(c);
      }

      oos.flush();
    } catch (Exception ex) {
      // do nothing
    } finally {
      if (oos != null) {
        try {
          oos.close();
        } catch (IOException ex2) {
          // do nothing
        }
      }
    }
  }
  
  public static void main(String[] args) {
    new FraxCommandLineTool(args);
  }
  
  private static class FraxShutdownHook extends Thread {    
    public FraxShutdownHook() {
      super();
    }
    
    public void run() {
      Frax.getInstance().shutDown();
    }
  }  
}