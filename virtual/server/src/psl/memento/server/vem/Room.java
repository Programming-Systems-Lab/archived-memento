package psl.memento.server.vem;

import java.awt.Polygon;

/*
 * Room.java
 *
 * Created on November 6, 2002, 7:20 PM
 */

/**
 *
 * @author  vlad
 */
public class Room {

	private int height;
	private int length;
	private int width;
	private Polygon plan;

	/** Creates a new instance of Room */
	public Room(int w, int l, int h) {
		setHeight(h);
		setWidth(w);
		setLength(l);
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public void setPlan(Polygon plan) {
		this.plan = plan;
	}

	public Polygon getPlan() {
		return plan;
	}



}
