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
 * Advanced layout class
 *
 * @author  Vladislav
 */
public class AdvancedLayout extends SimpleLayout implements Layout {
	
	public String getLayoutSchemeName() {
		return "Advanced Layout";
	}
	
	protected boolean doObjects(int max) {
		int numObjs = dr.mObjs.size();
		RoomObject ro;
		List objects;
		String type;
		
		/*
		 *  Here we arrange the room objects by their type
		 *  They are placed into the hashtable, where the key is the type
		 *  and the value is a List of room objects of this type.
		 */
		
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
		
		Iterator iter = types.keySet().iterator();
		while (iter.hasNext()) {
			type = (String) iter.next();
			objects = (List) types.get(type);
			
			placeObjects(objects.iterator(), objects.size(), searchForBlock());
		}
		
		return true;
	}
	
	/**
	 *  Places objects in iter into the box specified by bound
	 */
	protected int placeObjects(Iterator iter, int numObj, Rectangle bound) {
		if (bound == null) return 0;
		
		//System.out.println("***Begin***");
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
		//System.out.println("block: " + startX + ", " + startY + ", " + limitX + ", " + limitY);
		
		while (iter.hasNext()) {
			ro = (RoomObject) iter.next();
			
			foundPosition = false;  // find position for room object
			for (xtmp = startX; xtmp < limitX; xtmp += delta) {
				for (ytmp = startY; ytmp < limitY; ytmp += delta) {
					if (testAndOccupy(xtmp, ytmp, ro.width, ro.length)) {
						foundPosition = true;
						placedCount++;
						ro.xloc = xtmp;
						ro.yloc = ytmp;
						ro.placed = true;
						
						if (xtmp + ro.width > maxX) maxX = xtmp + ro.width;
						if (ytmp + ro.length > maxY) maxY = ytmp + ro.length;
						
						//System.out.println("placed: " + xtmp + ", " + ytmp + ", " + ro.width + ", " + ro.length);
						break;
					}
				}
				if (foundPosition) break;
			}
		}
		
		occupy(startX, startY, (maxX-startX)+delta, (maxY-startY)+delta);
		return placedCount;
	}
	
	public Rectangle searchForBlock() {
		int i, j, lastdx;
		int xpos, ypos, dx, dy, besty, bestx, score, bestscore;
		
		for (ypos = 0; ypos < gran_y; ypos++) {
			for(xpos = 0; xpos < gran_x; xpos++) {
				
				if (cells[xpos][ypos]) continue;
				
				lastdx = gran_x;
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
				
				if ((besty-ypos < 2) || (bestx-xpos < 2)) continue;
				
				// return this rectangle
				return new Rectangle(xpos, ypos, bestx-xpos, besty-ypos);
			}
		}
		
		return null;
	}
	
	public int findMaxDxForPos(int x, int y) {
		int i, dx = 0;
		for (i = x; i < gran_x; i++) {
			if (cells[i][y]) return dx;
			dx++;
		}
		return dx;
	}
}
