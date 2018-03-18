package belfern.uji.es.hmm;

import belfern.uji.es.statistics.ProbabilityDensityFunction;

public class Edge {
    Node start;
    Node end;
    ProbabilityDensityFunction pdf;

    Edge(Node start, Node end, ProbabilityDensityFunction pdf) {
        if(start == null ||
                end == null ||
                pdf == null) throw new IllegalArgumentException("Nodes can not be null.");
        this.start = start;
        this.end = end;
        this.pdf = pdf;
    }

    // todo Constructor with node's identifier.

    double density() {
        return pdf.density();
    }
}
