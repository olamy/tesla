package org.apache.maven.cli;

import org.apache.maven.model.building.ModelProcessor;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public class TeslaCli extends MavenCli {
  @Override
  protected void customizeContainer(final PlexusContainer container) {
    try {
      //
      // Override the default ModelProcessor binding. By looking up the PolyglotModelProcessor and
      // using the instance to add a component descriptor to the container it will have a higher
      // rank and be used by default.
      //
      ModelProcessor modelProcessor = container.lookup(ModelProcessor.class, "tesla");
      container.addComponent(modelProcessor, ModelProcessor.class, "default");
    } catch (ComponentLookupException e) {
      //
      // Won't happen
      //
    }
  }

  public static void main(String[] args) {
    final int result = main(args, null);
    System.exit(result);
  }

  public static int main(String[] args, ClassWorld classWorld) {
    TeslaCli cli = new TeslaCli();
    return cli.doMain(new CliRequest(args, classWorld));
  }
}
