/*
 * ObjectModelerBase.java
 *
 * Created on February 9, 2003, 2:20 AM
 */

package psl.memento.server.vem;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import psl.memento.server.vem.util.ModelResourceFile;
import psl.memento.server.vem.util.ResourceFile;
import psl.memento.server.vem.util.ResourceFileManager;

import com.hp.hpl.mesa.rdf.jena.model.Property;

/**
 *
 * @author  vlad
 */
public abstract class ObjectModelerBase {

	protected ResourceFileManager rfm = ResourceFileManager.getInstance();

	protected Hashtable viewMap;
	protected LinkedList rules;

	protected static final String MATCH_EXTENSION = "\\.[^\\./]+$";
	protected static final String MATCH_START = "^";
	protected static final String MATCH_END = "$";
	protected static final String MATCH_ALL = ".*";

	public ObjectModelerBase() {
		viewMap = new Hashtable();
		rules = new LinkedList();
	}

	protected void addRule(ModelerRule iRule) {
		rules.addLast(iRule);
	}

	protected void addRule(String pattern, String type) {
		addRule(new ModelerRule(pattern, type));
	}

	protected void addRuleContains(String str, String type) {
		addRule(ModelerRule.escape(str), type);
	}

	protected void addRuleStartsWith(String str, String type) {
		addRule(MATCH_START + ModelerRule.escape(str), type);
	}

	protected void addRuleEndsWith(String str, String type) {
		addRule(ModelerRule.escape(str) + MATCH_END, type);
	}

	protected void addRuleMatchAll(String type) {
		addRule(MATCH_ALL, type);
	}

	protected void setView(String type, ResourceFile file) {
		viewMap.put(type, file);
	}

	protected ResourceFile getView(String type) {
		return (ResourceFile) viewMap.get(type);
	}

	public RoomObject createRoomObjectView(Object iVocab, String iName) {

		iName = preprocess((Property) iVocab, iName);

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
					mrf = (ModelResourceFile) rf;
					
					 System.out.println(type + " [" + 
					mrf.getWidth() + ", " + 
					mrf.getLength() + ", " + 
					mrf.getHeight() +"]");	
					
					ro =
						new RoomObject(
							mrf.getWidth(),
							mrf.getLength(),
							mrf.getHeight());
				} else {
					ro = new RoomObject(15, 15, 15);
					/* System.out.println(iName); */
				}

				ro.setType(type);
				return ro;
			}
		}

		return null;
	}

	protected String preprocess(Property prop, String iName) {
		return iName;
	}
}
