package psl.memento.ether.deploy;

/**
 * Describes the class information used to load a component.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class ClassInfo
{
	private String className;


	/**
	 * Construct a new ClassInfo object.
	 *
	 * @param className name of the class
	 */
	public ClassInfo(String className)
	{
		setClassName(className);
	}

	/**
	 * Get the name of the class for the given component.
	 *
	 * @return name of the class
	 */
	public String getClassName()
	{
		return className;
	}

	/**
	 * Set the name of the class.
	 *
	 * @param className name of the class
	 */
	public void setClassName(String className)
	{
		if (className == null)
		{
			String msg = "className can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.className = className;
	}

	/**
	 * Get a new instance of the class represented by this ClassInfo
	 * object.
	 *
	 * @return new instance of the class represented by this object
	 * @throws DeploymentException
	 *         if the class couldn't be loaded
	 */
	public Object newInstance() throws DeploymentException
	{
		Class theClass;
		try
		{
			theClass = Thread.currentThread().
					getContextClassLoader().loadClass(className);
		}
		catch (ClassNotFoundException cnfe)
		{
			try
			{
				theClass = getClass().getClassLoader().loadClass(className);
			}
			catch (ClassNotFoundException cnfe2)
			{
				String msg = "couldn't load class " + className;
				throw new DeploymentException(msg);
			}
		}

		Object result = null;
		if (theClass != null)
		{
			try
			{
				result = theClass.newInstance();
			}
			catch (Exception e)
			{
				String msg = "couldn't load class " + className;
				throw new DeploymentException(msg, e);
			}
		}

		return result;
	}
}
