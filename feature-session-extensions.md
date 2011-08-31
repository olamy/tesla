Session extensions are similar to maven extensions plugins, except session extensions cannot define any goals, only
components. Session extensions are created during maven session setup and their components are available for the entire
duration of the session.

Session extensions are "installed" by creating an xml file in extension configuration directory, ~/.m2/ext/ by
default. -Dmaven.ext.conf.dir=<some-path> can be used to specify different configuration directory path and 
-Dmaven.ext.conf.dir can be used to suppress session extensions. 

Extensions configuration files 

TODO

Supported component configuration parameters

${settings} merged global, user and extensions settings. <pluginRepositories/> and <properties/> from extension.xml
file are injected in a new settings profile named after the extension, i.e. if the extension is defined in file 
foo-extension.xml, the injected profile will have id foo. <server/> elements defined in extension.xml file override 
<server/> elements with same id defined in settings.xml file.

${property} String value with corresponding key in <properties/> map. <properties/> map is merged from extension.xml,
active profiles in settings.xml, user properties and system properties. 
