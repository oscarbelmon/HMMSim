package belfern.uji.es.hmm;

import java.util.HashMap;
import java.util.Map;

public class HMM {
    Map<String, Node> nodes;
    Edge initialEdge;

    public HMM() {
        nodes = new HashMap<>();
    }

    public void setInitialEdge(Edge initialEdge) {
        if(initialEdge == null) throw new IllegalArgumentException("Initial edge can not be null");
        this.initialEdge = initialEdge;
    }

    public Node instanceNode(String id, Emitter emitter) {
        return new Node(id, emitter);
    }

    public Edge instanceEdge(Node start, Node end) {
        return new Edge(start, end);
    }
}
