package psl.memento.server.vem;

/*
 * RoomObject.java
 *
 * Created on November 6, 2002, 7:16 PM
 */

/**
 *
 * @author  vlad
 */
public class RoomObject implements Comparable {
    
    private int xloc;
    private int yloc;
    private int zloc;
    private int length;
    private int width;
    private int height;
    private String type;
    private boolean fixed;
    private boolean placed;
    private String name;
    private String model;
    
    /** Creates a new instance of RoomObject */
    public RoomObject() {
	setPlaced(false);
        setFixed(false);        
    }
    
    public RoomObject(int dx, int dy, int dz)
    {
        this();
        setWidth(dx);
        setLength(dy);
        setHeight(dz);
    }
    
    public RoomObject(int dx, int dy, int dz, int xloc, int yloc, int zloc)
    {
        this(dx, dy, dz);
        setXloc(xloc);
        setYloc(yloc);
        setZloc(zloc);
        setFixed(true);
    }
    
    public int compareTo(Object o) {
	return getType().compareTo(((RoomObject)o).getType());
    }

	public void setXloc(int xloc) {
		this.xloc = xloc;
	}

	public int getXloc() {
		return xloc;
	}

	public void setYloc(int yloc) {
		this.yloc = yloc;
	}

	public int getYloc() {
		return yloc;
	}

	public void setZloc(int zloc) {
		this.zloc = zloc;
	}

	public int getZloc() {
		return zloc;
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

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setPlaced(boolean placed) {
		this.placed = placed;
	}

	public boolean isPlaced() {
		return placed;
	}
	
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @return
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param string
	 */
	public void setModel(String string) {
		model = string;
	}

}
