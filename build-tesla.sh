#!/bin/sh

distributionId="tesla"
distributionShortName="Tesla"
distributionName="Sonatype Tesla"

mvn clean package -Dmaven.test.skip \
  -DdistributionId="${distributionId}" \
  -DdistributionShortName="${distributionShortName}" \
  -DdistributionName="${distributionName}"
