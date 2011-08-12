#!/bin/sh

distributionId="tesla"
distributionShortName="Tesla"
distributionName="Sonatype Tesla"

ant \
  -Dmaven.home=${HOME}/tesla-3.0.4-SNAPSHOT \
  -DdistributionId="${distributionId}" \
  -DdistributionShortName="${distributionShortName}" \
  -DdistributionName="${distributionName}"
