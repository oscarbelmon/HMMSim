package belfern.uji.es.hmm;

import belfern.uji.es.statistics.ProbabilityDensityFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HMM<T> {
    Map<String, Node> nodes;
    Node<T> initialNode;

    public HMM() {
        nodes = new HashMap<>();
    }

    public void setInitialNode(Node<T> initialNode) {
        if(initialNode == null) throw new IllegalArgumentException("Initial node can not be null");
        this.initialNode = initialNode;
    }

    public Node<T> instanceInitialNode(String id, Emitter<T> emitter) {
        initialNode = instanceNode(id, emitter);
        nodes.put(id, initialNode);
        return initialNode;
    }

    public Node<T> instanceNode(String id, Emitter<T> emitter) {
        Node<T> node = new Node<T>(id, emitter);
        nodes.put(id, node);
        return node;
    }

    public Edge<T> instanceEdge(Node<T> start, Node<T> end, ProbabilityDensityFunction pdf, double ratio) {
        Edge<T> edge = new Edge<T>(start, end, pdf);
        start.addEdge(edge, ratio);
        return edge;
    }

    public Edge<T> instanceEdge(String idStart, String idEnd, ProbabilityDensityFunction pdf, double ratio) {
        Node<T> start = nodes.get(idStart);
        Node<T> end = nodes.get(idEnd);
        return instanceEdge(start, end, pdf, ratio);
    }

    public List<T> generateSequence(long items) {
        List<T> sequence = new ArrayList<>();
        Node<T> currentNode = initialNode;

        for(long i = 0; i < items; i++) {
            sequence.add(currentNode.emmit());
            currentNode = currentNode.nextNode();
        }

        return sequence;
    }
}
