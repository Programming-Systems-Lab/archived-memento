/*
 * VemService.java
 *
 * Created on February 8, 2003, 8:50 PM
 */

package psl.memento.server.vem;

import java.util.Iterator;

import psl.memento.server.world.view.*;
import com.hp.hpl.mesa.rdf.jena.model.Model;

/**
 *  FIXME: only single threaded will work
 *
 * @author  vlad
 */
public class VemService {
    
    protected final static ObjectModeler kDefaultObjectModeler =
	new DefaultObjectModeler();
    
    protected final static Layout kDefaultLayout =
	new AdvancedLayout();
 
    public SectorView createSectorView(Model m) {
	return createSectorView(m, kDefaultObjectModeler, kDefaultLayout);
    }
    
    public SectorView createSectorView(Model m, Layout layout) {
	return createSectorView(m, kDefaultObjectModeler, layout);
    }
    
    public SectorView createSectorView(Model m, ObjectModeler om) {
	return createSectorView(m, om, kDefaultLayout);
    }
    
    public SectorView createSectorView(Model m, ObjectModeler om, Layout layout)
    {
	DataReader reader = new DataReader();	
	SectorView view = new SectorView();
	
	reader.setRoom(300, 300, 300);
	reader.getObjectsFromModel(m, om);
	
	view.setWidth(reader.mRoom.getWidth());
	view.setLength(reader.mRoom.getLength());
	view.setHeight(reader.mRoom.getHeight());
	
	layout.doLayout(reader);
	
	Iterator iter = reader.mObjs.iterator();
	RoomObject ro;
	
	while (iter.hasNext()) {
	    ro = (RoomObject) iter.next();
	    view.add(createView(ro));
	}

	return view;
    }
    
    public LocatableWorldObjectView createView(RoomObject ro) 
    {
	LocatableWorldObjectView o = new LocatableWorldObjectView();
	
	o.setX(ro.getXloc());
	o.setY(ro.getYloc());
	o.setZ(ro.getZloc());
	o.setWidth(ro.getWidth());
	o.setHeight(ro.getHeight());
	o.setLength(ro.getLength());
	
	return o;
    }
    
}
