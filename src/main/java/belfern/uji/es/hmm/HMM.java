package belfern.uji.es.hmm;

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

    public Edge instanceEdge(Node start, Node end) {
        return new Edge(start, end);
    }
}
