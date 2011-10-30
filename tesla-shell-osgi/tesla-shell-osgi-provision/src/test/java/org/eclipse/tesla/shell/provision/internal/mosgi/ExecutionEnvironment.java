package org.eclipse.tesla.shell.provision.internal.mosgi;

/**
 * TODO
 *
 * @since 1.0
 */
public enum ExecutionEnvironment
{
    J2SE_1_3( "J2SE-1.3" ),
    J2SE_1_4( "J2SE-1.4" ),
    J2SE_1_5( "J2SE-1.5" ),
    JavaSE_1_6( "JavaSE-1.6" );

    private final String value;

    ExecutionEnvironment( final String value )
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
