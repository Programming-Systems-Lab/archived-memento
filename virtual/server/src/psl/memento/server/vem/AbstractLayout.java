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
	
	public int rx, ry;
	public Polygon floorPlan;
	public DataReader dr;
	
	public AbstractLayout()
	{}
	
	/**
	 *	Initializes internal settings and calls the <code>calculateLayout()
	 *	</code> method()
	 */
	public void init(DataReader dr) {
		this.dr = dr;

		setParameters();
		calculateLayout();
	}
	
	/**
	 *	Main method that comprise the layout algorithm
	 */
	public abstract void calculateLayout();
	public abstract void setParameters();
	public abstract String getLayoutSchemeName();
}
