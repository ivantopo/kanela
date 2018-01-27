// tag:worker:start
import scala.util.Random

class Worker {
  def performTask(): Unit = Thread.sleep((Random.nextFloat() * 500) toLong)
}
// tag:worker:end

// tag:instrumentation:start
class TimeSpentInstrumentation extends KamonInstrumentation {
  forTargetType("run.Worker") { builder ⇒
    builder
      .withAdvisorFor(method("performTask"), classOf[PerformTaskMethodAdvisor])
      .build()
  }
}
// tag:instrumentation:end

// tag:advisor:start
object PerformTaskMethodAdvisor {
  @OnMethodEnter
  def onMethodEnter(): Long =
    System.nanoTime() // Return current time, entering as parameter in the onMethodExist

  @OnMethodExit
  def onMethodExit(@This instance: MonitorAware, @Enter start: Long, @Origin origin: String): Unit =
    println(s"Method $origin was executed in ${System.nanoTime() - start} ns.")
}
// tag:advisor:end

// tag:run:start
object TimeSpent extends App {
  for(_ <- 1 to 10) {
    new Worker().performTask()
  }
}
// tag:run:end

//Interceptor

// tag:instrumentation-interceptor:start
class TimeSpentInstrumentation extends KamonInstrumentation {
  forTargetType("run.Worker") { builder ⇒
    builder
      .withInterceptorFor(method("performTask"), PerformTaskMethodInterceptor)
      .build()
  }
}
// tag:instrumentation-interceptor:end

// tag:interceptor:start
object PerformTaskMethodInterceptor {
  @RuntimeType
  def around(@SuperCall callable: Callable[_], @Origin origin:String): Any = {
    val start = System.nanoTime()
    try callable.call() finally {
      println(s"Method $origin was executed in ${System.nanoTime() - start} ns.")
    }
  }
}
// tag:interceptor:end
