package aether.server.domain;

import java.io.Serializable;

/**
 * Keeps information about a domain.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DomainInfo implements Serializable
{
    private String domainName;
	private String authority;

	/**
	 * Set the authority for the domain.
	 *
	 * @return authority for the domain
	 */
	public String getAuthority()
	{
		return authority;
	}

	/**
	 * Set the authority for the domain.
	 *
	 * @param authority authority for the domain
	 */
	public void setAuthority(String authority)
	{
		this.authority = authority;
	}

	/**
	 * Get the name of the domain.
	 *
	 * @return name of the domain
	 */
	public String getDomainName()
	{
		return domainName;
	}

	/**
	 * Set the name of the domain.
	 *
	 * @param domainName name of the domain
	 */
	public void setDomainName(String domainName)
	{
		this.domainName = domainName;
	}
}
