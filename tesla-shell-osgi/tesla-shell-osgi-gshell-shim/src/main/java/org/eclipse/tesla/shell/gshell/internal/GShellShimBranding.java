/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/

package org.eclipse.tesla.shell.gshell.internal;

import static org.sonatype.gshell.variables.VariableNames.SHELL_GROUP;
import static org.sonatype.gshell.variables.VariableNames.SHELL_USER_DIR;

import java.io.File;
import javax.inject.Named;
import javax.inject.Singleton;

import org.fusesource.jansi.Ansi;
import org.sonatype.gshell.branding.BrandingSupport;
import org.sonatype.gshell.branding.License;
import org.sonatype.gshell.branding.LicenseSupport;
import org.sonatype.gshell.util.PrintBuffer;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
@Named
@Singleton
public class GShellShimBranding
    extends BrandingSupport
{

    private static final String[] BANNER = {
        " _______        _       _  ",
        "|__   __|      | |     | | ",
        "   | | ___  ___| | __ _| | ",
        "   | |/ _ \\/ __| |/ _` | | ",
        "   | |  __/\\__ \\ | (_| |_| ",
        "   |_|\\___||___/_|\\__,_(_)  THE FUTURE BUILDS WITH TESLA!",
    };

    @Override
    public String getDisplayName()
    {
        return getMessages().format( "displayName" );
    }

    @Override
    public String getWelcomeMessage()
    {
        PrintBuffer buff = new PrintBuffer();
        for ( String line : BANNER )
        {
            buff.println( Ansi.ansi().fg( Ansi.Color.CYAN ).a( line ).reset() );
        }
        buff.println();
        buff.format( "%s (%s)", getDisplayName(), getVersion() ).println();
        buff.println();
        buff.println( "Type '@|bold help|@' for more information." );
        buff.print( line() );
        buff.flush();

        return buff.toString();
    }

    @Override
    public String getGoodbyeMessage()
    {
        return getMessages().format( "goodbye" );
    }

    @Override
    public String getPrompt()
    {
        return String.format( "@|bold %s|@(${%s}):${%s}> ", getProgramName(), SHELL_GROUP, SHELL_USER_DIR + "~." );
    }

    @Override
    public File getUserContextDir()
    {
        return new File( getUserHomeDir(), ".m2/tsh" );
    }

    @Override
    public License getLicense()
    {
        return new LicenseSupport( "Eclipse Public License, 1.0", getClass().getResource( "license.txt" ) );
    }
}