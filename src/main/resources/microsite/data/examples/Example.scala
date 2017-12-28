// tag:worker:start
import scala.util.Random

case class Worker() {
  def performTask(): Unit = Thread.sleep((Random.nextFloat() * 500) toLong)
}
// tag:worker:end

// tag:mixin:start
trait MonitorAware {
  def execTimings: Map[String, Vector[Long]]
  def addExecTimings(methodName: String, time: Long): Vector[Long]
}
// tag:mixin:end


// tag:instrumentation:start
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
// tag:instrumentation:end

// tag:mixin-implementation:start
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
// tag:mixin-implementation:end

// tag:advisor:start
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
// tag:advisor:end
