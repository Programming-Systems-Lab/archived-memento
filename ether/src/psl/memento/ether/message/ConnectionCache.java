package psl.memento.ether.message;

import psl.memento.ether.util.CounterMap;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/**
 * Object responsible for caching connections between components.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
class ConnectionCache
{
   private Map addressCacheMap = Collections.synchronizedMap(new HashMap());
	private CounterMap connCount = new CounterMap();

	/**
	 * Cache a connection from a connection
	 */
}
