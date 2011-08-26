package org.apache.maven.cli;

import org.apache.maven.model.building.ModelProcessor;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.component.composition.CycleDetectedInComponentGraphException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public class PolyglotMavenCli
    extends MavenCli
{
  @Override
  protected void customizeContainer( final PlexusContainer container )
  {
      assert container != null;

      // HACK: Wedge our processor in as the default
      ComponentDescriptor<?> source = container.getComponentDescriptor( ModelProcessor.class.getName(), "polyglot" );
      ComponentDescriptor<?> target = container.getComponentDescriptor( ModelProcessor.class.getName(), "default" );
      target.setImplementation( source.getImplementation() );

      // delete the old requirements and replace them with the new
      // with size == 0 is getRequirements is an emptyList which is immutable
      if ( target.getRequirements().size() > 0 )
      {
          target.getRequirements().clear();
      }
      for ( ComponentRequirement requirement : source.getRequirements() )
      {
          target.addRequirement( requirement );
      }

      // TODO this should not be needed
      ComponentRequirement manager = new ComponentRequirement();
      manager.setFieldName( "manager" );
      manager.setRole( "org.sonatype.maven.polyglot.PolyglotModelManager" );
      manager.setRoleHint( "default" );
      target.addRequirement( manager );

      try
      {
          container.addComponentDescriptor( target );
      }
      catch ( final CycleDetectedInComponentGraphException e )
      {
          throw new RuntimeException( e );
      }
  }
      
    public static void main( String[] args )                                                                                     
    {                                                                                                                                  
        final int result = main( args, null );                                                                                         
        System.exit( result );                                                                                                         
    }                                                                                                                                  
                                                                                                                                       
    public static int main( String[] args, ClassWorld classWorld )                                                         
    {                                                                                                                                  
        PolyglotMavenCli cli = new PolyglotMavenCli();                                                                           
        return cli.doMain( new CliRequest( args, classWorld ) );                                                                       
    }            
}
