package org.eclipse.tesla.osgi.provision;

import java.io.PrintStream;

import org.osgi.framework.Bundle;

/**
 * TODO
 *
 * @since 1.0
 */
public interface ProvisionSet
{

    Bundle[] install();

    Bundle[] installAndStart( );

    boolean hasProblems();

    void printProblems( final PrintStream err );

}
