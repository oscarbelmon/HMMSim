package belfern.uji.es.hmm;

public class Edge {
    Node start;
    Node end;

    Edge(Node start, Node end) {
        if(start == null || end == null) throw new IllegalArgumentException("Nodes can not be null.");
        this.start = start;
        this.end = end;
    }
}
