package belfern.uji.es.hmm;

import java.util.List;

public abstract class Node {
    List<Edge> edges;
    String name;

    public abstract <T> T emmit();
}
