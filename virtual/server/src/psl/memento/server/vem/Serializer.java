/*
 * Created on Jul 24, 2003
 * Modified on $Date: 2003-09-29 16:37:17 $
 */
package psl.memento.server.vem;

import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import memento.world.model.Dimension;
import memento.world.model.LocatableWorldObject;
import memento.world.model.Position;
import memento.world.model.Sector;
import memento.world.model.WorldObject;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * @author vshchogolev
 * @version $Revision: 1.1 $
 */
public class Serializer {

	public static Set ignoredProperties = null;
	public static Set referenceOnly = null;
	public static Set types = null;

	static {
		ignoredProperties = new HashSet();
		ignoredProperties.add("class");
		
		referenceOnly = new HashSet();
		types = new HashSet();
	}
	
	public static void serialize(Sector sector, OutputStream os) throws Exception {

		Element sectorElem = new Element("sector");
		sectorElem.setAttribute("id", sector.getGuid());
		serializeSector(sectorElem, sector);

		Document doc =
			new Document(new Element("message").addContent(sectorElem));

		XMLOutputter outp = new XMLOutputter();

		// Pretty output
		outp.setIndent("\t");
		outp.setNewlines(true);
		outp.output(doc, os);

		os.close();
	}
	
	/**
	 * @param sectorElem
	 * @param sector
	 * @param set
	 */
	private static void serializeSector(Element sectorElem, Sector sector) {
		
		Enumeration contents = sector.contents();
		WorldObject obj;
		
		serializeSize(sectorElem, sector.getSize());
		
		Element contentsElem = new Element("contents");
		sectorElem.addContent(contentsElem);
		
		while (contents.hasMoreElements()) {
			obj = (WorldObject) contents.nextElement();
			
			Element objElem = new Element("world-object");
			objElem.setAttribute("id", obj.getGuid());
			
			serializeWorldObject(objElem, obj);
			contentsElem.addContent(objElem);
		}
		
	}

	/**
	 * @param sectorElem
	 * @param dimension
	 */
	private static void serializeSize(Element objectElem, Dimension dimension) {
		Element s = new Element("size");
		s.setAttribute("width", String.valueOf(dimension.getWidth()));
		s.setAttribute("length", String.valueOf(dimension.getLength()));
		s.setAttribute("height", String.valueOf(dimension.getHeight()));
		objectElem.addContent(s);
	}

	/**
	 * @param element
	 * @param obj
	 */
	private static void serializeWorldObject(Element element, WorldObject obj) {
		serializeSize(element, obj.getSize());
		if (obj instanceof LocatableWorldObject) {
			LocatableWorldObject lwo = (LocatableWorldObject) obj;
			serializePosition(element, lwo.getPosition());
			
			element.setAttribute("name", lwo.getName());
			element.setAttribute("model", lwo.getModel());	
		}
	}

	/**
	 * @param element
	 * @param position
	 */
	private static void serializePosition(Element element, Position position) {
		Element s = new Element("posn");
		s.setAttribute("x", String.valueOf(position.getX()));
		s.setAttribute("y", String.valueOf(position.getY()));
		s.setAttribute("z", String.valueOf(position.getZ()));
		element.addContent(s);	
	}
	
	



}