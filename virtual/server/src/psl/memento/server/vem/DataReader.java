package psl.memento.server.vem;

/*
 * DataReader.java
 *
 * Created on November 6, 2002, 7:09 PM
 */

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import psl.memento.server.frax.vocabulary.Vocab;

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

	private Room mRoom;
	private List mObjs;
	private List mFixedObjs;
	private List mDoors;

	public DataReader() {
		// initialize
		setObjs(new ArrayList());
		setFixedObjs(new ArrayList());
		setDoors(new ArrayList());
	}

	public void reset() {
		getObjs().clear();
		getFixedObjs().clear();
		getDoors().clear();
	}

	public void setRoom(Polygon poly, int dz) {
		Rectangle b = poly.getBounds();
		setRoom(new Room(b.width, b.height, DEFAULT_ROOM_HEIGHT));
		getRoom().setPlan(poly);
	}

	public void setRoom(int dx, int dy, int dz) {
		setRoom(new Room(dx, dy, dz));
	}

	public void addRoomObject(RoomObject ro) {
		if (ro.isFixed()) {
			getFixedObjs().add(ro);
		} else {
			getObjs().add(ro);
		}
	}

	public void addDoor(char direction, int offset, int length) {
		Door d = new Door(direction, offset, length);
		getDoors().add(d);
	}

	public boolean getObjectsFromModel(Model m, ObjectModeler om) {
		RoomObject ro;
		Property prop;
		NodeIterator iter;
		String name;

		Vocab vocabs[] = (Vocab[]) om.getVocabsToSearch();

		try {
			for (int i = 0; i < vocabs.length; i++) {
				prop = vocabs[i].getProperty();

				iter = m.listObjectsOfProperty(prop);
				if (!iter.hasNext())
					continue;

				RDFNode n = iter.next();
				Container c = (Container) n;
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

	public void setDoors(List doors) {
		mDoors = doors;
	}

	public List getDoors() {
		return mDoors;
	}

	public void setFixedObjs(List fixedObjs) {
		mFixedObjs = fixedObjs;
	}

	public List getFixedObjs() {
		return mFixedObjs;
	}

	public void setObjs(List objs) {
		mObjs = objs;
	}

	public List getObjs() {
		return mObjs;
	}

	public void setRoom(Room room) {
		mRoom = room;
	}

	public Room getRoom() {
		return mRoom;
	}

}
