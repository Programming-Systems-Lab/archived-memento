package psl.memento.server.vem;

/*
 * AbstractLayout.java
 *
 * Created on November 11, 2002, 3:16 AM
 */

import java.util.*;
import java.awt.Polygon;

/**
 *
 * @author  Vladislav
 */
public abstract class AbstractLayout {
    
    protected int rx, ry;
    protected Polygon floorPlan;
    protected DataReader dr;
    protected LayoutParameters lp;
    
    public AbstractLayout()
    {}
    
    /**
     *	Initializes internal settings and calls the <code>calculateLayout()
     *	</code> method()
     */
    public void init(DataReader dr, LayoutParameters lp)
    {
	this.dr = dr;
	this.lp = lp;
	setParameters();
	calculateLayout();
    }
    
    /**
     *	Main method that comprise the layout algorithm
     */
    public abstract void calculateLayout();
    public abstract void setParameters();
    public abstract String getLayoutSchemeName();
    
    public String getRoomSize()
    {
	return "[" + rx + ", " + ry + "]";
    }    
    
    public String getSpaceUtil()
    {
	return String.valueOf((int)(getSpaceUsageRatio() * 100)) + " %";
    }
    
    public double getSpaceUsageRatio()
    {
	Iterator iter = dr.mObjs.iterator();
	return (double)(getAreaUsedByObjects(iter)) / (double)(rx*ry);	
    }
    
    protected int getAreaUsedByObjects(Iterator iter)
    {
	int minx = rx, miny = ry, maxx = 0, maxy = 0;
	
	RoomObject ro;
	
	while (iter.hasNext()) {
	    ro = (RoomObject) iter.next();
	    
	    if (ro.xloc < minx) minx = ro.xloc;
	    if (ro.yloc < miny) miny = ro.yloc;
	    if (ro.xloc+ro.width > maxx) maxx = ro.xloc+ro.width;
	    if (ro.yloc+ro.height > maxy) maxy = ro.yloc+ro.height;
	}
	
	if (maxx < minx || maxy < miny) 
	    return 0;
	
	return (maxx - minx) * (maxy - miny);
    }    
}
