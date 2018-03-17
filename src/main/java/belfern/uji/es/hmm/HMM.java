package belfern.uji.es.hmm;

import java.util.HashMap;
import java.util.Map;

public class HMM {
    Map<String, Node> nodes;

    public HMM() {
        nodes = new HashMap<>();
    }

    public Node instanceNode(String id, Emitter emitter) {
        return new Node(id, emitter);
    }

    public Edge instanceEdge(Node start, Node end) {
        return new Edge(start, end);
    }
}
