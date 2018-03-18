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
        edges = new HashMap<>();
    }

    public T emmit() {
        return emitter.emmit();
    }

    void addEdge(Edge edge, double ratio) {
        if(ratio < 0 || ratio > 1.0) throw new IllegalArgumentException("Edge's ratio can be between 0.0 and 1.0");
        edges.put(edge, ratio);
    }

    Node nextNode(double probability) {
        Map.Entry<Edge, Double> entry = accumulatedProbabilities().entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .filter(e -> e.getValue() > probability)
                .findFirst()
                .get();

        return entry.getKey().end;
    }

    Map<Edge, Double> accumulatedProbabilities() {
        double acc = 0;
        Map<Edge, Double> tmp = new HashMap<>();

        Map<Edge, Double> sorted = edges.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        for(Map.Entry<Edge, Double> edge: sorted.entrySet()) {
            tmp.put(edge.getKey(), edge.getValue() + acc);
            acc += edge.getValue();
        }

        return tmp;
    }

}
