package belfern.uji.es.hmm;

// [1] Speech and Language Processing. Daniel Jurafsky et al. (Chapter 9)

import java.util.*;

public class Node<T, U> {
    T id;
    private Map<Edge<T, U>, Double> edges; // Probabilities for going to each outgoing edge
    private Map<Edge<T, U>, Double> incomingEdges; // Probabilities for coming from each incoming edge
    Map<Node<T, U>, Double> nodes; // Probabilities for going from this node to other node.
    private Map<Node<T, U>, Double> incomingNodes; // Probability for comming from any other node to this node.
    Emitter<U> emitter; // Probability density function for observations
    private List<Double> alfas;
    double alfa;
    double alfaPrevious = 0;
    List<Viterbi> viterbiPath;
    double viterbi;
    double viterbiPrevious;
    private List<Double> betas;
    double beta;
    double betaNext = 0;

    Node(T id, Emitter<U> emitter) {
        if(emitter == null) throw new IllegalArgumentException("Emitter can not be null");
        this.id = id;
        this.emitter = emitter;
        edges = new LinkedHashMap<>();
        incomingEdges = new LinkedHashMap<>();
        nodes = new LinkedHashMap<>();
        incomingNodes = new LinkedHashMap<>();
        alfas = new ArrayList<>();
        viterbiPath = new ArrayList<>();
        betas = new ArrayList<>();
    }

    public Set<Node<T, U>> getNodes() {
        return nodes.keySet();
    }

    U emmit() {
        return emitter.emmit();
    }

    void addEdge(Edge<T, U> edge, double ratio) {
        if(ratio < 0 || ratio > 1.0) throw new IllegalArgumentException("Edge's ratio can be between 0.0 and 1.0");
        edges.put(edge, ratio);
        edge.end.addIncomingEdge(edge, ratio);
        nodes.put(edge.end, ratio);
        edge.end.addIncomingNode(this, ratio);
    }

    private void addIncomingEdge(Edge<T,U> edge, double ratio) {
        incomingEdges.put(edge, ratio);
    }

    private void addIncomingNode(Node<T, U> node, double ratio) {
        incomingNodes.put(node, ratio);
    }

    // This returns the probability of the transition form 'this' node to node 'endNode'
    double getProbabilityToNode(Node<T, U> endNode) {
        if(nodes.containsKey(endNode))
            return nodes.get(endNode);
        else return 0;
    }

    // todo This is maintained for testing purposes only
    Node<T,U> nextNode(double probability) {
        Map.Entry<Edge, Double> entry = accumulatedProbabilities().entrySet().stream()
                .filter(e -> e.getValue() > probability)
                .findFirst()
                .get();

        return entry.getKey().end;
    }

    Node<T,U> nextNode() {
        return nextNode(Math.random());
    }

    private Map<Edge, Double> accumulatedProbabilities() {
        double acc = 0;
        Map<Edge<T,U>, Double> tmp = new LinkedHashMap<>();

        for(Map.Entry<Edge<T,U>, Double> edge: edges.entrySet()) {
            acc += edge.getValue() * edge.getKey().density();
            tmp.put(edge.getKey(), acc);
        }

        Map<Edge, Double> tmpNormalized = new LinkedHashMap<>();

        for (Map.Entry<Edge<T,U>, Double> edge: tmp.entrySet()) {
            tmpNormalized.put(edge.getKey(), tmp.get(edge.getKey())/acc);
        }

        return tmpNormalized;
    }

    double getAlfaProbabilityForSymbol(U symbol) { // This is alfa in Reference [1]
        double result = 0;
        double symbolProbability = emitter.getSymbolProbability(symbol);

        for(Edge<T,U> edge: incomingEdges.keySet()) {
            result += edge.start.alfaPrevious * symbolProbability * incomingEdges.get(edge);
        }

        return alfa = result;
    }

    double getBetaProbabilityForSymbol(U symbol) { // This is beta in Reference [1]
        double result = 0;

        for(Edge<T, U> edge: edges.keySet()) {
            result += edge.end.betaNext * edge.end.emitter.getSymbolProbability(symbol) * edges.get(edge);
        }

        return beta = result;
    }

    void viterbiInit() {
        viterbiPath = new ArrayList<>();
    }

    double viterbi(U symbol) {
        double symbolProbability = emitter.getSymbolProbability(symbol);
        List<Viterbi> tmp = new ArrayList<>();

        for(Edge<T,U> edge: incomingEdges.keySet()) {
            tmp.add(new Viterbi(edge.start, edge.start.viterbiPrevious * symbolProbability * incomingEdges.get(edge)));
        }

        Viterbi max = tmp.stream()
                .max(Comparator.comparingDouble(a -> a.probability))
                .get();

        viterbiPath.add(max);
        viterbi = max.probability;

        return viterbi;
    }

    void stepForward() {
        alfaPrevious = alfa;
        alfas.add(alfa);
    }

    void stepBackward() {
        betaNext = beta;
        betas.add(beta);
    }

    List<Double> getAlfas() {
        return alfas;
    }

    void viterbiStepForward() {
        viterbiPrevious = viterbi;
    }

    List<Double> getBetas() {
        return betas;
    }

    class Viterbi {
        final Node<T,U> node;
        final double probability;

        Viterbi(Node<T,U> node, double probability) {
            this.node = node;
            this.probability = probability;
        }
    }
}