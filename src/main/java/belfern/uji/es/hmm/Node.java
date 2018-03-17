package belfern.uji.es.hmm;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
    List<Edge> edges;
    String id;

    Node(String id) {
        this.id = id;
        edges = new ArrayList<>();
    }

    public abstract <T> T emmit();
}
