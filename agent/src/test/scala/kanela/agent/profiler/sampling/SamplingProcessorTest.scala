package kanela.agent.profiler.sampling

import java.util

import kanela.agent.bootstrap.profiling.{SamplingNode, SamplingThread}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConverters._

class SamplingProcessorTest extends FlatSpec with Matchers {

  "SamplingProcessor" should
    "group all samples of a thread by root" in {

    val sampling = new SamplingThread {
      val samples = List(
        SamplingNode.of("methodA", 15, 18, 31, 35),
        SamplingNode.of("methodE", 18, 19, 31, 35),
        SamplingNode.of("methodB", 12, 20, 31, 35),
        SamplingNode.of("methodD", 25, 30, 31, 35),
        SamplingNode.of("methodC", 21, 40, 31, 35),
        SamplingNode.of("methodD", 42, 58, 31, 35),
        SamplingNode.of("methodA", 10, 70, 31, 35)
      )
      override def getSamplings: util.List[SamplingNode] = samples.asJava
    }

    val results = SamplingProcessor.groupByRoot(sampling).asScala.toList

    results.map(_.getNode) should be (List(sampling.samples.last))

  }

}
