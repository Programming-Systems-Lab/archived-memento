package aether.server.framework;

import aether.server.domain.Advertisement;

/**
 * Represents a component that generates an advertisement when it joins a
 * container.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface Advertising extends Identifiable
{
	/**
	 * Name of the bound advertisement property.
	 */
	public static final String AdvertisementProperty = "advertisement";

    /**
	 * Get the Advertisement being generated by the component.
	 *
	 * @return Advertisement for the component
	 */
	public Advertisement getAdvertisement();
}
