# Scala API

## Example

Suppose you have a simple worker that perform a simple operation:

```scala
import scala.util.Random

case class Worker() {
  def performTask(): Unit = Thread.sleep((Random.nextFloat() * 500) toLong)
}
```

You might want to mixin it with a type that provide a way to accumulate metrics, such as the following:

```scala
trait MonitorAware {
  def execTimings: Map[String, Vector[Long]]
  def addExecTimings(methodName: String, time: Long): Vector[Long]
}
```

And introduce some transformations in order to modify the bytecode and hook into the internal app.

```scala

import kamon.agent.scala.KamonInstrumentation

// And other imports !

class MonitorInstrumentation extends KamonInstrumentation {

  forTargetType("app.kamon.Worker") { builder ⇒
    builder
      .withMixin(classOf[MonitorMixin])
      .withAdvisorFor(named("performTask"), classOf[WorkerAdvisor])
      .build()
  }
}

```

```scala
class MonitorMixin extends MonitorAware {

  private var _execTimings: TrieMap[String, CopyOnWriteArrayList[Long]] = _

  def execTimings: TrieMap[String, CopyOnWriteArrayList[Long]] = this._execTimings

  def execTimings(methodName: String): java.util.List[Long] = this._execTimings.getOrElse(methodName, new CopyOnWriteArrayList())

  def addExecTimings(methodName: String, time: Long): java.util.List[Long] = {
    val update = this._execTimings.getOrElseUpdate(methodName, new CopyOnWriteArrayList())
    update.add(time)
    update
  }

  @Initializer
  def init(): Unit = this._execTimings = TrieMap[String, CopyOnWriteArrayList[Long]]()
}
```

```scala

object WorkerAdvisor {

  @OnMethodEnter
  def onMethodEnter(): Long = {
    System.nanoTime() // Return current time, entering as parameter in the onMethodExist
  }

  @OnMethodExit
  def onMethodExit(@This instance: MonitorAware, @Enter start: Long, @Origin origin: String): Unit = {
    val timing = System.nanoTime() - start
    instance.addExecTimings(origin, timing)
    println(s"Method $origin was executed in $timing ns.")
  }
}

```
