# Kamon Agent <img align="right" src="https://rawgit.com/kamon-io/Kamon/master/kamon-logo.svg" height="150px" style="padding-left: 20px"/>
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

```java
public class Worker {
  private final Random random = new java.util.Random();

  public void performTask() {
      Thread.sleep((long)(random.nextFloat() * 500));
  }   
}
```

You might want to mixin it with a type that provide a way to accumulate metrics, such as the following:
```java
public interface MonitorAware {
    Map<String, List<Long>> execTimings();
    List<Long> addExecTimings(String methodName, long time);
}

```

And introduce some transformations in order to modify the bytecode and hook into the internal app.

```java
import kamon.agent.api.instrumentation.KamonInstrumentation;

// And other imports !

public class MonitorInstrumentation extends KamonInstrumentation {
    public MonitorInstrumentation() {
        forTargetType(() -> "app.kamon.java.Worker", builder ->
            builder.withMixin(() -> MonitorMixin.class)
                   .withAdvisorFor(named("performTask"), () -> WorkerAdvisor.class)
                   .build()
        );
    }

}
```

```java
public class MonitorMixin implements MonitorAware {

    private Map<String, List<Long>> _execTimings;

    @Override
    public List<Long> execTimings(String methodName) {
        return _execTimings.getOrDefault(methodName, List.empty());
    }

    @Override
    public Map<String, List<Long>> execTimings() {
        return _execTimings;
    }

    @Override
    public List<Long> addExecTimings(String methodName, long time) {
        return this._execTimings.compute(methodName, (key, oldValues) -> Option.of(oldValues).map(vs -> vs.append(time)).getOrElse(List.of(time)));
    }

    @Initializer
    public void init() {
        this._execTimings = new ConcurrentHashMap<>();
    }
}

```

```java
import lombok.val;

public class WorkerAdvisor {

    @OnMethodEnter
    public static long onMethodEnter() {
        return System.nanoTime(); // Return current time, entering as parameter in the onMethodExist
    }

    @OnMethodExit
    public static void onMethodExit(@This MonitorAware instance, @Enter long start, @Origin String origin) {
        val timing = System.nanoTime() - start;
        instance.addExecTimings(origin, timing);
        System.out.println(String.format("Method %s was executed in %10.2f ns.", origin, (float) timing));
    }
}


```

Finally, we need to define a new module in the kamon agent configuration:

```hocon
kamon.agent {
  modules {
    example-module {
      name = "Example Module"
      stoppable = false
      instrumentations = ["app.kamon.instrumentation.MonitorInstrumentation"]
      within = [ "app.kamon..*" ] // List of patterns to match the types to instrument.
    }
  }
}
```

And you are ready to go!

Next, just run your app with the `kamon-agent` as parameter:

```
java -javaagent:kamon-agent.jar -jar /path/to/footpath-routing-api.jar
```

There it is! Your app instrumented with kamon-agent ready to introduce kamon under the hook.

Some other configuration that you can define is indicated in the agent [`reference.conf`](https://github.com/kamon-io/kamon-agent/blob/master/agent/src/main/resources/reference.conf)

## Lombok
This project uses [Lombok](https://projectlombok.org/) to reduce boilerplate. You can setup
the [IntelliJ plugin](https://plugins.jetbrains.com/plugin/6317) to add IDE support.

[ByteBuddy]:http://bytebuddy.net/#/
[ASM]:http://asm.ow2.org/
