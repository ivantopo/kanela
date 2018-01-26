---
layout: home
title:  "Home"
section: "home"
---

# Kamon Agent
[![Build Status](https://travis-ci.org/kamon-io/kamon-agent.svg?branch=master)](https://travis-ci.org/kamon-io/kamon-agent)

The **kamon-agent** is developed in order to provide a simple way to instrument an application running on the JVM and
introduce kamon features such as, creation of traces, metric measures, trace propagation, and so on.

It's a simple Java Agent written in Java 8 and powered by [ByteBuddy] with some additionally [ASM] features. It has a Pure-Java API and a
Scala-Friendly API to define the custom instrumentation in a declarative manner.

Kamon has several module that need to instrument the app to introduce itself in the internal components. Introducing this Agent,
you have other way to instrument your `app / library / framework` through a simple and declarative API and get additional features such as
retransformation of the loaded classes (so it's possible to attach agent on the runtime), revoke the instrumentation
when the app is in a critical state, and so on.

### How to use the Agent API?

The API has a version for *Java* and other one for *Scala*. To define the transformations you have to extends the
`KamonInstrumentation` type (picking the Java or the Scala version) and define a new module in the configuration, as you can see
in the following example.

## Example

Suppose you have a simple worker that perform a simple operation:

{% code_example %}
{%   language java Example.java tag:worker label:"Java" %}
{%   language scala Example.scala tag:worker label:"Scala" %}
{% endcode_example %}


And introduce some transformations in order to modify the bytecode and hook into the internal app.

{% code_example %}
{%   language java Example.java tag:instrumentation label:"Java" %}
{%   language scala Example.scala tag:instrumentation label:"Scala" %}
{% endcode_example %}


{% code_example %}
{%   language java Example.java tag:advisor label:"Java" %}
{%   language scala Example.scala tag:advisor label:"Scala" %}
{% endcode_example %}

Finally, we need to define a new module in the kamon agent configuration:

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

And you are ready to go!

{% code_example %}
{%   language java Example.java tag:run label:"Java" %}
{%   language scala Example.scala tag:run label:"Scala" %}
{% endcode_example %}

Next, just run your app with the `kamon-agent` as parameter:

```shell
java -javaagent:kamon-agent.jar -jar /path/to/kamon-agent.jar
```

<img class="img-fluid" src="/img/kamon.timespent.png">

There it is! Your app instrumented with kamon-agent ready.

Some other configuration that you can define is indicated in the agent [`reference.conf`](https://github.com/kamon-io/kamon-agent/blob/master/agent/src/main/resources/reference.conf)

## Lombok
This project uses [Lombok](https://projectlombok.org/) to reduce boilerplate. You can setup
the [IntelliJ plugin](https://plugins.jetbrains.com/plugin/6317) to add IDE support.

[ByteBuddy]:http://bytebuddy.net/#/
[ASM]:http://asm.ow2.org/
