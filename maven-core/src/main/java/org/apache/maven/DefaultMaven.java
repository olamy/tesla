package org.apache.maven;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulationException;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ProjectDependencyGraph;
import org.apache.maven.extension.internal.SessionExtensionLoader;
import org.apache.maven.lifecycle.internal.ExecutionEventCatapult;
import org.apache.maven.lifecycle.internal.LifecycleStarter;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemUtils;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.building.UrlModelSource;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.project.DuplicateProjectException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.apache.maven.project.ProjectSorter;
import org.apache.maven.repository.DelegatingLocalArtifactRepository;
import org.apache.maven.repository.LocalRepositoryNotAccessibleException;
import org.apache.maven.repository.internal.RepositorySystemSessionFactory;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.sonatype.aether.util.repository.ChainedWorkspaceReader;

/**
 * @author Jason van Zyl
 */
@Component(role = Maven.class)
public class DefaultMaven
    implements Maven
{

    @Requirement
    private Logger logger;

    @Requirement
    protected ProjectBuilder projectBuilder;

    @Requirement
    private LifecycleStarter lifecycleStarter;

    @Requirement
    protected PlexusContainer container;

    @Requirement
    private MavenExecutionRequestPopulator populator;

    @Requirement
    private ExecutionEventCatapult eventCatapult;

    @Requirement
    private RepositorySystemSessionFactory repoSessionFactory;

    @Requirement
    private LegacySupport legacySupport;

    @Requirement
    private SessionExtensionLoader sessionExtensionLoader;

    public MavenExecutionResult execute( MavenExecutionRequest request )
    {
        MavenExecutionResult result;

        try
        {
            result = doExecute( populator.populateDefaults( request ) );
        }
        catch ( OutOfMemoryError e )
        {
            result = processResult( new DefaultMavenExecutionResult(), e );
        }
        catch ( MavenExecutionRequestPopulationException e )
        {
            result = processResult( new DefaultMavenExecutionResult(), e );
        }
        catch ( RuntimeException e )
        {
            result =
                processResult( new DefaultMavenExecutionResult(),
                               new InternalErrorException( "Internal error: " + e, e ) );
        }
        finally
        {
            legacySupport.setSession( null );
        }

        return result;
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown", "ThrowableResultOfMethodCallIgnored"})
    private MavenExecutionResult doExecute( MavenExecutionRequest request )
    {
        //TODO: Need a general way to inject standard properties
        if ( request.getStartTime() != null )
        {
            request.getSystemProperties().put( "${build.timestamp}", new SimpleDateFormat( "yyyyMMdd-hhmm" ).format( request.getStartTime() ) );
        }        
        
        request.setStartTime( new Date() );
        
        MavenExecutionResult result = new DefaultMavenExecutionResult();

        try
        {
            validateLocalRepository( request );
        }
        catch ( LocalRepositoryNotAccessibleException e )
        {
            return processResult( result, e );
        }

        DelegatingLocalArtifactRepository delegatingLocalArtifactRepository =
            new DelegatingLocalArtifactRepository( request.getLocalRepository() );
        
        request.setLocalRepository( delegatingLocalArtifactRepository );        

        DefaultRepositorySystemSession repoSession = (DefaultRepositorySystemSession) newRepositorySession( request );

        ClassLoader sessionRealm;
        try
        {
            sessionRealm = sessionExtensionLoader.loadExtensions( request, repoSession );
        }
        catch ( Exception e )
        {
            return processResult( result, e );
        }

        MavenSession session = new MavenSession( container, repoSession, request, result );
        session.setClassRealm( sessionRealm );

        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader( ( sessionRealm != null ) ? sessionRealm
                                                                          : container.getContainerRealm() );

            return doExecute( request, result, session );
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( tccl );
        }
    }

    private MavenExecutionResult doExecute( MavenExecutionRequest request, MavenExecutionResult result,
                                            MavenSession session )
    {
        DefaultRepositorySystemSession repoSession = (DefaultRepositorySystemSession) session.getRepositorySession();

        legacySupport.setSession( session );

        try
        {
            for ( AbstractMavenLifecycleParticipant listener : getLifecycleParticipants( Collections.<MavenProject> emptyList() ) )
            {
                listener.afterSessionStart( session );
            }
        }
        catch ( MavenExecutionException e )
        {
            return processResult( result, e );
        }

        eventCatapult.fire( ExecutionEvent.Type.ProjectDiscoveryStarted, session, null );

        request.getProjectBuildingRequest().setRepositorySession( session.getRepositorySession() );

        List<MavenProject> projects;
        try
        {
            projects = getProjectsForMavenReactor( request, session.getClassRealm() );
        }
        catch ( ProjectBuildingException e )
        {
            return processResult( result, e );
        }

        session.setProjects( projects );

        result.setTopologicallySortedProjects( session.getProjects() );

        result.setProject( session.getTopLevelProject() );

        try
        {
            Map<String, MavenProject> projectMap;
            projectMap = getProjectMap( session.getProjects() );

            // Desired order of precedence for local artifact repositories
            //
            // Reactor
            // Workspace
            // User Local Repository
            ReactorReader reactorRepository = new ReactorReader( projectMap );

            repoSession.setWorkspaceReader( ChainedWorkspaceReader.newInstance( reactorRepository,
                                                                                repoSession.getWorkspaceReader() ) );
        }
        catch ( org.apache.maven.DuplicateProjectException e )
        {
            return processResult( result, e );
        }

        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            for ( AbstractMavenLifecycleParticipant listener : getLifecycleParticipants( projects ) )
            {
                Thread.currentThread().setContextClassLoader( listener.getClass().getClassLoader() );

                listener.afterProjectsRead( session );
            }
        }
        catch ( MavenExecutionException e )
        {
            return processResult( result, e );
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( originalClassLoader );
        }

        try
        {
            ProjectSorter projectSorter = new ProjectSorter( session.getProjects() );

            ProjectDependencyGraph projectDependencyGraph = createDependencyGraph( projectSorter, request );

            session.setProjects( projectDependencyGraph.getSortedProjects() );

            session.setProjectDependencyGraph( projectDependencyGraph );
        }
        catch ( CycleDetectedException e )
        {
            String message = "The projects in the reactor contain a cyclic reference: " + e.getMessage();

            ProjectCycleException error = new ProjectCycleException( message, e );

            return processResult( result, error );
        }
        catch ( DuplicateProjectException e )
        {
            return processResult( result, e );
        }
        catch ( MavenExecutionException e )
        {
            return processResult( result, e );
        }

        result.setTopologicallySortedProjects( session.getProjects() );

        if ( result.hasExceptions() )
        {
            return result;
        }

        lifecycleStarter.execute( session );

        validateActivatedProfiles( session.getProjects(), request.getActiveProfiles() );

        if ( session.getResult().hasExceptions() )
        {
            return processResult( result, session.getResult().getExceptions().get( 0 ) );
        }

        return result;
    }

    public RepositorySystemSession newRepositorySession( MavenExecutionRequest request )
    {
        RepositorySystemSession session = repoSessionFactory.newRepositorySession( request );
        if ( !( session instanceof DefaultRepositorySystemSession ) )
        {
            session = new DefaultRepositorySystemSession( session );
        }
        return session;
    }
    
    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private void validateLocalRepository( MavenExecutionRequest request )
        throws LocalRepositoryNotAccessibleException
    {
        File localRepoDir = request.getLocalRepositoryPath();

        logger.debug( "Using local repository at " + localRepoDir );

        localRepoDir.mkdirs();

        if ( !localRepoDir.isDirectory() )
        {
            throw new LocalRepositoryNotAccessibleException( "Could not create local repository at " + localRepoDir );
        }
    }

    private Collection<AbstractMavenLifecycleParticipant> getLifecycleParticipants( Collection<MavenProject> projects )
    {
        Collection<AbstractMavenLifecycleParticipant> lifecycleListeners =
            new LinkedHashSet<AbstractMavenLifecycleParticipant>();

        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            try
            {
                lifecycleListeners.addAll( container.lookupList( AbstractMavenLifecycleParticipant.class ) );
            }
            catch ( ComponentLookupException e )
            {
                // this is just silly, lookupList should return an empty list!
                logger.warn( "Failed to lookup lifecycle participants: " + e.getMessage() );
            }

            Collection<ClassLoader> scannedRealms = new HashSet<ClassLoader>();

            for ( MavenProject project : projects )
            {
                ClassLoader projectRealm = project.getClassRealm();

                if ( projectRealm != null && scannedRealms.add( projectRealm ) )
                {
                    Thread.currentThread().setContextClassLoader( projectRealm );

                    try
                    {
                        lifecycleListeners.addAll( container.lookupList( AbstractMavenLifecycleParticipant.class ) );
                    }
                    catch ( ComponentLookupException e )
                    {
                        // this is just silly, lookupList should return an empty list!
                        logger.warn( "Failed to lookup lifecycle participants: " + e.getMessage() );
                    }
                }
            }
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( originalClassLoader );
        }

        return lifecycleListeners;
    }

    private MavenExecutionResult processResult( MavenExecutionResult result, Throwable e )
    {
        if ( !result.getExceptions().contains( e ) )
        {
            result.addException( e );
        }

        return result;
    }
    
    private List<MavenProject> getProjectsForMavenReactor( MavenExecutionRequest request, ClassLoader sessionRealm )
        throws ProjectBuildingException
    {
        ProjectBuildingRequest projectBuildingRequest = request.getProjectBuildingRequest();
        projectBuildingRequest.setSessionRealm( sessionRealm );

        List<MavenProject> projects = new ArrayList<MavenProject>();

        // We have no POM file.
        //
        if ( request.getPom() == null )
        {
            ModelSource modelSource = new UrlModelSource( DefaultMaven.class.getResource( "project/standalone.xml" ) );
            MavenProject project = projectBuilder.build( modelSource, projectBuildingRequest ).getProject();
            project.setExecutionRoot( true );
            projects.add( project );
            request.setProjectPresent( false );
            return projects;
        }

        List<File> files = Arrays.asList( request.getPom().getAbsoluteFile() );
        collectProjects( projects, files, projectBuildingRequest, request.isRecursive() );
        return projects;
    }

    private Map<String, MavenProject> getProjectMap( List<MavenProject> projects )
        throws org.apache.maven.DuplicateProjectException
    {
        Map<String, MavenProject> index = new LinkedHashMap<String, MavenProject>();
        Map<String, List<File>> collisions = new LinkedHashMap<String, List<File>>();

        for ( MavenProject project : projects )
        {
            String projectId = ArtifactUtils.key( project.getGroupId(), project.getArtifactId(), project.getVersion() );

            MavenProject collision = index.get( projectId );

            if ( collision == null )
            {
                index.put( projectId, project );
            }
            else
            {
                List<File> pomFiles = collisions.get( projectId );

                if ( pomFiles == null )
                {
                    pomFiles = new ArrayList<File>( Arrays.asList( collision.getFile(), project.getFile() ) );
                    collisions.put( projectId, pomFiles );
                }
                else
                {
                    pomFiles.add( project.getFile() );
                }
            }
        }

        if ( !collisions.isEmpty() )
        {
            throw new org.apache.maven.DuplicateProjectException( "Two or more projects in the reactor"
                + " have the same identifier, please make sure that <groupId>:<artifactId>:<version>"
                + " is unique for each project: " + collisions, collisions );
        }

        return index;
    }

    private void collectProjects( List<MavenProject> projects, List<File> files, ProjectBuildingRequest projectBuildingRequest, boolean recursive )
        throws ProjectBuildingException
    {
        List<ProjectBuildingResult> results = projectBuilder.build( files, recursive, projectBuildingRequest );

        boolean problems = false;

        for ( ProjectBuildingResult result : results )
        {
            projects.add( result.getProject() );

            if ( !result.getProblems().isEmpty() && logger.isWarnEnabled() )
            {
                logger.warn( "" );
                logger.warn( "Some problems were encountered while building the effective model for "
                    + result.getProject().getId() );

                for ( ModelProblem problem : result.getProblems() )
                {
                    String location = ModelProblemUtils.formatLocation( problem, result.getProjectId() );
                    logger.warn( problem.getMessage() + ( StringUtils.isNotEmpty( location ) ? " @ " + location : "" ) );
                }

                problems = true;
            }
        }

        if ( problems )
        {
            logger.warn( "" );
            logger.warn( "It is highly recommended to fix these problems"
                + " because they threaten the stability of your build." );
            logger.warn( "" );
            logger.warn( "For this reason, future Maven versions might no"
                + " longer support building such malformed projects." );
            logger.warn( "" );
        }
    }

    private void validateActivatedProfiles( List<MavenProject> projects, List<String> activeProfileIds )
    {
        Collection<String> notActivatedProfileIds = new LinkedHashSet<String>( activeProfileIds );

        for ( MavenProject project : projects )
        {
            for ( List<String> profileIds : project.getInjectedProfileIds().values() )
            {
                notActivatedProfileIds.removeAll( profileIds );
            }
        }

        for ( String notActivatedProfileId : notActivatedProfileIds )
        {
            logger.warn( "The requested profile \"" + notActivatedProfileId
                + "\" could not be activated because it does not exist." );
        }
    }

    protected Logger getLogger()
    {
        return logger;
    }

    private ProjectDependencyGraph createDependencyGraph( ProjectSorter sorter, MavenExecutionRequest request )
        throws MavenExecutionException
    {
        ProjectDependencyGraph graph = new DefaultProjectDependencyGraph( sorter );

        List<MavenProject> activeProjects = sorter.getSortedProjects();

        activeProjects = trimSelectedProjects( activeProjects, graph, request );
        activeProjects = trimResumedProjects( activeProjects, request );

        if ( activeProjects.size() != sorter.getSortedProjects().size() )
        {
            graph = new FilteredProjectDependencyGraph( graph, activeProjects );
        }

        return graph;
    }

    private List<MavenProject> trimSelectedProjects( List<MavenProject> projects, ProjectDependencyGraph graph,
                                                     MavenExecutionRequest request )
        throws MavenExecutionException
    {
        List<MavenProject> result = projects;

        if ( !request.getSelectedProjects().isEmpty() )
        {
            File reactorDirectory = null;
            if ( request.getBaseDirectory() != null )
            {
                reactorDirectory = new File( request.getBaseDirectory() );
            }

            Collection<MavenProject> selectedProjects = new LinkedHashSet<MavenProject>( projects.size() );

            for ( String selector : request.getSelectedProjects() )
            {
                MavenProject selectedProject = null;

                for ( MavenProject project : projects )
                {
                    if ( isMatchingProject( project, selector, reactorDirectory ) )
                    {
                        selectedProject = project;
                        break;
                    }
                }

                if ( selectedProject != null )
                {
                    selectedProjects.add( selectedProject );
                }
                else
                {
                    throw new MavenExecutionException( "Could not find the selected project in the reactor: "
                        + selector, request.getPom() );
                }
            }

            boolean makeUpstream = false;
            boolean makeDownstream = false;

            if ( MavenExecutionRequest.REACTOR_MAKE_UPSTREAM.equals( request.getMakeBehavior() ) )
            {
                makeUpstream = true;
            }
            else if ( MavenExecutionRequest.REACTOR_MAKE_DOWNSTREAM.equals( request.getMakeBehavior() ) )
            {
                makeDownstream = true;
            }
            else if ( MavenExecutionRequest.REACTOR_MAKE_BOTH.equals( request.getMakeBehavior() ) )
            {
                makeUpstream = true;
                makeDownstream = true;
            }
            else if ( StringUtils.isNotEmpty( request.getMakeBehavior() ) )
            {
                throw new MavenExecutionException( "Invalid reactor make behavior: " + request.getMakeBehavior(),
                                                   request.getPom() );
            }

            if ( makeUpstream || makeDownstream )
            {
                for ( MavenProject selectedProject : new ArrayList<MavenProject>( selectedProjects ) )
                {
                    if ( makeUpstream )
                    {
                        selectedProjects.addAll( graph.getUpstreamProjects( selectedProject, true ) );
                    }
                    if ( makeDownstream )
                    {
                        selectedProjects.addAll( graph.getDownstreamProjects( selectedProject, true ) );
                    }
                }
            }

            result = new ArrayList<MavenProject>( selectedProjects.size() );

            for ( MavenProject project : projects )
            {
                if ( selectedProjects.contains( project ) )
                {
                    result.add( project );
                }
            }
        }

        return result;
    }

    private List<MavenProject> trimResumedProjects( List<MavenProject> projects, MavenExecutionRequest request )
        throws MavenExecutionException
    {
        List<MavenProject> result = projects;

        if ( StringUtils.isNotEmpty( request.getResumeFrom() ) )
        {
            File reactorDirectory = null;
            if ( request.getBaseDirectory() != null )
            {
                reactorDirectory = new File( request.getBaseDirectory() );
            }

            String selector = request.getResumeFrom();

            result = new ArrayList<MavenProject>( projects.size() );

            boolean resumed = false;

            for ( MavenProject project : projects )
            {
                if ( !resumed && isMatchingProject( project, selector, reactorDirectory ) )
                {
                    resumed = true;
                }

                if ( resumed )
                {
                    result.add( project );
                }
            }

            if ( !resumed )
            {
                throw new MavenExecutionException( "Could not find project to resume reactor build from: " + selector
                    + " vs " + projects, request.getPom() );
            }
        }

        return result;
    }

    private boolean isMatchingProject( MavenProject project, String selector, File reactorDirectory )
    {
        // [groupId]:artifactId
        if ( selector.indexOf( ':' ) >= 0 )
        {
            String id = ':' + project.getArtifactId();

            if ( id.equals( selector ) )
            {
                return true;
            }

            id = project.getGroupId() + id;

            if ( id.equals( selector ) )
            {
                return true;
            }
        }

        // relative path, e.g. "sub", "../sub" or "."
        else if ( reactorDirectory != null )
        {
            File selectedProject = new File( new File( reactorDirectory, selector ).toURI().normalize() );

            if ( selectedProject.isFile() )
            {
                return selectedProject.equals( project.getFile() );
            }
            else if ( selectedProject.isDirectory() )
            {
                return selectedProject.equals( project.getBasedir() );
            }
        }

        return false;
    }

}
