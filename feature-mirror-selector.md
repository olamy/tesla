Introduced MirrorSelectorDelegate optional components

Changed mirror selection logic to first consider mirror
configuration from settings.xml, as it always did.

If no mirror is found in settings.xml, each MirrorSelectorDelegate
found, if any, will be used.

Repository will be accessed directly if neither settings.xml nor
delegates provide mirror information.
