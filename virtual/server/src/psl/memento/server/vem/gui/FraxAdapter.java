/*
 * FraxAdapter.java
 *
 * Created on February 9, 2003, 12:38 AM
 */

package psl.memento.server.vem.gui;

import java.net.URI;

import psl.memento.server.frax.Frax;
import psl.memento.server.frax.FraxConfiguration;
import psl.memento.server.frax.XMLFraxConfiguration;

import com.hp.hpl.mesa.rdf.jena.model.Model;

/**
 *
 * @author  vlad
 */
public class FraxAdapter {
    
    private static Frax mFrax = null;
    
    public Model getModel(String uri) 
    {
	try {
	    return getFrax().extractMetadata(new URI(uri));
	} catch (Exception e) {
	    System.out.println(e);
	    e.printStackTrace();
	    return null;
	}
    }
    
    private Frax getFrax() {
	if (mFrax == null) {
	    initFrax();
	}
	return mFrax;
    }    
    
    private void initFrax() {
	mFrax = Frax.getInstance();
	
	// load configuration data
	try {
	    mFrax.setConfiguration(new XMLFraxConfiguration());
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.exit(1);
	}
	
	FraxConfiguration config = mFrax.getConfiguration();
	config.setExtractContentMetadata(true);
    }
}
