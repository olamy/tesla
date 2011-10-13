package org.apache.maven.incremental.internal;

import javax.inject.Named;

import org.eclipse.tesla.incremental.BuildContext;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;

@Named
public class MojoExecutionModule
    implements Module
{
    public static final String SCOPE_NAME = "mojoExecution";

    public void configure( Binder binder )
    {
        MojoExecutionScope batchScope = new MojoExecutionScope();

        // tell Guice about the scope
        binder.bindScope( MojoExecutionScoped.class, batchScope );

        // make our scope instance injectable
        binder.bind( MojoExecutionScope.class ).annotatedWith( Names.named( SCOPE_NAME ) ).toInstance( batchScope );

        binder.bind( BuildContext.class ).toProvider( MojoExecutionScope.<BuildContext> seededKeyProvider() ).in( MojoExecutionScoped.class );
    }
}
