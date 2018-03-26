package belfern.uji.es.hmm;

import belfern.uji.es.statistics.ProbabilityDensityFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HMM<T, U> {
    Map<U, Node<T, U>> nodes;
    Node<T, U> initialNode;

    public HMM() {
        nodes = new HashMap<>();
    }

    public void setInitialNode(Node<T, U> initialNode) {
        if(initialNode == null) throw new IllegalArgumentException("Initial node can not be null");
        this.initialNode = initialNode;
    }

    public Node<T, U> instanceInitialNode(U id, Emitter<T> emitter) {
        initialNode = instanceNode(id, emitter);
        nodes.put(id, initialNode);
        return initialNode;
    }

    public Node<T, U> instanceNode(U id, Emitter<T> emitter) {
        Node<T, U> node = new Node<T, U>(id, emitter);
        nodes.put(id, node);
        return node;
    }

    public Edge<T, U> instanceEdge(Node<T, U> start, Node<T, U> end, ProbabilityDensityFunction pdf, double ratio) {
        Edge<T, U> edge = new Edge<T, U>(start, end, pdf);
        start.addEdge(edge, ratio);
        return edge;
    }

    public Edge<T, U> instanceEdge(String idStart, String idEnd, ProbabilityDensityFunction pdf, double ratio) {
        Node<T, U> start = nodes.get(idStart);
        Node<T, U> end = nodes.get(idEnd);
        return instanceEdge(start, end, pdf, ratio);
    }

    public List<T> generateSequence(long items) {
        List<T> sequence = new ArrayList<>();
        Node<T, U> currentNode = initialNode;

        for(long i = 0; i < items; i++) {
            sequence.add(currentNode.emmit());
            currentNode = currentNode.nextNode();
        }

        return sequence;
    }
}
