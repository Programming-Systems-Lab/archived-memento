package aether.event;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Used to construct URIs.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class UriFactory
{
	/**
	 * Construct a URI to locate Aether resources.
	 *
	 * @param host  host of the resource
	 * @param path  path of the resource
	 * @param frag  fragment part of the uri
	 * @param query query element
	 * @throws URISyntaxException
	 *         if any parameter is invalid
	 */
	public static URI create(String host, String path, String frag,
							 String query) throws URISyntaxException
	{
		return new URI("aether", null, host, -1, path, query, frag);
	}
}
