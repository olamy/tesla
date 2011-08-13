#!/bin/sh

ANT=`which ant`

[ -z ${ANT} ] && ANT=/var/lib/hudson/tools/Ant_1.8.2/bin/ant

echo "Using ${ANT}"

${ANT} \
  -Dmaven.home=${HOME}/tesla-3.0.4-SNAPSHOT \
  -DhostEnvSettings="${HOME}/.m2/settings.xml" \
  -Dmaven.home.exists.continue=yes
