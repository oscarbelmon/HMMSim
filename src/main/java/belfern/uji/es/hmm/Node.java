package belfern.uji.es.hmm;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
    String id;
    List<Edge> edges;
    Emmiter<T> emmiter;

    Node(String id, Emmiter emmiter) {
        this.id = id;
        this.emmiter = emmiter;
        edges = new ArrayList<>();
    }

    public T emmit() {
        return emmiter.emmit();
    }
}
