package kamon.agent.profiler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import org.spf4j.ds.Graph;
import org.spf4j.ds.HashMapGraph;

public final class SampleNode implements Serializable {

    private static final long serialVersionUID = 1L;

    private int sampleCount;
        private Map<Method, SampleNode> subNodes;

    public SampleNode(final StackTraceElement[] stackTrace, final int from) {
        sampleCount = 1;
        if (from >= 0) {
            subNodes = new HashMap<>();
            subNodes.put(Method.getMethod(stackTrace[from]), new SampleNode(stackTrace, from - 1));
        }
    }

    public static SampleNode createSampleNode(final StackTraceElement... stackTrace) {
        SampleNode result = new SampleNode(1, null);
        SampleNode prevResult = result;
        for (int i = stackTrace.length - 1; i >= 0; i--) {
            StackTraceElement elem = stackTrace[i];
            if (prevResult.subNodes == null) {
                prevResult.subNodes = new HashMap<>();
            }
            SampleNode node = new SampleNode(1, null);
            prevResult.subNodes.put(Method.getMethod(elem), node);
            prevResult = node;
        }
        return result;
    }

    public static void addToSampleNode(final SampleNode node, final StackTraceElement... stackTrace) {
        SampleNode prevResult = node;
        prevResult.sampleCount++;
        for (int i = stackTrace.length - 1; i >= 0; i--) {
            StackTraceElement elem = stackTrace[i];
            final Method method = Method.getMethod(elem);
            SampleNode nNode;
            if (prevResult.subNodes == null) {
                prevResult.subNodes = new HashMap<>();
                nNode = new SampleNode(1, null);
                prevResult.subNodes.put(method, nNode);
            } else {
                nNode = prevResult.subNodes.get(method);
                if (nNode != null) {
                    nNode.sampleCount++;
                } else {
                    nNode = new SampleNode(1, null);
                    prevResult.subNodes.put(method, nNode);
                }
            }
            prevResult = nNode;
        }
    }

    public static SampleNode clone(final SampleNode node) {
        if (node.subNodes == null) {
            return new SampleNode(node.sampleCount, null);
        }
        final Map<Method, SampleNode> newSubNodes = new HashMap<>(node.subNodes.size());
        node.subNodes.forEach((Method a,  SampleNode b) -> newSubNodes.put(a, SampleNode.clone(b)));
        return new SampleNode(node.sampleCount, newSubNodes);
    }

    public static SampleNode aggregate(final SampleNode node1, final SampleNode node2) {
        int newSampleCount = node1.sampleCount + node2.sampleCount;
        Map<Method, SampleNode> newSubNodes;
        if (node1.subNodes == null && node2.subNodes == null) {
            newSubNodes = null;
        } else if (node1.subNodes == null) {
            newSubNodes = cloneSubNodes(node2);
        } else if (node2.subNodes == null) {
            newSubNodes = cloneSubNodes(node1);
        } else {
            final Map<Method, SampleNode> ns = new HashMap<>(node1.subNodes.size() + node2.subNodes.size());
            node1.subNodes.forEach((Method m, SampleNode b) -> {
                SampleNode other = node2.subNodes.get(m);
                if (other == null) {
                    ns.put(m, SampleNode.clone(b));
                } else {
                    ns.put(m, aggregate(b, other));
                }
            });
            node2.subNodes.forEach((Method m, SampleNode b) -> {
                if (!node1.subNodes.containsKey(m)) {
                    ns.put(m, SampleNode.clone(b));
                }
            });
            newSubNodes = ns;

        }
        return new SampleNode(newSampleCount, newSubNodes);
    }

    public static Map<Method, SampleNode> cloneSubNodes(final SampleNode node) {
        final Map<Method, SampleNode> ns = new HashMap<>(node.subNodes.size());
        putAllClones(node.subNodes, ns);
        return ns;
    }

    public static void putAllClones(final Map<Method, SampleNode> source,
                                    final Map<Method, SampleNode> destination) {
        source.forEach((final Method a, final SampleNode b) -> destination.put(a, SampleNode.clone(b)));
    }

    public SampleNode(final int count, final Map<Method, SampleNode> subNodes) {
        this.sampleCount = count;
        this.subNodes = subNodes;
    }

