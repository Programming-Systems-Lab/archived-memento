package memento.world.model;

/**
 * Describes the dimensions of an object in the world.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class Dimension
{
    private int width;
	private int height;
	private int length;

	public Dimension(int width, int height, int length)
	{
		this.width = width;
		this.height = height;
		this.length = length;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}
}
