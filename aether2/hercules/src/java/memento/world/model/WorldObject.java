package memento.world.model;

import java.io.Serializable;

/**
 * Partial implementation of the WorldObject interface.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class WorldObject implements Serializable
{
	/**
	 * GUID of this object.
	 */
	protected String uid;

	/**
	 * Size of the object.
	 */
	protected Dimension size;

	/**
	 * WorldModel that created this object.
	 */
	protected WorldModel worldModel;

    /**
     * URL associated with the world object.
     */
    protected String url;

    /**
     * Caption associated with the world object.
     */
    protected String caption;

	public String getUID()
	{
		return uid;
	}

	public void setUID(String uid)
	{
		this.uid = uid;
	}

	public Dimension getSize()
	{
		return size;
	}

	public void setSize(Dimension dim)
	{
		this.size = dim;
	}

	public WorldModel getWorldModel()
	{
		return worldModel;
	}

	public void setWorldModel(WorldModel model)
	{
		this.worldModel = model;
    }

    public String getURL()
    {
        return url;
    }

    public void setURL(String url)
    {
        this.url = url;
    }

    public String getCaption()
    {
        return caption;
    }

    public void setCaption(String caption)
    {
        this.caption = caption;
    }
}
