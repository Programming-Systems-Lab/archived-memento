package psl.memento.ether.naming;

import psl.memento.ether.util.IteratorEnumerationAdapter;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.Enumeration;

/**
 * A LocalNamingContext represents a context in which the object-name binding
 * is stored in memory. A LocalNamingContext can store any kind of java object.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class LocalNamingContext extends NamingContext
{
	private Map valMap = Collections.synchronizedMap(new HashMap());

   /**
	 * Construct a new LocalNamingContext which can serve as a root context.
	 */
	public LocalNamingContext()
	{
		; // do nothing
	}

	/**
	 * Construct a new LocalNamingContext with the given parent and the given
	 * name.
	 *
	 * @param name   name of the local naming context being created
	 * @param parent NamingContext to serve as the parent of the new context
	 * @throws NamingException
	 *         if the new context fails
	 */
	public LocalNamingContext(String name, NamingContext parent)
		throws NamingException
	{
		if ((name == null) || (parent == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		this.parentCtx = parent;
		parentCtx.bind(name, this);
	}

	/**
	 * Retrieve an object from this context bound to a given name.
	 *
	 * @param name simple, non-path name of the object
	 * @return object bound to <code>name</code> or <code>null</code>
	 * @throws NamingException
	 *         never
	 */
   protected Object get(String name) throws NamingException
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

		return valMap.get(name);
	}

	/**
	 * Bind a simple name directly against the current context.
	 *
	 * @param name simple, non-path name
	 * @param ob   object to bind against the name
	 * @throws NamingException
	 *         never
	 */
	protected void put(String name, Object ob) throws NamingException
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

		valMap.put(name, ob);
	}

	/**
	 * Delete an object binding in the current context.
	 *
	 * @param name simple, non-path name of the binding to delete
	 * @throws NamingException
	 *         never
	 */
	protected void delete(String name) throws NamingException
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

		valMap.remove(name);
	}

   /**
	 * Get an enumeration of all the names bound in this context.
	 *
	 * @return Enumeration over all the names bound in this context
	 * @throws NamingException
	 *         never
	 */
	public Enumeration list() throws NamingException
	{
		return new IteratorEnumerationAdapter(valMap.keySet().iterator());
	}
}
