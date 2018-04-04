package belfern.uji.es.hmm;

import belfern.uji.es.statistics.ProbabilityDensityFunction;

import java.util.*;

public class HMM<T, U> {
    Map<T, Node<T, U>> nodes;
    Node<T, U> initialNode;
    private double[][] forward = null;
    private int numNodes;
    int numObservations;
    private static final Node startNode = new Node(null, new TabulatedProbabilityEmitter());


    public HMM() {
        nodes = new LinkedHashMap<>();
        startNode.alfaPrevious = 1;
    }

    public void setInitialNode(Node<T, U> initialNode) {
        if(initialNode == null) throw new IllegalArgumentException("Initial node can not be null");
        this.initialNode = initialNode;
        Edge<T,U> edge = instanceEdge(startNode, this.initialNode, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        this.initialNode.incomingEdges.put(edge, 1.0);
    }

    public Node<T, U> instanceInitialNode(T id, Emitter<U> emitter) {
        initialNode = instanceNode(id, emitter);
        Edge<T,U> edge = instanceEdge(startNode, this.initialNode, ProbabilityDensityFunction.CONSTANT_PROBABILITY, 1.0);
        initialNode.incomingEdges.put(edge, 1.0);
        nodes.put(id, initialNode);
        return initialNode;
    }

    public Node<T, U> instanceNode(T id, Emitter<U> emitter) {
        Node<T, U> node = new Node<T, U>(id, emitter);
        nodes.put(id, node);
        return node;
    }

    public Edge<T, U> instanceEdge(Node<T, U> start, Node<T, U> end, ProbabilityDensityFunction pdf, double ratio) {
        Edge<T, U> edge = new Edge<T, U>(start, end, pdf);
        start.addEdge(edge, ratio);
        return edge;
    }

    public Edge<T, U> instanceEdge(String idStart, String idEnd, ProbabilityDensityFunction pdf, double ratio) {
        Node<T, U> start = nodes.get(idStart);
        Node<T, U> end = nodes.get(idEnd);
        return instanceEdge(start, end, pdf, ratio);
    }

    public List<U> generateSequence(long items) {
        List<U> sequence = new ArrayList<>();
        Node<T, U> currentNode = initialNode;

        for(long i = 0; i < items; i++) {
            sequence.add(currentNode.emmit());
            currentNode = currentNode.nextNode();
        }

        return sequence;
    }

//    double probabilityIJ(T idStart, T idEnd) {
//        Node<T, U> start = nodes.get(idStart);
//        Node<T, U> end = nodes.get(idEnd);
//        return start.getProbabilityToNode(end);
//    }

//    public double forward(List<T> simbols) {
//        if(forward == null) {
//            numNodes = this.nodes.size();
//            numObservations = simbols.size();
//            forward = new double[numNodes + 2][numObservations];
//        }
//    }
//
    void initialization(U symbol) {
        for(Node<T, U> node: nodes.values()) {
            node.getProbabilityForSymbol(symbol);
        }
        for(Node<T, U> node: nodes.values()) {
            node.stepForward();
        }


    }

    void recursion(List<U> symbols) {

    }
//
//    private void recursion(double[][] forward) {
//
//    }
//
//    private void termination(double[][] forward) {
//
//    }
}
