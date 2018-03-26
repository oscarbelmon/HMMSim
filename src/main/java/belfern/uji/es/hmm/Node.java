package belfern.uji.es.hmm;


import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Node<T> {
    String id;
    Map<Edge, Double> edges;
    Emitter<T> emitter;

    Node(String id, Emitter emitter) {
        if(emitter == null) throw new IllegalArgumentException("Emitter can not be null");
        this.id = id;
        this.emitter = emitter;
//        edges = new HashMap<>();
        edges = new LinkedHashMap<>();
    }

    public T emmit() {
        return emitter.emmit();
    }

    void addEdge(Edge edge, double ratio) {
        if(ratio < 0 || ratio > 1.0) throw new IllegalArgumentException("Edge's ratio can be between 0.0 and 1.0");
        edges.put(edge, ratio);
    }

    // todo This is maintained for testing purposes only
    Node nextNode(double probability) {
        Map.Entry<Edge, Double> entry = accumulatedProbabilities().entrySet().stream()
//                .sorted(Map.Entry.comparingByValue())
                .filter(e -> e.getValue() > probability)
                .findFirst()
                .get();

        return entry.getKey().end;
    }

    Node nextNode() {
        return nextNode(Math.random());
    }

    Map<Edge, Double> accumulatedProbabilities() {
        double acc = 0;
//        Map<Edge, Double> tmp = new HashMap<>();
        Map<Edge, Double> tmp = new LinkedHashMap<>();

//        Map<Edge, Double> sorted = edges.entrySet().stream()
//                .sorted(Map.Entry.comparingByValue())
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

//        for(Map.Entry<Edge, Double> edge: edges.entrySet()) {
//            acc += edge.getValue();
//            tmp.put(edge.getKey(), acc);
//        }

        for(Map.Entry<Edge, Double> edge: edges.entrySet()) {
            acc += edge.getValue() * edge.getKey().density();
            tmp.put(edge.getKey(), acc);
        }

        Map<Edge, Double> tmpNormalized = new LinkedHashMap<>();

        for (Map.Entry<Edge, Double> edge: tmp.entrySet()) {
            tmpNormalized.put(edge.getKey(), tmp.get(edge.getKey())/acc);
        }

//        return tmp;
        return tmpNormalized;
    }

}