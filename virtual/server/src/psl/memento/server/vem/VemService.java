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
 *
 * @author  vlad
 */
public class VemService {
    
    /** Creates a new instance of VemService */
    public VemService() {

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
	
	o.setX(ro.xloc);
	o.setY(ro.yloc);
	o.setZ(ro.zloc);
	o.setWidth(ro.width);
	o.setHeight(ro.height);
	o.setLength(ro.length);
	
	return o;
    }
    
}
