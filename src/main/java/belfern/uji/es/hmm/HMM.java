package belfern.uji.es.hmm;

import java.util.HashMap;
import java.util.Map;

public class HMM {
    Map<String, Node> nodes;

    public HMM() {
        nodes = new HashMap<>();
    }

    public Node instanceNode(String id, Emmiter emmiter) {
        return new Node(id, emmiter);
    }

    public Edge instanceEdge(Node start, Node end) {
        return new Edge(start, end);
    }
}
