/**********************************************************************************************************************
 * Copyright (c) 2011 to original author or authors                                                                   *
 * All rights reserved. This program and the accompanying materials                                                   *
 * are made available under the terms of the Eclipse Public License v1.0                                              *
 * which accompanies this distribution, and is available at                                                           *
 *   http://www.eclipse.org/legal/epl-v10.html                                                                        *
 **********************************************************************************************************************/
package org.eclipse.tesla.osgi.provision.url.masor.internal;

import static org.eclipse.tesla.osgi.provision.url.maor.Constants.PROTOCOL_MAOR;
import static org.eclipse.tesla.osgi.provision.url.masor.Constants.PROTOCOL_MASOR;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.felix.bundlerepository.DataModelHelper;
import org.apache.felix.bundlerepository.Resource;
import org.apache.felix.bundlerepository.impl.DataModelHelperImpl;
import org.apache.felix.bundlerepository.impl.Referral;
import org.apache.felix.bundlerepository.impl.RepositoryImpl;
import org.eclipse.tesla.osgi.provision.Storage;
import org.eclipse.tesla.osgi.provision.internal.IOUtils;
import org.eclipse.tesla.osgi.provision.url.masor.MavenArtifactSetObrRepository;

/**
 * @author <a href="mailto:adreghiciu@gmail.com">Alin Dreghiciu</a>
 * @since 3.0.4
 */
@Named
public class DefaultMavenArtifactSetObrRepository
    implements MavenArtifactSetObrRepository
{

    static final String OBR_PATH_TEMPLATE = "masor/%s/obr.xml";

    private Storage storage;

    private Digester digester;

    @Inject
    public DefaultMavenArtifactSetObrRepository( final Storage storage,
                                                 final Digester digester )
    {
        this.storage = storage;
        this.digester = digester;
    }

    @Override
    public String create( final String... coordinates )
    {
        try
        {
            final String digest = digester.digest( coordinates );
            final DataModelHelper helper = new DataModelHelperImpl();
            final RepositoryImpl repository = (RepositoryImpl) helper.repository( new Resource[0] );

            for ( final String coordinate : coordinates )
            {
                final Referral referral = new Referral();
                referral.setDepth( "0" );
                referral.setUrl( PROTOCOL_MAOR + ":" + coordinate );
                repository.addReferral( referral );
            }
            Writer out = null;
            try
            {
                out = new OutputStreamWriter( storage.outputStreamFor( obrPathFor( digest ) ) );
                helper.writeRepository( repository, out );
            }
            finally
            {
                IOUtils.close( out );
            }
            return PROTOCOL_MASOR + ":" + digest;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public InputStream openStream( final String path )
    {
        return storage.inputStreamFor( obrPathFor( path ) );
    }

    private String obrPathFor( final String path )
    {
        return String.format( OBR_PATH_TEMPLATE, path );
    }

}
