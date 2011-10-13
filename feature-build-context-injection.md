Automatic BuildContext injection and lifecycle management

New BuildContext instance is automatically created and associated with each
project mojo execution and can be injected as a plexus or jsr330 component
to the mojo itself of one of components directly or indirected used by the
mojo.

BuildContex is automatically closed after mojo execution and execution failure
exception is raised if there are not cleared error messages reported to the 
build context.

Digested information about maven plugin artifact and all its dependencies is
automatically stored in the build context and full build is triggered if any
of the artifact changes. Likewise, project effective pom and session user
properties are stored in the build context and changes to either will result
in full build.

Open questions

* decide if settings.xml or some parts of it should be considered part of
  configuration
* decide if session all/some system properties should be considered part of
  configuration
* deal with volatile properties like ${maven.build.timestamp}
* write ITs
* either cleanup tesla-build-avoidance API and implementation or define 
  separate tesla-specific and hopefully easier to understand and use 
  BuildContext interface
