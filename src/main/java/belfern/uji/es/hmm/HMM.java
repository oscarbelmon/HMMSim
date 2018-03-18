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
        return initialNode = instanceNode(id, emitter);
    }

    public Node instanceNode(String id, Emitter emitter) {
        return new Node(id, emitter);
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
