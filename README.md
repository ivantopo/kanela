# Kanela: The Kamon Instrumentation Agent <img align="right" src="https://rawgit.com/kamon-io/Kamon/master/kamon-logo.svg" height="150px" style="padding-left: 20px"/>
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/kamon-io/Kamon?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
![Build Status](https://travis-ci.org/kamon-io/kanela.svg?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.kamon/kanela-agent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.kamon/kanela-agent)
[![Download](https://api.bintray.com/packages/kamon-io/releases/kanela/images/download.svg) ](https://bintray.com/kamon-io/releases/kanela/_latestVersion)

**Kanela** is a Java Agent written in Java 8+ and powered by [ByteBuddy] with some additionally [ASM] features to provide a simple way to instrument applications running on the JVM and allow introduce Kamon features such as context propagation and metrics.

- Continue to the new :sparkles: [Microsite](http://kamon-io.github.io/kanela/) :sparkles:
- Read the [**Changelog**](CHANGELOG.md) for more info.

### Modules ###

The modules currently available are:
  - [Akka](https://github.com/kamon-io/kamon-akka) for actor metrics and tracing inside a single JVM.
  - [Akka HTTP](https://github.com/kamon-io/kamon-akka-http) with client and service side tracing and HTTP server metrics.
  - [Futures](https://github.com/kamon-io/kamon-futures) bring automatic context propagation for Scala, Finagle and
    Scalaz futures.
  - [Executors](https://github.com/kamon-io/kamon-executors) context propagation and collects executor service metrics.
  - [Play Framework](https://github.com/kamon-io/kamon-play) with client and server side tracing.
  - [JDBC](https://github.com/kamon-io/kamon-jdbc) gives you metrics and tracing for JDBC statements execution and
    Hikari pool metrics.
  - [Logback](https://github.com/kamon-io/kamon-logback) comes with utilities for adding trace IDs to your logs and
    instrumentation to keep context when using async appenders.
  - [Spring Framework](https://github.com/kamon-io/kamon-spring) with client and service side tracing, context propagation and HTTP server metrics.
  - [Cassandra Driver](https://github.com/kamon-io/kamon-casssandra) gives you metrics, context propagation and tracing for C* statements.
  - [Annotation](https://github.com/kamon-io/kamon-annotation) provides a set of annotations that allow you to easily integrate Kamon's metrics and tracing facilities with your application.
  - [Netty](https://github.com/kamon-io/kamon-netty) with client and service side tracing and HTTP server metrics.(**wip**)
  - [OkHttp](https://github.com/kamon-io/kamon-okhttp) (**soon**)
  - [Kafka](https://github.com/kamon-io/kamon-kafka) (**soon**)
  - [Mongo](https://github.com/kamon-io/kamon-mongo) (**soon**)
  - [Akka Remote](https://github.com/kamon-io/kamon-akka-remote) has now serialization and remoting metrics and is able. (**soon**)

### Flavors ###
**Kanela** has 2 flavors, one is just the plain agent and gives us the option to add only the modules we need and 
the other is for those who want **APM-like experience** from the very beginning that contains all the modules embedded within the agent.

#### Plain
First, download the **Kanela** plain agent:

``` 
wget -O kanela-agent.jar 'https://search.maven.org/remote_content?g=io.kamon&a=kanela-agent&v=LATEST'
```

Then, simply add the instrumentation modules that you want in your `build.sbt`, `pom.xml` or `gradle.build` file:

```
libraryDependencies += "io.kamon" %% "kamon-jdbc" % "1.0.0"
```

```xml
<dependency>
    <groupId>io.kamon</groupId>
    <artifactId>kamon-jdbc_2.12</artifactId>
    <version>1.0.0</version>
</dependency>
``` 

```
compile group: 'io.kamon', name: 'kamon-jdbc_2.12', version: "1.0.0"
```

Finally, add the following JVM argument when starting your application in your IDE, 
your Maven, Sbt or Gradle application script, or your java -jar command:

``
-javaagent:/path/to/the/kanela--agent.jar
``

 
![kanela-plain-jdbc][plain-jdbc]


#### Bundle


## License

This software is licensed under the Apache 2 license, quoted below.

Copyright © 2013-2018 the kamon project <http://kamon.io>

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    [http://www.apache.org/licenses/LICENSE-2.0]

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.


[ByteBuddy]:http://bytebuddy.net/#/
[ASM]:http://asm.ow2.org/
[plain-jdbc]: img/kanela-plain-jdbc.png
