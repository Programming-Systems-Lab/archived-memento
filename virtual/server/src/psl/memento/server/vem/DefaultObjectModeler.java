/*
 * DefaultObjectModeler.java
 *
 * Created on February 8, 2003, 12:30 PM
 */

package psl.memento.server.vem;

import java.util.*;
import java.net.*;

import psl.memento.server.vem.util.*;
import com.hp.hpl.mesa.rdf.jena.model.*;
import psl.memento.server.frax.vocabulary.*;

/**
 *
 * @author  vlad
 */
public class DefaultObjectModeler extends ObjectModelerBase
    implements ObjectModeler {

    /** Creates a new instance of DefaultObjectModeler */
    public DefaultObjectModeler() {
	initialize();
    }
    
    protected void initialize() {
	defineRules();
	defineViews();
    }
    
    protected void defineRules() {
	addRuleEndsWith("/", "directory");
	addRuleContains("@", "email");
	addRule(MATCH_EXTENSION, null);
	addRuleMatchAll(null);
    }
    
    protected void defineViews() {
	setView("directory", rfm.getRF("violin_case.3ds"));
	setView("email", rfm.getRF("stool.3ds"));
	setView("unknown", rfm.getRF("cube.3ds"));
	setView(".gif", rfm.getRF("cube.3ds"));
	setView(".html", rfm.getRF("stool.3ds"));	
    }
    
    protected String preprocess(Property prop, String iName) {
	
	Property links = HTMLVocab.kLinks.getProperty();
	Property images = HTMLVocab.kImages.getProperty();
	
	if (prop == links || prop == images) {
	    try {
		return new URI(iName).getPath();
	    } catch (URISyntaxException use) {}
	}
	
	return iName;
    }
    
    public Object[] getVocabsToSearch() {
	return properties;
    } 
    
    protected static Property[] properties = {
	HTMLVocab.kLinks.getProperty(),
	HTMLVocab.kImages.getProperty(),
	FileVocab.kContents.getProperty()
    };    
}
