package psl.memento.server.container.naming;

import java.util.*;

/**
 * Represent's a container naming context for storing server side resources.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class NamingContext
{
	private static final NamingContext rootCtx = new NamingContext();
	private NamingContext parentCtx;
	private Map valMap = Collections.synchronizedMap(new HashMap());
	
	/**
	 * Private constructor to hide the root naming context.
	 **/
	private NamingContext()
	{
		; // do nothing
	}
	
	/**
	 * Get the root context.
	 * 
	 * @return the unnamed root naming context
	 **/
	public static NamingContext getRootContext()
	{
		return rootCtx;
	}

	/**
	 * Construct a new NamingContext with the given resource name. If the 
	 * name is absolute (in the form ctx1.ctx2.ctx3) then the last context
	 * in the path will be created along with all intermediate contexts. If
	 * the name is relative (ctx3) then a new context will be created and
	 * added directly to the root context.
	 * 
	 * @param name resource name of the new context to create
	 * @return NamingContext with the given name
	 * @throws NamingException 
	 *         if a resource already exists with the given name
	 **/
	public static NamingContext createNamingContext(String name) 
		throws NamingException
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		// attach this context directly to the root context
		
		// first make sure one doesn't already exist
		if (rootCtx.exists(name))
		{
			String msg = "resource " + name + " already exists";
			throw new NamingException(msg);
		}
		
		if (isAbsolute(name))
		{
			// if it's an absolute name let's walk down the hiearchy until
			// we get to the last naming context in the path which exists
			String[] path = tokenize(name);
			NamingContext current = rootCtx;
			int i = 0;
			for (; i < path.length; ++i)
			{
				Object obj = current.valMap.get(path[i]);
				
				// if the object is null, break cuz we've found the last
				// existing context in the path
				if (obj == null)
				{
					break;
				}
				
				// if the object isn't a naming context throw an exception
				if (!(obj instanceof NamingContext))
				{
					String msg = "invalid path for naming context";
					throw new NamingException(msg);
				}
				
				// otherwise make the new context the current one
				current = (NamingContext) obj;
			}
			
			// now, starting from the current i let's loop through the
			// rest of the array and construct a new naming context for 
			// each token in the path
			NamingContext ctx;
			for (; i < path.length; ++i)
			{
				ctx = new NamingContext();
				
				// put ctx into current under the token for this ith one
				current.valMap.put(path[i], ctx);
				
				// now make the newly created context the current one as
				// we work our way down the rest of the path
				current = ctx;
			}
			
			// now the last context we created is the one to return
			return current;
		}
		else
		{
			// if it's a relative name just attach it to the root context
			NamingContext ctx = new NamingContext();
			rootCtx.valMap.put(name, ctx);
			return ctx;
		}
	}
	
	/**
	 * Determine if an object exists in the naming context.
	 * 
	 * @param name absolute or relative name of the resource
	 * @return <c>true</c> if the resource exists in the naming context else
	 *         <c>false</c>
	 **/
	public boolean exists(String name)
	{
		return get(name) != null; 
	}
	
	/**
	 * Retrieve a resource from the naming context. The name of the resource
	 * can either be absolute in the form of ctx1.ctx2.resName in which
	 * case the resource be located by traversing the context hiearchy or
	 * it can be relative in which case only this context wil be searched for
	 * the resource.
	 * 
	 * @param name name of the resource
	 * @return objeect mapped to the resource with the given name or if no
	 *         such resource exists then <c>null</c>
	 **/
	public Object get(String name)
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		// if it's an absolute name then start with the root context and
		// work your way down
		if (isAbsolute(name))
		{
			String[] path = tokenize(name);
			NamingContext current = rootCtx;
			
			// loop through everything but the last element which identifies 
			// the concrete resource
			for (int i = 0; i < path.length - 1; ++i)
			{
				Object ob = current.valMap.get(path[i]);
				
				// if we don't find the naming context, just return null 
				if (ob == null)
				{
					return null;
				}
				
				// if we find a resource which isn't a naming context and so
				// can't be part of the path return null, it's a mistake
				if (!(ob instanceof NamingContext))
				{
					return null;
				}
				
				// otherwise set the current naming context to be the one found
				current = (NamingContext) ob;
			}
			
			// now we found the lowest naming context, let's ask it for the
			// resource
			return current.valMap.get(path[path.length - 1]);
		}
		else
		{
			// if it's relative just ask the local valmap
			return valMap.get(name);
		}
	}
	
	/**
	 * Place an object innn the current naming context.
	 * <p>
	 * Note that you can't put absolutely named resources in the naming context.
	 * 
	 * @param name relative name to store the resource under
	 * @param ob   object to store in the naming context
	 **/				
	public void put(String name, Object ob)
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		if (isAbsolute(name))
		{
			String msg = "cannot put absolutely named resources";
			throw new IllegalArgumentException(msg);
		}
		
		valMap.put(name, ob);
	}	

	
	/**
	 * Break a resource name of the form 'res1.res2.res3' up into a series
	 * of tokens.
	 * 
	 * @param name name of the resource to tokenize
	 * @return tokenized version of <c>name</c> where each level is a hiearchy
	 **/
	private static String[] tokenize(String name)
	{
		List stringList = new ArrayList();
		
		StringTokenizer tokenizer = new StringTokenizer(name, ".");
		while (tokenizer.hasMoreTokens())
		{
			stringList.add(tokenizer.nextToken());
		}
		
		return (String[]) stringList.toArray(new String[0]);
	}
	
	/**
	 * Determine if the name of a resource is an absolute path to the resource.
	 * This is true essentialy if the name contains a '.'
	 * 
	 * @param name name of the resource
	 * @return <C>true</c> if it's the absolute name else <c>false</c>
	 **/		
	private static boolean isAbsolute(String name)
	{
		return name.indexOf('.') >= 0;
	}		

}
