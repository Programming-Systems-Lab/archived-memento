/*
 * Layout.java
 *
 * Created on November 6, 2002, 7:08 PM
 */

package psl.memento.virtual.server.vem;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 *
 * @author  Vlad Shchogolev
 */
public class Layout extends AbstractLayout implements LayoutDrawer {
    
    // future version will make this var (cell size) dynamic
    protected static final int delta = 20;
    
    // cells
    protected int gran_x, gran_y;
    protected boolean[][] cells;
    
    // min spacing factor
    protected int spacingFactor;
    
    // legend info
    Hashtable colorHash = null;
    
    public void setParameters() {
	this.spacingFactor = lp.getSpacingValue();
	this.rx = dr.mRoom.getWidth();
	this.ry = dr.mRoom.getHeight();	
	
	// create deep copy of floor plan
	floorPlan = new Polygon();
	Polygon orig = dr.mRoom.plan;
	for (int i=0; i<orig.npoints; i++) {
	    floorPlan.addPoint(orig.xpoints[i], orig.ypoints[i]);
	}
    }
    
    /**
     *	Main methods that comprise the layout algorithm
     */
    
    public void calculateLayout()
    {
	boolean okay;
	Polygon p;
	
	do {
	    // set instance variables
	    gran_x = rx / delta;
	    gran_y = ry / delta;
	    cells = new boolean[gran_x+1][gran_y+1];	

	    // fill in objects here
	    if (!(okay = doObjects(spacingFactor))) {
		// make room bigger
		rx = rx + rx/2;
		ry = ry + ry/2;
		if (floorPlan != null) {
		    for (int i=0; i<floorPlan.npoints; i++) {
			floorPlan.xpoints[i] *= 1.5;
			floorPlan.ypoints[i] *= 1.5;
		    }
		    floorPlan.invalidate();
		}
	    }	    
	} while (!okay);
    }
    
    /**
     *	Returns true if all objects were successfully placed
     *
     *	@param min  the minimum spacing factor between objects
     *		    (0 = tightest fit)
     */
    protected boolean doObjects(int min) {
	int spacing = min - 1;
	int numObjs = dr.mObjs.size();
	int result = numObjs;
	boolean tmpcells[][] = new boolean[gran_x+1][gran_y+1];
	
	if (floorPlan != null)
	    applyFloorPlan();
	
	while (result == numObjs) {
	    spacing++;
	    copyCells(tmpcells, cells);
	    result = placeObjectsInGrid(dr.mObjs.iterator(), spacing);
	    copyCells(cells, tmpcells);
	}
	
	spacing--;  // roll back to last working spacing
	if (spacing >= min) {  // means that at least the minimal spacing worked
	    placeObjectsInGrid(dr.mObjs.iterator(), spacing);
	    return true;
	}
	
	return false;
    }
    
    protected int placeObjectsInGrid(Iterator iter, int spacing)
    {
	RoomObject ro;
	int ytmp = delta, xtmp = delta;
	boolean foundPosition;
	int placedCount = 0;
	
	if (spacing < 0) spacing = 0;
	
	while (iter.hasNext()) {
	    ro = (RoomObject) iter.next();
	    foundPosition = false;  // find position for room object
	    for (xtmp = delta; xtmp < rx; xtmp += delta*(spacing+1)) {
	        for (ytmp = delta; ytmp < ry; ytmp += delta*(spacing+1)) {
		    if (testAndOccupy(xtmp, ytmp, ro.width, ro.height)) {
			foundPosition = true;
			placedCount++;
			ro.xloc = xtmp;
			ro.yloc = ytmp;			
			break;
		    }
		}
		if (foundPosition) break;		
	    }
	} 
	
	return placedCount;
    }

    /*
     *	Helper methods for layout algorithm
     */
    
    protected void occupy(int x1, int y1, int dx, int dy) {
	int i,j;
	
	for(i = x1 / delta; i <= (x1+dx)/delta; i++) {
	    for (j = y1 / delta; j <= (y1+dy)/delta; j++) {
		if (i > gran_x || j > gran_y) continue;
		cells[i][j] = true;
	    }
	}
    }
    
