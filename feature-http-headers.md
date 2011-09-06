Based on improvements in Aether 1.12 ([AETHER-98](https://issues.sonatype.org/browse/AETHER-98)), this feature branch
makes Maven configure (repository-specific) HTTP request headers for Aether based on the [Wagon-style server configuration](http://maven.apache.org/guides/mini/guide-http-settings.html#HTTP_Headers)
from the `settings.xml`, i.e.:

    <settings>
      <servers>
        <server>
          <id>my-server</id>
          <configuration>
            <httpHeaders>
              <httpHeader>
                <name>Foo</name>
                <value>Bar</value>
              </httpHeader>
            </httpHeaders>
          </configuration>
        </server>
      </servers>
    </settings>

The above snippet is already functional with stock Maven when the Wagon-based repository connector is used. This branch
decouples support for HTTP headers from Wagon/Plexus and generalizes it to all connectors like AHC.
