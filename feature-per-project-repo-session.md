This feature branch adds support for project-specific Aether configurations by associating a `RepositorySystemSession` with
each `MavenProject` instance. By default, all projects in a build session will use the same Aether configuration, a new
extension point in form of a `ProjectBuilderDelegate` can then be used to customize the Aether session for a given project.
As a concrete example, an extension plugin could configure project-specific HTTP request headers for dependency resolution.

