package org.eclipse.tesla.shell.provision.internal.mosgi;

/**
 * TODO
 *
 * @since 1.0
 */
public enum OSGiFramework
{
    OSGi_FRAMEWORK_4_2( "OSGi-4.2" );

    private final String value;

    OSGiFramework( final String value )
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
