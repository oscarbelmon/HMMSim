package belfern.uji.es.hmm;

import belfern.uji.es.statistics.ProbabilityDensityFunction;

import java.util.*;

public class HMM<T, U> {
    final Map<T, Node<T, U>> nodes;
    private final Map<Node<T,U>, Double> initialNodes;

    public HMM() {
        nodes = new LinkedHashMap<>();
        initialNodes = new LinkedHashMap<>();
    }

    public void addInitialNode(Node<T,U> node, double probability) {
        initialNodes.put(node, probability);
    }

    public Node<T, U> instanceNode(T id, Emitter<U> emitter) {
        Node<T, U> node = new Node<>(id, emitter);
        nodes.put(id, node);
        return node;
    }

    public Edge<T, U> instanceEdge(Node<T, U> start, Node<T, U> end, ProbabilityDensityFunction pdf, double ratio) {
        Edge<T, U> edge = new Edge<>(start, end, pdf);
        start.addEdge(edge, ratio);
        return edge;
    }

    public Edge<T, U> instanceEdge(Node<T, U> start, Node<T, U> end, double ratio) {
        Edge<T, U> edge = new Edge<>(start, end, ProbabilityDensityFunction.CONSTANT_PROBABILITY);
        start.addEdge(edge, ratio);
        return edge;
    }

    Edge<T, U> instanceEdge(String idStart, String idEnd, ProbabilityDensityFunction pdf, double ratio) {
        Node<T, U> start = nodes.get(idStart);
        Node<T, U> end = nodes.get(idEnd);
        return instanceEdge(start, end, pdf, ratio);
    }

    Edge<T, U> instanceEdge(String idStart, String idEnd, double ratio) {
        Node<T, U> start = nodes.get(idStart);
        Node<T, U> end = nodes.get(idEnd);
        return instanceEdge(start, end, ProbabilityDensityFunction.CONSTANT_PROBABILITY, ratio);
    }

    List<U> generateSequence(long items) {
        List<U> sequence = new ArrayList<>();
        Node<T, U> currentNode = getInitialNode();

        for(long i = 0; i < items; i++) {
            sequence.add(currentNode.emmit());
            currentNode = currentNode.nextNode();
        }

        return sequence;
    }

    public List<T> viterbi(List<U> symbols) {
        initializationViterbi(symbols.get(0));
        recursionViterbi(symbols);
        return terminationViterbi();
    }

    private void initializationViterbi(U symbol) {
        for(Node<T,U> node: nodes.values()) {
            if (initialNodes.get(node) != null) {
                node.viterbi = node.viterbiPrevious = node.emitter.getSymbolProbability(symbol) * initialNodes.get(node);
            } else {
                node.viterbi = node.viterbiPrevious = 0;
            }
            node.viterbiInit();
        }
    }

    private void recursionViterbi(List<U> symbols) {
        for(int i = 1; i < symbols.size(); i++) {
            for(Node<T, U> node: nodes.values()) {
                node.viterbi(symbols.get(i));
            }
            for(Node<T, U> node: nodes.values()) {
                node.viterbiStepForward();
            }
        }
    }

    private List<T> terminationViterbi() {
        Node<T,U> bestNode = nodes.values().stream()
                .max(Comparator.comparingDouble(a -> a.viterbi))
                .get();

        List<T> reverse = new ArrayList<>();
        Node<T,U> node = bestNode;
        reverse.add(node.id);
        for(int i = bestNode.viterbiPath.size(); i >0; i--) {
            node = node.viterbiPath.get(i-1).node;
            reverse.add(node.id);
        }

        Collections.reverse(reverse);

        return reverse;
    }

    double forward(List<U> symbols) {
        initializationForward(symbols.get(0));
        recursionForward(symbols);
        return terminationForward();
    }

    void initializationForward(U symbol) {
        nodes.values()//.stream()
                .forEach(node -> {
                    if (initialNodes.get(node) != null) {
                        node.alfa = node.alfaPrevious = node.emitter.getSymbolProbability(symbol) * initialNodes.get(node);
                    } else {
                        node.alfa = node.alfaPrevious = 0;
                    }
                    node.stepForward();
                });
    }

    void recursionForward(List<U> symbols) {
        for(int i = 1; i < symbols.size(); i++) {
            for(Node<T, U> node: nodes.values()) {
                node.getProbabilityForSymbol(symbols.get(i));
            }
            for(Node<T, U> node: nodes.values()) {
                node.stepForward();
            }
        }
    }

    double terminationForward() {
        return nodes.values().stream()
                .mapToDouble(node -> node.alfa)
                .max()
                .getAsDouble();
    }

    private Node<T,U> getInitialNode() {
        double acc = 0;
        Map<Node<T,U>, Double> tmp = new LinkedHashMap<>();

        for(Map.Entry<Node<T,U>, Double> node: initialNodes.entrySet()) {
            acc += node.getValue();
            tmp.put(node.getKey(), acc);
        }

        Map<Node<T,U>, Double> tmpNormalized = new LinkedHashMap<>();

        for (Map.Entry<Node<T,U>, Double> node: tmp.entrySet()) {
            tmpNormalized.put(node.getKey(), tmp.get(node.getKey())/acc);
        }

        double probability = Math.random();
        Map.Entry<Node<T,U>, Double> entry = tmpNormalized.entrySet().stream()
                .filter(e -> e.getValue() > probability)
                .findFirst()
                .get();

        return entry.getKey();

    }
}
