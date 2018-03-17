package belfern.uji.es.hmm;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node<T> {
    String id;
    Map<Edge, Double> edges;
    Emitter<T> emitter;

    Node(String id, Emitter emitter) {
        if(emitter == null) throw new IllegalArgumentException("Emitter can not be null");
        this.id = id;
        this.emitter = emitter;
        edges = new HashMap<>();
    }

    public T emmit() {
        return emitter.emmit();
    }

    void addEdge(Edge edge, double ratio) {
        if(ratio < 0 || ratio > 1.0) throw new IllegalArgumentException("Edge's ratio can be between 0.0 and 1.0");
        edges.put(edge, ratio);
    }

//    Node nextNode() {
//        edges.entrySet().stream()
//                .map(e -> e.)
//    }
}
