package psl.memento.server.frax.vocabulary;

import com.hp.hpl.mesa.rdf.jena.model.Property;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;

public class DatabaseVocab implements Vocab {
	private static final String kNamespace =
    "http://www.psl.cs.columbia.edu/frax-rdf-database/1.0#";

  private Property mProperty;
  
  public static final DatabaseVocab kVendorName =
    new DatabaseVocab(kNamespace, "vendorName");
  public static final DatabaseVocab kVendorVersion =
    new DatabaseVocab(kNamespace, "vendorVersion");
  public static final DatabaseVocab kDriverName =
    new DatabaseVocab(kNamespace, "driverName");
  public static final DatabaseVocab kDriverVersion =
    new DatabaseVocab(kNamespace, "driverVersion");
  public static final DatabaseVocab kCatalogs =
    new DatabaseVocab(kNamespace, "catalogs");
  public static final DatabaseVocab kCatalog =
    new DatabaseVocab(kNamespace, "catalog");
  public static final DatabaseVocab kCatalogName =
    new DatabaseVocab(kNamespace, "catalogName");
  public static final DatabaseVocab kTables =
    new DatabaseVocab(kNamespace, "tables");
  public static final DatabaseVocab kColumns =
    new DatabaseVocab(kNamespace, "columns");
  
  private DatabaseVocab(String iNamespace, String iPropertyName) {
    try {
      mProperty = new PropertyImpl(iNamespace, iPropertyName);
    } catch (RDFException ex) {
      throw new RuntimeException("Could not create property: " +
        iPropertyName, ex);
    }
  }
  
  public Property getProperty() {
    return mProperty;
  }
}