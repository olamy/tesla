#!/bin/sh

ANT=`which ant`

[ -z ${ANT} ] && ANT=/var/lib/hudson/tools/Ant_1.8.2/bin/ant

echo "Using ${ANT}"

distributionId="tesla"
distributionShortName="Tesla"
distributionName="Eclipse Tesla"

${ANT} \
  -Dmaven.home=${HOME}/tesla-3.0.4-SNAPSHOT \
  -DdistributionId="${distributionId}" \
  -DdistributionShortName="${distributionShortName}" \
  -DdistributionName="${distributionName}" \
  -DhostEnvSettings="${HOME}/.m2/settings.xml" \
  -Dmaven.home.exists.continue=yes
