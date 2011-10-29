package org.eclipse.tesla.shell.provision.internal.mosgi;

import java.io.Serializable;

import org.mockito.internal.stubbing.defaultanswers.ReturnsMoreEmptyValues;
import org.mockito.invocation.InvocationOnMock;

/**
 * TODO
 *
 * @since 1.0
 */
public class PartialImplementation
    extends ReturnsMoreEmptyValues
    implements Serializable
{

    private Class<?> implementer;

    public PartialImplementation( final Class<?> implementer )
    {

        this.implementer = implementer;
    }

    @Override
    public Object answer( final InvocationOnMock invocation )
        throws Throwable
    {
        try
        {
            final String name = invocation.getMethod().getName();
            final Class<?>[] parameterTypes = invocation.getMethod().getParameterTypes();
            // if method is not present will throw NoSuchMethodException and we get the usual way
            implementer.getDeclaredMethod( name, parameterTypes );
            return invocation.callRealMethod();
        }
        catch ( NoSuchMethodException ignore )
        {
            return super.answer( invocation );
        }

    }

}
