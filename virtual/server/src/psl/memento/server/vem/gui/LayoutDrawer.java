package psl.memento.server.vem.gui;

/*
 * LayoutDrawer.java
 *
 * Created on November 11, 2002, 6:14 PM
 */

import java.awt.*;
import java.util.*;

import psl.memento.server.vem.*;


/**
 *
 * @author  Vladislav
 */
public class LayoutDrawer {
    
    SimpleLayout layout;
    
    // legend info
    Hashtable colorHash = null;
	
    public LayoutDrawer(SimpleLayout iLayout) {
	this.layout = iLayout;
    }
    
    /**
     *	Simply creates a representation of this object onto the passed Graphics
     *	object.
     */
    public void doDrawing(Graphics g) {
	Door d;
	RoomObject ro;
	int i,j, off, len;
	
	Color[] colorArr = {Color.green, Color.blue, Color.cyan, Color.magenta,
	Color.orange, Color.pink, Color.red, Color.yellow};
	colorHash = new Hashtable();
	int nextColor = 0;
	
	// draw grid
	g.setColor(new Color(204, 204, 204));
	if (layout.floorPlan == null)  {
	    g.fillRect(0, 0, layout.rx, layout.ry);
	    g.setColor(Color.black);
	    g.drawRect(0, 0, layout.rx, layout.ry);
	}
	else {
	    g.fillPolygon(layout.floorPlan);
	    g.setColor(Color.black);
	    g.drawPolygon(layout.floorPlan);
	}
	
	// do doors
	g.setColor(Color.black);
	Iterator iter = layout.dr.mDoors.iterator();
	while (iter.hasNext()) {
	    d = (Door) iter.next();
	    off = d.mOffset;
	    len = d.mLength;
	    
	    switch (d.mWall) {
		case 'n':
		    g.fillRoundRect(off, 0, len, layout.delta/2, 5, 5);
		    //occupy(off, 0, len, l.delta/2);
		    break;
		case 's':
		    g.fillRoundRect(off, layout.ry-layout.delta/2, len, layout.delta/2, 5, 5);
		    //occupy(off, l.ry-l.delta, len, l.delta/2);
		    break;
		case 'w':
		    g.fillRoundRect(0, off, layout.delta/2, len, 5, 5);
		    //occupy(0, off, delta/2, len);
		    break;
		case 'e':
		    g.fillRoundRect(layout.rx-layout.delta/2, off, layout.delta/2, len, 5, 5);
		    //occupy(rx-delta, off, delta/2, len);
		    break;
	    }
	}
	
	// draw objects
	ArrayList tmp = new ArrayList();
	Color color;
	tmp.addAll(layout.dr.mObjs);
	tmp.addAll(layout.dr.mFixedObjs);
	iter = tmp.iterator();
	while (iter.hasNext()) {
	    ro = (RoomObject) iter.next();
	    
	    if (!ro.isPlaced()) continue;
	    
	    if (ro.isFixed()) {
		color = Color.white;
	    } else {
		if (!colorHash.containsKey(ro.getType())) {
		    colorHash.put(ro.getType(), colorArr[nextColor]);
		    nextColor = (nextColor + 1) % colorArr.length;
		}
		color = (Color)colorHash.get(ro.getType());
	    }
	    
	    g.setColor(color);
	    g.fill3DRect(ro.getXloc()+1, ro.getYloc()+1, ro.getWidth()-1, ro.getLength()-1, true);
	    g.setColor(Color.black);
	    g.draw3DRect(ro.getXloc(), ro.getYloc(), ro.getWidth(), ro.getLength(), true);
	}
    }
    
    public Hashtable getLegend() {
	return colorHash;
    }
    
    public String getCellSize() {
	    return "[" + layout.delta + ", " + layout.delta + "]";
    }    
    
	public String getRoomSize() {
		return "[" + layout.rx + ", " + layout.ry + "]";
	}
	
	public String getSpaceUtil() {
		return String.valueOf((int)(getSpaceUsageRatio() * 100)) + " %";
	}
	
	public double getSpaceUsageRatio() {
		Iterator iter = layout.dr.mObjs.iterator();
		return (double)(getAreaUsedByObjects(iter)) / (double)(layout.rx*layout.ry);
	}
	
	protected int getAreaUsedByObjects(Iterator iter) {
		int minx = layout.rx, miny = layout.ry, maxx = 0, maxy = 0;
		
		RoomObject ro;
		
		while (iter.hasNext()) {
			ro = (RoomObject) iter.next();
			
			if (ro.getXloc() < minx) minx = ro.getXloc();
			if (ro.getYloc() < miny) miny = ro.getYloc();
			if (ro.getXloc()+ro.getWidth() > maxx) maxx = ro.getXloc()+ro.getWidth();
			if (ro.getYloc()+ro.getLength() > maxy) maxy = ro.getYloc()+ro.getLength();
		}
		
		if (maxx < minx || maxy < miny)
			return 0;
		
		return (maxx - minx) * (maxy - miny);
	}    
}
