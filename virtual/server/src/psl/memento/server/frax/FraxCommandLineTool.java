package psl.memento.server.frax;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;

import com.hp.hpl.mesa.rdf.jena.common.*;
import com.hp.hpl.mesa.rdf.jena.common.prettywriter.*;
import com.hp.hpl.mesa.rdf.jena.model.*;
import psl.memento.server.frax.util.MiscUtils;

class FraxCommandLineTool {
  static {
    MiscUtils.configureLogging();
  }
  
  private static final String kWarningCouldNotLoadConfig =
    "Could not load configuration data: ";
  private static final String kWarningCouldNotSynchWithOracle =
    "Could not synchronize with the oracle server: ";
  
  private static Logger sLog = Logger.getLogger("psl.memento.server.frax");
  
  private Frax mFrax;
  private List mURIs;
  private boolean mPrintExtractionTimes;
  private boolean mUseMDCache;
  private boolean mUseOracleForExtractors;
  private boolean mUseOracleForPlugs;
  private boolean mExtractContentMetadata;
  
  public FraxCommandLineTool(String[] iArgs) {
    // quick check
    if (iArgs.length == 0) {     
      printUsage();
    } else {
      setDefaultValues();
      parseCommandLineArgs(iArgs);

      if (mURIs.isEmpty()) {        
        printUsage();
      } else {
        initFrax();

        for (int i = 0, n = mURIs.size(); i < n; i++) {
          extractFrom((String) mURIs.get(i));
        }
      }
    }
  }
  
  private void setDefaultValues() {
    mURIs = new ArrayList();
    mPrintExtractionTimes = false;
    mUseMDCache = false;
    mUseOracleForExtractors = false;
    mUseOracleForPlugs = false;
    mExtractContentMetadata = true;
  }
  
  private void parseCommandLineArgs(String[] iArgs) {
    for (int i = 0; i < iArgs.length; i++) {      
      if (iArgs[i].charAt(0) != '-') {        
        // argument is a URI
        mURIs.add(iArgs[i]);
        continue;
      }
      
      // argument is an option
      if (iArgs[i].equals("-t")) {
        mPrintExtractionTimes = true;
      } else if (iArgs[i].equals("-mdc")) {
        mUseMDCache = true;        
      } else if (iArgs[i].equals("-oe")) {
        mUseOracleForExtractors = true;        
      } else if (iArgs[i].equals("-op")) {
        mUseOracleForPlugs = true;        
      } else if (iArgs[i].equals("-sonly")) {
        mExtractContentMetadata = false;        
      }
    }
  }
  
  private void initFrax() {
    mFrax = Frax.getInstance();
    
    // load configuration data
    try {
      mFrax.setConfiguration(new XMLFraxConfiguration());
    } catch (Exception ex) {      
      sLog.warning(kWarningCouldNotLoadConfig + ex);
    }    
    
    FraxConfiguration config = mFrax.getConfiguration();
    
    // update configuration data
    config.setUseMetadataCache(mUseMDCache);
    config.setUseOracleForExtractors(mUseOracleForExtractors);
    config.setUseOracleForPlugs(mUseOracleForPlugs);
    config.setExtractContentMetadata(mExtractContentMetadata);

    // synchronize with the oracle, if necessary
    if (config.getUseOracleForExtractors() || config.getUseOracleForPlugs()) {
      try {
        mFrax.synchWithOracle();
      } catch (FraxException ex) {
        sLog.warning(kWarningCouldNotSynchWithOracle + ex);
      }
    }
    
    // load persistent RDF model (metadata cache)
    if (config.getUseMetadataCache()) {      
      mFrax.loadPersistentModel();
    }
  }
  
  private void extractFrom(String iURI) {
    try {
      URI uri = new URI(iURI);
      if (uri.getScheme() == null) {
        throw new URISyntaxException(iURI,
          "URI must be absolute (scheme element must be present)");
      }

      FraxConfiguration config = Frax.getInstance().getConfiguration();                

      long startTime = System.currentTimeMillis();

      Model m = Frax.getInstance().extractMetadata(uri,
        config.getExtractContentMetadata(),
        config.getUseOracleForExtractors(),
        config.getUseOracleForPlugs(),
        config.getUseMetadataCache());

      long endTime = System.currentTimeMillis();

      PrettyWriter pw = new PrettyWriter();
      StringWriter sw = new StringWriter();
      pw.write(m, sw, null);
      
      System.out.print(sw.toString());

      if (mPrintExtractionTimes) {
        System.out.println("(Extracted in " +
          (float) (endTime - startTime) / 1000 +
          " seconds.)");
      }
      
      System.out.println();
    } catch (URISyntaxException ex) {
      System.err.println(ex);
    } catch (FraxException ex) {
      System.err.println(ex);
    } catch (RDFException ex) {
      System.err.println(ex);
    }
  }
  
  private void printUsage() {
    System.out.println("Frax - Smart Metadata Extractor - " + Frax.kVersion);
    System.out.println("Programming Systems Lab, Columbia University");
    System.out.println();
    System.out.println("USAGE:");
    System.out.println("java " + Frax.class.getName() + " <options> <URI list>");
    System.out.println();
    System.out.println("<options> can be zero or more of the following:");
    System.out.println("\t-t\tprint extraction times");
    System.out.println("\t-mdc\tuse DBMS to cache metadata");
    System.out.println("\t-oe\tuse oracle to resolve unknown extractors");
    System.out.println("\t-op\tuse oracle to resolve unknown plugs");
    System.out.println("\t-sonly\textract only scheme metadata");
  }
}