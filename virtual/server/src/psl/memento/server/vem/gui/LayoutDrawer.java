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
    
    SimpleLayout l;
    
    // legend info
    Hashtable colorHash = null;
	
    public LayoutDrawer(SimpleLayout iLayout) {
	this.l = iLayout;
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
	if (l.floorPlan == null)  {
	    g.fillRect(0, 0, l.rx, l.ry);
	    g.setColor(Color.black);
	    g.drawRect(0, 0, l.rx, l.ry);
	}
	else {
	    g.fillPolygon(l.floorPlan);
	    g.setColor(Color.black);
	    g.drawPolygon(l.floorPlan);
	}
	
	// do doors
	g.setColor(Color.black);
	Iterator iter = l.dr.mDoors.iterator();
	while (iter.hasNext()) {
	    d = (Door) iter.next();
	    off = d.mOffset;
	    len = d.mLength;
	    
	    switch (d.mWall) {
		case 'n':
		    g.fillRoundRect(off, 0, len, l.delta/2, 5, 5);
		    //occupy(off, 0, len, l.delta/2);
		    break;
		case 's':
		    g.fillRoundRect(off, l.ry-l.delta/2, len, l.delta/2, 5, 5);
		    //occupy(off, l.ry-l.delta, len, l.delta/2);
		    break;
		case 'w':
		    g.fillRoundRect(0, off, l.delta/2, len, 5, 5);
		    //occupy(0, off, delta/2, len);
		    break;
		case 'e':
		    g.fillRoundRect(l.rx-l.delta/2, off, l.delta/2, len, 5, 5);
		    //occupy(rx-delta, off, delta/2, len);
		    break;
	    }
	}
	
	// draw objects
	ArrayList tmp = new ArrayList();
	Color color;
	tmp.addAll(l.dr.mObjs);
	tmp.addAll(l.dr.mFixedObjs);
	iter = tmp.iterator();
	while (iter.hasNext()) {
	    ro = (RoomObject) iter.next();
	    
	    if (!ro.placed) continue;
	    
	    if (ro.fixed) {
		color = Color.white;
	    } else {
		if (!colorHash.containsKey(ro.type)) {
		    colorHash.put(ro.type, colorArr[nextColor]);
		    nextColor = (nextColor + 1) % colorArr.length;
		}
		color = (Color)colorHash.get(ro.type);
	    }
	    
	    g.setColor(color);
	    g.fill3DRect(ro.xloc+1, ro.yloc+1, ro.width-1, ro.length-1, true);
	    g.setColor(Color.black);
	    g.draw3DRect(ro.xloc, ro.yloc, ro.width, ro.length, true);
	}
    }
    
    public Hashtable getLegend() {
	return colorHash;
    }
    
    public String getCellSize() {
	    return "[" + l.delta + ", " + l.delta + "]";
    }    
    
	public String getRoomSize() {
		return "[" + l.rx + ", " + l.ry + "]";
	}
	
	public String getSpaceUtil() {
		return String.valueOf((int)(getSpaceUsageRatio() * 100)) + " %";
	}
	
	public double getSpaceUsageRatio() {
		Iterator iter = l.dr.mObjs.iterator();
		return (double)(getAreaUsedByObjects(iter)) / (double)(l.rx*l.ry);
	}
	
	protected int getAreaUsedByObjects(Iterator iter) {
		int minx = l.rx, miny = l.ry, maxx = 0, maxy = 0;
		
		RoomObject ro;
		
		while (iter.hasNext()) {
			ro = (RoomObject) iter.next();
			
			if (ro.xloc < minx) minx = ro.xloc;
			if (ro.yloc < miny) miny = ro.yloc;
			if (ro.xloc+ro.width > maxx) maxx = ro.xloc+ro.width;
			if (ro.yloc+ro.length > maxy) maxy = ro.yloc+ro.length;
		}
		
		if (maxx < minx || maxy < miny)
			return 0;
		
		return (maxx - minx) * (maxy - miny);
	}    
}
