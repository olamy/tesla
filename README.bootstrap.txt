# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

BOOTSTRAPPING BASICS
-----------------------

You'll need:

- Java 1.5
- Ant 1.6.5 or later

First, give Ant a location into which the completed Maven distro should be installed:

    export M2_HOME=$HOME/apps/maven/apache-maven-3.0-SNAPSHOT

Then, run Ant:

    ant

Once the build completes, you should have a new Maven distro ready to roll in your $M2_HOME directory!

Enjoy.