    void addSample(final StackTraceElement[] stackTrace, final int from) {
        sampleCount++;
        if (from >= 0) {
            Method method = Method.getMethod(stackTrace[from]);
            SampleNode subNode = null;
            if (subNodes == null) {
                subNodes = new HashMap();
            } else {
                subNode = subNodes.get(method);
            }
            if (subNode == null) {
                subNodes.put(method, new SampleNode(stackTrace, from - 1));
            } else {
                subNode.addSample(stackTrace, from - 1);
            }
        }
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public Map<Method, SampleNode> getSubNodes() {
        return subNodes;
    }

    @Override
    public String toString() {
        return "SampleNode{" + "count=" + sampleCount
                + ((subNodes == null || subNodes.isEmpty()) ? "" : ", subNodes=" + subNodes) + '}';
    }

    public int height() {
        if (subNodes == null) {
            return 1;
        } else {
            int subHeight = 0;
            for (SampleNode node : subNodes.values()) {
                int nHeight = node.height();
                if (nHeight > subHeight) {
                    subHeight = nHeight;
                }
            }
            return subHeight + 1;
        }

    }

    public int getNrNodes() {
        if (subNodes == null) {
            return 1;
        } else {
            int nrNodes = 0;
            for (SampleNode node : subNodes.values()) {
                nrNodes += node.getNrNodes();
            }
            return nrNodes + 1;
        }
    }

    public SampleNode filteredBy(final Predicate<Method> predicate) {

        int newCount = this.sampleCount;

        Map<Method, SampleNode> sns = null;
        if (this.subNodes != null) {
            for (Map.Entry<Method, SampleNode> entry : this.subNodes.entrySet()) {
                Method method = entry.getKey();
                SampleNode sn = entry.getValue();
                if (predicate.test(method)) {
                    newCount -= sn.getSampleCount();
                } else {
                    if (sns == null) {
                        sns = new HashMap<>();
                    }
                    SampleNode sn2 = sn.filteredBy(predicate);
                    if (sn2 == null) {
                        newCount -= sn.getSampleCount();
                    } else {
                        newCount -= sn.getSampleCount() - sn2.getSampleCount();
                        sns.put(method, sn2);
                    }

                }
            }
        }
        if (newCount == 0) {
            return null;
        } else if (newCount < 0) {
            throw new IllegalStateException("child sample counts must be <= parent sample count, detail: " + this);
        } else {
            return new SampleNode(newCount, sns);
        }
    }

    public interface InvocationHandler {

        /**
         * handler for SampleNode tree traversal for each invocation.
         * @param from method
         * @param to method
         * @param sampleCount number of samples
         * @param ancestors
         */
        void handle(Method from, Method to, int sampleCount, Map<Method, Integer> ancestors);
    }

    public void forEach(final InvocationHandler handler, final Method from,
                        final Method to, final Map<Method, Integer> ancestors) {

        handler.handle(from, to, sampleCount, ancestors);

        if (subNodes != null) {
            Integer val = ancestors.get(to);
            if (val != null) {
                val = val + 1;
            } else {
                val = 1;
            }
            ancestors.put(to, val);
            for (Map.Entry<Method, SampleNode> subs : subNodes.entrySet()) {
                Method toKey = subs.getKey();
                subs.getValue().forEach(handler, to, toKey, ancestors);
            }
            val = ancestors.get(to);
            if (val == 1) {
                ancestors.remove(to);
            } else {
                val = val - 1;
                ancestors.put(to, val);
            }
        }
    }

    public static final class InvocationCount {

        private int value;

        public InvocationCount(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(final int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "InvocationCount{" + "value=" + value + '}';
        }
    }

    public static Graph<InvokedMethod, InvocationCount> toGraph(final SampleNode rootNode) {
        final HashMapGraph<InvokedMethod, InvocationCount> result = new HashMapGraph<>();

        rootNode.forEach((final Method pfrom, final Method pto,
                          final int count, final Map<Method, Integer> ancestors) -> {
            InvokedMethod from;
            InvokedMethod to;
            Integer val = ancestors.get(pfrom);
            if (val != null) {
                from = new InvokedMethod(pfrom, val - 1);
            } else {
                from = new InvokedMethod(pfrom, 0);
            }
            val = ancestors.get(pto);
            if (val != null) {
                to = new InvokedMethod(pto, val);
            } else {
                to = new InvokedMethod(pto, 0);
            }

            InvocationCount ic = result.getEdge(from, to);

            if (ic == null) {
                result.add(new InvocationCount(count), from, to);
            } else {
                ic.setValue(count + ic.getValue());
            }
        }, Method.ROOT, Method.ROOT, new HashMap<Method, Integer>());

        return result;

    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + this.sampleCount;
        return 89 * hash + Objects.hashCode(this.subNodes);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SampleNode other = (SampleNode) obj;
        if (this.sampleCount != other.sampleCount) {
            return false;
        }
        if (this.subNodes == other.subNodes) {
            return true;
        }
        if (this.subNodes != null && other.subNodes == null && this.subNodes.isEmpty()) {
            return true;
        }
        if (this.subNodes == null && other.subNodes != null && other.subNodes.isEmpty()) {
            return true;
        }
        return Objects.equals(this.subNodes, other.subNodes);
    }

}