This branch introduces a new extension point `RepositorySystemSessionFactoryDelegate` for core extensions to customize
the base Aether configuration to use. In more practical terms, this feature allows extensions to change conflict resolution,
authentication/proxy/mirror selection or to introduce additional workspaces to overlay artifacts.

There is generally a chicken-egg scenario when it comes to customizing the Aether configuration via extensions. Obviously,
any extension that is resolved from a repository cannot customize the Aether configuration used during its own resolution
process. As such, this feature targets extensions that get manually installed into `lib/ext` of a Maven distribution or
via bundle fragments for M2E's Maven runtime. Open for discussion/review is the interaction with session extensions.
