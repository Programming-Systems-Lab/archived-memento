/*
 * ObjectModelerBase.java
 *
 * Created on February 9, 2003, 2:20 AM
 */

package psl.memento.server.vem;

import java.util.*;
import java.net.*;

import psl.memento.server.vem.util.*;
import psl.memento.server.frax.vocabulary.*;
import com.hp.hpl.mesa.rdf.jena.model.*;

/**
 *
 * @author  vlad
 */
public abstract class ObjectModelerBase {
    
    ResourceFileManager rfm = ResourceFileManager.getInstance();    
    
    protected Hashtable viewMap = new Hashtable();    
    protected LinkedList rules = new LinkedList();
    
    protected static final String MATCH_EXTENSION = "\\.[^\\./]+$";
    protected static final String MATCH_START = "^";
    protected static final String MATCH_END = "$";
    protected static final String MATCH_ALL = ".*";
    
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
	viewMap.put(type, file);
    }
    
    public ResourceFile getView(String type) {
	return (ResourceFile) viewMap.get(type);
    }
    
    public RoomObject createRoomObjectView(Object iVocab, String iName) {
	
	iName = preprocess((Property)iVocab, iName);
	
	/* traverse rules */
	Iterator iter = rules.iterator();
	ModelerRule r;
	String type;
	RoomObject ro;
	ModelResourceFile mrf;
	ResourceFile rf;
	
	while (iter.hasNext()) {
	    r = (ModelerRule) iter.next();
	    
	    if (r.matches(iName)) {
		type = r.getType();	
		rf = getView(type);
		
		if (rf != null) {
		    mrf = (ModelResourceFile)rf;
		    /*
		     System.out.println(type + " [" + 
			mrf.getWidth() + ", " + 
			mrf.getLength() + ", " + 
			mrf.getHeight() +"]");	
		     */
		    ro = new RoomObject(
			mrf.getWidth(), mrf.getLength(), mrf.getHeight());
		} else {
		    ro = new RoomObject(15, 15, 15);
		    /* System.out.println(iName); */
		}
	
		ro.type = type;
		return ro;
	    }
	}
	
	return null;
    }   
    
    protected String preprocess(Property prop, String iName)
    {
	return iName;
    }
}
