/*
 * Layout.java
 *
 * Created on November 6, 2002, 7:08 PM
 */

package psl.memento.server.vem;

import java.awt.Rectangle;
import java.awt.Polygon;
import java.util.*;

/**
 *
 * @author  Vlad Shchogolev
 */
public class SimpleLayout implements Layout {
    
    // future version will make this var (cell size) dynamic
    public static final int delta = 20;
    
    // cells
    public int gran_x, gran_y;
    public boolean[][] cells;
    
    public int rx, ry;
    public Polygon floorPlan;
    public DataReader dr;
    
    public SimpleLayout()
    {}
    
    /**
     *	Initializes internal settings and calls the <code>calculateLayout()
     *	</code> method()
     */
    public void doLayout(DataReader dr) {
	this.dr = dr;	
	calculateLayout();
    }
    
    public void setParameters() {
	this.rx = dr.mRoom.getWidth();
	this.ry = dr.mRoom.getLength();
	
	// set cell variables
	gran_x = rx / delta;
	gran_y = ry / delta;
	cells = new boolean[gran_x][gran_y];
	
	// do floor plan
	floorPlan = dr.mRoom.plan;
	if (floorPlan != null)
	    applyFloorPlan();
    }
    
    /**
     *	Main methods that comprise the layout algorithm
     */
    
    public void calculateLayout() {
	setParameters();
	placeFixedObjects();
	doObjects(2);
    }
    
    protected boolean placeFixedObjects() {
	Iterator iter = dr.mFixedObjs.iterator();
	RoomObject ro;
	
	while (iter.hasNext()) {
	    ro = (RoomObject) iter.next();
	    
	    if (!testAndOccupy(ro.xloc, ro.yloc, ro.width, ro.length))
		;//return false;
	    else
		ro.placed = true;
	}
	
	return true;
    }
    
    /**
     *	FIXME : need to make deep copy first.
     */
    protected void growRoomSize() {
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
    
    /**
     *	Returns true if ALL objects were successfully placed
     *
     *	@param min  the minimum spacing factor between objects
     *		    (0 = tightest fit)
     */
    protected boolean doObjects(int max) {
	int spacing;
	int numObjs = dr.mObjs.size();
	int result = numObjs;
	boolean tmpcells[][] = new boolean[gran_x][gran_y];
	
	for (spacing = 0; spacing <= max; spacing++) {
	    copyCells(tmpcells, cells);
	    result = placeObjectsInGrid(dr.mObjs.iterator(), spacing, -1);
	    copyCells(cells, tmpcells);
	    if (result < numObjs) break;
	}
	
	spacing--;  // roll back to last working spacing
	if (spacing >= 0) {  // means that at least the minimal spacing worked
	    placeObjectsInGrid(dr.mObjs.iterator(), spacing, -1);
	    return true;
	} else {    // place minimum number of objects
	    placeObjectsInGrid(dr.mObjs.iterator(), spacing, result);
	    return false;
	}
    }
    
    protected int placeObjectsInGrid(Iterator iter, int spacing, int limit) {
	RoomObject ro;
	int ytmp = delta, xtmp = delta;
	boolean foundPosition;
	int placedCount = 0;
	
	if (spacing < 0) spacing = 0;
	
	while (iter.hasNext()) {
	    if (limit >= 0 && placedCount >= limit) break;
	    ro = (RoomObject) iter.next();
	    foundPosition = false;  // find position for room object
	    for (xtmp = delta; xtmp < rx; xtmp += delta*(spacing+1)) {
		for (ytmp = delta; ytmp < ry; ytmp += delta*(spacing+1)) {
		    if (testAndOccupy(xtmp, ytmp, ro.width, ro.length)) {
			foundPosition = true;
			placedCount++;
			ro.xloc = xtmp;
			ro.yloc = ytmp;
			ro.placed = true;
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
		if (i >= gran_x || j >= gran_y) continue;
		cells[i][j] = true;
	    }
	}
    }
    
    protected boolean testAndOccupy(int x1, int y1, int dx, int dy) {
	int i,j;
	
	for(i = x1 / delta; i <= (x1+dx)/delta; i++) {
	    for (j = y1 / delta; j <= (y1+dy)/delta; j++) {
		if (i >= gran_x || j >= gran_y) return false;
		if (cells[i][j]) return false;
	    }
	}
	
	occupy(x1, y1, dx, dy);
	return true;
    }
    
    protected void applyFloorPlan() {
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
    
    
    public static void copyCells(boolean dest[][], boolean src[][]) {
	for (int i = 0; i < src.length; i++)
	    for (int j = 0; j < src[i].length; j++)
		dest[i][j] = src[i][j];
    }
    
	/*
	 *	Misc. methods used for visualization and statistics
	 */
    
    
    
    public String getLayoutSchemeName() {
	return "Simple Layout";
    }
    
    
    
}
