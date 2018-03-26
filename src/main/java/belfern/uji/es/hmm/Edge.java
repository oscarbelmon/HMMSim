package belfern.uji.es.hmm;

import belfern.uji.es.statistics.ProbabilityDensityFunction;

public class Edge<T, U> {
    Node<T, U> start;
    Node<T, U> end;
    ProbabilityDensityFunction pdf;

    Edge(Node<T, U> start, Node<T, U> end, ProbabilityDensityFunction pdf) {
        if(start == null ||
                end == null ||
                pdf == null) throw new IllegalArgumentException("Nodes can not be null.");
        this.start = start;
        this.end = end;
        this.pdf = pdf;
    }

    double density() {
        return pdf.density();
    }
}
