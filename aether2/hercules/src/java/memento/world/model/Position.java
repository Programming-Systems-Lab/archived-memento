package memento.world.model;

/**
 * Describes a position in the three dimensional world.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class Position
{
	private int x;
	private int y;
	private int z;

	public Position(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getZ()
	{
		return z;
	}
}
