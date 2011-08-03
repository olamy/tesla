#!/bin/sh

distributionId="tesla"
distributionShortName="Tesla"
distributionName="Sonatype Tesla"

mvn clean package \
  -DdistributionId="${distributionId}" \
  -DdistributionShortName="${distributionShortName}" \
  -DdistributionName="${distributionName}"
