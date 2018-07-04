---
layout: home
title:  "Home"
section: "home"
---

# Kanela: The Kamon Instrumentation Agent
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/kamon-io/Kamon?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
![Build Status](https://travis-ci.org/kamon-io/kanela.svg?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.kamon/kanela-agent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.kamon/kanela-agent)
[![Download](https://api.bintray.com/packages/kamon-io/releases/kanela/images/download.svg) ](https://bintray.com/kamon-io/releases/kanela/_latestVersion)


**Kanela** is a Java Agent written in Java 8+ and powered by [ByteBuddy] with some additionally [ASM] features to provide a simple way to instrument applications running on the `JVM` and allow introduce [Kamon][1] features such as context propagation and metrics.

## Getting Started

The Instrumentation API is written in *Java* but there are [extensions modules][2] to define transformations in idiomatic manner for *Scala* and *Kotlin*.

### Modules ###

The modules currently available are shown below:

#### Web Frameworks

| Server  | Status | Versions  | Description            
|:------:|:------:|:----:|------------------
| [Java Servlet]  | stable | 2.3+, 3.0+ | brings traces and metrics to your servlet based applications.
| [Play Framework] | stable | 2.4-2.6 | with client and server side tracing.
| [Akka HTTP] | stable | 10.0+ | with client and service side tracing and HTTP server metrics.
| [Spring Web] | stable | 4.0+ | rings traces and metrics to your spring based applications.


[Java Servlet]:https://github.com/kamon-io/kamon-servlet
[Play Framework]:https://github.com/kamon-io/kamon-play 
[Akka HTTP]: https://github.com/kamon-io/kamon-akka-http
[Spring Web]: https://github.com/kamon-io/kamon-spring

#### Database Drivers/Frameworks

| Driver  | Status | Versions  | Description            
|:------:|:------:|:----:|------------------
| [JDBC]  | stable | All | gives you metrics and tracing for JDBC statements execution and Hikari pool metrics.
| [Cassandra Driver] | experimental | 3.2+ | gives you metrics, context propagation and tracing for C* statements.
| [Mongo] | experimental | 3.2+ | (**coming soon**)

[JDBC]:https://github.com/kamon-io/kamon-jdbc 
[Cassandra Driver]:https://github.com/kamon-io/kamon-casssandra 
[Mongo]:https://github.com/kamon-io/kamon-mongo


#### Networking Frameworks

| Framework  | Status | Versions  | Description            
|:------:|:------:|:----:|------------------
| [Netty]  | stable | 4.0+ | with client and service side tracing and HTTP server metrics.
| [Akka] | stable | 2.3-2.5 | for actor metrics and tracing inside a single JVM.
| [Akka Remote] | stable | 2.3-2.5 | has now serialization and remoting metrics and is able to trace messages across remote actor systems. (**coming soon**)
| [Spring Web] | stable | 4.0+ | rings traces and metrics to your spring based applications.

[Netty]:https://github.com/kamon-io/kamon-netty 
[Akka]:https://github.com/kamon-io/kamon-akka 
[Akka Remote]: https://github.com/kamon-io/kamon-akka-remote



#### Others

| Framework  | Status | Versions  | Description            
|:------:|:------:|:----:|------------------
| [Futures]  | stable | 2.10-2.12 | bring automatic context propagation for Scala, Finagle and Scalaz futures.
| [Executors] | stable | All | context propagation and collects executor service metrics.
| [Logback] | stable | 2.3-2.5 | comes with utilities for adding trace IDs to your logs and instrumentation to keep context when using async appenders.
| [Annotation] | stable | 2.10-2.12 | provides a set of annotations that allow you to easily integrate Kamon's metrics and tracing facilities with your application.



[Futures]:https://github.com/kamon-io/kamon-futures 
[Executors]:https://github.com/kamon-io/kamon-executors 
[Logback]:https://github.com/kamon-io/kamon-logback
[Annotation]:https://github.com/kamon-io/kamon-annotation 


Don’t see your preferred framework? We’re continually adding additional modules, check with our team to see if we can help or make your custom instrumentation with the **Kanela**
instrumentation API.


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
your `Maven`, `SBT` or `Gradle` application script, or your `java -jar` command:

``
-javaagent:/path/to/the/kanela-agent.jar
``
 
![kanela-plain-jdbc][plain-jdbc]

There it is! Our application is instrumented with the **Kanela** agent.

#### Bundle
First, download the **Kanela** bundle agent that includes all the **Kamon** instrumentation modules embedded within the agent:

``` 
wget -O kanela-agent.jar 'https://search.maven.org/remote_content?g=io.kamon&a=kanela-bundle-agent&v=LATEST'
```

Then, add the following JVM argument when starting your application in your IDE, 
your `Maven`, `SBT` or `Gradle` application script, or your `java -jar` command:

``
-javaagent:/path/to/the/kanela-bundle-agent.jar
``

Finally, Simply Enjoy!

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
[plain-jdbc]: microsite/img/kanela-plain-jdbc.png

[1]:http://kamon.io
[2]:https://github.com/kamon-io/kamon-agent-extensions
[3]:http://bytebuddy.net/javadoc/1.7.9/net/bytebuddy/asm/Advice.html
