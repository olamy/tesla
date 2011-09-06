The JSR-330 support enables Maven plugins and their dependent libraries to:
- use `@javax.inject.Inject` for injection of required components (replacing Plexus' `@Requirement`)
- use `@javax.inject.Inject` for injection of a SLF4J logger that integrates with Maven's diagnostics (replacing Plexus' proprietary logging)
- use `@javax.inject.Named` in combination with an index created by the sisu-maven-plugin to advertise available components for auto-binding (replacing `META-INF/plexus/components.xml`)

It's worth to clarify that the injected SLF4J loggers are still backed by a simple console-based output. Integration of
Logback or other SLF4J bindings is outside the scope of this feature branch.

The feature is generally complete but commit 7cfd82e4778115668fbfab65af7cbf26113fe2a2 needs to be reviewed prior to
release in order to ensure we don't introduce more coupling between Guice internals and Maven than necessary.
