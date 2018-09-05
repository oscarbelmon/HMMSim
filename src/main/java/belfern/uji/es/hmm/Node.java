package belfern.uji.es.hmm;


import java.util.*;

public class Node<T, U> {
    T id;
    private Map<Edge<T, U>, Double> edges;
    private Map<Edge<T, U>, Double> incomingEdges;
    Emitter<U> emitter;
    private List<Double> alfas;
    double alfa;
    double alfaPrevious = 0;
    List<Viterbi> viterbiPath;
    double viterbi;
    double viterbiPrevious;
    private List<Double> betas;
    double beta;
    double betaPrevious = 0;

    Node(T id, Emitter<U> emitter) {
        if(emitter == null) throw new IllegalArgumentException("Emitter can not be null");
        this.id = id;
        this.emitter = emitter;
        edges = new LinkedHashMap<>();
        incomingEdges = new LinkedHashMap<>();
        alfas = new ArrayList<>();
        viterbiPath = new ArrayList<>();
        betas = new ArrayList<>();
    }

    U emmit() {
        return emitter.emmit();
    }

    void addEdge(Edge<T, U> edge, double ratio) {
        if(ratio < 0 || ratio > 1.0) throw new IllegalArgumentException("Edge's ratio can be between 0.0 and 1.0");
        edges.put(edge, ratio);
        edge.end.addIncomingEdge(edge, ratio);
    }

    private void addIncomingEdge(Edge<T,U> edge, double ratio) {
        incomingEdges.put(edge, ratio);
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

    double getProbabilityForSymbol(U symbol) {
        double result = 0;
        double symbolProbability = emitter.getSymbolProbability(symbol);

        for(Edge<T,U> edge: incomingEdges.keySet()) {
            result += edge.start.alfaPrevious * symbolProbability * incomingEdges.get(edge);
        }

        return alfa = result;
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
        betaPrevious = beta;
        betas.add(beta);
    }

    List<Double> getAlfas() {
        return alfas;
    }

    void viterbiStepForward() {
        viterbiPrevious = viterbi;
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