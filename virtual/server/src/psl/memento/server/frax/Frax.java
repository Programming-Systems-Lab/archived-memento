package psl.memento.server.frax;

// jdk imports
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.*;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.common.SelectorImpl;
import com.hp.hpl.mesa.rdf.jena.mem.ModelMem;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.rdb.*;
import org.apache.commons.logging.*;
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
  private static final String kWarningCouldNotInitMetadataCache =
    "Could not initialize metadata cache: ";
  private static final String kInfoPersistentModelLoaded =
    "Persistent RDF model loaded for metadata cache.";
  
  private static Log sLog = LogFactory.getLog(Frax.class);
  private static Frax sInstance;  
  
  private FraxConfiguration mConfiguration;
  private Oracle mOracle;
  private ConnectionSource mConnSource;
  private Connection mMasterConn;
  private Model mPersistentModel;
  
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
        sLog.warn(kWarningCouldNotInitMetadataCache +
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
      sLog.warn(kWarningCouldNotInitMetadataCache + ex);
    } catch (URISyntaxException ex) {
      sLog.warn(kWarningCouldNotInitMetadataCache + ex);
    } catch (RDFException ex) {
      sLog.warn(kWarningCouldNotInitMetadataCache + ex);      
    } catch (InstantiationException ex) {
      sLog.warn(kWarningCouldNotInitMetadataCache +
        kErrorCouldNotInstantiateCoupler + ex);
    } catch (IllegalAccessException ex) {
      sLog.warn(kWarningCouldNotInitMetadataCache +
        kErrorCouldNotInstantiateCoupler + ex);
    } catch (ClassNotFoundException ex) {
      sLog.warn(kWarningCouldNotInitMetadataCache +
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
    return extractMetadata(iURI, false, false, true);
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
  public Model extractMetadata(URI iURI, boolean iUseOracleForExtractors,
      boolean iUseOracleForPlugs, boolean iUseCacheIfExists)
      throws FraxException {
    
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
        // TODO: look for extractor on oracle server -- if found,
        //       use it, otherwise, throw an exception
        throw new FraxException(kErrorNoExtractorObjectFound + scheme);
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
          
          ////////////////////          
/*          
          Model m = mPersistentModel.query(new SelectorImpl(r, null,
            (RDFNode) null));          

          Model mCopy = new ModelMem();
          mCopy.add(m);          
          addStatementsRecursively(mCopy, m);  
*/          
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
    if (contentStream != null) {
      String mimeType = MiscUtils.getMIMEType(r);      
      if (mimeType != null) {
        Plug plug = Plug.getInstance(mimeType);
        
        if (plug == null) {
          if (iUseOracleForPlugs) {
            // TODO: look for plug on oracle server -- if found, use it,
            //       otherwise do not extract any content metadata
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
/*
      for (StmtIterator i = mCopy.listStatements(); i.hasNext(); ) {
        r.addProperty(ResourceVocab.kStatements.getProperty(), i.next());
      }
*/
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
  }
  
  public void synchWithOracle() throws FraxException {
    FraxConfiguration config = getConfiguration();
    mOracle = OracleImpl.resolveOracle();
    
    try {
      String[] schemes = config.getLocalSchemes();
      String[] types = config.getLocalTypes();    
      String[] unhandledSchemes = mOracle.getUnhandledSchemes(schemes);
      String[] unhandledTypes = mOracle.getUnhandledTypes(types);

      for (int i = 0; i < unhandledSchemes.length; i++) {
        mOracle.registerExtractor(ClassBundle.getInstance(
          config.getExtractorClass(unhandledSchemes[i])));
      }

      for (int i = 0; i < unhandledTypes.length; i++) {
        mOracle.registerPlug(ClassBundle.getInstance(
          config.getPlugClass(unhandledTypes[i])));
      }
      
      mOracle.registerExtractor(ClassBundle.getInstance(config.getExtractorClass("ftp")));
    } catch (RemoteException ex) {
      throw new FraxException("RMI error.", ex);
    }
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