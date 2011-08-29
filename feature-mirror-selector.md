Automatic private repository mirror selection

#### User stories

* As a developer, I want to have local (as in "on my development machine") mirrors
  of remote artifact repositories used by my projects, so my builds run faster and
  more reliably.
* As a development manager, I want to have local (as in "fully under our
  company's control")  mirrors of all remote repositories used by my company's
  builds, so the builds continue to work when original repositories become 
  unavailable (temporary or permanently) and to have additional layer of
  control of external dependencies for audit/legal purposes.
* As an opensource developer, I want my builds to use local mirrors when
  available and access remote repositories directly when local mirrors are not
  available.

  
#### Problems with mirrorOf=* or mirrorOf=external:*

* mirrorOf must include all remote repositories used by all builds. Builds that
  use repositories not included in the mirror fail.
* mirrorOf exposes builds to artifacts that should not be visible according to
  projects' pom.xml files. Builds fail with different mirrorOf or no mirrorOf
* mirror is most often implemented as a group repository, which makes the group
  repository configuration implicitly part of project build configuration.
  Changes to the group repository configuration makes builds not-reproducible
  (either fail or produce different results).
* [implementation] Maven and Tycho track artifact-repository association in
  local repository. Switching between mirrorOf and direct access makes maven
  re-download remote artifacts.

#### Problems with specific mirrorOf=<repo>

* tedious to maintain
* even without any mirrorOf (i.e. direct access) all password protected
  repositories require separate <server/> element

#### Automatic mirror selection

High-level idea -- build system (i.e. Maven) sends list of repositories 
configured in project pom.xml file to a repository manager (i.e. nexus). For
each repository, the repository manager instructs the build system to either
use a mirror, access repository directly or block the repository access
altogether. 

An important aspect of this design, is that mirrors do not affect set of
artifacts visible to the builds and so the repository manager can change
mirror selection policies over time without breaking build reproducibility
(unless some repositories are blocked, that is, but this is a separate concern).
This property opens up some interesting possibilities. For example, the
repository manager can automatically or semi-automatically create new mirrors
based on repositories used by the projects.


##### Specific example

This example relies on complimentary new Maven core feature that allows 
user-configurable extensions. This scenario describes dependency resolution
when there is no ~/.m2/settings.xml and the following xml configuration file
is created as ~/.m2/ext/nexus-mirror-selector.xml

* during maven session setup, maven will read the xml file, resolve specified 
  main extension artifact (i.e. com.sonatype.nexus.plugins:nexus-mirror-selector) 
  from specified repositories (i.e. nexus and central) and will load resolved 
  artifacts in a separate extensions class realm. 
* nexus-mirror-selector defines NexusMirrorSelector component, which is an 
  implementation of MirrorSelectorDelegate component interface.
* for each repository and pluginRepository, Maven will
*# check if there is matching <mirror/> element in user or global settings.xml file. in this particular example, there is no user settings.xml, so there is no matching <mirror/>
*# invoke each available MirrorSelectorDelegate implementation, in this particular example NexusMirrorSelector defined by nexus-mirror-selector extensions
*# NexusMirrorSelector will send "resolve repository mirror" request to nexus server, providing the server with repository url and layout information
*# Nexus server will attempt to find proxy or hosted repository with matching url and layout. Mirror repository url, if found, is returned to NexusMirrorSelector using "mirror selected" response
*# NexusMirrorSelector returns selected mirror along with credentials used to access Nexus to Maven core
