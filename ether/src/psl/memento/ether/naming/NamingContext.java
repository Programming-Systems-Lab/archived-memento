package psl.memento.ether.naming;

import java.util.*;

/**
 * A NamingContext binds a set of given objects to a set of unique names. Later,
 * objects within the local container or on a remote container may be
 * discovered using the names they've been bound to. NamingContexts can also
 * contain other NamingContexts thereby forming a tree. Objects can be
 * addressed within the tree by chaining the names of contexts together.
 * <p>
 * Two types of names may be used to lookup objects inside a given naming
 * context. <em>Absolute</em> names are of the form '/{name}/{name}'. An
 * absolute name is resolved by beginning with the container's root context and
 * working down the name chain. <em>Relative</em> names are of the form
 * '{name}/{name}'. They are evaluated by starting at the current context and
 * working downwards to locate the name.
 * <p>
 * Depending on its type, a NamingContext may not be able to store all kinds of
 * Java objects.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public abstract class NamingContext
{
   private static NamingContext rootCtx;
   protected NamingContext parentCtx;

   /**
	 * Get the local root context for the current container.
	 *
	 * @return local root context for the current container
	 */
	public static NamingContext getRootContext()
	{
		return rootCtx;
	}

	/**
	 * Set the local root context for the current container. Only the container
	 * should call this method.
	 *
	 * @param ctx local root context for the current container
	 */
	public static void setRootContext(NamingContext ctx)
	{
		rootCtx = ctx;
	}

	/**
	 * Retrieve an object bound to a given name in the naming context.
	 *
	 * @param name absolute or relative name of an object
	 * @return object bound to <code>name</code> or <code>null</code>
	 * @throws NamingException
	 *         if the lookup fails
	 */
	public Object lookup(String name) throws NamingException
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

		// if the name isn't a path, resolve it directly against the
		// current context
		if (!isPath(name))
		{
			return get(name);
		}

		 // break the name path up into components
      String[] path = tokenize(name);

		// attempt to resolve the NamingContext which contains the last component
		// of the path
		boolean absolute = isAbsoluteName(name);
		NamingContext ctx = resolve(path, absolute);

		if (ctx == null)
		{
			return null;
		}
		else
		{
			// pass the simple, non-path of the name directly to the context
			return ctx.get(path[path.length - 1]);
		}
	}

	/**
	 * Get an object bound to a given name in the current context.
	 *
	 * @param name simple, non-path name of an object bound in the current
	 *             current context
	 * @return Object bound to <code>name</code> or <code>null</code>
	 * @throws NamingException
	 *         if the get fails
	 */
	protected abstract Object get(String name) throws NamingException;

   /**
	 * Bind an object to a given name. The name may be absolute or relative.
	 *
	 * @param name name to bind the object against
	 * @param obj  Object to bind to the given name
	 * @throws NamingException
	 *         if the binding operation fails
	 */
	public void bind(String name, Object ob) throws NamingException
	{
		if ((name == null) || (ob == null))
		{
			String msg = "name or object can't be null";
			throw new IllegalArgumentException(msg);
		}

		// if it's a simple path name, put it directly into the current context
		if (!isPath(name))
		{
			put(name, ob);
			return;
		}

		// break the name up and determine if it's absolute
		boolean absolute = isAbsoluteName(name);
		String[] path = tokenize(name);

		// attempt to reolve the name to a target context
		NamingContext ctx = resolve(path, absolute);

		if (ctx == null)
		{
			String msg = "invalid name " + name;
			throw new NamingException(msg);
		}

		// map object to the name in the target context
		ctx.put(name, ob);
	}


	/**
	 * Put an object directly into the current context.
	 *
	 * @param name simple, non-path name to bind the object against
	 * @param ob   Object to map the given name to
	 * @throws NamingException
	 *         if the put fails
	 */
	protected abstract void put(String name, Object ob) throws NamingException;

   /**
	 * Remove an object bounded from a given name.
	 *
	 * @param name absolute or relative name of the object to unbind from
	 * @throws NamingException
	 *         if the unbinding operation fails
	 */
	public void unbind(String name) throws NamingException
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

		// if the name isn't a path, pass it directly to the current context
		if (!isPath(name))
		{
			delete(name);
			return;
		}

		// otherwise break the name up and attempt to resolve the context that
		// directly contains it
		boolean absolute = isAbsoluteName(name);
		String[] path = tokenize(name);

      NamingContext ctx = resolve(path, absolute);

		if (ctx == null)
		{
			String msg = "invalid name " + name;
			throw new NamingException(msg);
		}
		else
		{
			ctx.delete(name);
		}
	}

	/**
	 * Delete a binding directly inside the current context.
	 *
	 * @param name simple, non-path name of the binding to delete
	 * @throws NamingException
	 *         if the delete operation fails
	 */
	protected abstract void delete(String name) throws NamingException;

   /**
	 * Get an enumeration over all the names of the objects bound in the
	 * current context. This does not search child or parent contexts.
	 *
	 * @return Enumeration over the names in the context in an unspecified
	 *         order
	 * @throws NamingException
	 *         if the list operation fails
	 */
	public abstract Enumeration list() throws NamingException;

	/**
	 * Break a resource name of the form '/name/name' or 'name/name' up into
	 * tokens.
	 *
	 * @param name name of the resource
	 * @return tokenized version of <code>name</code>
	 */
	protected String[] tokenize(String name)
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

      List stringList = new ArrayList();

      StringTokenizer tokenizer = new StringTokenizer(name, "/");
      while (tokenizer.hasMoreTokens())
		{
			stringList.add(tokenizer.nextToken());
		}

		return (String[]) stringList.toArray(new String[0]);
	}

	/**
	 * Determine if a given name is absolute or not.
	 *
	 * @param name name of the resource to test
	 * @return <code>true</code> if <code>name</code> is absolute else
	 *         <code>false</code>
	 */
	protected boolean isAbsoluteName(String name)
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

		return name.charAt(0) == '/';
	}

	/**
	 * Determine if a name represents a path to a resource of the form
	 * '/{path}/{path}' or whether it's just a plain name.
	 *
	 * @param name name of the resource
	 * @return <code>true</code> if <code>name</code> is a path else
	 *         <code>false</code>
	 */
	protected boolean isPath(String name)
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

		return name.indexOf('/') >= 0;
	}

	/**
	 * Given a name, attempt to resolve the NamingContext which directly
	 * contains the name.
	 *
	 * @param path     the tokenized version of the name of the resource
	 * @param absolute whether the path represents an absolute path or not
	 * @return NamingContext which directly contains the last component of
	 *         <code>path</code> else <code>null</code>
	 * @throws NamingException
	 *         if the resolution fails
	 */
	protected NamingContext resolve(String[] path, boolean absolute)
			  throws NamingException
	{
		if (path == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

      NamingContext currentCtx = null;

		// if it's an absolute name, start with the container's root context
		// otherwise start with this context

		if (absolute)
		{
			currentCtx = NamingContext.getRootContext();
		}
		else
		{
			currentCtx = this;
		}

      // loop down each component of the name until you get to the
		// second to last component - each non-last component must be a naming
		// context

      for (int i = 0; i < path.length - 1; ++i)
		{
			// ask the current component directly for the given path component
         Object ob = currentCtx.get(path[i]);

			// if we don't find it or we find a non-context then the path is bad
			// so return null
			if ((ob == null) || !(ob instanceof NamingContext))
			{
            return null;
			}

         // otherwise set the current naming context to the new one
			currentCtx = (NamingContext) ob;
		}

      return currentCtx;
	}



}
