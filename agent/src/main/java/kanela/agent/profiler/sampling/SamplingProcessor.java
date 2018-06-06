package kanela.agent.profiler.sampling;

import static io.vavr.collection.List.empty;
import static io.vavr.collection.List.ofAll;

import io.vavr.collection.List;
import java.util.stream.Collectors;
import kanela.agent.bootstrap.profiling.SamplingNode;
import kanela.agent.bootstrap.profiling.SamplingThread;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.val;

@Value
public class SamplingProcessor {

//    public void process(Map<ThreadInfo, SamplingThread> samples) {
//        samples.entrySet()
//            .stream()
//            .forEach((entry) -> {});
//    }
//
//    private void process(ThreadInfo thread, SamplingThread samples) {
//        val byRoot = groupByRoot(samples);
//    }

    public static java.util.List<SampleNodeResult> groupByRoot(SamplingThread samples) {
        final List<SamplingNode> samplingNodes = ofAll(samples.getSamplings());

        final java.util.Map<SamplingNode, SampleNodeResult> nodeResultMap = new java.util.HashMap<>();

        samplingNodes
            .zipWithIndex()
            .forEach((nodeWithIndex) -> {
                val currentNode = nodeWithIndex._1();
                val index = nodeWithIndex._2();

                val currentNodeBuilder = nodeResultFrom(nodeResultMap, currentNode);

                samplingNodes
                    .slice(index + 1, samplingNodes.length())
                    .forEach((possibleParent) -> {
                        if (possibleParent.getStartTimeMethodNs() < currentNode.getStartTimeMethodNs()
                            && possibleParent.getEndTimeMethodNs() >= currentNode.getEndTimeMethodNs()) {
                            nodeResultFrom(nodeResultMap, currentNode).addChild(currentNode);
                            currentNodeBuilder.setRoot(false);
                        }
                    });

            });

        return nodeResultMap.values().stream().filter(SampleNodeResult::isRoot).collect(Collectors.toList());
    }

    private static SampleNodeResult nodeResultFrom(java.util.Map<SamplingNode, SampleNodeResult> nodeResultMap, SamplingNode node) {
        return nodeResultMap.computeIfAbsent(node, SampleNodeResult::newOne);
    }

    @Value
    @RequiredArgsConstructor(staticName = "newOne")
    public static class SampleNodeResult {

        SamplingNode node;

        @NonFinal
        @Setter
        boolean isRoot = true;

        List<SamplingNode> children = empty();

        public SampleNodeResult addChild(SamplingNode node) {
            children.prepend(node);
            return this;
        }
    }

}
