Expendable repository mirror selection.

Introduced new MirrorSelectorDelegate component interface. For repositories 
that do not have matching <mirror/> element in global/user settings.xml
files, all registered MirrorSelectorDelegate implementations will be called
in no particular order and first not-null Mirror will be used.

MirrorSelectorDelegate can associate credentials information with returned 
Mirror instances. Because of this, MirrorSelectorDelegate are NOT required to
change repository id when using mirrors. This is useful when mirror is known
to be exact copy of the original repository and use of the same repository id
for direct and mirror access allows better sharing of artifacts in Maven local
repository.

The main underlaying usecase is automatic mirror repository discovery based
on specifics of build environment. For example, a MirrorSelectorDelegate
implementation could negotiate available mirror repositories with a local
repository manager, thus eliminating the need to manually maintain <mirror/>
and <server/> configuration in settings.xml.
