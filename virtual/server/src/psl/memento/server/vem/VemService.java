/*
 * VemService.java
 *
 * Created on February 8, 2003, 8:50 PM
 */

package psl.memento.server.vem;

import java.util.HashMap;
import java.util.Iterator;

import memento.world.model.Dimension;
import memento.world.model.LocatableWorldObject;
import memento.world.model.Position;
import memento.world.model.Sector;
import memento.world.model.WorldModel;

import psl.memento.server.vem.layout.*;
import psl.memento.server.vem.modeler.*;

import com.hp.hpl.mesa.rdf.jena.model.Model;

/**
 *  FIXME: only single threaded will work
 *
 * @author  vlad
 */
public class VemService {

	protected final static ObjectModeler kDefaultObjectModeler =
		new DefaultObjectModeler();

	protected final static Layout kDefaultLayout = new AdvancedLayout();

	protected WorldModel worldModel;

	// --------------------------------------------- Constructor

	public VemService(WorldModel worldModel) {
		this.worldModel = worldModel;
	}

	// --------------------------------------------- Public Methods

	public Sector createSector(Model m) {
		return createSector(m, kDefaultObjectModeler, kDefaultLayout);
	}

	public Sector createSector(Model m, Layout layout) {
		return createSector(m, kDefaultObjectModeler, layout);
	}

	public Sector createSector(Model m, ObjectModeler om) {
		return createSector(m, om, kDefaultLayout);
	}

	public Sector createSector(Model m, ObjectModeler om, Layout layout) {
		DataReader data = new DataReader();
		Sector sector = (Sector) worldModel.create(Sector.class, new HashMap());

		data.setRoom(300, 300, 300);
		data.getObjectsFromModel(m, om);

		Dimension size =
			new Dimension(
				data.getRoom().getWidth(),
				data.getRoom().getHeight(),
				data.getRoom().getLength());
		sector.setSize(size);

		layout.doLayout(data);

		Iterator iter = data.getObjs().iterator();
		RoomObject ro;

		while (iter.hasNext()) {
			ro = (RoomObject) iter.next();
			sector.add(createObject(ro));
		}

		return sector;
	}

	public LocatableWorldObject createObject(RoomObject ro) {
		LocatableWorldObject o =
			(LocatableWorldObject) worldModel.create(
				LocatableWorldObject.class,
				new HashMap());

		o.setPosition(new Position(ro.getXloc(), ro.getYloc(), ro.getZloc()));

		Dimension d =
			new Dimension(ro.getWidth(), ro.getHeight(), ro.getLength());
		o.setSize(d);

		o.setName(ro.getName());
		o.setModel(ro.getModel());

		return o;
	}

}
