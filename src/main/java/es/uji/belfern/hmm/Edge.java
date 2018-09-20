package es.uji.belfern.hmm;

import es.uji.belfern.statistics.ProbabilityDensityFunction;

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

    @Override
    public String toString() {
        return "{" +
                start.id +
                ", " + end.id +
                '}';
    }
}
