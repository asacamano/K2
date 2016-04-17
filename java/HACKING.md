# Hacking in the K2 Crypto Library Java Implementation

Copyright 2014 Google. Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## Read the master HACKING.md file

## Style guide

Follow the guidelines at the
[Google Java Style Guide](http://google-styleguide.googlecode.com/svn/trunk/javaguide.html)

These guidelines are enforced by the Spotless Gradle plugin. You can set up Eclipse to follow
these guidelines by importing the the Eclipse profiles from:

* code-style/eclipse-java-google-style.xml
* code-style/eclipse.importorder

See [the Spotless site](https://github.com/diffplug/spotless) for how to import the profiles.

## Unit tests

All concrete classes should have good unit tests. See ../HACKING.md for info
about what makes a good unit test.

## Getting started

1. Download and install [Gradle](http://www.gradle.org). We only support
Gradle 2.11, but other versions (particularly later version) will probably
work.

2. Make sure you can build the project by running

> gradle build

3. You can build an Eclipse project that can be imported into any workspace
by running

> gradle build eclipse

and then importing the Eclipse project in this directory.

Note that any changes you make to the project will not be commited to the repo.
All build changes need to be made to the gradle file, which is the source of
truth for the build.

Also note that you must run build before running eclipse - so that the generated
protos will exist to be included on the Eclipse classpath.  (Nonexistant directories
are ignored by the Eclipse plugin.)

4. Hack away.
