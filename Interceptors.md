## Getting Started with Interceptors
TODO: description

### Time Spent Example([ByteBuddy Interceptor][1]) in 4 steps

Suppose you have a simple worker that perform a simple operation:

{% code_example %}
{%   language java Example.java tag:worker label:"Java" %}
{%   language scala Example.scala tag:worker label:"Scala" %}
{% endcode_example %}

### Step 1: The Instrumentation

We need introduce a transformation `Before` and `After` method execution in order to measure the time spent in the method execution:

{% code_example %}
{%   language java Example.java tag:instrumentation-interceptor label:"Java" %}
{%   language scala Example.scala tag:instrumentation-interceptor label:"Scala" %}
{% endcode_example %}

### Step 2: The Method `Interceptor`

{% code_example %}
{%   language java Example.java tag:interceptor label:"Java" %}
{%   language scala Example.scala tag:interceptor label:"Scala" %}
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

[1]:https://github.com/raphw/byte-buddy#a-more-complex-example
