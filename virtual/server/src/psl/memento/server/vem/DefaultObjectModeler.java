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
public class DefaultObjectModeler implements ObjectModeler {
    
    public void initialize() {
	ResourceFileManager rfm = ResourceFileManager.getInstance();
	ModelerRule rule;
	
	addRuleEndsWith("/", "directory");
	addRuleContains("@", "email");
	addRule(MATCH_EXTENSION, null);
	addRuleMatchAll(null);
	
	setView("directory", rfm.getRF("table.3ds"));
	setView("email", rfm.getRF("letter.3ds"));
	setView("unknown", rfm.getRF("box.3ds"));
    }
    
    
    public RoomObject createRoomObjectView(Object iVocab, String iName) {
	
	iName = preprocess((Property)iVocab, iName);
	
	/* traverse rules */
	Iterator iter = rules.iterator();
	ModelerRule r;
	String type;
	RoomObject ro;
	
	while (iter.hasNext()) {
	    r = (ModelerRule) iter.next();
	    
	    if (r.matches(iName)) {
		type = r.getType();	
		ro = new RoomObject(15, 15, 15);
		ro.type = type;
		
		return ro;
	    }
	}
	
	return null;
    }
    
    private String preprocess(Property prop, String iName) {
	
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
    
    /** Creates a new instance of DefaultObjectModeler */
    public DefaultObjectModeler() {
	rules = new LinkedList();
	initialize();
    }
    
    public void addRule(ModelerRule iRule) {
	rules.addLast(iRule);
    }
    
    public void addRule(String pattern, String type) {
	addRule(new ModelerRule(pattern, type));
    }
    
    public void addRuleContains(String str, String type) {
	addRule(ModelerRule.escape(str), type);
    }
    
    public void addRuleStartsWith(String str, String type) {
	addRule(MATCH_START + ModelerRule.escape(str), type);
    }
    
    public void addRuleEndsWith(String str, String type) {
	addRule(ModelerRule.escape(str) + MATCH_END, type);
    }
    
    public void addRuleMatchAll(String type) {
	addRule(MATCH_ALL, type);
    }
    
    public void setView(String type, ResourceFile file) {
	
    }
    
    private static Property[] properties = {
	HTMLVocab.kLinks.getProperty(),
	HTMLVocab.kImages.getProperty(),
	FileVocab.kContents.getProperty()
    };
    
    private LinkedList rules;
    
    private static final String MATCH_EXTENSION = "\\.[^\\./]+$";
    private static final String MATCH_START = "^";
    private static final String MATCH_END = "$";
    private static final String MATCH_ALL = ".*";
}
