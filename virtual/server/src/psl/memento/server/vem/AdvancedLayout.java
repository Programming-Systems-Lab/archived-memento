package psl.memento.server.vem;

/*
 * AdvancedLayout.java
 *
 * Created on November 11, 2002, 11:09 PM
 */

import java.util.*;
import java.awt.Rectangle;
import java.awt.Polygon;

/**
 *
 * @author  Vladislav
 */
public class AdvancedLayout extends Layout {
    
    private LinkedList blocks;  
    
    public String getLayoutSchemeName() {
	return "Advanced Layout";
    }    
    
    protected boolean doObjects(int max) {
	int numObjs = dr.mObjs.size();
	RoomObject ro;
	List objects;
	String type;
	
	Hashtable types = new Hashtable();
	for (int i=0; i<numObjs; i++) {
	    ro = (RoomObject) dr.mObjs.get(i);
	    objects = (List)types.get(ro.type);
	    if (objects == null) {
		objects = new LinkedList();
	        types.put(ro.type, objects); 
	    }
	    objects.add(ro);
	}
	
	blocks = new LinkedList();
	searchForBlock();
	
	Iterator iter = types.keySet().iterator();
	while (iter.hasNext()) {
	    type = (String) iter.next();
	    objects = (List) types.get(type);
	    
	    placeObjects(objects.iterator(), objects.size());
	    blocks = new LinkedList();
	    searchForBlock();
	}
	
	return true;
    }
    
    protected int placeObjects(Iterator iter, int numObj)
    {
	System.out.println("***Begin***");
	Rectangle bound = (Rectangle) blocks.removeFirst();

	RoomObject ro = null;

	int ytmp, xtmp, limitX = -1, limitY = -1, startX = -1, startY = -1;
	int maxX, maxY;
	boolean foundPosition = true;
	int placedCount = 0;

	startX = bound.x * delta;
	startY = bound.y * delta;
	limitX = (bound.x+bound.width) * delta;
	limitY = (bound.y+bound.height) * delta;
	maxX = 0;
	maxY = 0;
	System.out.println("block: " + startX + ", " + startY + ", " + limitX + ", " + limitY); 
		
	while (iter.hasNext()) {
	    ro = (RoomObject) iter.next(); 
	
	    foundPosition = false;  // find position for room object
	    for (xtmp = startX; xtmp < limitX; xtmp += delta) {
	        for (ytmp = startY; ytmp < limitY; ytmp += delta) {
		    if (testAndOccupy(xtmp, ytmp, ro.width, ro.height)) {
			foundPosition = true;
			placedCount++;
			ro.xloc = xtmp;
			ro.yloc = ytmp;	
			ro.placed = true;
			
			if (xtmp + ro.width > maxX) maxX = xtmp + ro.width;
			if (ytmp + ro.height > maxY) maxY = ytmp + ro.height;
			
			System.out.println("placed: " + xtmp + ", " + ytmp + ", " + ro.width + ", " + ro.height); 			
			break;
		    }
		}
		if (foundPosition) break;		
	    }
	} 
	
	occupy(startX, startY, (maxX-startX)+delta, (maxY-startY)+delta);
	
	return placedCount;
    } 
    
    public void searchForBlock()
    {
	int i, j, lastdx; 
	int xpos, ypos, dx, dy, besty, bestx, score, bestscore;
	Rectangle output;
	boolean reserved[][] = new boolean[gran_x][gran_y];
	
	//System.out.println("Begin search");
	    for (ypos = 0; ypos < gran_y; ypos++) {
	for(xpos = 0; xpos < gran_x; xpos++) {

		//System.out.println("Posn: " + xpos + ", " + ypos + "\t");
		if (cells[xpos][ypos] || reserved[xpos][ypos]) continue;

		lastdx = gran_x;// - xpos;
		besty = ypos;
		bestx = xpos;
		bestscore = 0;

		for (j = ypos, dy = 1; j < gran_y; j++, dy++) {

		    dx = Math.min(lastdx, findMaxDxForPos(xpos, j));
		    lastdx = dx;

			score = Math.min(dy, dx);		    
			score *= score;
			score += dy*dx;

		    if (score > bestscore) {
			bestscore = score;
			besty = ypos + dy;
			bestx = xpos + dx;
		    }
		}

		if ((besty-ypos < 3) || (bestx-xpos < 3)) continue;

		// reserve this rectangle
		output = new Rectangle(xpos, ypos, bestx-xpos, besty-ypos);
		blocks.add(output);
		
		for (i=xpos; i<bestx; i++) {
		    for (j=ypos; j<besty; j++) {
			reserved[i][j] = true;
		    }
		}

/*
		g.drawRect(xpos*delta + 5, ypos*delta + 5, 
		    (bestx - xpos) * delta - 10, (besty - ypos) * delta - 10);   
*/
	    }
	}
    }
    
    public int findMaxDxForPos(int x, int y)
    {
        int i, dx = 0;
        for (i = x; i < gran_x; i++) {
            if (cells[i][y]) return dx;
            dx++;
        }
        return dx;
    }  
    
         public static void main(String args[]) {
        new RoomViewer().show();
    }
}
