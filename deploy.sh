#!/bin/sh

curl -u admin:jvz91131 --request PUT @${1} http://jvz.io:8081/nexus/content/repositories/snapshots
