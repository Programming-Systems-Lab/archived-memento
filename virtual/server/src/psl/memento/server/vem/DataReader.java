package psl.memento.server.vem;

/*
 * DataReader.java
 *
 * Created on November 6, 2002, 7:09 PM
 */

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

import com.hp.hpl.mesa.rdf.jena.model.Container;
import com.hp.hpl.mesa.rdf.jena.model.Model;
import com.hp.hpl.mesa.rdf.jena.model.NodeIterator;
import com.hp.hpl.mesa.rdf.jena.model.Property;
import com.hp.hpl.mesa.rdf.jena.model.RDFNode;

/**
 *
 * @author  vlad
 */
public class DataReader {
    
    public static final int DEFAULT_ROOM_HEIGHT = 100;
    
    public Room mRoom;
    public ArrayList mObjs, mFixedObjs, mDoors;
    
    public DataReader() {
	// initialize
	mObjs = new ArrayList();
	mFixedObjs = new ArrayList();
	mDoors = new ArrayList();
    }
    
    public void reset() {
	mObjs.clear();
	mFixedObjs.clear();
	mDoors.clear();
    }
    
    public void setRoom(Polygon poly, int dz) {
	Rectangle b = poly.getBounds();
	mRoom = new Room(b.width, b.height, DEFAULT_ROOM_HEIGHT);
	mRoom.setPlan(poly);
    }
    
    public void setRoom(int dx, int dy, int dz) {
	mRoom = new Room(dx, dy, dz);
    }
    
    public void addRoomObject(RoomObject ro) {
	if (ro.isFixed()) {
	    mFixedObjs.add(ro);
	} else {
	    mObjs.add(ro);
	}
    }
    
    public void addDoor(char direction, int offset, int length) {
	Door d = new Door(direction, offset, length);
	mDoors.add(d);
    }
    
    public boolean getObjectsFromModel(Model m, ObjectModeler om) 
    {	
	RoomObject ro;
	Property prop;
	NodeIterator iter;
	String name;

	Property properties[] = (Property[])om.getVocabsToSearch();
	
	try {
	    for (int i=0; i<properties.length; i++) {
		prop = properties[i];

		iter = m.listObjectsOfProperty(prop);
		if (!iter.hasNext())
		    continue;

		RDFNode n = iter.next();
		Container c = (Container)n;
		iter = c.iterator();

		while (iter.hasNext()) {
		    name = iter.next().toString();
		    
		    ro = om.createRoomObjectView(prop, name);
		    addRoomObject(ro);
		}
	    }
	    
	    return true;
	    
	} catch (Exception e) {
	    System.out.println(e);
	    e.printStackTrace();
	    return false;
	}
    }
    
}


