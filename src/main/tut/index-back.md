---
layout: home
title:  "Home"
section: "home"
---

# Kanela: The Kamon Instrumentation Agent
[![Build Status](https://travis-ci.org/kamon-io/kamon-agent.svg?branch=master)](https://travis-ci.org/kamon-io/kamon-agent)

**Kanela** is a Java Agent written in Java 8+ and powered by [ByteBuddy] with some additionally [ASM] features to provide a simple way to instrument applications running on the `JVM` and allow introduce [Kamon][1] features such as context propagation and metrics.

## Getting Started

The Instrumentation API is written in *Java* but there are [extensions modules][2] to define transformations in idiomatic manner for *Scala* and *Kotlin*.

### Time Spent Example([ByteBuddy Advice][3]) in 4 steps

Suppose you have a simple worker that perform a simple operation:

{% code_example %}
{%   language java Example.java tag:worker label:"Java" %}
{%   language scala Example.scala tag:worker label:"Scala" %}
{% endcode_example %}

### Step 1: The Instrumentation

We need introduce a transformation `Before` and `After` method execution in order to measure the time spent in the method execution:

{% code_example %}
{%   language java Example.java tag:instrumentation label:"Java" %}
{%   language scala Example.scala tag:instrumentation label:"Scala" %}
{% endcode_example %}

### Step 2: The Method `Advisor`

{% code_example %}
{%   language java Example.java tag:advisor label:"Java" %}
{%   language scala Example.scala tag:advisor label:"Scala" %}
{% endcode_example %}

### Step 3: The Configuration Module

In our `reference.conf/application.conf` we need to define a new configuration module:

```javascript
kamon.agent {
  show-banner = true
  log-level = "INFO"

  modules {
    time-spent-module {
          name = "Time Spent Module"
          instrumentations = ["instrumentation.TimeSpentInstrumentation"]
          within = ["run..*"]
    }
  }
}
```

### Step 4: Run!

We make some calls to our `Worker` and also pass the **Kanela** agent as JVM parameter

{% code_example %}
{%   language java Example.java tag:run label:"Java" %}
{%   language scala Example.scala tag:run label:"Scala" %}
{% endcode_example %}

```shell
java -javaagent:kanela.jar -jar /path/to/our-application.jar
```

<img class="img-fluid" src="microsite/img/kamon.timespent.png">

There it is! Our application is instrumented with the **Kanela** agent.

Some other configuration that you can define is indicated in the agent [`reference.conf`](https://github.com/kamon-io/kamon-agent/blob/master/agent/src/main/resources/reference.conf)

## Lombok
This project uses [Lombok](https://projectlombok.org/) to reduce boilerplate. You can setup
the [IntelliJ plugin](https://plugins.jetbrains.com/plugin/6317) to add IDE support.

[ByteBuddy]:http://bytebuddy.net/#/
[ASM]:http://asm.ow2.org/

[1]:http://kamon.io
[2]:https://github.com/kamon-io/kamon-agent-extensions
[3]:http://bytebuddy.net/javadoc/1.7.9/net/bytebuddy/asm/Advice.html
