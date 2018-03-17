package belfern.uji.es.hmm;


import java.util.ArrayList;
import java.util.List;

public class Node<T> {
    String id;
    List<Edge> edges;
    Emitter<T> emitter;

    Node(String id, Emitter emitter) {
        if(emitter == null) throw new IllegalArgumentException("Emitter can not be null");
        this.id = id;
        this.emitter = emitter;
        edges = new ArrayList<>();
    }

    public T emmit() {
        return emitter.emmit();
    }
}