    protected boolean testAndOccupy(int x1, int y1, int dx, int dy) 
    {
	int i,j;
	
	for(i = x1 / delta; i <= (x1+dx)/delta; i++) {
	    for (j = y1 / delta; j <= (y1+dy)/delta; j++) {
		if (i > gran_x || j > gran_y) return false;		
		if (cells[i][j]) return false;
	    }
	}
	
	occupy(x1, y1, dx, dy);
	return true;
    }
    
    protected void applyFloorPlan() 
    {
	int xtmp,ytmp;
	Rectangle r = new Rectangle();
	
	for (xtmp = 0; xtmp < rx; xtmp += delta) {
	    for (ytmp = 0; ytmp < ry; ytmp += delta) {
		r.setBounds(xtmp, ytmp, delta, delta);
		if (!floorPlan.contains(r))
		    cells[xtmp/delta][ytmp/delta] = true;
	    }
	}
    }
    
    
    public static void copyCells(boolean dest[][], boolean src[][])
    {
	for (int i = 0; i < src.length; i++)
	    for (int j = 0; j < src[i].length; j++)
		dest[i][j] = src[i][j];
    }    
    
    /*
     *	Misc. methods used for visualization and statistics
     */
    
    public String getCellSize()
    {
	return "[" + delta + ", " + delta + "]";
    }
    
    public String getLayoutSchemeName()
    {
	return "Simple Layout";
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
	if (lp.highlightFreeSpace()) {
	    g.setColor(Color.white);
	    for (i=0; i<=rx; i+=delta) {
		g.drawLine(i, 0, i, ry);
	    }

	    for (i=0; i<=ry; i+=delta) {
		g.drawLine(0, i, rx, i);
	    }
	}
	
	if (floorPlan == null) {
	    g.setColor(Color.black);
	    g.drawRect(0, 0, rx, ry);
	}
	
	// do doors
	g.setColor(Color.black);
	Iterator iter = dr.mDoors.iterator();
	while (iter.hasNext()) {
	    d = (Door) iter.next();
	    off = d.mOffset;
	    len = d.mLength;
	    
	    switch (d.mWall) {
		case 'n':
		    g.fillRoundRect(off, 0, len, delta/2, 5, 5);
		    occupy(off, 0, len, delta/2);
		    break;
		case 's':
		    g.fillRoundRect(off, ry-delta/2, len, delta/2, 5, 5);
		    occupy(off, ry-delta, len, delta/2);
		    break;
		case 'w':
		    g.fillRoundRect(0, off, delta/2, len, 5, 5);
		    occupy(0, off, delta/2, len);
		    break;
		case 'e':
		    g.fillRoundRect(rx-delta/2, off, delta/2, len, 5, 5);
		    occupy(rx-delta, off, delta/2, len);
		    break;
	    }
	}
	
	// draw objects
	iter = dr.mObjs.iterator();
	while (iter.hasNext()) {
	    ro = (RoomObject) iter.next();
	    
	    if (!colorHash.containsKey(ro.type)) {
		colorHash.put(ro.type, colorArr[nextColor]);
		nextColor = (nextColor + 1) % colorArr.length;
	    }
	    
	    g.setColor((Color)colorHash.get(ro.type));
	    g.fillRoundRect(ro.xloc, ro.yloc, ro.width, ro.height, 5, 5);
	    g.setColor(Color.black);
	    g.drawRoundRect(ro.xloc, ro.yloc, ro.width, ro.height, 5, 5);
	}
	
	if (lp.highlightFreeSpace()) {
	    // show free space
	    g.setColor(Color.yellow);
	    for (i=0; i<gran_x; i++) {
		for (j=0; j<gran_y; j++) {
		    if (!cells[i][j])
			g.fillRect(i*delta+1, j*delta+1, delta-2, delta-2);
		}
	    }
	}
	
	g.setColor(Color.black);
	if (floorPlan != null) {
	    g.drawPolygon(floorPlan);
	}
    }
    
    public Hashtable getLegend()
    {
	return colorHash;
    }
      
}
