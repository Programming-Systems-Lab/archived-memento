package aether.server.domain;

/**
 * Generic exception raised by a ComponentRegistry.
 *
 * Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class RegistryException extends Exception
{
    public RegistryException()
    {
        super();
    }

    public RegistryException(String s)
    {
        super(s);
    }

    public RegistryException(String s, Throwable t)
    {
        super(s, t);
    }

    public RegistryException(Throwable t)
    {
        super(t);
    }
}
