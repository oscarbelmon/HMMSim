package belfern.uji.es.hmm;

import belfern.uji.es.statistics.ProbabilityDensityFunction;

import java.util.HashMap;
import java.util.Map;

public class HMM {
    Map<String, Node> nodes;
    Node initialNode;

    public HMM() {
        nodes = new HashMap<>();
    }

    public void setInitialNode(Node initialNode) {
        if(initialNode == null) throw new IllegalArgumentException("Initial node can not be null");
        this.initialNode = initialNode;
    }

    public Node instanceInitialNode(String id, Emitter emitter) {
        initialNode = instanceNode(id, emitter);
        nodes.put(id, initialNode);
        return initialNode;
    }

    public Node instanceNode(String id, Emitter emitter) {
        Node node = new Node(id, emitter);
        nodes.put(id, node);
        return node;
    }

    public Edge instanceEdge(Node start, Node end, ProbabilityDensityFunction pdf, double ratio) {
        Edge edge = new Edge(start, end, pdf);
        start.addEdge(edge, ratio);
        return edge;
    }

    public Edge instanceEdge(String idStart, String idEnd, ProbabilityDensityFunction pdf, double ratio) {
        Node start = nodes.get(idStart);
        Node end = nodes.get(idEnd);
        return instanceEdge(start, end, pdf, ratio);
    }

}
